package it.polito.verefoo.rest.spring;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.tags.Tags;

@RestController
public class InfoVerefooV2 {
	
	@Tags({@Tag(name = "version 2")})
	@Hidden

    @RequestMapping(method = RequestMethod.GET, value = "/v2")
	public String infoVerefoo() {
		return "<h1>Verefoo Spring Boot REST APIs</h1>\r\n" + 
		"<p>Verefoo allows to run simulations on network topologies by performing the following three steps:</p>\r\n" + 
		"<ol>\r\n" + 
		"<li>Store and retrieve Service Graphs and Allocation Graphs, along with their constraints and network security requirements to satisfy;</li>\r\n" + 
		"<li>Store and retrieve the Substrate Networks where VNFs can be deployed;</li>\r\n" + 
		"<li>Run simulations by specifing the graphs and the associated data: the substrates and the usable network functions for each graph. It is possible to retrieve the results of past simulations also in a second moment.</li>\r\n" + 
		"</ol>\r\n" + 
		"<p>For each of the three steps mentioned above, Verefoo furnishes three groups of REST APIs, whose documentation can be accessed from <a href=\"./swagger-ui.html \">here</a>.</p>\r\n";
	}
}
