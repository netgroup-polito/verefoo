package it.polito.verefoo.rest.war;

import java.nio.file.Paths;
import java.util.stream.Collectors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.appender.FileAppender;

import io.swagger.annotations.*;

/**
 * This class implements the web service that deals with the log request (present only for debugging purposes)
 *
 */

@Path("/log")
@Api(value = "/log")

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
	    @ApiOperation(value = "Get Verifoo Log", notes = "Get the last 200 log lines of Verifoo Processing"
	    		)
   	    @ApiResponses(value = {
	    		@ApiResponse(code = 200, message = "OK"),
	    		@ApiResponse(code = 415, message = "Invalid Media Type")
	    		})

		@Produces(MediaType.TEXT_HTML)
	    public String get(){
	    	try {
				String debug=LogReader.get(Paths.get(DebugFile),200).stream().collect(Collectors.joining("</br>"));
				String error=LogReader.get(Paths.get(ErrorFile),200).stream().collect(Collectors.joining("</br>"));
				String html="<!DOCTYPE html><html><body><h1>Error Log</h1><p>"
						    +error
						    +"</p><h1>Debug Log</h1><p>"
						    +debug
						    +"</p></body></html>";
				return html;
			} catch (Exception e) {
				System.out.println("Still no log created");
			}
			return "Still no log created";
	    }
}