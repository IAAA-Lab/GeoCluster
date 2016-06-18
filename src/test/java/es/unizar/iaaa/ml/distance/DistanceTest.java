package es.unizar.iaaa.ml.distance;

import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

import org.geotools.data.DataUtilities;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.junit.BeforeClass;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;


/**
 * This class contains all the functionality that is common to all tests about distances between
 * features.
 *
 * @author Javier Beltran
 */
public abstract class DistanceTest {

    private static SimpleFeatureBuilder pointType;
    private static SimpleFeatureBuilder polygonType;
    private static SimpleFeatureBuilder lineStringType;
    private static SimpleFeatureBuilder pointAndIntegerType;
    private static WKTReader parser;

    @BeforeClass
    public static void setup() throws SchemaException {
        /* Creates a FeatureType consisting in a point  */
        pointType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "PointFeature",
                        "geom:Point"));

        /* Creates a FeatureType consisting in a polygon  */
        polygonType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "PolygonFeature",
                        "geom:Polygon"));
        
        /* Creates a FeatureType consisting in a line string  */
        lineStringType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "LineStringFeature",
                        "geom:LineString"));
        
        /* Creates a FeatureType consisting in a point and an integer */
        pointAndIntegerType = new SimpleFeatureBuilder(
                DataUtilities.createType(
                        "PointAndIntegerFeature",
                        "geom:Point,number:Integer"));

        parser = new WKTReader(new GeometryFactory());
    }

    Clusterable getFeaturePoint(String wkt) {
        return parseWkt(pointType, wkt);
    }

    Clusterable getFeaturePolygon(String wkt) {
        return parseWkt(polygonType, wkt);
    }

    Clusterable getFeatureLineString(String wkt) {
        return parseWkt(lineStringType, wkt);
    }

    Clusterable getFeaturePointAndInteger(String wkt, long number) {
    	pointAndIntegerType.add(wkt);
    	pointAndIntegerType.add(number);
		return new SimpleFeatureClusterable(pointAndIntegerType.buildFeature(null));
    }

    private Clusterable parseWkt(SimpleFeatureBuilder type, String wkt) {
        try {
            type.add(parser.read(wkt));
        } catch (ParseException pe) {
            throw new RuntimeException("Unexpected error in WKT", pe);
        }
        return new SimpleFeatureClusterable(type.buildFeature(null));
    }
}
