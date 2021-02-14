package it.polito.verefoo.rest.spring.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;

@Controller
public class VerefooController {
	
	@Hidden
	@Operation(summary = "Online docs of Verefoo", description = "HTML page of http://localhost:8085/verefoo/")
	
	@RequestMapping(method = RequestMethod.GET, value = "/", consumes = {}, produces = MediaType.TEXT_HTML_VALUE)
	public String infoVerefoo() {
		return "VerefooRESTAPIsDocs.html";
	}

}
