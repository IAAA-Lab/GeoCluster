package es.unizar.iaaa.ml.validation;

/**
 * This exception is thrown when an annotations validator is trying to validate
 * a feature that has not been annotated.
 * 
 * @author Javier Beltran
 */
public class FeatureNotAnnotatedException extends Exception {

	private static final long serialVersionUID = 1L;

	public FeatureNotAnnotatedException() {
		super();
	}
	
	public FeatureNotAnnotatedException(String msg) {
		super(msg);
	}
	
}
