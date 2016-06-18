package es.unizar.iaaa.ml.annotation;

import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.IOException;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;

/**
 * A feature annotator takes a feature and obtains its corresponding annotations
 * from a specified extractor.
 * 
 * @author Javier Beltran
 */
public class FeatureAnnotator {
	
	private Extractor extractor;
	
	public FeatureAnnotator(Extractor extractor) {
		this.extractor = extractor;
	}
	
	/**
	 * Given a SimpleFeature and the name of one of its fields, annotates the
	 * feature by extracting information from the specified field, using the
	 * extractor.
	 * 
	 * @param feature the feature to be annotated.
	 * @param field the field (attribute) to take the info from.
	 * @return the same feature with a new annotation field, containing a JSON
	 * representation of the annotations found.
	 */
	public Clusterable annotate(Clusterable feature, String field) throws IOException {
		/* Builds a new feature type including the annotations field */
		SimpleFeatureType original = feature.getAdaptee(SimpleFeature.class).getFeatureType();
		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
		b.setName(original.getName());
		b.addAll(original.getAttributeDescriptors());
		b.add("annotation",String.class);
		SimpleFeatureType enhanced = b.buildFeatureType();
		
		String annotations = buildAnnotations(feature, field);
		
		/* Builds a new feature with the previous fields and the annotations */
		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(enhanced);
		featureBuilder.addAll(feature.getAdaptee(SimpleFeature.class).getAttributes());
		featureBuilder.add(annotations);
		return new SimpleFeatureClusterable(featureBuilder.buildFeature(null));
	}
	
	/**
	 * Given a SimpleFeature and the name of one of its fields, obtains the
	 * annotations of the feature by extracting information from the specified
	 * field, using the extractor.
	 * 
	 * @param feature the feature to be annotated.
	 * @param field the field (attribute) to take the info from.
	 * @return a JSON representation of the annotations found.
	 */
	public String buildAnnotations(Clusterable feature, String field) throws IOException {
		return extractor.extract(feature, field);
	}
	
}
