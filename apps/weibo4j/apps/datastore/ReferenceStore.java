package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Reference;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class ReferenceStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void createReference(Reference reference){
		Key key = KeyFactory.createKey("Reference", reference.getUid() + reference.getRid());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", reference.getUid());
		entity.setProperty("Uname", reference.getUname());
        entity.setProperty("Rid", reference.getRid());
		entity.setProperty("Rname", reference.getRname());
		entity.setProperty("Tag", reference.getTag());
        datastore.put(entity);
	}

	public void createBatchReferences(ArrayList<Reference> references){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Reference reference: references){
			if(null == reference.getUname())
				continue;
			Key key = KeyFactory.createKey("Reference", reference.getUname() + reference.getRname());
			Entity entity = new Entity(key);
			entity.setProperty("Uid", reference.getUid());
			entity.setProperty("Uname", reference.getUname());
	        entity.setProperty("Rid", reference.getRid());
			entity.setProperty("Rname", reference.getRname());
			entity.setProperty("FollowersCount", reference.getFollowersCount());
			entity.setProperty("Tag", reference.getTag());
			entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public void updateReference(Reference reference){
		try {
			Key key = KeyFactory.createKey("Reference", reference.getUid() + reference.getRid());
			Entity entity = datastore.get(key);

			entity.setProperty("Uid", reference.getUid());
			entity.setProperty("Uname", reference.getUname());
	        entity.setProperty("Rid", reference.getRid());
			entity.setProperty("Rname", reference.getRname());
			entity.setProperty("FollowersCount", reference.getFollowersCount());
			entity.setProperty("Tag", reference.getTag());

            datastore.put(entity);

        } catch (EntityNotFoundException e) {
        }
	}
	
	public void deleteReference(String id) {
		Key key = KeyFactory.createKey("Reference", id);
        datastore.delete(key);
    }
	
	public ArrayList<Reference> getAllReferences() {
		ArrayList<Reference> references = new ArrayList<Reference>();
		Query query = new Query("Reference");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			if(entity.getProperty("Uid") == null)
				System.out.println(entity);
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			reference.setTag(entity.getProperty("Tag").toString());
			references.add(reference);
		}
		
		return references;
	}
	
	public void emptyReference() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Reference");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}

	public ArrayList<Reference> getRepostReferencesByName(String name) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		Query query = new Query("Reference");
		query.addFilter("Uname", FilterOperator.EQUAL, name);
		query.addFilter("Tag", FilterOperator.EQUAL, "post");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			reference.setTag(entity.getProperty("Tag").toString());
			references.add(reference);
		}
		return references;
	}
	
	public ArrayList<Reference> getFollowReferencesByName(String name) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		Query query = new Query("Reference");
		query.addFilter("Uname", FilterOperator.EQUAL, name);
		query.addFilter("Tag", FilterOperator.EQUAL, "follow");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			if(entity.getProperty("Uid") == null){
				System.out.println(entity);
			}
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			reference.setTag(entity.getProperty("Tag").toString());
			references.add(reference);
		}
		return references;
	}
	
	public ArrayList<Reference> getRepostReferencesById(String uid) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		Query query = new Query("Reference");
		query.addFilter("Uid", FilterOperator.EQUAL, uid);
		query.addFilter("Tag", FilterOperator.EQUAL, "post");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			reference.setTag(entity.getProperty("Tag").toString());
			references.add(reference);
		}
		return references;
	}
	
	public ArrayList<Reference> getFollowReferencesById(String uid) {
		ArrayList<Reference> references = new ArrayList<Reference>();
		Query query = new Query("Reference");
		query.addFilter("Uid", FilterOperator.EQUAL, uid);
		query.addFilter("Tag", FilterOperator.EQUAL, "follow");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Reference reference = new Reference();
			if(entity.getProperty("Uid") == null){
				System.out.println(entity);
			}
			reference.setUid(entity.getProperty("Uid").toString());
			reference.setUname(entity.getProperty("Uname").toString());
			reference.setRid(entity.getProperty("Rid").toString());
			reference.setRname(entity.getProperty("Rname").toString());
			reference.setFollowersCount(Integer.parseInt(entity.getProperty("FollowersCount").toString()));
			reference.setTag(entity.getProperty("Tag").toString());
			references.add(reference);
		}
		return references;
	}
}
