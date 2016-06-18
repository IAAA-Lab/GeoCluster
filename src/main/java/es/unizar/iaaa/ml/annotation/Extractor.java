package es.unizar.iaaa.ml.annotation;

import java.io.IOException;

import es.unizar.iaaa.ml.adapter.Clusterable;

/**
 * An extractor allows name recognition by searching in a data collection (for
 * example, a csv file) and extracting the names in it that are related to a
 * feature.
 * 
 * @author Javier Beltran
 */
public interface Extractor {
	
	/**
	 * Given a clusterable element and the name of one of its fields, uses the 
	 * value of that field to extract names.
	 * 
	 * @param feature the feature we are working with.
	 * @param field the name of the field of the feature that we are using.
	 */
	String extract(Clusterable feature, String field) throws IOException;
	
}
