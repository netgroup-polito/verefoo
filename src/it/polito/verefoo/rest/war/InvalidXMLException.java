package it.polito.verefoo.rest.war;
/**
 *  This exception will be thrown by the web service if there is an error in the marshalling or the unmarshalling of a request 
 */
public class InvalidXMLException extends RuntimeException {


	private static final long serialVersionUID = -1171106531665313527L;
	public InvalidXMLException() {
		super();
		// TODO Auto-generated constructor stub
	}
	public InvalidXMLException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
		// TODO Auto-generated constructor stub
	}
	public InvalidXMLException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}
	public InvalidXMLException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	public InvalidXMLException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}
	
}
