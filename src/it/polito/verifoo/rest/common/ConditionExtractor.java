package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verigraph.mcnet.components.NetworkObject;

public class ConditionExtractor {
	private Logger logger = LogManager.getLogger("mylog");
	private Context ctx;
	private AutoContext autoctx;
	private Node n, next;
	private List<Connection> connections;
	HashMap<Node, HashMap<String, BoolExpr>> conditionDB;
	HashMap<Node, HashMap<String, BoolExpr>> stageConditions;
	private int latency;
	
	public ConditionExtractor(Context ctx, AutoContext autoctx, Node n, List<Connection> connections, HashMap<Node, HashMap<String, BoolExpr>> conditionDB, HashMap<Node, HashMap<String, BoolExpr>> stageConditions) {
		this.ctx = ctx;
		this.autoctx = autoctx;
		this.n = n;
		this.connections = connections;
		this.conditionDB = conditionDB;
		this.stageConditions = stageConditions;
	}
	
	private BoolExpr DeploymentConditionFromDualString(String s, List<Node> nodes){
		String first = s.substring(0, s.lastIndexOf('/'));
		String second = s.substring(s.lastIndexOf('/')+1);
		String firstNode = first.substring(0,first.lastIndexOf('@'));
		String secondNode = second.substring(0, second.lastIndexOf('@'));
		String firstHost = first.substring(first.lastIndexOf('@')+1);
		String secondHost = second.substring(second.lastIndexOf('@')+1);
		if(firstHost.equals(secondHost)){
			latency = 0;
		}
		else{
			latency = connections.stream()
					.filter(con -> con.getSourceHost().equals(firstHost) && con.getDestHost().equals(secondHost))
					.findFirst().get().getAvgLatency();
		}
		BoolExpr firstCond, secondCond;
		
		next = nodes.stream().filter(node -> node.getName().equals(secondNode)  ).findFirst().get();
		if(autoctx.nodeIsOptional(next)){
			secondCond = ctx.mkOr(ctx.mkBoolConst(second), ctx.mkNot(autoctx.getOptionalDeploymentCondition(next)));
		}else{
			secondCond = ctx.mkBoolConst(second);
		}
		
		if(autoctx.nodeIsOptional(n)){
			firstCond = ctx.mkOr(ctx.mkBoolConst(first), ctx.mkNot(autoctx.getOptionalDeploymentCondition(n)));
			BoolExpr dependency = autoctx.getOptionalDeploymentCondition(n);// autoctx.optionalConditionBetween(n, next);
			//logger.debug(secondCond + " depends from " + dependency);
			//System.out.println(secondCond + " depends from " + dependency);
			autoctx.addDependency(secondNode, secondHost, n.getName(), secondCond, dependency);
		}else{
			firstCond = ctx.mkBoolConst(first);
		}
		
		BoolExpr c = ctx.mkAnd(firstCond, secondCond);
		if(n.getName().equals(firstNode)){
			if(autoctx.nodeIsOptional(n)){
				autoctx.addOptionalCondition(n, firstCond);
				conditionDB.get(n).put(firstHost, ctx.mkBoolConst(first));
				stageConditions.get(n).put(firstHost, ctx.mkBoolConst(first));
				//conditionDB.get(n).put(firstHost, firstCond);
				//stageConditions.get(n).put(firstHost, firstCond);
			}else{
				conditionDB.get(n).put(firstHost, firstCond);
				stageConditions.get(n).put(firstHost, firstCond);
			}
		}	
		return c;
	}
	
	private BoolExpr DeploymentConditionFromSingleString(String s, Node client, Node server, List<Node> nodes, String hostClient, String hostServer){
		String node = s.substring(0,s.lastIndexOf('@'));
		String host = s.substring(s.lastIndexOf('@')+1);
		if(n.getName().equals(client.getName())){
			latency = connections.stream()
					.filter(con -> con.getSourceHost().equals(hostClient) && con.getDestHost().equals(host))
					.findFirst().get().getAvgLatency();
			next = nodes.stream().filter(no -> no.getName().equals(node) ).findFirst().get();
		}
		else{
			//logger.debug("Finding latency between " + host + " and " + hostServer);
			latency = connections.stream()
					.filter(con -> con.getSourceHost().equals(host) && con.getDestHost().equals(hostServer))
					.map(c -> c.getAvgLatency())
					.findFirst().orElse(0);

			next = nodes.stream().filter(no -> no.getName().equals(server.getName()) ).findFirst().get();
		}
		
		BoolExpr c;
		if(autoctx.nodeIsOptional(n)){
			c = ctx.mkOr(ctx.mkBoolConst(s), ctx.mkNot(autoctx.getOptionalDeploymentCondition(n)));			
			
		}else if(n.getName().equals(client.getName()) && autoctx.nodeIsOptional(next)){
			c = ctx.mkOr(ctx.mkBoolConst(s), ctx.mkNot(autoctx.getOptionalDeploymentCondition(next)));
		}else{
			c = ctx.mkBoolConst(s);
		}
		List<Node> dependency = autoctx.hasOptionalNodes(n, next);
		if(!dependency.isEmpty()){
			dependency.forEach(d -> {
				//System.out.println(c + " doesn't depend anymore from " + d.getName());
				//logger.debug(c + " doesn't depend anymore from " + d.getName());
				autoctx.removeDependency(node, host, d.getName());
			});
		}
		//logger.debug("Checking optional placement for " + n.getName() + " gave condition " + c);
		if(n != client){
			if(autoctx.nodeIsOptional(n)){
				autoctx.addOptionalCondition(n, c);
				conditionDB.get(n).put(host, ctx.mkBoolConst(s));
				stageConditions.get(n).put(host, ctx.mkBoolConst(s));
				//conditionDB.get(n).put(host, c);
				//stageConditions.get(n).put(host, c);
			}else{
				conditionDB.get(n).put(host, c);
				stageConditions.get(n).put(host, c);
			}
		}
		return c;
	}
	
	public BoolExpr DeploymentConditionFromString(String s, Node client, Node server, List<Node> nodes, String hostClient, String hostServer){
		BoolExpr c;
		if(s.lastIndexOf('/') != -1){
			c = DeploymentConditionFromDualString(s, nodes);
		}
		else{
			c = DeploymentConditionFromSingleString(s, client, server, nodes, hostClient, hostServer);							
		}
		return c;
	}
	
	public BoolExpr computeOptional(BoolExpr c){
		BoolExpr optionals = autoctx.optionalConditionBetween(n, next);
		System.out.println("List of Optional: " + optionals);
		if(optionals != null){
			c = ctx.mkAnd(c,ctx.mkNot(optionals));
		}
		return c;
	}

	/**
	 * @return the next
	 */
	public Node getNext() {
		return next;
	}

	/**
	 * @return the latency
	 */
	public int getLatency() {
		return latency;
	}

}
