package weibo4j.apps.resource;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

import weibo4j.Friendships;
import weibo4j.ShortUrl;
import weibo4j.Timeline;
import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.apps.datastore.AccountStore;
import weibo4j.apps.datastore.AdsStore;
import weibo4j.apps.datastore.CommentStore;
import weibo4j.apps.datastore.FollowReferenceStore;
import weibo4j.apps.datastore.HotStore;
import weibo4j.apps.datastore.OverviewStore;
import weibo4j.apps.datastore.PostReferenceStore;
import weibo4j.apps.datastore.RecordStore;
import weibo4j.apps.datastore.ReferenceStore;
import weibo4j.apps.datastore.StaticsStore;
import weibo4j.apps.pojo.Account;
import weibo4j.apps.pojo.Ads;
import weibo4j.apps.pojo.Comment;
import weibo4j.apps.pojo.Record;
import weibo4j.apps.pojo.Reference;
import weibo4j.model.Status;
import weibo4j.model.StatusWapper;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

@Path("/follow")
public class ScheduleTaskResource {
	public String defaultMaleImageUrl = "http://tp2.sinaimg.cn/{uid}/50/0/1";
	public String defaultFemaleImageUrl = "http://tp2.sinaimg.cn/{uid}/50/0/0";
	public static String[] repostStr = {"[good]", "[赞]", "[心]", "[呵呵]", "[给力]", "[威武]", "[浮云]"};

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public void followpost() {
		follow();
		post();
	}
	
	public void follow() {
		AccountStore accountStore = new AccountStore();
		ReferenceStore referenceStore = new ReferenceStore();
		
		FollowReferenceStore followReferenceStore = new FollowReferenceStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		Collections.shuffle(accounts);
		
		for (Account account : accounts) {
			String uid = account.getUid();
			String accessToken = account.getAccessToken();
			
			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			ArrayList<Reference> followReferences = referenceStore.getFollowReferencesById(uid);

			if(followReferences.size() == 0)
				continue;
			
			Reference reference = this.getRandomFollowReference(followReferences, referenceStore, followReferenceStore);
			
			Friendships fm = new Friendships();
			int i = 0, k = 0;
			try{
				UserWapper userWapper = fm.getFollowersById(reference.getRid(), 200, 0);
				List<User> users = userWapper.getUsers();
				for(int j = 0; i < users.size() && j < 30; i++){
					User user = users.get(i);
					if (!isInvalid(user)){
						fm.createFriendshipsById(user.getId());
						Thread.sleep(5000);
						j++;
					}
					if (user.getFollowersCount() < 3)
						k++;
				}
				Thread.sleep(30000);
			} catch (Exception e) {
				System.out.println(reference.getRname());
				e.printStackTrace();
			}
			if(i != 0){
				float invalidRate = 100 * k / i;
				reference.setInvalidRate(invalidRate);
				followReferenceStore.store(reference);
			}
		}
	}
	
	private Reference getRandomFollowReference(ArrayList<Reference> references, ReferenceStore referenceStore, FollowReferenceStore followReferenceStore){
		Collections.shuffle(references);
		Reference reference = references.get(0);
		
		for(Reference r: references){
			Reference followReference = followReferenceStore.getFollowReferenceById(r.getRid());

			int followersCount = 0;
			if(followReference == null)
				followersCount = r.getFollowersCount();
			else
				followersCount = followReference.getFollowersCount();
			
			int currentCount = 0;
			Users um = new Users();
			try {
				User user = um.showUserById(r.getRid());
				currentCount = user.getFollowersCount();
			} catch (WeiboException e1) {
				e1.printStackTrace();
			}
			int increaseCount = currentCount - followersCount;
			
			if(increaseCount < 0){
				if(followReference == null){
					r.setFollowersCount(currentCount);
					r.setFollowersIncrease(increaseCount);
					r.setInvalidRate(0);
					followReferenceStore.store(r);
				}else{
					followReference.setFollowersCount(currentCount);
					followReference.setFollowersIncrease(increaseCount);
					followReferenceStore.store(followReference);
				}
			}
			
			if(increaseCount > -50){
				reference = r;
				reference.setFollowersCount(currentCount);
				reference.setFollowersIncrease(increaseCount);
				reference.setInvalidRate(0);
				break;
			}
		}
		
		return reference;
	}
	
	@GET
	@Path("post")
	@Produces(MediaType.APPLICATION_JSON)
	public void post() {
		AccountStore accountStore = new AccountStore();
		ReferenceStore referenceStore = new ReferenceStore();
		//ArrayList<Account> accounts = accountStore.getAccountsByGroup("P");
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		Collections.shuffle(accounts);
		for (Account account : accounts) {
			String uid = account.getUid();
			String name = account.getName();
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			
			ArrayList<Reference> postReferences = referenceStore.getRepostReferencesById(uid);
			Collections.shuffle(postReferences);
			Timeline tm = new Timeline();
			ShortUrl su = new ShortUrl();
			try {
				if(postReferences.size() == 0){
					continue;
				}
				Reference reference = postReferences.get(0);
				String rid = reference.getRid();
				StatusWapper statusWapper = tm.getUserTimelineByUid(rid);
				ArrayList<Status> statuses = new ArrayList<Status>();
				int count = 0;
				for(Status s : statusWapper.getStatuses()){
					Status retweetedStatus = s.getRetweetedStatus();
					Date createAt = s.getCreatedAt();
					
					Calendar calendar = Calendar.getInstance();   
					calendar.add(Calendar.DAY_OF_MONTH, -1);
					Date yesterday = calendar.getTime();
					
					if(!txtVerification(s.getText())){
						continue;
					}
					
					if(s.getText().contains("@"))
						continue;

					String[] ss = s.getText().split("@");
					if(ss.length > 1)
						continue;
					
					if(s.getText().indexOf("t.cn") != -1){
						int index = s.getText().indexOf("http://t.cn");
						//System.out.println(s.getText());
						String url_short = s.getText().substring(index, index + 19);
						//System.out.println(url_short);
						JSONObject jo = su.shortToLongUrl(url_short);
						JSONArray urls = (JSONArray) jo.get("urls");
						JSONObject url = (JSONObject) urls.get(0);
						String url_long = url.get("url_long").toString();
						//System.out.println(url_long);
						if(!urlVerification(url_long)){
							continue;
						}
					}
					
					if(createAt.after(yesterday)){
						count++;
					}else{
						continue;
					}
					
					if(retweetedStatus == null){
						statuses.add(s);
					}
					
					if(statuses.size() == 10)
						break;
				}
				
				PostReferenceStore postReferenceStore = new PostReferenceStore();
				reference.setUpdateCount(count);
				postReferenceStore.store(reference);
				
				Collections.shuffle(statuses);
				if(statuses.size() == 0)
					continue;

				StatusWapper iStatusWapper = tm.getUserTimelineByUid(uid);
				Status status = new Status();
				for(Status s : statuses){
					String txt = s.getText();
					boolean isDuplicated = false;
					for(Status is : iStatusWapper.getStatuses()){
						String itxt = is.getText();
						if(txt.equals(itxt)){
							isDuplicated = true;
							break;
						}
					}
					if(!isDuplicated){
						status = s;
						break;
					}
				}
				
				String txt = status.getText();
				String referenceName = reference.getRname();
				if(txt.indexOf(referenceName) != -1){
					txt = txt.replaceAll(referenceName, name);
				}
				
				String url = status.getOriginalPic();
				//System.out.print("####################");
				//System.out.println(status.getText());
				//System.out.println(url);
				if(url == null || url == "")
					tm.UpdateStatus(txt);
				else{
					InputStream is = download(url);
					tm.updateStatus(txt, is);
				}
				Thread.sleep(30000);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@GET
	@Path("post2")
	@Produces(MediaType.APPLICATION_JSON)
	public void post2() {
		AccountStore accountStore = new AccountStore();
		ReferenceStore referenceStore = new ReferenceStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		Collections.shuffle(accounts);
		for (Account account : accounts) {
			String uid = account.getUid();
			String name = account.getName();
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);

			Timeline tm = new Timeline();
			ShortUrl su = new ShortUrl();
			
			try {
				StatusWapper statusWapper = tm.getUserTimelineByUid(uid);
				
				ArrayList<Status> statuses = new ArrayList<Status>();
				
				boolean hasPosted = false;
				int statusesCount = 0;
				for(Status s : statusWapper.getStatuses()){
					if(statusesCount > 2)
						break;
					Status retweetedStatus = s.getRetweetedStatus();
					if(retweetedStatus == null){
						hasPosted = true;
					}
					statusesCount++;
				}
				if(hasPosted)
					continue;
				
				ArrayList<Reference> postReferences = referenceStore.getRepostReferencesById(uid);
				Collections.shuffle(postReferences);
				if(postReferences.size() == 0){
					continue;
				}
				Reference reference = postReferences.get(0);
				String rid = reference.getRid();
				statusWapper = tm.getUserTimelineByUid(rid);
				statuses = new ArrayList<Status>();
				int count = 0;
				for(Status s : statusWapper.getStatuses()){
					Status retweetedStatus = s.getRetweetedStatus();
					
					if(!txtVerification(s.getText())){
						continue;
					}

					if(s.getText().contains("@"))
						continue;

					String[] ss = s.getText().split("@");
					if(ss.length > 1)
						continue;
					
					if(s.getText().indexOf("t.cn") != -1){
						int index = s.getText().indexOf("http://t.cn");
						String url_short = s.getText().substring(index, index + 19);
						JSONObject jo = su.shortToLongUrl(url_short);
						JSONArray urls = (JSONArray) jo.get("urls");
						JSONObject url = (JSONObject) urls.get(0);
						String url_long = url.get("url_long").toString();
						if(!urlVerification(url_long)){
							continue;
						}
					}

					if(retweetedStatus == null){
						statuses.add(s);
					}
					
					if(statuses.size() == 10)
						break;
				}
				/*
				PostReferenceStore postReferenceStore = new PostReferenceStore();
				reference.setUpdateCount(count);
				postReferenceStore.store(reference);
				*/
				Collections.shuffle(statuses);
				if(statuses.size() == 0)
					continue;
				Status status = statuses.get(0);
				String txt = status.getText();
				String referenceName = reference.getRname();
				if(txt.indexOf(referenceName) != -1){
					txt = txt.replaceAll(referenceName, name);
				}
				
				String url = status.getOriginalPic();
				if(url == null || url == "")
					tm.UpdateStatus(txt);
				else{
					InputStream is = download(url);
					tm.updateStatus(txt, is);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@GET
	@Path("repost")
	@Produces(MediaType.APPLICATION_JSON)
	public void repost() {
		AccountStore accountStore = new AccountStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		ArrayList<Account> activeAccounts = accountStore.getAllAccounts();
		for (Account account : accounts) {
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			Collections.shuffle(activeAccounts);
			Timeline tm = new Timeline();
			try {
				StatusWapper statusWapper = tm.getUserTimelineByUid(activeAccounts.get(0).getUid());
				for(Status s : statusWapper.getStatuses()){
					Status retweetedStatus = s.getRetweetedStatus();
					if(retweetedStatus == null){
						tm.Repost(s.getId());
						Thread.sleep(3000);
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@GET
	@Path("repost2")
	@Produces(MediaType.APPLICATION_JSON)
	public void repost2() {
		AccountStore accountStore = new AccountStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		//ArrayList<Account> accounts = accountStore.getAccountsByGroup("A");
		//accounts.addAll(accountStore.getAccountsByGroup("B"));
		//accounts.addAll(accountStore.getAccountsByGroup("C"));
		Collections.shuffle(accounts);
		//accounts.addAll(accountStore.getAccountsByGroup("G"));
		for (Account account : accounts) {
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			Timeline tm = new Timeline();
			try {
				StatusWapper statusWapper = tm.getUserTimelineByUid(account.getUid());
				
				int maxRepostsCount = 0;
				int maxIndex = 0;
				List<Status> statuses = new ArrayList<Status>();
				for(int i = 0; i < statusWapper.getStatuses().size(); i++){
					Status s = statusWapper.getStatuses().get(i);
					Status retweetedStatus = s.getRetweetedStatus();
					if(retweetedStatus == null){
						int repostsCount = s.getRepostsCount();
						if(repostsCount > maxRepostsCount){
							maxIndex = i;
							maxRepostsCount = repostsCount;
						}
						statuses.add(s);
					}
				}
				
				Random r = new Random();
				int randomIndex = r.nextInt(7);
				Collections.shuffle(statuses);
				
				Status status = statuses.get(0);
				
				tm.Repost(status.getId(), repostStr[randomIndex], 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@GET
	@Path("repost3")
	@Produces(MediaType.APPLICATION_JSON)
	public void repost3() {
		AccountStore accountStore = new AccountStore();
		HotStore hotStore = new HotStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		ArrayList<Account> hotAccounts = hotStore.getAllHots();
		Collections.shuffle(accounts);
		Collections.shuffle(hotAccounts);
		for (Account account : accounts) {
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			Timeline tm = new Timeline();
			
			for (Account hot : hotAccounts) {
				if(!hot.getTarget().equals(account.getTarget()))
					continue;
				try {
					StatusWapper statusWapper = tm.getUserTimelineByUid(hot.getUid());
					int maxRepostsCount = 0;
					int maxIndex = 0;
					List<Status> statuses = new ArrayList<Status>();
					for(int i = 0; i < statusWapper.getStatuses().size(); i++){
						Status s = statusWapper.getStatuses().get(i);
						Status retweetedStatus = s.getRetweetedStatus();
						if(retweetedStatus == null){
							int repostsCount = s.getRepostsCount();
							if(repostsCount > maxRepostsCount){
								maxIndex = i;
								maxRepostsCount = repostsCount;
							}
							statuses.add(s);
						}
					}
					Status maxRepostStatus = statusWapper.getStatuses().get(maxIndex);
					Status status;
					Random r = new Random();
					int statusSelector = r.nextInt(2);
					if(statusSelector == 0)
						status = maxRepostStatus;
					else{
						Collections.shuffle(statuses);
						status = statuses.get(0);
					}
					
					int randomIndex = r.nextInt(7);
					try {
						tm.Repost(status.getId(), repostStr[randomIndex], 0);
						Thread.sleep(10000);
						break;
					} catch (WeiboException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (WeiboException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@GET
	@Path("ads")
	@Produces(MediaType.APPLICATION_JSON)
	public void repostAds() {
		AccountStore accountStore = new AccountStore();
		RecordStore recordStore = new RecordStore();
		CommentStore commentStore = new CommentStore();
		AdsStore adsStore = new AdsStore();
		
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		ArrayList<Ads> adss = adsStore.getAllAds();
		ArrayList<Record> records = recordStore.getAllRecords();
		ArrayList<Comment> icomments = commentStore.getAllComments();

		Collections.shuffle(accounts);
		
		int i = 0;
		for (Account account : accounts) {

			String accessToken = account.getAccessToken();
			
			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			
			String accountTarget = account.getTarget();
			String uid = account.getUid();
			Collections.shuffle(adss);
			
			for (Ads ad : adss) {
				String mid = ad.getStatusId();
				
				Timeline tm = new Timeline();
				JSONObject idObj = null;
				try {
					idObj = tm.QueryId( mid, 1,1);
				} catch (WeiboException e1) {
					e1.printStackTrace();
				}
				String statusId = null;
				try {
					System.out.println(idObj);
					if(null == idObj)
						continue;
					statusId = idObj.getString("id");
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				
				String type = ad.getType();
				String adsTarget = ad.getTarget();
				if(! isTarget(adsTarget, accountTarget)){
					continue;
				}
				ArrayList<Comment> comments = getCommentsByType(type, icomments);
				if(comments.size() == 0)
					continue;
				
				if(!isContainRecord(records, uid, statusId)){
					Record record = new Record();
					record.setUid(uid);
					record.setStatusId(statusId);
					
					try {
						Random r = new Random();
						int randomIndex = r.nextInt(comments.size());
						tm.Repost(statusId, comments.get(randomIndex).getComment(), 0);
						recordStore.createRecord(record);
						i++;
						Thread.sleep(5000);
						break;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if(i > adss.size() * 3){
				break;
			}
		}
	}
	
	private boolean isTarget(String adsTarget, String accountTarget){
		if("All".equals(adsTarget)){
			return true;
		}else if("Female".equals(adsTarget)){
			if("Male".equals(accountTarget))
				return false;
			else
				return true;
		}else{
			if("Female".equals(accountTarget))
				return false;
			else
				return true;
		}
	}
	
	private ArrayList<Comment> getCommentsByType(String type, ArrayList<Comment> comments){
		ArrayList<Comment> filteredComments = new ArrayList<Comment>();
		
		for(Comment comment: comments){
			if(type.equals(comment.getType()))
				filteredComments.add(comment);
		}
		
		return filteredComments;
	}
	
	private boolean isContainRecord(ArrayList<Record> records, String uid, String statusId){
		for(Record record: records){
			if(uid.equals(record.getUid()) && statusId.equals(record.getStatusId()))
				return true;
		}
		return false;
	}
	
	@GET
	@Path("clear")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject clearRecord() {
		JSONObject res = new JSONObject();
		
		RecordStore recordStore = new RecordStore();
		recordStore.emptyRecord();
		
		return res;
	}
	
	@GET
	@Path("destroy")
	@Produces(MediaType.APPLICATION_JSON)
	public void destroy() {
		AccountStore accountStore = new AccountStore();
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		Collections.shuffle(accounts);
		for (Account account : accounts) {
			String accessToken = account.getAccessToken();

			if("NONE".equals(accessToken))
				continue;
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			Friendships fm = new Friendships();
			try {
				String[] friendsIds = fm.getFriendsIdsByUid(account.getUid());
				String[] friendsBilateralIds = fm.getFriendsBilateralIds(account.getUid());

				List<String> friendsIdsList = Arrays.asList(friendsIds);
				List<String> friendsBilateralIdsList = Arrays.asList(friendsBilateralIds);
				
				if(friendsIdsList.size() > 1700){
					List<String> friendsUnBilateralIdsList = new ArrayList<String>();
					friendsUnBilateralIdsList.addAll(friendsIdsList);
			        friendsUnBilateralIdsList.removeAll(friendsBilateralIdsList);
			        for(String friendsUnBilateralId: friendsUnBilateralIdsList){
						fm.destroyFriendshipsDestroyById(friendsUnBilateralId);
						Thread.sleep(4000);
					}
				}

				int destroyCount = 0;
				if(friendsBilateralIdsList.size() > 1500){
					for(String friendsBilateralId: friendsBilateralIds){
						if(destroyCount > 800)
							break;
						fm.destroyFriendshipsDestroyById(friendsBilateralId);
						Thread.sleep(4000);
						destroyCount++;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void statics() {
		AccountStore accountStore = new AccountStore();
		StaticsStore staticsStore = new StaticsStore();
		ArrayList<Account> accounts = accountStore.getActiveAccounts();
		for (Account account : accounts) {
			String accessToken = account.getAccessToken();
			if("NONE".equals(accessToken))
				continue;

			Account statics = staticsStore.getAccountStaticsById(account.getUid());
			int followersCount = statics.getFollowersCount();
			int friendsCount = statics.getFriendsCount();
			
			Weibo weibo = new Weibo();
			weibo.setToken(accessToken);
			Users um = new Users();
			try{
				User user = um.showUserById(account.getUid());
				int currentFollowersCount = user.getFollowersCount();
				int currentFriendsCount = user.getFriendsCount();
				account.setFollowersCount(currentFollowersCount);
				account.setFriendsCount(currentFriendsCount);
				account.setFollowersIncrease(currentFollowersCount - followersCount);
				account.setFriendsIncrease(currentFriendsCount - friendsCount);
			} catch(Exception e) {
				e.printStackTrace();
			}
			staticsStore.storeStatics(account);
		}
	}
	
	@GET
	@Path("weibo")
	@Produces(MediaType.APPLICATION_JSON)
	public void test() {
		//shortenUrl();
		//repostAds();
		post();
		//follow();
		//destroy();
		//generateOverview();
	}
	
	public void shortenUrl() {
		Weibo weibo = new Weibo();
		String access_token = "2.00NtZDnCaEwoPC798c6b9849yOWFmC";
		String url = "http://t.cn/zlLNVT3";
		String url_short = url.substring(0, 19);
		weibo.setToken(access_token);
		ShortUrl su = new ShortUrl();
		try {
			JSONObject jo = su.shortToLongUrl(url_short);
			JSONArray urls = (JSONArray) jo.get("urls");
			JSONObject urlObj = (JSONObject) urls.get(0);
			String url_long = urlObj.get("url_long").toString();
			//System.out.println(urlVerification(url_long));
			//System.out.println(url_long);
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void emptyStatics() {
		StaticsStore staticsStore = new StaticsStore();
		staticsStore.emptyStatics();
	}
	
	public void initStatics() {
		AccountStore accountStore = new AccountStore();
		StaticsStore staticsStore = new StaticsStore();
		
		Account soccerAccount = accountStore.getAccountByName("欧洲足球报道");
		String accessToken = soccerAccount.getAccessToken();
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken);
		Users um = new Users();
		
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		for (Account account : accounts) {
			account.setFollowersCount(0);
			account.setFriendsCount(0);
			account.setFollowersIncrease(0);
			account.setFriendsIncrease(0);
			account.setUpdate(0);
			try{
				User user = um.showUserById(account.getUid());
				int currentFollowersCount = user.getFollowersCount();
				int currentFriendsCount = user.getFriendsCount();
				account.setFollowersCount(currentFollowersCount);
				account.setFriendsCount(currentFriendsCount);
				account.setIsActive("ON");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch(WeiboException e) {
				e.printStackTrace();
				if(e.getError().equals("User does not exists!"))
					account.setIsActive("OFF");
			}
			staticsStore.storeStatics(account);
		}
	}

	@GET
	@Path("statics")
	@Produces(MediaType.APPLICATION_JSON)
	public void show() {
		AccountStore accountStore = new AccountStore();
		StaticsStore staticsStore = new StaticsStore();
		
		Account soccerAccount = accountStore.getAccountByName("欧洲足球报道");
		String accessToken = soccerAccount.getAccessToken();
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken);
		Users um = new Users();
		
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		for (Account account : accounts) {
			Account statics = staticsStore.getAccountStaticsById(account.getUid());
			if(statics == null){
				account.setFollowersCount(0);
				account.setFriendsCount(0);
				account.setFollowersIncrease(0);
				account.setFriendsIncrease(0);
				account.setUpdate(0);
				staticsStore.storeStatics(account);
				continue;
			}
			int followersCount = statics.getFollowersCount();
			int friendsCount = statics.getFriendsCount();
			int update = statics.getUpdate();
			try{
				Calendar calendar = Calendar.getInstance();
				if(update == calendar.DATE){
					User user = um.showUserById(account.getUid());
					int currentFollowersCount = user.getFollowersCount();
					int currentFriendsCount = user.getFriendsCount();
					account.setFollowersCount(currentFollowersCount);
					account.setFriendsCount(currentFriendsCount);
					account.setFollowersIncrease(currentFollowersCount - followersCount);
					account.setFriendsIncrease(currentFriendsCount - friendsCount);
					
					account.setUpdate(calendar.DATE);
				}
			} catch(WeiboException e) {
				e.printStackTrace();
				if(e.getError().equals("User does not exists!"))
					account.setIsActive("OFF");
			}
			staticsStore.storeStatics(account);
		}
	}

	public void emptyOverview() {
		OverviewStore staticsStore = new OverviewStore();
		staticsStore.emptyStatics();
	}
	
	public void generateOverview() {
		AccountStore accountStore = new AccountStore();
		OverviewStore staticsStore = new OverviewStore();
		
		Account soccerAccount = accountStore.getAccountByName("欧洲足球报道");
		String accessToken = soccerAccount.getAccessToken();
		Weibo weibo = new Weibo();
		weibo.setToken(accessToken);
		Users um = new Users();
		
		ArrayList<Account> accounts = accountStore.getAllAccounts();
		for (Account account : accounts) {
			account.setFollowersCount(0);
			account.setFriendsCount(0);
			account.setFollowersIncrease(0);
			account.setFriendsIncrease(0);
			account.setUpdate(0);
			try{
				User user = um.showUserById(account.getUid());
				int currentFollowersCount = user.getFollowersCount();
				int currentFriendsCount = user.getFriendsCount();
				account.setFollowersCount(currentFollowersCount);
				account.setFriendsCount(currentFriendsCount);
				account.setIsActive("ON");
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} catch(WeiboException e) {
				e.printStackTrace();
				if(e.getError().equals("User does not exists!"))
					account.setIsActive("OFF");
			}
			staticsStore.storeStatics(account);
		}
	}
	
	private boolean txtVerification(String url){
		if(url.contains("微信")){
			return false;
		}else if(url.contains("QQ")){
			return false;
		}else if(url.contains("兼职")){
			return false;
		}else if(url.contains("扣扣")){
			return false;
		}else if(url.contains("兼-职")){
			return false;
		}else if(url.contains("日赚")){
			return false;
		}else if(url.contains("客服")){
			return false;
		}else if(url.contains("网赚")){
			return false;
		}else if(url.contains("宝贝链接")){
			return false;
		}else if(url.contains("评论里")){
			return false;
		}else if(url.contains("评论") && url.contains("链接")){
			return false;
		}else if(url.contains("评论") && url.contains("地址")){
			return false;
		}else if(url.contains("評论")){
			return false;
		}
		
		return true;
	}
	
	private boolean urlVerification(String url){
		if(url.contains("360buy")){
			return false;
		}else if(url.contains("taobao")){
			return false;
		}else if(url.contains("yihaodian")){
			return false;
		}else if(url.contains("tuan")){
			return false;
		}else if(url.contains("http://kan.weibo.com")){
			return false;
		}else if(url.contains("qq")){
			return false;
		}else if(url.contains("tmall")){
			return false;
		}
		
		return true;
	}
	
	/**
	* JAVA判断字符串数组中是否包含某字符串元素
	*
	* @param substring 某字符串
	* @param source 源字符串数组
	* @return 包含则返回true，否则返回false
	*/
	public static boolean isIn(String substring, String[] source) {
		if (source == null || source.length == 0) {
			return false;
		}
		for (int i = 0; i < source.length; i++) {
			String aSource = source[i];
			if (aSource.equals(substring)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isInvalid(User user) {
		if (user.getFollowersCount() < 20)
			return true;
		if (user.getFriendsCount() < 20)
			return true;
		if (user.isFollowMe())
			return true;
		
		return false;
	}

	private InputStream download(String urlString) {
		InputStream is = null;
		try {
			URL url = new URL(urlString);
			//URLConnection con = url.openConnection();
			
			HttpURLConnection connection=(HttpURLConnection)url.openConnection();
			connection.setDoOutput(true);
	        connection.setConnectTimeout(12*1000);
	        is = connection.getInputStream();
	         
			//is = con.getInputStream();

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
		return is;
	}

}
