package es.unizar.iaaa.ml.clustering;

import org.junit.Test;

import es.unizar.iaaa.ml.adapter.SimpleFeatureCollectionBuilder;
import es.unizar.iaaa.ml.util.DataStoreIterator;

/**
 * This class contains the unit tests that check the correctness of the k-means
 * algorithm.
 * 
 * @author Javier Beltran
 */
public class KMeansClustererTest extends KMeansTest {
	
	/**
	 * K-Means initialization based on the positions of existing elements in
	 * the dataset should guarantee that no cluster is left empty by the 
	 * algorithm, as every cluster will always have at least one nearer element.
	 */
	@Test
	public void testNoClustersAreEmpty() throws Exception {
		DataStoreIterator iterator = dataset.iterator();
		builder = new SimpleFeatureCollectionBuilder(iterator.next().getType());
		iterator.close();
		int k = 10;
		noClustersAreEmpty(new KMeansClusterer(k, distance, builder, params));
	}
	
	/**
	 * The number of resulting clusters should be the specified when executing
	 * the algorithm.
	 */
	@Test
	public void testNumberOfClusters() throws Exception {
		DataStoreIterator iterator = dataset.iterator();
		builder = new SimpleFeatureCollectionBuilder(iterator.next().getType());
		iterator.close();
		int k = 5;
		numberOfClusters(new KMeansClusterer(k, distance, builder, params), k);
	}
	
}
