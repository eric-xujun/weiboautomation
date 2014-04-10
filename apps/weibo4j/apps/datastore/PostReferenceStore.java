package weibo4j.apps.datastore;

import weibo4j.apps.pojo.Reference;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class PostReferenceStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void store(Reference reference){
		Key key = KeyFactory.createKey("Post Reference", reference.getUid());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", reference.getUid());
		entity.setProperty("Uname", reference.getUname());
		entity.setProperty("Rid", reference.getRid());
		entity.setProperty("Rname", reference.getRname());
		entity.setProperty("updateCount", reference.getUpdateCount());
		datastore.put(entity);
	}
	
	public Reference getPostReferenceById(String rid) {
		Reference reference = new Reference();
		Query query = new Query("Post Reference");
		query.addFilter("Rid", FilterOperator.EQUAL, rid);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setUpdateCount(Integer.parseInt(entity.getProperty("updateCount").toString()));
		}
		return reference;
	}
}
