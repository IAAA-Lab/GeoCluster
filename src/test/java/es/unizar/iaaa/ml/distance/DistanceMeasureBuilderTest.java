package es.unizar.iaaa.ml.distance;

import org.junit.Before;
import org.junit.Test;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.distance.DistanceMeasureBuilder.discreteHausdorffDistance;
import static es.unizar.iaaa.ml.distance.DistanceMeasureBuilder.exactHausdorffDistance;
import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static org.junit.Assert.assertEquals;

/**
 * Test for checking the correctness of the Distance Measure Builder.
 *
 * @author Javier Beltran
 */
public class DistanceMeasureBuilderTest extends DistanceTest {

    private ExactHausdorffDistance exactDistance;
    private DiscreteHausdorffDistance discreteDistance;
    private Clusterable feature1;
    private Clusterable feature2;

    @Before
    public void setupDistance() {
        exactDistance = new ExactHausdorffDistance();
        discreteDistance = new DiscreteHausdorffDistance();
        feature1 = getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))");
        feature2 = getFeaturePolygon("POLYGON((-1 0, -2 0, -1 1, -1 0))");
    }

    /**
     * Unit test that checks whether the exact hausdorff distance returned by the builder is the
     * same as the one returned by itself.
     */
    @Test
    public void testExactHausdorffDistance() throws ParameterNotFoundException {
        /* Computes the exact Hausdorff distance without using the builder */
        double distance = exactDistance.distance(feature1, feature2, geom());

        /* Computes the exact Hausdorff distance using the builder */
        double builderDistance = exactHausdorffDistance(feature1, feature2, geom());

		/* Both methods should return the same value */
        assertEquals(builderDistance, distance, 0);
    }

    /**
     * Unit test that checks whether the discrete hausdorff distance returned by the builder is the
     * same as the one returned by itself.
     */
    @Test
    public void testDiscreteHausdorffDistance() throws ParameterNotFoundException {
        /* Computes the discrete Hausdorff distance without using the builder */
        double distance = discreteDistance.distance(feature1, feature2, geom());
        
        /* Computes the discrete Hausdorff distance using the builder */
        double builderDistance = discreteHausdorffDistance(feature1, feature2, geom());

		/* Both methods should return the same value */
        assertEquals(builderDistance, distance, 0);
    }

}
