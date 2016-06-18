package es.unizar.iaaa.ml.validation;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.csv.CSVParser;
import org.opengis.feature.simple.SimpleFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import es.unizar.iaaa.ml.distance.PointToSetDistance;
import es.unizar.iaaa.ml.util.DataStoreReader;
import es.unizar.iaaa.ml.util.RemoveVisitor;

/**
 * A geometry validator checks the correctness of the annotations obtained for a feature with a
 * FeatureAnnotator. To do so, it compares the default feature geometry and the corresponding
 * geometry for the feature named as the main annotation found. It searches for those geometries in
 * a data store like GADM.
 *
 * @author Javier Beltran
 */
public abstract class GeometryValidator {

    /* Equals to approx 0º1' */
    private static final double MEAN_DISTANCE = 0.02;

    CSVParser[] parsers;
    DataStoreReader[] readers;
    Path workingDir;

    /**
     * Given a simple feature, gets its annotated name and looks for the geometry with that name. If
     * found, checks if both geometries are similar.
     *
     * @param feature the simple feature to be validated.
     * @return true, if a geometry is found and is similar to the one of the feature; false, if the
     * found geometry is not similar or if no geometry is found at all.
     */
    abstract boolean validateFeature(SimpleFeature feature)
            throws FeatureNotAnnotatedException, IOException;

    /**
     * Closes the validator. This should always be called after having used a validator.
     */
    public void close() throws IOException {
        Files.walkFileTree(workingDir, new RemoveVisitor());
    }

    /**
     * Compares two feature geometries and returns true if they are similar.
     *
     * @param geom1 one geometry to be compared.
     * @param geom2 the other geometry to be compared.
     * @return true, if both geometries are similar enough; false otherwise.
     */
    boolean compareGeometries(Geometry geom1, Geometry geom2) {
        return geom1.equalsTopo(geom2) ||
                approxEquals(geom1.getArea(), geom2.getArea()) &&
                approxEquals(geom1.getLength(), geom2.getLength()) &&
                similarPointSets(geom1, geom2);
    }

    /**
     * Given two geometries, calculates the mean distance between their point sets, and checks if it
     * is small enough so both geometries are considered similar.
     *
     * @param geom1 one geometry.
     * @param geom2 another geometry.
     * @return true, if the distance between both geometries is small; false, otherwise.
     */
    private boolean similarPointSets(Geometry geom1, Geometry geom2) {
        Coordinate[] points1 = geom1.getCoordinates();
        Coordinate[] points2 = geom2.getCoordinates();

        double mean = 1.0 / (points1.length + points2.length);
        double sum = 0;
		
		/* Calculates point-to-set distance between all points and sets */
        for (Coordinate aPoints1 : points1) {
            sum += PointToSetDistance.distance(aPoints1, points2);
        }
        for (Coordinate aPoints2 : points2) {
            sum += PointToSetDistance.distance(aPoints2, points1);
        }
        mean *= sum;

        return mean < MEAN_DISTANCE;
    }

    /**
     * Checks if two values are approximately the same. In particular, the least value should be at
     * least 90% of the greater value.
     *
     * @param val1 one double value
     * @param val2 another double value
     * @return true, if they are approximately equal; false, otherwise.
     */
    private boolean approxEquals(double val1, double val2) {
        double min = (val1 < val2) ? val1 : val2;
        double max = (val1 < val2) ? val2 : val1;

        return max * 0.9 < min;
    }

}
