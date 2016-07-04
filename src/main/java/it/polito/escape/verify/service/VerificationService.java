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
import javax.tools.ToolProvider;
import javax.ws.rs.ProcessingException;
import javax.xml.bind.JAXBException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fasterxml.jackson.databind.JsonNode;

import it.polito.escape.verify.client.Neo4jManagerClient;
import it.polito.escape.verify.exception.BadRequestException;
import it.polito.escape.verify.exception.DataNotFoundException;
import it.polito.escape.verify.exception.ForbiddenException;
import it.polito.escape.verify.exception.InternalServerErrorException;
import it.polito.escape.verify.model.Configuration;
import it.polito.escape.verify.model.Entry;
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

	private static final String	generatorFolder		= System.getProperty("catalina.base")
														+ "/webapps/verify/WEB-INF/classes/tests/j-verigraph-generator";

	private String				testClassGenerator	= generatorFolder + "/test_class_generator.py";

	private String				testGenerator		= generatorFolder + "/test_generator.py";

	public VerificationService() {

	}

	private Paths getPaths(Graph graph, Node sourceNode, Node destinationNode) {

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
																routingTable.get(node.getName()	+ "_" + node.getId()
																					+ "_out")
																			.add(new Entry(	"output",
																							neighbour.getName()	+ "_"
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

		Neo4jManagerClient client = new Neo4jManagerClient(	"http://localhost:8080/neo4jmanager/rest/",
															source,
															destination,
															endpoints,
															firewalls,
															routingTable);

		Paths paths = null;
		try {
			paths = client.getPaths();
		}
		catch (JAXBException e) {
			throw new InternalServerErrorException("Error generating input for neo4jmanager: " + e.getMessage());
		}
		catch (ProcessingException e) {
			throw new InternalServerErrorException("Response of neo4jmanager doesn't contain any path: "
													+ e.getMessage());
		}
		catch (IllegalStateException e) {
			throw new InternalServerErrorException("Error getting a response from neo4jmanager, no input stream for paths or input stream already consumed: "
													+ e.getMessage());
		}
		catch (Exception e) {
			throw new InternalServerErrorException("Unable to continue due to a neo4jmanager error: " + e.getMessage());
		}

		return paths;

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

	private List<List<String>> sanitizePaths(Paths paths) {
		List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
		for (String path : paths.getPath()) {
			System.out.println("Original path: " + path);
			List<String> newPath = sanitizePath(path);
			sanitizedPaths.add(newPath);
		}
		return sanitizedPaths;
	}

	static private Map<String, Long> toMap(List<String> lst) {
		return lst.stream().collect(Collectors.groupingBy(s -> s, Collectors.counting()));
	}

	private void eliminateLoopsInPaths(List<List<String>> sanitizedPaths) {
		List<List<String>> pathsToBeRemoved = new ArrayList<List<String>>();

		for (List<String> path : sanitizedPaths) {
			Map<String, Long> occurrencesMap = toMap(path);
			for (long occurrences : occurrencesMap.values()) {
				if (occurrences > 1) {
					pathsToBeRemoved.add(path);
					break;
				}
			}
		}
		for (List<String> path : pathsToBeRemoved) {
			sanitizedPaths.remove(path);
		}
	}

	private void printListsOfStrings(String message, List<List<String>> lists) {
		System.out.println(message);
		for (List<String> element : lists) {
			System.out.println(element);
		}
	}

	private static File createTempDir(String prefix) throws IOException {
		String tmpDirStr = System.getProperty("java.io.tmpdir");
		if (tmpDirStr == null) {
			throw new IOException("System property 'java.io.tmpdir' does not specify a tmp dir");
		}

		File tmpDir = new File(tmpDirStr);
		if (!tmpDir.exists()) {
			boolean created = tmpDir.mkdirs();
			if (!created) {
				throw new IOException("Unable to create tmp dir " + tmpDir);
			}
		}

		File resultDir = null;
		int suffix = (int) System.currentTimeMillis();
		int failureCount = 0;
		do {
			resultDir = new File(tmpDir, prefix + suffix % 10000);
			suffix++;
			failureCount++;
		} while (resultDir.exists() && failureCount < 50);

		if (resultDir.exists()) {
			throw new IOException(failureCount
									+ " attempts to generate a non-existent directory name failed, giving up");
		}
		boolean created = resultDir.mkdir();
		if (!created) {
			throw new IOException("Failed to create tmp directory");
		}

		return resultDir;
	}

	@SuppressWarnings("unchecked")
	private void generateChainsFile(Graph graph, List<List<String>> sanitizedPaths, String chainsFile) {
		JSONObject root = new JSONObject();
		JSONArray chains = new JSONArray();

		int chainCounter = 0;

		for (List<String> path : sanitizedPaths) {
			Iterator<String> pathsIterator = path.iterator();
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

	@SuppressWarnings("unchecked")
	private void generateConfigFile(Graph graph, String configFile) {
		JSONObject root = new JSONObject();
		JSONArray nodes = new JSONArray();

		for (Node n : graph.getNodes().values()) {
			JSONObject node = new JSONObject();
			// JSONArray configuration = new JSONArray();
			Configuration nodeConfig = n.getConfiguration();
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

	private void printCommand(String[] cmd) {
		for (String c : cmd) {
			System.out.printf(c + " ");
		}
		System.out.println("");
	}

	private String platfromIndependentPath(String path) {
		path = path.replaceAll("/", Matcher.quoteReplacement(Character.toString(File.separatorChar)));
		return path;
	}

	private void generateTestScenarios(String chainsFile, String configFile, String scenarioFile) {

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
			if (process.exitValue() != 0) {
				throw new InternalServerErrorException("Unable to generate test scenario file for the verification request: test_class_generator returned "
														+ process.exitValue());
			}
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Error generating tests for Z3: unable to execute generator");
		}
		catch (InterruptedException e) {
			throw new InternalServerErrorException("Error generating tests for Z3: generator got interrupted during execution");
		}

	}

	private void generateTests(	int scenariosCounter, String scenariosBasename, String source, String destination,
								String testsBasename) {

		List<String> scenarios = new ArrayList<String>();
		List<String> tests = new ArrayList<String>();
		for (int i = 0; i < scenariosCounter; i++) {
			scenarios.add(scenariosBasename + "_" + (i + 1) + ".java");
			tests.add(testsBasename + "_" + (i + 1) + ".java");
		}

		for (int i = 0; i < scenariosCounter; i++) {
			String[] cmd = {	"python", platfromIndependentPath(testGenerator), "-i",
								platfromIndependentPath(scenarios.get(i)), "-o", platfromIndependentPath(tests.get(i)),
								"-s", source, "-d", destination };
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
				if (process.exitValue() != 0) {
					throw new InternalServerErrorException("Unable to generate test file for the verification request: test_generator returned "
															+ process.exitValue());
				}
			}
			catch (IOException e) {
				throw new InternalServerErrorException("Error generating tests for Z3: unable to execute generator");
			}
			catch (InterruptedException e) {
				throw new InternalServerErrorException("Error generating tests for Z3: generator got interrupted during execution");
			}

		}

	}

	private void prepareForCompilationAndExecution(	int scenariosCounter, String scenarioBasename, String testBasename,
													List<File> sourceFiles, List<File> classFiles) {
		for (int i = 0; i < scenariosCounter; i++) {
			String scenario = scenarioBasename + "_" + (i + 1) + ".java";
			sourceFiles.add(new File(scenario));
			System.out.println("Scenario file " + scenario + " added to compilation");

			String testSource = testBasename + "_" + (i + 1) + ".java";
			String testClass = testBasename + "_" + (i + 1);

			sourceFiles.add(new File(testSource));
			System.out.println("Test file " + testSource + " added to copilation");
			classFiles.add(new File(testClass));
			System.out.println("Test file " + testClass + " added to execution");
		}
	}

	private void compileFiles(List<File> files, String folder) {

		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
		if (compiler == null) {
			throw new InternalServerErrorException("Error getting the Java compiler: JDK >= 1.8 required");
		}
		StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);

		try {
			// fileManager.setLocation(StandardLocation.CLASS_OUTPUT,
			// Arrays.asList(new File(projectFolder)));

			// String z3 = "/usr/lib/com.microsoft.z3.jar";
			// List<String> optionList = new ArrayList<String>();
			// optionList.add("-classpath");
			// optionList.add(System.getProperty("java.class.path") + ":" + z3);
			List<String> optionList = new ArrayList<String>();
			optionList.add("-d");
			optionList.add(folder);
			DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

			boolean success = compiler
										.getTask(	null,
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

	private int runIt(File filename, String folder) {
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
			Class<?> userClass = new DynamicClassLoader(folder).load("tests." + filenameNoExtension);
			Object context = ReflectUtil.newInstance(userClass);
			Object result = ReflectUtil.invoke("run", context);
			return (int) result;
		}
		catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			throw new InternalServerErrorException("Error executing Z3 tests: "	+ e.getMessage()
													+ ". There are errors in the Z3 model.");
		}
	}

	private List<Test> runFiles(String folder, List<List<String>> paths, Graph graph, List<File> files) {
		List<Test> tests = new ArrayList<Test>();
		for (int i = 0; i < files.size(); i++) {
			System.out.println("Running test file \"" + files.get(i).getAbsolutePath() + "\"");
			int result = runIt(files.get(i), folder);
			System.out.println("Execution returned: " + result);

			List<Node> path = new ArrayList<Node>();
			for (String nodeString : paths.get(i)) {
				Node node = graph.searchNodeByName(nodeString);
				path.add(node);
			}
			Test t = new Test(path, result);
			tests.add(t);
		}

		return tests;
	}
	
	@SuppressWarnings("unused")
	private static boolean deleteDir(File dir) {
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

	@SuppressWarnings("unused")
	private void deleteFilesWithPrefix(String directory, String prefix, String extension) {
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
		if (source == null || source.equals("")) {
			throw new BadRequestException("Please specify the 'source' parameter in your request");
		}
		if (destination == null || destination.equals("")) {
			throw new BadRequestException("Please specify the 'destination' parameter in your request");
		}
		if (type == null || type.equals("")) {
			throw new BadRequestException("Please specify the 'type' parameter in your request");
		}

		Node sourceNode = graph.searchNodeByName(verificationBean.getSource());
		Node destinationNode = graph.searchNodeByName(verificationBean.getDestination());

		if (sourceNode == null) {
			throw new BadRequestException("The 'source' parameter '" + source + "' is not valid, please insert the name of an existing node");
		}
		if (destinationNode == null) {
			throw new BadRequestException("The 'destination' parameter '" + destination + "' is not valid, please insert the name of an existing node");
		}
		if ((!type.equals("reachability")) && (!type.equals("isolation")) && (!type.equals("traversal"))) {
			throw new BadRequestException("The 'type' parameter '"	+ type
											+ "' is not valid: valid types are: 'reachability', 'isolation' and 'traversal'");
		}

		Verification v = null;
		String middlebox;
		Node middleboxNode;
		switch (type) {
			case "reachability":
				v = reachabilityVerification(graph, sourceNode, destinationNode);
				break;
			case "isolation":
				middlebox = verificationBean.getMiddlebox();
				if (middlebox == null || middlebox.equals("")) {
					throw new BadRequestException("Please specify the 'middlebox' parameter in your request");
				}

				middleboxNode = graph.searchNodeByName(middlebox);
				if (middleboxNode == null) {
					throw new BadRequestException("The 'middlebox' parameter '" + middlebox + "' is not valid, please insert the name of an existing node");
				}
				if (middleboxNode.getFunctional_type().equals("endpoint")) {
					throw new BadRequestException("'"	+ middlebox
													+ "' is of type 'endpoint', please choose a valid middlebox");
				}
				v = isolationVerification(graph, sourceNode, destinationNode, middleboxNode);
				break;
			case "traversal":
				middlebox = verificationBean.getMiddlebox();
				if (middlebox == null || middlebox.equals("")) {
					throw new BadRequestException("Please specify the 'middlebox' parameter in your request");
				}

				middleboxNode = graph.searchNodeByName(middlebox);
				if (middleboxNode == null) {
					throw new BadRequestException("The 'middlebox' parameter '" + middlebox + "' is not valid, please insert the name of an existing node");
				}
				if (middleboxNode.getFunctional_type().equals("endpoint")) {
					throw new BadRequestException("'"	+ middlebox
													+ "' is of type 'endpoint', please choose a valid middlebox");
				}
				v = traversalVerification(graph, sourceNode, destinationNode, middleboxNode);
				break;
			default:
				break;
		}

		return v;
	}

	private Verification isolationVerification(Graph graph, Node sourceNode, Node destinationNode, Node middleboxNode) {

		Paths paths = getPaths(graph, sourceNode, destinationNode);
		if (paths.getPath().size() == 0) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		List<List<String>> sanitizedPaths = sanitizePaths(paths);

		printListsOfStrings("Before loops removal", sanitizedPaths);

		eliminateLoopsInPaths(sanitizedPaths);

		printListsOfStrings("After loops removal", sanitizedPaths);

		if (sanitizedPaths.isEmpty()) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		List<Test> tests = extractTestsFromPaths(graph, sanitizedPaths, "UNKNWON");

		extractPathsWithMiddlebox(sanitizedPaths, middleboxNode.getName());

		if (sanitizedPaths.isEmpty()) {
			return new Verification("UNSAT",
									tests,
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
											+ destinationNode.getName() + "' which traverse middlebox '"
											+ middleboxNode.getName() + "'. See below all the available paths.");
		}

		printListsOfStrings("Paths with middlebox '" + middleboxNode.getName() + "'", sanitizedPaths);

		File tempDir = null;

		try {
			tempDir = createTempDir("isolation");
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to perform verification: " + e.getMessage());
		}

		String chainsFile = tempDir.getAbsolutePath() + "/chains.json";
		generateChainsFile(graph, sanitizedPaths, chainsFile);

		String configFile = tempDir.getAbsolutePath() + "/config.json";
		generateConfigFile(graph, configFile);

		String isolationScenariosBasename = tempDir.getAbsolutePath() + "/IsolationScenario";
		generateTestScenarios(chainsFile, configFile, isolationScenariosBasename);

		String isolationTestsBasename = tempDir.getAbsolutePath() + "/IsolationTest";
		generateTests(	sanitizedPaths.size(),
						isolationScenariosBasename,
						sourceNode.getName(),
						middleboxNode.getName(),
						isolationTestsBasename);

		List<File> sourceFiles = new ArrayList<File>();
		List<File> classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(	sanitizedPaths.size(),
											isolationScenariosBasename,
											isolationTestsBasename,
											sourceFiles,
											classFiles);

		compileFiles(sourceFiles, tempDir.getAbsolutePath());

		tests = runFiles(tempDir.getAbsolutePath(), sanitizedPaths, graph, classFiles);

		return evaluateIsolationResults(tests,
										sourceNode.getName(),
										destinationNode.getName(),
										middleboxNode.getName());

	}

	private List<Test> extractTestsFromPaths(Graph graph, List<List<String>> paths, String result) {
		List<Test> tests = new ArrayList<Test>();
		for (List<String> path : paths) {
			List<Node> nodes = new ArrayList<Node>();
			for (String nodeName : path) {
				nodes.add(graph.searchNodeByName(nodeName));
			}
			tests.add(new Test(nodes, result));
		}
		return tests;
	}

	private Verification evaluateIsolationResults(	List<Test> tests, String source, String destination,
													String middlebox) {
		Verification v = new Verification();
		boolean isSat = false;
		int unsatCounter = 0;
		for (Test t : tests) {
			v.getTests().add(t);

			if (t.getResult().equals("SAT")) {
				isSat = true;
			}
			else if (t.getResult().equals("UNKNOWN")) {
				v.setResult("UNKNWON");
				v.setComment("Isolation property with source '"	+ source + "', destination '" + destination
								+ "' and middlebox '" + middlebox + "' is UNKNOWN because although '" + source
								+ "' cannot reach '" + middlebox + "' in any path from '" + source + "' to '"
								+ destination + "' which traverses middlebox '" + middlebox
								+ "' at least one reachability test between '" + source + "' and '" + middlebox
								+ "' returned UNKNOWN (see below all the paths that have been checked)");
			}
			else if (t.getResult().equals("UNSAT")) {
				unsatCounter++;
			}
		}
		if (isSat) {
			v.setResult("UNSAT");
			v.setComment("Isolation property with source '"	+ source + "', destination '" + destination
							+ "' and middlebox '" + middlebox + "' is UNSATISFIED because reachability between '"
							+ source + "' and '" + middlebox + "' is SATISFIED in at least one path between '" + source
							+ "' and '" + destination + "' which traverses middlebox '" + middlebox
							+ "' (see below all the paths that have been checked)");
		}
		else if (unsatCounter == tests.size()) {
			v.setResult("SAT");
			v.setComment("Isolation property with source '"	+ source + "', destination '" + destination
							+ "' and middlebox '" + middlebox + "' is SATISFIED because reachability between '" + source
							+ "' and '" + middlebox + "' is UNSATISFIED in all paths between '" + source + "' and '"
							+ destination + "' which traverse middlebox '" + middlebox
							+ "' (see below all the paths that have been checked)");
		}
		return v;

	}

	private void extractPathsWithMiddlebox(List<List<String>> sanitizedPaths, String middleboxName) {
		List<List<String>> pathsToBeRemoved = new ArrayList<List<String>>();
		for (List<String> path : sanitizedPaths) {
			boolean middleboxFound = false;
			for (String node : path) {
				if (node.equals(middleboxName)) {
					middleboxFound = true;
					break;
				}
			}
			if (!middleboxFound) {
				pathsToBeRemoved.add(path);
			}
		}

		for (List<String> path : pathsToBeRemoved) {
			sanitizedPaths.remove(path);
		}

	}

	private void extractPathsWithoutMiddlebox(List<List<String>> sanitizedPaths, String middleboxName) {
		List<List<String>> pathsToBeRemoved = new ArrayList<List<String>>();
		for (List<String> path : sanitizedPaths) {
			boolean middleboxFound = false;
			for (String node : path) {
				if (node.equals(middleboxName)) {
					middleboxFound = true;
					break;
				}
			}
			if (middleboxFound) {
				pathsToBeRemoved.add(path);
			}
		}

		for (List<String> path : pathsToBeRemoved) {
			sanitizedPaths.remove(path);
		}

	}

	private Verification traversalVerification(Graph graph, Node sourceNode, Node destinationNode, Node middleboxNode) {

		Paths paths = getPaths(graph, sourceNode, destinationNode);
		if (paths.getPath().size() == 0) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		List<List<String>> pathsBetweenSourceAndDestination = sanitizePaths(paths);

		printListsOfStrings("Before loops removal", pathsBetweenSourceAndDestination);

		eliminateLoopsInPaths(pathsBetweenSourceAndDestination);

		printListsOfStrings("After loops removal", pathsBetweenSourceAndDestination);

		if (pathsBetweenSourceAndDestination.isEmpty()) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		List<Test> tests = extractTestsFromPaths(graph, pathsBetweenSourceAndDestination, "UNKNOWN");

		List<List<String>> pathsWithMiddlebox = new ArrayList<List<String>>();
		for (List<String> path : pathsBetweenSourceAndDestination) {
			pathsWithMiddlebox.add(path);
		}

		extractPathsWithMiddlebox(pathsWithMiddlebox, middleboxNode.getName());

		if (pathsWithMiddlebox.isEmpty()) {
			return new Verification("UNSAT",
									tests,
									"There are no paths between '"	+ sourceNode.getName() + "' and '"
											+ destinationNode.getName() + "' which traverse middlebox '"
											+ middleboxNode.getName() + "'. See below all the available paths");
		}

		printListsOfStrings("Paths with middlebox '" + middleboxNode.getName() + "'", pathsWithMiddlebox);

		File tempDir = null;

		try {
			tempDir = createTempDir("traversal");
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to perform verification: " + e.getMessage());
		}

		String chainsFile = tempDir.getAbsolutePath() + "/chains.json";
		generateChainsFile(graph, pathsWithMiddlebox, chainsFile);

		String configFile = tempDir.getAbsolutePath() + "/config.json";
		generateConfigFile(graph, configFile);

		String traversalScenariosBasename = tempDir.getAbsolutePath() + "/TraversalScenario";
		generateTestScenarios(chainsFile, configFile, traversalScenariosBasename);

		String traversalTestsBasename = tempDir.getAbsolutePath() + "/TraversalTest";
		generateTests(	pathsWithMiddlebox.size(),
						traversalScenariosBasename,
						sourceNode.getName(),
						destinationNode.getName(),
						traversalTestsBasename);

		List<File> sourceFiles = new ArrayList<File>();
		List<File> classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(	pathsWithMiddlebox.size(),
											traversalScenariosBasename,
											traversalTestsBasename,
											sourceFiles,
											classFiles);

		compileFiles(sourceFiles, tempDir.getAbsolutePath());

		tests = runFiles(tempDir.getAbsolutePath(), pathsWithMiddlebox, graph, classFiles);

		for (Test t : tests) {
			if (t.getResult().equals("UNSAT")) {
				return new Verification("UNSAT",
										tests,
										"There is at least a path between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "' traversing middlebox '"
												+ middleboxNode.getName() + "' where '" + sourceNode.getName()
												+ "' cannot reach '" + destinationNode.getName()
												+ "'. See below the paths that have been checked");
			}
			if (t.getResult().equals("UNKNOWN")) {
				return new Verification("UNKNOWN",
										tests,
										"There is at least a path between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "' traversing middlebox '"
												+ middleboxNode.getName() + "' where it is not guaranteed that '"
												+ sourceNode.getName() + "' can effectively reach '"
												+ destinationNode.getName()
												+ "'. See below the paths that have been checked");
			}
		}

		extractPathsWithoutMiddlebox(pathsBetweenSourceAndDestination, middleboxNode.getName());
		printListsOfStrings("Paths without middlebox '" + middleboxNode.getName() + "'", pathsBetweenSourceAndDestination);
		
		if (pathsBetweenSourceAndDestination.isEmpty()) {
			return new Verification("SAT",
									tests,
									"All the paths between node '"	+ sourceNode.getName() + "' and '"
											+ destinationNode.getName() + "' traverse middlebox '"
											+ middleboxNode.getName() + "'");
		}

		tempDir = null;

		try {
			tempDir = createTempDir("traversal");
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to perform verification: " + e.getMessage());
		}

		chainsFile = tempDir.getAbsolutePath() + "/chains.json";
		generateChainsFile(graph, pathsBetweenSourceAndDestination, chainsFile);

		configFile = tempDir.getAbsolutePath() + "/config.json";
		generateConfigFile(graph, configFile);

		traversalScenariosBasename = tempDir.getAbsolutePath() + "/TraversalScenario";
		generateTestScenarios(chainsFile, configFile, traversalScenariosBasename);

		traversalTestsBasename = tempDir.getAbsolutePath() + "/TraversalTest";
		generateTests(	pathsBetweenSourceAndDestination.size(),
						traversalScenariosBasename,
						sourceNode.getName(),
						destinationNode.getName(),
						traversalTestsBasename);

		sourceFiles = new ArrayList<File>();
		classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(	pathsBetweenSourceAndDestination.size(),
											traversalScenariosBasename,
											traversalTestsBasename,
											sourceFiles,
											classFiles);

		compileFiles(sourceFiles, tempDir.getAbsolutePath());

		tests = runFiles(tempDir.getAbsolutePath(), pathsBetweenSourceAndDestination, graph, classFiles);

		return evaluateTraversalResults(tests,
										sourceNode.getName(),
										destinationNode.getName(),
										middleboxNode.getName());

	}

	private Verification evaluateTraversalResults(	List<Test> tests, String source, String destination,
													String middlebox) {
		Verification v = new Verification();
		boolean isSat = false;
		int unsatCounter = 0;
		for (Test t : tests) {
			v.getTests().add(t);

			if (t.getResult().equals("SAT")) {
				isSat = true;
			}
			else if (t.getResult().equals("UNKNOWN")) {
				v.setResult("UNKNWON");
				v.setComment("There is at least one path from '"	+ source + "' to '" + destination
								+ "' that doesn't traverse middlebox '" + middlebox
								+ "' (see below all the paths that have been checked)");
			}
			else if (t.getResult().equals("UNSAT")) {
				unsatCounter++;
			}
		}
		if (isSat) {
			v.setResult("UNSAT");
			v.setComment("There is at least one path from '"	+ source + "' to '" + destination
							+ "' that doesn't traverse middlebox '" + middlebox
							+ "' (see below all the paths that have been checked)");
		}
		else if (unsatCounter == tests.size()) {
			v.setResult("SAT");
			v.setComment("The only available paths from '"	+ source + "' to '" + destination
							+ "' are those that traverse middlebox '" + middlebox
							+ "' (see below the alternative paths that have been checked and are unusable)");
		}
		return v;
	}

	private Verification reachabilityVerification(Graph graph, Node sourceNode, Node destinationNode) {
		Paths paths = getPaths(graph, sourceNode, destinationNode);

		if (paths.getPath().size() == 0) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		List<List<String>> sanitizedPaths = sanitizePaths(paths);

		printListsOfStrings("Before loops removal", sanitizedPaths);

		eliminateLoopsInPaths(sanitizedPaths);

		printListsOfStrings("After loops removal", sanitizedPaths);

		if (sanitizedPaths.isEmpty()) {
			return new Verification("UNSAT",
									"There are no available paths between '"	+ sourceNode.getName() + "' and '"
												+ destinationNode.getName() + "'");
		}

		File tempDir = null;

		try {
			tempDir = createTempDir("reachability");
		}
		catch (IOException e) {
			throw new InternalServerErrorException("Unable to perform verification: " + e.getMessage());
		}

		String chainsFile = tempDir.getAbsolutePath() + "/chains.json";
		generateChainsFile(graph, sanitizedPaths, chainsFile);

		String configFile = tempDir.getAbsolutePath() + "/config.json";
		generateConfigFile(graph, configFile);

		String reachabilityScenariosBasename = tempDir.getAbsolutePath() + "/ReachabilityScenario";
		generateTestScenarios(chainsFile, configFile, reachabilityScenariosBasename);

		String reachabilityTestsBasename = tempDir.getAbsolutePath() + "/ReachabilityTest";
		generateTests(	sanitizedPaths.size(),
						reachabilityScenariosBasename,
						sourceNode.getName(),
						destinationNode.getName(),
						reachabilityTestsBasename);

		List<File> sourceFiles = new ArrayList<File>();
		List<File> classFiles = new ArrayList<File>();
		prepareForCompilationAndExecution(	sanitizedPaths.size(),
											reachabilityScenariosBasename,
											reachabilityTestsBasename,
											sourceFiles,
											classFiles);

		compileFiles(sourceFiles, tempDir.getAbsolutePath());

		List<Test> tests = runFiles(tempDir.getAbsolutePath(), sanitizedPaths, graph, classFiles);

		return evaluateReachabilityResult(tests, sourceNode.getName(), destinationNode.getName());
	}

	private Verification evaluateReachabilityResult(List<Test> tests, String source, String destination) {
		Verification v = new Verification();
		boolean sat = false;
		int unsat = 0;
		for (Test t : tests) {
			v.getTests().add(t);

			if (t.getResult().equals("SAT")) {
				sat = true;
			}
			else if (t.getResult().equals("UNKNOWN")) {
				v.setResult("UNKNWON");
				v.setComment("Reachability from '"	+ source + "' to '" + destination
								+ "' is unknown. See all the checked paths below");
			}
			else if (t.getResult().equals("UNSAT")) {
				unsat++;
			}
		}
		if (sat) {
			v.setResult("SAT");
			v.setComment("There is at least one path '"	+ source + "' can use to reach '" + destination
							+ "'. See all the available paths below");
		}
		else if (unsat == tests.size()) {
			v.setResult("UNSAT");
			v.setComment("There isn't any path '"	+ source + "' can use to reach '" + destination
							+ "'. See all the checked paths below");
		}
		return v;
	}

}
