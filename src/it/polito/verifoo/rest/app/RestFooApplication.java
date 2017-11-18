package it.polito.verifoo.rest.app;

import org.glassfish.jersey.server.ResourceConfig;

public class RestFooApplication extends ResourceConfig {
    public RestFooApplication() {
        // Define the package which contains the service classes.
        packages("it.polito.verifoo.rest.webservice");
    }
}
