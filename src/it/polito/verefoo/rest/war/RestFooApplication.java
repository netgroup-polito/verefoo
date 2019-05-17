package it.polito.verefoo.rest.war;

import java.io.File;
import java.net.MalformedURLException;

import javax.annotation.PostConstruct;
import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;

import io.swagger.jaxrs.config.BeanConfig;
/**
 * Main Rest Class
 */
public class RestFooApplication extends ResourceConfig {
	private String contextpath;
	/**
	 * Constructor of Main RestClass
	 * @param context Servlet Context
	 * @throws MalformedURLException Log4j Configuration File not found.
	 */
    public RestFooApplication(@Context ServletContext context) throws MalformedURLException {
		System.setProperty("log4j.configuration", new File(context.getRealPath("/WEB-INF/classes/log4j2.xml")).toURI().toURL().toString());
		String fullPath = context.getRealPath("/WEB-INF/lib/jni/");
    	JniFinder.extractZ3Lib(fullPath);
        // Define the package which contains the service classes.
    	this.contextpath=context.getContextPath();
    	register(io.swagger.jaxrs.listing.ApiListingResource.class); 
        register(io.swagger.jaxrs.listing.SwaggerSerializers.class);
        packages("it.polito.verifoo.rest.webservice");
    }
    @PostConstruct
    /**
     * Initializes Swagger Configuration
     */
    public void initializeSwaggerConfiguration() {
        BeanConfig config = new BeanConfig();
        config.setTitle("Restfoo");
        config.setDescription("Verifoo Rest Service");
        config.setVersion("1.0");
        if(contextpath!=null || !contextpath.isEmpty()){
            config.setBasePath(contextpath+"/rest");
        }else{
        	config.setBasePath("/rest");
        }
        config.setResourcePackage("it.polito.verifoo.rest.webservice");
        config.setScan(true);
    }
    
}
