package it.polito.escape.verify.model;

public class JavaLibPath {
	 
    public static void main(String[] args) {
        String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
    }
}
