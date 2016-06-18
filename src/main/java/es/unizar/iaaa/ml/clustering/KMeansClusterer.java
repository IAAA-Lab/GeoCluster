package es.unizar.iaaa.ml.clustering;

import org.geotools.feature.SchemaException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.distance.DistanceMeasure;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * Basic implementation of K-Means algorithm. The centroid initialization step
 * is performed by randomly selecting features from the set.
 * 
 * @author Javier Beltran
 */
public class KMeansClusterer extends FeatureClusterer {

	protected int k;
	protected Map<Clusterable, Clusterable> clusters;
	protected List<Clusterable> centroids;
	protected ClusterBuilder builder;
	
	protected KMeansClusterer(int k, DistanceMeasure distance, ClusterBuilder builder, Parameter[] params) {
		super(distance, builder, params);
		
		this.k = k;
		clusters = new HashMap<>(k);
		centroids = new ArrayList<>(k);
		this.builder = builder;
	}

	/**
	 * Given a list of simple features, applies k-means algorithm to clusterize
	 * the features.
	 * 
	 * @param features the list of simple features
	 * @return a list of clusters, each defined as a simple feature collection
	 */
	public List<Cluster> cluster(List<Clusterable> features)
			throws ParameterNotFoundException, SchemaException {
		initializeCentroids(new ArrayList<>(features));
		iterateClusters(features);
		
		return toCollections(features);
	}

	/**
	 * Initializes the centroids by assigning them to random features.
	 * 
	 * @param features the list of features to be clusterized
	 */
	protected void initializeCentroids(List<Clusterable> features) throws ParameterNotFoundException {
		/* Creates a shuffled list with all the indexes of the features */
		List<Integer> randomList = new ArrayList<>();
		for (int i=0; i<features.size(); i++) {
			randomList.add(i);
		}
		Collections.shuffle(randomList);

		/* Selects random features as centroids */
		for (int i=0; i<k; i++) {
			centroids.add(features.get(randomList.get(i)));
		}
	}
	
	/**
	 * Performs the typical two steps of k-means algorithm (assigning features
	 * to a clusters and reassigning centroids) repeatedly, until no centroids
	 * change.
	 * 
	 * @param features the list of features to be clusterized
	 */
	private void iterateClusters(List<Clusterable> features)
			throws ParameterNotFoundException, SchemaException {
		boolean stop = false;
		while (!stop) {
			/* Assigns features to clusters and recalculates the centroids */
			assignClusters(features);
			List<Clusterable> newCentroids = reassignCentroids(features);
			
			/* Checks if centroids have changed so the algorithm ends */
			if (centroidsChanged(newCentroids)) {
				centroids = newCentroids;
			} else {
				stop = true;
			}
		}
		
	}

	/**
	 * Checks if the present centroids and the new calculated centroids are the
	 * same or not.
	 * 
	 * @param newCentroids the list of new centroids
	 * @return true, if newCentroids!=centroids; false, otherwise
	 */
	private boolean centroidsChanged(List<Clusterable> newCentroids) throws ParameterNotFoundException {
		boolean changed = false;
		for (int i=0; i<newCentroids.size() && !changed; i++) {
			/* Checks if distance between new and old centroids is zero */
			if (distance.distance(newCentroids.get(i), centroids.get(i), params) != 0) {
				changed = true;
			}
		}
		return changed;
	}

	/**
	 * Given a list of centroids, assigns every feature to its nearest centroid,
	 * using the specified distance.
	 * 
	 * @param features the list of features
	 */
	private void assignClusters(List<Clusterable> features) throws ParameterNotFoundException {
 		for (Clusterable feature : features) {
			int minIndex = 0;
			double minDistance = Double.POSITIVE_INFINITY;
			
			/* Selects the least-distant centroid from each feature */
			for (int i=0; i<centroids.size(); i++) {
				double d = distance.distance(feature, centroids.get(i), params);
				if (d < minDistance) {
					minDistance = d;
					minIndex = i;
				}
			}
			
			clusters.put(feature, centroids.get(minIndex));
		}
	}
	
	/**
	 * Given a mapping of every feature to its assigned centroid, calculates the
	 * new centroid of that cluster.
	 * 
	 * @param features the list of features
	 * @return the list of new centroids
	 */
	private List<Clusterable> reassignCentroids(List<Clusterable> features) throws SchemaException {
		List<Clusterable> newCentroids = new ArrayList<>();
		
		/* Creates cluster objects */
		List<List<Clusterable>> lists = new ArrayList<>(k);
		for (int i=0; i<k; i++) {
			lists.add(new ArrayList<Clusterable>());
		}
		for (Clusterable feature : features) {
			lists.get(centroids.indexOf(clusters.get(feature))).add(feature);
		}
		
		/* Calculates the centroid of every created cluster */
		for (List<Clusterable> l : lists) {
			newCentroids.add(builder.create(l).getCentroid());
		}
		
		return newCentroids;
	}
	
	/**
	 * Converts the clusters from the data structures used in this class to
	 * those used in clustering package.
	 * 
	 * @param features the list of features to be clusterized
	 * @return a list of feature collections
	 */
	private List<Cluster> toCollections(List<Clusterable> features) {
		List<List<Clusterable>> lists = new ArrayList<>();
		List<Cluster> collections = new ArrayList<>();
		
		/* Creates a list with every cluster components */
		for (int i=0; i<k; i++) {
			lists.add(new ArrayList<Clusterable>());
		}
		for (Clusterable feature : features) {
			lists.get(centroids.indexOf(clusters.get(feature))).add(feature);
		}
		
		/* Creates a cluster with every created list */
		for (List<Clusterable> l : lists) {
			collections.add(builder.create(l));
		}
		
		return collections;
	}
	
}
