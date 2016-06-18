package es.unizar.iaaa.ml.util;

/**
 * This exception is thrown when the converter utility cannot execute a
 * conversion, due to an incorrect format or other reasons.
 * 
 * @author Javier Beltran
 *
 */
public class ConversionException extends Exception {
	
	private static final long serialVersionUID = 1L;

	public ConversionException() {
		super();
	}
	
	public ConversionException(String msg) {
		super(msg);
	}
	
}
