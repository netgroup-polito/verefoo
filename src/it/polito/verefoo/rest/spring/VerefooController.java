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

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.ResponseHeader;

//mvn clean package && java -jar target\verifoo-0.0.1-SNAPSHOT.jar

// swagger can be accessed at http://localhost:8085/verefoo/swagger-ui.html 

@Controller
public class VerefooController {

	@ApiOperation(value = "getGreeting", nickname = "getGreeting")
	@RequestMapping(value = "/deployment", method = RequestMethod.POST)
	@ApiImplicitParams({
			@ApiImplicitParam(name = "name", value = "User's name", required = false, dataType = "string", paramType = "query", defaultValue = "Niklas") })
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Success", response = VerefooController.class),
			@ApiResponse(code = 401, message = "Unauthorized"), @ApiResponse(code = 403, message = "Forbidden"),
			@ApiResponse(code = 404, message = "Not Found"), @ApiResponse(code = 500, message = "Failure") })
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
		return new ResponseEntity<NFV>(test.getResult(), HttpStatus.CREATED);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public String infoVerifoo() {
		System.out.println("Info from Verifoot");
		return "hi";
	}

}
