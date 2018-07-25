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
 * @author Antonio
 *
 */
public class ConditionStringBuilder {
	private Logger logger = LogManager.getLogger("mylog");
	private Context ctx;
	private AutoContext autoctx;
	private List<Connection> connections;
	private HashMap<Node,List<String>> rawConditions;
	
	public ConditionStringBuilder(Context ctx, AutoContext autoctx, List<Connection> connections, HashMap<Node,List<String>> rawConditions) {
		this.ctx = ctx;
		this.autoctx = autoctx;
		this.connections = connections;
		this.rawConditions = rawConditions;
	}
	/**
	 * From the arguments it creates the string source@host, also checking if the source is optional and registering this information
	 * @param source
	 * @param host
	 * @return
	 */
	public String buildConditionString(Node source, String host){
		//System.out.print("On RT("+next.getName()+") ");
		//System.out.println(next.getName()+"@"+host1);
		//logger.debug("\t"+source.getName()+"@"+currentHost);
		
		if(autoctx.nodeIsOptional(source)){
			autoctx.addOptionalCondition(source, ctx.mkBoolConst(source.getName()+"@"+host));
		}
		return source.getName()+"@"+host;
	}
	/**
	 From the arguments it creates the string n1@h1/n2@h2
	 * @param n1
	 * @param h1
	 * @param n2
	 * @param h2
	 * @return
	 */
	public String buildConditionString(Node n1, String h1, Node n2, String h2){
		//logger.debug("\t"+n1.getName()+"@"+h1 + " AND " + n2.getName()+"@"+h2;;
		return n1.getName()+"@"+h1 + "/" + n2.getName()+"@"+h2;
	}
}
