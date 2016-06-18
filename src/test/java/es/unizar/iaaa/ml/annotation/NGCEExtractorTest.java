package es.unizar.iaaa.ml.annotation;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import es.unizar.iaaa.ml.adapter.Clusterable;

/**
 * This class contains the unit tests that check the correctness of the NGCE
 * extractor.
 * 
 * @author Javier Beltran
 */
public class NGCEExtractorTest extends AnnotatorTest {
	
	private static FeatureAnnotator annotator;
	private static FeatureAnnotator positionAnn;
	
	@BeforeClass
	public static void setupAnnotator() {
		annotator = new FeatureAnnotator(new NGCEExtractor(NGCEv2012_CSV, false));
		positionAnn = new FeatureAnnotator(new NGCEExtractor(NGCEv2012_CSV, true));
	}
	
	/**
	 * Unit test for the case when the annotations list is empty because no
	 * matchings were found.
	 */
	@Test
	public void testEmptyJson() throws IOException {
		Clusterable feature = getPolygonFeature("POLYGON((1 0, 2 0, 2 1, 1 0))", "Beijing", "China");
		String json = annotator.buildAnnotations(feature, "city");
		List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		assertTrue(list.isEmpty());
	}
	
	/**
	 * Unit test for the case when checkPosition is activated and there is a
	 * valid matching.
	 */
	@Test
	public void testRightPosition() throws IOException {
		Clusterable feature = getPolygonFeature("POLYGON((-1 40, -1 42, 1 42, 1 40, -1 40))",
				"Villanueva", "España");
		String json = positionAnn.buildAnnotations(feature, "city");
		List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		assertTrue(list.contains("Villanueva de Gállego"));
	}
	
	/**
	 * Unit test for the case when checkPosition is activated and no valid
	 * matching is found.
	 */
	@Test
	public void testWrongPosition() throws IOException {
		Clusterable feature = getPolygonFeature("POLYGON((-1 0, -1 2, 1 2, 1 0, -1 0))",
				"Villanueva", "España");
		String json = positionAnn.buildAnnotations(feature, "city");
		List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		assertTrue(!list.contains("Villanueva de Gállego"));
	}
	
}
