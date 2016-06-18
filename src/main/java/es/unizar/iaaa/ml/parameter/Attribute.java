package es.unizar.iaaa.ml.parameter;

/**
 * An attribute is a specific kind of parameter that defines a feature attribute
 * to be used for running the clustering algorithm and calculate the distances
 * with that attribute.
 * 
 * @author Javier Beltran
 *
 */
public abstract class Attribute extends Parameter {
	
	String name;
	
	public Attribute(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
