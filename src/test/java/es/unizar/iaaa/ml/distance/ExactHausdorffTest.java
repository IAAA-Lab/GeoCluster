package es.unizar.iaaa.ml.distance;

import org.geotools.feature.SchemaException;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.opengis.feature.simple.SimpleFeature;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;
import es.unizar.iaaa.ml.parameter.ParameterNotFoundException;
import es.unizar.iaaa.ml.util.DataStoreIterator;
import es.unizar.iaaa.ml.util.DataStoreReader;
import es.unizar.iaaa.ml.util.RemoveVisitor;

import static es.unizar.iaaa.ml.parameter.ParameterBuilder.geom;
import static org.junit.Assert.assertEquals;

/**
 * Unit tests for the exact Hausdorff Distance.
 *
 * @author Javier Beltran
 */
public class ExactHausdorffTest extends DistanceTest {

    private static final String PATH_CCAA_NAME = "Comunidades_Autonomas_ETRS89_30N";
    private static final String PATH_CCAA_ZIP = "/data/" + PATH_CCAA_NAME + ".zip";
    private static final String PATH_CCAA_SHP = PATH_CCAA_NAME + ".shp";
    private static Path workingDir;
    private static DataStoreReader datasetCCAA;
    private ExactHausdorffDistance exactDistance;
    private DiscreteHausdorffDistance discreteDistance;

    @BeforeClass
    public static void unzip() throws Exception {
        File zip = new File(ExactHausdorffTest.class.getResource(PATH_CCAA_ZIP).getFile());
        workingDir = Files.createTempDirectory(ExactHausdorffTest.class.getCanonicalName());
        ZipUtil.unpack(zip, workingDir.toFile());
        datasetCCAA = DataStoreReader.shapefile(FileSystems.getDefault().getPath(workingDir.toString(), PATH_CCAA_SHP).toFile());
    }

    @AfterClass
    public static void removeUnzipped() throws IOException {
        Files.walkFileTree(workingDir, new RemoveVisitor());
    }

    @Before
    public void setupDistance() throws SchemaException {
        exactDistance = new ExactHausdorffDistance();
        discreteDistance = new DiscreteHausdorffDistance();
    }

    /**
     * Unit test that checks whether two identical features have zero distance.
     */
    @Test
    public void testIdenticalFeatures() throws ParameterNotFoundException {
        /* Computes the exact Hausdorff distance */
        double distance = exactDistance.distance(
                getFeaturePoint("POINT(2 -2)"),
                getFeaturePoint("POINT(2 -2)"),
                geom());

		/* Distance should be 0 as both features are the same thing */
        assertEquals(0, distance, 0);
    }

    /**
     * Unit test that ckecks whether two features with different FeatureType have infinite
     * distance.
     */
    @Test
    public void testDifferentTypeFeatures() throws ParameterNotFoundException {
        /* Computes the exact Hausdorff distance */
        double distance = exactDistance.distance(
                getFeaturePoint("POINT(2 -2)"),
                getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))"),
                geom());

		/*
		 * Distance should be POSITIVE_INFINITY as features 1 and 2 have
		 * different feature types.
		 */
        assertEquals(Double.POSITIVE_INFINITY, distance, 0);
    }

    /**
     * Unit test that checks the general case of Hausdorff Distance algorithm.
     */
    @Test
    public void testFeatures() throws ParameterNotFoundException {
		/* Computes the exact Hausdorff distance */
        double distance = exactDistance.distance(
                getFeaturePolygon("POLYGON((0 0, 1 0, 0 1, 0 0))"),
                getFeaturePolygon("POLYGON((-1 0, -2 0, -1 1, -1 0))"),
                geom());

		 /* Distance between feature1 and feature2 should be 2. */
        assertEquals(2, distance, 0);
    }

    /**
     * Unit test that loads two features from a SHP file and calculates the distance using
     * ExactHausdorffDistance and using JTS DiscreteHausdorffDistance. It checks if both distances
     * are the same.
     */
    @Test
    public void testByComparisonWithJTS() throws Exception {
    	/* Reads the features from a SHP file */
        DataStoreIterator it = datasetCCAA.iterator();
        Clusterable andalucia = new SimpleFeatureClusterable(it.next());
        Clusterable aragon = new SimpleFeatureClusterable(it.next());
        it.close();

    	/* Gets the distances using both the exact and discrete method */
        double testDistance = exactDistance.distance(andalucia, aragon, geom());
        double realDistance = discreteDistance.distance(andalucia, aragon, geom());

        assertEquals(realDistance, testDistance, 0);
    }

    /**
     * Unit test that calculates the ExactHausdorffDistance between every pair of features found in
     * a shapefile, and compares with the solution found by JTS DiscreteHausdorffDistance.
     */
    @Test
    public void testAllFeaturesByComparisonWithJTS() throws Exception {
        List<Clusterable> list = new ArrayList<>();
        for(SimpleFeature feature: datasetCCAA) {
            list.add(new SimpleFeatureClusterable(feature));
        }

    	/* Tests every feature against every feature */
        for (Clusterable f1 : list) {
            for (Clusterable f2 : list) {

    			/* Gets the distances using both the exact and discrete method */
                double testDistance = exactDistance.distance(f1, f2, geom());
                double realDistance = discreteDistance.distance(f1, f2, geom());

                assertEquals(realDistance, testDistance, 0);
            }
        }
    }

}
