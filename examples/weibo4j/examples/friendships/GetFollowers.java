package weibo4j.examples.friendships;

import weibo4j.Friendships;
import weibo4j.Weibo;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.User;
import weibo4j.model.UserWapper;
import weibo4j.model.WeiboException;

public class GetFollowers {

	public static void main(String[] args) {
		//String access_token = args[0];
		String access_token = "2.00NtZDnC0f9xl88e2c6fdee1rzgsfC";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		Friendships fm = new Friendships();
		//String screen_name =args[1];
		String uid =  "2618231705";
		try {
			//UserWapper users = fm.getFollowersByName(screen_name);
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
