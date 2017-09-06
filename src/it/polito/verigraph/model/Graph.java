/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.verigraph.deserializer.GraphCustomDeserializer;
import it.polito.verigraph.serializer.CustomMapSerializer;

@ApiModel(value = "Graph")
@XmlRootElement
@JsonDeserialize(using = GraphCustomDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Graph {

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private long id;

    @ApiModelProperty(name = "nodes", notes = "Nodes", dataType = "List[it.polito.verigraph.model.Node]")
    private Map<Long, Node>nodes= new HashMap<Long, Node>();

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private Set<Link>links= new HashSet<Link>();

    public Graph() {

    }

    public Graph(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @JsonSerialize(using = CustomMapSerializer.class)
    public Map<Long, Node> getNodes() {
        return nodes;
    }

    public void setNodes(Map<Long, Node> nodes) {
        this.nodes = nodes;
    }

    @XmlTransient
    public Set<Link> getLinks() {
        return links;
    }

    public void setLinks(Set<Link> links) {
        this.links = links;
    }

    public void addLink(String url, String rel) {
        Link link = new Link();
        link.setLink(url);
        link.setRel(rel);
        links.add(link);
    }

    public Node searchNodeByName(String name) {
        for (Node node : this.nodes.values()) {
            if (node.getName().equals(name))
                return node;
        }
        return null;
    }

    public int nodesWithName(String name) {
        int occurrences = 0;
        for (Node node : this.nodes.values()) {
            if (node.getName().equals(name))
                occurrences++;

        }
        return occurrences;
    }

}