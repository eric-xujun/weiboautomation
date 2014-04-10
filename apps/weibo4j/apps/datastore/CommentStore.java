package weibo4j.apps.datastore;

import java.util.ArrayList;

import weibo4j.apps.pojo.Comment;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

public class CommentStore {
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	
	public ArrayList<String> getCommentsByType(String type){
		ArrayList<String> comments = new ArrayList<String>();
		Query query = new Query("Comment");
		query.addFilter("Type", FilterOperator.EQUAL, type);
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			String comment = entity.getProperty("Comment").toString();
			comments.add(comment);
		}
		return comments;
	}
	
	public ArrayList<Comment> getAllComments(){
		ArrayList<Comment> comments = new ArrayList<Comment>();
		Query query = new Query("Comment");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
			Comment comment = new Comment();
			String str = entity.getProperty("Comment").toString();
			String type = entity.getProperty("Type").toString();
			comment.setComment(str);
			comment.setType(type);
			comments.add(comment);
		}
		return comments;
	}
	
	public void emptyComments() {
		ArrayList<Key> keys = new ArrayList<Key>();
		Query query = new Query("Comment");
		PreparedQuery pq = datastore.prepare(query);
		for (Entity entity : pq.asIterable()){
	      keys.add(entity.getKey());
	    }
		datastore.delete(keys);
	}
	
	public void createBatchComments(ArrayList<Comment> comments){
		ArrayList<Entity> entities = new ArrayList<Entity>();
		for(Comment comment: comments){
			Key key = KeyFactory.createKey("Comment", comment.getComment());
			Entity entity = new Entity(key);
			entity.setProperty("Comment", comment.getComment());
			entity.setProperty("Type", comment.getType());
	        entities.add(entity);
		}
		datastore.put(entities);
	}
}
