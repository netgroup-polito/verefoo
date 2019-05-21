package it.polito.verigraph.extra;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.IntNum;

import it.polito.verigraph.solver.NetContext;
/**
 * This class abstracts the concept of a firewall rule
 * @author Antonio
 *
 */
public class PacketFilterRule {
	private Context ctx;
	private NetContext nctx;
	private BoolExpr action;
	private DatatypeExpr source;
	private DatatypeExpr destination;
	private IntNum start_src_port;
	private IntNum end_src_port;
	private IntNum start_dst_port;
	private IntNum end_dst_port;
	private IntNum protocol;
	private boolean directional;
	/**
	 * Creates a firewall rule with a set of parameters
	 * @param nctx the z3 net context
	 * @param ctx the z3 context
	 * @param action the action of the rule in boolean format (ALLOW=true, DENY=false)
	 * @param source the DataType expression of the source address of the rule
	 * @param destination the DataType expression of the destination address of the rule
	 * @param string_src_port the string representing the port source interval of the rule
	 * @param string_dst_port the string representing the destination port interval of the rule
	 * @param protocol the level 4 protocol encoded as integer value (it is based on the enumeration class of the jaxb object)
	 * @param directional if the rule needs to be considered also in reverse
	 */
	public PacketFilterRule(NetContext nctx, Context ctx, boolean action, DatatypeExpr source, DatatypeExpr destination, String string_src_port,
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
	/**
	 * Creates a firewall rule with a set of parameters
	 * @param nctx the z3 net context
	 * @param ctx the z3 context
	 * @param action the action of the rule in boolean format (ALLOW=true, DENY=false)
	 * @param source the source address of the rule in a string format
	 * @param destination the destination address of the rule in a string format
	 * @param string_src_port the string representing the port source interval of the rule
	 * @param string_dst_port the string representing the destination port interval of the rule
	 * @param protocol the level 4 protocol encoded as integer value (it is based on the enumeration class of the jaxb object)
	 * @param directional if the rule needs to be considered also in reverse
	 */
	public PacketFilterRule(NetContext nctx, Context ctx, boolean action, String source, String destination,
			String string_src_port, String string_dst_port, int protocol, boolean directional) {
		DatatypeExpr sourceExpr = nctx.createIpAddress(source);
		DatatypeExpr destinationExpr = nctx.createIpAddress(destination);
		PortInterval pS = new PortInterval(string_src_port);
		PortInterval pD = new PortInterval(string_dst_port);
		
		this.nctx = nctx;
		this.ctx = ctx;
		this.action = action? ctx.mkTrue():ctx.mkFalse();
		this.source = sourceExpr;
		this.destination = destinationExpr;
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
		return "PacketFilterRule [action=" + action + ", source=" + source + ", destination=" + destination
				+ ", start_src_port=" + start_src_port + ", end_src_port=" + end_src_port + ", start_dst_port="
				+ start_dst_port + ", end_dst_port=" + end_dst_port + ", protocol=" + protocol + ", directional="
				+ directional + "]";
	}
	/**
	 * @param p0 the packet for the z3 expression
	 * @return a z3 boolean expression to check if the rule match the packet
	 */
	public BoolExpr matchPacket(Expr p0){
		BoolExpr ipEqual;
		if(directional){
			ipEqual = ctx.mkAnd(nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("src").apply(p0),this.source),
								nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("dest").apply(p0),this.destination));
		}else{
			ipEqual = ctx.mkOr(ctx.mkAnd(nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("src").apply(p0),this.source),
											nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("dest").apply(p0),this.destination)
										),
								ctx.mkAnd(nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("src").apply(p0),this.destination),
											nctx.equalPacketIpToPfIpRule(nctx.functionsMap.get("dest").apply(p0),this.source)
									)
								);
		}
		ipEqual = ctx.mkAnd(ipEqual,nctx.equalPacketLv4ProtoToFwPacketLv4Proto(nctx.functionsMap.get("lv4proto").apply(p0),this.protocol));
		return ctx.mkAnd(ipEqual,
						ctx.mkGe((IntExpr)nctx.portFunctionsMap.get("start").apply(nctx.functionsMap.get("src_port").apply(p0)),(IntExpr)this.start_src_port),
		        		ctx.mkLe((IntExpr)nctx.portFunctionsMap.get("end").apply(nctx.functionsMap.get("src_port").apply(p0)),(IntExpr)this.end_src_port),
		        		ctx.mkGe((IntExpr)nctx.portFunctionsMap.get("start").apply(nctx.functionsMap.get("dest_port").apply(p0)),(IntExpr)this.start_dst_port),
		        		ctx.mkLe((IntExpr)nctx.portFunctionsMap.get("end").apply(nctx.functionsMap.get("dest_port").apply(p0)),(IntExpr)this.end_dst_port)
				);
						
					
	}
	public String getSrc_port() {
		return this.getStart_src_port()+"-"+this.getEnd_src_port();
	}
	public String getDst_port() {
		return this.getStart_dst_port()+"-"+this.getEnd_dst_port();
	}
	
}
