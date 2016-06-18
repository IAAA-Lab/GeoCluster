package es.unizar.iaaa.ml.adapter;

import java.util.List;

/**
 * A cluster builder allows the creation of clusters given a list of
 * clusterable elements.
 * 
 * @author Javier Beltran
 */
public interface ClusterBuilder {

	/**
	 * Creates a cluster containing some clusterable elements.
	 * 
	 * @param list a list of clusterable elements to be added.
	 * @return a cluster containing the elements from the list.
	 */
    Cluster create(List<Clusterable> list);

}
