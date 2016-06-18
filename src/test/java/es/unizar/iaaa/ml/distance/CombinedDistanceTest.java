package es.unizar.iaaa.ml.distance;

import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static es.unizar.iaaa.ml.parameter.ParameterBuilder.integer;
import static org.junit.Assert.assertEquals;

/**
 * This class contains the unit tests that check the correctness of the combined
 * distance implementation.
 * 
 * @author Javier Beltran
 */
public class CombinedDistanceTest extends DistanceTest {
	
	private static List<DistanceMeasure> distances;
	
	@BeforeClass
	public static void setupDistance() {
		EuclideanDistance euclidean = new EuclideanDistance();
		AbsoluteDifferenceDistance absolute = new AbsoluteDifferenceDistance();
		
		distances = new ArrayList<>();
		distances.add(euclidean);
		distances.add(absolute);
	}
	
	/**
	 * Checks a standard case of use where combined distance is built from an
	 * euclidean distance evaluated over point geometries, and an absolute diff
	 * distance evaluated over integers.
	 */
	@Test
	public void testGeneralCase() throws ParameterNotFoundException {
		CombinedDistance combined =
				new CombinedDistance(distances, Arrays.asList(0.4, 0.6));
		
		Clusterable a = getFeaturePointAndInteger("POINT (0 0)", 12);
		Clusterable b = getFeaturePointAndInteger("POINT (4 3)", 4);
		
		double d = combined.distance(a, b, geom(), integer("number"));
		assertEquals(6.8, d, 0);
	}
	
	/**
	 * Checks the case where one distance has weight 0 and one distance has
	 * weight 1. Should return the same distance as a standard distance.
	 */
	@Test
	public void testWeightsAreOneAndZero() throws ParameterNotFoundException {
		CombinedDistance combined =
				new CombinedDistance(distances, Arrays.asList(0.0, 1.0));
		AbsoluteDifferenceDistance absolute = new AbsoluteDifferenceDistance();
		
		Clusterable a = getFeaturePointAndInteger("POINT (0 0)", 12);
		Clusterable b = getFeaturePointAndInteger("POINT (4 3)", 4);
		
		double distance = combined.distance(a, b, geom(), integer("number"));
		double expected = absolute.distance(a, b, integer("number"));
		assertEquals(expected, distance, 0);
	}
	
}
