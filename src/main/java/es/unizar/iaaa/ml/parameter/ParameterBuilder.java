package es.unizar.iaaa.ml.parameter;

import java.util.ArrayList;
import java.util.List;

/**
 * A parameter builder allows the creation of different types of parameters. It
 * also contains solutions for retrieving specific parameters from a parameter
 * list passed in a clustering algorithm.
 * 
 * @author Javier Beltran
 */
public class ParameterBuilder {
	
	public static Attribute geom() {
		return new GeomAttr();
	}
	
	public static Attribute geom(String name) {
		return new GeomAttr(name);
	}
	
	public static Attribute integer(String name) {
		return new IntegerAttr(name);
	}
	
	/**
	 * Given an array of parameters, returns the one of a given class. If two
	 * or more parameters share the same class, it only returns the first one,
	 * in order of occurrence.
	 * 
	 * @param params the array of params.
	 * @param type the class type.
	 * @return the first param of the specified class.
	 * @throws ParameterNotFoundException when no parameter matches that class.
	 */
	public static Parameter getParam(Parameter[] params, Class<?> type) 
			throws ParameterNotFoundException {
		int i=0;
		boolean found = false;
		while (i < params.length && !found) {
			if (type.isInstance(params[i])) {
				found = true;
			}
			i++;
		}
		if (found) {
			return params[i-1];
		} else {
			throw new ParameterNotFoundException();
		}
	}
	
	/**
	 * Given an array of parameters, true if one of them has a specified type. 
	 * 
	 * @param params the array of params.
	 * @param type the class type.
	 * @return true if one param has the specified type, false otherwise.
	 */
	public static boolean hasParam(Parameter[] params, Class<?> type) {
		try {
			getParam(params, type);
			return true;
		} catch(ParameterNotFoundException e) {
			return false;
		}
	}
	
	/**
	 * Given an array of parameters, returns those of a given class in a list.
	 * 
	 * @param params the array of params.
	 * @param type the class type.
	 * @return a list with those params of the specified class.
	 * @throws ParameterNotFoundException when no parameter matches that class.
	 */
	public static List<Parameter> getParams(Parameter[] params, Class<?> type) 
			throws ParameterNotFoundException {
		List<Parameter> list = new ArrayList<>();
		for (Parameter param : params) {
			if (param.getClass() == type) {
				list.add(param);
			}
		}
		if (list.size() > 0) {
			return list;
		} else {
			throw new ParameterNotFoundException();
		}
	}
	
	/**
	 * Given an array of parameters, returns the one of a given attribute type. 
	 * If two or more attributes share the same class, it only returns the first 
	 * one, in order of occurrence.
	 * 
	 * @param params the array of params.
	 * @param type the class type.
	 * @return the first attribute of the specified class.
	 * @throws ParameterNotFoundException when no attribute matches that class.
	 */
	public static Attribute getAttr(Parameter[] params, Class<? extends Attribute> type) 
			throws ParameterNotFoundException {
		return (Attribute) getParam(params, type);
	}

}
