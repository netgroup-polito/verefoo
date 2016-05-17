package it.polito.escape.verify.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

//import org.eclipse.persistence.oxm.annotations.XmlPath;
//import org.eclipse.persistence.oxm.annotations.XmlVariableNode;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import it.polito.escape.verify.resources.GraphMapAdapter;

@ApiModel(value = "Graph")
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Graph {
	@ApiModelProperty(required = false, hidden = true)
	private long id;
	
//	@XmlPath(".")
//    @XmlJavaTypeAdapter(GraphMapAdapter.class)
	
	
//	@XmlElementWrapper
//    @XmlVariableNode("id")
	//@ApiModelProperty(name ="nodes", notes = "Nodes", dataType = "Map[java.lang.Long,it.polito.escape.verify.model.Node]")
	@ApiModelProperty(name ="nodes", notes = "Nodes", dataType = "List[it.polito.escape.verify.model.Node]")
	private Map<Long, Node> nodes = new HashMap<Long,Node>();
	
	@ApiModelProperty(required = false, hidden = true)
	private Set<Link> links = new HashSet<Link>();
	
	public Graph(){
		
	}
	
	public Graph(long id){
		this.id = id;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	//@XmlTransient
	public Map<Long, Node> getNodes() {
		return nodes;
	}

	public void setNodes(Map<Long, Node> nodes) {
		this.nodes = nodes;
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
