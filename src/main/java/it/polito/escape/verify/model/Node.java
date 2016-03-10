package it.polito.escape.verify.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlTransient;

import it.polito.escape.verify.model.Link;

public class Node {
	private long id;
	private String name;
	private String functional_type;

	private Map<Long, Neighbour> neighbours = new HashMap<>();
	private List<Link> links = new ArrayList<>();
	
	public Node(){

	}
	
	public Node(long id, String name, String functional_type) {
		this.id = id;
		this.name = name;
		this.functional_type = functional_type;
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
	public Map<Long, Neighbour> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(Map<Long, Neighbour> neighbours) {
		this.neighbours = neighbours;
	}

	public long getId() {
		return this.id;
	}
	
	public void setId(long id){
		this.id = id;
	}
	
	public List<Link> getLinks() {
		return links;
	}

	public void setLinks(List<Link> links) {
		this.links = links;
	}
	
	public void addLink(String url, String rel) {
		Link link = new Link();
		link.setLink(url);
		link.setRel(rel);
		links.add(link);
	}
	
	
}
