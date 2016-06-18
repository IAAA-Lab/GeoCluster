package es.unizar.iaaa.ml.util;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureCollectionBuilder;

import static org.junit.Assert.assertTrue;

/**
 * This class contains unit tests for the cluster writer.
 * 
 * @author Javier Beltran
 */
public class ClusterWriterTest {
	
	/* Connection parameters */
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "3306";
	private static final String DB = "geocluster_1";
	private static final String USER = "geouser";
	private static final String PASSWD = "geopass";
	private static final String SCHEMA_ELEMENTS = "PointFeature";
	private static final String SCHEMA_CLUSTERS = "cluster";
	
	private static final String PATH_POINT_FEATURE = "src/test/resources/PointFeature.shp";
	private static final String PATH_CLUSTER = "src/test/resources/cluster.shp";
	
	private static SimpleFeature f1;
	private static SimpleFeature f2;
	private static SimpleFeature f3;
	private static SimpleFeature f4;
	
	private ClusterWriter writer;
	private static SimpleFeatureBuilder builder;
	
	@BeforeClass
	public static void setup() throws SchemaException {
		builder = new SimpleFeatureBuilder(DataUtilities
				.createType(SCHEMA_ELEMENTS,"Geom:Point,city:String"));
		builder.add("POINT(1 0)");
		builder.add("Zaragoza");
		f1 = builder.buildFeature(null);
		builder.add("POINT(2 0)");
		builder.add("Barcelona");
		f2 = builder.buildFeature(null);
		builder.add("POINT(0 1)");
		builder.add("Paris");
		f3 = builder.buildFeature(null);
		builder.add("POINT(0 2)");
		builder.add("London");
		f4 = builder.buildFeature(null);
	}
	
	@After
	public void close() {
		writer.close();
	}
	
	@AfterClass
	public static void cleanDatabase() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Properties props = new Properties();
		props.put("user", USER);
		props.put("password", PASSWD);
		
		Connection conn = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/"+DB, props);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS " + SCHEMA_ELEMENTS);
		stmt.executeUpdate("DROP TABLE IF EXISTS " + SCHEMA_CLUSTERS);
		conn.close();
	}
	
	@AfterClass
	public static void removeShapefiles() {
		File res = new File("src/test/resources");
		File[] files = res.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isFile() && (file.getName().contains(SCHEMA_ELEMENTS)
						|| file.getName().contains(SCHEMA_CLUSTERS))) {
					file.delete();
				}
			}
		}
	}
	
	/**
	 * Unit test that writes a cluster and its elements in a MySQL database.
	 */
	@Test
	public void testWriteMySQL() throws IOException {
		writer = ClusterWriter.mysql(HOST, PORT, DB, USER, PASSWD);
		test(writer);
	}
	
	/**
	 * Unit test that writes a cluster and its elements in two shapefiles.
	 */
	@Test
	public void testWriteShapefile() throws IOException {
		writer = ClusterWriter.shapefile(new File(PATH_POINT_FEATURE), 
				new File(PATH_CLUSTER));
		test(writer);
	}
	
	private void test(ClusterWriter writer) throws IOException {
		List<Clusterable> elements = createElements();
		List<Cluster> clusters = createClusters();
		
		assertTrue(writer.writeCluster(elements, clusters));
	}
	
	/**
	 * Creates a list with four test elements.
	 * 
	 * @return the list.
	 */
	private List<Clusterable> createElements() {
		List<Clusterable> list = new ArrayList<>();
		
		list.add(new SimpleFeatureClusterable(f1));
		list.add(new SimpleFeatureClusterable(f2));
		list.add(new SimpleFeatureClusterable(f3));
		list.add(new SimpleFeatureClusterable(f4));
		
		return list;
	}
	
	/**
	 * Creates a list with two clusters.
	 * 
	 * @return the list.
	 */
	private List<Cluster> createClusters() {
		List<Clusterable> c1 = new ArrayList<>();
		c1.add(new SimpleFeatureClusterable(f1));
		c1.add(new SimpleFeatureClusterable(f2));
		Cluster cluster1 = new SimpleFeatureCollectionBuilder().create(c1);
		
		List<Clusterable> c2 = new ArrayList<>();
		c2.add(new SimpleFeatureClusterable(f3));
		c2.add(new SimpleFeatureClusterable(f4));
		Cluster cluster2 = new SimpleFeatureCollectionBuilder().create(c2);
		
		List<Cluster> clusters = new ArrayList<>();
		clusters.add(cluster1);
		clusters.add(cluster2);
		return clusters;
	}
}
