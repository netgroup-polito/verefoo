package it.polito.verigraph.grpc.test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.FilenameFilter;
import java.io.InputStreamReader;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;

import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.Policy;
import it.polito.verigraph.grpc.VerificationGrpc;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.grpc.client.Client;
import it.polito.verigraph.grpc.server.GrpcUtils;
import it.polito.verigraph.grpc.server.Service;
import it.polito.verigraph.service.ValidationUtils;
import it.polito.verigraph.test.TestCase;
import it.polito.verigraph.test.TestExecutionException;

@RunWith(JUnit4.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ReachabilityTest {
	private File			schema;
	private List<File>		testFiles	= new ArrayList<File>();
	private List<TestCase>	testCases	= new ArrayList<TestCase>();
	private Client	client;
	private Service server;

	@Before
	public void setUpBeforeClass() throws Exception {
		client = new Client("localhost" , 50051);
		server = new Service(50051);
		server.start();
		
		String folderName = System.getProperty("user.dir") + "/tester/testcases";
		File folder = new File(folderName);
		if (!folder.exists()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    String s;
		    do{
		    	System.out.println("Please enter the testcases folder path: ");
		    	s = in.readLine();
		    	if (isValidpath(s)){
		    		folder = new File(s);
		    		break;
		    	}
		    }while (s != null && s.length() != 0);
		    if(s == null)
		    	System.exit(0);
		}
		String schemaName = System.getProperty("user.dir") + "/tester/testcase_schema.json";
		File schema = new File(schemaName);
		if (!schema.exists()) {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		    String s;
		    do{
		    	System.out.println("Please enter the full path of 'testcase_schema.json': ");
		    	s = in.readLine();
		    	if (isValidpath(s)){
		    		folder = new File(s);
		    		break;
		    	}
		    }while (s != null && s.length() != 0);
		    if(s == null)
		    	System.exit(0);
		}

		this.schema = schema;
		this.testFiles = getTests(folder);
		this.testCases = getTestCases(this.testFiles);
	}
	
	@After
	public void tearDown() throws Exception {
		server.stop();
		client.shutdown();
	}
	
	@Test
	public final void wrongReachability() {
		System.out.println("DEBUG: starting testWrongReachability");
		
		VerificationGrpc nullVer = VerificationGrpc.newBuilder()
							.setErrorMessage("Graph with id 52 not found").build();
		//verification on uncreated graph
		Policy policyToVerify = Client.createPolicy("Node1", "Node4", "reachability", null, 52);			
		VerificationGrpc ver = client.verify(policyToVerify);			
		assertEquals(ver, nullVer);
		
		//verification on uncreated nodes
		nullVer = VerificationGrpc.newBuilder()
				.setErrorMessage("The \'source\' parameter \'Node5\' is not valid, please insert the name of an existing node").build();
		policyToVerify = Client.createPolicy("Node5", "Node4", "reachability", null, 1);			
		ver = client.verify(policyToVerify);			
		assertEquals(ver, nullVer);
		
		//verification on uncreated nodes
		nullVer = VerificationGrpc.newBuilder()
				.setErrorMessage("The \'source\' parameter \'Node1\' is not valid, please insert the name of an existing node").build();
		
		policyToVerify = Client.createPolicy("Node1", "Node10", "reachability", null, 1);			
		ver = client.verify(policyToVerify);			
		assertEquals(ver, nullVer);
		
	}
	
	public List<File> getTests(File folder) {
		List<File> filesList = new ArrayList<File>();

		System.out.println("Test folder set to '" + folder.getAbsolutePath() + "'");

		File[] files = folder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".json");
			}
		});

		for (File f : files) {
			filesList.add(f);
			System.out.println("File '" + f.getName() + "' added to test files");
		}

		return filesList;
	}

	public List<TestCase> getTestCases(List<File> files)	throws JsonParseException, JsonMappingException, IOException,
															Exception {
		List<TestCase> testCases = new ArrayList<TestCase>();

		for (File file : files) {
			validateTestFile(file);
			try {
				TestCase tc = new ObjectMapper().readValue(file, TestCase.class);
				testCases.add(tc);
			}
			catch (Exception e) {
				throw e;
			}
		}

		return testCases;
	}

	@Test
	public void runTestCases() throws VerifyClientException, TestExecutionException {
		int counter = 0;
		for (TestCase tc : this.testCases) {
			String result = runTestCase(tc);
			if (!result.equals(tc.getResult()))
				throw new TestExecutionException("Error running test given in file '"	+ this.testFiles.get(counter).getName()
									+ "'. Test returned '" + result + "' instead of '" + tc.getResult() + "'.");
			else
				System.out.println("Test given in file '"	+ this.testFiles.get(counter).getName() + "' returned '"
									+ result + "' as expected");
			counter++;
			
		}
		System.out.println("All tests PASSED");
	}
	
	private String runTestCase(TestCase tc) throws VerifyClientException, TestExecutionException{
		GraphGrpc graph = GrpcUtils.obtainGraph(tc.getGraph());
		
		NewGraph newGraph = this.client.createGraph(graph);
		if(newGraph.getSuccess() == false)
			throw new VerifyClientException("gRPC request failed");
		GraphGrpc createdGraph = newGraph.getGraph();
		
		GraphGrpc addedgraph = client.getGraph(createdGraph.getId());
		System.out.println(addedgraph);
		
		final Map<String, String> map = GrpcUtils.getParamGivenString(tc.getPolicyUrlParameters());
		
		Policy policy = Client.createPolicy(map.get("source"), 
											map.get("destination"),
											map.get("type"),
											map.get("middlebox"), 
											createdGraph.getId());
		VerificationGrpc verification = this.client.verify(policy);
		return verification.getResult();
	}

	public void validateTestFile(File testFile) throws Exception {
		JsonSchema schemaNode = null;
		try {
			schemaNode = ValidationUtils.getSchemaNode(schema);
		}
		catch (IOException e) {
			throw new Exception("Unable to load '" + schema.getAbsolutePath() + "' schema file");
		}
		catch (ProcessingException e) {
			throw new Exception("Unable to resolve '" + schema.getAbsolutePath() + "' schema file as a schema node");
		}

		JsonNode jsonNode;
		try {
			jsonNode = ValidationUtils.getJsonNode(testFile);
		}
		catch (IOException e) {
			throw new Exception("Unable to load '" + testFile.getAbsolutePath() + "' as a json node");
		}

		try {
			ValidationUtils.validateJson(schemaNode, jsonNode);
		}
		catch (ProcessingException e) {
			throw new Exception("There were errors in the validation of file '"	+ testFile.getAbsolutePath()
								+ "' against the json schema '" + schema.getAbsolutePath() + "': " + e.getMessage());

		}
	}

	private static boolean isValidpath(String s) {
		if (s==null)
			return false;
		File file = new File(s);
		return file.exists();
	}
}
