package es.unizar.iaaa.ml.distance;

import com.vividsolutions.jts.geom.Geometry;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.GeomAttr;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;
import es.unizar.iaaa.ml.util.JTS;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.getAttr;

/**
 * The discrete hausdorff distance is an approximate implementation of the
 * hausdorff distance between two point sets.
 * 
 * @author Javier Beltran
 */
public class DiscreteHausdorffDistance implements DistanceMeasure {

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
    public double distance(Clusterable a, Clusterable b, Parameter... params) throws ParameterNotFoundException {
    		getAttr(params, GeomAttr.class);
            return JTS.discreteHausdorffDistance(
                    a.getAttribute(Clusterable.Property.REPRESENTATIVE_GEOMETRY, Geometry.class),
                    b.getAttribute(Clusterable.Property.REPRESENTATIVE_GEOMETRY, Geometry.class));
    }
}
