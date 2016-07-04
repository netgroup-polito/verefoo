package it.polito.escape.verify.model;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.deserializer.ConfigurationCustomDeserializer;
import it.polito.escape.verify.serializer.CustomConfigurationSerializer;

@XmlRootElement
@ApiModel("Configuration")
@JsonSerialize(using = CustomConfigurationSerializer.class)
@JsonDeserialize(using = ConfigurationCustomDeserializer.class)
public class Configuration {

	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private String		id;

	@ApiModelProperty(required = false)
	@XmlTransient
	private String		description	= "";

	@ApiModelProperty(required = true)
	private JsonNode	configuration;

	public Configuration() {

	}

	public Configuration(String id, String description, JsonNode configuration) {
		this.id = id;
		this.description = description;
		this.configuration = configuration;
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

	public JsonNode getConfiguration() {
		return configuration;
	}

	public void setConfiguration(JsonNode configuration) {
		this.configuration = configuration;
	}

}
