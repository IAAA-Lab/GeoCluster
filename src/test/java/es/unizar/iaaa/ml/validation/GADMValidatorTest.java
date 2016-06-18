package es.unizar.iaaa.ml.validation;

import com.google.gson.Gson;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains the unit tests that check the correctness of the GADM
 * validator.
 * 
 * @author Javier Beltran
 */
public class GADMValidatorTest {
	
	private static SimpleFeatureBuilder notAnnotatedType;
	private static SimpleFeatureBuilder annotatedType;
	private static GeometryValidator validator;
	
	// TODO: think of a better way to store these values (txt, properties?)
	private static final String COORDS_SIMILAR_CASE = "POLYGON ((-1.1298 41.5236, -1.1341 41.5249," +
			" -1.1647 41.5250, -1.1621 41.5335, -1.1568 41.5416, -1.1521 41.5436, -1.1447 41.5418, -1.1406 41.5383," +
			" -1.1389 41.5338, -1.1260 41.5309, -1.1277 41.5290, -1.1276 41.5241, -1.1282 41.5230, -1.1298 41.5236))";
	private static final String COORDS_EQUAL_CASE = "POLYGON ((-1.1298470497131348 41.52361297607433," +
			" -1.1341819763183594 41.524986267089844, -1.1647880077361492 41.52505111694347, -1.1621899604796226" +
			" 41.53350067138683, -1.1568020582199097 41.54169845581055, -1.1521919965744019 41.54361724853527," +
			" -1.1447269916534424 41.54182052612316, -1.1406179666519165 41.538330078125, -1.1389249563217163" +
			" 41.533859252929744, -1.126060962677002 41.530998229980526, -1.1277359724044231 41.529045104980526," +
			" -1.1276249885559082 41.524173736572266, -1.1282219886779785 41.523010253906364, -1.1298470497131348" +
			" 41.52361297607433))";
	
	@BeforeClass
	public static void setupTypes() throws IOException, SchemaException {
		notAnnotatedType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "NotAnnotated",
                        "geom:Polygon"));
		annotatedType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "Annotated",
                        "geom:Polygon,annotations:String"));
	}
	
	@Before
	public void setupValidator() throws IOException {
		String path = "/data/ESP_adm_shp.zip";
		validator = new GADMValidator(GADMValidatorTest.class.getResource(path).getFile(), 4);
	}
	
	@After
	public void closeValidator() throws IOException {
		validator.close();
	}
	
	/**
	 * Creates a feature with a geometry and no annotations.
	 * 
	 * @param wkt the geometry in wkt format.
	 * @return a clusterable containing the feature created.
	 */
	private static Clusterable getNotAnnotatedFeature(String wkt) {
		notAnnotatedType.add(wkt);
		return new SimpleFeatureClusterable(notAnnotatedType.buildFeature(null));
	}
	
	/**
	 * Creates a feature with a geometry and a annotations.
	 * 
	 * @param wkt the geometry in wkt format.
	 * @param annot the annotations in json format.
	 * @return a clusterable containing the feature created.
	 */
	private static Clusterable getAnnotatedFeature(String wkt, String annot) {
		annotatedType.add(wkt);
		annotatedType.add(annot);
		return new SimpleFeatureClusterable(annotatedType.buildFeature(null));
	}
	
	/**
	 * Unit test that checks that AnnotationNotFoundException is thrown if
	 * validator is used on a feature not annotated.
	 * @throws FeatureNotAnnotatedException 
	 * @throws IOException 
	 */
	@Test(expected=FeatureNotAnnotatedException.class)
	public void testAnnotationNotFound() throws FeatureNotAnnotatedException, IOException {
		Clusterable c = getNotAnnotatedFeature("POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))");
		
		validator.validateFeature(c.getAdaptee(SimpleFeature.class));
	}
	
	/**
	 * Auxiliar method for testing the validation of a feature.
	 * 
	 * @param annotation the annotation of that feature.
	 * @param wkt the geometry of that feature, in wkt format.
	 * @return true, if the created feature is validated; false otherwise.
	 * @throws FeatureNotAnnotatedException
	 * @throws IOException
	 */
	private boolean testFeature(String annotation, String wkt) 
			throws FeatureNotAnnotatedException, IOException {
		List<String> jsonList = new ArrayList<>();
		jsonList.add(annotation);
		Clusterable c = getAnnotatedFeature(wkt, new Gson().toJson(jsonList));
		return validator.validateFeature(c.getAdaptee(SimpleFeature.class));
	}
	
	/**
	 * Unit test that checks validator returning false if the annotation of a
	 * feature has no matching with any feature found in gadm.
	 * 
	 * @throws FeatureNotAnnotatedException 
	 * @throws IOException 
	 */
	@Test
	public void testAnnotationNotMatching() throws FeatureNotAnnotatedException, IOException {
		String annotation = "Beijing";
		String wkt = "POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))";
		
		assertFalse(testFeature(annotation, wkt));
	}
	
	/**
	 * Unit test that checks validator returning false if the annotation of a
	 * feature matches with a feature found in gadm, but it is not similar
	 * enough to the features' geometry.
	 * 
	 * @throws IOException 
	 * @throws FeatureNotAnnotatedException 
	 */
	@Test
	public void testDifferentGeometries() throws FeatureNotAnnotatedException, IOException {
		String annotation = "Zaragoza";
		String wkt = "POLYGON((0 0, 0 1, 1 1, 1 0, 0 0))";
		
		assertFalse(testFeature(annotation, wkt));
	}
	
	/**
	 * Unit test that checks validator returning true if geometry retrieved and
	 * the original geometry are similar enough.
	 * 
	 * @throws IOException
	 * @throws FeatureNotAnnotatedException 
	 */
	@Test
	public void testSimilarGeometries() throws FeatureNotAnnotatedException, IOException {
		assertTrue(testFeature("Zaragoza", COORDS_SIMILAR_CASE));
	}
	
	/**
	 * Unit test that checks validator returning true if geometry retrieved and
	 * the original geometry are the same.
	 * 
	 * @throws IOException
	 * @throws FeatureNotAnnotatedException 
	 */
	@Test
	public void testEqualGeometries() throws FeatureNotAnnotatedException, IOException {
		assertTrue(testFeature("Zaragoza", COORDS_EQUAL_CASE));
	}
	
}
