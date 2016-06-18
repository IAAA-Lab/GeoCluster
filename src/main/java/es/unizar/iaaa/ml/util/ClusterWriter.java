package es.unizar.iaaa.ml.util;

import org.geotools.data.DataUtilities;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.annotation.ClusterAnnotator;

/**
 * A cluster writer is an utility for writing the results of GeoCluster into
 * a data store like a shapefile or a MySQL database. It outputs two pieces of
 * data: the generated clusters and the elements contained in the them, both
 * with their corresponding annotations.
 * 
 * @author Javier Beltran
 */
public class ClusterWriter {
	
	private DataStoreWriter forElements;
	private DataStoreWriter forClusters;
	
	/**
	 * Creates a cluster writer that will output in two shapefiles: one for the
	 * clusters and another one for the elements, both with their annotations.
	 * 
	 * @param elementsFile the elements shapefile.
	 * @param clustersFile the clusters shapefile.
	 * @return a cluster writer.
	 */
	public static ClusterWriter shapefile(File elementsFile, File clustersFile) throws IOException {
		return new ClusterWriter(DataStoreWriter.shapefile(elementsFile),
				DataStoreWriter.shapefile(clustersFile));
	}
	
	/**
	 * Creates a cluster writer that will output in two schemas of a database:
	 * one for the clusters and another one for the elements, both with their
	 * annotations.
	 * 
	 * @param host the MySQL server host.
	 * @param port the MySQL server port.
	 * @param db the database name.
	 * @param user the user to connect to.
	 * @param pass the pass to connect to.
	 * @return a cluster writer.
	 */
	public static ClusterWriter mysql(String host, String port, String db, String user, 
			String pass) throws IOException {
		DataStoreWriter writer = DataStoreWriter.mysql(host, port, db, user, pass);
		return new ClusterWriter(writer, writer);
	}
	
	public ClusterWriter(DataStoreWriter forElements, DataStoreWriter forClusters) {
		this.forElements = forElements;
		this.forClusters = forClusters;
	}
	
	/**
	 * Writes a list of clusters and a list of elements in the selected data
	 * store. All the elements forming the clusters must appear in the elements
	 * list.
	 * 
	 * @param elements the list of elements.
	 * @param clusters the list of clusters.
	 * @return true, if the clusters and elements could both be written; false
	 * otherwise.
	 */
	public boolean writeCluster(List<Clusterable> elements, List<Cluster> clusters) 
			throws IOException {
		List<SimpleFeature> elementsWithId = matchElements(clusters, elements);
		
		/* Builds an annotated feature for every cluster */
		ClusterAnnotator cAnn = new ClusterAnnotator();
		List<SimpleFeature> clusterList = new ArrayList<>();
		for (Cluster c : clusters) {
			clusterList.add(cAnn.annotate(c).getAdaptee(SimpleFeature.class));
		}
		
		boolean write1 = forElements.writeFeatures(DataUtilities.collection(elementsWithId));
		boolean write2 = forClusters.writeFeatures(DataUtilities.collection(clusterList));
		
		return write1 && write2;
	}
	
	/**
	 * Given a cluster list and a list with the clusterable elements contained
	 * at those clusters, performs a matching between clusterable and cluster by
	 * adding a new attribute to the elements called "cluster_id", which is the
	 * index of the cluster that contains it.
	 * 
	 * @param clusters the list of clusters.
	 * @param elements the list of elements that are contained in any cluster.
	 * @return a list containing the elements, with a new cluster_id integer
	 * attribute.
	 */
    public List<SimpleFeature> matchElements(List<Cluster> clusters, List<Clusterable> elements) {
    	List<SimpleFeature> added = new ArrayList<>();
    	
    	if (elements.size() > 0) {
    		/* Builds a new feature type including the cluster_id field */
    		SimpleFeatureType original = elements.get(0).getAdaptee(SimpleFeature.class).getFeatureType();
    		SimpleFeatureTypeBuilder b = new SimpleFeatureTypeBuilder();
    		b.setName(original.getName());
    		b.addAll(original.getAttributeDescriptors());
    		b.add("cluster_id",String.class);
    		SimpleFeatureType enhanced = b.buildFeatureType();
        	
        	for (Clusterable c : elements) {
        		String id = getMatching(c, clusters);
        		
        		/* Builds a new feature with the previous fields and the cluster_id */
        		SimpleFeatureBuilder featureBuilder = new SimpleFeatureBuilder(enhanced);
        		featureBuilder.addAll(c.getAdaptee(SimpleFeature.class).getAttributes());
        		featureBuilder.add(id);
        		
        		added.add(featureBuilder.buildFeature(null));
        	}
    	}
    	
    	return added;
    }
    
    /**
	 * Given a feature and a list of clusters, obtains what cluster contains
	 * that feature, if there is one.
	 * 
	 * @param feature the feature we are searching in the clusters.
	 * @param clusters the clusters in which the feature is being searched.
	 * @return an id of the cluster in which the feature is found, 
	 * or "no_matching" if not found at any.
	 */
	public String getMatching(Clusterable feature, List<Cluster> clusters) {		
		for (int i=0; i<clusters.size(); i++) {
			if (clusters.get(i).contains(feature)) {
				return "matching_" + i;
			}
		}
		return "no_matching";
	}
	
	/**
	 * Closes the data store writers used by the cluster writer. This should
	 * always be done after using it.
	 */
	public void close() {
		forElements.close();
		forClusters.close();
	}
	
}
