package it.poltio.verifoo.spring;

import it.polito.verifoo.rest.common.VerifooSerializer;
import it.polito.verifoo.rest.jaxb.NFV;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class VerifooController {

	@RequestMapping(method = RequestMethod.POST, value = "/deployment", consumes = MediaType.APPLICATION_XML_VALUE)
	@ResponseBody
	public NFV registerStudent(@RequestBody NFV nfv) {
		System.out.println(nfv.getGraphs().getGraph().get(0).getId());
		
		return null;
		/*
		 * JAXBContext jc = null; VerifooSerializer test = null; StringWriter
		 * stringWriter = null; // create a JAXBContext capable of handling the
		 * generated classes try { jc =
		 * JAXBContext.newInstance("it.polito.verifoo.rest.jaxb"); Marshaller m =
		 * jc.createMarshaller(); m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
		 * Boolean.TRUE); m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION,
		 * "./xsd/nfvSchema.xsd");
		 * 
		 * StringWriter stringWriter2 = new StringWriter(); m.marshal( nfv,
		 * stringWriter2); System.out.println(stringWriter2.toString());
		 * 
		 * test = new VerifooSerializer(nfv); if (test.isSat()) {
		 * System.out.println("SAT");
		 * System.out.println("----------------------OUTPUT----------------------");
		 * stringWriter = new StringWriter(); m.marshal(test.getResult(), System.out);
		 * // for debug purpose m.marshal(test.getResult(), stringWriter);
		 * System.out.println(stringWriter.toString());
		 * System.out.println("--------------------------------------------------"); }
		 * else { System.out.println("UNSAT");
		 * System.out.println("----------------------OUTPUT----------------------");
		 * stringWriter = new StringWriter(); m.marshal(test.getResult(), stringWriter);
		 * System.out.println(stringWriter.toString());
		 * System.out.println("--------------------------------------------------");
		 * System.exit(1); }
		 * 
		 * } catch (JAXBException e) { e.printStackTrace(); } return test.getResult();
		 */
    }
	
}
