package it.polito.escape.verify.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.model.Link;
import it.polito.escape.verify.resources.CustomMapSerializer;
import it.polito.escape.verify.resources.NodeCustomDeserializer;

@ApiModel(value = "Node")
@XmlRootElement
@JsonDeserialize(using = NodeCustomDeserializer.class)
public class Node {
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private long id;
	@ApiModelProperty(required=true, example="ep", value="The name of the node can be any string")
	private String name;
	@ApiModelProperty(required=true, example="endpoint", value="The functional types that are currently supported are: endpoint, firewall, nat, antispam, webclient, webserver, mailclient, mailserver")
	private String functional_type;
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private String configuration;
	
	@ApiModelProperty(name ="neighbours", notes = "Neighbours", dataType = "List[it.polito.escape.verify.model.Neighbour]")
	private Map<Long, Neighbour> neighbours = new HashMap<Long,Neighbour>();
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private Set<Link> links = new HashSet<>();
	
	public Node(){

	}
	
	public Node(long id, String name, String functional_type, String configuration) {
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
	
	@XmlTransient
	public String getConfiguration(){
		return configuration;
	}
	
	public void setConfiguration(String configuration){
		this.configuration = configuration;
	}
	
	@JsonSerialize(using = CustomMapSerializer.class)
	public Map<Long,Neighbour> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Map<Long,Neighbour> neighbours) {
		this.neighbours = neighbours;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id){
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

	public int neighboursWithName(String name) {
		int occurrences = 0;
		for (Neighbour neighbour : this.neighbours.values()){
			if (neighbour.getName().equals(name))
				occurrences++;

		}
		return occurrences;
	}

	
	
}
