package it.polito.verifoo.rest.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.glassfish.jersey.server.ResourceConfig;

public class RestFooApplication extends ResourceConfig {
    public RestFooApplication() {
    	try {
			Process proc=Runtime.getRuntime().exec("lsb_release -d -s");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String distro=stdInput.readLine();
			System.err.println(distro);
			if(distro.contains("Ubuntu")){
				System.err.println("It's looklike ubuntu");
				int ret=Runtime.getRuntime().exec("tar -xvzpf apps/verifoo.war/WEB-INF/lib/jni/ubuntu64.tar.gz -C apps/verifoo.war/WEB-INF/lib/jni").waitFor();
				System.out.println("Unzip return with code:"+ret);
			}
		} catch (IOException | InterruptedException e) {
			// Probably Windows :D
		}
    	
        String javaLibPath = System.getProperty("java.library.path");
        System.err.println(javaLibPath);
        // Define the package which contains the service classes.
        packages("it.polito.verifoo.rest.webservice");
    }
}
