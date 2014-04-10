package weibo4j;

import weibo4j.http.HttpClient;
import weibo4j.http.OriginalHttpClient;

/**
 * @author sinaWeibo
 * 
 */

public class Weibo implements java.io.Serializable {

	private static final long serialVersionUID = 4282616848978535016L;

	public static HttpClient client = new HttpClient();
	public static OriginalHttpClient oclient = new OriginalHttpClient();

	/**
	 * Sets token information
	 * 
	 * @param token
	 */
	public synchronized void setToken(String token) {
		client.setToken(token);
	}

}