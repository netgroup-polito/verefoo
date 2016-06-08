package it.polito.escape.verify.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.DocumentationTool.Location;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.StandardLocation;
import javax.tools.ToolProvider;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import it.polito.escape.verify.client.Neo4jManagerClient;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.ConfigurationObject;
import it.polito.escape.verify.model.Entry;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.resources.beans.VerificationBean;
import it.polito.nffg.neo4j.jaxb.Paths;
import qj.util.ReflectUtil;
import qj.util.ThreadUtil;
import qj.util.lang.DynamicClassLoader;

public class VerificationService {

//	LINUX
//	private static final String projectFolder = System.getProperty("catalina.base") + "/webapps/verify/WEB-INF/classes/tests/";
//	WINDOWS
	private static final String projectFolder = System.getProperty("catalina.base") + "/wtpwebapps/verify/WEB-INF/classes/tests/";
	private String chainsFile =  projectFolder + "j-verigraph-generator/examples/chains.json";
	private String configFile = projectFolder + "j-verigraph-generator/examples/config.json";
	private String testClassGenerator = projectFolder + "j-verigraph-generator/test_class_generator.py";
	private String scenarioFile = projectFolder + "examples/Scenario";
	private String testGenerator = projectFolder + "j-verigraph-generator/test_generator.py";
	
	/** where shall the compiled class be saved to (should exist already) */
	private static String classOutputFolder = System.getProperty("catalina.base");
	
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
	
	public String runTests(Graph graph, Paths paths, String source, String destination){
		if (paths == null){
			// throw error or return UNSAT because there are no paths
			System.out.println("There was an error getting the paths.");
			return null;
		}
		
		List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
		
		for (String path : paths.getPath()){
			System.out.println("Original path: " + path);
			List<String> newPath = sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
		
		generateChainsFile(graph, sanitizedPaths, chainsFile);
		
		generateConfigFile(graph, configFile);
		
		generateTestScenarios(chainsFile, configFile);
		
		generateTests(sanitizedPaths, source, destination);
		
		ThreadUtil.sleep(5000);
		
		int result = compileAndRunTests(sanitizedPaths);
		
		if (result == 0){
			return "SAT";
		}
		else if (result == -1){
			return "UNSAT";
		}
		else if (result == -2)
			return "UNPREDICTED";
		else
			return "ERROR";
	}
	
	private void generateConfigFile(Graph graph, String configFile) {
		JSONObject root = new JSONObject();
		JSONArray nodes = new JSONArray();
		
		for (Node n : graph.getNodes().values()){
			JSONObject node = new JSONObject();
			JSONArray configuration = new JSONArray();
			Configuration nodeConfig = n.getConfiguration(); 
			List<String> configurationList = nodeConfig.getConfigurationList();
			List<ConfigurationObject> configurationMap = nodeConfig.getConfigurationMap();
			if (configurationList.size() > 0){
				for (String s : configurationList){
					configuration.add("ip_" + s);
				}
			}			
			else if (configurationMap.size() > 0){
				for (ConfigurationObject c : configurationMap){
					Iterator<java.util.Map.Entry<String, String>> iter = c.getMap().entrySet().iterator();
					while(iter.hasNext()){
						java.util.Map.Entry<String, String> entry = iter.next();
						JSONObject configItem = new JSONObject();
						configItem.put("ip_" + entry.getKey(), "ip_" + entry.getValue());
						configuration.add(configItem);
					}
				}
				
			}
			node.put("configuration", configuration);
			node.put("id", nodeConfig.getId());
			node.put("description", nodeConfig.getDescription());
			
			nodes.add(node);
		}		
		root.put("nodes", nodes);

		try (FileWriter file = new FileWriter(configFile)) {
			file.write(root.toJSONString());
			System.out.println("Successfully Copied JSON Object to File...");
			System.out.println("JSON Object: " + root);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
		
		String[] cmd = {
		        "python",
		        platfromIndependentPath(testClassGenerator),
		        "-c",
		        platfromIndependentPath(chainsFile),
		        "-f",
		        platfromIndependentPath(configFile),
		        "-o",
		        platfromIndependentPath(scenarioFile)
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
	
	private int generateTests(List<List<String>> paths, String source, String destination) {
		
		List<String> scenarios = new ArrayList<String>();
		for (int i=0; i< paths.size();i++){
			scenarios.add("Scenario_" + (i+1));
		}
		
		for (String scenario : scenarios) {
			String[] cmd = { 
					"python",
					platfromIndependentPath(testGenerator),
					"-i",
					platfromIndependentPath(projectFolder + "examples/" + scenario + ".java"),
					"-o",
					platfromIndependentPath(projectFolder + scenario + "_test.java"),
					"-s",
					source,
					"-d",
					destination};
			for (String c : cmd) {
				System.out.printf(c + " ");
			}
			System.out.println("");
			try {
				String s = null;
				Process p = Runtime.getRuntime().exec(cmd);

				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));

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
		}
		return 0;
		
	}
	
	private int compileAndRunTests(List<List<String>> sanitizedPaths){
		List<File> testFiles = new ArrayList<File>();
		List<File> runFiles = new ArrayList<File>();
		for (int i=0; i<sanitizedPaths.size();i++){
			System.out.println("Creating a test file for path: " + sanitizedPaths.get(i).toString());
			//add scenario to compilation
			String scenario = this.scenarioFile + "_" + (i+1) + ".java";
			testFiles.add(new File(scenario));
			System.out.println("Scenario file " + scenario + " added to compilation");
			//add test to compilation
			String test = projectFolder + "Scenario_" + (i+1) + "_test.java";
			testFiles.add(new File(test));
			System.out.println("Test file " + test + " added to copilation");
			//add test to execution
			runFiles.add(new File(test));
			System.out.println("Test file " + test + " added to execution");
		}

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(projectFolder)));
			
			System.out.println("Java class path is: " + System.getProperty("java.class.path"));
			
            String z3 = "/usr/lib/com.microsoft.z3.jar";
			List<String> optionList = new ArrayList<String>();
            optionList.add("-classpath");            
            optionList.add(System.getProperty("java.class.path") + ":" + z3);
            
			boolean success = compiler.getTask(null, fileManager, null, optionList, null,
					fileManager.getJavaFileObjectsFromFiles(testFiles)).call();
			if(success){
				System.out.println("Compilation succeded!");
			}			
			fileManager.close();
			
			for (File file : runFiles){
				System.out.println("Running test file " + file.getAbsolutePath().split("\\.")[0]);
				int result = runIt(file);
				System.out.println("Execution returned: " + result);
				if (result < 0)
					return result;
			}
			//SAT
			return 0;
		} catch (IOException e) {
			// IO exception, return -3
			e.printStackTrace();
			return -3;
		}
	      
	}
	
	private String platfromIndependentPath(String path){
		path = path.replaceAll("/", Matcher.quoteReplacement(Character.toString(File.separatorChar)));
		return path;
	}
	
    public static class MyDiagnosticListener implements DiagnosticListener<JavaFileObject>
    {
        public void report(Diagnostic<? extends JavaFileObject> diagnostic)
        {
 
            System.out.println("Line Number->" + diagnostic.getLineNumber());
            System.out.println("code->" + diagnostic.getCode());
            System.out.println("Message->"
                               + diagnostic.getMessage(Locale.ENGLISH));
            System.out.println("Source->" + diagnostic.getSource());
            System.out.println(" ");
        }
    }
	
    /** compile your files by JavaCompiler */
    public static void compile(Iterable<? extends JavaFileObject> files)
    {
        //get system compiler:
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
 
        // for compilation diagnostic message processing on compilation WARNING/ERROR
        MyDiagnosticListener c = new MyDiagnosticListener();
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(c,
                                                                              Locale.ENGLISH,
                                                                              null);
        //specify classes output folder
        Iterable options = Arrays.asList("-d", classOutputFolder);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager,
                                                             c, options, null,
                                                             files);
        Boolean result = task.call();
        if (result == true)
        {
            System.out.println("Compilation succeeded");
        }
    }
    
    /** run class from the compiled byte code file by URLClassloader */
    @SuppressWarnings("deprecation")
	public static int runFile(File file)
    {
        // Create a File object on the root of the directory
        // containing the class file
        File directory = new File(classOutputFolder);
 
        try
        {
            // Convert File to a URL
            URL url = directory.toURL(); // file:/classes/demo
            URL[] urls = new URL[] { url };
 
            // Create a new class loader with the directory
            ClassLoader loader = new URLClassLoader(urls);
 
            // Load in the class; Class.childclass should be located in
            // the directory file:/class/demo/
            Class thisClass = loader.loadClass(directory.getName());
 
            Class params[] = {};
            Object paramsObj[] = {};
            Object instance = thisClass.newInstance();
            Method thisMethod = thisClass.getDeclaredMethod("run", params);
 
            // run the testAdd() method on the instance:
            return (int) thisMethod.invoke(instance, paramsObj);
        }
        catch (MalformedURLException e)
        {
        }
        catch (ClassNotFoundException e)
        {
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return -10;
    }
	
	//@SuppressWarnings("unchecked")
	public int runIt(File filename) {
		try {
			String filenameNoExtension = filename.getName().split("\\.")[0];
			
			Class<?> userClass = new DynamicClassLoader(projectFolder)
				      .load("tests." + filenameNoExtension);
			Object context = ReflectUtil.newInstance(userClass);
			int result = (int)ReflectUtil.invoke("run", context);
			return result;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return -4;
	}

}
