package it.polito.escape.verify.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//import org.eclipse.persistence.oxm.annotations.XmlPath;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.model.Link;
import it.polito.escape.verify.resources.GraphMapAdapter;
import it.polito.escape.verify.resources.NodeMapAdapter;

@ApiModel(value = "Node")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Node {
	@ApiModelProperty(required = false, hidden = true)
	@XmlTransient
	private long id;
	private String name;
	private String functional_type;
	
	//@ApiModelProperty(name ="neighbours", notes = "Neighbours", dataType = "Map[java.lang.Long,it.polito.escape.verify.model.Neighbour]")
//	@XmlPath(".")
//    @XmlJavaTypeAdapter(NodeMapAdapter.class)
	@ApiModelProperty(name ="neighbours", notes = "Neighbours", dataType = "List[it.polito.escape.verify.model.Neighbour]")
	private Map<Long, Neighbour> neighbours = new HashMap<Long,Neighbour>();
	@ApiModelProperty(required = false, hidden = true)
	private Set<Link> links = new HashSet<>();
	
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
	//@XmlTransient
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
	
	
}
