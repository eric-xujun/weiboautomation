package weibo4j.http;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.cookie.CookiePolicy;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;

import weibo4j.model.Configuration;
import weibo4j.model.Paging;
import weibo4j.model.PostParameter;
import weibo4j.model.WeiboException;
import weibo4j.org.json.JSONException;

import com.google.appengine.api.urlfetch.FetchOptions;
import com.google.appengine.api.urlfetch.HTTPHeader;
import com.google.appengine.api.urlfetch.HTTPMethod;
import com.google.appengine.api.urlfetch.HTTPRequest;
import com.google.appengine.api.urlfetch.HTTPResponse;
import com.google.appengine.api.urlfetch.URLFetchService;
import com.google.appengine.api.urlfetch.URLFetchServiceFactory;

/**
 * @author sinaWeibo
 * 
 */
public class HttpClient implements java.io.Serializable {

	private static final long serialVersionUID = -176092625883595547L;
	private static final int OK = 200; // OK: Success!
	private static final int NOT_MODIFIED = 304; // Not Modified: There was
													// no new data to return.
	private static final int BAD_REQUEST = 400; // Bad Request: The request
												// was invalid. An
												// accompanying error
												// message will explain why.
												// This is the status code
												// will be returned during
												// rate limiting.
	private static final int NOT_AUTHORIZED = 401; // Not Authorized:
													// Authentication
													// credentials were missing
													// or incorrect.
	private static final int FORBIDDEN = 403; // Forbidden: The request is
												// understood, but it has
												// been refused. An
												// accompanying error
												// message will explain why.
	private static final int NOT_FOUND = 404; // Not Found: The URI
												// requested is invalid or
												// the resource requested,
												// such as a user, does not
												// exists.
	private static final int NOT_ACCEPTABLE = 406; // Not Acceptable: Returned
													// by the Search API when an
													// invalid format is
													// specified in the request.
	private static final int INTERNAL_SERVER_ERROR = 500; // Internal Server
															// Error:
															// Something is
															// broken.
															// Please post to
															// the group
															// so the Weibo team
															// can
															// investigate.
	private static final int BAD_GATEWAY = 502; // Bad Gateway: Weibo is
												// down or being upgraded.
	private static final int SERVICE_UNAVAILABLE = 503; // Service Unavailable:
														// The
														// Weibo servers are up,
														// but
														// overloaded with
														// requests.
														// Try again later. The
														// search and trend
														// methods
														// use this to indicate
														// when
														// you are being rate
														// limited.

	private String proxyHost = Configuration.getProxyHost();
	private int proxyPort = Configuration.getProxyPort();
	private String proxyAuthUser = Configuration.getProxyUser();
	private String proxyAuthPassword = Configuration.getProxyPassword();
	private String token;

	public String getProxyHost() {
		return proxyHost;
	}

	/**
	 * Sets proxy host. System property -Dsinat4j.http.proxyHost or
	 * http.proxyHost overrides this attribute.
	 * 
	 * @param proxyHost
	 */
	public void setProxyHost(String proxyHost) {
		this.proxyHost = Configuration.getProxyHost(proxyHost);
	}

	public int getProxyPort() {
		return proxyPort;
	}

	/**
	 * Sets proxy port. System property -Dsinat4j.http.proxyPort or
	 * -Dhttp.proxyPort overrides this attribute.
	 * 
	 * @param proxyPort
	 */
	public void setProxyPort(int proxyPort) {
		this.proxyPort = Configuration.getProxyPort(proxyPort);
	}

	public String getProxyAuthUser() {
		return proxyAuthUser;
	}

	/**
	 * Sets proxy authentication user. System property -Dsinat4j.http.proxyUser
	 * overrides this attribute.
	 * 
	 * @param proxyAuthUser
	 */
	public void setProxyAuthUser(String proxyAuthUser) {
		this.proxyAuthUser = Configuration.getProxyUser(proxyAuthUser);
	}

	public String getProxyAuthPassword() {
		return proxyAuthPassword;
	}

	/**
	 * Sets proxy authentication password. System property
	 * -Dsinat4j.http.proxyPassword overrides this attribute.
	 * 
	 * @param proxyAuthPassword
	 */
	public void setProxyAuthPassword(String proxyAuthPassword) {
		this.proxyAuthPassword = Configuration
				.getProxyPassword(proxyAuthPassword);
	}

	public String setToken(String token) {
		this.token = token;
		return this.token;
	}

	private final static boolean DEBUG = Configuration.getDebug();
	static Logger log = Logger.getLogger(HttpClient.class.getName());
	org.apache.commons.httpclient.HttpClient client = null;

	private MultiThreadedHttpConnectionManager connectionManager;
	private int maxSize;

	private static URLFetchService urlFetchService = URLFetchServiceFactory
			.getURLFetchService();

	public HttpClient() {
		// change timeout to 2s avoid block thread-pool (Tim)
		this(150, 2000, 2000, 1024 * 1024);
	}

	public HttpClient(int maxConPerHost, int conTimeOutMs, int soTimeOutMs,
			int maxSize) {
		connectionManager = new MultiThreadedHttpConnectionManager();
		HttpConnectionManagerParams params = connectionManager.getParams();
		params.setDefaultMaxConnectionsPerHost(maxConPerHost);
		params.setConnectionTimeout(conTimeOutMs);
		params.setSoTimeout(soTimeOutMs);

		HttpClientParams clientParams = new HttpClientParams();
		// 忽略cookie 避免 Cookie rejected 警告
		clientParams.setCookiePolicy(CookiePolicy.IGNORE_COOKIES);
		client = new org.apache.commons.httpclient.HttpClient(clientParams,
				connectionManager);
		// Protocol myhttps = new Protocol("https", new MySSLSocketFactory(),
		// 443);
		// Protocol.registerProtocol("https", myhttps);
		this.maxSize = maxSize;
		// 支持proxy
		if (proxyHost != null && !proxyHost.equals("")) {
			client.getHostConfiguration().setProxy(proxyHost, proxyPort);
			client.getParams().setAuthenticationPreemptive(true);
			if (proxyAuthUser != null && !proxyAuthUser.equals("")) {
				client.getState().setProxyCredentials(
						AuthScope.ANY,
						new UsernamePasswordCredentials(proxyAuthUser,
								proxyAuthPassword));
				log("Proxy AuthUser: " + proxyAuthUser);
				log("Proxy AuthPassword: " + proxyAuthPassword);
			}
		}
	}

	/**
	 * log调试
	 * 
	 */
	private static void log(String message) {
		if (DEBUG) {
			log.log(Level.INFO, message);
		}
	}

	/**
	 * 处理http getmethod 请求
	 * 
	 */

	public Response get(String url) throws WeiboException {

		return get(url, new PostParameter[0]);

	}

	public Response get(String url, PostParameter[] params)
			throws WeiboException {
		if (null != params && params.length > 0) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		// GetMethod getmethod = new GetMethod(url);

		HTTPRequest getmethod;
		try {
			getmethod = new HTTPRequest(new URL(url), HTTPMethod.GET);
			return httpRequest(getmethod);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	public Response get(String url, PostParameter[] params, Paging paging)
			throws WeiboException {
		if (null != paging) {
			List<PostParameter> pagingParams = new ArrayList<PostParameter>(4);
			if (-1 != paging.getMaxId()) {
				pagingParams.add(new PostParameter("max_id", String
						.valueOf(paging.getMaxId())));
			}
			if (-1 != paging.getSinceId()) {
				pagingParams.add(new PostParameter("since_id", String
						.valueOf(paging.getSinceId())));
			}
			if (-1 != paging.getPage()) {
				pagingParams.add(new PostParameter("page", String
						.valueOf(paging.getPage())));
			}
			if (-1 != paging.getCount()) {
				if (-1 != url.indexOf("search")) {
					// search api takes "rpp"
					pagingParams.add(new PostParameter("rpp", String
							.valueOf(paging.getCount())));
				} else {
					pagingParams.add(new PostParameter("count", String
							.valueOf(paging.getCount())));
				}
			}
			PostParameter[] newparams = null;
			PostParameter[] arrayPagingParams = pagingParams
					.toArray(new PostParameter[pagingParams.size()]);
			if (null != params) {
				newparams = new PostParameter[params.length
						+ pagingParams.size()];
				System.arraycopy(params, 0, newparams, 0, params.length);
				System.arraycopy(arrayPagingParams, 0, newparams,
						params.length, pagingParams.size());
			} else {
				if (0 != arrayPagingParams.length) {
					String encodedParams = HttpClient
							.encodeParameters(arrayPagingParams);
					if (-1 != url.indexOf("?")) {
						url += "&" + encodedParams;
					} else {
						url += "?" + encodedParams;
					}
				}
			}
			return get(url, newparams);
		} else {
			return get(url, params);
		}
	}

	/**
	 * 处理http deletemethod请求
	 */

	public Response delete(String url, PostParameter[] params)
			throws WeiboException {
		if (0 != params.length) {
			String encodedParams = HttpClient.encodeParameters(params);
			if (-1 == url.indexOf("?")) {
				url += "?" + encodedParams;
			} else {
				url += "&" + encodedParams;
			}
		}
		// DeleteMethod deleteMethod = new DeleteMethod(url);
		HTTPRequest deleteMethod;
		try {
			deleteMethod = new HTTPRequest(new URL(url), HTTPMethod.DELETE);
			return httpRequest(deleteMethod);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;

	}

	/**
	 * 处理http post请求
	 * 
	 */

	public Response post(String url, PostParameter[] params)
			throws WeiboException {
		return post(url, params, true);

	}

	public Response post(String url, PostParameter[] params,
			Boolean WithTokenHeader) throws WeiboException {
		log("POST:" + url);

		// --------
		// PostMethod postMethod = new PostMethod(url);
		// for (int i = 0; i < params.length; i++) {
		// postMethod.addParameter(params[i].getName(), params[i].getValue());
		// }
		// HttpMethodParams param = postMethod.getParams();
		// param.setContentCharset("UTF-8");
		// if (WithTokenHeader) {
		// return httpRequest(postMethod);
		// } else {
		// return httpRequest(postMethod, WithTokenHeader);
		// }
		// --------------
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < params.length; i++) {
				if (i != 0) {
					sb.append("&");
				}
				sb.append(params[i].getName()).append("=")
						.append(params[i].getValue());
			}
			HTTPRequest postMethod = new HTTPRequest(new URL(url + "?"
					+ URLEncoder.encode(sb.toString(), "utf-8")),
					HTTPMethod.POST);
			postMethod.setPayload(sb.toString().getBytes("utf-8"));
			if (WithTokenHeader) {
				return httpRequest(postMethod);
			} else {
				return httpRequest(postMethod, WithTokenHeader);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 获取对应的HTTP请求
	 * 
	 * @param apiUrl
	 *            远程服务器地址
	 * @param method
	 *            请求方法：GET或POST
	 * @param params
	 *            请求参数：GET的参数添加到URL尾部，POST的参数使用setPayload();
	 * @return 返回设置好的HTTPRequest
	 */
	public HTTPRequest getRequest(String apiUrl, HTTPMethod method,
			String params) {
		HTTPRequest request = null;
		try {
			URL url = new URL(apiUrl);
			request = new HTTPRequest(url, method);

			if (method == HTTPMethod.POST) {
				request.setPayload(params.getBytes("UTF-8"));
			}
		} catch (Exception e) {
			throw new RuntimeException("创建request失败 ", e);
		}
		return request;
	}

	/**
	 * 支持multipart方式上传图片
	 * 
	 */
	public Response multPartURL(String url, PostParameter[] params,
			ImageItem item) throws WeiboException {
		try {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < params.length; i++) {
				if (i != 0) {
					sb.append("&");
				}
				sb.append(params[i].getName()).append("=")
						.append(params[i].getValue());
			}
			HTTPRequest postMethod = new HTTPRequest(new URL(url + "?"
					+ URLEncoder.encode(sb.toString(), "utf-8")),
					HTTPMethod.POST);
			buildPayload(postMethod, params, item.getContent());

			return httpRequest(postMethod, true);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private void buildPayload(HTTPRequest req, PostParameter[] params, byte[] imageBytes) throws IOException {
	    String boundary = makeBoundary();
	     
	    req.setHeader(new HTTPHeader("Content-Type","multipart/form-data; boundary=" + boundary));
	     
	    ByteArrayOutputStream baos = new ByteArrayOutputStream();
	     
	    for (int i = 0; i < params.length; i++) {
		    write(baos, "--"+boundary+"\r\n");
		    writeParameter(baos, params[i].getName(), params[i].getValue());
	    }
	    write(baos, "--"+boundary+"\r\n");
	    writeImage(baos, "pic", imageBytes);
	    write(baos, "--"+boundary+"--\r\n");
	 
	    req.setPayload(baos.toByteArray());
	}
	 
	private static Random random = new Random();    
	 
	private static String randomString() {
	    return Long.toString(random.nextLong(), 36);
	}
	 
	private String makeBoundary() {
	    return "---------------------------" + randomString() + randomString() + randomString();
	}        
	 
	private void write(OutputStream os, String s) throws IOException {
	    os.write(s.getBytes());
	}
	 
	private void writeParameter(OutputStream os, String name, String value) throws IOException {
	    write(os, "Content-Disposition: form-data; name=\""+name+"\"\r\n\r\n"+URLEncoder.encode(value, "utf-8")+"\r\n");
	}
	 
	private void writeImage(OutputStream os, String name, byte[] bs) throws IOException {
	    write(os, "Content-Disposition: form-data; name=\""+name+"\"; filename=\"image.jpg\"\r\n");
	    write(os, "Content-Type: image/jpeg\r\n\r\n");
	    os.write(bs);
	    write(os, "\r\n");
	}
	
	public Response multPartURL(String url, String params)
			throws WeiboException {
		InetAddress ipaddr;
		int responseCode = -1;
		try {
			ipaddr = InetAddress.getLocalHost();

			HTTPRequest request = getRequest(url, HTTPMethod.POST, params);
			if (token == null) {
				throw new IllegalStateException("Oauth2 token is not set!");
			}
			request.addHeader(new HTTPHeader("Authorization", "OAuth2 " + token));
			request.addHeader(new HTTPHeader("API-RemoteIP", ipaddr
					.getHostAddress()));
			for (HTTPHeader hd : request.getHeaders()) {
				log(hd.getName() + ": " + hd.getValue());
			}

			HTTPResponse responseFetch = urlFetchService.fetch(request);

			List<HTTPHeader> resHeader = responseFetch.getHeaders();
			responseCode = responseFetch.getResponseCode();
			log("Response:");
			log("https StatusCode:" + String.valueOf(responseCode));

			for (HTTPHeader header : resHeader) {
				log(header.getName() + ":" + header.getValue());
			}
			Response response = new Response();
			response.setResponseAsString(new String(responseFetch.getContent(),
					"utf-8"));
			log(response.toString() + "\n");

			if (responseCode != OK)

			{
				try {
					throw new WeiboException(getCause(responseCode),
							response.asJSONObject(), responseCode);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return response;
		} catch (Exception ex) {
			throw new WeiboException(ex.getMessage(), ex, -1);
		}
	}

	public Response multPartURL(String fileParamName, String url,
			PostParameter[] params, File file, boolean authenticated)
			throws WeiboException {
		try {
			throw new UnsupportedEncodingException("还没有实现");

		} catch (Exception ex) {
			throw new WeiboException(ex.getMessage(), ex, -1);
		}
	}

	public Response httpRequest(HTTPRequest method) throws WeiboException {
		return httpRequest(method, true);
	}

	public Response httpRequest(HTTPRequest method, Boolean WithTokenHeader)
			throws WeiboException {
		InetAddress ipaddr;
		int responseCode = -1;
		try {
			ipaddr = InetAddress.getLocalHost();

			if (WithTokenHeader) {
				if (token == null) {
					throw new IllegalStateException("Oauth2 token is not set!");
				}
				method.addHeader(new HTTPHeader("Authorization", "OAuth2 "
						+ token));
				method.addHeader(new HTTPHeader("API-RemoteIP", ipaddr
						.getHostAddress()));
				for (HTTPHeader hd : method.getHeaders()) {
					log(hd.getName() + ": " + hd.getValue());
				}
			}

			method.getFetchOptions().setDeadline(10d);
			HTTPResponse responseFetch = urlFetchService.fetch(method);

			List<HTTPHeader> resHeader = responseFetch.getHeaders();
			responseCode = responseFetch.getResponseCode();
			log("Response:");
			log("https StatusCode:" + String.valueOf(responseCode));

			for (HTTPHeader header : resHeader) {
				log(header.getName() + ":" + header.getValue());
			}
			Response response = new Response();
			response.setResponseAsString(new String(responseFetch.getContent(),
					"utf-8"));
			log(response.toString() + "\n");

			if (responseCode != OK)

			{
				try {
					throw new WeiboException(getCause(responseCode),
							response.asJSONObject(), responseCode);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return response;

		} catch (IOException ioe) {
			throw new WeiboException(ioe.getMessage(), ioe, responseCode);
		} finally {
			// method.releaseConnection();
		}

	}

	public Response httpMultiRequest(HTTPRequest method, Boolean WithTokenHeader)
			throws WeiboException {
		InetAddress ipaddr;
		int responseCode = -1;
		try {
			ipaddr = InetAddress.getLocalHost();

			if (WithTokenHeader) {
				if (token == null) {
					throw new IllegalStateException("Oauth2 token is not set!");
				}
				method.addHeader(new HTTPHeader("Authorization", "OAuth2 "
						+ token));
				method.setHeader(new HTTPHeader("Content-Type",
						"multipart/form-data"));
				method.addHeader(new HTTPHeader("API-RemoteIP", ipaddr
						.getHostAddress()));
				for (HTTPHeader hd : method.getHeaders()) {
					log(hd.getName() + ": " + hd.getValue());
				}
			}

			HTTPResponse responseFetch = urlFetchService.fetch(method);

			List<HTTPHeader> resHeader = responseFetch.getHeaders();
			responseCode = responseFetch.getResponseCode();
			log("Response:");
			log("https StatusCode:" + String.valueOf(responseCode));

			for (HTTPHeader header : resHeader) {
				log(header.getName() + ":" + header.getValue());
			}
			Response response = new Response();
			response.setResponseAsString(new String(responseFetch.getContent(),
					"utf-8"));
			log(response.toString() + "\n");

			if (responseCode != OK)

			{
				try {
					throw new WeiboException(getCause(responseCode),
							response.asJSONObject(), responseCode);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return response;

		} catch (IOException ioe) {
			throw new WeiboException(ioe.getMessage(), ioe, responseCode);
		} finally {
			// method.releaseConnection();
		}

	}

	/*
	 * 对parameters进行encode处理
	 */
	public static String encodeParameters(PostParameter[] postParams) {
		StringBuffer buf = new StringBuffer();
		for (int j = 0; j < postParams.length; j++) {
			if (j != 0) {
				buf.append("&");
			}
			try {
				buf.append(URLEncoder.encode(postParams[j].getName(), "UTF-8"))
						.append("=")
						.append(URLEncoder.encode(postParams[j].getValue(),
								"UTF-8"));
			} catch (java.io.UnsupportedEncodingException neverHappen) {
			}
		}
		return buf.toString();
	}

	private static class ByteArrayPart extends PartBase {
		private byte[] mData;
		private String mName;

		public ByteArrayPart(byte[] data, String name, String type)
				throws IOException {
			super(name, type, "UTF-8", "binary");
			mName = name;
			mData = data;
		}

		protected void sendData(OutputStream out) throws IOException {
			out.write(mData);
		}

		protected long lengthOfData() throws IOException {
			return mData.length;
		}

		protected void sendDispositionHeader(OutputStream out)
				throws IOException {
			super.sendDispositionHeader(out);
			StringBuilder buf = new StringBuilder();
			buf.append("; filename=\"").append(mName).append("\"");
			out.write(buf.toString().getBytes());
		}
	}

	private static String getCause(int statusCode) {
		String cause = null;
		switch (statusCode) {
		case NOT_MODIFIED:
			break;
		case BAD_REQUEST:
			cause = "The request was invalid.  An accompanying error message will explain why. This is the status code will be returned during rate limiting.";
			break;
		case NOT_AUTHORIZED:
			cause = "Authentication credentials were missing or incorrect.";
			break;
		case FORBIDDEN:
			cause = "The request is understood, but it has been refused.  An accompanying error message will explain why.";
			break;
		case NOT_FOUND:
			cause = "The URI requested is invalid or the resource requested, such as a user, does not exists.";
			break;
		case NOT_ACCEPTABLE:
			cause = "Returned by the Search API when an invalid format is specified in the request.";
			break;
		case INTERNAL_SERVER_ERROR:
			cause = "Something is broken.  Please post to the group so the Weibo team can investigate.";
			break;
		case BAD_GATEWAY:
			cause = "Weibo is down or being upgraded.";
			break;
		case SERVICE_UNAVAILABLE:
			cause = "Service Unavailable: The Weibo servers are up, but overloaded with requests. Try again later. The search and trend methods use this to indicate when you are being rate limited.";
			break;
		default:
			cause = "";
		}
		return statusCode + ":" + cause;
	}
}
