package it.polito.verifoo.rest.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;

public final class JniFinder {

	 public static void extractZ3Lib(String fullPath){
	    	try {
				Process proc=Runtime.getRuntime().exec("lsb_release -d -s");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(proc.getInputStream()));
				String distro=stdInput.readLine();
				System.out.println(distro);
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
	    private static void setJavaLibPath(String path){
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
