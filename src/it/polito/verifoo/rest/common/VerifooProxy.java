package it.polito.verifoo.rest.common;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Status;

import it.polito.verifoo.components.RoutingTable;
import it.polito.verifoo.rest.jaxb.*;
import it.polito.verifoo.rest.logger.LoggerStream;
import it.polito.verigraph.mcnet.components.*;
import it.polito.verigraph.mcnet.netobjs.*;

public class VerifooProxy {
	    private Context ctx;
	    private NetContext nctx;
	    private Network net;
	    private HashMap<Node,NetworkObject> netobjs;
	    private ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm;
		public Checker check;
		private Logger logger = LogManager.getLogger("mylog");
		private List<VNF> vnfCat;
		List<Node> nodes;
		NFFG nffg;
	    public VerifooProxy(NFFG nffg,Hosts hosts,Connections conns, VNFCatalog vnfCat) throws BadNffgException{
			HashMap<String, String> cfg = new HashMap<String, String>();
		    cfg.put("model", "true");
		    ctx = new Context(cfg);
		    nodes=nffg.getNode();
		    this.vnfCat = vnfCat.getVNF();
		    this.nffg = nffg;
			String[] nodesname=new String[nodes.size()];
			String[] nodesip=new String[nodes.size()];
		    for(int i = 0; i < nodes.size(); i++){
		    	nodesname[i] = new String(nodes.get(i).getName());
				nodesip[i] = new String(nodes.get(i).getIp());
		    }
			nctx = new NetContext (ctx,nodesname,nodesip);
			//System.out.println(nctx.am);
			net = new Network (ctx,new Object[]{nctx});
			netobjs=new HashMap<Node,NetworkObject>();
			nodes.forEach(this::generateNetworkObject);
			adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
			nodes.forEach(this::generateAddressMapping);
		    net.setAddressMappings(adm);
		    checkNffg();		 
		    netobjs.forEach(this::attachToNet);
		    nodes.forEach(this::generateAcl);
		    check = new Checker(ctx,nctx,net);
	    }
	    
	    private void attachToNet(Node n,NetworkObject obj){
	    	net.attach(obj);
	    }
	    private FName getFunctionalType(String vnf){
	    	return this.vnfCat.stream().filter(nf -> nf.getName().compareTo(vnf)==0).findFirst().get().getFunctionalType(); 
	    }
	    private VNF getVNF(Node n){
			return this.vnfCat.stream().filter(nf->nf.getName().compareTo(n.getVNF())==0).findFirst().get();
	    }
		private void generateAcl(Node n){
			VNF vnf=getVNF(n);
			if(vnf.getFunctionalType().equals(FName.FW)){
				vnf.getConfiguration().forEach((c)->{
					if(c.getName()!=null && c.getValue() !=null && !c.getName().isEmpty()&& !c.getValue().isEmpty()){
				    	ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
						((AclFirewall)netobjs.get(n)).addAcls(acl);
						Tuple<DatatypeExpr,DatatypeExpr> rule=new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get(c.getName()),nctx.am.get(c.getValue()));
						acl.add(rule);
						logger.debug("Added acl:"+ rule.toString());
					}else{
						throw new IllegalArgumentException();
					} 
				});
			}
		}
	    private void generateAddressMapping(Node n){
			ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
			//System.out.println("Adding " + n.getIp() +"/"+ nctx.am.get(n.getIp()));
			al.add(nctx.am.get(n.getIp()));
			adm.add(new Tuple<>(netobjs.get(n), al));
	    }
		private void generateNetworkObject(Node n){
			FName ftype=getFunctionalType(n.getVNF());
			switch (ftype) {
				case FW:{
					netobjs.put(n,new AclFirewall(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case CLASSIFIER:{					
					netobjs.put(n,new Classifier(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case DUMB:{
					netobjs.put(n,new DumbNode(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case ENDHOST:{
					//TODO
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case SPAM:{
					PolitoAntispam spam=new PolitoAntispam(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					netobjs.put(n,spam);
					int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
					spam.installAntispam(blacklist);
					break;
				}
				case CACHE:{
					//TODO
					netobjs.put(n,new PolitoCache(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case IDS:{
					PolitoIDS ids=new PolitoIDS(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx});
					netobjs.put(n,ids);
					int[] blacklist=getVNF(n).getConfiguration().stream().map(ConfigurationType::getValue).mapToInt(s->Integer.parseInt(s)).toArray();
					ids.installIDS(blacklist);
					break;
				}
				case MAIL_CLIENT:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				// TODO for PolitoMailClient is needed another parameter
				case MAIL_SERVER:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case NAT:{					
					netobjs.put(n,new PolitoNat(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case VPN:{					
					break;
				}
				case WEB_CLIENT:{
					// TODO for PolitoWebClient is needed another parameter
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				case WEB_SERVER:{
					netobjs.put(n,new PolitoEndHost(ctx,new Object[]{nctx.nm.get(n.getName()),net,nctx}));
					break;
				}
				default:{
					System.err.println("Braiiinssssssssssss!");
					break;
				}
			}
		}
		
		
		private void checkNffg() throws BadNffgException{
            long nMailServer = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER;})
	            	 .count();
            long nWebServer = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.WEB_SERVER;})
	            	 .count();
            long nMailClient = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT;})
	            	 .count();
            long nWebClient = nodes.stream()
	            	 .filter((n) -> {return getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;})
	            	 .count();
            /*System.out.println("nMailServer: " + nMailServer +
            				   " nMailClient: " + nMailClient +
            				   " nWebServer: " + nWebServer +
            				   " nWebClient: " + nWebClient);*/
            if(nMailServer != nMailClient || nWebServer != nWebClient || nMailServer+nWebServer>1){
            	System.err.println("Only one client and one server of the same type is allowed");
            	throw new BadNffgException();
            }
            Node client = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT || getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;}).findFirst().get();
            Node server = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER || getFunctionalType(n.getVNF()) == FName.WEB_SERVER;}).findFirst().get();
			
            
		    
            setNextHop(client, server);   
		}

		private boolean setNextHop(Node source, Node server) throws BadNffgException{
			ArrayList<RoutingTable> rt = new ArrayList<RoutingTable>();
			//System.out.println("Searching next hop for " + source.getName() + " towards " + server.getName());
			if(source.getName().compareTo(server.getName()) == 0){
				return true;
			}
			List<Link> outgoingLinks = nffg.getLink().stream().filter(l -> l.getSourceNode().compareTo(source.getName()) == 0).collect(Collectors.toList());
			if(outgoingLinks.size() == 0){
				logger.error("Route: From " + source.getName() 
									+ " to " + nctx.am.get(server.getIp()) 
									+ " -> Dead End");
				throw new BadNffgException();
			}
			/* Redirect output to logfile */
			PrintStream stdout = System.out;
		    
			for(Link link : outgoingLinks){
				Node next = nodes.stream().filter(n -> n.getName().compareTo(link.getDestNode()) == 0).findFirst().get();
				if(setNextHop(next, server)){
					System.out.println("Route from " + source.getName() 
													+ " to " + nctx.am.get(server.getIp()) 
													+ " -> next hop: " + netobjs.get(next));
					System.setOut(new LoggerStream(System.out,logger));
					rt.add(new RoutingTable(nctx.am.get(server.getIp()), netobjs.get(next), link.getReqLatency(), nctx.ture));
					net.routingTable2(netobjs.get(source), rt);
					System.setOut(stdout);
				}
			}
			
			return true;
			
		}
		
		public void checkNFFGProperty(){

            Node source = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_CLIENT || getFunctionalType(n.getVNF()) == FName.WEB_CLIENT;}).findFirst().get();
            Node dest = nodes.stream().filter(n -> {return getFunctionalType(n.getVNF()) == FName.MAIL_SERVER || getFunctionalType(n.getVNF()) == FName.WEB_SERVER;}).findFirst().get();
            
			IsolationResult ret = this.check.checkIsolationProperty(netobjs.get(source), netobjs.get(dest));
			if (ret.result == Status.UNSATISFIABLE){
		     	   System.out.println("UNSAT"); // Nodes a and b are isolated
		    	}else{
		     		System.out.println("SAT ");
		     		logger.debug( ""+ret.model); //p.printModel(ret.model);
		     	}
		}
		
}
