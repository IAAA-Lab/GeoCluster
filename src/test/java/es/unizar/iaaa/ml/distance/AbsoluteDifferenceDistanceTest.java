package es.unizar.iaaa.ml.distance;

import org.junit.BeforeClass;
import org.junit.Test;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.integer;
import static org.junit.Assert.assertEquals;

/**
 * This class contains the unit tests that check the correctness of Absolute
 * Difference Distance implementation.
 * 
 * @author Javier Beltran
 */
public class AbsoluteDifferenceDistanceTest extends DistanceTest {
	
	private static AbsoluteDifferenceDistance absDistance;
	
	@BeforeClass
	public static void setupDistance() {
		absDistance = new AbsoluteDifferenceDistance();
	}
	
	/**
	 * Checks distance between 2 positive integers.
	 */
	@Test
	public void testPositiveValues() throws ParameterNotFoundException {
		testValues(4, 2, 2);
	}
	
	/**
	 * Checks distance between 2 negative integers.
	 */
	@Test
	public void testNegativeValues() throws ParameterNotFoundException {
		testValues(-3, -6, 3);
	}
	
	/**
	 * Checks distance between a positive and a negative integer.
	 */
	@Test
	public void testPosNegValues() throws ParameterNotFoundException {
		testValues(5, -10, 15);
	}
	
	private void testValues(int a, int b, int res) throws ParameterNotFoundException {
		Clusterable f1 = getFeaturePointAndInteger("POINT (2 0)", a);
		Clusterable f2 = getFeaturePointAndInteger("POINT (3 1)", b);
		
		double d = absDistance.distance(f1, f2, integer("number"));
		
		assertEquals(res, d, 0);
	}
	
}
