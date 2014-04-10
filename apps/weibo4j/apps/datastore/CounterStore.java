package weibo4j.apps.datastore;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class CounterStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void store( int counter ){
		Key key = KeyFactory.createKey("Counter", "Counter");
		Entity entity = new Entity(key);
		entity.setProperty("Number", counter);
		datastore.put(entity);
	}
	
	public int getCounter() {
		int counter = 0;
		Query query = new Query("Counter");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			counter = Integer.parseInt(entity.getProperty("Number").toString());
		}
		return counter;
	}
}
