package es.unizar.iaaa.ml.distance;

import com.vividsolutions.jts.geom.Coordinate;

/**
 * This class implements the point-to-set distance, that is, the distance from
 * a point to a set of points. It is defined as the minimum distance from the
 * point to any point of the set.
 * 
 * Note that this class is not an implementation of DistanceMeasure, as it has
 * not been thought as a distance for clustering algorithms.
 * 
 * @author Javier Beltran
 */
public class PointToSetDistance {
	
	/**
	 * Calculates the point-to-set distance.
	 * 
	 * @param point the coordinates of the point.
	 * @param set an array with the coordinates of every point in the set.
	 * @return the point-to-set distance between them.
	 */
	public static double distance(Coordinate point, Coordinate[] set) {
		double minDist = Double.POSITIVE_INFINITY;
		
		/* Looks for the minimum distance */
		for (Coordinate aSet : set) {
			double dist = point.distance(aSet);
			if (dist < minDist) {
				minDist = dist;
			}
		}
		
		return minDist;
	}
	
}
