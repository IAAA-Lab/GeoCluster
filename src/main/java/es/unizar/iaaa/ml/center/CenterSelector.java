package es.unizar.iaaa.ml.center;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A center selector provides a representative feature (the center) of a feature
 * collection.
 * 
 * @author Javier Beltran
 */
public interface CenterSelector {
	
	/**
	 * Creates a feature that can be considered the collection center.
	 * 
	 * @param col the collecion of features
	 * @return a new center feature
	 */
	SimpleFeature center(SimpleFeatureCollection col);
	
}
