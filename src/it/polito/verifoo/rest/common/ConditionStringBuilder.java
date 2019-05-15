package it.polito.verifoo.rest.common;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Node;
/**
 * Sets up the enviroment to create a formatted string that represents a deployment
 *
 */
public class ConditionStringBuilder {
	private Logger logger = LogManager.getLogger("mylog");
	private Context ctx;

	private List<Connection> connections;
	private HashMap<AllocationNode,List<String>> rawConditions;
	
	/**
	 * Public constructor of ConditionStringBuilder
	 * @param ctx It is the Z3 Context object
	 * @param connections It is a list of connections between nodes
	 * @param rawDeploymentConditions It is a map from VerifooProxy with raw information about deployment on the nodes
	 */
	public ConditionStringBuilder(Context ctx,  List<Connection> connections, HashMap<AllocationNode, List<String>> rawDeploymentConditions) {
		this.ctx = ctx;

		this.connections = connections;
		this.rawConditions = rawDeploymentConditions;
	}
	
	/**
	 * From the arguments it creates the string source@host, also checking if the source is optional and registering this information
	 * @param source Node logical source
	 * @param host String of physical host
	 * @return the formatted string that represents the specified deployment
	 */
	public String buildConditionString(Node source, String host){
		//System.out.print("On RT("+next.getName()+") ");
		//System.out.println(next.getName()+"@"+host1);
		//logger.debug("\t"+source.getName()+"@"+currentHost);
		return source.getName()+"@"+host;
	}
	/**
	 *  From the arguments it creates the string n1@h1/n2@h2
	 * @param n1 first node
	 * @param h1 first host
	 * @param n2 second node
	 * @param h2 second host
	 * @return the formatted string that represents the specified deployment
	 */
	public String buildConditionString(Node n1, String h1, Node n2, String h2){
		//logger.debug("\t"+n1.getName()+"@"+h1 + " AND " + n2.getName()+"@"+h2;;
		return n1.getName()+"@"+h1 + "/" + n2.getName()+"@"+h2;
	}
}
