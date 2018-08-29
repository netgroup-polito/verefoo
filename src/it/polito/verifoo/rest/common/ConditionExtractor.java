package it.polito.verifoo.rest.common;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Node;
/**
 * This class hides all the complexities regarding the z3 deployment constraints.
 * It creates a boolean z3 expression, from a string formatted in a specific way, considering a certain number of complexities
 * @author Antonio
 *
 */
public class ConditionExtractor {
	private Logger logger = LogManager.getLogger("mylog");
	private Context ctx;
	private AutoContext autoctx;
	private Node n, next;
	private List<Connection> connections;
	HashMap<Node, HashMap<String, BoolExpr>> conditionDB;
	HashMap<Node, HashMap<String, BoolExpr>> stageConditions;
	private int latency;
	/**
	 * Sets up the enviroment to retrieve the information to return the right boolean expression from a string (it also saves it in a specific data structure)
	 * @param ctx the z3 context
	 * @param autoctx the z3 auto-context which holds all the informations about the auto-placement
	 * @param n the node subject of the 
	 * @param connections the list of connections between the hosts needed to retrieve the specific deployment latency
	 * @param conditionDB the reference to the external data structure that contains all the deployemnt conditions
	 * @param stageConditions the reference to the external data structure that contains only a part of the deployemnt conditions
	 */
	public ConditionExtractor(Context ctx, AutoContext autoctx, Node n, List<Connection> connections, HashMap<Node, HashMap<String, BoolExpr>> conditionDB, HashMap<Node, HashMap<String, BoolExpr>> stageConditions) {
		this.ctx = ctx;
		this.autoctx = autoctx;
		this.n = n;
		this.connections = connections;
		this.conditionDB = conditionDB;
		this.stageConditions = stageConditions;
	}
	/**
	 * Builds the boolean expression that represent the deployment from a string like "n1@h1/n2@h2" where the "/" is translated into an AND
	 * @param s the string that needs to be translated
	 * @param nodes the list of all the nodes in the service graph
	 * @return the z3 boolean expression of the string
	 */
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
		//if the nexthop is optional, add this information in the final boolean expression
		if(autoctx.nodeIsOptional(next)){
			secondCond = ctx.mkOr(ctx.mkBoolConst(second), ctx.mkNot(autoctx.getOptionalDeploymentCondition(next)));
		}else{
			secondCond = ctx.mkBoolConst(second);
		}
		//if the current node is optional, add this information in the final boolean expression
		if(autoctx.nodeIsOptional(n)){
			firstCond = ctx.mkOr(ctx.mkBoolConst(first), ctx.mkNot(autoctx.getOptionalDeploymentCondition(n)));
			BoolExpr dependency = autoctx.getOptionalDeploymentCondition(n);// autoctx.optionalConditionBetween(n, next);
			//logger.debug(secondCond + " depends from " + dependency);
			//System.out.println(secondCond + " depends from " + dependency);
			autoctx.addDependency(secondNode, secondHost, n.getName(), secondCond, dependency);
		}else{
			firstCond = ctx.mkBoolConst(first);
		}
		// build the final condition and saves the various information
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
	/**
	 * Builds the boolean expression that represent the deployment from a string like "n1@h1"
	 * @param s the string that needs to be translated
	 * @param client the node that represents the client
	 * @param server the node that represents the server
	 * @param nodes nodes the list of all the nodes in the service graph
	 * @param hostClient the host on which the client node will be deployed
	 * @param hostServer the host on which the server node will be deployed
	 * @return the z3 boolean expression of the string
	 */
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
			//if the current node is optional, add this information in the final boolean expression
			c = ctx.mkOr(ctx.mkBoolConst(s), ctx.mkNot(autoctx.getOptionalDeploymentCondition(n)));			
			
		}else if(n.getName().equals(client.getName()) && autoctx.nodeIsOptional(next)){
			//if the current node is the client and the nexthop is optional, add this information in the final boolean expression
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
	/**
	 * Public method that receives a string that represents a deployment and returns the correspondent boolean expression
	 * @param s the string that needs to be translated
	 * @param client the node that represents the client
	 * @param server the node that represents the server
	 * @param nodes nodes the list of all the nodes in the service graph
	 * @param hostClient the host on which the client node will be deployed
	 * @param hostServer the host on which the server node will be deployed
	 * @return the z3 boolean expression of the string
	 */
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
	/**
	 * Add to a condition c the information about the presence of optional nodes
	 * @param c
	 * @return
	 */
	public BoolExpr computeOptional(BoolExpr c){
		BoolExpr optionals = autoctx.optionalConditionBetween(n, next);
		System.out.println("List of Optional: " + optionals);
		if(optionals != null){
			c = ctx.mkAnd(c,ctx.mkNot(optionals));
		}
		return c;
	}

	/**
	 * @return the next hop of the node in the current deployment condition
	 */
	public Node getNext() {
		return next;
	}

	/**
	 * @return the latency envisages in the current deployment condition
	 */
	public int getLatency() {
		return latency;
	}

}
