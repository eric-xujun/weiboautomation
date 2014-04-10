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
import com.google.appengine.api.datastore.Query.FilterOperator;

public class StaticsStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void store(ArrayList<Account> accounts){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Account account: accounts){
			if(null == account.getName())
				continue;
			Key key = KeyFactory.createKey("Statics", account.getName());
			Entity entity = new Entity(key);
			entity.setProperty("Uid", account.getUid());
			entity.setProperty("Name", account.getName());
			entity.setProperty("Password", account.getPassword());
			entity.setProperty("FriendsCount", account.getFriendsCount());
	        entity.setProperty("FollowersCount", account.getFollowersCount());
			entity.setProperty("FriendsIncrease", account.getFriendsIncrease());
	        entity.setProperty("FollowersIncrease", account.getFollowersIncrease());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public void storeStatics(Account account){
		Key key = KeyFactory.createKey("Statics", account.getUid());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", account.getUid());
		entity.setProperty("Name", account.getName());
		entity.setProperty("Password", account.getPassword());
		entity.setProperty("FriendsCount", account.getFriendsCount());
        entity.setProperty("FollowersCount", account.getFollowersCount());
		entity.setProperty("FriendsIncrease", account.getFriendsIncrease());
        entity.setProperty("FollowersIncrease", account.getFollowersIncrease());
        entity.setProperty("Active", account.getIsActive());
        datastore.put(entity);
	}
	
	public Account getAccountStaticsById(String uid) {
		Account account = new Account();
		Query query = new Query("Statics");
		query.addFilter("Uid", FilterOperator.EQUAL, uid);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			if(entity.getProperty("Uid") == null)
				return null;
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			account.setFriendsCount(Integer.parseInt(entity.getProperty("FriendsCount").toString()));
			account.setFriendsIncrease(Integer.parseInt(entity.getProperty("FriendsIncrease").toString()));
			account.setFollowersIncrease(Integer.parseInt(entity.getProperty("FollowersIncrease").toString()));
		}
		return account;
	}
	
	public void emptyStatics() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Statics");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public ArrayList<Account> getAllStatics() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Query query = new Query("Statics");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Account account = new Account();
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setPassword(entity.getProperty("Password").toString());
			accounts.add(account);
		}
		return accounts;
	}
}
