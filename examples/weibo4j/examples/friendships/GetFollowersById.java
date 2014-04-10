package weibo4j.examples.friendships;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class GetFollowersById {

	public static void main(String[] args) {
		String access_token = "2.00NtZDnCaEwoPC798c6b9849yOWFmC";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String uid = "2557159075";
		
		Friendships fm = new Friendships();
		try {
			UserWapper users = fm.getFollowersById(uid);
			for(User u : users.getUsers()){
				Log.logInfo(u.toString());
			}
			System.out.println(users.getNextCursor());
			System.out.println(users.getPreviousCursor());
			System.out.println(users.getTotalNumber());
		} catch (WeiboException e) {
			e.printStackTrace();
		}

	}

}
