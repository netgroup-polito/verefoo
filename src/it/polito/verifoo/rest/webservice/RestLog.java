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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;



@Path("/log")
public class RestLog {
		private static final org.apache.logging.log4j.Logger LOG = LogManager.getLogger("mylog");

		private static String getLoggerFileName(String logname) {
		  Logger loggerImpl =  (Logger) LOG;
		  Appender appender = loggerImpl.getAppenders().get(logname);
		  return ((FileAppender) appender).getFileName();
		}
		private static String DebugFile=RestLog.getLoggerFileName("DebugFile");
		private static String ErrorFile=RestLog.getLoggerFileName("ErrorFile");
		
	    @GET
		@Produces(MediaType.TEXT_HTML)
	    public String get() throws IOException {
	    	String debug=Files.readAllLines(Paths.get(DebugFile)).stream().collect(Collectors.joining("</br>"));
			String error=Files.readAllLines(Paths.get(ErrorFile)).stream().collect(Collectors.joining("</br>"));
	    	String html="<!DOCTYPE html><html><body><h1>Error Log</h1><p>"
	    			    +error
	    			    +"</p><h1>Debug Log</h1><p>"
	    			    +debug
	    			    +"</p></body></html>";
			return html;
	    }
}