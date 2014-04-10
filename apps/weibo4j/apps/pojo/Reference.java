package weibo4j.apps.pojo;

public class Reference {
	private String uid;
	private String uname;
	private String rid;
	private String rname;
	private String tag;
	private int followersCount;
	private int followersIncrease;
	private float invalidRate;
	private int updateCount;
	
	public int getUpdateCount() {
		return updateCount;
	}
	public void setUpdateCount(int updateCount) {
		this.updateCount = updateCount;
	}
	public int getFollowersIncrease() {
		return followersIncrease;
	}
	public void setFollowersIncrease(int followersIncrease) {
		this.followersIncrease = followersIncrease;
	}
	public float getInvalidRate() {
		return invalidRate;
	}
	public void setInvalidRate(float invalidRate) {
		this.invalidRate = invalidRate;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = rid;
	}
	public String getUname() {
		return uname;
	}
	public void setUname(String uname) {
		this.uname = uname;
	}
	public String getRname() {
		return rname;
	}
	public void setRname(String rname) {
		this.rname = rname;
	}
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
}
