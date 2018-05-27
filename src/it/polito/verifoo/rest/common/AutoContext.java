package it.polito.verifoo.rest.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Expr;
import com.microsoft.z3.Optimize;

import it.polito.verifoo.rest.jaxb.*;
import it.polito.verigraph.mcnet.components.Core;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
/**
 * This class handles all the information about the auto-placement
 * @author Antonio
 *
 */
public class AutoContext extends Core{
	private Logger logger = LogManager.getLogger("mylog");
	Context ctx;
	public List<BoolExpr> constraints;
	public List<Tuple<BoolExpr, String>> softConstrAutoPlace;
	public List<Tuple<BoolExpr, String>> softConstrAutoConf;
	public List<Tuple<BoolExpr, String>> softConstrPorts;
	public List<Tuple<BoolExpr, String>> softConstrWildcard;
	
	private Map<String, List<NetworkObject>> optionalPlacement;
	private HashMap<Node, NetworkObject> optionalNodes;
	private Map<Node, List<BoolExpr>> optionalConditions;
	private Map<String, Tuple<BoolExpr, BoolExpr>> dependencies;
	private List<String> unnecessaryDependency;
	/**
	 * Public constructor for the auto context class
	 */
	public AutoContext(Context ctx,Object[]... args ) {
		super(ctx, args);
	} 
	
	@Override
	protected void init(Context ctx, Object[]... args) {
		this.ctx = ctx;
		optionalPlacement = new HashMap<>();
		optionalNodes = new HashMap<>();
		optionalConditions = new HashMap<>();
		dependencies = new HashMap<>();
		unnecessaryDependency = new ArrayList<>();
		constraints = new ArrayList<>();
		softConstrAutoPlace = new ArrayList<>();
		softConstrAutoConf = new ArrayList<>();
		softConstrWildcard = new ArrayList<>();
		softConstrPorts = new ArrayList<>();
	}
	
	@Override
	public void addConstraints(Optimize solver) {
		BoolExpr[] constr = new BoolExpr[constraints.size()];
		//System.out.println("Nr of autocontext hard constraint " + constraints.size());
		/*System.out.println("======AUTO CONTEXT HARD CONSTRAINTS====== ");
		constraints.forEach(c -> {
			System.out.println(c);
		});*/
        solver.Add(constraints.toArray(constr));
        //the order indicates the priority for the soft constraints
        //System.out.println("======AUTO CONTEXT SOFT CONSTRAINTS====== ");
		//System.out.println("Nr of autocontext autoconfiguration soft constraint " + softConstrAutoConf.size());
        for (Tuple<BoolExpr, String> t : softConstrAutoConf) {
        	//System.out.println(t._1 + "\n with value " + 100 + ". Node is " + t._2);
			solver.AssertSoft(t._1, 1000, t._2);
		}
		//System.out.println("Nr of autocontext autoplacement soft constraint " + softConstrAutoPlace.size());
        for (Tuple<BoolExpr, String> t : softConstrAutoPlace) {
        	//System.out.println(t._1 + "\n with value " + 100 + ". Node is " + t._2);
			solver.AssertSoft(t._1, 100, t._2);
		}
        //System.out.println("Wildcards Constraints");
		//System.out.println("Nr of autocontext wildcards soft constraint " + softConstrWildcard.size());
        for (Tuple<BoolExpr, String> t : softConstrWildcard) {
        	//System.out.println(t._1 + "\n with value " + 100 + ". Node is " + t._2);
			solver.AssertSoft(t._1, -10, t._2);
		}
        //System.out.println("Nr of net context ports soft constraint " + softConstrPorts.stream().distinct().count());
        for (Tuple<BoolExpr, String> t : softConstrPorts) {
        	//System.out.println(t._1 + "\n with value " + 10 + ". Node is " + t._2);
			solver.AssertSoft(t._1, -100, t._2);
		}
	}
	
	public void addOptionalPlacement(NetworkObject previous, NetworkObject next, NetworkObject optional){
		String key = previous+"__"+next;
		if(!optionalPlacement.containsKey(key)){
			optionalPlacement.put(key, new ArrayList<>());
		}
		if(!optionalPlacement.get(key).contains(optional)){
			logger.debug("Between " + previous + " and " + next + " there is an optional node: "+optional);
			optionalPlacement.get(key).add(optional);
		}
		
	}
	public void addOptionalCondition(Node optional, BoolExpr c){
		if(!optionalConditions.containsKey(optional)){
			optionalConditions.put(optional, new ArrayList<>());
		}
		for(BoolExpr e : optionalConditions.get(optional)){
			if(e.toString().equals(c.toString())){
				//logger.debug("Found duplicate condition for optional node " + optional.getName() + ": " + c);
				return;
			}
		}
		logger.debug("Set optional " + optional.getName() + " condition " + c);
		optionalConditions.get(optional).add(c);
	}

	public List<NetworkObject> hasOptionalNodes(NetworkObject previous, NetworkObject next){
		List<NetworkObject> res = optionalPlacement.get(previous+"__"+next);
		/*if(res != null && res.size() > 0){
			logger.debug("Between " + previous + " and " + next + " found these optional nodes: ");
			res.forEach(o -> logger.debug("\t"+o));
		}*/
		return res;
	}
	public List<Node> hasOptionalNodes(Node previous, Node next){
		List<NetworkObject> tmp = optionalPlacement.get(previous.getName()+"__"+next.getName());
		List<Node> res = new ArrayList<>();
		if(tmp == null || tmp.isEmpty()) return res;
		tmp.forEach(no -> {
			//res.add(optionalNodes.entrySet().stream().filter(e -> e.getValue().equals(no)).map(e -> e.getKey()).findFirst().get());
			res.add(optionalNodes.entrySet().stream().filter(e -> e.getValue().equals(no)).map(e -> e.getKey()).findFirst().get());
		});
		return res;
	}
	public BoolExpr optionalConditionBetween(Node previous, Node next){
		List<NetworkObject> dependency = optionalPlacement.get(previous.getName()+"__"+next.getName());
		BoolExpr res = null;
		if(dependency == null || dependency.size() == 0) return res;
		BoolExpr[] tmp = new BoolExpr[dependency.size()];
		res = ctx.mkOr(dependency.stream().map(no -> no.isUsed()).collect(Collectors.toList()).toArray(tmp));
		return res;
		
	}
	public BoolExpr fromOptionalNodeToCondition(Node optional){
		List<BoolExpr> conditions = optionalConditions.get(optional);
		if(conditions == null)
			throw new BadGraphError("Autoplacement condition not set for " + optional.getName(), EType.INVALID_NODE_CONFIGURATION);
		BoolExpr[] tmp = new BoolExpr[conditions.size()];
		BoolExpr result = ctx.mkOr(conditions.toArray(tmp));
		//logger.debug("From optional node " + optional.getName() + " to condition " + result);
		return result;
	}
	
	public void addOptionalNode(Node n, NetworkObject no){
		if(!optionalNodes.keySet().contains(n)){
			logger.debug("Added optional node: " + n.getName() +" with condition " + no.isUsed());
			optionalNodes.put(n, no);
		}
	}
	
	public boolean nodeIsOptional(Node n){
		return optionalNodes.keySet().contains(n);
	}

	public BoolExpr getOptionalDeploymentCondition(Node n){
		return optionalNodes.get(n).isUsed();
	}

	public boolean networkObjectIsOptional(NetworkObject no) {
		return optionalNodes.values().contains(no);
	}
	public void addDependency(String node, String host, String dependantNode, BoolExpr statement, BoolExpr dependency){
		dependencies.put(node+"@"+host+"@"+dependantNode, new Tuple<BoolExpr, BoolExpr>(statement, dependency));
	}
	public void removeDependency(String node, String host, String dependantNode){
		unnecessaryDependency.add(node+"@"+host+"@"+dependantNode);
	}
	
	public Map<String, List<Tuple<BoolExpr, BoolExpr>>> getDependencies(){
		Map<String, Tuple<BoolExpr, BoolExpr>> tmp = new HashMap<>(dependencies);
		unnecessaryDependency.forEach(d -> {
			tmp.remove(d);
		});
		Map<String, List<Tuple<BoolExpr, BoolExpr>>> res = new HashMap<>();
		tmp.forEach((k,v) -> {
			String newKey = k.substring(0, k.lastIndexOf("@"));
			if(!res.containsKey(newKey))
				res.put(newKey, new ArrayList<>());
			res.get(newKey).add(v);
		});
		//System.out.println("Final Dependencies" + res);
		logger.debug("Final Dependencies" + res);
		return res;
	}
}
