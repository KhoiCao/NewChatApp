package login;

import java.io.Serializable;
import java.net.InetAddress;

public class Account implements Serializable{
	private String username;
	private String password;
	private InetAddress IPadd;
	private boolean online;
	private boolean isLogin;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public InetAddress getIPadd() {
		return IPadd;
	}
	public void setIPadd(InetAddress iPadd) {
		IPadd = iPadd;
	}
	
	public boolean isOnline() { return online; }
	
	public void setOnline(boolean online) { this.online = online; }
	
	public boolean isLogin() { return isLogin; }
	
	public Account(String username, String password, InetAddress IPadd, boolean isLogin) {
		this.username = username;
		this.password = password;
		this.IPadd = IPadd;
		this.online = true;
		this.isLogin = isLogin;
	}
	
	public Account(String username, String password, InetAddress IPadd) {
		this.username = username;
		this.password = password;
		this.IPadd = IPadd;
		this.online = true;
	}
}

