/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.service;

import java.io.IOException;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.xml.bind.JAXBException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.microsoft.z3.*;
import it.polito.neo4j.exceptions.MyInvalidDirectionException;
import it.polito.neo4j.jaxb.Paths;
import it.polito.neo4j.manager.Neo4jDBManager;
import it.polito.neo4j.manager.Neo4jLibrary;
import it.polito.neo4j.translator.*;
import it.polito.verigraph.exception.BadRequestException;
import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.exception.ForbiddenException;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Test;
import it.polito.verigraph.model.Verification;
import it.polito.verigraph.resources.beans.VerificationBean;
import it.polito.verigraph.solver.GeneratorSolver;
import it.polito.verigraph.solver.Scenario;

public class VerificationService {

    private Neo4jDBManager manager=new Neo4jDBManager();
    //private final static Logger LOGGER = Logger.getLogger(VerigraphLogger.class.getName());
    public static VerigraphLogger vlogger = VerigraphLogger.getVerigraphlogger();

    public VerificationService() {}

    private Paths getPaths(Graph graph, Node sourceNode, Node destinationNode) throws MyInvalidDirectionException {

        String source = sourceNode.getName();
        String destination = destinationNode.getName();
        Paths paths;
        paths=manager.getPath(graph.getId(), source, destination, "outgoing");
        return paths;
    }

    private List<List<String>> sanitizePaths(Paths paths) {
        List<List<String>> sanitizedPaths = new ArrayList<List<String>>();
        for (String path : paths.getPath()) {
            List<String> newPath = extractPath(path);
            sanitizedPaths.add(newPath);
        }
        return sanitizedPaths;
    }

    private List<String> extractPath(String path) {
        List<String> newPath = new ArrayList<String>();
        // find all nodes, i.e. all names between parentheses
        Matcher m = Pattern.compile("\\(([^)]+)\\)").matcher(path);
        while (m.find()) {
            String node = m.group(1);
            newPath.add(node);
        }
        return newPath;
    }

    private void printListsOfStrings(String message, List<List<String>> lists) {
        vlogger.logger.info(message);
        for (List<String> element : lists) {
            StringBuilder paths= new StringBuilder();
            for(String s : element){
                paths.append(s+" ");
            }
            vlogger.logger.info(paths.toString());
            //System.out.println(element);
        }
    }

    public Verification verify(long graphId, VerificationBean verificationBean) throws MyInvalidDirectionException, JsonParseException, JsonMappingException, JAXBException, IOException {
        if (graphId < 0) {
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
            throw new BadRequestException("The 'type' parameter '"+ type
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
                throw new BadRequestException("'"+ middlebox
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
                throw new BadRequestException("'"+ middlebox
                        + "' is of type 'endpoint', please choose a valid middlebox");
            }
            v = traversalVerification(graph, sourceNode, destinationNode, middleboxNode);
            break;
        default:
            break;
        }
        return v;
    }

    private Verification isolationVerification(Graph graph, Node sourceNode, Node destinationNode, Node middleboxNode) throws MyInvalidDirectionException {
        Long time_isolation=(long) 0;
        Calendar cal_isolation = Calendar.getInstance();
        Date start_time_isolation = cal_isolation.getTime();
        Paths paths = getPaths(graph, sourceNode, destinationNode);
        if (paths.getPath().size() == 0) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "'");
        }

        List<List<String>> sanitizedPaths = sanitizePaths(paths);
        List<Test> tests = extractTestsFromPaths(graph, sanitizedPaths, "UNKNWON");

        //printListsOfStrings("sanitizedPaths", sanitizedPaths);

        if (sanitizedPaths.isEmpty()) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "'");
        }
        extractPathsWithoutMiddlebox(sanitizedPaths, middleboxNode.getName());

        if (sanitizedPaths.isEmpty()) {
            return new Verification("UNSAT",
                    tests,
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "' which no traverse middlebox '"
                            + middleboxNode.getName() + "'. See below all the available paths.");
        }

        //printListsOfStrings("Paths with middlebox '" + middleboxNode.getName() + "'", sanitizedPaths);

        Map<Integer, GeneratorSolver> scenarios=createScenarios(sanitizedPaths, graph);

        tests = run(graph, scenarios, sourceNode.getName(), destinationNode.getName());
        Verification isolation=evaluateIsolationResults(tests, sourceNode.getName(),
                destinationNode.getName(),
                middleboxNode.getName());
        Calendar cal_isolation_stop = Calendar.getInstance();
        time_isolation = time_isolation +(cal_isolation_stop.getTime().getTime() - start_time_isolation.getTime());
        vlogger.logger.info("Time to check reachability policy: " + time_isolation);
        //System.out.println("Time to check reachability policy: " + time_isolation);
        return isolation;
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

    private Verification evaluateIsolationResults(List<Test> tests, String source, String destination,
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
                v.setResult("UNKNOWN");
                v.setComment("Isolation property with source '"+ source + "', destination '" + destination
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
            v.setResult("SAT");
            v.setComment("Isolation property with source '"+ source + "', destination '" + destination
                    + "' and middlebox '" + middlebox + "' is SATISFIED because reachability between '" + source
                    + "' and '" + middlebox + "' is UNSATISFIED in all paths between '" + source + "' and '"
                    + destination + "' which traverse middlebox '" + middlebox
                    + "' (see below all the paths that have been checked)");

        }
        else if (unsatCounter == tests.size()) {
            v.setResult("UNSAT");
            v.setComment("Isolation property with source '"+ source + "', destination '" + destination
                    + "' and middlebox '" + middlebox + "' is UNSATISFIED because reachability between '"
                    + source + "' and '" + middlebox + "' is SATISFIED in at least one path between '" + source
                    + "' and '" + destination + "' which traverses middlebox '" + middlebox
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

    private Verification traversalVerification(Graph graph, Node sourceNode, Node destinationNode, Node middleboxNode) throws MyInvalidDirectionException {
        Long time_traversal=(long) 0;
        Calendar cal_traversal = Calendar.getInstance();
        Date start_time_traversal = cal_traversal.getTime();
        Paths paths = getPaths(graph, sourceNode, destinationNode);
        if (paths.getPath().size() == 0) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "'");
        }

        List<List<String>> pathsBetweenSourceAndDestination = sanitizePaths(paths);

        //printListsOfStrings("Paths", pathsBetweenSourceAndDestination);

        if (pathsBetweenSourceAndDestination.isEmpty()) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
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
                    "There are no paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "' which traverse middlebox '"
                            + middleboxNode.getName() + "'. See below all the available paths");
        }

        //printListsOfStrings("Paths with middlebox '" + middleboxNode.getName() + "'", pathsWithMiddlebox);

        Map<Integer, GeneratorSolver> scenarios=createScenarios(pathsWithMiddlebox, graph);

        tests = run(graph, scenarios, sourceNode.getName(), destinationNode.getName());

        for (Test t : tests) {
            if (t.getResult().equals("UNSAT")) {
                return new Verification("UNSAT",
                        tests,
                        "There is at least a path between '"+ sourceNode.getName() + "' and '"
                                + destinationNode.getName() + "' traversing middlebox '"
                                + middleboxNode.getName() + "' where '" + sourceNode.getName()
                                + "' cannot reach '" + destinationNode.getName()
                                + "'. See below the paths that have been checked");
            }
            if (t.getResult().equals("UNKNOWN")) {
                return new Verification("UNKNOWN",
                        tests,
                        "There is at least a path between '"+ sourceNode.getName() + "' and '"
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
                    "All the paths between node '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "' traverse middlebox '"
                            + middleboxNode.getName() + "'");
        }


        Map<Integer, GeneratorSolver> scenarios2=createScenarios( pathsBetweenSourceAndDestination, graph);

        tests = run(graph, scenarios2, sourceNode.getName(), destinationNode.getName());

        Verification traversal= evaluateTraversalResults(tests,
                sourceNode.getName(),
                destinationNode.getName(),
                middleboxNode.getName());

        Calendar cal_traversal_stop = Calendar.getInstance();
        time_traversal = time_traversal +(cal_traversal_stop.getTime().getTime() - start_time_traversal.getTime());
        vlogger.logger.info("Time to check traversal policy: " + time_traversal);
        //System.out.println("Time to check traversal policy: " + time_traversal);
        return traversal;
    }

    private Verification evaluateTraversalResults(List<Test> tests, String source, String destination,
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
                v.setComment("There is at least one path from '"+ source + "' to '" + destination
                        + "' that doesn't traverse middlebox '" + middlebox
                        + "' (see below all the paths that have been checked)");
            }
            else if (t.getResult().equals("UNSAT")) {
                unsatCounter++;
            }
        }
        if (isSat) {
            v.setResult("UNSAT");
            v.setComment("There is at least one path from '"+ source + "' to '" + destination
                    + "' that doesn't traverse middlebox '" + middlebox
                    + "' (see below all the paths that have been checked)");
        }
        else if (unsatCounter == tests.size()) {
            v.setResult("SAT");
            v.setComment("The only available paths from '"+ source + "' to '" + destination
                    + "' are those that traverse middlebox '" + middlebox
                    + "' (see below the alternative paths that have been checked and are unusable)");
        }
        return v;
    }

    private Verification reachabilityVerification(Graph graph, Node sourceNode, Node destinationNode) throws MyInvalidDirectionException {
        Long time_reachability=(long) 0;
        Calendar cal_reachability = Calendar.getInstance();
        Date start_time_reachability = cal_reachability.getTime();

        Paths paths = getPaths(graph, sourceNode, destinationNode);

        if (paths.getPath().size() == 0) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "'");
        }

        List<List<String>> sanitizedPaths = sanitizePaths(paths);

        printListsOfStrings("Paths", sanitizedPaths);

        if (sanitizedPaths.isEmpty()) {
            return new Verification("UNSAT",
                    "There are no available paths between '"+ sourceNode.getName() + "' and '"
                            + destinationNode.getName() + "'");
        }

        Map<Integer, GeneratorSolver> scenarios=createScenarios(sanitizedPaths, graph);

        List<Test> tests = run(graph, scenarios, sourceNode.getName(), destinationNode.getName());

        Calendar cal_reachability_after_run = Calendar.getInstance();
        time_reachability = time_reachability +(cal_reachability_after_run.getTime().getTime() - start_time_reachability.getTime());
        vlogger.logger.info("Time reachability after run: " + time_reachability);
        //System.out.println("Time reachability after run: " + time_reachability);

        Verification reachability= evaluateReachabilityResult(tests, sourceNode.getName(), destinationNode.getName());

        Calendar cal_reachability_stop = Calendar.getInstance();
        time_reachability = time_reachability +(cal_reachability_stop.getTime().getTime() - start_time_reachability.getTime());
        vlogger.logger.info("Time to check reachability policy: " + time_reachability);
        //System.out.println("Time to check reachability policy: " + time_reachability);

        return reachability;
    }

    private List<Test> run(Graph graph, Map<Integer, GeneratorSolver> scenarios, String src, String dst) {
        List<Test> tests = new ArrayList<Test>();
        String result;

        //estimation time
        //Long time=(long) 0;
        //Calendar cal = Calendar.getInstance();
        //Date start_time = cal.getTime();

        for(Map.Entry<Integer, GeneratorSolver> t : scenarios.entrySet()){

            result=t.getValue().run(src, dst);

            List<Node> path = new ArrayList<Node>();
            for (String nodeString : t.getValue().getPaths()) {
                Node node = graph.searchNodeByName(nodeString);
                path.add(node);
            }
            Test test = new Test(path, result);
            tests.add(test);
        }

        //Calendar cal2 = Calendar.getInstance();
        //time = time +(cal2.getTime().getTime() - start_time.getTime());
        //System.out.println("Time occur to run: " + time);

        return tests;
    }

    private Map<Integer, GeneratorSolver> createScenarios(List<List<String>> sanitizedPaths, Graph graph) {
        int index=0;
        Map<Integer, GeneratorSolver> scenarios=new HashMap<Integer, GeneratorSolver>();
        for(List<String> s : sanitizedPaths){
            Scenario tmp=new Scenario(graph, s);
            tmp.createScenario();
            GeneratorSolver gs=new GeneratorSolver(tmp, s);
            gs.genSolver();
            scenarios.put(index++, gs);
        }
        return scenarios;
    }

    private Verification evaluateReachabilityResult(List<Test> tests, String source, String destination) {
        Verification v = new Verification();
        boolean sat = false;
        int unsat = 0;
        for (Test t : tests) {

            if (t.getResult().equals("SAT")) {
                sat = true;
            }

            else if (t.getResult().equals("UNKNOWN")) {
                v.setResult("UNKNWON");
                v.setComment("Reachability from '"+ source + "' to '" + destination
                        + "' is unknown. See all the checked paths below");

            }
            else if (t.getResult().equals("UNSAT")) {
                unsat++;
            }
            v.getTests().add(t);
        }
        if (sat) {
            v.setResult("SAT");
            v.setComment("There is at least one path '"+ source + "' can use to reach '" + destination
                    + "'. See all the available paths below");

        }
        else if (unsat == tests.size()) {
            v.setResult("UNSAT");
            v.setComment("There isn't any path '"+ source + "' can use to reach '" + destination
                    + "'. See all the checked paths below");

        }

        return v;
    }

    public List<List<Node>> getPaths(long graphId, String source, String destination) throws JsonParseException, JsonMappingException, JAXBException, IOException, MyInvalidDirectionException {
        if (graphId < 0) {
            throw new ForbiddenException("Illegal graph id: " + graphId);
        }
        GraphService graphService = new GraphService();
        Graph graph = graphService.getGraph(graphId);
        if (graph == null) {
            throw new DataNotFoundException("Graph with id " + graphId + " not found");
        }

        if (source == null || source.equals("")) {
            throw new BadRequestException("Please specify the 'source' parameter in your request");
        }
        if (destination == null || destination.equals("")) {
            throw new BadRequestException("Please specify the 'destination' parameter in your request");
        }

        Node sourceNode = graph.searchNodeByName(source);
        Node destinationNode = graph.searchNodeByName(destination);

        if (sourceNode == null) {
            throw new BadRequestException("The 'source' parameter '" + source + "' is not valid, please insert the name of an existing node");
        }
        if (destinationNode == null) {
            throw new BadRequestException("The 'destination' parameter '" + destination + "' is not valid, please insert the name of an existing node");
        }

        Paths all_paths = getPaths(graph, sourceNode, destinationNode);

        if (all_paths.getPath().size() == 0) {
            vlogger.logger.info("No path available");
            //System.out.println("No path available");
            return null;
        }

        List<List<String>> sanitizedPaths = sanitizePaths(all_paths);

        printListsOfStrings("Paths", sanitizedPaths);

        if (sanitizedPaths.isEmpty()) {
            return null;
        }

        List<List<Node>> paths=new ArrayList<List<Node>>();
        List<Node> p= new ArrayList<Node>();

        for(int i=0; i<sanitizedPaths.size(); i++){
            List<String> name=sanitizedPaths.get(i);
            for(int j=0; j<name.size(); j++){
                Node n=graph.searchNodeByName(name.get(j));
                if(n!=null)
                    p.add(j, n);
            }
            if(!p.isEmpty())
                paths.add(i, p);
        }

        if(!paths.isEmpty())
            return paths;
        else
            return null;
    }
}