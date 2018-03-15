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
import it.polito.verigraph.deserializer.NodeCustomDeserializer;
import it.polito.verigraph.serializer.CustomMapSerializer;

@ApiModel(value = "Node")
@XmlRootElement
@JsonDeserialize(using = NodeCustomDeserializer.class)
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Node {

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private long id;

    @ApiModelProperty(required = true, example = "ep", value = "The name of the node can be any string")
    private String name;

    @ApiModelProperty(required = true,
            example = "endpoint",
            value = "The functional types that are currently supported are: endpoint, firewall, nat, antispam, webclient, webserver, mailclient, mailserver")
    private String functional_type;

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private Configuration configuration= new Configuration();

    @ApiModelProperty(name = "neighbours",
            notes = "Neighbours",
            dataType = "List[it.polito.verigraph.model.Neighbour]")
    private Map<Long, Neighbour>neighbours= new HashMap<Long, Neighbour>();

    @ApiModelProperty(required = false, hidden = true)
    @XmlTransient
    private Set<Link>links= new HashSet<>();

    public Node() {

    }

    public Node(long id, String name, String functional_type, Configuration configuration) {
        this.id = id;
        this.name = name;
        this.functional_type = functional_type;
        this.configuration = configuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFunctional_type() {
        return functional_type;
    }

    public void setFunctional_type(String functional_type) {
        this.functional_type = functional_type;
    }

    // @XmlTransient
    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    @JsonSerialize(using = CustomMapSerializer.class)
    public Map<Long, Neighbour> getNeighbours() {
        return neighbours;
    }

    public void setNeighbours(Map<Long, Neighbour> neighbours) {
        this.neighbours = neighbours;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        else
            return false;
    }

    public Neighbour searchNeighbourByName(String name) {
        for (Neighbour neighbour : this.neighbours.values()) {
            if (neighbour.getName().equals(name))
                return neighbour;
        }
        return null;
    }

}