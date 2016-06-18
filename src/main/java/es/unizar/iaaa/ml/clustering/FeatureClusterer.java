package es.unizar.iaaa.ml.clustering;

import org.geotools.feature.SchemaException;

import java.util.List;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.distance.DistanceMeasure;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;

/**
 * A feature clusterer is a clustering algorithm adapted to geospatial features.
 *
 * @author Javier Beltran
 */
public abstract class FeatureClusterer {

    protected DistanceMeasure distance;
    protected Parameter[] params;
    protected ClusterBuilder builder;

    protected FeatureClusterer(DistanceMeasure distance, ClusterBuilder builder, Parameter... params) {
        this.distance = distance;
        this.params = params;
        this.builder = builder;
    }

    /**
     * Runs a clustering algorithm with some clusterable elements.
     * 
     * @param features the list of features to be added to be clusterized.
     * @return a list containing the clusters created.
     */
    public abstract List<Cluster> cluster(List<Clusterable> features)
    		throws ParameterNotFoundException, SchemaException;

}
