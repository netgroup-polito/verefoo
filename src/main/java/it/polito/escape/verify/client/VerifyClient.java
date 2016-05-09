package it.polito.escape.verify.client;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.resources.PathsMessageBodyReader;
import it.polito.nffg.neo4j.jaxb.Paths;

public class VerifyClient {

	private List<Node> nodes = new ArrayList<Node>();
	
	private String projectFolder;
	private String baseTarget;
	private String graphFilePath;
	private String source;
	private String destination;
	private String chainsFile;
	private String configFile;
	
	public VerifyClient() {
		this.projectFolder = System.getProperty("user.dir");
		this.baseTarget = "http://localhost:8080/verify/api/";
		this.graphFilePath = this.projectFolder + "/service/src/tests/j-verigraph-generator/examples/budapest/graph.json";
		this.source = "user1";
		this.destination = "webserver";
		this.chainsFile = this.projectFolder + "/service/src/tests/j-verigraph-generator/examples/chains.json";
		this.configFile = this.projectFolder + "/service/src/tests/j-verigraph-generator/examples/budapest/config_success.json";
	}
	
	public VerifyClient(String baseTarget, String graphFilePath, String configFile, String source, String destination) {
		this.projectFolder = System.getProperty("user.dir");
		this.baseTarget = baseTarget;
		this.graphFilePath = graphFilePath;
		this.configFile = configFile;
		this.source = source;
		this.destination = destination;
	}

	public Node getNode(String name) {
		for (Node node : this.nodes) {
			if (node.getName().equals(name)) {
				return node;
			}
		}
		return null;
	}

	public void printNodes() {
		for (Node node : this.nodes) {
			System.out.println("Node id: " + node.getId());
			System.out.println("Node name: " + node.getName());
			System.out.println("Node functional type: " + node.getFunctional_type());
			for (Neighbour neighbour : node.getNeighbours().values()) {
				System.out.println("\tNeighbour id: " + neighbour.getId());
				System.out.println("\tNeighbour name: " + neighbour.getName());
			}
			System.out.println("");
		}
	}

	public int uploadNodes() {
		Client client = ClientBuilder.newClient();

		WebTarget baseTarget = client.target(this.baseTarget);
		WebTarget nodesTarget = baseTarget.path("nodes");
		WebTarget neighboursTarget = nodesTarget.path("{nodeId}/neighbours");

		for (Node node : this.nodes){
			Response addNodeResponse = nodesTarget
					.request()
					.post(Entity.json(node));
			if (addNodeResponse.getStatus() != Status.CREATED.getStatusCode() &&
					addNodeResponse.getStatus() != Status.OK.getStatusCode()){
				return addNodeResponse.getStatus();
			}
			for (Neighbour neighbour : node.getNeighbours().values()){				
				Response addNeighbourResponse = neighboursTarget
						.resolveTemplate("nodeId", node.getId())
						.request()
						.post(Entity.json(neighbour));
				if (addNeighbourResponse.getStatus() != Status.CREATED.getStatusCode() &&
						addNeighbourResponse.getStatus() != Status.OK.getStatusCode()){
					return addNeighbourResponse.getStatus();
				}
			}
		}
		
		return Status.CREATED.getStatusCode();

	}
	
	public void parseGraphJsonFile()
			throws FileNotFoundException, IOException, ParseException {
		
		FileReader reader = new FileReader(graphFilePath);
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(reader);

		JSONArray nodes = (JSONArray) jsonObject.get("nodes");
		Iterator nodesIterator = nodes.iterator();
		int nodesCouter = 0;
		while (nodesIterator.hasNext()) {
			nodesCouter++;
			JSONObject node = (JSONObject) nodesIterator.next();
			this.nodes
					.add(new Node(nodesCouter, (String) node.get("name"), (String) node.get("functional_type")));
			System.out.println(
					"Node name: " + node.get("name") + ", functional type: " + node.get("functional_type"));
		}

		JSONArray links = (JSONArray) jsonObject.get("links");
		Iterator linksIterator = links.iterator();
		while (linksIterator.hasNext()) {
			JSONObject link = (JSONObject) linksIterator.next();
			Node source = getNode((String) link.get("source"));
			int sourceNeighbours = source.getNeighbours().size();
			source.getNeighbours().put(Integer.toUnsignedLong(sourceNeighbours + 1),
					new Neighbour(sourceNeighbours + 1, (String) link.get("destination")));
			System.out.println("Link between " + link.get("source") + " and destination " + link.get("destination"));
		}
		System.out.println("");
		
		//printNodes();
	}
	
	public static void main(String[] args) {
		VerifyClient verify = new VerifyClient();
		
		System.out.println("Reading graph...");
		
		try {
			verify.parseGraphJsonFile();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ParseException ex) {
			ex.printStackTrace();
		} catch (NullPointerException ex) {
			ex.printStackTrace();
		}
		
		System.out.println("Uploading nodes...");
		
		int uploadNodesResult = verify.uploadNodes();
		
		if (uploadNodesResult == Status.BAD_REQUEST.getStatusCode()){
			System.out.println("BAD REQUEST: an invalid node has been supplied!");
		}
		else if (uploadNodesResult != Status.CREATED.getStatusCode()){
			System.out.println("Unknown error: " + uploadNodesResult);
		}
		
		System.out.println("Nodes successfully added!");
		
		System.out.println("Getting all paths between \"" + verify.source + "\" and \"" + verify.destination + "\"");
		
		Paths paths = verify.getChains();
		
		if (paths == null){
			System.out.println("There was an error getting the paths.");
			return;
		}
		
		List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
		
		for (String path : paths.getPath()){
			System.out.println("Original path: " + path);
			List<String> newPath = verify.sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
		
//		for (List<String> path : sanitizedPaths){
//			System.out.printf("Path found: ");
//			for (String node : path){
//				System.out.printf(node + " ");
//			}
//			System.out.println("");
//		}
		
		verify.generateChainsFile(sanitizedPaths, verify.chainsFile);
		
		verify.generateTestScenarios(verify.chainsFile, verify.configFile);
		
		verify.compileAndRunTests();
	
		
	}

	private void generateChainsFile(List<List<String>> paths, String chainsFile) {
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
				Node currentNode = getNode(nodeName);				
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
	
	private String platfromIndependentPath(String path){
		path = path.replaceAll("/", Matcher.quoteReplacement(Character.toString(File.separatorChar)));
		return path;
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

	private Paths getChains() {
		Client client = ClientBuilder.newBuilder()
				.register(PathsMessageBodyReader.class).build();

		WebTarget baseTarget = client.target(this.baseTarget);
		WebTarget chainsTarget = baseTarget.path("chains");

		Response getChainsResponse = chainsTarget
					.queryParam("source", this.source)
					.queryParam("destination", this.destination)
					.request()
					.get();
				
		Paths paths;
		try {
			paths = getChainsResponse.readEntity(Paths.class);
			return paths;
		} catch (Exception e) {
			//e.printStackTrace();
			return null;
		}
		
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
