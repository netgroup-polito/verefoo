package it.polito.verefoo.translator;

import it.polito.verefoo.jaxb.Host;
import it.polito.verefoo.jaxb.Node;
/**
 * This class returns the various patterns for matching the right strings in the model. This translator works for the version 3.3.4 of z3 
 * @author Antonio
 *
 */
public class z3Translator {
	public enum Datatype{
		ip_constructor, port_range_constructor;
	}
	
	public static String stringToSearchDeploymentCondition(Node node, Host host){
		return "define-fun .*integer_.*"+node.getName()+"@"+host.getName()+".* \\(\\) Int\n  1\\)";
	}
	public static String stringToSearchAddress(String address){
		return "define-fun .* Address\n  \\(ip_constructor "+address+"\\)\\)";
	}
	public static String stringToSearchNode(Node n){
		return "define-fun .*"+n.getName()+".*_auto_src.* Address\n  \\(ip_constructor .*\\)\\)"; 
	}
	public static String stringToSearchFwDestination(Node n, String nrOfRule){
		return "define-fun .*"+n.getName()+".*_auto_dst_"+nrOfRule+".* Address\n  \\(ip_constructor .*\\)\\)";
	}
	public static String stringToSearchFwProtocol(Node n, String nrOfRule){
		return "define-fun .*"+n.getName()+".*_auto_proto_"+nrOfRule+".* Int\n  .*\\)";
	}	
	public static String stringToSearchFwAction(Node n, String defAction){
		return "define-fun .*"+n.getName()+".*_auto_"+defAction+".* Bool\n  .*\\)";
	}
	public static String stringToSearchFwPort(Node n, String nrOfRule, String srcOrDest){
		return "define-fun .*"+n.getName()+".*_auto_"+srcOrDest+"p_"+nrOfRule+".* PortRange\n  \\(port_range_constructor .*\\)\\)";
	}
	public static String stringToSearchAntispamEmailFrom(Node n){
		return "define-fun .*"+n.getName()+".*_auto_emailFrom_.* Int\n .*";
	}
	public static String stringToSearchDpiNotAllowed(Node n){
		return "define-fun .*"+n.getName()+".*_auto_notAllowed_.* Int\n .*";
	}
	public static String stringToSearchNodeBasic(Node n){
		return "define-fun .*"+n.getName()+".*_auto_src.* Address\n  .*\\)";
	}
	public static String stringToSearchFwDestinationBasic(Node n, String nrOfRule){
		return "define-fun .*"+n.getName()+".*_auto_dst_"+nrOfRule+".* Address\n  .*\\)";
	}
	
	public static String stringToSeachNetworkObjectUsed(Node n) {
		return "define-fun (\\D)*" + n.getName() + "(\\D)*_used.* \\(\\) Bool\n.*true";
	}
	

	public static String stringToSeachNetworkObjectNotUsed(Node n) {
		return "define-fun (\\D)*" + n.getName() + "(\\D)*_used.* \\(\\) Bool\n.*false";
	}
	
	
	public static String matchNodeName(String match){
        String nodeSrc = match.substring(match.lastIndexOf("define-fun ")+11, match.lastIndexOf(" () Address"));
        nodeSrc = nodeSrc.replace("|", "");
        return nodeSrc;
	}
	
	public static String matchNrOfRule(String match){
		String nrOfRule = match.substring(match.lastIndexOf("_src_")+5, match.lastIndexOf(" () Address"));
        nrOfRule = nrOfRule.replace("|", "");
        return nrOfRule;
	}
	
	public static String matchPlainAttribute(String match){
        match = match.replace("|", "");
		return match.substring(match.lastIndexOf("\n  ")+3, match.lastIndexOf(")")); //+3 because the pattern it's like "\n  value"
	}
	
	public static String matchComplexAttribute(String match, Datatype datatype){
        match = match.replace("|", "");
		return match.substring(match.lastIndexOf(datatype.name())+datatype.name().length()+1, match.lastIndexOf("))"));
	}

	public static String saneString(String s){
		return s.replace("(", "").replace(")", "").replace("- ", "-").replace(" ", ".");
	}
}
