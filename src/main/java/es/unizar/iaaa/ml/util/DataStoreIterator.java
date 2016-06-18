package es.unizar.iaaa.ml.util;

import org.geotools.data.DataStore;
import org.geotools.data.FeatureReader;
import org.geotools.data.Query;
import org.geotools.data.Transaction;
import org.opengis.feature.simple.SimpleFeature;
import org.opengis.feature.simple.SimpleFeatureType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;

/**
 * A DataStoreIterator allows to iterate over the elements of a data store.
 * 
 * @author Javier Beltran
 */
public class DataStoreIterator implements Iterator<SimpleFeature>, AutoCloseable {
	
    private static Logger LOGGER = LoggerFactory.getLogger(DataStoreIterator.class);
    
    private FeatureReader<SimpleFeatureType, SimpleFeature> reader;
    private boolean open;
    private boolean fail;
    private String featureType;
    
    public DataStoreIterator(DataStore dataStore, String featureType) {
        this.featureType = featureType;
        Query query = new Query(featureType);
        try {
            reader = dataStore.getFeatureReader(query, Transaction.AUTO_COMMIT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        open = true;
    }

    /**
     * Closes the data store iterator. This should always be done after using it.
     */
    @Override
    public void close() throws IOException {
        if (open) {
            open = false;
            reader.close();
        }
    }

    /**
     * Checks whether the data store has another element to be read.
     * 
     * @return true, if there is another element; false otherwise.
     */
    @Override
    public boolean hasNext() {
        try {
            boolean hasNext = reader.hasNext();
            if (!hasNext) {
                close();
            }
            return hasNext;
        } catch (Exception e) {
            fail = true;
            return false;
        }
    }

    /**
     * Retrieves the next element from the data store, if it exists.
     * 
     * @return the next element, or null if all elements have been iterated.
     */
    @Override
    public SimpleFeature next() {
        try {
            return reader.next();
        } catch (IOException e) {
            fail = true;
            return null;
        }
    }

    @Override
    public void remove() {

    }

    @Override
    protected void finalize() throws Throwable {
        if (open) {
            reader.close();
            LOGGER.warn("If you see this message, you have an iterator on "+featureType+" that needs to be closed!");
        }
    }

    public boolean isOk() {
        return !fail;
    }
}
