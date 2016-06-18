package es.unizar.iaaa.ml.util;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.DefaultTransaction;
import org.geotools.data.Transaction;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.geotools.data.simple.SimpleFeatureCollection;
import org.geotools.data.simple.SimpleFeatureSource;
import org.geotools.data.simple.SimpleFeatureStore;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * A data store writer is an interface for writing features into a certain
 * data store, like a shapefile or a database.
 * 
 * @author Javier Beltran
 */
public class DataStoreWriter {
	
	private DataStore dataStore;
	
	public static DataStoreWriter shapefile(File path) throws IOException {
		Map<String, Serializable> params = new HashMap<>();
		params.put("url", path.toURI().toURL());
		params.put("create spatial index", Boolean.TRUE);
		
		return new DataStoreWriter(params);
	}
	
	public static DataStoreWriter mysql(String host, String port, String db, String user, 
			String pass) throws IOException {
		Map<String, Serializable> params = new HashMap<>();
		params.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
        params.put(MySQLDataStoreFactory.HOST.key, host);
        params.put(MySQLDataStoreFactory.PORT.key, port);
        params.put(MySQLDataStoreFactory.DATABASE.key, db);
        params.put(MySQLDataStoreFactory.USER.key, user);
        params.put(MySQLDataStoreFactory.PASSWD.key, pass);
        
        return new DataStoreWriter(params);
	}
	
	public DataStoreWriter(Map<String, Serializable> params) throws IOException {
		dataStore = DataStoreFinder.getDataStore(params);
	}
	
	/**
	 * Writes a collection of features in the specified data store.
	 * 
	 * @param collection the collection of simple features.
	 * @return true, if the features could be written; false otherwise.
	 */
	public boolean writeFeatures(SimpleFeatureCollection collection) throws IOException {
		if (!collection.isEmpty()) {
			dataStore.createSchema(collection.getSchema());
			SimpleFeatureSource featureSource = dataStore
					.getFeatureSource(collection.getSchema().getName().getLocalPart());
			
			if (featureSource != null) {
				Transaction transaction = new DefaultTransaction("create");
				SimpleFeatureStore featureStore = (SimpleFeatureStore) featureSource;
				featureStore.setTransaction(transaction);
				
				boolean result;
				try {
					featureStore.addFeatures(collection);
					transaction.commit();
					result = true;
				} catch (Exception e) {
					transaction.rollback();
					result = false;
				} finally {
					transaction.close();
				}
				return result;
			}
		}
		return false;
	}
	
	/**
	 * Closes the data store writer. This should always be done after using it.
	 */
	public void close() {
		dataStore.dispose();
	}
	
}
