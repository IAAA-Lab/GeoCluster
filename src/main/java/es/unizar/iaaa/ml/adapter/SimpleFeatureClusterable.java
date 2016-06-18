package es.unizar.iaaa.ml.adapter;

import com.vividsolutions.jts.geom.Geometry;

import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.type.FeatureType;

import java.util.Arrays;
import java.util.List;

/**
 * This class is an implementation of clusterable that allows simple features
 * to be added to a cluster.
 * 
 * @author Javier Beltran
 */
public class SimpleFeatureClusterable implements Clusterable {

    private SimpleFeature feature;

    public SimpleFeatureClusterable(SimpleFeature feature) {
        this.feature = feature;
    }

    /**
	 * Checks if the simple feature is comparable with a clusterable, that
	 * is, if a distance between them can be calculated in a clustering
	 * algorithm.
	 * 
	 * @param other the clusterable to be compared.
	 * @return true, if both are comparable; false otherwise.
	 */
    @Override
    public boolean isComparableWith(Clusterable other) {
        if (other instanceof SimpleFeatureClusterable) {
            SimpleFeatureClusterable knownOther = (SimpleFeatureClusterable) other;
            return getFeatureType().equals(knownOther.getFeatureType()) && hasGeometry() && knownOther.hasGeometry();
        }
        return false;
    }

    /**
     * Checks if the simple feature has a geometry.
     * 
     * @return true, if it has a geometry; false otherwise.
     */
    private boolean hasGeometry() {
        return hasAttribute(Property.REPRESENTATIVE_GEOMETRY);
    }

    /**
     * Retrieves the feature type for the simple feature.
     * 
     * @return the feature type.
     */
    private FeatureType getFeatureType() {
        return getAttribute(Property.FEATURE_TYPE, FeatureType.class);
    }

    /**
     * Checks if the simple feature is exactly the same as another clusterable,
     * that is, the distance between them in a clustering algorithm should be 0.
     * 
     * @param other the clusterable to be compared.
     * @return true, if both are the same; false otherwise.
     */
    @Override
    public boolean isSame(Clusterable other) {
        if (other instanceof SimpleFeatureClusterable) {
            SimpleFeatureClusterable knownOther = (SimpleFeatureClusterable) other;
            return getRepresentativeJTSGeometry().equalsExact(knownOther.getRepresentativeJTSGeometry());
        }
        return false;
    }

    /**
     * Retrieves the representative geometry for the simple feature.
     * 
     * @return the JTS default geometry.
     */
    private Geometry getRepresentativeJTSGeometry() {
        return (Geometry) feature.getDefaultGeometry();
    }

    @Override
    public boolean hasAttribute(String name) {
        return false;
    }

    /**
     * Checks if this clusterable element has an attribute called name.
     * 
     * @param name the name of the attribute.
     * @return true, if there is an attribute with that name; false otherwise.
     */
    @Override
    public boolean hasAttribute(Property name) {
        switch (name) {
            case REPRESENTATIVE_GEOMETRY:
                return feature.getDefaultGeometry() != null;
            default:
                return false;
        }
    }

    @Override
    public <S> List<S> getAttributeList(String name, Class<S> clazz) {
        return null;
    }

    /**
     * Retrieves the value of a given attribute in this clusterable.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attribute value.
     * @return the attribute value.
     */
    @Override
    public <S> S getAttribute(Property name, Class<S> clazz) {
        switch (name) {
            case REPRESENTATIVE_GEOMETRY:
                return clazz.cast(getRepresentativeJTSGeometry());
            case REPRESENTATIVE_POINT:
                return clazz.cast(getRepresentativeJTSGeometry().getCentroid());
            case FEATURE_TYPE:
                return clazz.cast(feature.getFeatureType());
            default:
                return null;
        }
    }

    /**
     * Retrieves the value of the list of attributes with a name.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attributes value.
     * @return a list of attribute values.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <S> List<S> getAttributeList(Property name, Class<S> clazz) {
        switch (name) {
            case REPRESENTATIVE_COORDINATES:
                return (List<S>) Arrays.asList(getRepresentativeJTSGeometry().getCoordinates());
            default:
                return null;
        }
    }

    /**
     * Retrieves the element that this clusterable entity contains.
     * 
     * @param clazz the class of the element contained in this clusterable.
     * @return the element contained in this clusterable.
     */
    @Override
    public <S> S getAdaptee(Class<S> clazz) {
        return clazz.cast(feature);
    }

    /**
     * Retrieves the value of a given attribute in this clusterable.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attribute value.
     * @return the attribute value.
     */
    @Override
    public <S> S getAttribute(String name, Class<S> clazz) {
        return clazz.cast(feature.getAttribute(name));
    }
}
