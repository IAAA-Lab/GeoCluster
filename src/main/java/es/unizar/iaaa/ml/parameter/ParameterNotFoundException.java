package es.unizar.iaaa.ml.parameter;

/**
 * This exception is thrown when a parameter is specified in a clustering
 * algorithm, but it is not found or cannot be used with the features passed.
 * 
 * @author Javier Beltran
 */
public class ParameterNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;
	
	public ParameterNotFoundException() {
		super();
	}
	
	public ParameterNotFoundException(String msg) {
		super(msg);
	}
	
}
