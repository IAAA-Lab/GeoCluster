package es.unizar.iaaa.ml.util;

import org.junit.Ignore;
import org.junit.Test;

import static es.unizar.iaaa.ml.util.Converter.dmsToDegrees;
import static org.junit.Assert.assertEquals;

/**
 * This class contains the unit tests for the converter methods.
 * 
 * @author Javier Beltran
 */
public class ConverterTest {
	
	/**
	 * Unit test that checks a conversion of a positive dms value to degrees.
	 */
	@Test
	public void testDmsToDegrees() throws ConversionException {
		assertEquals(45.36556, dmsToDegrees("45º21'56\""), 0.00001);
	}
	
	/**
	 * Unit test that checks a conversion of a negative dms value to degrees.
	 */
	@Test
	public void testNegativeDmsToDegrees() throws ConversionException {
		assertEquals(-12.74389, dmsToDegrees("-12º44'38\""), 0.00001);
	}
	
	/**
	 * Unit test that checks ConversionException is thrown when format of dms
	 * is not appropriate.
	 */
	@Test(expected=ConversionException.class)
	public void testConversionExceptionGeneral() throws ConversionException {
		dmsToDegrees("");
	}
	
	/**
	 * Unit test that checks ConversionException is thrown if d, m or s are
	 * not numbers.
	 */
	@Test(expected=ConversionException.class)
	public void testConversionExceptionNoNumbers() throws ConversionException {
		dmsToDegrees("-12ºa'33\"");
	}
	
	// TODO: converter is not made to work with some incomplete formats (0º0")
	
	@Ignore
	@Test
	public void testDmsToDegreesNoSeconds() throws ConversionException {
		assertEquals(-12.73333, dmsToDegrees("-12º44'"), 0.00001);
	}
	
	@Ignore
	@Test
	public void testDmsToDegreesNoMinutes() throws ConversionException {
		assertEquals(-12.01222, dmsToDegrees("-12º44\""), 0.00001);
	}
	
	@Ignore
	@Test
	public void testDmsToDegreesOnlyDegrees() throws ConversionException {
		assertEquals(-12, dmsToDegrees("-12º"), 0.00001);
	}
	
}
