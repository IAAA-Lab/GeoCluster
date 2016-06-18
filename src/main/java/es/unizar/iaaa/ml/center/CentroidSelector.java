package es.unizar.iaaa.ml.center;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

/**
 * A centroid selector creates a feature representing the centroid of a feature
 * collection.
 * 
 * @author Javier Beltran
 */
public class CentroidSelector implements CenterSelector {

	private GeometryFactory factory;
	private SimpleFeatureBuilder builder;
	
	public CentroidSelector(SimpleFeatureBuilder builder) throws SchemaException {
		factory = new GeometryFactory();
		this.builder = builder;
	}
	
	/**
	 * Creates a centroid for the feature collection.
	 * 
	 * @param col the feature collection whose centroid will be calculated
	 */
	@Override
	public SimpleFeature center(SimpleFeatureCollection col) {
		double cx = 0;
		double cy = 0;
		int numFeatures = 0;
		
		/* Takes all features and calculates centroid media */
		SimpleFeatureIterator it = col.features();
		while (it.hasNext()) {
			SimpleFeature sf = it.next();
			Geometry geom = (Geometry) sf.getDefaultGeometry();
			cx += geom.getCentroid().getX();
			cy += geom.getCentroid().getY();
			numFeatures++;
		}
		
		cx /= numFeatures;
		cy /= numFeatures;
		Point p = factory.createPoint(new Coordinate(cx, cy));
		
		builder.add(p);
		return builder.buildFeature(null);
	}

}
