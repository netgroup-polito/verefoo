package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import com.microsoft.z3.Status;
import it.polito.verifoo.rest.common.BadNffgException;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verigraph.mcnet.components.IsolationResult;


@Path("/rest")
public class RestFoo {
	    @POST
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(@Context HttpServletRequest req,NFV root) throws MalformedURLException {
			try {
				String z3model = new String();
				for(Graph g:root.getGraphs().getGraph()){
	            	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections(), root.getCapacityDefinition());
	            	IsolationResult res=test.checkNFFGProperty();
	            	if(res.result != Status.UNSATISFIABLE){
	            		z3model=z3model.concat(res.model.toString());
	            	}
	            	root.getPropertyDefinition().getProperty().stream().filter(p->p.getGraph()==g.getId()).findFirst().get().setIsSat(res.result!=Status.UNSATISFIABLE);            
	            }
				if(!z3model.isEmpty()){
					root.setParsingString(z3model);			
					Response res = ClientBuilder.newClient()
							.target(req.getRequestURL().toString().replace("/rest", ""))
							.path("/translate")
							.request(MediaType.APPLICATION_XML)
							.accept(MediaType.APPLICATION_XML)
							.post(Entity.entity(root,MediaType.APPLICATION_XML));
					if(res.getStatusInfo()==javax.ws.rs.core.Response.Status.OK){
							return res.readEntity(NFV.class);
					}else{
							throw new ProcessingException("Translator Error:"+res.readEntity(String.class));
					}
				}
				return root;
			} catch (BadNffgException e) {
	        	throw new ProcessingException("Error in NFFG: "+e.toString());
			}
	    }

}