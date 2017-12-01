package it.polito.verifoo.rest.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.MalformedURLException;

import javax.servlet.ServletContext;
import javax.ws.rs.core.Context;

import org.glassfish.jersey.server.ResourceConfig;

public class RestFooApplication extends ResourceConfig {
    public RestFooApplication(@Context ServletContext context) throws MalformedURLException {
		System.setProperty("log4j.configuration", new File(context.getRealPath("/WEB-INF/classes/log4j2.xml")).toURI().toURL().toString());
    	this.extractZ3Lib(context);
        // Define the package which contains the service classes.
        packages("it.polito.verifoo.rest.webservice");
    }
    private void extractZ3Lib(ServletContext context){
    	try {
			Process proc=Runtime.getRuntime().exec("lsb_release -d -s");
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String distro=stdInput.readLine();
			System.out.println(distro);
			String fullPath = context.getRealPath("/WEB-INF/lib/jni/");
			System.out.println(fullPath);
			if(distro.contains("Ubuntu")){
				int ret=0;
				if(System.getProperty("os.arch").contains("64")){
					System.out.println("It's looklike ubuntu64");
					ret=Runtime.getRuntime().exec("tar -xvzpf "+fullPath+"ubuntu64.tar.gz -C "+fullPath).waitFor();
				}else{
					System.out.println("It's looklike ubuntu32");
					ret=Runtime.getRuntime().exec("tar -xvzpf "+fullPath+"ubuntu32.tar.gz -C "+fullPath).waitFor();
				}
				System.out.println("Unzip return with code:"+ret);
			}else if(distro.contains("Debian")){
				System.out.println("It's looklike debian");
				int ret=Runtime.getRuntime().exec("tar -xvzpf "+fullPath+"debian64.tar.gz -C "+fullPath).waitFor();
				System.out.println("Unzip return with code:"+ret);
			}
	        String javaLibPath = System.getProperty("java.library.path");
	        System.out.println(javaLibPath);
	        if(!javaLibPath.contains(fullPath)){
	        	setJavaLibPath(javaLibPath+";"+fullPath);
	        }
		} catch (IOException | InterruptedException e) {
			// Probably Windows :D
		}
    }
    private void setJavaLibPath(String path){
			try {
				System.setProperty("java.library.path", path );
				Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
				fieldSysPath.setAccessible( true );
				fieldSysPath.set( null, null );
			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
    }
}
