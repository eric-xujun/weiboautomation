package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Account;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

public class HotStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void emptyHots() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Hots");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public void createBatchHots(ArrayList<Account> hots){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Account hot: hots){
			Key key = KeyFactory.createKey("Hots", hot.getUid());
			Entity entity = new Entity(key);
			entity.setProperty("Uid", hot.getUid());
			entity.setProperty("Name", hot.getName());
			entity.setProperty("Target", hot.getTarget());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public ArrayList<Account> getAllHots() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Query query = new Query("Hots");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Account account = new Account();
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setTarget(entity.getProperty("Target").toString());
			accounts.add(account);
		}
		return accounts;
	}
}
