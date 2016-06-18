package es.unizar.iaaa.ml.clustering;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.SchemaException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.opengis.feature.simple.SimpleFeature;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.ClusterBuilder;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;
import es.unizar.iaaa.ml.distance.EuclideanDistance;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;
import es.unizar.iaaa.ml.util.DataStoreReader;
import es.unizar.iaaa.ml.util.RemoveVisitor;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * This class contains the common logic for the tests regarding the multiple 
 * versions of k-means algorithm.
 * 
 * @author Javier Beltran
 */
public abstract class KMeansTest {
	
	protected static EuclideanDistance distance;
	protected static Parameter[]params = {geom()};
	protected static ClusterBuilder builder;
	protected static DataStoreReader dataset;

	private static Path path;

	@BeforeClass
	public static void setupAndUnzip() throws IOException {
        distance = new EuclideanDistance();
        String pathZip = "/data/R15.zip";
        String pathShp = "R15.shp";

        File zip = new File(KMeansClustererTest.class.getResource(pathZip).getFile());
        path = Files.createTempDirectory(KMeansClustererTest.class.getCanonicalName());
        ZipUtil.unpack(zip, path.toFile());
        dataset = DataStoreReader.shapefile(FileSystems.getDefault()
                .getPath(path.toString(), pathShp).toFile());
	}
	
	@AfterClass
    public static void removeUnzipped() throws IOException {
		Files.walkFileTree(path, new RemoveVisitor());
    }
	
	/**
	 * Checks that, after executing the algorithm, no cluster is left empty.
	 */
	 void noClustersAreEmpty(KMeansClusterer clusterer)
			throws ParameterNotFoundException, SchemaException {
		List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: dataset) {
            list.add(new SimpleFeatureClusterable(feature));
        }
		
        List<Cluster> clusters = clusterer.cluster(list);
        for (Cluster cluster : clusters) {
        	SimpleFeatureIterator it = cluster.getAttribute(
        			Clusterable.Property.CLUSTERS_ITERATOR, SimpleFeatureIterator.class);
        	assertTrue(it.hasNext());
        }
	}
	
	/**
	 * Checks that, after executing the algorithm, the number of clusters is k.
	 */
	 void numberOfClusters(KMeansClusterer clusterer, int k)
			throws ParameterNotFoundException, SchemaException {
		List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: dataset) {
            list.add(new SimpleFeatureClusterable(feature));
        }

        List<Cluster> clusters = clusterer.cluster(list);
        assertEquals(clusters.size(), k);
	}
	
}
