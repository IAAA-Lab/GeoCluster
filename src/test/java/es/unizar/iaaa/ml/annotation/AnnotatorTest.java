package es.unizar.iaaa.ml.annotation;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.BeforeClass;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;

/**
 * This class contains the common methods for testing feature annotators.
 * 
 * @author Javier Beltran
 */
public abstract class AnnotatorTest {
	
	private static SimpleFeatureBuilder testPointType;
	private static SimpleFeatureBuilder testPolygonType;

	protected static String NGCEv2012_CSV = AnnotatorTest.class.getResource("/NGCEv2012.csv").getFile();

	@BeforeClass
    public static void setup() throws SchemaException {
        /* Creates the feature type builders */
		testPointType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "TestFeature",
                        "geom:Point,city:String,country:String"));
		
		testPolygonType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "TestFeature",
                        "geom:Polygon,city:String,country:String"));
    }
	
	Clusterable getPointFeature(String wkt, String city, String country) {
		testPointType.add(wkt);
		testPointType.add(city);
		testPointType.add(country);
		return new SimpleFeatureClusterable(testPointType.buildFeature(null));
    }
	
	Clusterable getPolygonFeature(String wkt, String city, String country) {
		testPolygonType.add(wkt);
		testPolygonType.add(city);
		testPolygonType.add(country);
		return new SimpleFeatureClusterable(testPolygonType.buildFeature(null));
	}
	
}
