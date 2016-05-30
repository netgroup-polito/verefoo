package it.polito.escape.verify.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.polito.escape.verify.client.Neo4jManagerClient;
import it.polito.escape.verify.model.Entry;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.resources.beans.VerificationBean;
import it.polito.nffg.neo4j.jaxb.Paths;

public class VerificationService {
	private static final String projectFolder = System.getProperty("user.dir");
	private String chainsFile =  projectFolder+ "/service/src/tests/j-verigraph-generator/examples/chains.json";
	private String configFile = projectFolder + "/service/src/tests/j-verigraph-generator/examples/budapest/config_success.json";
	
	public VerificationService(){
		
	}

	public Paths getPaths(Graph graph, VerificationBean verificationBean) {
		Node sourceNode = graph.searchNodeByName(verificationBean.getSource());
		Node destinationNode = graph.searchNodeByName(verificationBean.getDestination());
		if (sourceNode == null || destinationNode == null){
			ErrorMessage errorMessage = new ErrorMessage("Bad request", 400, "http://localhost:8080/verify/api-docs/");
			Response response = Response.status(Status.BAD_REQUEST)
					.entity(errorMessage)
					.build();
			throw new WebApplicationException(response);
		}
		String source = verificationBean.getSource() + "_" + sourceNode.getId();
		String destination = verificationBean.getDestination() + "_" + destinationNode.getId();
		//System.out.println("Source: " + source + ", destination: " + destination);

		List<String> endpoints = new ArrayList<>();
		List<String> firewalls = new ArrayList<>();
		Map<String, List<Entry>> routingTable = new HashMap<>();
		
		for (Node node : graph.getNodes().values()){
			// if firewall
			if (node.getFunctional_type().equals("NF")){
				// add 2 connection points to RT
				routingTable.put(node.getName() + "_" + node.getId() + "_in", new ArrayList<Entry>());
				routingTable.put(node.getName() + "_" + node.getId() + "_out", new ArrayList<Entry>());
				// add node to firewalls
				firewalls.add(node.getName() + "_" + node.getId());
				//scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()){
					//check if neighbour is a firewall
					Node hop = graph.searchNodeByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId() + "_out").add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else
						//connect normally to node
						routingTable.get(node.getName() + "_" + node.getId() + "_out").add(new Entry("output", neighbour.getName() + "_" + hop.getId()));
				}
			}
			// if endpoint
			else {
				// add endpoint to RT
				routingTable.put(node.getName() + "_" + node.getId(), new ArrayList<Entry>());
				// add to endpoints
				endpoints.add(node.getName() + "_" + node.getId());
				// scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()){
					//check if neighbour is a firewall
					Node hop = graph.searchNodeByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId()).add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else
						//connect normally to node
						routingTable.get(node.getName() + "_" + node.getId()).add(new Entry("output", neighbour.getName() + "_" + hop.getId()));
				}
			}
			
		//end node scan	
		}
		//debug print
		System.out.println("Endpoints:");
		for (String endpoint : endpoints){
			System.out.println(endpoint);
		}
		System.out.println("Firewalls:");
		for (String firewall : firewalls){
			System.out.println(firewall);
		}
		System.out.println("Source: " + source);
		System.out.println("Destination: " + destination);
		for (String key: routingTable.keySet()){
			System.out.println("RT for node " + key);
			for (Entry entry : routingTable.get(key)){
				System.out.println(entry.getDirection() + " " + entry.getDestination());
			}
		}
		//end debug print
		
		Neo4jManagerClient client = new Neo4jManagerClient(source, destination, endpoints, firewalls, routingTable);
		Paths paths = client.runClient();

		return paths;
		
	}
	
	public void runTests(Graph graph, Paths paths){
		if (paths == null){
			System.out.println("There was an error getting the paths.");
			return;
		}
		
		List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
		
		for (String path : paths.getPath()){
			System.out.println("Original path: " + path);
			List<String> newPath = sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
		
//		for (List<String> path : sanitizedPaths){
//			System.out.printf("Path found: ");
//			for (String node : path){
//				System.out.printf(node + " ");
//			}
//			System.out.println("");
//		}
		
		generateChainsFile(graph, sanitizedPaths, chainsFile);
		
		generateTestScenarios(chainsFile, configFile);
		
		compileAndRunTests();
	}
	
	private List<String> sanitizePath(String path) {
		List<String> newPath = new ArrayList<String>();
	     Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(path);
	     while(m.find()) {
	       String node = m.group(1);
	       //System.out.println("before: " + node);
	       
	       int spaceIndex = node.lastIndexOf("_");
	       if (spaceIndex != -1)
	       {
	           node = node.substring(0, spaceIndex);
	           newPath.add(node);
	           //System.out.println("after: " + node);
	       }
	     }
	     return newPath;
		
	}
	
	private void generateChainsFile(Graph graph, List<List<String>> paths, String chainsFile) {
		JSONObject root = new JSONObject();
		JSONArray chains = new JSONArray();
		JSONObject chain = new JSONObject();
		
		int chainCounter = 0;
		
		for (List<String> path : paths){
			Iterator pathsIterator = path.iterator();			
			chain.put("id", ++chainCounter);
			chain.put("flowspace", "tcp=80");
			JSONArray nodes = new JSONArray();
			while(pathsIterator.hasNext()){
				String nodeName = (String) pathsIterator.next();
				Node currentNode = graph.searchNodeByName(nodeName);				
				JSONObject node = new JSONObject();
				node.put("name", currentNode.getName());
				node.put("address", "ip_" + currentNode.getName());
				node.put("functional_type", currentNode.getFunctional_type());
				nodes.add(node);
				chain.put("nodes", nodes);
			}
			chains.add(chain);
		}		
		root.put("chains", chains);

		try (FileWriter file = new FileWriter(chainsFile)) {
			file.write(root.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("JSON Object: " + root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	private int generateTestScenarios(String chainsFile, String configFile) {
		String projectRootFolder = System.getProperty("user.dir");
		
		String[] cmd = {
		        "python",
		        platfromIndependentPath(projectRootFolder + "/service/src/tests/j-verigraph-generator/test_class_generator.py"),
		        "-c",
		        platfromIndependentPath(chainsFile),
		        "-f",
		        platfromIndependentPath(projectRootFolder + configFile),
		        "-o",
		        platfromIndependentPath(projectRootFolder + "/service/src/tests/examples/Scenario")
		    };
		for (String c : cmd){
			System.out.printf(c + " ");
		}
		System.out.println("");
		try {
			String s = null;
			Process p = Runtime.getRuntime().exec(cmd);
			
			BufferedReader stdInput = new BufferedReader(new
	                 InputStreamReader(p.getInputStream()));
			BufferedReader stdError = new BufferedReader(new
                    InputStreamReader(p.getErrorStream()));
			

			// read the output from the command
//            System.out.println("Here is the standard output of the command:\n");
//            while ((s = stdInput.readLine()) != null) {
//                System.out.println(s);
//            }

//            System.out.println("Here is the standard error of the command (if any):\n");
//            while ((s = stdError.readLine()) != null) {
//                System.out.println(s);
//            }
		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		
		return 0;
		
	}
	
	private void compileAndRunTests(){
		File sourceFile = new File("service/src/tests/Test.java");

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File("service/src/tests/")));

			// Compile the file
			boolean success = compiler.getTask(null, fileManager, null, null, null,
					fileManager.getJavaFileObjectsFromFiles(Arrays.asList(sourceFile))).call();
			fileManager.close();
			runIt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      
	}
	
	private String platfromIndependentPath(String path){
		path = path.replaceAll("/", Matcher.quoteReplacement(Character.toString(File.separatorChar)));
		return path;
	}
	
	@SuppressWarnings("unchecked")
	public static void runIt() {
		try {
			Class params[] = {};
			Object paramsObj[] = {};
			Class thisClass = Class.forName("tests.Test");
			Object iClass = thisClass.newInstance();
			Method thisMethod = thisClass.getDeclaredMethod("doStuff2", params);
			int result = (int) thisMethod.invoke(iClass, paramsObj);
			System.out.println("\nTest returned " + result);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
