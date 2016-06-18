package es.unizar.iaaa.ml.adapter;

import java.util.List;

import com.vividsolutions.jts.geom.Geometry;

/**
 * A cluster is a group of clusterable elements.
 * 
 * @author Javier Beltran
 */
public interface Cluster extends Clusterable {

	/**
	 * Given a cluster, adds elements to it.
	 * 
	 * @param list the list of elements.
	 * @return the cluster with the added elements.
	 */
    Clusterable add(List<Cluster> list);

    /**
     * Given a cluster, returns its centroid. It can be an existing element
     * contained in the cluster, or a new representative element.
     * 
     * @return the centroid of the cluster.
     */
    Clusterable getCentroid();
    
    /**
     * Computes the union of geometries of a cluster.
     * 
     * @return the geometry resulting from the union of the cluster elements.
     */
    Geometry union();
    
    /**
     * Checks if a clusterable element is contained in the cluster.
     * 
     * @return true, if the cluster contains that element; false otherwise.
     */
    boolean contains(Clusterable c);
    
    /**
     * Gets the geometry type of the cluster elements.
     * 
     * @return the class of the geometry type.
     */
    Class<? extends Geometry> getGeometryType();
}
