package es.unizar.iaaa.ml.distance;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * This class contains the tests for the euclidean distance.
 *
 * @author Javier Beltran
 */
public class EuclideanDistanceTest extends DistanceTest {

    private EuclideanDistance euclidean;

    @Before
    public void setupDistance() {
        euclidean = new EuclideanDistance();
    }

    /**
     * Unit test that checks that features with different feature types have an infinite distance.
     */
    @Test
    public void testDifferentTypeFeatures() {
        /* Computes the exact Hausdorff distance */
        double distance = euclidean.distance(
                getFeaturePoint("POINT(2 -2)"),
                getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))"));

		/*
         * Distance should be POSITIVE_INFINITY as features 1 and 2 have
		 * different feature types.
		 */
        assertEquals(Double.POSITIVE_INFINITY, distance, 0);
    }

    /**
     * Unit test between point geometries.
     */
    @Test
    public void testPoints() {
		/* Computes the exact Hausdorff distance */
        double distance = euclidean.distance(
                getFeaturePoint("POINT(2 -2)"),
                getFeaturePoint("POINT(1 0)"));

        assertEquals(Math.sqrt(5), distance, 0);
    }

    /**
     * Unit test between line strings.
     */
    @Test
    public void testLineStrings() {
		/* Computes the exact Hausdorff distance */
        double distance = euclidean.distance(
                getFeatureLineString("LINESTRING(0 0, 1 0, 1 1)"),
                getFeatureLineString("LINESTRING(0 0, 0 1, 1 1)"));

        assertEquals(Math.sqrt(0.5), distance, 0);
    }

    /**
     * Unit test between polygons.
     */
    @Test
    public void testPolygons() {
		/* Computes the exact Hausdorff distance */
        double distance = euclidean.distance(
                getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))"),
                getFeaturePolygon("POLYGON((-1 0, -2 0, -1 1, -1 0))"));

        assertEquals(1.666, distance, 0.001);
    }

}
