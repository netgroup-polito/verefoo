package it.polito.verifoo.rest.app;

import java.io.File;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;
/**
 * Main Rest Class
 */
public class RestFooApplication extends ResourceConfig {
    public RestFooApplication(@Context ServletContext context) throws MalformedURLException {
		System.setProperty("log4j.configuration", new File(context.getRealPath("/WEB-INF/classes/log4j2.xml")).toURI().toURL().toString());
		String fullPath = context.getRealPath("/WEB-INF/lib/jni/");
    	JniFinder.extractZ3Lib(fullPath);
        // Define the package which contains the service classes.
        packages("it.polito.verifoo.rest.webservice");
    }
}
