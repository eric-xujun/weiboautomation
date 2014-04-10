package weibo4j.apps.application;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import weibo4j.apps.resource.AccountResource;
import weibo4j.apps.resource.AttachmentResource;
import weibo4j.apps.resource.ReferenceResource;
import weibo4j.apps.resource.ScheduleTaskResource;

public class PlayerApplication extends Application {
	  
	  public Set<Class<?>> getClasses() {
		System.out.println("PlayerApplication starts...");
	    Set<Class<?>> classes = new HashSet<Class<?>>();
	    classes.add(AccountResource.class);
	    classes.add(ReferenceResource.class);
	    classes.add(ScheduleTaskResource.class);
	    classes.add(AttachmentResource.class);
	    return classes;
	  }
	  
}