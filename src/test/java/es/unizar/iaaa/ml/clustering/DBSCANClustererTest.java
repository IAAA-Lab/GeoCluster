package es.unizar.iaaa.ml.clustering;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import es.unizar.iaaa.ml.adapter.SimpleFeatureCollectionBuilder;
import es.unizar.iaaa.ml.distance.ExactHausdorffDistance;
import es.unizar.iaaa.ml.distance.ExactHausdorffTest;
import es.unizar.iaaa.ml.parameter.Parameter;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;
import es.unizar.iaaa.ml.util.DataStoreReader;
import es.unizar.iaaa.ml.util.RemoveVisitor;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static org.junit.Assert.assertTrue;

/**
 * This class contains tests that check the correctness of the DBSCANClusterer with existing
 * datasets from Shapefiles.
 *
 * @author Javier Beltran
 */
public class DBSCANClustererTest {

    private static String[] pathNames = {"Aggregation",
            "jain", "R15", "D31"};

    private static Path[] paths;
    private static DataStoreReader[] datasets;
    private static ExactHausdorffDistance distance;
    private static ClusterBuilder builder;
    private static Logger logger = LoggerFactory.getLogger(DBSCANClustererTest.class);
    private double eps;
    private int minPts;
    private DBSCANClusterer clusterer;
    private Parameter[] params = {geom()};

    @BeforeClass
    public static void setupAndUnzip() throws Exception {
        int num = pathNames.length;
        String[] pathZips = new String[num];
        String[] pathSHPs = new String[num];
        paths = new Path[num];
        datasets = new DataStoreReader[num];
        distance = new ExactHausdorffDistance();
        builder = new SimpleFeatureCollectionBuilder();

        for (int i = 0; i < pathNames.length; i++) {
            pathZips[i] = "/data/" + pathNames[i] + ".zip";
            pathSHPs[i] = pathNames[i] + ".shp";

            File zip = new File(ExactHausdorffTest.class.getResource(pathZips[i]).getFile());
            paths[i] = Files.createTempDirectory(DBSCANClustererTest.class.getCanonicalName());
            ZipUtil.unpack(zip, paths[i].toFile());
            datasets[i] = DataStoreReader.shapefile(FileSystems.getDefault()
                    .getPath(paths[i].toString(), pathSHPs[i]).toFile());

        }
    }

    @AfterClass
    public static void removeUnzipped() throws IOException {
        for (Path dir : paths) {
            Files.walkFileTree(dir, new RemoveVisitor());
        }
    }

    /**
     * Tests algorithm with Aggregation.shp
     */
    @Test
    public void testAggregationShapeset() throws ParameterNotFoundException {
        logger.info("Aggregation");
        eps = 2;
        minPts = 2;
        clusterer = new DBSCANClusterer(eps, minPts, distance, builder, params);
        List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: datasets[Datasets.AGGREGATION.ordinal()]) {
            list.add(new SimpleFeatureClusterable(feature));
        }

        List<Cluster> clusters = clusterer.cluster(list);
        testClusters(clusters, 7);
    }

    /**
     * Tests algorithm with jain.shp
     */
    @Test
    public void testJainShapeset() throws ParameterNotFoundException {
        logger.info("jain");
        eps = 3;
        minPts = 5;
        clusterer = new DBSCANClusterer(eps, minPts, distance, builder,  params);
        List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: datasets[Datasets.JAIN.ordinal()]) {
            list.add(new SimpleFeatureClusterable(feature));
        }

        List<Cluster> clusters = clusterer.cluster(list);
        testClusters(clusters, 2);
    }

    /**
     * Tests algorithm with R15.shp
     */
    @Test
    public void testR15Shapeset() throws ParameterNotFoundException {
        logger.info("R15");
        eps = 0.5;
        minPts = 2;
        clusterer = new DBSCANClusterer(eps, minPts, distance, builder, params);

        List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: datasets[Datasets.R15.ordinal()]) {
            list.add(new SimpleFeatureClusterable(feature));
        }

        List<Cluster> clusters = clusterer.cluster(list);
        testClusters(clusters, 15);
    }

    /**
     * Tests algorithm with D31.shp
     */
    @Test
    public void testD31Shapeset() throws ParameterNotFoundException {
        logger.info("D31");
        eps = 1;
        minPts = 5;
        clusterer = new DBSCANClusterer(eps, minPts, distance, builder, params);

        List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: datasets[Datasets.D31.ordinal()]) {
            list.add(new SimpleFeatureClusterable(feature));
        }

        List<Cluster> clusters = clusterer.cluster(list);
        testClusters(clusters, 31);
    }

    /**
     * Checks no features with the same output are placed in different clusters.
     *
     * @param clusters the list of clusters
     * @param num      the number of output features
     */
    private void testClusters(List<Cluster> clusters, int num) {
        /* Initializes status vector to 0 */
        int[] status = new int[num];
        for (int i = 0; i < status.length; i++) {
            status[i] = 0;
        }

		/* Checks every element in every cluster to fill the status vector */
        for (int i = 0; i < clusters.size(); i++) {
            Cluster cluster = clusters.get(i);
            String t = "";
            SimpleFeatureIterator it = cluster.getAttribute(Clusterable.Property.CLUSTERS_ITERATOR, SimpleFeatureIterator.class);
            while (it.hasNext()) {
                SimpleFeature f = it.next();
                int output = (int) f.getAttribute("output");
                t += output + " ";
                if (status[output - 1] == 0) {
                    /* Sets the corresponding cluster of one feature */
                    status[output - 1] = i;
                } else if (status[output - 1] != i) {
					/* If a feature is found in more than one cluster, error (-1) */
                    status[output - 1] = -1;
                }

            }
            logger.info("Cluster: " + t);
        }

		/* Checks every output */
        for (int statu : status) {
            assertTrue(statu != -1);
        }
    }

    private enum Datasets {AGGREGATION, JAIN, R15, D31}

}
