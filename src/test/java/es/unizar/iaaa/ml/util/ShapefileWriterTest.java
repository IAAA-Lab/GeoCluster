package es.unizar.iaaa.ml.util;

import org.geotools.data.DataUtilities;
import org.junit.After;
import org.junit.AfterClass;
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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * This class contains the unit tests for the shapefile writer utility.
 * 
 * @author Javier Beltran
 */
public class ShapefileWriterTest {
	
	private static final String JAIN = "jain";
	private static final String PATH_JAIN_ZIP = "/data/jain.zip";
	private static final String PATH_JAIN_SHP = "jain.shp";
	private static final String PATH_NEW_JAIN = "src/test/resources/jain.shp";
	private static final String PATH_NEW_EMPTY = "src/test/resources/empty.shp";
	
	private DataStoreWriter writer;
	private static DataStoreReader dataset;
	private static Path path;
	
	@BeforeClass
	public static void setupAndUnzip() throws Exception {
        File zip = new File(ShapefileWriterTest.class.getResource(PATH_JAIN_ZIP).getFile());
        path = Files.createTempDirectory(ShapefileWriterTest.class.getCanonicalName());
        ZipUtil.unpack(zip, path.toFile());
        dataset = DataStoreReader.shapefile(FileSystems.getDefault()
                .getPath(path.toString(), PATH_JAIN_SHP).toFile());
    }
	
	@After
	public void closeWriter() {
		writer.close();
	}
	
	/**
	 * Removes the created files in resources folder.
	 */
	@AfterClass
	public static void removeUnzippedAndCreated() throws IOException {
		Files.walkFileTree(path, new RemoveVisitor());
		File f = new File("src/test/resources");
		File[] files = f.listFiles();
		for (File file : files) {
			if (file.getName().contains(JAIN)) {
				file.delete();
			}
		}
	}
	
	/**
	 * Unit test that checks features from an existing shapefile are written
	 * into a new one.
	 */
	@Test
	public void testWriteFeatures() throws IOException {
		writer = DataStoreWriter.shapefile(new File(PATH_NEW_JAIN));
		
		/* Writes features from shapefile into another shapefile */
		List<SimpleFeature> list = new ArrayList<>();
		for (SimpleFeature f : dataset) {
			list.add(f);
		}
		boolean write = writer.writeFeatures(DataUtilities.collection(list));
		
		assertTrue(write);
		
	}
	
	/**
	 * Unit test that checks that the writing is not performed when the
	 * feature list is empty; instead, it returns false.
	 */
	@Test
	public void testWriteEmptyList() throws IOException {
		writer = DataStoreWriter.shapefile(new File(PATH_NEW_EMPTY));
		
		/* Writes features from shapefile into another shapefile */
		List<SimpleFeature> list = new ArrayList<>();
		boolean write = writer.writeFeatures(DataUtilities.collection(list));
		
		assertFalse(write);
	}
	
}
