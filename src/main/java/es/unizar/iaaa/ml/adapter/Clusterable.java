package es.unizar.iaaa.ml.adapter;

import java.util.List;

/**
 * This interface represents any element that can be added to a cluster. It is
 * an adapter for elements that can be used in clustering algorithms.
 * 
 * @author Javier Beltran
 */
public interface Clusterable {

	/**
	 * Checks if this clusterable element is comparable with another one, that
	 * is, if a distance between them can be calculated in a clustering
	 * algorithm.
	 * 
	 * @param other the element to be compared.
	 * @return true, if both are comparable; false otherwise.
	 */
    boolean isComparableWith(Clusterable other);

    /**
     * Checks if this clusterable element is exactly the same as another one,
     * that is, the distance between them in a clustering algorithm should be 0.
     * 
     * @param other the element to be compared.
     * @return true, if both are the same; false otherwise.
     */
    boolean isSame(Clusterable other);

    /**
     * Checks if this clusterable element has an attribute called name.
     * 
     * @param name the name of the attribute.
     * @return true, if there is an attribute with that name; false otherwise.
     */
    boolean hasAttribute(String name);

    /**
     * Checks if this clusterable element has an attribute called name.
     * 
     * @param name the name of the attribute.
     * @return true, if there is an attribute with that name; false otherwise.
     */
    boolean hasAttribute(Property name);

    /**
     * Retrieves the value of a given attribute in this clusterable.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attribute value.
     * @return the attribute value.
     */
    <S> S getAttribute(String name, Class<S> clazz);

    /**
     * Retrieves the value of the list of attributes with a name.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attributes value.
     * @return a list of attribute values.
     */
    <S> List<S> getAttributeList(String name, Class<S> clazz);

    /**
     * Retrieves the value of a given attribute in this clusterable.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attribute value.
     * @return the attribute value.
     */
    <S> S getAttribute(Property name, Class<S> clazz);

    /**
     * Retrieves the value of the list of attributes with a name.
     * 
     * @param name the name of the attribute.
     * @param clazz the class of the attributes value.
     * @return a list of attribute values.
     */
    <S> List<S> getAttributeList(Property name, Class<S> clazz);

    /**
     * Retrieves the element that this clusterable entity contains.
     * 
     * @param clazz the class of the element contained in this clusterable.
     * @return the element contained in this clusterable.
     */
    <S> S getAdaptee(Class<S> clazz);

    /**
     * A clusterable element has properties. They can have defined a value and
     * it can be retrieved using getAttribute() methods.
     */
    enum Property {FEATURE_TYPE, REPRESENTATIVE_GEOMETRY, REPRESENTATIVE_COORDINATES, REPRESENTATIVE_POINT, CLUSTERS_ITERATOR}
}
