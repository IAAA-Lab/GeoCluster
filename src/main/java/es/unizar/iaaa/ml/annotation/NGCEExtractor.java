package es.unizar.iaaa.ml.annotation;

import com.google.gson.Gson;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.geotools.geometry.jts.JTSFactoryFinder;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;
import es.unizar.iaaa.ml.util.ConversionException;
import es.unizar.iaaa.ml.util.Converter;

/**
 * A NGCE Extractor is a feature extractor suited for the NGCE database. It
 * looks for the important information contained in it, like the town name and
 * the names of the main administrative divisions (provinces and administrative
 * communities).
 * 
 * It can also check the position of every element of the database whose names
 * are to be extracted, and only extract them if the position of that element
 * is within the feature's geometry.
 * 
 * @author Javier Beltran
 */
public class NGCEExtractor implements Extractor {

	/* Constants indicating the number of column where every element is */
	private static final int NGCE_INDEX_MAIN_NAME = 0;
	private static final int NGCE_INDEX_MUNICIPALITY = 7;
	private static final int NGCE_INDEX_PROVINCE = 8;
	private static final int NGCE_INDEX_CCAA = 9;
	private static final int NGCE_INDEX_LATITUDE_ETRS89 = 13;
	private static final int NGCE_INDEX_LONGITUDE_ETRS89 = 14;
	
	private File file;
	private boolean checkPosition;

	public NGCEExtractor(String path, boolean checkPosition) {
		this.file = new File(path);
		this.checkPosition = checkPosition;
	}
	
	/**
	 * Given a feature and a field contained in that feature, extracts the
	 * names related with that field.
	 * 
	 * @param feature the feature to extract the annotations into.
	 * @param field the attribute field to be looked for the extraction.
	 * @return a json representation of the extracted annotations.
	 */
	@Override
	public String extract(Clusterable feature, String field) throws IOException {
		String attribute = (String) feature.getAdaptee(SimpleFeature.class).getAttribute(field);

		if (attribute != null) {
			List<CSVRecord> candidates = new ArrayList<>();
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), 
					CSVFormat.RFC4180.withHeader());
			
			/* Iterates looking for coincidences in the first column (name) */
			for (CSVRecord row : parser) {
				if (isValid(feature, row, attribute)) {
					candidates.add(row);
				}
			}
			parser.close();
			
			/* Gets the names for the town, province and CCAA */
			ArrayList<String> list = new ArrayList<>();
			for (CSVRecord candidate : candidates) {	
				String mainName = candidate.get(NGCE_INDEX_MAIN_NAME);
				String town = candidate.get(NGCE_INDEX_MUNICIPALITY);
				String province = candidate.get(NGCE_INDEX_PROVINCE);
				String community = candidate.get(NGCE_INDEX_CCAA);
				List<String> components = new ArrayList<>(Arrays.asList(mainName, town, province, community));
				
				for (String c : components) {
					if (!list.contains(c) && !c.equals("")){
						list.add(c);
					}
				}
			}
			
			/* Returns the list in JSON format */
			return new Gson().toJson(list);
		} else return "[]";
	}

	/**
	 * Checks if a database row corresponds to a valid element to be extracted,
	 * given a feature and its attribute.
	 * 
	 * @param feature the feature to get the names extracted.
	 * @param row the CSVRecord with the element from NGCE.
	 * @param attribute the attribute of the feature that is being used.
	 * @return true, if the row is considered valid; false if it is not valid or
	 * if it could not be determined.
	 */
	private boolean isValid(Clusterable feature, CSVRecord row, String attribute) {
		if (row.size() > 1) {
			GeometryFactory geometryFactory = JTSFactoryFinder.getGeometryFactory(null);
			
			boolean positioned = true;
			if (checkPosition) {
				/* 
				 * Determines if the element's representative point is inside 
				 * the feature's geometry 
				 */
				Geometry geom = (Geometry) feature.getAdaptee(SimpleFeature.class).getDefaultGeometry();
				try {
					double lat = Converter.dmsToDegrees(row.get(NGCE_INDEX_LATITUDE_ETRS89));
					double lon = Converter.dmsToDegrees(row.get(NGCE_INDEX_LONGITUDE_ETRS89));
					Point p = geometryFactory.createPoint(new Coordinate(lon, lat));
					positioned = p.within(geom);
				} catch (ConversionException e) {
					return false;
				}
			}
			
			boolean contained = row.get(0).contains(attribute) || attribute.contains(row.get(0));
			return contained && positioned;
		} else {
			return false;
		}
	}

}
