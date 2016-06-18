package es.unizar.iaaa.ml.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

/**
 * This class contains the unit tests for the MySQL Writer. It requires a
 * database to be running in localhost for this tests to run.
 * 
 * @author Javier Beltran
 */
public class MySQLWriterTest {
	
	/* Connection parameters */
	private static final String HOST = "127.0.0.1";
	private static final String PORT = "3306";
	private static final String DB = "geocluster_1";
	private static final String USER = "geouser";
	private static final String PASSWD = "geopass";
	private static final String SCHEMA = "PointFeature";
	
	private DataStoreWriter writer;
	private static SimpleFeatureBuilder builder;
	private static WKTReader parser;
	
	@BeforeClass
	public static void setFeatures() throws SchemaException {
		parser = new WKTReader(new GeometryFactory());
		builder = new SimpleFeatureBuilder(DataUtilities
				.createType(SCHEMA,"Geom:Point,city:String"));
	}
	
	@Before
	public void establishWriter() throws IOException {
		writer = DataStoreWriter.mysql(HOST, PORT, DB, USER, PASSWD);
	}
	
	@After
	public void closeWriter() {
		writer.close();
	}
	
	/**
	 * The created table is erased after running every test.
	 */
	@After
	public void cleanDatabase() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		Properties props = new Properties();
		props.put("user",USER);
		props.put("password", PASSWD);
		
		Connection conn = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/"+DB, props);
		Statement stmt = conn.createStatement();
		stmt.executeUpdate("DROP TABLE IF EXISTS " + SCHEMA);
		conn.close();
	}
	
	/**
	 * Unit test that writes a list of features in a MySQL database.
	 */
	@Test
	public void testWriteFeatures() throws ParseException, IOException {
		SimpleFeature f1 = getFeature("POINT (1 0)", "Zaragoza");
		SimpleFeature f2 = getFeature("POINT (2 0)", "Barcelona");
		SimpleFeature f3 = getFeature("POINT (0 0)", "Madrid");
		
		List<SimpleFeature> list = new ArrayList<>();
		list.add(f1);
		list.add(f2);
		list.add(f3);
		
		assertTrue(writer.writeFeatures(DataUtilities.collection(list)));
	}
	
	/**
	 * Unit test that checks the case when an empty list is passed to the
	 * writer.
	 */
	@Test
	public void testNoFeatures() throws IOException {
		List<SimpleFeature> list = new ArrayList<>();
		assertFalse(writer.writeFeatures(DataUtilities.collection(list)));
	}
	
	/**
	 * Constructs a SimpleFeature from a WKT String.
	 */
	private SimpleFeature getFeature(String geometry, String city) throws ParseException {
		builder.add(parser.read(geometry));
		builder.add(city);
		return builder.buildFeature(null);
	}
	
}
