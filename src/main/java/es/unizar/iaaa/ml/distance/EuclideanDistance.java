package es.unizar.iaaa.ml.distance;

import com.vividsolutions.jts.geom.Point;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Parameter;

/**
 * Implementation of the Euclidean Distance between features. As it is defined 
 * as a point-to-point distance, there needs to be a representative point for 
 * every kind of geometry. When calculating distance between point geometries, 
 * the representative point is itself. For line geometries, the mean point of 
 * the segment is used. For polygon geometries, the centroid is used.
 *
 * @author Javier Beltran
 */
public class EuclideanDistance implements DistanceMeasure {

    /**
     * Computes the euclidean distance between features a and b.
     *
     * @param a a feature.
     * @param b a feature.
     * @return the distance between a and b, with double precision.
     */
    public double distance(Clusterable a, Clusterable b, Parameter... params) {

        if (a.isComparableWith(b)) {
            if (a.isSame(b)) {
                return 0.0;
            } else {
                return a.getAttribute(Clusterable.Property.REPRESENTATIVE_POINT, Point.class).
                        distance(b.getAttribute(Clusterable.Property.REPRESENTATIVE_POINT, Point.class));
            }
        } else {
            /*
             * Features of distinct type are incomparable, so their distance is
			 * set to infinity.
			 */
            return Double.POSITIVE_INFINITY;
        }
    }

}
