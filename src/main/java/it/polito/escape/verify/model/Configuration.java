package it.polito.escape.verify.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.resources.CustomConfigurationSerializer;
import it.polito.escape.verify.resources.CustomMapSerializer;
import it.polito.escape.verify.resources.NodeCustomDeserializer;

@XmlRootElement
@ApiModel("Configuration")
@JsonSerialize(using = CustomConfigurationSerializer.class)
public class Configuration {
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private String						id;
	@ApiModelProperty(required = false)
	@XmlTransient
	private String						description			= "";
	@ApiModelProperty(required = false, example = "[\"node1\", \"node2\", \"node3\"]")
	private List<String>				configurationList	= new ArrayList<String>();
	@ApiModelProperty(required = false, example = "[{\"destination_node\":\"source_node\"}]")
	private List<ConfigurationObject>	configurationMap	= new ArrayList<ConfigurationObject>();

	public Configuration() {

	}

	public List<ConfigurationObject> getConfigurationMap() {
		return configurationMap;
	}

	public void setConfigurationMap(List<ConfigurationObject> configurationMap) {
		this.configurationMap = configurationMap;
	}

	public Configuration(	String id, String description, List<String> configurationList,
							List<ConfigurationObject> configurationMap) {
		this.id = id;
		this.description = description;
		this.configurationList = configurationList;
		this.configurationMap = configurationMap;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getConfigurationList() {
		return configurationList;
	}

	public void setConfigurationList(List<String> configurationList) {
		this.configurationList = configurationList;
	}

}
