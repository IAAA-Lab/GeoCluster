package es.unizar.iaaa.ml.adapter;

import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.SchemaException;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.opengis.feature.simple.SimpleFeature;

import com.vividsolutions.jts.geom.Geometry;

import java.util.List;

import es.unizar.iaaa.ml.center.CenterSelector;
import es.unizar.iaaa.ml.center.CentroidSelector;

/**
 * This class is an implementation of Cluster for clusters that contain simple
 * features.
 * 
 * @author Javier Beltran
 */
public class SimpleFeatureCollectionCluster implements Cluster {

    private SimpleFeatureCollection collection;
    private SimpleFeatureBuilder builder;
    private Class<? extends Geometry> geomType;

    public SimpleFeatureCollectionCluster(SimpleFeatureCollection collection, 
    		Class<? extends Geometry> geomType, SimpleFeatureBuilder builder) {
        this.collection = collection;
        this.builder = builder;
        this.geomType = geomType;
    }
    
    
    @Override
    public boolean isComparableWith(Clusterable other) {
        return false;
    }

    @Override
    public boolean isSame(Clusterable other) {
        return false;
    }

    @Override
    public boolean hasAttribute(String name) {
        return false;
    }

    @Override
    public boolean hasAttribute(Property name) {
        return false;
    }

    @Override
    public <S> S getAttribute(String name, Class<S> clazz) {
        return null;
    }

    @Override
    public <S> List<S> getAttributeList(String name, Class<S> clazz) {
        return null;
    }

    /**
     * Retrieves the value of a given attribute in this cluster.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attribute value.
     * @return the attribute value.
     */
    @Override
    public <S> S getAttribute(Property name, Class<S> clazz) {
        switch (name) {
            case CLUSTERS_ITERATOR:
                return clazz.cast(collection.features());
            default:
                return null;
        }
    }

    @Override
    public <S> List<S> getAttributeList(Property name, Class<S> clazz) {
        return null;
    }

    @Override
    public <S> S getAdaptee(Class<S> clazz) {
        return null;
    }

    @Override
    public Clusterable add(List<Cluster> list) {
        return null;
    }

    /**
     * Given a cluster, returns its centroid. It can be an existing element
     * contained in the cluster, or a new representative element.
     * 
     * @return the centroid of the cluster.
     */
    @Override
    public Clusterable getCentroid() {
        try {
            CenterSelector selector = new CentroidSelector(builder);
            return new SimpleFeatureClusterable(selector.center(collection));
        } catch (SchemaException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Computes the union of geometries of a cluster.
     * 
     * @return the geometry resulting from the union of the simple features.
     */
    public Geometry union() {
    	SimpleFeatureIterator it = collection.features();
    	Geometry result = null;
    	if (it.hasNext()) {
    		result = (Geometry) it.next().getDefaultGeometry();
    		
    		while (it.hasNext()) {
        		result = ((Geometry) it.next().getDefaultGeometry()).union(result);
        	}
    	}
    	return result;
    }
    
    /**
     * Checks if a clusterable element is contained in the cluster.
     * 
     * @return true, if the cluster contains that element; false otherwise.
     */
    @Override
    public boolean contains(Clusterable c) {
    	return collection.contains(c.getAdaptee(SimpleFeature.class));
    }
    
    /**
     * Gets the geometry type of the cluster elements.
     * 
     * @return the class of the geometry type.
     */
    public Class<? extends Geometry> getGeometryType() {
    	return geomType;
    }
    
}
