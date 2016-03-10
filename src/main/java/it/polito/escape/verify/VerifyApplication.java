package it.polito.escape.verify;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import io.swagger.jaxrs.config.BeanConfig;


//@ApplicationPath("")
public class VerifyApplication extends Application{
	
	public VerifyApplication() {
		BeanConfig beanConfig = new BeanConfig();
        beanConfig.setVersion("1.0.2");
        beanConfig.setSchemes(new String[]{"http"});
        beanConfig.setHost("localhost:8090");
        beanConfig.setBasePath("/verify");
        beanConfig.setResourcePackage("it.polito.escape.verify.resources");
        beanConfig.setScan(true);
	}
	
	@Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> resources = new HashSet<>();

        resources.add(it.polito.escape.verify.resources.NodeResource.class);
        resources.add(it.polito.escape.verify.resources.NeighbourResource.class);
        resources.add(it.polito.escape.verify.resources.VerificationResource.class);
        

        resources.add(io.swagger.jaxrs.listing.ApiListingResource.class);
        resources.add(io.swagger.jaxrs.listing.SwaggerSerializers.class);

        return resources;
    }
}
