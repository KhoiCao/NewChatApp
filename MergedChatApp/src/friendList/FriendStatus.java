package friendList;

import java.io.Serializable;

public class FriendStatus implements Serializable {
	private String username;
	private int online;
	
	public String getUsername() {
		return username;
	}
	public int isOnline() {
		return online;
	}
	
	private int ID;
	
	private String IP;
	
	public int getID() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public String getIP() {
		return IP;
	}
	public void setIP(String iP) {
		IP = iP;
	}
	
	public FriendStatus(String username, int online, int ID, String IP) {
		this.username = username;
		this.online = online;
		this.ID = ID;
		this.IP = IP;
	}
}