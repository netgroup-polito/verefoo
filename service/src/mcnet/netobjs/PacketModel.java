/*
Copyright 2016 Politecnico di Torino
Authors:
Project Supervisor and Contact: Riccardo Sisto (riccardo.sisto@polito.it)

This file is part of Verigraph.

    Verigraph is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation, either version 3 of
    the License, or (at your option) any later version.

    Verigraph is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public
    License along with Verigraph.  If not, see
    <http://www.gnu.org/licenses/>. 
*/
package mcnet.netobjs;

import com.microsoft.z3.DatatypeExpr;

/*
 * Fields that can be configured -> "dest","body","seq","proto","emailFrom","url","options"
 */
public class PacketModel {
	
	private DatatypeExpr ip_dest;
	private Integer body;
	private Integer seq;
	private Integer proto;
	private Integer emailFrom;
	private Integer url;
	private Integer options;
	
	public DatatypeExpr getIp_dest() {
		return ip_dest;
	}
	public void setIp_dest(DatatypeExpr ip_dest) {
		this.ip_dest = ip_dest;
	}
	public Integer getBody() {
		return body;
	}
	public void setBody(Integer body) {
		this.body = body;
	}
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public Integer getProto() {
		return proto;
	}
	public void setProto(Integer proto) {
		this.proto = proto;
	}
	public Integer getEmailFrom() {
		return emailFrom;
	}
	public void setEmailFrom(Integer emailFrom) {
		this.emailFrom = emailFrom;
	}
	public Integer getUrl() {
		return url;
	}
	public void setUrl(Integer url) {
		this.url = url;
	}
	public Integer getOptions() {
		return options;
	}
	public void setOptions(Integer options) {
		this.options = options;
	}

}
