package message;

import java.io.Serializable;

public class Message implements Serializable {
	
	private String username;
	private String message;
	
	public Message(String username, String message) {
		this.username = username;
		this.message = message;
	}
	
	public Message(String message) {
		super();
		this.username = "";
		this.message = message;
	}

	public String getUsername() { return username; }
	
	public String getMessage() { return message; }
	
	int a;
	public int a() {
		return 1;
	}
	void test() {
		a();
		a = 2;
	}
	
	
}
