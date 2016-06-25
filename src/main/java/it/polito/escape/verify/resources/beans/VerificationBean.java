package it.polito.escape.verify.resources.beans;

import javax.ws.rs.QueryParam;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("Verification")
public class VerificationBean {

	@ApiModelProperty(example = "webclient", value = "Source node. Must refer to an existing node of the same graph")
	private @QueryParam("source") String		source;

	@ApiModelProperty(	example = "webserver",
						value = "Destination node. Must refer to an existing node of the same graph")
	private @QueryParam("destination") String	destination;

	@ApiModelProperty(	example = "reachability",
						value = "Verification policy ('reachability', 'isolation', 'traversal')")
	private @QueryParam("type") String			type;

	@ApiModelProperty(	example = "firewall",
						value = "Absent if verification type is 'reachability', equal to the name of a middlebox to be avoided if verification type is 'isolation', equal to the name of a middlebox to be traversed if verification type is 'traversal'")
	private @QueryParam("middlebox") String		middlebox;

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMiddlebox() {
		return middlebox;
	}

	public void setMiddlebox(String middlebox) {
		this.middlebox = middlebox;
	}

}
