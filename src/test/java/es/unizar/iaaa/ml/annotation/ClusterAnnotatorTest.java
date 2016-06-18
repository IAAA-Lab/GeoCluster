package es.unizar.iaaa.ml.annotation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPoint;

import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.Clusterable.Property;
import es.unizar.iaaa.ml.adapter.SimpleFeatureCollectionBuilder;
import static org.junit.Assert.assertTrue;

/**
 * This class contains the unit tests that check the correctness of the cluster
 * annotator.
 * 
 * @author Javier Beltran
 */
public class ClusterAnnotatorTest extends AnnotatorTest {
	
	private static FeatureAnnotator featureAnn;
	private static ClusterAnnotator clusterAnn;
	
	@BeforeClass
	public static void setupAnnotator() {
		featureAnn = new FeatureAnnotator(new DefaultExtractor(NGCEv2012_CSV));
		clusterAnn = new ClusterAnnotator();
	}
	
	/**
	 * Unit test for the case when no common annotations are found, so there
	 * should be no cluster annotations.
	 */
	@Test
	public void testEmptyJson() throws IOException {
		Clusterable c1 = getPointFeature("POINT(1 0)", "Zaragoza", "España");
		Clusterable c2 = getPointFeature("POINT(-1 0)", "Madrid", "España");
		
		c1 = featureAnn.annotate(c1, "city");
		c2 = featureAnn.annotate(c2, "city");
		
		List<Clusterable> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);
		Cluster cluster = new SimpleFeatureCollectionBuilder().create(list);
		
		List<String> annotations = new Gson().
				fromJson(clusterAnn.buildAnnotations(cluster),
						new TypeToken<List<String>>(){}.getType());
		
		assertTrue(annotations.isEmpty());
	}
	
	/**
	 * Unit test for the case when there are common annotations, so the cluster
	 * annotations should be a not empty list.
	 */
	@Test
	public void testNotEmptyJson() throws IOException {
		Clusterable c1 = getPointFeature("POINT(1 0)", "Barcelona", "España");
		Clusterable c2 = getPointFeature("POINT(2 1)", "Puerto de Barcelona", "España");
		Clusterable c3 = getPointFeature("POINT(-1 3)", "Aeropuerto de Barcelona", "España");
		
		c1 = featureAnn.annotate(c1, "city");
		c2 = featureAnn.annotate(c2, "city");
		c3 = featureAnn.annotate(c3, "city");
		
		List<Clusterable> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		Cluster cluster = new SimpleFeatureCollectionBuilder().create(list);
		
		List<String> annots = new Gson().
				fromJson(clusterAnn.buildAnnotations(cluster),
						new TypeToken<List<String>>(){}.getType());
		
		assertTrue(annots != null && !annots.isEmpty());
	}
	
	/**
	 * Unit test that checks the geometry of the resulting clusterable is the
	 * union of the cluster elements.
	 */
	@Test
	public void testUnionGeometry() throws IOException {
		/* Creates a cluster */
		Clusterable c1 = getPointFeature("POINT(1 0)", "Barcelona", "España");
		Clusterable c2 = getPointFeature("POINT(2 1)", "Puerto de Barcelona", "España");
		Clusterable c3 = getPointFeature("POINT(-1 3)", "Aeropuerto de Barcelona", "España");
		
		c1 = featureAnn.annotate(c1, "city");
		c2 = featureAnn.annotate(c2, "city");
		c3 = featureAnn.annotate(c3, "city");
		
		List<Clusterable> list = new ArrayList<>();
		list.add(c1);
		list.add(c2);
		list.add(c3);
		Cluster cluster = new SimpleFeatureCollectionBuilder().create(list);
		
		/* Creates the expected geometry */
		Clusterable representingCluster = clusterAnn.annotate(cluster);
		Geometry g1 = (Geometry) c1.getAdaptee(SimpleFeature.class).getDefaultGeometry();
		Geometry g2 = (Geometry) c2.getAdaptee(SimpleFeature.class).getDefaultGeometry();
		Geometry g3 = (Geometry) c3.getAdaptee(SimpleFeature.class).getDefaultGeometry();
		Geometry union = g1.union(g2).union(g3);
		
		/* Checks whether both geometries are equal */
		assertTrue(union.equalsTopo(representingCluster
				.getAttribute(Property.REPRESENTATIVE_GEOMETRY, MultiPoint.class)));
	}
	
}
