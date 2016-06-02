package it.polito.escape.verify.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.polito.escape.verify.resources.ConfigurationCustomDeserializer;
import it.polito.escape.verify.resources.CustomMapSerializer;

@JsonDeserialize(using = ConfigurationCustomDeserializer.class)
public class ConfigurationObject{
	private Map<String, String> map = new HashMap<String, String>();
	public ConfigurationObject(){
		
	}
	//@JsonSerialize(using = CustomMapSerializer.class)
	public Map<String, String> getMap() {
		return map;
	}
	public void setMap(Map<String, String> map) {
		this.map = map;
	}
}