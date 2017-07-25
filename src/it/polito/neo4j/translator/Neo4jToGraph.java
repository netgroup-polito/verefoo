/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.neo4j.translator;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import it.polito.neo4j.jaxb.Elements;
import it.polito.verigraph.model.Configuration;
import it.polito.verigraph.model.Node;

public class Neo4jToGraph {
    public static it.polito.verigraph.model.Graph generateGraph(it.polito.neo4j.jaxb.Graph g) throws JsonProcessingException{

        it.polito.verigraph.model.Graph graph=new it.polito.verigraph.model.Graph();
        graph.setId(g.getId());
        Map<Long, it.polito.verigraph.model.Node> nodes=new HashMap<Long, it.polito.verigraph.model.Node>();
        for(it.polito.neo4j.jaxb.Node n : g.getNode()){
            it.polito.verigraph.model.Node node=new it.polito.verigraph.model.Node();
            node.setId(n.getId());
            node.setName(n.getName());
            node.setFunctional_type(n.getFunctionalType().toString().toLowerCase());
            Map<Long, it.polito.verigraph.model.Neighbour> neighbours=new HashMap<Long, it.polito.verigraph.model.Neighbour>();
            List<it.polito.neo4j.jaxb.Neighbour> list=n.getNeighbour();
            for(it.polito.neo4j.jaxb.Neighbour neigh : list){
                it.polito.verigraph.model.Neighbour ngraph=new it.polito.verigraph.model.Neighbour();
                ngraph.setId(neigh.getId());
                ngraph.setName(neigh.getName());
                neighbours.put(ngraph.getId(), ngraph);
            }
            node.setNeighbours(neighbours);

            it.polito.verigraph.model.Configuration conf=new it.polito.verigraph.model.Configuration();
            //setConfiguration(n.getConfiguration(), conf);
            node.setConfiguration(setConfiguration(n.getConfiguration(), conf));
            nodes.put(node.getId(), node);
        }
        graph.setNodes(nodes);
        return graph;
    }

    public static Map<Long, it.polito.verigraph.model.Node> NodesToVerigraph(Set<it.polito.neo4j.jaxb.Node> set) throws JsonProcessingException{

        Map<Long, it.polito.verigraph.model.Node> nodes=new HashMap<Long, it.polito.verigraph.model.Node>();
        for(it.polito.neo4j.jaxb.Node n : set){
            it.polito.verigraph.model.Node node=new it.polito.verigraph.model.Node();
            node.setId(n.getId());
            node.setName(n.getName());
            node.setFunctional_type(n.getFunctionalType().toString().toLowerCase());
            Map<Long, it.polito.verigraph.model.Neighbour> neighbours=new HashMap<Long, it.polito.verigraph.model.Neighbour>();
            for(it.polito.neo4j.jaxb.Neighbour neigh : n.getNeighbour()){
                it.polito.verigraph.model.Neighbour ngraph=new it.polito.verigraph.model.Neighbour();
                ngraph.setId(neigh.getId());
                ngraph.setName(neigh.getName());
                neighbours.put(ngraph.getId(), ngraph);
            }
            node.setNeighbours(neighbours);
            it.polito.verigraph.model.Configuration conf=new it.polito.verigraph.model.Configuration();
            //setConfiguration(n.getConfiguration(), conf);
            node.setConfiguration(setConfiguration(n.getConfiguration(), conf));
            nodes.put(node.getId(), node);
        }

        return nodes;
    }

    private static Configuration setConfiguration(it.polito.neo4j.jaxb.Configuration configuration, Configuration conf) throws JsonProcessingException {

        conf.setId(configuration.getId().toString());
        if(configuration.getDescription()!=null)
            conf.setDescription(configuration.getDescription());
        switch(configuration.getName().toUpperCase()){
        case "FIREWALL":{
            ObjectMapper mapper=new ObjectMapper();
            JsonNode root=mapper.createObjectNode();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getFirewall().getElements()!=null){
                for(Elements e : configuration.getFirewall().getElements()){
                    JsonNode element=mapper.createObjectNode();
                    ((ObjectNode)element).put(e.getDestination(), e.getSource());
                    child.add(element);
                }
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "ANTISPAM":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getAntispam().getSource()!=null){
                for(String e : configuration.getAntispam().getSource()){
                    child.add(e);
                }
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "CACHE":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getCache().getResource()!=null){
                for(String e : configuration.getCache().getResource()){
                    child.add(e);
                }
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }

        case "DPI":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();

            if(configuration.getDpi().getNotAllowed()!=null){
                for(String e : configuration.getDpi().getNotAllowed()){
                    child.add(e);
                }
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }

        case "ENDHOST":{

            ObjectMapper mapper=new ObjectMapper();
            JsonNode root=mapper.createObjectNode();
            ArrayNode child=mapper.createArrayNode();

            Map<String, String> map=new HashMap<String, String>();

            if(configuration.getEndhost().getBody()!=null)
                map.put("body", configuration.getEndhost().getBody());

            if(configuration.getEndhost().getDestination()!=null)
                map.put("destination", configuration.getEndhost().getDestination());

            if(configuration.getEndhost().getEmailFrom()!=null)
                map.put("email_from", configuration.getEndhost().getEmailFrom());

            if(configuration.getEndhost().getOptions()!=null)
                map.put("options", configuration.getEndhost().getOptions());

            if(configuration.getEndhost().getUrl()!=null)
                map.put("url", configuration.getEndhost().getUrl());

            if(configuration.getEndhost().getProtocol()!=null)
                map.put("protocol", configuration.getEndhost().getProtocol().toString());


            if((configuration.getEndhost().getSequence()) != null && !(configuration.getEndhost().getSequence()).equals(BigInteger.ZERO))
                map.put("sequence", new String(configuration.getEndhost().getSequence().toByteArray()));


            JsonNode element=mapper.createObjectNode();
            if(!map.isEmpty()){
                for(Map.Entry<String, String> s : map.entrySet()){
                    if((s.getKey()).compareTo("sequence")==0)
                        ((ObjectNode)element).put(s.getKey(), s.getValue());
                    if(!(s.getValue().compareTo("")==0))
                        ((ObjectNode)element).put(s.getKey(), s.getValue());

                }
                child.add(element);
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "ENDPOINT":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();

            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }

        case "FIELDMODIFIER":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            conf.setConfiguration(child);
            break;
        }
        case "MAILCLIENT":{
            Map<String, String> map=new HashMap<String, String>();
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getMailclient().getMailserver()!=null){
                map.put("mailserver", configuration.getMailclient().getMailserver());
                JsonNode element=mapper.createObjectNode();
                for(Map.Entry<String, String> s : map.entrySet()){
                    ((ObjectNode)element).put(s.getKey(), s.getValue());
                }
                child.add(element);
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "MAILSERVER":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            conf.setConfiguration(child);
            break;
        }
        case "NAT":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getNat().getSource()!=null){
                for(String e : configuration.getNat().getSource()){
                    child.add(e);
                }
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "VPNACCESS":{
            Map<String, String> map=new HashMap<String, String>();
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getVpnaccess().getVpnexit()!=null){
                map.put("vpnexit", configuration.getVpnaccess().getVpnexit());
                JsonNode element=mapper.createObjectNode();
                for(Map.Entry<String, String> s : map.entrySet()){
                    ((ObjectNode)element).put(s.getKey(), s.getValue());
                }
                child.add(element);
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "VPNEXIT":{
            Map<String, String> map=new HashMap<String, String>();
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getVpnexit().getVpnaccess()!=null){
                map.put("vpnaccess", configuration.getVpnexit().getVpnaccess());
                JsonNode element=mapper.createObjectNode();
                for(Map.Entry<String, String> s : map.entrySet()){
                    ((ObjectNode)element).put(s.getKey(), s.getValue());
                }
                child.add(element);
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "WEBCLIENT":{
            Map<String, String> map=new HashMap<String, String>();
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            if(configuration.getWebclient().getNameWebServer()!=null){
                map.put("webserver", configuration.getWebclient().getNameWebServer());
                JsonNode element=mapper.createObjectNode();
                for(Map.Entry<String, String> s : map.entrySet()){
                    ((ObjectNode)element).put(s.getKey(), s.getValue());
                }
                child.add(element);
            }
            //((ObjectNode)root).set("configuration", child);
            //conf.setConfiguration(root);
            conf.setConfiguration(child);
            break;
        }
        case "WEBSERVER":{
            ObjectMapper mapper=new ObjectMapper();
            ArrayNode child=mapper.createArrayNode();
            conf.setConfiguration(child);
            break;
        }
        }
        return conf;

    }

    public static it.polito.verigraph.model.Node NodeToVerigraph(it.polito.neo4j.jaxb.Node n) throws JsonProcessingException {
        it.polito.verigraph.model.Node node=new it.polito.verigraph.model.Node();
        node.setId(n.getId());
        node.setName(n.getName());
        node.setFunctional_type(n.getFunctionalType().toString().toLowerCase());
        Map<Long, it.polito.verigraph.model.Neighbour> neighbours=new HashMap<Long, it.polito.verigraph.model.Neighbour>();
        for(it.polito.neo4j.jaxb.Neighbour neigh : n.getNeighbour()){
            it.polito.verigraph.model.Neighbour ngraph=new it.polito.verigraph.model.Neighbour();
            ngraph.setId(neigh.getId());
            ngraph.setName(neigh.getName());
            neighbours.put(ngraph.getId(), ngraph);

        }
        node.setNeighbours(neighbours);
        it.polito.verigraph.model.Configuration conf=new it.polito.verigraph.model.Configuration();
        //setConfiguration(n.getConfiguration(), conf);
        node.setConfiguration(setConfiguration(n.getConfiguration(), conf));
        return node;
    }

    public static it.polito.verigraph.model.Neighbour NeighbourToVerigraph(it.polito.neo4j.jaxb.Neighbour n) throws JsonProcessingException {
        it.polito.verigraph.model.Neighbour ngraph=new it.polito.verigraph.model.Neighbour();
        ngraph.setId(n.getId());
        ngraph.setName(n.getName());
        return ngraph;
    }
    public static Map<Long, it.polito.verigraph.model.Neighbour> NeighboursToVerigraph(Set<it.polito.neo4j.jaxb.Neighbour> set) throws JsonProcessingException{

        Map<Long, it.polito.verigraph.model.Neighbour> neighbours=new HashMap<Long, it.polito.verigraph.model.Neighbour>();
        for(it.polito.neo4j.jaxb.Neighbour n : set){
            it.polito.verigraph.model.Neighbour ngraph=new it.polito.verigraph.model.Neighbour();
            ngraph.setId(n.getId());
            ngraph.setName(n.getName());
            neighbours.put(n.getId(), ngraph);
        }
        return neighbours;
    }

    public static it.polito.verigraph.model.Configuration ConfToVerigraph(it.polito.neo4j.jaxb.Configuration c) throws JsonProcessingException {
        it.polito.verigraph.model.Configuration conf=new it.polito.verigraph.model.Configuration();
        //setConfiguration(n.getConfiguration(), conf);
        setConfiguration(c, conf);
        return conf;
    }
}