package it.polito.escape.verify.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
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

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.escape.verify.client.Neo4jManagerClient;
import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Configuration2;
import it.polito.escape.verify.model.Entry;
import it.polito.escape.verify.model.ErrorMessage;
import it.polito.escape.verify.model.Graph;
import it.polito.escape.verify.model.Neighbour;
import it.polito.escape.verify.model.Node;
import it.polito.escape.verify.model.Test;
import it.polito.escape.verify.model.Verification;
import it.polito.escape.verify.resources.beans.VerificationBean;
import it.polito.nffg.neo4j.jaxb.Paths;
import qj.util.ReflectUtil;
import qj.util.lang.DynamicClassLoader;

public class VerificationService {

	// LINUX
	private static final String	projectFolder		= System.getProperty("catalina.base")
														+ "/webapps/verify/WEB-INF/classes/tests/";
	// WINDOWS
	// private static final String projectFolder =
	// System.getProperty("catalina.base") +
	// "/wtpwebapps/verify/WEB-INF/classes/tests/";
	private String				chainsFile			= projectFolder + "j-verigraph-generator/examples/chains.json";
	private String				configFile			= projectFolder + "j-verigraph-generator/examples/config.json";
	private String				testClassGenerator	= projectFolder + "j-verigraph-generator/test_class_generator.py";
	private String				scenarioFolder		= projectFolder + "examples";
	private String				scenarioName		= "Scenario";
	private String				scenarioFile		= this.scenarioFolder + "/" + scenarioName;
	private String				testFolder			= projectFolder;
	private String				testGenerator		= projectFolder + "j-verigraph-generator/test_generator.py";
//	List<List<String>>			sanitizedPaths		= new ArrayList<List<String>>();

	public VerificationService() {

	}

	public Paths getPaths(Graph graph, Node sourceNode, Node destinationNode) {

		String source = sourceNode.getName() + "_" + sourceNode.getId();
		String destination = destinationNode.getName() + "_" + destinationNode.getId();

		List<String> endpoints = new ArrayList<>();
		List<String> firewalls = new ArrayList<>();
		Map<String, List<Entry>> routingTable = new HashMap<>();

		for (Node node : graph.getNodes().values()) {
			// if firewall
			if (node.getFunctional_type().equals("NF")) {
				// add 2 connection points to RT
				routingTable.put(node.getName() + "_" + node.getId() + "_in", new ArrayList<Entry>());
				routingTable.put(node.getName() + "_" + node.getId() + "_out", new ArrayList<Entry>());
				// add node to firewalls
				firewalls.add(node.getName() + "_" + node.getId());
				// scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()) {
					// check if neighbour is a firewall
					Node hop = graph.searchNodeByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId() + "_out")
									.add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else
																// connect
																// normally to
																// node
																routingTable.get(node.getName()+ "_" + node.getId()
																					+ "_out")
																			.add(new Entry(	"output",
																							neighbour.getName()+ "_"
																										+ hop.getId()));
				}
			}
			// if endpoint
			else {
				// add endpoint to RT
				routingTable.put(node.getName() + "_" + node.getId(), new ArrayList<Entry>());
				// add to endpoints
				endpoints.add(node.getName() + "_" + node.getId());
				// scan neighbours
				for (Neighbour neighbour : node.getNeighbours().values()) {
					// check if neighbour is a firewall
					Node hop = graph.searchNodeByName(neighbour.getName());
					// if neighbour is a firewall connect to its input port
					if (hop.getFunctional_type().equals("NF"))
						routingTable.get(node.getName() + "_" + node.getId())
									.add(new Entry("output", neighbour.getName() + "_" + hop.getId() + "_in"));
					else {
						// connect
						// normally to
						// node
						routingTable.get(node.getName() + "_" + node.getId())
									.add(new Entry("output", neighbour.getName() + "_" + hop.getId()));
					}
				}
			}

			// end node scan
		}
		// debug print
		System.out.println("Endpoints:");
		for (String endpoint : endpoints) {
			System.out.println(endpoint);
		}
		System.out.println("Firewalls:");
		for (String firewall : firewalls) {
			System.out.println(firewall);
		}
		System.out.println("Source: " + source);
		System.out.println("Destination: " + destination);
		for (String key : routingTable.keySet()) {
			System.out.println("RT for node " + key);
			for (Entry entry : routingTable.get(key)) {
				System.out.println("\t" + entry.getDirection() + "->" + entry.getDestination());
			}
		}
		// end debug print

		// Neo4jManagerClient client = new
		// Neo4jManagerClient("http://localhost:8080/Project-Neo4jManager/rest/",
		// source, destination, endpoints, firewalls, routingTable);
		Neo4jManagerClient client = new Neo4jManagerClient(	"http://localhost:8080/neo4jmanager/rest/",
															source,
															destination,
															endpoints,
															firewalls,
															routingTable);

		Paths paths = client.runClient();

		return paths;

	}

	public Verification runReachabilityTests(Graph graph, Paths paths, String source, String destination) {
		if (paths.getPath().size() == 0) {
			System.out.println("No paths between '" + source + "' and '" + destination + "'");
			return new Verification("UNSAT");
		}

		List<List<String>> sanitizedPaths = sanitizePaths(paths);
		//debug print
		printListsOfStrings("Before pruning", sanitizedPaths);
		
		prunePaths(sanitizedPaths);
		//debug print
		printListsOfStrings("After pruning", sanitizedPaths);

		deletePreviousReachabilityTestFiles(this.scenarioFolder, this.testFolder);

		generateChainsFile(graph, sanitizedPaths, chainsFile);

		generateConfigFile(graph, configFile);

		generateTestScenarios(chainsFile, configFile);

		generateTests(sanitizedPaths, source, destination);
		
		List<File> sourceFiles = new ArrayList<File>();
		List<File> classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(sanitizedPaths.size(), sourceFiles, classFiles);

		compileFiles(sourceFiles);

		List<Test> tests = runFiles(sanitizedPaths, graph, classFiles);

		return evaluateResult(tests);
	}

	private void printListsOfStrings(String message, List<List<String>> lists) {
		System.out.println(message);
		for (List<String> element : lists){
			System.out.println(element);
		}
	}

	private void prunePaths(List<List<String>> sanitizedPaths) {
		List<List<String>> pathsToBeRemoved = new ArrayList<List<String>>();
		
		for(List<String> path : sanitizedPaths){
			Map<String, Long> occurrencesMap = toMap(path);
			for (long occurrences : occurrencesMap.values()){
				if (occurrences > 1){
					pathsToBeRemoved.add(path);
					break;
				}
			}
		}
		for (List<String> path : pathsToBeRemoved){
			sanitizedPaths.remove(path);
		}
	}
	
	static public Map<String,Long> toMap(List<String> lst){
	    return lst.stream().collect(Collectors.groupingBy(s -> s, 
	                                  Collectors.counting()));
	}
	
	private Verification evaluateResult(List<Test> tests) {
		Verification v = new Verification();
		boolean sat = false;
		int unsat = 0;
		for (Test t : tests) {
			v.getTests().add(t);

			if (t.getResult().equals("SAT")) {
				sat = true;
			}
			if (t.getResult().equals("UNKNOWN")) {
				v.setResult("UNKNWON");
			}
			if (t.getResult().equals("UNSAT")) {
				unsat++;
			}
		}
		if (sat)
			v.setResult("SAT");
		else if (unsat == tests.size())
			v.setResult("UNSAT");
		return v;
	}

	private List<List<String>> sanitizePaths(Paths paths) {
		List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
		for (String path : paths.getPath()) {
			System.out.println("Original path: " + path);
			List<String> newPath = sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
		return sanitizedPaths;
	}

	private void deletePreviousReachabilityTestFiles(String scenarioDir, String TestDir) {
		deleteFiles(scenarioDir, "Scenario_", "java");
		deleteFiles(TestDir, "Scenario_", "java");
	}

	private void deleteFiles(String directory, String prefix, String extension) {
		final File scenarioFolder = new File(directory);
		final File[] scenarioFiles = scenarioFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches(prefix + ".*\\." + extension);
			}
		});
		for (final File file : scenarioFiles) {
			if (!file.delete()) {
				System.err.println("Can't remove " + file.getAbsolutePath());
			}
			else {
				System.out.println("Removed file " + file.getAbsolutePath());
			}
		}
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}

		return dir.delete();

	}

	private List<String> sanitizePath(String path) {
		List<String> newPath = new ArrayList<String>();
		// find all nodes, i.e. all names between parentheses
		Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(path);
		while (m.find()) {
			String node = m.group(1);

			int spaceIndex = node.lastIndexOf("_");
			if (spaceIndex != -1) {
				node = node.substring(0, spaceIndex);
				newPath.add(node);
			}
		}
		return newPath;

	}

	private void generateChainsFile(Graph graph, List<List<String>> sanitizedPaths, String chainsFile) {
		JSONObject root = new JSONObject();
		JSONArray chains = new JSONArray();
		

		int chainCounter = 0;

		for (List<String> path : sanitizedPaths) {
			Iterator pathsIterator = path.iterator();
			JSONObject chain = new JSONObject();
			chain.put("id", ++chainCounter);
			chain.put("flowspace", "tcp=80");
			JSONArray nodes = new JSONArray();
			while (pathsIterator.hasNext()) {
				String nodeName = (String) pathsIterator.next();
				Node currentNode = graph.searchNodeByName(nodeName);
				if (currentNode == null) {
					throw new InternalServerErrorException("Unable to generate 'chains.json' for neo4jmanager: node "
															+ nodeName + " not found");
				}
				JSONObject node = new JSONObject();
				node.put("name", currentNode.getName());
				// if(currentNode.getFunctional_type().equals("firewall"))
				// node.put("address", "ip_nat");
				// else
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
			System.out.println("Successfully created 'chains.json' with the following content:");
			System.out.println(root);
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Error saving 'chains.json' for neo4jmanager");
		}

	}
	
	private void generateConfigFile(Graph graph, String configFile) {
		JSONObject root = new JSONObject();
		JSONArray nodes = new JSONArray();

		for (Node n : graph.getNodes().values()) {
			JSONObject node = new JSONObject();
//			JSONArray configuration = new JSONArray();
			Configuration2 nodeConfig = n.getConfiguration();
			JsonNode configuration = nodeConfig.getConfiguration();
			
			node.put("configuration", configuration);
			node.put("id", nodeConfig.getId());
			node.put("description", nodeConfig.getDescription());
			
			nodes.add(node);

		}
		root.put("nodes", nodes);

		try (FileWriter file = new FileWriter(configFile)) {
			file.write(root.toJSONString());
			System.out.println("Successfully created 'config.json' with the following content:");
			System.out.println(root);
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Error saving 'config.json' for neo4jmanager");
		}

	}

	private void generateTestScenarios(String chainsFile, String configFile) {

		String[] cmd = {	"python", platfromIndependentPath(testClassGenerator), "-c",
							platfromIndependentPath(chainsFile), "-f", platfromIndependentPath(configFile), "-o",
							platfromIndependentPath(scenarioFile) };
		printCommand(cmd);
		
		ProcessBuilder pb = new ProcessBuilder(cmd);
		pb.redirectErrorStream(true);
		Process process;
		try {
			process = pb.start();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = reader.readLine()) != null)
			    System.out.println("test_class_generator.py: " + line);
			process.waitFor();
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Error generating tests for Z3: unable to execute generator");
		}
		catch (InterruptedException e) {
			throw new InternalServerErrorException("Error generating tests for Z3: generator got interrupted during execution");
		}

	}

	private void printCommand(String[] cmd) {
		for (String c : cmd) {
			System.out.printf(c + " ");
		}
		System.out.println("");
	}

	private void generateTests(List<List<String>> sanitizedPaths, String source, String destination) {

		List<String> scenarios = new ArrayList<String>();
		for (int i = 0; i < sanitizedPaths.size(); i++) {
			scenarios.add("Scenario_" + (i + 1));
		}

		for (String scenario : scenarios) {
			String[] cmd = {	"python", platfromIndependentPath(testGenerator), "-i",
								platfromIndependentPath(projectFolder + "examples/" + scenario + ".java"), "-o",
								platfromIndependentPath(projectFolder + scenario + "_test.java"), "-s", source, "-d",
								destination };
			printCommand(cmd);
			
			ProcessBuilder pb = new ProcessBuilder(cmd);
			pb.redirectErrorStream(true);
			Process process;
			try {
				process = pb.start();
				
				BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null)
				    System.out.println("test_generator.py: " + line);
				process.waitFor();
			}
			catch (IOException e) {
				throw new InternalServerErrorException("Error generating tests for Z3: unable to execute generator");
			}
			catch (InterruptedException e) {
				throw new InternalServerErrorException("Error generating tests for Z3: generator got interrupted during execution");
			}
			
		}

	}

	private void prepareForCompilationAndExecution(int size, List<File> sourceFiles, List<File> classFiles) {
		for (int i = 0; i < size; i++) {
			System.out.println("Creating reachability test file #" + (i+1));
			String scenario = this.scenarioFile + "_" + (i + 1) + ".java";
			sourceFiles.add(new File(scenario));
			System.out.println("Scenario file " + scenario + " added to compilation");
			String testSourceFile = projectFolder + "Scenario_" + (i + 1) + "_test.java";
			String testClassFile = projectFolder + "Scenario_" + (i + 1) + "_test";
			sourceFiles.add(new File(testSourceFile));
			System.out.println("Test file " + testSourceFile + " added to copilation");
			classFiles.add(new File(testClassFile));
			System.out.println("Test file " + testClassFile + " added to execution");
		}
	}

	private void compileFiles(List<File> files) {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new InternalServerErrorException("Error getting the Java compiler: JDK >= 1.8 required");
		}
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(projectFolder)));

//			System.out.println("Java class path is: " + System.getProperty("java.class.path"));

//			String z3 = "/usr/lib/com.microsoft.z3.jar";
//			List<String> optionList = new ArrayList<String>();
//			optionList.add("-classpath");
//			optionList.add(System.getProperty("java.class.path") + ":" + z3);
			List<String> optionList = null;
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

			boolean success = compiler.getTask(	null,
												fileManager,
												diagnostics,
												optionList,
												null,
												fileManager.getJavaFileObjectsFromFiles(files))
										.call();
			if (!success) {
				Locale myLocale = Locale.getDefault();
				StringBuilder msg = new StringBuilder();
				msg.append("Error compiling Z3 test files: ");
				for (Diagnostic<? extends JavaFileObject> err : diagnostics.getDiagnostics()) {
					msg.append('\n');
					msg.append(err.getKind());
					msg.append(": ");
					if (err.getSource() != null) {
						msg.append(err.getSource().getName());
					}
					msg.append(':');
					msg.append(err.getLineNumber());
					msg.append(": ");
					msg.append(err.getMessage(myLocale));
				}
				throw new InternalServerErrorException(msg.toString());
			}
			fileManager.close();

		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to set the location of the Z3 test files to be compiled");
		}

	}

	public List<Test> runFiles(List<List<String>> sanitizedPaths, Graph graph, List<File> files) {
		List<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < files.size(); i++) {
			System.out.println("Running test file " + files.get(i).getAbsolutePath());
			int result = runIt(files.get(i));
			System.out.println("Execution returned: " + result);

			List<Node> path = new ArrayList<Node>();
			for (String nodeString : sanitizedPaths.get(i)) {
				Node node = graph.searchNodeByName(nodeString);
				path.add(node);
			}
			Test t = new Test(path, result);
			tests.add(t);
		}
		return tests;
	}

	private String platfromIndependentPath(String path) {
		path = path.replaceAll("/", Matcher.quoteReplacement(Character.toString(File.separatorChar)));
		return path;
	}

	// @SuppressWarnings("unchecked")
	public int runIt(File filename) {
		int endIndex = filename.getName().lastIndexOf(".");
		String filenameNoExtension;
		if (endIndex == -1) {
			filenameNoExtension = filename.getName();
		}
		else {
			filenameNoExtension = filename.getName().substring(0, endIndex);
			if (!filenameNoExtension.matches("\\w+")) {
				filenameNoExtension = filename.getName();
			}
		}

		System.out.println("Filename is: " + filenameNoExtension);

		try {
			Class<?> userClass = new DynamicClassLoader(projectFolder).load("tests." + filenameNoExtension);
			Object context = ReflectUtil.newInstance(userClass);
			Object result = ReflectUtil.invoke("run", context);
			return (int) result;
		}
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			throw new InternalServerErrorException("Error executing Z3 tests: "+ e.getMessage()
													+ ". There are errors in the Z3 model.");
		}
	}

	public Verification verify(long graphId, VerificationBean verificationBean) {
		if (graphId <= 0) {
			throw new ForbiddenException("Illegal graph id: " + graphId);
		}		
		GraphService graphService = new GraphService();
		Graph graph = graphService.getGraph(graphId);
		if (graph == null) {
			throw new DataNotFoundException("Graph with id " + graphId + " not found");
		}		
		String source = verificationBean.getSource();
		String destination = verificationBean.getDestination();
		String type = verificationBean.getType();
		if (source == null || source.equals("")){
			throw new BadRequestException("Please specify the 'source' parameter in your request");
		}
		if (destination == null || destination.equals("")){
			throw new BadRequestException("Please specify the 'destination' parameter in your request");
		}
		if (type == null || type.equals("")){
			throw new BadRequestException("Please specify the 'type' parameter in your request");
		}
		
		Node sourceNode = graph.searchNodeByName(verificationBean.getSource());
		Node destinationNode = graph.searchNodeByName(verificationBean.getDestination());

		if (sourceNode == null) {
			throw new BadRequestException("The 'source' parameter is not valid, please insert the name of an existing node");
		}
		if (destinationNode == null) {
			throw new BadRequestException("The 'destination' parameter is not valid, please insert the name of an existing node");
		}
		if ((!type.equals("reachability")) && (!type.equals("isolation")) && (!type.equals("traversal"))){
			throw new BadRequestException("The 'verification' parameter '" + type + "' is not valid: valid types are: 'reachability', 'isolation' and 'traversal'");
		}
		
		Verification v = null;
		
		switch(type){
			case "reachability":
				v = reachabilityVerification(graph, sourceNode, destinationNode);
				break;
			case "isolation":
				String middlebox = verificationBean.getMiddlebox();
				if (middlebox == null || middlebox.equals("")){
					throw new BadRequestException("Please specify the 'middlebox' parameter in your request");
				}
				
				Node middleboxNode = graph.searchNodeByName(middlebox);
				if (middleboxNode == null) {
					throw new BadRequestException("The 'middlebox' parameter is not valid, please insert the name of an existing node");
				}
				if(middleboxNode.getFunctional_type().equals("endpoint")){
					throw new BadRequestException("'" + middlebox + "' is of type 'endpoint', please choose a valid endpoint");
				}
				v = isolationVerification(graph, sourceNode, destinationNode, middleboxNode);
				break;
			default:
				break;
		}
		
		return v;
	}

	private Verification isolationVerification(Graph graph, Node sourceNode, Node destinationNode, Node middleboxNode) {
		
		Paths paths = getPaths(graph, sourceNode, destinationNode);
		if (paths.getPath().size() == 0) {
			System.out.println("No paths between '" + sourceNode.getName() + "' and '" + destinationNode.getName() + "'");
			return new Verification("UNSAT");
		}

		List<List<String>> sanitizedPaths = sanitizePaths(paths);
		
		printListsOfStrings("Before pruning", sanitizedPaths);
		
		prunePaths(sanitizedPaths);
		
		printListsOfStrings("After pruning", sanitizedPaths);
		
		extractPathsWithMiddlebox(sanitizedPaths, middleboxNode.getName());
		
		printListsOfStrings("After middlebox research", sanitizedPaths);
		
		generateChainsFile(graph, sanitizedPaths, chainsFile);

		generateConfigFile(graph, configFile);

		generateTestScenarios(chainsFile, configFile);

		generateTests(sanitizedPaths, sourceNode.getName(), middleboxNode.getName());
		
		List<File> sourceFiles = new ArrayList<File>();
		List<File> classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(sanitizedPaths.size(), sourceFiles, classFiles);

		compileFiles(sourceFiles);

		List<Test> tests = runFiles(sanitizedPaths, graph, classFiles);
		
		return evaluateIsolationResults(tests);
		
	}

	private Verification evaluateIsolationResults(List<Test> tests) {
		Verification v = new Verification();
		boolean isUnsat = false;
		int unsatCounter = 0;
		for (Test t : tests) {
			v.getTests().add(t);

			if (t.getResult().equals("SAT")) {
				isUnsat = true;
			}
			if (t.getResult().equals("UNKNOWN")) {
				v.setResult("UNKNWON");
			}
			if (t.getResult().equals("UNSAT")) {
				unsatCounter++;
			}
		}
		if (isUnsat)
			v.setResult("UNSAT");
		else if (unsatCounter == tests.size())
			v.setResult("SAT");
		return v;
		
	}

	private void extractPathsWithMiddlebox(List<List<String>> sanitizedPaths, String middleboxName) {
		List<List<String>> pathsToBeRemoved = new ArrayList<List<String>>();
		for (List<String> path : sanitizedPaths){
			boolean middleboxFound = false;
			for (String node : path){
				if (node.equals(middleboxName)){
					middleboxFound = true;
					break;
				}
			}
			if (!middleboxFound){
				pathsToBeRemoved.add(path);
			}
		}
		
		for (List<String> path : pathsToBeRemoved){
			sanitizedPaths.remove(path);
		}
		
	}

	private Verification reachabilityVerification(Graph graph, Node sourceNode, Node destinationNode) {
		Paths paths = getPaths(graph, sourceNode, destinationNode);
		Verification v = runReachabilityTests(graph, paths, sourceNode.getName(), destinationNode.getName());
		return v;
	}

}
