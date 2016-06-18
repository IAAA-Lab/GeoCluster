package es.unizar.iaaa.ml.annotation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import org.geotools.data.simple.SimpleFeatureIterator;
import org.geotools.feature.simple.SimpleFeatureBuilder;
import org.geotools.feature.simple.SimpleFeatureTypeBuilder;
import org.geotools.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.simple.SimpleFeatureType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import es.unizar.iaaa.ml.adapter.Cluster;
import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.adapter.SimpleFeatureClusterable;

/**
 * A cluster annotator takes a cluster with annotated features and extracts
 * the common features to create a set of annotations for the cluster.
 * 
 * @author Javier Beltran
 */
public class ClusterAnnotator {
	
	/* Max number of annotations to be added to the cluster */
	private static final int MAX = 10;
	
	/**
	 * Given a cluster of features, returns an annotated feature representing
	 * the cluster. It contains the common annotations from the cluster and
	 * its geometry is the union of geometries of the elements.
	 * 
	 * @param cluster the cluster we are annotating.
	 * @return a clusterable element representing the cluster.
	 */
	public Clusterable annotate(Cluster cluster) {
		Class<? extends Geometry> geomType = cluster.getGeometryType();
		
		/* Builds a new feature type with a geometry and the annotations */
		SimpleFeatureTypeBuilder tb = new SimpleFeatureTypeBuilder();
		tb.setName("cluster");
		tb.setCRS(DefaultGeographicCRS.WGS84);
		tb.add("the_geom", multiType(geomType));
		tb.add("annotation", String.class);
		SimpleFeatureType type = tb.buildFeatureType();
		
		/* Gets the components of the cluster feature */
		String annotations = buildAnnotations(cluster);
		Geometry geometry = cluster.union();
		
		/* Builds the cluster feature */
		SimpleFeatureBuilder b = new SimpleFeatureBuilder(type);
		b.add(geometry);
		b.add(annotations);
		
		return new SimpleFeatureClusterable(b.buildFeature(null));
	}
	
	/**
	 * Given a cluster of features, creates a new set of annotations from the
	 * most common appearances of annotations of the features. Features are 
	 * supposed to be annotated with FeatureAnnotator.
	 * 
	 * @param cluster the cluster of features.
	 * @return a JSON representation containing the 
	 */
	public String buildAnnotations(Cluster cluster) {
		Map<String, Integer> candidates = new TreeMap<>();
		SimpleFeatureIterator features = cluster.getAttribute(
				Clusterable.Property.CLUSTERS_ITERATOR, SimpleFeatureIterator.class);
		
		while (features.hasNext()) {
			String annotation = (String) features.next().getAttribute("annotation");
			if (annotation != null) {
				/* Reads the JSON annotations as a list of strings */
				List<String> names = new Gson().fromJson(annotation, 
						new TypeToken<List<String>>(){}.getType());
				
				for (String name : names) {
					/* Increments vote by 1 in the votes map */
					int votes = (candidates.containsKey(name)) ? candidates.get(name) : 0;
					candidates.put(name, votes + 1);
				}
				
			}
		}

		/* Selects the most voted annotations */
		List<String> best = new ArrayList<>();
		Iterator<Entry<String, Integer>> sortedCandidates = 
				sortMapByValue(candidates).iterator();	
		int lastNum = 2;
		
		/* Only up to MAX annotations, and only considered if have >1 vote */
		while (sortedCandidates.hasNext() && best.size()<MAX && lastNum > 1) {
			Entry<String, Integer> next = sortedCandidates.next();
			lastNum = next.getValue();
			if (lastNum > 1) {
				best.add(next.getKey());
			}
		}
		
		return new Gson().toJson(best);
	}
	
	/**
	 * Auxiliar method for ordering by descending number of votes the votes map.
	 * 
	 * @param map the votes map, with string keys and integer values.
	 * @return a sorted set with the votations ordered by descending number of votes.
	 */
	private Set<Entry<String, Integer>> sortMapByValue(Map<String, Integer> map) {
		/* Declares the comparator for the data structure */
		SortedSet<Entry<String, Integer>> sorted = new TreeSet<>(
				new Comparator<Entry<String, Integer>>() {
					@Override
					public int compare(Entry<String, Integer> e1, Entry<String, Integer> e2) {
						int res = e2.getValue().compareTo(e1.getValue());
						return res != 0 ? res : 1;
					}
				});
		
		/* When adding the entry set, it will be ordered */
		sorted.addAll(map.entrySet());
		return sorted;
	}
	
	private Class<? extends Geometry> multiType(Class<? extends Geometry> type) {
		if (type.equals(Point.class)) {
			return MultiPoint.class;
		} else if (type.equals(LineString.class)) {
			return MultiLineString.class;
		} else if (type.equals(Polygon.class)) {
			return MultiPolygon.class;
		} else {
			return type;
		}
	}
	
}
