package it.polito.escape.verify.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Configuration {
	private String configuration;
	
	public Configuration(){
		
	}
	
	public Configuration(String configuration){
		this.configuration = configuration;
	}

	public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}
}
