package es.unizar.iaaa.ml.util;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTReader;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertTrue;

/**
 * Class containings unit tests for the MySQLReader class.
 * 
 * @author Javier Beltran
 */
public class MySQLReaderTest {
	
	private DataStoreReader reader;
	private static SimpleFeatureBuilder builder;
	private static WKTReader parser;
	
	@BeforeClass
	public static void setFeatures() throws Exception {
		parser = new WKTReader(new GeometryFactory());
		builder = new SimpleFeatureBuilder(DataUtilities
				.createType("PointFeature","Geom:Point"));
	}
	
	/**
	 * Connects to test database located in localhost. If it is not present,
	 * tests in this class are not executed.
	 */
	@Before
	public void establishReader() throws IOException {
		reader = DataStoreReader.mysql("127.0.0.1", "3306", "geocluster_1",
					"geouser", "geopass", "Features");
	}
	
	/**
	 * Checks whether two features retrieved from database and two example
	 * features have the same geometry.
	 */
	@Test
	public void testReadFeatures() throws Exception {

		SimpleFeature f1 = getFeature("POINT(1 0)");
		SimpleFeature f2 = getFeature("POINT(2 1)");

		List<SimpleFeature> list = new ArrayList<>();
		for(SimpleFeature f: reader) {
			list.add(f);
		}
		assertTrue(check(list, f1));
		assertTrue(check(list, f2));
	}
	
	/**
	 * Constructs a SimpleFeature from a WKT String.
	 */
	private SimpleFeature getFeature(String geometry) throws Exception {
		builder.add(parser.read(geometry));
		return builder.buildFeature(null);
	}
	
	/**
	 * Returns true if features f1 and f2 share the same geometry, false
	 * otherwise.
	 */
	private boolean check(List<SimpleFeature> list, SimpleFeature f2) {
		Geometry g2 = (Geometry) f2.getDefaultGeometry();
		for(SimpleFeature f1: list) {
			Geometry g1 = (Geometry) f1.getDefaultGeometry();
			if (g1.equalsExact(g2)) {
				return true;
			}
		}
		return false;
	}
	
}
