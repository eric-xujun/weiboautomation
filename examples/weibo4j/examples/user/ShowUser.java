package weibo4j.examples.user;

import weibo4j.Users;
import weibo4j.Weibo;
import weibo4j.examples.oauth2.Log;
import weibo4j.model.User;
import weibo4j.model.WeiboException;

public class ShowUser {

	public static void main(String[] args) {
		String access_token = "2.00NtZDnCaEwoPC798c6b9849yOWFmC";
		Weibo weibo = new Weibo();
		weibo.setToken(access_token);
		String uid =  "2557159075";
		Users um = new Users();
		try {
			User user = um.showUserById(uid);
			Log.logInfo(user.toString());
		} catch (WeiboException e) {
			e.printStackTrace();
		}
	}

}
