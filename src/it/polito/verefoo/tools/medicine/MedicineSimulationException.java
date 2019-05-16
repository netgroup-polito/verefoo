package it.polito.verefoo.tools.medicine;

/**
 * Exception thrown by the MeDICINE simulator classes
 * @author Antonio
 *
 */
public class MedicineSimulationException extends ResourceModelException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8162686603935202018L;
	
	public MedicineSimulationException() {
		super();
	}

	public MedicineSimulationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public MedicineSimulationException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public MedicineSimulationException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public MedicineSimulationException(String message) {
		super(message);
	}
}
