/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.ResponseProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.main.JsonSchema;

import it.polito.verigraph.client.VerifyClient;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.service.ValidationUtils;

public class Tester {

    private File schema;

    private List<File> testFiles= new ArrayList<File>();

    private List<TestCase> testCases= new ArrayList<TestCase>();

    private String target;

    private VerifyClient verifyClient;

    public Tester(String target, File schema, File folder)throws JsonParseException, JsonMappingException, IOException,
    Exception {
        init(target, schema, folder);
    }

    private void init(String target, File schema, File folder)throws JsonParseException, JsonMappingException,
    IOException, Exception {
        this.target = target;
        this.verifyClient = new VerifyClient(this.target);
        this.schema = schema;
        this.testFiles = getTests(folder);
        this.testCases = getTestCases(this.testFiles);
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

    public List<TestCase> getTestCases(List<File> files)throws JsonParseException, JsonMappingException, IOException,
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

    private void runTestCases() throws VerifyClientException, TestExecutionException {
        int counter = 0;
        for (TestCase tc : this.testCases) {
            List<String> results = runTestCase(tc);
            Iterator<String> iter = tc.getResults().iterator();
            for(String result : results){
                if (iter.hasNext()){
                    if( !result.equals(iter.next()))
                        throw new TestExecutionException("Error running test given in file '"+ this.testFiles.get(counter).getName()
                                + "'. Test returned '" + result + "' instead of '" + tc.getResults() + "'.");
                    else
                        System.out.println("Test given in file '"+ this.testFiles.get(counter).getName() + "' returned '"
                                + result + "' as expected");
                } else throw new TestExecutionException("Error running test given in file '"+ this.testFiles.get(counter).getName()
                        + "'. Test returned '" + result + "' instead of '" + tc.getResults() + "'.");
            }
            counter++;
        }
        System.out.println("All tests PASSED");
    }

    private List<String> runTestCase(TestCase tc) throws VerifyClientException, TestExecutionException{
        Client client = ClientBuilder.newClient();

        List<String> results = new ArrayList<String>();

        Graph graph = tc.getGraph();
        Response response = null;
        try{
            response = this.verifyClient.createGraph(graph);
        }
        catch(ResponseProcessingException e){
            throw new TestExecutionException("Response processing has failed: " + e.getResponse().readEntity(String.class));
        }
        catch(javax.ws.rs.ProcessingException e){
            throw new TestExecutionException("HTTP request failed");
        }
        Graph createdGraph = response.readEntity(Graph.class);
        for (String urlParams : tc.getPolicyUrlParameters()){
            WebTarget target = client.target(this.target + "/graphs/" + createdGraph.getId() + "/policy" + urlParams);

            response = target.request().get();
            Verification verification = response.readEntity(Verification.class);
            results.add(verification.getResult());
        }
        return results;
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
            throw new Exception("There were errors in the validation of file '"+ testFile.getAbsolutePath()
            + "' against the json schema '" + schema.getAbsolutePath() + "': " + e.getMessage());

        }
    }

    public static void main(String[] args)throws JsonParseException, JsonMappingException, IOException,
    VerifyClientException, Exception {
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

        Tester tester = new Tester("http://localhost:8080/verigraph/api", schema, folder);

        tester.runTestCases();

    }

    private static boolean isValidpath(String s) {
        if (s==null)
            return false;
        File file = new File(s);
        return file.exists();
    }

}
