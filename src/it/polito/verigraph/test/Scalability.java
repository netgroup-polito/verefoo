/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.verigraph.client.VerifyClient;
import it.polito.verigraph.client.VerifyClientException;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Graph;
import it.polito.verigraph.model.Neighbour;
import it.polito.verigraph.model.Node;
import it.polito.verigraph.model.Verification;

public class Scalability {

    private VerifyClient client = new VerifyClient("http://localhost:8080/verigraph/api");

    public static void main(String[] args) throws VerifyClientException {
        Scalability s = new Scalability();

        reachabilityTest(s, 10);
        for(int i=50; i<=1000;i +=50)
            reachabilityTest(s,i); 

//        for(int i=50; i<=1000;i +=50)
//            isolationTest(s,i);

//      for(int i=50; i<=1000;i +=50)
//            traversalTest(s,i);
    }

    private static void reachabilityTest(Scalability s, int n) throws VerifyClientException {
        System.out.printf("Reachability test with N=" + n + ": ");
        printTimestamp();
        Graph graph = generateNatScenario(n);
        Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
        Verification result = s.client.getReachability(createdGraph.getId(), "client", "server");
        System.out.println("Test returned " + result.getResult());
        System.out.printf("Finished reachability test with N=" + n + ": ");
        printTimestamp();
        System.out.println();
    }

    private static void isolationTest(Scalability s, int n) throws VerifyClientException {
        System.out.printf("Isolation test with N=" + n + ": ");
        printTimestamp();
        Graph graph = generateNatScenario(n);
        Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
        Verification result = s.client.getIsolation(createdGraph.getId(), "client", "server", "firewall");
        System.out.println("Test returned " + result.getResult());
        System.out.printf("Finished isolation test with N=" + n + ": ");
        printTimestamp();
        System.out.println();
    }

    private static void traversalTest(Scalability s, int n) throws VerifyClientException {
        System.out.printf("Traversal test with N=" + n + ": ");
        printTimestamp();
        Graph graph = generateNatScenario(n);
        Graph createdGraph = s.client.createGraph(graph).readEntity(Graph.class);
        Verification result = s.client.getTraversal(createdGraph.getId(), "client", "server", "firewall");
        System.out.println("Test returned " + result.getResult());
        //System.out.println("Result explanation: " + result.getComment());
        System.out.printf("Finished traversal test with N=" + n + ": ");
        printTimestamp();
        System.out.println();
    }

    private static void printTimestamp() {
        java.util.Date date= new java.util.Date();
        System.out.println(new Timestamp(date.getTime()));
    }

    private static Graph generateNatScenario(int n) {
        List<Node> nodes = new ArrayList<Node>();

        Node client = new Node();
        client.setName("client");
        client.setFunctional_type("endhost");
        ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
        JsonNode clientConfig = new ObjectMapper().createObjectNode();
        ((ObjectNode)clientConfig).put("url", "www.facebook.com");
        ((ObjectNode)clientConfig).put("body", "word");
        ((ObjectNode)clientConfig).put("destination","server");
        ((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
        clientConfigArray.add(clientConfig);
        client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));

        Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
        clientNeighbours.put(1L, new Neighbour(1L, "nat1"));
        client.setNeighbours(clientNeighbours );
        //add client to list
        nodes.add(client);

        for(int i=0; i< n;i++){
            Node nat = new Node();
            nat.setId(i+1);
            nat.setName("nat" + (i+1));
            nat.setFunctional_type("nat");
            ArrayNode configArray = new ObjectMapper().createArrayNode();

            Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();

            //set left neighbour for each node except the first
            if(nat.getId() != 1){
                natNeighbours.put(1L, new Neighbour(1L, "nat" + i));
                configArray.add("client");
                for (int j=1; j <= i; j++){
                    configArray.add("nat" + j);
                }
            }
            //first nat: set only client as neighbour and natted node
            else{
                natNeighbours.put(1L, new Neighbour(1L, "client"));
                configArray.add("client");
            }
            //set right neighbour for each node except the last
            if(nat.getId() != n){
                natNeighbours.put(2L, new Neighbour(1L, "nat" + (i+2)));
            }
            //last nat: set server as neighbour
            else{
                natNeighbours.put(2L, new Neighbour(1L, "server"));
            }

            nat.setNeighbours(natNeighbours);
            nat.setConfiguration(new Configuration(nat.getName(),"", configArray));

            //add nat to list
            nodes.add(nat);
        }

        Node server = new Node();
        server.setName("server");
        server.setFunctional_type("webserver");
        ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
        server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));

        Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
        serverNeighbours.put(1L, new Neighbour(1L, "nat" + (n)));
        server.setNeighbours(serverNeighbours );

        //add server to list
        nodes.add(server);

        //create graph
        Graph g = new Graph();
        Map<Long, Node> graphNodes = new HashMap<Long, Node>();
        long index = 1L;
        for (Node node : nodes){
            graphNodes.put(index, node);
            index++;
        }
        g.setNodes(graphNodes);

        return g;
    }

    private static Graph generateFirewallScenario(int n) {
        List<Node> nodes = new ArrayList<Node>();

        Node client = new Node();
        client.setName("client");
        client.setFunctional_type("endhost");
        ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
        JsonNode clientConfig = new ObjectMapper().createObjectNode();
        ((ObjectNode)clientConfig).put("url", "www.facebook.com");
        ((ObjectNode)clientConfig).put("body", "word");
        ((ObjectNode)clientConfig).put("destination","server");
        ((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
        clientConfigArray.add(clientConfig);
        client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));

        Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
        clientNeighbours.put(1L, new Neighbour(1L, "firewall1"));
        client.setNeighbours(clientNeighbours );
        //add client to list
        nodes.add(client);

        for(int i=0; i< n;i++){
            Node firewall = new Node();
            firewall.setId(i+1);
            firewall.setName("firewall" + (i+1));
            firewall.setFunctional_type("firewall");
            ArrayNode configArray = new ObjectMapper().createArrayNode();

            Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();

            //set left neighbour for each node except the first
            if(firewall.getId() != 1){
                natNeighbours.put(1L, new Neighbour(1L, "firewall" + i));
            }
            //first firewall: set only client as neighbour and natted node
            else{
                natNeighbours.put(1L, new Neighbour(1L, "client"));
            }
            //set right neighbour for each node except the last
            if(firewall.getId() != n){
                natNeighbours.put(2L, new Neighbour(1L, "firewall" + (i+2)));
            }
            //last firewall: set server as neighbour
            else{
                natNeighbours.put(2L, new Neighbour(1L, "server"));
            }

            firewall.setNeighbours(natNeighbours);
            firewall.setConfiguration(new Configuration(firewall.getName(),"", configArray));

            //add nat to list
            nodes.add(firewall);
        }

        Node server = new Node();
        server.setName("server");
        server.setFunctional_type("webserver");
        ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
        server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));

        Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
        serverNeighbours.put(1L, new Neighbour(1L, "firewall" + (n)));
        server.setNeighbours(serverNeighbours );

        //add server to list
        nodes.add(server);

        //create graph
        Graph g = new Graph();
        Map<Long, Node> graphNodes = new HashMap<Long, Node>();
        long index = 1L;
        for (Node node : nodes){
            graphNodes.put(index, node);
            index++;
        }
        g.setNodes(graphNodes);

        return g;
    }

    private static Graph generateScenario(int n) {
        List<Node> nodes = new ArrayList<Node>();

        Node firewall = new Node();
        firewall.setName("firewall");
        firewall.setFunctional_type("firewall");
        ArrayNode firewallConfigArray = new ObjectMapper().createArrayNode();
        Map<Long, Neighbour> firewallNeighbours = new HashMap<Long, Neighbour>();

        for (int i=0; i < n; i++){
            if(i!=0){
                JsonNode firewallEntry = new ObjectMapper().createObjectNode();
                ((ObjectNode) firewallEntry).put("server", "client" + (i+1));
                firewallConfigArray.add(firewallEntry);
            }
            firewallNeighbours.put(new Long(i+1), new Neighbour(new Long(i+1), "client" + (i+1)));
        }

        firewallNeighbours.put(new Long(n+1), new Neighbour(new Long(n+1), "server"));

        firewall.setConfiguration(new Configuration(firewall.getName(),"", firewallConfigArray));


        firewall.setNeighbours(firewallNeighbours );
        //add client to list
        nodes.add(firewall);

        for(int i=0; i< n;i++){
            Node client = new Node();
            client.setId(i+1);
            client.setName("client" + (i+1));
            client.setFunctional_type("endhost");
            ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
            JsonNode clientConfig = new ObjectMapper().createObjectNode();
            ((ObjectNode)clientConfig).put("url", "www.facebook.com");
            ((ObjectNode)clientConfig).put("body", "word");
            ((ObjectNode)clientConfig).put("destination","server");
            ((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
            clientConfigArray.add(clientConfig);

            Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();

            clientNeighbours.put(1L, new Neighbour(1L, "firewall"));

            client.setNeighbours(clientNeighbours);
            client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));

            //add client to list
            nodes.add(client);
        }

        Node server = new Node();
        server.setName("server");
        server.setFunctional_type("webserver");
        ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
        server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));

        Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
        serverNeighbours.put(1L, new Neighbour(1L, "firewall"));
        server.setNeighbours(serverNeighbours );

        //add server to list
        nodes.add(server);

        //create graph
        Graph g = new Graph();
        Map<Long, Node> graphNodes = new HashMap<Long, Node>();
        long index = 1L;
        for (Node node : nodes){
            graphNodes.put(index, node);
            index++;
        }
        g.setNodes(graphNodes);

        return g;
    }

    private static Graph generateDpiScenario(int n) {
        List<Node> nodes = new ArrayList<Node>();

        Node client = new Node();
        client.setName("client");
        client.setFunctional_type("endhost");
        ArrayNode clientConfigArray = new ObjectMapper().createArrayNode();
        JsonNode clientConfig = new ObjectMapper().createObjectNode();
        ((ObjectNode)clientConfig).put("url", "www.facebook.com");
        ((ObjectNode)clientConfig).put("body", "word");
        ((ObjectNode)clientConfig).put("destination","server");
        ((ObjectNode)clientConfig).put("protocol", "HTTP_REQUEST");
        clientConfigArray.add(clientConfig);
        client.setConfiguration(new Configuration(client.getName(),"", clientConfigArray));

        Map<Long, Neighbour> clientNeighbours = new HashMap<Long, Neighbour>();
        clientNeighbours.put(1L, new Neighbour(1L, "dpi1"));
        client.setNeighbours(clientNeighbours );
        //add client to list
        nodes.add(client);

        for(int i=0; i< n;i++){
            Node dpi = new Node();
            dpi.setId(i+1);
            dpi.setName("dpi" + (i+1));
            dpi.setFunctional_type("dpi");
            ArrayNode configArray = new ObjectMapper().createArrayNode();
            configArray.add("drug");

            Map<Long, Neighbour> natNeighbours = new HashMap<Long, Neighbour>();

            //set left neighbour for each node except the first
            if(dpi.getId() != 1){
                natNeighbours.put(1L, new Neighbour(1L, "dpi" + i));
            }
            //first firewall: set only client as neighbour and natted node
            else{
                natNeighbours.put(1L, new Neighbour(1L, "client"));
            }
            //set right neighbour for each node except the last
            if(dpi.getId() != n){
                natNeighbours.put(2L, new Neighbour(1L, "dpi" + (i+2)));
            }
            //last firewall: set server as neighbour
            else{
                natNeighbours.put(2L, new Neighbour(1L, "server"));
            }

            dpi.setNeighbours(natNeighbours);
            dpi.setConfiguration(new Configuration(dpi.getName(),"", configArray));

            //add nat to list
            nodes.add(dpi);
        }

        Node server = new Node();
        server.setName("server");
        server.setFunctional_type("webserver");
        ArrayNode serverConfigArray = new ObjectMapper().createArrayNode();
        server.setConfiguration(new Configuration(server.getName(),"", serverConfigArray));

        Map<Long, Neighbour> serverNeighbours = new HashMap<Long, Neighbour>();
        serverNeighbours.put(1L, new Neighbour(1L, "dpi" + (n)));
        server.setNeighbours(serverNeighbours );

        //add server to list
        nodes.add(server);

        //create graph
        Graph g = new Graph();
        Map<Long, Node> graphNodes = new HashMap<Long, Node>();
        long index = 1L;
        for (Node node : nodes){
            graphNodes.put(index, node);
            index++;
        }
        g.setNodes(graphNodes);

        return g;
    }

}