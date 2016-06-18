package es.unizar.iaaa.ml.util;

import com.vividsolutions.jts.algorithm.distance.DiscreteHausdorffDistance;
import com.vividsolutions.jts.geom.Geometry;

/**
 * This class provides access to algorithms and methods implemented by the
 * JTS library.
 * 
 * @author Javier Beltran
 */
public class JTS {

	/**
	 * Calculates the discrete hausdorff distance between two geometries.
	 * 
	 * @param a a geometry.
	 * @param b another geometry.
	 * @return the discrete hausdorff distance between them.
	 */
    public static double discreteHausdorffDistance(Geometry a, Geometry b) {
        return DiscreteHausdorffDistance.distance(a, b);
    }
}
