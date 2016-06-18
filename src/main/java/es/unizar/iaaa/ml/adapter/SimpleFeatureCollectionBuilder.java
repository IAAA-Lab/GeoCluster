package es.unizar.iaaa.ml.adapter;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import com.vividsolutions.jts.geom.Geometry;

import es.unizar.iaaa.ml.adapter.Clusterable.Property;

import java.util.List;

/**
 * This class is an implementation of cluster builder for creating clusters
 * that contain simple features.
 * 
 * @author Javier Beltran
 */
public class SimpleFeatureCollectionBuilder implements ClusterBuilder {
	
	private SimpleFeatureBuilder builder;
	
	public SimpleFeatureCollectionBuilder() {
    	
	}
	
	public SimpleFeatureCollectionBuilder(SimpleFeatureBuilder builder) {
		this.builder = builder;
	}
	
	public SimpleFeatureCollectionBuilder(SimpleFeatureType type) {
		this.builder = new SimpleFeatureBuilder(type);
	}

	/**
	 * Creates a cluster containing some clusterable elements.
	 * 
	 * @param list a list of clusterable elements to be added.
	 * @return a cluster containing the elements from the list.
	 */
	@Override
    public Cluster create(List<Clusterable> list) {
		if (list.size() > 0) {
			Class<? extends Geometry> geom = list.get(0).getAttribute(
					Property.REPRESENTATIVE_GEOMETRY, Geometry.class).getClass();
			
			SimpleFeature[] sf = new SimpleFeature[list.size()];
	        for(int i = 0; i<list.size();  i++) {
	            sf[i] = list.get(i).getAdaptee(SimpleFeature.class);
	        }
	        return new SimpleFeatureCollectionCluster(DataUtilities.collection(sf), geom, builder);
		} else return null;
    }
}
