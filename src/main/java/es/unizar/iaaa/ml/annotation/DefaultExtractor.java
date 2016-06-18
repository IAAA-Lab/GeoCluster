package es.unizar.iaaa.ml.annotation;

import com.google.gson.Gson;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import es.unizar.iaaa.ml.adapter.Clusterable;

/**
 * A basic implementation of an extractor. It checks if the attribute is
 * contained in every row, and then obtains the first by order of appearance.
 * 
 * @author Javier Beltran
 */
public class DefaultExtractor implements Extractor {

	/* Max number of annotations to be extracted with default extractor */
	private final static int MAX = 10;
	
	private File file;

	public DefaultExtractor(String path) {
		this.file = new File(path);
	}
	
	/**
	 * Given a SimpleFeature and the name of one of its fields, uses the value
	 * of that field to extract names.
	 * 
	 * @param feature the feature we are working with.
	 * @param field the name of the field of the feature that we are using.
	 */
	@Override
	public String extract(Clusterable feature, String field) throws IOException {
		String attribute = (String) feature.getAdaptee(SimpleFeature.class).getAttribute(field);

		if (attribute != null) {
			List<String> candidates = new ArrayList<>();
			CSVParser parser = CSVParser.parse(file, Charset.defaultCharset(), CSVFormat.RFC4180);
			
			/* Iterates over the csv table looking for coincidences */
			for (CSVRecord row : parser) {
				Iterator<String> it = row.iterator();
				String name = "";
				
				/* It stops searching a row when an occurrence happens */
				while (it.hasNext() && "".equals(name)) {
					String next = it.next();
					
					if (next.contains(attribute) || attribute.contains(next)) {
						name = next;
					}
				}
				
				/* If element found, adds it to the list of candidates */
				if (!"".equals(name)) {
					candidates.add(name);
				}
			}
			parser.close();
			
			/* Fills an arraylist with the MAX first occurrences */
			ArrayList<String> list = new ArrayList<>();
			for (int i=0; i<candidates.size() && list.size()<MAX; i++) {
				if (!list.contains(candidates.get(i))){
					list.add(candidates.get(i));
				}
			}
			
			/* Returns the list in JSON format */
			return new Gson().toJson(list);
		} else return "[]";
		
	}

}
