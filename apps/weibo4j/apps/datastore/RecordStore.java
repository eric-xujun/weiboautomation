package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Record;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class RecordStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public void createRecord(Record record){
		Key key = KeyFactory.createKey("Record", record.getUid() + record.getStatusId());
		Entity entity = new Entity(key);
		entity.setProperty("Uid", record.getUid());
		entity.setProperty("StatusId", record.getStatusId());
        datastore.put(entity);
	}
	
	public void createBatchRecords(ArrayList<Record> records){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Record record: records){
			Key key = KeyFactory.createKey("Record", record.getUid() + record.getStatusId());
			Entity entity = new Entity(key);
			entity.setProperty("Uid", record.getUid());
			entity.setProperty("StatusId", record.getStatusId());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
	
	public ArrayList<Record> getAllRecords() {
		ArrayList<Record> records = new ArrayList<Record>();
		Query query = new Query("Record");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Record record = new Record();
			record.setUid(entity.getProperty("Uid").toString());
			record.setStatusId(entity.getProperty("StatusId").toString());
			records.add(record);
		}
		return records;
	}

	public void emptyRecord() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Record");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public Record getRecordByUidAndStatueId(String uid, String statusId) {
		Record record = new Record();
		Query query = new Query("Record");
		query.addFilter("Uid", FilterOperator.EQUAL, uid);
		query.addFilter("StatusId", FilterOperator.EQUAL, statusId);
		PreparedQuery pq = datastore.prepare(query);
		if(!pq.asIterator().hasNext())
			return null;
		for (Entity entity : pq.asIterable()){
			record.setUid(entity.getProperty("Uid").toString());
			record.setStatusId(entity.getProperty("StatusId").toString());
		}
		return record;
	}
}
