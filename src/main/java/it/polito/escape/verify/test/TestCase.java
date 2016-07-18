package it.polito.escape.verify.test;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import it.polito.escape.verify.model.Graph;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "id", "name", "description", "policy_url_parameters", "result", "graph" })
public class TestCase {

	@JsonProperty("id")
	private Integer				id;

	@JsonProperty("name")
	private String				name;

	@JsonProperty("description")
	private String				description;

	@JsonProperty("policy_url_parameters")
	private String				policyUrlParameters;

	@JsonProperty("result")
	private String				result;

	@JsonProperty("graph")
	private Graph				graph;

	@JsonIgnore
	private Map<String, Object>	additionalProperties	= new HashMap<String, Object>();

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
	public String getPolicyUrlParameters() {
		return policyUrlParameters;
	}

	/**
	 * 
	 * @param policyUrlParameters
	 *            The policy_url_parameters
	 */
	@JsonProperty("policy_url_parameters")
	public void setPolicyUrlParameters(String policyUrlParameters) {
		this.policyUrlParameters = policyUrlParameters;
	}

	/**
	 * 
	 * @return The result
	 */
	@JsonProperty("result")
	public String getResult() {
		return result;
	}

	/**
	 * 
	 * @param result
	 *            The result
	 */
	@JsonProperty("result")
	public void setResult(String result) {
		this.result = result;
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
