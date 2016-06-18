package es.unizar.iaaa.ml.util;

import org.geotools.data.DataStore;
import org.geotools.data.DataStoreFinder;
import org.geotools.data.mysql.MySQLDataStoreFactory;
import org.opengis.feature.simple.SimpleFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A DataStoreReader is an utility for reading the features stored in a data
 * store. Implementations of it include SHPReader, for shapefiles; and
 * MySQLReader, for MySQL databases.
 * 
 * @author Javier Beltran
 */
public class DataStoreReader implements Iterable<SimpleFeature> {

	/**
	 * Creates a data store reader for a shapefile.
	 * 
	 * @param path the path of the shapefile.
	 * @return the created DataStoreReader.
	 */
    public static DataStoreReader shapefile(File path) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put("url", path.toURI().toURL());
        return new DataStoreReader(map);
    }

    /**
     * Creates a data store reader for a MySQL database connection.
     * 
     * @param host the host to connect to the database.
     * @param port the port to connect to the database.
     * @param db the database to be read.
     * @param user the user to access the database.
     * @param pass the password of the user accessing the database.
     * @param table the table to be read.
     * @return the created DataStoreReader.
     */
    public static DataStoreReader mysql(String host, String port, String db, String user, String pass,
                String table) throws IOException {
        Map<String, Object> map = new HashMap<>();
        map.put(MySQLDataStoreFactory.DBTYPE.key, "mysql");
        map.put(MySQLDataStoreFactory.HOST.key, host);
        map.put(MySQLDataStoreFactory.PORT.key, port);
        map.put(MySQLDataStoreFactory.DATABASE.key, db);
        map.put(MySQLDataStoreFactory.USER.key, user);
        map.put(MySQLDataStoreFactory.PASSWD.key, pass);
        return new DataStoreReader(map, table);
    }

    private DataStore dataStore;
    private String type;

    protected DataStoreReader(Map<String, Object> parameters) throws IOException {
        dataStore = DataStoreFinder.getDataStore(parameters);
        type =  dataStore.getTypeNames()[0];
    }

    protected DataStoreReader(Map<String, Object> parameters, String type) throws IOException {
        dataStore = DataStoreFinder.getDataStore(parameters);
        this.type = type;
    }

    /**
     * Returns an iterator for the data store reader.
     * 
     * @return the iterator.
     */
    @Override
    public DataStoreIterator iterator() {
        return new DataStoreIterator(dataStore, type);
    }
}

