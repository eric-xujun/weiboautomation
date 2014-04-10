package weibo4j.util;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class WeiboConfig {
	public WeiboConfig(){}
	private static Properties props = new Properties(); 
	static{
		try {
			props.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("config.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static String getValue(String key){
		if(key.equals("client_ID")){
			return ConfigProperties.client_ID;
		}else if(key.equals("client_SERCRET")){
			return ConfigProperties.client_SERCRET;
		}else if(key.equals("redirect_URI")){
			return ConfigProperties.redirect_URI;
		}else if(key.equals("baseURL")){
			return ConfigProperties.baseURL;
		}else if(key.equals("accessTokenURL")){
			return ConfigProperties.accessTokenURL;
		}else{
			return ConfigProperties.authorizeURL;
		}
		
		//return props.getProperty(key);
	}

    public static void updateProperties(String key,String value) {    
            props.setProperty(key, value); 
    } 
}
