package it.polito.verifoo.rest.webservice;

import java.net.MalformedURLException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBException;
import org.xml.sax.SAXException;
import com.microsoft.z3.Status;
import it.polito.verifoo.rest.common.BadNffgException;
import it.polito.verifoo.rest.common.Translator;
import it.polito.verifoo.rest.common.VerifooProxy;
import it.polito.verifoo.rest.jaxb.NFV;
import it.polito.verigraph.mcnet.components.IsolationResult;


@Path("/rest")
public class RestFoo {
	    @POST
	    @Consumes(MediaType.APPLICATION_XML)
		@Produces(MediaType.APPLICATION_XML)
	    public NFV put(NFV root) throws JAXBException, SAXException, BadNffgException, MalformedURLException {
			VerifooProxy test = new VerifooProxy(root.getNFFG(), root.getHosts(), root.getConnections(), root.getVNFCatalog());
			IsolationResult res=test.checkNFFGProperty();
			new Translator(res.model.toString(),root).convert();
			root.getProperty().setIsSat(res.result!=Status.UNSATISFIABLE);
			return root;
	    }

}