package it.polito.verefoo.graph;

import java.util.HashMap;
import java.util.Map;

import it.polito.verefoo.jaxb.*;

public class Flow {

	SecurityRequirement requirement;
	Traffic originalTraffic;
	int idFlow;
	FlowPath path;
	Map<String, Traffic> functionTrafficMap;
	

	
	public Flow(SecurityRequirement requirement, FlowPath path, int idFlow) {
		this.requirement = requirement;
		this.path = path;
		this.idFlow = idFlow;
		this.requirement.getOriginalProperty().setSrcPort((this.requirement.getOriginalProperty() == null || this.requirement.getOriginalProperty().getSrcPort() == null) ? "null":this.requirement.getOriginalProperty().getSrcPort());
		this.requirement.getOriginalProperty().setDstPort((this.requirement.getOriginalProperty() == null || this.requirement.getOriginalProperty().getDstPort() == null) ? "null":this.requirement.getOriginalProperty().getDstPort());
		this.requirement.getOriginalProperty().setLv4Proto((this.requirement.getOriginalProperty() == null || this.requirement.getOriginalProperty().getLv4Proto() == null) ? L4ProtocolTypes.ANY:this.requirement.getOriginalProperty().getLv4Proto());
		this.functionTrafficMap = new HashMap<String, Traffic>();
		Property originalProperty = this.requirement.getOriginalProperty();
		this.originalTraffic = new Traffic(originalProperty.getName(), originalProperty.getSrc(), originalProperty.getDst());
		this.originalTraffic.setpSrc(originalProperty.getSrcPort());
		this.originalTraffic.setpDst(originalProperty.getDstPort());
		this.originalTraffic.settProto(originalProperty.getLv4Proto());
		
		if(originalProperty.getHTTPDefinition() != null) {
			HTTPDefinition webPart = originalProperty.getHTTPDefinition();
			if(webPart.getUrl() != null) this.originalTraffic.setUrl(webPart.getUrl());
			if(webPart.getDomain() != null) this.originalTraffic.setDomain(webPart.getDomain());
		}
		
		if(originalProperty.getBody() != null) this.originalTraffic.setBody(originalProperty.getBody());
	}


	public SecurityRequirement getRequirement() {
		return requirement;
	}

	public void setRequirement(SecurityRequirement requirement) {
		this.requirement = requirement;
	}

	public Traffic getOriginalTraffic() {
		return originalTraffic;
	}

	public void setOriginalTraffic(Traffic originalTraffic) {
		this.originalTraffic = originalTraffic;
	}

	public int getIdFlow() {
		return idFlow;
	}

	public void setIdFlow(int idFlow) {
		this.idFlow = idFlow;
	}

	public FlowPath getPath() {
		return path;
	}

	public void setPath(FlowPath path) {
		this.path = path;
	}

	public Map<String, Traffic> getFunctionTrafficMap() {
		return functionTrafficMap;
	}

	public void setFunctionTrafficMap(Map<String, Traffic> functionTrafficMap) {
		this.functionTrafficMap = functionTrafficMap;
	}


	static public Property copyProperty(Property original) {
		Property copy = new Property();
		copy.setName(original.getName());
		copy.setGraph(original.getGraph());
		copy.setSrc(original.getSrc());
		copy.setDst(original.getDst());
		copy.setSrcPort(original.getSrcPort());
		copy.setDstPort(original.getDstPort());
		copy.setBody(original.getBody());
		return copy;
	}


	public void addModifiedTraffic(String name, Traffic t) {
		functionTrafficMap.put(name, t);

	}
	
	public Traffic getCrossedTraffic(String name) {
		if(functionTrafficMap.containsKey(name)) 
			return functionTrafficMap.get(name);
		else
			return originalTraffic;
	}
	
	
	
	
}
