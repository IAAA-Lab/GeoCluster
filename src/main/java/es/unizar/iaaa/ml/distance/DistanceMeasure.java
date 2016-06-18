package es.unizar.iaaa.ml.distance;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * Interface for distance measures between features.
 *
 * @author Javier Beltran
 */
public interface DistanceMeasure {
	
	/**
	 * Given two clusterable elements and a parameter to be used for the
	 * calculation, returns the discrete hausdorff distance between both
	 * elements.
	 * 
	 * @param a one element.
	 * @param b the other element.
	 * @param params a list of parameters. Should contain the comparing one.
	 * @return the distance between a and b.
	 */
    double distance(Clusterable a, Clusterable b, Parameter... params) throws ParameterNotFoundException;

}
