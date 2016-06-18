package es.unizar.iaaa.ml.distance;

import com.vividsolutions.jts.geom.Coordinate;

import java.util.Collections;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Attribute;
import es.unizar.iaaa.ml.parameter.GeomAttr;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.getAttr;

/**
 * An implementation of the HausdorffDistance. Unlike DiscreteHausdorffDistance, 
 * whose results are approximate, this implementation gives exact results.
 *
 * @author Javier Beltran
 */
public class ExactHausdorffDistance implements DistanceMeasure {

    /**
     * Computes the Hausdorff Distance between features a and b, defined as the maximum between the
     * directed distance in both directions.
     *
     * @param a the first feature.
     * @param b the second feature.
     * @return the distance between a and b, with double precision.
     * @throws ParameterNotFoundException when no attribute is specified.
     */
    public double distance(Clusterable a, Clusterable b, Parameter... params) throws ParameterNotFoundException {
    	Attribute attr = getAttr(params, Attribute.class);
    	if (attr instanceof GeomAttr) {
    		return Math.max(directedDistance(a, b), directedDistance(b, a));
    	} else return Double.POSITIVE_INFINITY;
    }

    /**
     * Computes the Directed Hausdorff Distance from feature a to b.
     *
     * @param a the origin feature.
     * @param b the destination feature.
     * @return the distance from a to b, with double precision.
     */
    private double directedDistance(Clusterable a, Clusterable b) {
        if (a.isComparableWith(b)) {
            if (a.isSame(b)) {
                return 0.0;
            } else {
                final List<Coordinate> pointsA = a.getAttributeList(Clusterable.Property.REPRESENTATIVE_COORDINATES, Coordinate.class);
                final List<Coordinate> pointsB = b.getAttributeList(Clusterable.Property.REPRESENTATIVE_COORDINATES, Coordinate.class);
                Collections.shuffle(pointsA);
                Collections.shuffle(pointsB);

                double cMax = 0.0;
                for (Coordinate cA : pointsA) {
                    double cMin = Double.POSITIVE_INFINITY;

                    for (Coordinate cB : pointsB) {
                        double d = cA.distance(cB);

						/* Saves the minimum distance from cA to any cB */
                        if (d < cMin) {
                            cMin = d;
                        }

						/* Early Break */
                        if (d < cMax) {
                            break;
                        }
                    }

					/* Saves the maximum of the minimum distances found */
                    if (cMin > cMax) {
                        cMax = cMin;
                    }
                }
                return cMax;
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
