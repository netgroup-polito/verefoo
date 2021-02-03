package it.polito.verefoo.rest.spring.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

// swagger can be accessed at http://localhost:8085/verefoo/swagger-ui.html 

@RestController
public class VerefooController {
	
	@Hidden
	@Operation(summary = "infoVerefoo", description = "Info on Verefoo")
	@RequestMapping(method = RequestMethod.GET, value = "/")

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
