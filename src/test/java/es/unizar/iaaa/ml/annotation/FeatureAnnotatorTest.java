package es.unizar.iaaa.ml.annotation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * This class contains the unit test that check the correctness of the feature
 * annotator.
 * 
 * @author Javier Beltran
 */
public class FeatureAnnotatorTest extends AnnotatorTest {
	
	private static FeatureAnnotator annotator;
	
	@BeforeClass
	public static void setupAnnotator() {
		annotator = new FeatureAnnotator(new DefaultExtractor(NGCEv2012_CSV));
	}
	
	/**
	 * Unit test for a case where the output shouldn't be empty.
	 * @throws IOException
	 */
	@Test
	public void testNotEmptyJson() throws IOException {
		Clusterable feature = getPointFeature("POINT(2 0)", "Salou", "España");
		String json = annotator.buildAnnotations(feature, "city");
		assertNotNull(json);
		assertNotEquals("[]", json);
		assertNotEquals("", json);
	}
	
	/**
	 * Unit test for a case where the output should be empty.
	 * @throws IOException
	 */
	@Test
	public void testEmptyJson() throws IOException {
		Clusterable feature = getPointFeature("POINT(6 0)", "Beijing", "China");
		String json = annotator.buildAnnotations(feature, "city");
		List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		assertTrue(list.isEmpty());
	}
	
	/**
	 * Unit test that checks no null or empty elements are added to the json.
	 * @throws IOException
	 */
	@Test
	public void testNoEmptyElementsInJson() throws IOException {
		Clusterable feature = getPointFeature("POINT(1 0)", "Zaragoza", "España");
		String json = annotator.buildAnnotations(feature, "city");
		List<String> list = new Gson().fromJson(json, new TypeToken<List<String>>(){}.getType());
		// Ensure that the list is not empty
		assertEquals(3, list.size());
		// Ensure that no null or empty elements are added
		for (String s : list) {
			assertNotNull(s);
			assertNotEquals("", s);
		}
	}
	
}
