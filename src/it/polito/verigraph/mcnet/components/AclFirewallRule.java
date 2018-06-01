package it.polito.verigraph.mcnet.components;

import com.microsoft.z3.ArithExpr;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;

public class AclFirewallRule {
	private BoolExpr action;
	private DatatypeExpr source;
	private DatatypeExpr destination;
	private IntNum start_src_port;
	private IntNum end_src_port;
	private IntNum start_dst_port;
	private IntNum end_dst_port;
	private IntNum protocol;
	private boolean directional;
	private Context ctx;
	private NetContext nctx;
	public AclFirewallRule(NetContext nctx, Context ctx, boolean action, DatatypeExpr source, DatatypeExpr destination, String string_src_port,
			String string_dst_port, int protocol, boolean directional) {
		this.nctx = nctx;
		this.ctx = ctx;
		this.action = action? ctx.mkTrue():ctx.mkFalse();
		this.source = source;
		this.destination = destination;
		int start_src_port, end_src_port;
		if(string_src_port.indexOf("-") != -1){
			int i_separator = string_src_port.indexOf("-");
			start_src_port = Integer.parseInt(string_src_port.substring(0, i_separator));
			end_src_port = Integer.parseInt(string_src_port.substring(i_separator+1));
		}else{
			if(string_src_port.equals("*")){
				start_src_port = 0;
				end_src_port = nctx.MAX_PORT;
			}else{
				start_src_port = Integer.parseInt(string_src_port);
				end_src_port = Integer.parseInt(string_src_port);
			}
		}
		int start_dst_port, end_dst_port;
		if(string_dst_port.indexOf("-") != -1){
			int i_separator = string_dst_port.indexOf("-");
			start_dst_port = Integer.parseInt(string_dst_port.substring(0, i_separator));
			end_dst_port = Integer.parseInt(string_dst_port.substring(i_separator+1));
		}else{
			if(string_src_port.equals("*")){
				start_dst_port = 0;
				end_dst_port = nctx.MAX_PORT;
			}else{
				start_dst_port = Integer.parseInt(string_dst_port);
				end_dst_port = Integer.parseInt(string_dst_port);
			}
		}
		this.start_src_port = ctx.mkInt(start_src_port);
		this.end_src_port = ctx.mkInt(end_src_port);
		this.start_dst_port = ctx.mkInt(start_dst_port);
		this.end_dst_port = ctx.mkInt(end_dst_port);
		this.protocol = ctx.mkInt(protocol);
		this.directional = directional;
	}
	public AclFirewallRule(NetContext nctx, Context ctx, boolean action, String source, String destination,
			String string_src_port, String string_dst_port, int protocol, boolean directional) {
		DatatypeExpr sourceExpr = nctx.createIpAddress(source);
		DatatypeExpr destinationExpr = nctx.createIpAddress(destination);
		this.nctx = nctx;
		this.ctx = ctx;
		this.action = action? ctx.mkTrue():ctx.mkFalse();
		this.source = sourceExpr;
		this.destination = destinationExpr;
		PortInterval pS = new PortInterval(string_src_port);
		PortInterval pD = new PortInterval(string_dst_port);
		this.start_src_port = ctx.mkInt(pS.getStart());
		this.end_src_port = ctx.mkInt(pS.getEnd());
		this.start_dst_port = ctx.mkInt(pD.getStart());
		this.end_dst_port = ctx.mkInt(pD.getEnd());
		this.protocol = ctx.mkInt(protocol);
		this.directional = directional;
	}
	/**
	 * @return the action
	 */
	public BoolExpr getAction() {
		return action;
	}
	/**
	 * @param action the action to set
	 */
	public void setAction(BoolExpr action) {
		this.action = action;
	}
	/**
	 * @return the source
	 */
	public DatatypeExpr getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(DatatypeExpr source) {
		this.source = source;
	}
	/**
	 * @return the destination
	 */
	public DatatypeExpr getDestination() {
		return destination;
	}
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(DatatypeExpr destination) {
		this.destination = destination;
	}
	/**
	 * @return the start_src_port
	 */
	public IntNum getStart_src_port() {
		return start_src_port;
	}
	/**
	 * @param start_src_port the start_src_port to set
	 */
	public void setStart_src_port(IntNum start_src_port) {
		this.start_src_port = start_src_port;
	}
	/**
	 * @return the end_src_port
	 */
	public IntNum getEnd_src_port() {
		return end_src_port;
	}
	/**
	 * @param end_src_port the end_src_port to set
	 */
	public void setEnd_src_port(IntNum end_src_port) {
		this.end_src_port = end_src_port;
	}
	/**
	 * @return the start_dst_port
	 */
	public IntNum getStart_dst_port() {
		return start_dst_port;
	}
	/**
	 * @param start_dst_port the start_dst_port to set
	 */
	public void setStart_dst_port(IntNum start_dst_port) {
		this.start_dst_port = start_dst_port;
	}
	/**
	 * @return the end_dst_port
	 */
	public IntNum getEnd_dst_port() {
		return end_dst_port;
	}
	/**
	 * @param end_dst_port the end_dst_port to set
	 */
	public void setEnd_dst_port(IntNum end_dst_port) {
		this.end_dst_port = end_dst_port;
	}
	/**
	 * @return the protocol
	 */
	public IntNum getProtocol() {
		return protocol;
	}
	/**
	 * @param protocol the protocol to set
	 */
	public void setProtocol(IntNum protocol) {
		this.protocol = protocol;
	}
	/**
	 * @return the directional
	 */
	public boolean isDirectional() {
		return directional;
	}
	/**
	 * @param directional the directional to set
	 */
	public void setDirectional(boolean directional) {
		this.directional = directional;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "AclFirewallRule [action=" + action + ", source=" + source + ", destination=" + destination
				+ ", start_src_port=" + start_src_port + ", end_src_port=" + end_src_port + ", start_dst_port="
				+ start_dst_port + ", end_dst_port=" + end_dst_port + ", protocol=" + protocol + ", directional="
				+ directional + "]";
	}
	
	public BoolExpr matchPacket(Expr p0){
		BoolExpr ipEqual;
		if(directional){
			ipEqual = ctx.mkAnd(nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p0),this.source),
								nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p0),this.destination));
								//ctx.mkEq(nctx.pf.get("src").apply(p0),this.source),
								//ctx.mkEq(nctx.pf.get("dest").apply(p0),this.destination));
		}else{
			ipEqual = ctx.mkOr(ctx.mkAnd(nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p0),this.source),
											nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p0),this.destination)
										),
								ctx.mkAnd(nctx.equalPacketIpToFwIpRule(nctx.pf.get("src").apply(p0),this.destination),
											nctx.equalPacketIpToFwIpRule(nctx.pf.get("dest").apply(p0),this.source)
									)
								);
		}
		if(this.protocol != ctx.mkInt(0)){
			ipEqual = ctx.mkAnd(ipEqual,nctx.equalPacketLv4ProtoToFwPacketLv4Proto(nctx.pf.get("lv4proto").apply(p0),this.protocol));
		}
		//System.out.println(ipEqual);
		return ctx.mkAnd(ipEqual,
						ctx.mkGe((IntExpr)nctx.port_functions.get("start").apply(nctx.pf.get("src_port").apply(p0)),(IntExpr)this.start_src_port),
		        		ctx.mkLe((IntExpr)nctx.port_functions.get("end").apply(nctx.pf.get("src_port").apply(p0)),(IntExpr)this.end_src_port),
		        		ctx.mkGe((IntExpr)nctx.port_functions.get("start").apply(nctx.pf.get("dest_port").apply(p0)),(IntExpr)this.start_dst_port),
		        		ctx.mkLe((IntExpr)nctx.port_functions.get("end").apply(nctx.pf.get("dest_port").apply(p0)),(IntExpr)this.end_dst_port)
						//ctx.mkEq(nctx.pf.get("src").apply(p_0),tp._1),ctx.mkEq(nctx.pf.get("dest").apply(p_0),tp._2),
						//ctx.mkGe((ArithExpr) nctx.pf.get("src_port").apply(p0),this.start_src_port),ctx.mkLe((ArithExpr) nctx.pf.get("src_port").apply(p0),this.end_src_port),
						//ctx.mkGe((ArithExpr) nctx.pf.get("dest_port").apply(p0),this.start_dst_port),ctx.mkLe((ArithExpr) nctx.pf.get("dest_port").apply(p0),this.end_dst_port)
						);
						
					
	}
}
