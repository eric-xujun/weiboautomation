package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Account;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class AccountStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void createAccount(Account account){
		Key key = KeyFactory.createKey("Account", account.getUid());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", account.getUid());
		entity.setProperty("Name", account.getName());
		entity.setProperty("Password", account.getPassword());
        entity.setProperty("AccessToken", account.getAccessToken());
        datastore.put(entity);
	}
	
	public void createBatchAccounts(ArrayList<Account> accounts){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Account account: accounts){
			if(null == account.getName())
				break;
			Key key = KeyFactory.createKey("Account", account.getName());
			Entity entity = new Entity(key);
			entity.setProperty("Uid", account.getUid());
			entity.setProperty("Name", account.getName());
			entity.setProperty("Password", account.getPassword());
	        entity.setProperty("AccessToken", account.getAccessToken());
	        entity.setProperty("IsActive", account.getIsActive());
	        entity.setProperty("Group", account.getGroup());
	        entity.setProperty("Target", account.getTarget());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public void updateAccount(Account account){
		try {
			Key key = KeyFactory.createKey("Account", account.getUid());
			Entity entity = datastore.get(key);

            entity.setProperty("Name", account.getName());
            entity.setProperty("Link", account.getAccessToken());

            datastore.put(entity);

        } catch (EntityNotFoundException e) {
        }
	}
	
	public void deleteAccount(String uid) {
		Key key = KeyFactory.createKey("Account", uid);
        datastore.delete(key);
    }
	
	public ArrayList<Account> getAllAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Query query = new Query("Account");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Account account = new Account();
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setPassword(entity.getProperty("Password").toString());
			account.setAccessToken(entity.getProperty("AccessToken").toString());
			account.setTarget(entity.getProperty("Target").toString());
			accounts.add(account);
		}
		return accounts;
	}

	public ArrayList<Account> getActiveAccounts() {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Query query = new Query("Account");
		query.addFilter("AccessToken", FilterOperator.NOT_EQUAL, "NONE");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Account account = new Account();
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setPassword(entity.getProperty("Password").toString());
			account.setAccessToken(entity.getProperty("AccessToken").toString());
			accounts.add(account);
		}
		return accounts;
	}
	
	public void emptyAccount() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Account");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public Account getAccountByName(String name) {
		Account account = new Account();
		Query query = new Query("Account");
		query.addFilter("Name", FilterOperator.EQUAL, name);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setPassword(entity.getProperty("Password").toString());
			account.setAccessToken(entity.getProperty("AccessToken").toString());
		}
		return account;
	}
	
	public ArrayList<Account> getAccountsByGroup(String group) {
		ArrayList<Account> accounts = new ArrayList<Account>();
		Query query = new Query("Account");
		query.addFilter("Group", FilterOperator.EQUAL, group);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Account account = new Account();
			account.setUid(entity.getProperty("Uid").toString());
			account.setName(entity.getProperty("Name").toString());
			account.setPassword(entity.getProperty("Password").toString());
			account.setAccessToken(entity.getProperty("AccessToken").toString());
			account.setGroup(entity.getProperty("Group").toString());
			accounts.add(account);
		}
		return accounts;
	}
}
