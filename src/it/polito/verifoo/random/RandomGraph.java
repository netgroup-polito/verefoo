package it.polito.verifoo.random;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import it.polito.verifoo.rest.jaxb.Antispam;
import it.polito.verifoo.rest.jaxb.Cache;
import it.polito.verifoo.rest.jaxb.Configuration;
import it.polito.verifoo.rest.jaxb.Dpi;
import it.polito.verifoo.rest.jaxb.Elements;
import it.polito.verifoo.rest.jaxb.Endhost;
import it.polito.verifoo.rest.jaxb.Fieldmodifier;
import it.polito.verifoo.rest.jaxb.Firewall;
import it.polito.verifoo.rest.jaxb.FunctionalTypes;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.Mailserver;
import it.polito.verifoo.rest.jaxb.Nat;
import it.polito.verifoo.rest.jaxb.Node;
import it.polito.verifoo.rest.jaxb.PName;
import it.polito.verifoo.rest.jaxb.Property;
import it.polito.verifoo.rest.jaxb.PropertyDefinition;
import it.polito.verifoo.rest.jaxb.Vpnaccess;
import it.polito.verifoo.rest.jaxb.Webserver;
import it.polito.verigraph.random.PolicyGen;
import it.polito.verigraph.random.RandomGenerator;

public class RandomGraph {

	Graph graph = null;
	PropertyDefinition properties = new PropertyDefinition();
	/**
	 * Creates a totally random generated graph for Verifoo
	 * @param random a random number generator
	 * @param maxClients max number of clients in the service graph
	 * @param maxServers max number of servers in the service graph
	 * @param maxInternalNodes max number of internal nodes in the service graph
	 * @param maxProperty max number of property that will be checked
	 */
	public RandomGraph(Random random, int maxClients, int maxServers, int maxInternalNodes, int maxProperty) {
		RandomGenerator randG = new RandomGenerator(maxClients, maxServers, maxInternalNodes);
		graph = randG.getTranslatedGraph();
		for(Node node : graph.getNode()){
			Configuration conf = createConfiguration(node, random.nextBoolean());
			node.setConfiguration(conf);
		}
		int nprop = 0;
		for(PolicyGen policy : randG.getPolicies()){
			if(nprop >= maxProperty) break;
			Property p = new Property();
			p.setGraph(0);
			p.setSrc(policy.getNodesrc().getName());
			p.setDst(policy.getNodedst().getName());
			switch(policy.getPolicyType()){
				/*case ISOLATION:{
					p.setName(PName.ISOLATION_PROPERTY);
					nprop++;
					break;
				}*/
				case REACHABILITY:{
					p.setName(PName.REACHABILITY_PROPERTY);
					nprop++;
					break;
				}
				default:
					continue;
			}
			properties.getProperty().add(p);
		}
		
	}
	/**
	 * 
	 * @return a random generated graph
	 */
	public Graph getGraph() {
		return graph;
	}
	/**
	 * 
	 * @return a random generated set of properties
	 */
	public PropertyDefinition getProperties() {
		return properties;
	}

	private Configuration createConfiguration (Node n, boolean nextBoolean) {
	
	    Configuration conf = new Configuration();
	    conf.setName("conf_"+n.getName());
	    switch(n.getFunctionalType().toString()){
	    case "ENDHOST":
	    	Endhost eh = new Endhost();
	        /*if(!nextBoolean){
	        	eh.setUrl("www.deepweb.com");
	        	eh.setBody("weapons");
	        	eh.setEmailFrom("spam@polito.it");
	        	
	        } else{*/
	        	eh.setUrl("www.facebook.com");
	        	eh.setBody("cats");
	        	eh.setEmailFrom("verigraph@polito.it");
	        //}
	        conf.setEndhost(eh);
	        break;
	    case "MAILSERVER":
	    	Mailserver s = new Mailserver();
	    	s.setName(n.getName());
	    	conf.setMailserver(s);
	        //conf = new Configuration(nodename, "Mail Server configuration", conf_array);
	        break;
	    case "WEBSERVER":
	    	Webserver s1 = new Webserver();
	    	s1.setName(n.getName());
	    	conf.setWebserver(s1);
	        //conf = new Configuration(nodename, "Web Sever configuration", conf_array);  //do not print
	        break;
	    case "ANTISPAM":
	    	Antispam a = new Antispam();
	        if(!nextBoolean){
	        	a.getSource().add("spam@polito.it");
	        } else{
	        	a.getSource().add("verigraph@polito.it");
	        }
	        conf.setAntispam(a);
	        break;
	    case "CACHE":
	    	Cache c = new Cache();
	        for(Node n1 : getClients())
	           c.getResource().add(n1.getName());
	        conf.setCache(c);
	        break;
	    case "DPI":
	    	Dpi d = new Dpi();
	    	d.getNotAllowed().add("weapons");
	        conf.setDpi(d);
	        break;
	    case "FIELDMODIFIER":
	    	Fieldmodifier f = new Fieldmodifier();
	    	f.setName("name");
	        /*if(!nextBoolean){
	            //conf_node.put("url", "www.facebook.com");
	            conf_node.put("body", "weapon");
	            conf_node.put("email_from", "spam@polito.it");
	            conf_array.add(conf_node);
	        }*/
	    	conf.setFieldmodifier(f);
	        break;
	    case "FIREWALL":
	    	Firewall fw = new Firewall();
	        if(!nextBoolean){
	            //DROPS ANY PACKET
	            for(Node client : getClients()){
	                for(Node server : getServers()){
	                    Elements e = new Elements();
	                	e.setSource(client.getName());
	                	e.setDestination(server.getName());
	                	fw.getElements().add(e);
	                }
	            }
	        }
	        conf.setFirewall(fw);
	        break;
	    case "NAT" :
	    	Nat nat = new Nat();
	        for(Node n1 : getClients())
	           nat.getSource().add(n1.getName());
	        conf.setNat(nat);
	        break;
	    case "VPNACCESS":
	    	Vpnaccess v = new Vpnaccess();
	    	v.setVpnexit("VPNEXIT"+(Integer.parseInt(n.getName().substring(9))+1)); //"vpnaccess"-> 9 char
	        conf.setVpnaccess(v);
	        break;
	    case "VPNEXIT":
	    	Vpnaccess v1 = new Vpnaccess();
	    	v1.setVpnexit("VPNACCESS"+(Integer.parseInt(n.getName().substring(7))+1)); //"vpnexit"-> 7 char nodename.replaceFirst("VPNEXIT", "VPNACCESS")
	        conf.setVpnaccess(v1);
	        break;
	    default:
	        return null;
	    }
	
	    return conf;
	}
	/**
	 * Getter for the client nodes
	 * @return only the client nodes in the service graph
	 */
	public List<Node> getClients(){
		return graph.getNode().stream().filter(n2 -> n2.getFunctionalType().equals(FunctionalTypes.ENDHOST) || n2.getFunctionalType().equals(FunctionalTypes.WEBCLIENT) || n2.getFunctionalType().equals(FunctionalTypes.MAILCLIENT)).collect(Collectors.toList());
	}
	/**
	 * Getter for the server nodes
	 * @return only the server nodes in the service graph
	 */
	public List<Node> getServers(){
		return graph.getNode().stream().filter(n2 -> n2.getFunctionalType().equals(FunctionalTypes.WEBSERVER) || n2.getFunctionalType().equals(FunctionalTypes.MAILSERVER)).collect(Collectors.toList());
	}
	/**
	 * Getter for the internal nodes
	 * @return only the internal nodes in the service graph
	 */
	public List<Node> getMiddle(){
		List<Node> tmp = new ArrayList<>(graph.getNode());
		tmp.removeAll(getClients());
		tmp.removeAll(getServers());
		return tmp;
	}
}
