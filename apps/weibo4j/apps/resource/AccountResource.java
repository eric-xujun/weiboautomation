package weibo4j.apps.resource;

import java.util.ArrayList;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import weibo4j.apps.datastore.AccountStore;
import weibo4j.apps.datastore.AdsStore;
import weibo4j.apps.datastore.RecordStore;
import weibo4j.apps.datastore.ReferenceStore;
import weibo4j.apps.pojo.Account;

/**
 * Resource of Admin
 * @author Xu Jun
 * @version 1.0
 */
@Path("/accounts")
public class AccountResource {
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject list() throws JSONException {
		JSONObject res = new JSONObject();
		JSONArray items = new JSONArray();
		AccountStore store = new AccountStore();
		ArrayList<Account> accounts = store.getAllAccounts();
		for(int i = 0; i < accounts.size(); i++){
			Account account = accounts.get(i);
			JSONObject item = new JSONObject();
			item.put("Uid", account.getUid());
			item.put("Name", account.getName());
			item.put("Password", account.getPassword());
			item.put("AccessToken", account.getAccessToken());
			items.put(item);
		}
		res.put("items", items);
		return res;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject create(
			@FormParam("Uid") String uid,
            @FormParam("Name") String name,
            @FormParam("Password") String password,
            @FormParam("AccessToken") String accessToken
            ) throws JSONException {
		JSONObject res = new JSONObject();
		AccountStore store = new AccountStore();
		Account account = new Account();
		account.setUid(uid);
		account.setName(name);
		account.setPassword(password);
		account.setAccessToken(accessToken);
		store.createAccount(account);
		return res;
	}
	
	@DELETE
	@Path("{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteById(@PathParam("uid") String uid) {
		JSONObject res = new JSONObject();
		
		AccountStore store = new AccountStore();
		store.deleteAccount(uid);
		
		return res;
	}
	
	@GET
	@Path("all")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteAll() {
		JSONObject res = new JSONObject();
		
		AccountStore accountStore = new AccountStore();
		accountStore.emptyAccount();
		ReferenceStore referenceStore = new ReferenceStore();
		referenceStore.emptyReference();
		//AdsStore adsStore = new AdsStore();
		//adsStore.emptyAds();
		RecordStore recordStore = new RecordStore();
		recordStore.emptyRecord();
		
		return res;
	}
	
	@PUT
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject update(
			@FormParam("Uid") String uid,
            @FormParam("Name") String name,
            @FormParam("Password") String password,
            @FormParam("AccessToken") String accessToken
            ) throws JSONException {
		JSONObject res = new JSONObject();
		AccountStore store = new AccountStore();
		Account account = new Account();
		account.setUid(uid);
		account.setName(name);
		account.setPassword(password);
		account.setAccessToken(accessToken);
		store.updateAccount(account);
		return res;
	}
}
