package es.unizar.iaaa.ml.distance;

import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * A CombinedDistance is defined as the linear combination of multiple distance
 * measures. That is, it's calculated as d = d1*w1 + d2*w2 + d3*w3..., where the
 * user specifies the distances d1..dn and weights w1..wn.
 * 
 * @author Javier Beltran
 */
public class CombinedDistance implements DistanceMeasure {

	private List<DistanceMeasure> distances;
	private List<Double> weights;
	
	public CombinedDistance(List<DistanceMeasure> distances, List<Double> weights) {
		this.distances = distances;
		this.weights = weights;
	}
	
	/**
	 * Computes the combined distance specified when constructing the object,
	 * between features a and b.
	 * 
	 * @param a one feature.
	 * @param b another feature.
	 * @param params the parameters for the algorithm.
	 * @return the combined distance between a and b.
	 */
	public double distance(Clusterable a, Clusterable b, Parameter... params) throws ParameterNotFoundException {
		double dist = 0;
		for (int i=0; i<distances.size(); i++) {
			dist += distances.get(i).distance(a, b, params[i]) * weights.get(i);
		}
		return dist;
	}
	
}
