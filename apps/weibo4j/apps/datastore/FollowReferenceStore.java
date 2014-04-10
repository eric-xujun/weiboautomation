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

public class FollowReferenceStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void store(Reference reference){
		Key key = KeyFactory.createKey("Follow Reference", reference.getRid());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", reference.getUid());
		entity.setProperty("Uname", reference.getUname());
		entity.setProperty("Rid", reference.getRid());
		entity.setProperty("Rname", reference.getRname());
		entity.setProperty("followersCount", reference.getFollowersCount());
        entity.setProperty("followersIncrease", reference.getFollowersIncrease());
		entity.setProperty("invalidRate", reference.getInvalidRate());
		datastore.put(entity);
	}
	
	public Reference getFollowReferenceById(String rid) {
		Query query = new Query("Follow Reference");
		query.addFilter("Rid", FilterOperator.EQUAL, rid);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			if(entity.getProperty("Rid") == null)
				return null;
			
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("followersCount").toString()));
			reference.setFollowersIncrease(Integer.parseInt(entity.getProperty("followersIncrease").toString()));
			reference.setInvalidRate(Float.parseFloat(entity.getProperty("invalidRate").toString()));
			return reference;
		}
		return null;
	}
}
