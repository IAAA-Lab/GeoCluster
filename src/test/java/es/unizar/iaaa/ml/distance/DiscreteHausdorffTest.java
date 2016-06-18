package es.unizar.iaaa.ml.distance;

import org.junit.Before;
import org.junit.Test;

import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the JTS implementation of the discrete Hausdorff distance.
 *
 * @author Javier Beltran
 */
public class DiscreteHausdorffTest extends DistanceTest {

    private DiscreteHausdorffDistance hausdorff;

    @Before
    public void setupDistance() {
        hausdorff = new DiscreteHausdorffDistance();
    }

    /**
     * Unit test for testing the discrete hausdorff distance between features.
     */
    @Test
    public void testFeatures() throws ParameterNotFoundException {
        /* Computes the exact Hausdorff distance */
        double distance = hausdorff.distance(
                getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))"),
                getFeaturePolygon("POLYGON((-1 0, -2 0, -1 1, -1 0))"),
                geom());

		/* Distance should be 0 as both features are the same thing */
        assertEquals(2, distance, 0);
    }

}
