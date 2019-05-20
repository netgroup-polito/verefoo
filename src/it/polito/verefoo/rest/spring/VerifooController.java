package it.polito.verefoo.rest.spring;

import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import it.polito.verefoo.VerefooSerializer;
import it.polito.verefoo.jaxb.NFV;

//mvn clean package && java -jar target\verifoo-0.0.1-SNAPSHOT.jar

@Controller
public class VerifooController {

	@RequestMapping(value = "/deployment", method = RequestMethod.POST)
	public ResponseEntity<NFV> solveNFV(@RequestBody NFV nfv) {

		JAXBContext jc = null;
		VerefooSerializer test = null;
		StringWriter stringWriter = null; // create a JAXBContext capable of handling the generated classes
		try {
			jc = JAXBContext.newInstance("it.polito.verefoo.jaxb");
			Marshaller m = jc.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.setProperty(Marshaller.JAXB_NO_NAMESPACE_SCHEMA_LOCATION, "./xsd/nfvSchema.xsd");

			/*
			 * StringWriter stringWriter2 = new StringWriter(); m.marshal(nfv,
			 * stringWriter2); System.out.println(stringWriter2.toString());
			 */

			test = new VerefooSerializer(nfv);
			if (test.isSat()) {
				System.out.println("SAT");
				System.out.println("----------------------OUTPUT----------------------");
				stringWriter = new StringWriter();
				m.marshal(test.getResult(), System.out);
				// for debug purpose m.marshal(test.getResult(), stringWriter);
				System.out.println(stringWriter.toString());
				System.out.println("--------------------------------------------------");
			} else {
				System.out.println("UNSAT");
				System.out.println("----------------------OUTPUT----------------------");
				stringWriter = new StringWriter();
				m.marshal(test.getResult(), stringWriter);
				System.out.println(stringWriter.toString());
				System.out.println("--------------------------------------------------");
			}

		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return  new ResponseEntity<NFV>(test.getResult(), HttpStatus.CREATED);
	}

}
