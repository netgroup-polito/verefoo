package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import javax.xml.ws.WebServiceException;

import org.xml.sax.SAXException;
import com.microsoft.z3.Status;
import it.polito.verifoo.rest.common.BadNffgException;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.Graph;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verigraph.mcnet.components.IsolationResult;


@Path("/rest")
public class RestFoo {
	    @POST
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(NFV root) throws MalformedURLException {
			try {
				for(Graph g:root.getGraphs().getGraph()){
	            	VerifooProxy test = new VerifooProxy(g, root.getHosts(), root.getConnections());
	            	IsolationResult res=test.checkNFFGProperty();
	            	if(res.result != Status.UNSATISFIABLE)
	            		new Translator(res.model.toString(),root).convert();
	            	g.getProperty().setIsSat(res.result!=Status.UNSATISFIABLE);
	            }
				return root;
			} catch (BadNffgException e) {
	        	throw new ProcessingException("Error in NFFG: "+e.toString());
			}
	    }

}