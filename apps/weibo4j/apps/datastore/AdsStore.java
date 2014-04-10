package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Ads;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class AdsStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void emptyAds() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Ads");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public void createAds(Ads ads){
		Key key = KeyFactory.createKey("Ads", ads.getStatusId());
		Entity entity = new Entity(key);
		entity.setProperty("StatusId", ads.getStatusId());
		entity.setProperty("Type", ads.getType());
		entity.setProperty("Target", ads.getTarget());
        datastore.put(entity);
	}
	
	public void createBatchAds(ArrayList<Ads> adss){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Ads ads: adss){
			Key key = KeyFactory.createKey("Ads", ads.getStatusId());
			Entity entity = new Entity(key);
			entity.setProperty("StatusId", ads.getStatusId());
			entity.setProperty("Type", ads.getType());
			entity.setProperty("Target", ads.getTarget());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public Ads getAdsByType(String type){
		Ads ads = new Ads();
		Query query = new Query("Ads");
		query.addFilter("Type", FilterOperator.EQUAL, type);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			ads.setStatusId(entity.getProperty("StatusId").toString());
			ads.setType(entity.getProperty("Type").toString());
			ads.setTarget(entity.getProperty("Target").toString());
		}
		return ads;
	}
	
	public ArrayList<Ads> getAllAds() {
		ArrayList<Ads> ads = new ArrayList<Ads>();
		Query query = new Query("Ads");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Ads ad = new Ads();
			ad.setStatusId(entity.getProperty("StatusId").toString());
			ad.setType(entity.getProperty("Type").toString());
			ad.setTarget(entity.getProperty("Target").toString());
			ads.add(ad);
		}
		return ads;
	}
}
