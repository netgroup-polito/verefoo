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
	private String				scenarioFile		= projectFolder + "examples/Scenario";
	private String				testFolder			= projectFolder;
	private String				testGenerator		= projectFolder + "j-verigraph-generator/test_generator.py";
	List<List<String>>			sanitizedPaths		= new ArrayList<List<String>>();
	private List<File>			testFiles			= new ArrayList<File>();
	private List<File>			runFiles			= new ArrayList<File>();
	private List<Test>			tests				= new ArrayList<Test>();

	public VerificationService() {

	}

	public Paths getPaths(Graph graph, VerificationBean verificationBean) {
		Node sourceNode = graph.searchNodeByName(verificationBean.getSource());
		Node destinationNode = graph.searchNodeByName(verificationBean.getDestination());
		String verificationType = verificationBean.getType();
		if (sourceNode == null || destinationNode == null || verificationType == null) {
			ErrorMessage errorMessage = new ErrorMessage("Bad request", 400, "http://localhost:8080/verify/api-docs/");
			Response response = Response.status(Status.BAD_REQUEST).entity(errorMessage).build();
			throw new WebApplicationException(response);
		}
		String source = verificationBean.getSource() + "_" + sourceNode.getId();
		String destination = verificationBean.getDestination() + "_" + destinationNode.getId();

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

	public Verification runTests(Graph graph, Paths paths, String source, String destination) {
		if (paths.getPath().size() == 0) {
			System.out.println("No paths between '" + source + "' and '" + destination + "'");
			return new Verification("UNSAT");
		}

		sanitizePaths(paths);
		
		System.out.println("Before pruning");
		for (List<String> path : sanitizedPaths){
			System.out.println(path);
		}
		
		prunePaths();
		
		System.out.println("After pruning");
		for (List<String> path : sanitizedPaths){
			System.out.println(path);
		}

		deletePreviousTestFiles(this.scenarioFolder, this.testFolder);

		generateChainsFile(graph, chainsFile);

		generateConfigFile(graph, configFile);

		generateTestScenarios(chainsFile, configFile);

		generateTests(source, destination);

		prepareForCompilationAndExecution();

		compileTestScenarios(sanitizedPaths);

		runFiles(graph, runFiles);

		return evaluateResult();
	}

	private void prunePaths() {
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
	
	private Verification evaluateResult() {
		Verification v = new Verification();
		boolean sat = false;
		int unsat = 0;
		for (Test t : this.tests) {
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

	private void sanitizePaths(Paths paths) {
		for (String path : paths.getPath()) {
			System.out.println("Original path: " + path);
			List<String> newPath = sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
	}

	private void deletePreviousTestFiles(String scenarioDir, String Testdir) {
		final File scenarioFolder = new File(scenarioDir);
		final File[] scenarioFiles = scenarioFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches("Scenario_.*\\.java");
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

		final File testFolder = new File(Testdir);
		final File[] testFiles = testFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.matches("Scenario_.*\\.java");
			}
		});
		for (final File file : testFiles) {
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

	private void generateChainsFile(Graph graph, String chainsFile) {
		JSONObject root = new JSONObject();
		JSONArray chains = new JSONArray();
		

		int chainCounter = 0;

		for (List<String> path : this.sanitizedPaths) {
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

	private void generateConfigFileOld(Graph graph, String configFile) {
//		JSONObject root = new JSONObject();
//		JSONArray nodes = new JSONArray();
//
//		for (Node n : graph.getNodes().values()) {
//			JSONObject node = new JSONObject();
//			JSONArray configuration = new JSONArray();
//			Configuration nodeConfig = n.getConfiguration();
//			List<String> configurationList = nodeConfig.getConfigurationList();
//			List<ConfigurationObject> configurationMap = nodeConfig.getConfigurationMap();
//			if (configurationList.size() > 0) {
//				for (String s : configurationList) {
//					configuration.add("ip_" + s);
//				}
//			}
//			else if (configurationMap.size() > 0) {
//				for (ConfigurationObject c : configurationMap) {
//					Iterator<java.util.Map.Entry<String, String>> iter = c.getMap().entrySet().iterator();
//					while (iter.hasNext()) {
//						java.util.Map.Entry<String, String> entry = iter.next();
//						JSONObject configItem = new JSONObject();
//						configItem.put("ip_" + entry.getKey(), "ip_" + entry.getValue());
//						configuration.add(configItem);
//					}
//				}
//
//			}
//			node.put("configuration", configuration);
//			node.put("id", nodeConfig.getId());
//			node.put("description", nodeConfig.getDescription());
//
//			nodes.add(node);
//		}
//		root.put("nodes", nodes);
//
//		try (FileWriter file = new FileWriter(configFile)) {
//			file.write(root.toJSONString());
//			System.out.println("Successfully created 'config.json' with the following content:");
//			System.out.println(root);
//		}
//		catch (IOException e) {
//			throw new InternalServerErrorException("Error saving 'config.json' for neo4jmanager");
//		}

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
//		String s = null;
//		try {
//			Process p = Runtime.getRuntime().exec(cmd);
//			
//			BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//			BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//			// read the output from the command
//			System.out.println("Here is the standard output of the command:\n");
//			while ((s = stdInput.readLine()) != null) {
//				System.out.println(s);
//			}
//
//			// read any errors from the attempted command
//			System.out.println("Here is the standard error of the command (if any):\n");
//			while ((s = stdError.readLine()) != null) {
//				System.out.println(s);
//			}
//			p.waitFor();
//		}
//		catch (IOException e) {
//			throw new InternalServerErrorException("Error generating test scenarios for Z3: unable to execute generator");
//		}
//		catch (InterruptedException e) {
//			throw new InternalServerErrorException("Error generating test scenarios for Z3: generator got interrupted during execution");
//		}
		
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

	private void generateTests(String source, String destination) {

		List<String> scenarios = new ArrayList<String>();
		for (int i = 0; i < this.sanitizedPaths.size(); i++) {
			scenarios.add("Scenario_" + (i + 1));
		}

		for (String scenario : scenarios) {
			String[] cmd = {	"python", platfromIndependentPath(testGenerator), "-i",
								platfromIndependentPath(projectFolder + "examples/" + scenario + ".java"), "-o",
								platfromIndependentPath(projectFolder + scenario + "_test.java"), "-s", source, "-d",
								destination };
			printCommand(cmd);
//			String s = null;
//			try {
//				Process p = Runtime.getRuntime().exec(cmd);
//				
//				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
//
//				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//
//				// read the output from the command
//				System.out.println("Here is the standard output of the command:\n");
//				while ((s = stdInput.readLine()) != null) {
//					System.out.println(s);
//				}
//
//				// read any errors from the attempted command
//				System.out.println("Here is the standard error of the command (if any):\n");
//				while ((s = stdError.readLine()) != null) {
//					System.out.println(s);
//				}
//				
//				p.waitFor();
//			}
//			catch (IOException e) {
//				throw new InternalServerErrorException("Error generating tests for Z3: unable to execute generator");
//			}
//			catch (InterruptedException e) {
//				throw new InternalServerErrorException("Error generating tests for Z3: generator got interrupted during execution");
//			}
			
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

	private void prepareForCompilationAndExecution() {
		for (int i = 0; i < sanitizedPaths.size(); i++) {
			System.out.println("Creating a test file for path: " + sanitizedPaths.get(i).toString());
			String scenario = this.scenarioFile + "_" + (i + 1) + ".java";
			testFiles.add(new File(scenario));
			System.out.println("Scenario file " + scenario + " added to compilation");
			String testSourceFile = projectFolder + "Scenario_" + (i + 1) + "_test.java";
			String testClassFile = projectFolder + "Scenario_" + (i + 1) + "_test";
			testFiles.add(new File(testSourceFile));
			System.out.println("Test file " + testSourceFile + " added to copilation");
			runFiles.add(new File(testClassFile));
			System.out.println("Test file " + testClassFile + " added to execution");
		}
	}

	private void compileTestScenarios(List<List<String>> sanitizedPaths) {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new InternalServerErrorException("Error getting the Java compiler: JDK >= 1.8 required");
		}
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		try {
			fileManager.setLocation(StandardLocation.CLASS_OUTPUT, Arrays.asList(new File(projectFolder)));

			System.out.println("Java class path is: " + System.getProperty("java.class.path"));

			String z3 = "/usr/lib/com.microsoft.z3.jar";
			List<String> optionList = new ArrayList<String>();
			optionList.add("-classpath");
			optionList.add(System.getProperty("java.class.path") + ":" + z3);
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

			boolean success = compiler.getTask(	null,
												fileManager,
												diagnostics,
												optionList,
												null,
												fileManager.getJavaFileObjectsFromFiles(testFiles))
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

	public void runFiles(Graph graph, List<File> files) {
		for (int i = 0; i < files.size(); i++) {
			System.out.println("Running test file " + files.get(i).getAbsolutePath());
			int result = runIt(files.get(i));
			System.out.println("Execution returned: " + result);

			List<Node> path = new ArrayList<Node>();
			for (String nodeString : this.sanitizedPaths.get(i)) {
				Node node = graph.searchNodeByName(nodeString);
				path.add(node);
			}
			Test t = new Test(path, result);
			this.tests.add(t);
		}
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

}
