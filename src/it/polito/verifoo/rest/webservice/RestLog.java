package it.polito.verifoo.rest.webservice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;



@Path("/log")
public class RestLog {
	    @GET
		@Produces(MediaType.TEXT_HTML)
	    public String get() throws IOException {
	    	String debug=Files.readAllLines(Paths.get("log/debug.log")).stream().sorted(Comparator.reverseOrder()).collect(Collectors.joining("</br>"));
			String error=Files.readAllLines(Paths.get("log/error.log")).stream().sorted(Comparator.reverseOrder()).collect(Collectors.joining("</br>"));
	    	String html="<!DOCTYPE html><html><body><h1>Error Log</h1><p>"
	    			    +error
	    			    +"</p><h1>Debug Log</h1><p>"
	    			    +debug
	    			    +"</p></body></html>";
			return html;
	    }
}