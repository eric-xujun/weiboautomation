package weibo4j.apps.friendships;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class FriendshipsResource {

	public String defaultMaleImageUrl = "http://tp2.sinaimg.cn/{uid}/50/0/1";
	public String defaultFemaleImageUrl = "http://tp2.sinaimg.cn/{uid}/50/0/0";
	
	public void createBatchFriendships(Weibo weibo, String uid){
		Friendships fm = new Friendships();
		try {
			UserWapper users = fm.getFollowersById(uid, 200, 0);
			for(User u : users.getUsers()){
				if(!isInvalid(u))
					fm.createFriendshipsById(u.getId());
			}
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}
	
	private boolean isInvalid(User user){
		String maleImageUrl = ImageUrlGenerator(defaultMaleImageUrl, user.getId());
		String femaleImageUrl = ImageUrlGenerator(defaultFemaleImageUrl, user.getId());
		if(maleImageUrl.equals(user.getProfileImageUrl()) || femaleImageUrl.equals(user.getProfileImageUrl()))
			return true;
		if(user.getFollowersCount() < 10)
			return true;
		if(user.getFriendsCount() < 10)
			return true;
		
		return false;
	}
	
	private String ImageUrlGenerator(String url, String uid){
		int startIndex = url.indexOf("{");
		int endIndex = url.indexOf("}");
		return url.substring(0, startIndex - 1) + uid + url.substring(endIndex + 1);
	}
}
