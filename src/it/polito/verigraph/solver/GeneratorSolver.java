package it.polito.verigraph.solver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.DatatypeExpr;
import com.microsoft.z3.Expr;
import com.microsoft.z3.FuncDecl;
import com.microsoft.z3.IntExpr;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Sort;
import com.microsoft.z3.Status;
import com.microsoft.z3.Z3Exception;

import it.polito.verigraph.exception.DataNotFoundException;
import it.polito.verigraph.mcnet.components.Checker;
import it.polito.verigraph.mcnet.components.IsolationResult;
import it.polito.verigraph.mcnet.components.NetContext;
import it.polito.verigraph.mcnet.components.Network;
import it.polito.verigraph.mcnet.components.NetworkObject;
import it.polito.verigraph.mcnet.components.Tuple;
import it.polito.verigraph.mcnet.netobjs.AclFirewall;
import it.polito.verigraph.mcnet.netobjs.EndHost;
import it.polito.verigraph.mcnet.netobjs.PacketModel;
import it.polito.verigraph.mcnet.netobjs.PolitoAntispam;
import it.polito.verigraph.mcnet.netobjs.PolitoCache;
import it.polito.verigraph.mcnet.netobjs.PolitoEndHost;
import it.polito.verigraph.mcnet.netobjs.PolitoFieldModifier;
import it.polito.verigraph.mcnet.netobjs.PolitoIDS;
import it.polito.verigraph.mcnet.netobjs.PolitoMailClient;
import it.polito.verigraph.mcnet.netobjs.PolitoMailServer;
import it.polito.verigraph.mcnet.netobjs.PolitoNat;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnAccess;
import it.polito.verigraph.mcnet.netobjs.PolitoVpnExit;
import it.polito.verigraph.mcnet.netobjs.PolitoWebClient;
import it.polito.verigraph.mcnet.netobjs.PolitoWebServer;

public class GeneratorSolver{
	Scenario scenario;
	Context ctx;
	Network net;
	NetContext nctx;
	List<BoolExpr> constraints = new ArrayList<BoolExpr>();
	public Checker check;
	Map<String, Object> mo=new HashMap<String, Object>();
	List<String> path=new ArrayList<String>();
	
	public List<String> getPaths(){
		if(path!=null)
			return path;
		else
			return null;
	}
	
	public GeneratorSolver(Scenario tmp, List<String> s) {
		this.scenario=tmp;
		this.path=s;
		
	}

	public void resetZ3() throws Z3Exception{
	    HashMap<String, String> cfg = new HashMap<String, String>();
	    cfg.put("model", "true");
	     ctx = new Context(cfg);
	}
	
	public String run(String src, String dst){	
		IsolationResult result;
		result=check.checkIsolationProperty((NetworkObject)mo.get(src), (NetworkObject)mo.get(dst));
		String res=new String();
		//System.out.println("Result RUN: " +result.result );
		if (result.result == Status.UNSATISFIABLE){
	     	   res="UNSAT"; // Nodes a and b are isolated
	    	}else if(result.result == Status.SATISFIABLE){
	     		res= "SAT";
	    	}else if(result.result == Status.UNKNOWN){
	    		res= "UNKNOWN";
	    	}
		return res;
	}
	
	public void genSolver() {
		resetZ3();
		/*List<String> names=new ArrayList<String>();
		List<String> addresses=new ArrayList<String>();
		for(Map.Entry<String, Map<String, String>> a : scenario.chn.entrySet()){
			String name=a.getKey();
			Map<String, String> nodo=a.getValue();
			String address=nodo.get("address");
			names.add(name);
			addresses.add(address);
		}*/
		
		//String name_nctx=listToArguments(scenario.nodes_names);
		//String type_nctx=listToArguments(scenario.nodes_addresses);
		
		//String name_nctx=listToArguments(names);
		//String type_nctx=listToArguments(addresses);
		
		String[] name_nctx=listToStringArguments(scenario.nodes_names);
		String[] address_nctx=listToStringArguments(scenario.nodes_addresses);	
		
		nctx = new NetContext (ctx, name_nctx, address_nctx);
		
		
		net = new Network (ctx,new Object[]{nctx});
		
		//classes creation mcnet.objects: Map<nome_nodo, obj created>		
		for(int i=0; i<scenario.nodes_names.size(); i++){
			if(scenario.nodes_names.get(i)!=null){	
				
				//mo.put(scenario.nodes_names.get(i),setDevice(scenario.nodes_names.get(i), ctx, nctx, net));
				//setDevice(scenario.nodes_names.get(i), ctx, nctx, net, mo);
				setDevice(scenario.nodes_names.get(i));
			}
		}
		
		
		//setMapping
		ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>> adm = new ArrayList<Tuple<NetworkObject,ArrayList<DatatypeExpr>>>();
		doMappings(adm, mo);
		System.out.println("adm: " + adm);
		net.setAddressMappings(adm);
		
		//setRouting
		Map<String, List<Tuple<DatatypeExpr,NetworkObject>>> ro=new HashMap<String,List<Tuple<DatatypeExpr,NetworkObject>>>();
		doRouting(ro, mo);
		
	/*	for(Map.Entry<String, Object> xx : mo.entrySet()){
			//System.out.println("nome device in mo: " + xx.getKey());
			Object oo=xx.getValue();
			if(oo instanceof PolitoEndHost){
				System.out.println("nome device in mo: " + xx.getKey() + " è un endhost");
			}else if(oo instanceof PolitoNat){
				System.out.println("nome device in mo: " + xx.getKey() + " è un nat");
			}else if(oo instanceof AclFirewall){
				System.out.println("nome device in mo: " + xx.getKey() + " è un firewall");
			}else if(oo instanceof EndHost){
				System.out.println("nome device in mo: " + xx.getKey() + " è un endpoint");
			}else if(oo instanceof PolitoIDS){
				System.out.println("nome device in mo: " + xx.getKey() + " è un dpi");
			}else if(oo instanceof PolitoWebServer){
				System.out.println("nome device in mo: " + xx.getKey() + " è un dpi");
			}
		}
		*/
		
		
		//configureDevice
		//System.out.println("Pre configuration device");
		configureDevice();
		
		
		check = new Checker(ctx,nctx,net);
		
		
	}

	private void setDevice(String name) {
//	private void setDevice(String name, Context ctx, NetContext nctx, Network net, Map<String, Object> mo) {
		Map<String, String> value=scenario.chn.get(name);	
		String type=value.get("functional_type");
		//System.out.println("type in setDevice: "+ type);
		if(type.compareTo("endhost")==0){			
			PolitoEndHost endhost=new PolitoEndHost(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, endhost);
		}else if(type.compareTo("cache")==0){
			PolitoCache cache=new PolitoCache(ctx, new Object[] { nctx.nm.get(name), net, nctx });
			mo.put(name, cache);
		}else if(type.compareTo("antispam")==0){
			PolitoAntispam antispam=new PolitoAntispam(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, antispam);
		}else if(type.compareTo("fieldmodifier")==0){
			PolitoFieldModifier fieldmodifier=new PolitoFieldModifier(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, fieldmodifier);
		}else if(type.compareTo("mailclient")==0){
			String conf=(scenario.config_obj.get(name)).get("mailserver");
			PolitoMailClient mailclient=new PolitoMailClient(ctx, new Object[]{nctx.nm.get(name), net, nctx, nctx.am.get(conf)});
			mo.put(name, mailclient);
		}else if(type.compareTo("mailserver")==0){
			PolitoMailServer mailserver=new PolitoMailServer(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, mailserver);
		}else if(type.compareTo("nat")==0){
			PolitoNat nat=new PolitoNat(ctx, new Object[]{nctx.nm.get(name), net, nctx});			
			mo.put(name, nat);
		}else if(type.compareTo("vpnaccess")==0){
			PolitoVpnAccess vpnaccess=new PolitoVpnAccess(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, vpnaccess);
		}else if(type.compareTo("vpnexit")==0){
			PolitoVpnExit vpnexit=new PolitoVpnExit(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, vpnexit);
		}else if(type.compareTo("webserver")==0){
			PolitoWebServer webserver=new PolitoWebServer(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			
			mo.put(name, webserver);
		}else if(type.compareTo("webclient")==0){
			String conf=(scenario.config_obj.get(name)).get("webserver");
			PolitoWebClient webclient=new PolitoWebClient(ctx, new Object[]{nctx.nm.get(name), net, nctx, nctx.am.get(conf)});
			mo.put(name, webclient);
		}else if(type.compareTo("dpi")==0){
			PolitoIDS dpi=new PolitoIDS(ctx, new Object[]{nctx.nm.get(name), net, nctx});		
			mo.put(name, dpi);
		}else if(type.compareTo("endpoint")==0){
			EndHost endpoint=new EndHost(ctx, new Object[]{nctx.nm.get(name), net, nctx});
			mo.put(name, endpoint);
		}else if(type.compareTo("firewall")==0){
			AclFirewall firewall=new AclFirewall(ctx, new Object[]{nctx.nm.get(name), net, nctx});		
			mo.put(name, firewall);
		}
		
		
	}

	private void configureDevice() {
		System.out.println("Configuration Device");
		for(Map.Entry<String,Object> cd : mo.entrySet()){
			String name=cd.getKey();
			//System.out.println("Configuration Device name: " + name);
			//String type=(scenario.chn.get(name)).get("functional_type");
			String address=(scenario.chn.get(name)).get("address");
			Object model=cd.getValue();
			
			if(model instanceof PolitoEndHost){
				//System.out.println("Configuration Endhost");
				Map<String, String> packet=scenario.config_obj.get(name);
				if(packet!=null){	
				
					PacketModel pModel = new PacketModel();
					if(packet.get("body")!=null){
						pModel.setBody(String.valueOf(packet.get("body")).hashCode());	
					//	System.out.println("body_hashCode: " + pModel.getBody());
					}
					if(packet.get("destination")!=null){
						  pModel.setIp_dest(nctx.am.get(packet.get("destination")));
						//  System.out.println("destination: " + nctx.am.get(packet.get("destination")));
					}
					if(packet.get("sequence")!=null){
						  pModel.setSeq(String.valueOf(packet.get("sequence")).hashCode());
					}
					if(packet.get("email_from")!=null){
						  pModel.setEmailFrom(String.valueOf(packet.get("email_from")).hashCode());
					}
					if(packet.get("url")!=null){
						  pModel.setUrl(String.valueOf(packet.get("url")).hashCode());
					}
					if(packet.get("options")!=null){
						  pModel.setOptions(String.valueOf(packet.get("options")).hashCode());
					}
					if(packet.get("protocol")!=null){
						String proto=packet.get("protocol");
						if(proto.compareTo("HTTP_REQUEST")==0)
							pModel.setProto(nctx.HTTP_REQUEST);
						else if(proto.compareTo("HTTP_RESPONSE")==0)
							pModel.setProto(nctx.HTTP_RESPONSE);
						else if(proto.compareTo("POP3_REQUEST")==0)
							pModel.setProto(nctx.POP3_REQUEST);
						else if(proto.compareTo("POP3_RESPONSE")==0)
							pModel.setProto(nctx.POP3_RESPONSE);						
					}
			  
			   
					((PolitoEndHost)cd.getValue()).installEndHost(pModel);
				}
				else{
					System.out.println("endhost empty");
					//throw new DataNotFoundException("configuration endhost empty");		
					
				}
				
			}else if(model instanceof PolitoCache){
				
				List<String> list_tmp=scenario.config_array.get(name);
				for(int i=0; i<list_tmp.size(); i++){
					if(!scenario.nodes_addresses.contains(list_tmp.get(i)))
						list_tmp.remove(i);
				}
				List<String> list=trimIp(list_tmp);
				if(list!=null){					
					PolitoCache cache=(PolitoCache)cd.getValue();					
					NetworkObject [] array_no=listToNetworkArguments(list);					
					((PolitoCache)cd.getValue()).installCache(array_no);						
					}	
				else{
					System.out.println("Cache empty");
					//throw new DataNotFoundException("configuration cache empty");		
					
				}
				
			}else if(model instanceof PolitoAntispam){
				List<String> list_tmp=scenario.config_array.get(name);
				List<String> list=trimIp(list_tmp);
				if(list!=null){
					PolitoAntispam antispam=(PolitoAntispam)cd.getValue();					
					int[] blackList=listToIntArguments(list);
					((PolitoAntispam)cd.getValue()).installAntispam(blackList);
					
				}
				else{
					System.out.println("Antispam empty");
					//throw new DataNotFoundException("configuration antispam empty");		
					
				}
			}else if(model instanceof PolitoFieldModifier){
				((PolitoFieldModifier)cd.getValue()).installFieldModifier();
				
			}else if(model instanceof PolitoMailClient){
				//regole inserire nella init
				continue;
				
			}else if(model instanceof PolitoMailServer){
				//regole inserire nella init
				continue;
				
			}else if(model instanceof PolitoNat){
				List<String> list=scenario.config_array.get(name);				
				if(!list.isEmpty()){		
					/*for(int i=0; i<list.size(); i++){
						if(!scenario.nodes_addresses.contains("ip_"+list.get(i))){
							list.remove(i);
						}
					}*/
					 ArrayList<DatatypeExpr> ia = new ArrayList<DatatypeExpr>();
					 for(String s : list){	
						System.out.println("host da inserire nel nat: " + s);
						 if(scenario.nodes_addresses.contains(s)){
							 ia.add(nctx.am.get(s));
							 System.out.println("aggiunto: " + s);
						 }
					 }
					// System.out.println("elementi inseriti nella ia del nat: " + ia.size());
					// System.out.println("address NAT: " + address);
					((PolitoNat)cd.getValue()).natModel(nctx.am.get(address));
					((PolitoNat)cd.getValue()).setInternalAddress(ia);
				}	
				else{
					System.out.println("nat empty");
				//	throw new DataNotFoundException("configuration nat empty");			
					
				}
				
			}else if(model instanceof PolitoVpnAccess){
				Map<String, String> vpnexit=scenario.config_obj.get(name);
				if(vpnexit!=null){
					for(Map.Entry<String, String> a : vpnexit.entrySet())
					((PolitoVpnAccess)cd.getValue()).vpnAccessModel(nctx.am.get(address), nctx.am.get(a.getValue()));
				}
				else{
					System.out.println("Vpnaccess empty");
					
					
					
				}
				
			}else if(model instanceof PolitoVpnExit){
				Map<String, String> vpnaccess=scenario.config_obj.get(name);
				if(vpnaccess!=null){
					for(Map.Entry<String, String> a : vpnaccess.entrySet())
					((PolitoVpnExit)cd.getValue()).vpnExitModel(nctx.am.get(address), nctx.am.get(a.getValue()));
				}
				else{
					System.out.println("Vpnexit empty");
					//throw new DataNotFoundException("configuration vpnexit empty");
					
					
				}
				
			}else if(model instanceof PolitoWebServer){
				//le regole vengono inserite nella init()
				continue;
				
			}else if(model instanceof PolitoWebClient){
				//le regole vengono inserite nella init()
				continue;
				
			}else if(model instanceof PolitoIDS){
				List<String> list=scenario.config_array.get(name);				
				if(!list.isEmpty()){
					//System.out.println("dpi_da settare:   " + list);
					PolitoIDS dpi=(PolitoIDS)cd.getValue();					
					int[] blackList=listToIntArguments(list);
					for(int a : blackList){
						//System.out.println("blacklist_dpi: " + a);
					}
					((PolitoIDS)cd.getValue()).installIDS(blackList);	
					
					}	
				else{
					System.out.println("Dpi empty");
				//	throw new DataNotFoundException("configuration dpi empty");		
					
				}
				
				
			}else if(model instanceof EndHost){
				//le regole vengono inserite nella init()
				continue;
				
			}else if(model instanceof AclFirewall){
			//	System.out.println("Firewall configuration");
				Map<String, String> acls=scenario.config_obj.get(name);
				 ArrayList<Tuple<DatatypeExpr,DatatypeExpr>> acl = new ArrayList<Tuple<DatatypeExpr,DatatypeExpr>>();
				
				 if(!acls.isEmpty()){
				   	for(Map.Entry<String, String> a : acls.entrySet()){
						String dest=a.getKey();
						String src=a.getValue();
						System.out.println("Acl firewall  dest: " + dest + " src: " + src);
						if(scenario.nodes_addresses.contains("ip_"+dest) && scenario.nodes_addresses.contains("ip_"+src)){
							acl.add(new Tuple<DatatypeExpr,DatatypeExpr>(nctx.am.get("ip_"+dest),nctx.am.get("ip_"+src)));
						
						}
						System.out.println("acls size: " + acl.size());
							
					}
					((AclFirewall)cd.getValue()).addAcls(acl);
					
				}
				else{
					
					 //((AclFirewall)cd.getValue()).addAcls(acl);
					System.out.println("ACLS empty");
					//throw new DataNotFoundException("configuration firewall empty");
					
					
				}
			}
			else
				System.out.println("No Configuration added");
			
		}		
		
	}

	

	private List<String> trimIp(List<String> list) {
		List<String> result=new ArrayList<String>();
		for(String s : list){
			if(s.length()>3){
				if(s.substring(0, 3).compareTo("ip_")==0)
					result.add(s.substring(3));
				else
					result.add(s);
			}
		}
		return result;
	}
	

	private void doRouting(Map<String, List<Tuple<DatatypeExpr, NetworkObject>>> ro, Map<String, Object> mo) {
		for(String nodes : scenario.nodes_names){
			
			Object obj=mo.get(nodes);
			if(obj!=null){
				
				ArrayList<Tuple<DatatypeExpr,NetworkObject>> rt = new ArrayList<Tuple<DatatypeExpr,NetworkObject>>();
				Map<String, String> route=scenario.routing.get(nodes);
				for(Map.Entry<String,String> r : route.entrySet()){
					String dest=r.getKey();
					NetworkObject next_hop=(NetworkObject)mo.get(r.getValue());
					System.out.println("string nodes in doROuting: " + nodes);
					System.out.println("dest " + nodes + ": " + dest);
					System.out.println("next_hop " + nodes + ": " + next_hop);
					rt.add(new Tuple<DatatypeExpr,NetworkObject>(nctx.am.get(dest), next_hop));
				}
				net.routingTable((NetworkObject)obj, rt);
				net.attach((NetworkObject)obj);
				ro.put(nodes, rt);
			}
		}
	//	System.out.println("net.attach: " + net.elements);
		
		
		
	}


	private void doMappings(ArrayList<Tuple<NetworkObject, ArrayList<DatatypeExpr>>> adm, Map<String, Object> mo) {
		for(Map.Entry<String, Object> obj : mo.entrySet()){	
			String name=obj.getKey().toString();
			Object model=obj.getValue();
			ArrayList<DatatypeExpr> al = new ArrayList<DatatypeExpr>();
			al.add(nctx.am.get((scenario.chn.get(name)).get("address")));
		//	System.out.println("doMappings: al.add(nctx.am.get(indirizzo): " +(scenario.chn.get(name)).get("address")) ;
			adm.add(new Tuple<>((NetworkObject)(model),al));
		}
			
		
		
		
	}


	

	
	//function Arguments:
	
	
	private String listToArguments(List<String> nodes_names) {
		StringBuffer result=new StringBuffer();
		for(int i=0; i<nodes_names.size(); i++){
			if(i==nodes_names.size()-1){
				result.append("\"" + nodes_names.get(i) + "\"");
			}
			else{		
			result.append("\"" + nodes_names.get(i) + "\"");
			result.append(", ");
			}
			}
		return result.toString();
	}
	private NetworkObject[] listToNetworkArguments(List<String> arg){
		NetworkObject[] o= new NetworkObject[arg.size()];
		for(int i=0; i<arg.size(); i++){
			if(arg.get(i)!=null)
				o[i]= nctx.nm.get(arg.get(i));
		}
		return o;
	}
	
	private String[] listToStringArguments(List<String> arg){
		String[] o= new String[arg.size()];
		for(int i=0; i<arg.size(); i++){
			if(arg.get(i)!=null)
				o[i]= arg.get(i);
			//System.out.println("name_nctx: " + o[i]);
			
		}
		return o;
	}
	
private int[] listToIntArguments(List<String> arg) {
	int[] o= new int[arg.size()];
	for(int i=0; i<arg.size(); i++){
		if(arg.get(i)!=null)
			o[i]= String.valueOf(arg.get(i)).hashCode();
			
	}
	return o;
	}

}
