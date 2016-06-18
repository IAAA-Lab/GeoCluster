package es.unizar.iaaa.ml.distance;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * A builder for creating distance measures with static methods.
 *
 * @author Javier Beltran
 */
public class DistanceMeasureBuilder {

    /**
     * Creates a DiscreteHausdorffDistance object.
     *
     * @return a DiscreteHausdorffDistance object.
     */
    public static DiscreteHausdorffDistance discreteHausdorff() {
        return new DiscreteHausdorffDistance();
    }

    /**
     * Creates an ExactHausdorffDistance object.
     *
     * @return an ExactHausdorffDistance object.
     */
    public static ExactHausdorffDistance exactHausdorff() {
        return new ExactHausdorffDistance();
    }

    /**
     * Calculates the DiscreteHausdorffDistance between two features.
     *
     * @return the distance between features a and b.
     */
    public static double discreteHausdorffDistance(Clusterable a, Clusterable b, Parameter... params)
    		throws ParameterNotFoundException {
        return discreteHausdorff().distance(a, b, params);
    }

    /**
     * Calculates the ExactHausdorffDistance between two features.
     *
     * @return the distance between features a and b.
     */
    public static double exactHausdorffDistance(Clusterable a, Clusterable b, Parameter... params)
    		throws ParameterNotFoundException {
        return exactHausdorff().distance(a, b, params);
    }

}
