package it.polito.verifoo.rest.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;

public class RestFooApplication extends ResourceConfig {
    public RestFooApplication(@Context ServletContext context) {
    	try {
			Process proc=Runtime.getRuntime().exec("lsb_release -d -s");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String distro=stdInput.readLine();
			System.out.println(distro);
			String fullPath = context.getRealPath("/WEB-INF/lib/jni/");
			System.out.println(fullPath);
			if(distro.contains("Ubuntu")){
				System.out.println("It's looklike ubuntu");
				int ret=Runtime.getRuntime().exec("tar -xvzpf "+fullPath+"ubuntu64.tar.gz -C "+fullPath).waitFor();
				System.out.println("Unzip return with code:"+ret);
			}
		} catch (IOException | InterruptedException e) {
			// Probably Windows :D
		}
    	
        String javaLibPath = System.getProperty("java.library.path");
        System.out.println(javaLibPath);
        // Define the package which contains the service classes.
        packages("it.polito.verifoo.rest.webservice");
    }
}
