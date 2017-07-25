/*******************************************************************************
 * Copyright (c) 2017 Politecnico di Torino and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *******************************************************************************/
package it.polito.verigraph.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.polito.verigraph.model.Graph;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "description", "policy_url_parameters", "result", "graph" })
public class TestCase {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("description")
    private String description;

    @JsonProperty("policy_url_parameters")
    private ArrayList<String> policyUrlParameters;

    @JsonProperty("results")
    private ArrayList<String> results;

    @JsonProperty("graph")
    private Graph graph;

    @JsonIgnore
    private Map<String, Object>additionalProperties= new HashMap<String, Object>();

    /**
     *
     * @return The id
     */
    @JsonProperty("id")
    public Integer getId() {
        return id;
    }

    /**
     *
     * @param id
     *            The id
     */
    @JsonProperty("id")
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     *
     * @return The name
     */
    @JsonProperty("name")
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     *            The name
     */
    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return The description
     */
    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     *            The description
     */
    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return The policyUrlParameters
     */
    @JsonProperty("policy_url_parameters")
    public List<String> getPolicyUrlParameters() {
        return policyUrlParameters;
    }

    /**
     *
     * @param policyUrlParameters
     *            The policy_url_parameters
     */
    @JsonProperty("policy_url_parameters")
    public void setPolicyUrlParameters(List<String> policyUrlParameters) {
        if (this.policyUrlParameters == null)
            this.policyUrlParameters = new ArrayList<String>();

        this.policyUrlParameters.addAll(policyUrlParameters);
    }

    /**
     *
     * @return The result
     */
    @JsonProperty("results")
    public List<String> getResults() {
        return results;
    }

    /**
     *
     * @param result
     *            The result
     */
    @JsonProperty("results")
    public void setResults(List<String> results) {
        if (this.results == null)
            this.results = new ArrayList<String>();
        this.results.addAll(results);
    }

    /**
     *
     * @return The graph
     */
    @JsonProperty("graph")
    public Graph getGraph() {
        return graph;
    }

    /**
     *
     * @param graph
     *            The graph
     */
    @JsonProperty("graph")
    public void setGraph(Graph graph) {
        this.graph = graph;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }
}