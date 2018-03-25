package it.polito.verifoo.rest.common;

import java.util.HashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;

import it.polito.verifoo.rest.jaxb.Connection;
import it.polito.verifoo.rest.jaxb.Node;

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
	
	public String buildConditionString(Node source, String host){
		//System.out.print("On RT("+next.getName()+") ");
		//System.out.println(next.getName()+"@"+host1);
		//logger.debug("\t"+source.getName()+"@"+currentHost);
		
		if(autoctx.nodeIsOptional(source)){
			autoctx.addOptionalCondition(source, ctx.mkBoolConst(source.getName()+"@"+host));
		}
		return source.getName()+"@"+host;
	}
	public String buildConditionString(Node n1, String h1, Node n2, String h2){
		//logger.debug("\t"+n1.getName()+"@"+h1 + " AND " + n2.getName()+"@"+h2;;
		return n1.getName()+"@"+h1 + "/" + n2.getName()+"@"+h2;
	}
}
