package es.unizar.iaaa.ml.clustering;

import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.distance.DistanceMeasure;
import es.unizar.iaaa.ml.parameter.Parameter;

/**
 * A factory for creating instances of FeatureClusterer.
 * 
 * @author Javier Beltran
 */
public class ClustererFactory {
	
	/**
	 * Creates a new KMeans clusterer algorithm.
	 * 
	 * @param k the number of clusters.
	 * @param distance the distance measure used.
	 * @param builder the cluster builder used.
	 * @param params a list of parameters for the clusterer.
	 * @return an instance of KMeansClusterer.
	 */
	public KMeansClusterer newKMeansClusterer(int k, DistanceMeasure distance, 
			ClusterBuilder builder, Parameter[] params) {
		return new KMeansClusterer(k, distance, builder, params);
	}
	
	/**
	 * Creates a new KMeans++ clusterer algorithm.
	 * 
	 * @param k the number of clusters.
	 * @param distance the distance measure used.
	 * @param builder the cluster builder used.
	 * @param params a list of parameters for the clusterer.
	 * @return an instance of KMeansPlusPlusClusterer.
	 */
	public KMeansPlusPlusClusterer newKMeansPlusPlusClusterer(int k, DistanceMeasure distance, 
			ClusterBuilder builder, Parameter[] params) {
		return new KMeansPlusPlusClusterer(k, distance, builder, params);
	}
	
	/**
	 * Creates a new DBSCAN clusterer algorithm.
	 * 
	 * @param eps the max distance between neighbors.
	 * @param minPts the minimum number of elements to create a cluster.
	 * @param distance the distance measure used.
	 * @param builder the cluster builder used.
	 * @param params a list of parameters for the clusterer.
	 * @return an instance of DBSCANClusterer.
	 */
	public DBSCANClusterer newDBSCANClusterer(double eps, int minPts, 
			DistanceMeasure distance, ClusterBuilder builder, Parameter... params) {
		return new DBSCANClusterer(eps, minPts, distance, builder, params);
	}

}
