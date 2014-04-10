package weibo4j.examples.shorturl;

import weibo4j.ShortUrl;
import weibo4j.Weibo;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONArray;
import weibo4j.org.json.JSONException;
import weibo4j.org.json.JSONObject;

public class ShortToLongUrl {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
			System.out.println(urlVerification(url_long));
			System.out.println(url_long);
		} catch (WeiboException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	private static boolean urlVerification(String url){
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
		}
		
		return true;
	}
}

