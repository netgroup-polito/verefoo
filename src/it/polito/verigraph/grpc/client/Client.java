/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.grpc.client;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.google.protobuf.Message;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import it.polito.verigraph.grpc.ConfigurationGrpc;
import it.polito.verigraph.grpc.GetRequest;
import it.polito.verigraph.grpc.GraphGrpc;
import it.polito.verigraph.grpc.NeighbourGrpc;
import it.polito.verigraph.grpc.NewGraph;
import it.polito.verigraph.grpc.NewNeighbour;
import it.polito.verigraph.grpc.NewNode;
import it.polito.verigraph.grpc.NodeGrpc;
import it.polito.verigraph.grpc.NodeGrpc.FunctionalType;
import it.polito.verigraph.grpc.Policy;
import it.polito.verigraph.grpc.Policy.PolicyType;
import it.polito.verigraph.grpc.RequestID;
import it.polito.verigraph.grpc.Status;
import it.polito.verigraph.grpc.VerificationGrpc;
import it.polito.verigraph.grpc.VerigraphGrpc;

public class Client {

    private final ManagedChannel channel;
    private final VerigraphGrpc.VerigraphBlockingStub blockingStub;

    public Client(String host, int port) {
        this(ManagedChannelBuilder.forAddress(host, port).usePlaintext(true));
    }

    /** Construct client for accessing RouteGuide server using the existing channel. */
    public Client(ManagedChannelBuilder<?> channelBuilder) {
        channel = channelBuilder.build();
        blockingStub = VerigraphGrpc.newBlockingStub(channel);
    }

    /** Get array of graphs */
    public List<GraphGrpc> getGraphs() {
        List<GraphGrpc> graphsRecveived = new ArrayList<GraphGrpc>();
        GetRequest request = GetRequest.newBuilder().build();
        Iterator<GraphGrpc> graphs;
        try {
            graphs = blockingStub.getGraphs(request);
            while (graphs.hasNext()) {
                GraphGrpc graph = graphs.next();
                System.out.println("[getGraphs] Graph id : "+graph.getId());
                if(graph.getErrorMessage().equals("")){
                    System.out.println("[getGraphs] Node id : "+graph.getId());
                    graphsRecveived.add(graph);
                }else{
                    System.out.println("[getGraphs] Error : " + graph.getErrorMessage());
                    return graphsRecveived;
                }
            }
        } catch (StatusRuntimeException ex) {
            System.err.println("[getGraphs] RPC failed: " + ex.getStatus());
        }
        return graphsRecveived;
    }

    /** Create new Graph */
    public NewGraph createGraph(GraphGrpc gr) {
        NewGraph response;
        try {
            response = blockingStub.createGraph(gr);
            if(response.getSuccess()){
                System.out.println("[createGraph] Successful operation ");
            }else{
                System.out.println("[createGraph] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[createGraph] RPC failed: " + e.getStatus());
            return NewGraph.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Delete a Graph */
    public boolean deleteGraph(long idGraph) {

        RequestID id = RequestID.newBuilder().setIdGraph(idGraph).build();
        Status response;
        try {
            response = blockingStub.deleteGraph(id);
            if(response.getSuccess()){
                System.out.println("[deleteGraph] Successful operation ");
                return true;
            }else{
                System.out.println("[deleteGraph] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[deleteGraph] RPC failed: " + e.getStatus());
        }
        return false;
    }

    /** Edits a single graph */
    public NewGraph updateGraph(long idGraph, GraphGrpc newGraph) {

        GraphGrpc gr = GraphGrpc.newBuilder(newGraph).setId(idGraph).build();
        NewGraph response;
        try {
            response = blockingStub.updateGraph(gr);
            if(response.getSuccess()){
                System.out.println("[updateGraph] Successful operation ");
            }else{
                System.out.println("[updateGraph] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[updateGraph] RPC failed: " + e.getStatus());
            return NewGraph.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Get a single graph*/
    public GraphGrpc getGraph(long idGraph) {

        RequestID request = RequestID.newBuilder().setIdGraph(idGraph).build() ;
        try {
            GraphGrpc graph = blockingStub.getGraph(request);
            if(!graph.getErrorMessage().equals("")){
                System.out.println("[getGraph] Error in operation: " + graph.getErrorMessage());
            }
            return graph;
        } catch (StatusRuntimeException ex) {
            System.err.println("[getGraph] RPC failed: " + ex.getStatus());
            return null;
        }
    }

    /** Verify a given policy*/
    public VerificationGrpc verify(Policy policy) {

        VerificationGrpc response;
        try {
            response = blockingStub.verifyPolicy(policy);
            if(!response.getErrorMessage().equals("")){
                System.out.println("[verify] Error in operation: " + response.getErrorMessage());
            }
            System.out.println("[verify] Result : "+response.getResult());
            System.out.println("[verify] Comment : "+response.getComment());
            //uncomment if you want to print the paths
            /*for(TestGrpc test:response.getTestList()){
            System.out.println("Test : "+test.getResult()+". Traversed nodes:");
            for(NodeGrpc node:test.getNodeList()){
            //prints only the name
            System.out.println("Node "+node.getName());
            }
            }*/
            return response;
        } catch (StatusRuntimeException e) {
            System.err.println("[verify] RPC failed: " + e.getStatus());
            return null;
        }
    }

    /*Node part*/

    /** Get array of nodes */
    public List<NodeGrpc> getNodes(long idGraph) {
        List<NodeGrpc> nodesReceived = new ArrayList<NodeGrpc>();
        RequestID request = RequestID.newBuilder().setIdGraph(idGraph).build();
        Iterator<NodeGrpc> nodes;
        try {
            nodes = blockingStub.getNodes(request);
            while (nodes.hasNext()) {
                NodeGrpc node = nodes.next();
                if(node.getErrorMessage().equals("")){
                    System.out.println("[getNodes] Node id : "+node.getId());
                    nodesReceived.add(node);
                }else{
                    System.out.println("[getNodes] Error : " + node.getErrorMessage());
                    return nodesReceived;
                }
            }
        } catch (StatusRuntimeException ex) {
            System.err.println("[getNodes] RPC failed: " + ex.getStatus());
        }
        return nodesReceived;
    }

    /** Create new Node */
    public NewNode createNode(NodeGrpc node, long idGraph) {

        NewNode response;
        try {
            NodeGrpc n = NodeGrpc.newBuilder(node).setIdGraph(idGraph).build();
            response = blockingStub.createNode(n);
            if(response.getSuccess()){
                System.out.println("[createNode] Successful operation ");
            }else{
                System.out.println("[createNode] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[createNode] RPC failed: " + e.getStatus());
            return NewNode.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Delete a Node */
    public boolean deleteNode(long idGraph, long idNode) {

        RequestID id = RequestID.newBuilder().setIdGraph(idGraph).setIdNode(idNode).build();
        Status response;
        try {
            response = blockingStub.deleteNode(id);
            if(response.getSuccess()){
                System.out.println("[deleteNode] Successful operation ");
                return true;
            }else{
                System.out.println("[deleteNode] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[deleteNode] RPC failed: " + e.getStatus());
        }
        return false;
    }

    /** Edits a single Node */
    public NewNode updateNode(long idGraph, long idNode, NodeGrpc node) {

        NodeGrpc nu = NodeGrpc.newBuilder(node).setIdGraph(idGraph).setId(idNode).build();
        NewNode response;
        try {
            response = blockingStub.updateNode(nu);
            if(response.getSuccess()){
                System.out.println("[updateNode] Successful operation ");
            }else{
                System.out.println("[updateNode] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[updateNode] RPC failed: " + e.getStatus());
            return NewNode.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Get a single Node*/
    public NodeGrpc getNode(long idGraph, long idNode) {

        RequestID request = RequestID.newBuilder().setIdGraph(idGraph).setIdNode(idNode).build() ;
        try {
            NodeGrpc node = blockingStub.getNode(request);
            if(!node.getErrorMessage().equals("")){
                System.out.println("[getNode] Error in operation: " + node.getErrorMessage());
            }
            return node;
        } catch (StatusRuntimeException ex) {
            System.err.println("[getNode] RPC failed: " + ex.getStatus());
            return null;
        }
    }

    /** Configure a single Node*/
    public boolean configureNode(long idGraph, long idNode, ConfigurationGrpc configuration) {

        try {
            ConfigurationGrpc request = ConfigurationGrpc.newBuilder(configuration)
                    .setIdGraph(idGraph).setIdNode(idNode).build() ;

            Status response = blockingStub.configureNode(request);
            if(response.getSuccess()){
                System.out.println("[configureNode] Successful operation ");
                return true;
            }else{
                System.out.println("[configureNode] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[configureNode] RPC failed: " + e.getStatus());
        }
        return false;
    }

    /*Neighbour part*/

    /** Get array of neighbours */
    public List<NeighbourGrpc> getNeighbours(long idGraph, long idNode) {

        List<NeighbourGrpc> neighboursReceived = new ArrayList<NeighbourGrpc>();
        RequestID request = RequestID.newBuilder().setIdGraph(idGraph).setIdNode(idNode).build() ;
        Iterator<NeighbourGrpc> neighbours;
        try {
            neighbours = blockingStub.getNeighbours(request);
            while (neighbours.hasNext()) {
                NeighbourGrpc neighbour = neighbours.next();
                if(neighbour.getErrorMessage().equals("")){
                    System.out.println("[getNeighbours] Neighbour id : "+neighbour.getId());
                    neighboursReceived.add(neighbour);
                }else{
                    System.out.println("[getNeighbours] Error : " + neighbour.getErrorMessage());
                    return neighboursReceived;
                }
            }
        } catch (StatusRuntimeException ex) {
            System.err.println("[getNeighbours] RPC failed: " + ex.getStatus());
        }
        return neighboursReceived;
    }

    /** Create new Neighbour */
    public NewNeighbour createNeighbour(NeighbourGrpc neighbour, long idGraph, long idNode) {

        NewNeighbour response;
        try {
            NeighbourGrpc n = NeighbourGrpc.newBuilder(neighbour).setIdGraph(idGraph)
                    .setIdNode(idNode).build();
            response = blockingStub.createNeighbour(n);
            if(response.getSuccess()){
                System.out.println("[createNeighbour] Successful operation ");
            }else{
                System.out.println("[createNeighbour] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[createNeighbour] RPC failed: " + e.getStatus());
            return NewNeighbour.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Delete a Neighbour */
    public boolean deleteNeighbour(long idGraph, long idNode, long idNeighbour) {

        RequestID id = RequestID.newBuilder().setIdGraph(idGraph).setIdNode(idNode).setIdNeighbour(idNeighbour).build();
        Status response;
        try {
            response = blockingStub.deleteNeighbour(id);
            if(response.getSuccess()){
                System.out.println("[deleteNeighbour] Successful operation ");
                return true;
            }else{
                System.out.println("[deleteNeighbour] Unsuccesful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[deleteNeighbour] RPC failed: " + e.getStatus());
        }
        return false;
    }

    /** Edits a single Neighbour */
    public NewNeighbour updateNeighbour(long idGraph, long idNode, long idNeighbour, NeighbourGrpc neighbour) {

        NeighbourGrpc nu = NeighbourGrpc.newBuilder(neighbour).setIdGraph(idGraph).setIdNode(idNode)
                .setId(idNeighbour).build();
        NewNeighbour response;
        try {
            response = blockingStub.updateNeighbour(nu);
            if(response.getSuccess()){
                System.out.println("[updateNeighbour] Successful operation ");
            }else{
                System.out.println("[updateNeighbour] Unsuccessful operation: " + response.getErrorMessage());
            }
        } catch (StatusRuntimeException e) {
            System.err.println("[updateNeighbour] RPC failed: " + e.getStatus());
            return NewNeighbour.newBuilder().setSuccess(false).build();
        }
        return response;
    }

    /** Get a single Neighbour*/
    public NeighbourGrpc getNeighbour(long idGraph, long idNode, long idNeighbour) {

        RequestID request = RequestID.newBuilder().setIdGraph(idGraph).setIdNode(idNode).setIdNeighbour(idNeighbour).build() ;
        try {
            NeighbourGrpc neighbour = blockingStub.getNeighbour(request);
            if(!neighbour.getErrorMessage().equals("")){
                System.out.println("[getNeighbour] Error in operation: " + neighbour.getErrorMessage());
            }
            return neighbour;
        } catch (StatusRuntimeException ex) {
            System.err.println("[getNeighbour] RPC failed: " + ex.getStatus());
            return null;
        }
    }

    public void shutdown() throws InterruptedException {
        channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
    }

    /** Test on the Server. */
    public static void main(String[] args) throws Exception {

        List<Long> listGraph = new ArrayList<Long>();//list of ID

        Client client = new Client("localhost" , 50051);
        try {
            NodeGrpc node1 = createNodeGrpc("Node1", "endhost", null, null);
            List<NeighbourGrpc> neighbours = new ArrayList<NeighbourGrpc>();
            NeighbourGrpc nb = createNeighbourGrpc("Node1");
            neighbours.add(nb);
            NodeGrpc node2 = createNodeGrpc("Node2", "endpoint", neighbours, null);
            List<NodeGrpc> nodes = new ArrayList<NodeGrpc>();
            nodes.add(node1);
            nodes.add(node2);

            GraphGrpc graph = createGraphGrpc(nodes);

            NewGraph createdGraph = client.createGraph(graph);
            if(createdGraph.getSuccess() == true){
                listGraph.add(createdGraph.getGraph().getId());
                System.out.println("Created graph with id :"+ createdGraph.getGraph().getId());
            }

            for(GraphGrpc g : client.getGraphs()){
                long graph_id = g.getId();
                System.out.println("graph id: "+graph_id);
                for (NodeGrpc n: g.getNodeList()){
                    long node_id = n.getId();
                    System.out.println("node id: "+node_id);
                    NodeGrpc nodenew = createNodeGrpc("Newnode","endhost",null,null);
                    //NewNode node = client.updateNode(graph_id, node_id, nodenew);
                    //System.out.println("Client updates node: "+node.getSuccess());
                    System.out.println("end");
                }
            }


        } catch(Exception ex){
            System.out.println("Error: " + ex.getMessage());
            ex.printStackTrace();
        }finally {
            client.shutdown();
        }
    }

    public static NeighbourGrpc createNeighbourGrpc(String name){
        return NeighbourGrpc.newBuilder().setName(name).build();
    }

    public static NodeGrpc createNodeGrpc(String name, String functionalType, List<NeighbourGrpc> neighbours, ConfigurationGrpc conf) throws Exception{
        NodeGrpc.Builder nb = NodeGrpc.newBuilder();

        if(name != null)
            nb.setName(name);
        else
            throw new Exception("Node must have a name");

        if(functionalType != null)
            nb.setFunctionalType(FunctionalType.valueOf(functionalType));
        else
            throw new Exception("Node must have a functional type");

        if( neighbours!= null){
            for(NeighbourGrpc value:neighbours)
                nb.addNeighbour(value);
        }
        if(conf == null){
            try{
                conf = createConfigurationGrpc(null, null, null, null);
            }catch(Exception e){
                throw new Exception(e.getMessage());
            }
        }
        nb.setConfiguration(conf);
        return nb.build();
    }

    public static GraphGrpc createGraphGrpc(List<NodeGrpc> nodes){
        GraphGrpc.Builder gb = GraphGrpc.newBuilder();
        if(nodes != null){
            for(NodeGrpc value:nodes)
                gb.addNode(value);
        }
        return gb.build();
    }

    public static Policy createPolicy(String src, String dst, String type, String middlebox, long idGraph) throws IllegalArgumentException{
        if(!validMiddlebox(type, middlebox))
            throw new IllegalArgumentException("Not valid middlebox valid with this type");
        Policy.Builder policy = Policy.newBuilder();
        policy.setIdGraph(idGraph);
        if(src != null)
            policy.setSource(src);
        else{
            throw new IllegalArgumentException("Please insert source field");
        }
        if(dst != null)
            policy.setDestination(dst);
        else{
            throw new IllegalArgumentException("Please insert destination field");
        }
        if(type != null)
            policy.setType(PolicyType.valueOf(type));
        if(middlebox != null)
            policy.setMiddlebox(middlebox);
        return policy.build();
    }

    public static ConfigurationGrpc createConfigurationGrpc(Map<String,String> parameters, List<String> lists, String id, String description) throws Exception{
        ConfigurationGrpc.Builder cb = ConfigurationGrpc.newBuilder();
        StringBuilder sb = new StringBuilder("[");
        if(parameters != null && lists == null){
            int i = 0;
            sb.append("{");
            for (String key: parameters.keySet()) {
                sb.append("\"");
                sb.append(key);
                sb.append("\":\"");
                sb.append(parameters.get(key));
                sb.append("\"");
                if((i+1)<parameters.keySet().size()){
                    sb.append(", ");
                }
                i++;
            }
            sb.append("}");
        }
        else if(parameters == null && lists != null){
            int i = 0;
            for (String value: lists) {
                sb.append("\"");
                sb.append(value);
                sb.append("\"");
                if((i+1)<lists.size()){
                    sb.append(", ");
                }
                i++;
            }
        }
        else if(parameters != null && lists != null){
            throw new Exception("Error, configuration must contains or a sequence name:value or sequence"
                    + "of string, but not both");
        }
        sb.append("]");
        cb.setConfiguration(sb.toString());
        if(id != null)
            cb.setId(id);
        if(description != null)
            cb.setDescription(description);
        return cb.build();
    }

    private static boolean validMiddlebox(String type, String middlebox) {
        if(type == null)
            return false;
        if(type.equals("reachability") && (middlebox == null || middlebox.equals("")))
            return true;
        if(type.equals("isolation") && !(middlebox == null || middlebox.equals("")))
            return true;
        if(type.equals("traversal") && !(middlebox == null || middlebox.equals("")))
            return true;
        return false;
    }
}
