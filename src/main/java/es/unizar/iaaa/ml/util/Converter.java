package es.unizar.iaaa.ml.util;

import static java.lang.Double.parseDouble;
import static java.lang.Math.abs;
import static java.lang.Math.signum;

/**
 * This class contains methods to convert between units.
 * 
 * @author Javier Beltran
 */
public class Converter {
	
	/**
	 * Converts coordinates in format dms (Degrees, Minutes, Seconds) to
	 * coordinates in degrees.
	 * @param dms the coordinates representation in format ddºmm'ss".
	 * @return the coordinates in degrees.
	 */
	public static double dmsToDegrees(String dms) throws ConversionException {
		String[] parts = dms.split("[º'\"]");
		double d, m, s;
		
		if (parts.length < 1) {
			throw new ConversionException();
		}
		try {
			d = parseDouble(parts[0]);
			m = (parts.length < 2) ? 0 : parseDouble(parts[1]);
			s = (parts.length < 3) ? 0 : parseDouble(parts[2]);
		} catch (NumberFormatException e ) {
			throw new ConversionException();
		}
		
		return signum(d) * (abs(d) + (m / 60.0) + (s / 3600.0));
	}
	
}
