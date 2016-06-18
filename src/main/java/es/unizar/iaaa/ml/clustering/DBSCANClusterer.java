package es.unizar.iaaa.ml.clustering;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.distance.DistanceMeasure;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * An implementation of the DBSCAN clustering algorithm, adapted to geospatial 
 * features. It requires two parameters to work: an epsilon, eps; and the 
 * minimum number of features required to make a cluster, minPts.
 *
 * @author Javier Beltran
 */
public class DBSCANClusterer extends FeatureClusterer {

    private double eps;
    private int minPts;

    protected DBSCANClusterer(double eps, int minPts, DistanceMeasure distance, ClusterBuilder builder, Parameter... params) {
        super(distance, builder, params);

        this.eps = eps;
        this.minPts = minPts;
    }

    /**
     * Given the entry features, returns a list of feature collections, each 
     * representing a cluster.
     *
     * @param features a list of features to be clusterized
     * @return a list of clusters (feature collections).
     */
    public List<Cluster> cluster(List<Clusterable> features) throws ParameterNotFoundException {
        final List<Cluster> clusters = new ArrayList<>();
        final Map<Clusterable, FeatureStatus> visited = new HashMap<>();

        for (final Clusterable feature : features) {
            /* Features already classified are not processed */
            if (visited.get(feature) != null) {
                continue;
            }

            final List<Clusterable> neighbors = getNeighbors(feature, features);
            if (neighbors.size() >= minPts) {
                /* Creates a cluster with the feature and its neighbors */
                clusters.add(builder.create(expand(feature, neighbors, features, visited)));
            } else {
				/* Otherwise, the feature is classified as noise */
                visited.put(feature, FeatureStatus.NOISE);
            }
        }

        return clusters;
    }

    /**
     * Creates a cluster from a feature, a list of its neighbors, a list of all 
     * the features (as the algorithm is concerned about neighbors of neighbors)
     * and information about what features are not part of the cluster.
     *
     * @param feature   the feature that originates the cluster.
     * @param neighbors the neighbors of the feature.
     * @param features  a list with all the features.
     * @param visited   a map that holds info about the status of every feature.
     * @return a list containing all the features forming a cluster.
     */
    private List<Clusterable> expand(Clusterable feature, List<Clusterable> neighbors, List<Clusterable> features,
    		Map<Clusterable, FeatureStatus> visited) throws ParameterNotFoundException {
        List<Clusterable> cluster = new ArrayList<>();

		/* Adds the feature that originates the cluster */
        cluster.add(feature);
        visited.put(feature, FeatureStatus.PART_OF_CLUSTER);

        List<Clusterable> candidates = new ArrayList<>(neighbors);
        for (int i = 0; i < candidates.size(); i++) {
            final Clusterable current = candidates.get(i);
            final FeatureStatus currentStatus = visited.get(current);

			/* Get the neighbors of non-visited neighbors */
            if (currentStatus == null) {
                final List<Clusterable> currentNeighbors = getNeighbors(current, features);
                if (currentNeighbors.size() >= minPts) {
                    candidates = merge(candidates, currentNeighbors);
                }
            }

			/* Adds candidates that are not part of the cluster */
            if (currentStatus != FeatureStatus.PART_OF_CLUSTER) {
                visited.put(current, FeatureStatus.PART_OF_CLUSTER);
                cluster.add(current);
            }
        }

        return cluster;
    }

    /**
     * Adds list l2 to list l1, with no repeated elements.
     * 
     * @param l1 one list of features.
     * @param l2 another list of features.
     * @return a merged list with the features from l1 and l2, with no repeated
     * elements.
     */
    private List<Clusterable> merge(List<Clusterable> l1, List<Clusterable> l2) {
        final Set<Clusterable> oneSet = new HashSet<>(l1);
        for (Clusterable item : l2) {
            if (!oneSet.contains(item)) {
                l1.add(item);
            }
        }
        return l1;
    }

    /**
     * Given a feature and a list of possible neighbors, returns a list with the
     * neighbors of that feature. Two features are neighbors if the distance 
     * between them is <= eps.
     *
     * @param feature  the feature whose neighbors are being retrieved.
     * @param features the features that can be neighbors of feature.
     * @return a list of features, containing the neighbors of feature.
     */
    private List<Clusterable> getNeighbors(final Clusterable feature, final Collection<Clusterable> features)
    		throws ParameterNotFoundException {
        final List<Clusterable> neighbors = new ArrayList<>();
        for (final Clusterable neighbor : features) {
			/* Checks distance and the neighbor not being itself */
            if (feature != neighbor && distance.distance(neighbor, feature, params) <= eps) {
                neighbors.add(neighbor);
            }
        }

        return neighbors;
    }

    /* Features can be part of a cluster or noise (not part of any) */
    private enum FeatureStatus {
        NOISE, PART_OF_CLUSTER
    }

}
