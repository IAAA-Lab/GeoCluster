package es.unizar.iaaa.ml.parameter;

/**
 * A GeomAttr is a geometry attribute, that is, an attribute representing the
 * geometry of a feature.
 * 
 * @author Javier Beltran
 *
 */
public class GeomAttr extends Attribute {
	
	public GeomAttr() {
		super("geom");
	}
	
	public GeomAttr(String name) {
		super(name);
	}
	
}
