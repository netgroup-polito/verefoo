package it.polito.verifoo.components;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;

import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.netobjs.AclFirewallAuto;

public class RoutingTable {

	public DatatypeExpr ip;
	public NetworkObject nextHop;
	public int latency;
	public BoolExpr condition;
	
	public RoutingTable(DatatypeExpr ip, NetworkObject nextHop, int latency, BoolExpr condition){
		this.ip=ip;
		this.latency=latency;
		this.nextHop=nextHop;
		this.condition=condition;
	}
	
	
}
