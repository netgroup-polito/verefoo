package it.polito.escape.verify.model;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//import org.apache.log4j.Logger;

/**
 * Helper class for tomcat (or another web container).
 * 
 * Loads DLLs specified in dll-bootstrapper.properties into the common/shared classloader.
 * 
 * 
 * If deploying with Tomcat, this jar should exist in TOMCAT_HOME/lib
 * The dll-bootstrapper.properties file should exist in TOMCAT_HOME/lib
 * 
 * @author msm336
 */
public class DLLBootstrapper {
//    static Logger logger = Logger.getLogger(DLLBootstrapper.class);

	static {
		try {
			
			Properties prop = new Properties();
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("dll-bootstrapper.properties");
			prop.load(in);
			in.close();
			
			int count = 0;
			// iterate through all the dlls specified
			for (int i=0; i < 100; i++) {
				String dll = prop.getProperty("dll." + i);
				if (dll != null && !"".equals(dll)) {
					try {
						// try to load this library..
						System.loadLibrary(dll);
						System.out.println(dll + " DLL loaded (dll." + i + ")");
						count ++;
					} catch (UnsatisfiedLinkError ule) {
						System.err.println("Failed to load '" + dll + "' DLL (dll." + i + ")");
						ule.printStackTrace(System.err);
					}
				}
			}
			if (count == 0) {
				System.out.println("No DLLs specified.  Please create a dll-bootstrapper.properties file and specify DLLs to load.");
				System.out.println("  Format: dll.#=<dll-name>");
				System.out.println("  Example:");
				System.out.println("    dll.0=libsndfile-1");
				System.out.println("    dll.1=libsndfile_wrap");
			} else {
				System.out.println(count + " DLLs loaded.");
			}
		} catch (IOException ioe) {
			System.err.println("Failed to parse properties file: dll-bootstrapper.properties");
			ioe.printStackTrace(System.err);
		}
	}
	

	/**
	 * Required for JDK 6/7.  Without it, the static initializer above wouldn't execute.
	 * @param args
	 */
	public static void main(String args[]) {
		System.out.println("DLLBootstrapper.main(args)");
	}

}
