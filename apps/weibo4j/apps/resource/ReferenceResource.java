package weibo4j.apps.resource;

import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import weibo4j.apps.datastore.ReferenceStore;
import weibo4j.apps.pojo.Reference;

/**
 * Resource of Admin
 * @author Xu Jun
 * @version 1.0
 */
@Path("/references")
public class ReferenceResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject list() throws JSONException {
		JSONObject res = new JSONObject();
		JSONArray items = new JSONArray();
		ReferenceStore store = new ReferenceStore();
		ArrayList<Reference> references = store.getAllReferences();
		for(int i = 0; i < references.size(); i++){
			Reference reference = references.get(i);
			JSONObject item = new JSONObject();
			item.put("Uid", reference.getUid());
			item.put("Uname", reference.getUname());
			item.put("Rid", reference.getRid());
			item.put("Rname", reference.getRname());
			item.put("Tag", reference.getTag());
			items.put(item);
		}
		res.put("items", items);
		return res;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject create(
			@FormParam("Uid") String uid,
            @FormParam("Uname") String uname,
            @FormParam("Rid") String rid,
            @FormParam("Rname") String rname,
            @FormParam("Tag") String tag
            ) throws JSONException {
		JSONObject res = new JSONObject();
		ReferenceStore store = new ReferenceStore();
		Reference reference = new Reference();
		reference.setUid(uid);
		reference.setUname(uname);
		reference.setRid(rid);
		reference.setRname(rname);
		reference.setTag(tag);
		store.createReference(reference);
		return res;
	}
	
	@DELETE
	@Path("{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteById(@PathParam("id") String id) {
		JSONObject res = new JSONObject();
		
		ReferenceStore store = new ReferenceStore();
		store.deleteReference(id);
		
		return res;
	}
	
	@DELETE
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteAll() {
		JSONObject res = new JSONObject();
		
		ReferenceStore store = new ReferenceStore();
		store.emptyReference();
		
		return res;
	}
}
