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
	
	
	@RequestMapping(method = RequestMethod.GET, value = "/")
	@ResponseBody
	public String infoVerefoo() {
		return "<h1 style=\"color: #5e9ca0;\">VEREFOO Spring Boot Rest APIs</h1>\r\n" + 
				"<h2>Some useful features:</h2>\r\n" + 
				"<ol>\r\n" + 
				"<li>&nbsp;Store and retrieve Service Graphs and Allocation Graphs</li>\r\n" + 
				"<li>&nbsp;Store and retrieve the constraints related to a Service Graph</li>\r\n" + 
				"<li>&nbsp;Store and retrieve the Network Security Requirements to satisfy</li>\r\n" + 
				"<li>&nbsp;Store and retrieve the Substrate Networks where VNFs can be deployed</li>\r\n" + 
				"<li>&nbsp;Store and retrieve the Network Security Functions which can be allocated by the framework</li>\r\n" + 
				"<li>&nbsp;Run simulations and retrieve the results</li>\r\n" + 
				"</ol>\r\n" + 
				"<h2 style=\"color: #2e6c80;\">How to use the Rest APIs:</h2>\r\n" + 
				"<p>You can read the Swagger documentation clicking <a href=\"./swagger-ui.html \">HERE</a>&nbsp;</p>\r\n" + 
				"<h2 style=\"color: #2e6c80;\">&nbsp;</h2>\r\n" + 
				"<p>&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;</p>";
	}


}
