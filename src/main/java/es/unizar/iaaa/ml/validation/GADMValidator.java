package es.unizar.iaaa.ml.validation;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.vividsolutions.jts.geom.Geometry;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.opengis.feature.simple.SimpleFeature;
import org.zeroturnaround.zip.ZipUtil;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.util.List;

import es.unizar.iaaa.ml.util.DataStoreIterator;
import es.unizar.iaaa.ml.util.DataStoreReader;

/**
 * A GADM validator uses the GADM database of Global Administrative Areas to
 * perform geometry validation with annotated features.
 * 
 * @author Javier Beltran
 */
public class GADMValidator extends GeometryValidator {
	
	/**
	 * Creates a GADM validator based on the zip path and the number of files
	 * in which the collection is divided.
	 * @param zip the path of the zip.
	 * @param n the number of shapefiles and csv to be observed.
	 */
	public GADMValidator(String zip, int n) throws IOException {
        workingDir = Files.createTempDirectory(GADMValidator.class.getCanonicalName());
        ZipUtil.unpack(new File(zip), workingDir.toFile());
        String country = zip.substring(zip.lastIndexOf('/')+1, zip.indexOf('_'));
        
        String[] paths = new String[n];
        for (int i=0; i<paths.length; i++) {
        	paths[i] = workingDir.toString() + "/" + country + "_adm" + (i+1);
        }
        
		this.parsers = new CSVParser[paths.length];
		this.readers = new DataStoreReader[paths.length];
		for (int i=0; i<paths.length; i++) {
			this.parsers[i] = CSVParser.parse(
					FileSystems.getDefault().getPath(paths[i] + ".csv").toFile(), 
					Charset.defaultCharset(), CSVFormat.RFC4180.withHeader());
			this.readers[i] = DataStoreReader.shapefile(
					FileSystems.getDefault().getPath(paths[i] + ".shp").toFile());
		}
	}
	
	/**
	 * Given a simple feature, gets its annotated name and looks for the geometry
	 * with that name. If found, checks if both geometries are similar.
	 * 
	 * @param feature the simple feature to be validated.
	 * @return true, if a geometry is found and is similar to the one of the 
	 * feature; false, if the found geometry is not similar or if no geometry is
	 * found at all.
	 */
	@Override
	public boolean validateFeature(SimpleFeature feature) 
			throws IOException, FeatureNotAnnotatedException {
		/* Retrieves the main annotated name */
		String annotations = (String) feature.getAttribute("annotations");
		if (annotations == null) {
			throw new FeatureNotAnnotatedException();
		}
		
		List<String> jsonList = new Gson().fromJson(annotations, 
				new TypeToken<List<String>>(){}.getType());
		String annotatedName = jsonList.get(0);

        Geometry featureGeom = (Geometry) feature.getDefaultGeometry();
		
		/* Looks for a feature named as the annotation */
		for (int i=0; i<parsers.length; i++) {
			DataStoreIterator it = readers[i].iterator();
			for (CSVRecord row : parsers[i]) {
				Geometry itemGeometry = (Geometry)it.next().getDefaultGeometry();
				String nameFound = row.get("NAME_"+(i+1));
				if (StringUtils.equals(nameFound, annotatedName) &&
                        compareGeometries(itemGeometry,featureGeom)) {
                    return true;
                }
			}
			it.close();
		}
		
		return false;
	}
	
}
