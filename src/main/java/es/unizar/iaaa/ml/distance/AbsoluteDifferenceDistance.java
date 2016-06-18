package es.unizar.iaaa.ml.distance;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.parameter.Attribute;
import es.unizar.iaaa.ml.parameter.NumberAttr;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.getAttr;
import static es.unizar.iaaa.ml.parameter.ParameterBuilder.hasParam;

/**
 * The absolute difference distance is a one-dimensional distance measure. It
 * is the difference between two numerical values, in absolute value.
 * 
 * @author Javier Beltran
 */
public class AbsoluteDifferenceDistance implements DistanceMeasure {

	/**
	 * Given two clusterable elements and a parameter they both contain,
	 * calculates the absolute difference distance between both elements.
	 * 
	 * @param a one element.
	 * @param b the other element.
	 * @param params a list of parameters. Should contain the comparing one.
	 * @return the distance between a and b.
	 */
	public double distance(Clusterable a, Clusterable b, Parameter... params) throws ParameterNotFoundException {
		if (hasParam(params, NumberAttr.class)) {
			Attribute attr = getAttr(params, NumberAttr.class);

			long valueA = (long) a.getAttribute(attr.getName(), Integer.class);
			long valueB = (long) b.getAttribute(attr.getName(), Integer.class);
			
			return Math.abs(valueA - valueB);
		} else {
			throw new ParameterNotFoundException();
		}
	}

}
