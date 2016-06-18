package es.unizar.iaaa.ml.clustering;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.distance.DistanceMeasure;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * Implementation of K-Means++ clustering algorithm. It is an optimization of
 * the basic K-Means algorithm, in which initial centroids are selected trying
 * them to be as far from each other as possible.
 * 
 * @author Javier Beltran
 */
public class KMeansPlusPlusClusterer extends KMeansClusterer{

	protected KMeansPlusPlusClusterer(int k, DistanceMeasure distance, ClusterBuilder builder,
			Parameter[] params) {
		super(k, distance, builder, params);
	}
	
	/**
	 * Centroids for the K-Means++ algorithm are selected trying to maximize
	 * distance among them.
	 * 
	 * @param features the list of features.
	 */
	@Override
	protected void initializeCentroids(List<Clusterable> features) throws ParameterNotFoundException {
		Random random = new Random();

		/* First centroid is selected randomly */
		int index = random.nextInt(features.size());
		centroids.add(features.get(index));
		features.remove(index);
		
		/* 
		 * The bigger the distance (SSE) to the rest of the centroids, the more
		 * odds a feature has to be selected as a new centroid.
		 */
		for (int i=1; i<k; i++) {
			Clusterable selected = selectMostLikelyFeature(features, distancesFrom(features));
			centroids.add(selected);
			features.remove(selected);
		}
	}
	
	/**
	 * Given a list of features, calculates its distances to the centroids.
	 * 
	 * @param features the list of features
	 * @return a list with the distances from every features to the centroids
	 */
	private List<Double> distancesFrom(List<Clusterable> features) throws ParameterNotFoundException {
		List<Double> distances = new ArrayList<>();
		double sum = 0;
		
		/* Sum of squared distances from every feature to the centroids */
		for (Clusterable f : features) {
			double dist = 0;
			for (Clusterable c : centroids) {
				dist += Math.pow(distance.distance(f, c, params), 2);
			}
			sum += dist;
			distances.add(dist);
		}
		
		/* Normalizes distances so they can be used as a probability */
		for (int i=0; i<distances.size(); i++) {
			distances.set(i, distances.get(i) / sum);
		}
		
		return distances;
	}

	/**
	 * Given the list of features and its associated list of weights (normalized
	 * distances), selects a feature as the new centroid. The odds to be chosen
	 * centroid are proportional to the weight.
	 * 
	 * @param features the list of features
	 * @param weights the list of weights (normalized distances)
	 * @return a new centroid from the feature list
	 */
	private Clusterable selectMostLikelyFeature(List<Clusterable> features, List<Double> weights) {
		/* Creates a list of pair (feature - weight) */
		List<Pair<Clusterable, Double>> relations = new ArrayList<>();
		for (int i=0; i<features.size(); i++) {
			relations.add(new ImmutablePair<>(
					features.get(i), weights.get(i)));
		}
		
		/* Sorts the list of pairs in ascending weight */
		Collections.sort(relations, new Comparator<Pair<Clusterable, Double>>() {
			@Override
			public int compare(Pair<Clusterable, Double> o1,
					Pair<Clusterable, Double> o2) {
				return (int) (o1.getRight() - o2.getRight());
			}
		});
		Random random = new Random();
		double selector = random.nextDouble();
		
		/* Selects a feature to be centroid based on probabilities */
		int i=0;
		Clusterable selected = null;
		double sum = 0;
		while (selected == null) {
			sum += relations.get(i).getRight();
			if (selector < sum) {
				selected = relations.get(i).getLeft();
			}
			i++;
		}
		
		return selected;
	}
	
}
