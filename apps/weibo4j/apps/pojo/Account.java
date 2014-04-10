package weibo4j.apps.pojo;

public class Account {
	private String uid;
	private String loginName;
	private String name;
	private String accessToken;
	private String password;
	private int followersCount;
	private int friendsCount;
	private int followersIncrease;
	private int friendsIncrease;
	private String isActive;
	private String group;
	private int update;
	private int index;
	private String target;
	
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public int getUpdate() {
		return update;
	}
	public void setUpdate(int update) {
		this.update = update;
	}
	public int getFollowersIncrease() {
		return followersIncrease;
	}
	public void setFollowersIncrease(int followersIncrease) {
		this.followersIncrease = followersIncrease;
	}
	public int getFriendsIncrease() {
		return friendsIncrease;
	}
	public void setFriendsIncrease(int friendsIncrease) {
		this.friendsIncrease = friendsIncrease;
	}
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getIsActive() {
		return isActive;
	}
	public void setIsActive(String isActive) {
		this.isActive = isActive;
	}
	public int getFollowersCount() {
		return followersCount;
	}
	public void setFollowersCount(int followersCount) {
		this.followersCount = followersCount;
	}
	public int getFriendsCount() {
		return friendsCount;
	}
	public void setFriendsCount(int friendsCount) {
		this.friendsCount = friendsCount;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getLoginName() {
		return loginName;
	}
	public void setLoginName(String loginName) {
		this.loginName = loginName;
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
}
