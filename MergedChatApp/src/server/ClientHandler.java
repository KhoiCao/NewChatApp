package server;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import friendList.FriendStatus;
import login.Account;
import message.Message;

public class ClientHandler implements Runnable {
	
	private static final String friendRequestCommand = "SVR_REQ: FRIEND FILTER";
	public static String getFriendRequestCommand() { return friendRequestCommand; }
	
	private static final String newFriendCommand = "SVR_REQ: NEW FRIEND";
	public static String getNewFriendRequestCommand() { return newFriendCommand; }
	
	private boolean alreadyLoggedIn;
	public boolean getAlreadyLoggedIn() { return alreadyLoggedIn; }
	
	private Socket connection;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	// For data storage
	private String username;
	public String getUsername() { return username; }
	
	// For login
	private Account account;
	private boolean verified;
	
	// For friend statuses
	private ArrayList<FriendStatus> statusList;
	
	private DatabaseManager databaseManager;
	
	// For message handling
	private Message from;
	private Message to;
	
	private IClientHandlerHelper iHandlerHelper;
	
	public ClientHandler(Socket connection, IClientHandlerHelper iHandlerHelper) {
		this.alreadyLoggedIn = false;
		this.connection = connection;
		this.iHandlerHelper = iHandlerHelper;
	}
	
	public void run() {
		try {
			setupStreams();
			
			getAccountInfo();			
			databaseManager = new DatabaseManager(account);
			
			// If the current user is already logged in
			if (iHandlerHelper.alreadyLogin(username)) {
				alreadyLoggedIn = true;
				return;
			}
			
			if (account.isLogin()) {
				verified = databaseManager.verifyLogin();
			} else {
				verified = databaseManager.verifySignUp();
				//return;
			}
			
			sendLoginResult();
			
			caseToClose();
			
			// Wait for a friend list request from client
			System.out.println("\nWaiting for friend list request.");
			String command = ((Message) input.readObject()).getMessage();			
			if (command.equals(friendRequestCommand)) {
				statusList = databaseManager.filterFriendStatus();
				sendFriendStatus();
			}
			
			startReceivingMessage();
		} catch (EOFException eofException) {
			System.out.println("\nClient " + username + " terminated connection.");
		} catch (SocketException socketException) {
			System.out.println("\nClient " + username + " terminated connection.");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			System.out.println("\nERROR: Unrecognized message!");
		} finally {
			closeAll();
			if (!alreadyLoggedIn) databaseManager.setOffline();
			terminateClient();
		}
	}

	// Setup the streams between server and client
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		System.out.println("\nStreams are now setup.");
	}

	// Get input username and password from client
	private void getAccountInfo() {
		try {
			account = (Account) input.readObject();
			username = account.getUsername();
			System.out.println("\n" + account.getUsername() + "  " + account.getPassword() + "  " + account.getIPadd());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	// Send login result to client
	private void sendLoginResult() throws IOException {
		output.writeObject(verified);
		//output.flush();
		System.out.println("\nSent login result.");
	}
	
	// Send friend list and friends' status to client
	private void sendFriendStatus() throws IOException {
		output.reset();
		output.writeObject(statusList);
		System.out.println("\nSent status list.");
	}
	
	// If login failed
	private void caseToClose() throws IOException, ClassNotFoundException {
		if(!verified) { closeAll(); } 
		System.out.println("\nLogin successful: " + verified);
	}
	
	// Close all streams and connection
	public void closeAll() {
		try {
			output.close();
			input.close();
			connection.close();
			System.out.println("\nConnection closed: " + account.getUsername());
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	// Remove client from server's client's list
	private void terminateClient() {
		iHandlerHelper.onClientTerminated(username);
	}
	
	// Start receiving messages from client
	private void startReceivingMessage() throws ClassNotFoundException, IOException {
		System.out.println("\nStart receiving messages from client: " + account.getUsername());
		while(true) {
			from = (Message) input.readObject();
			
			if (from.getMessage().equals(friendRequestCommand)) {
				statusList = databaseManager.filterFriendStatus();
				sendFriendStatus();
			} else if(from.getMessage().equals(newFriendCommand)) {
				databaseManager.addNewFriend(from.getUsername());
			} else {
				System.out.println("\nMessage received.");
				String receiver = from.getUsername();
				String message = from.getMessage();
				iHandlerHelper.transferMsg(receiver, username, message);
			}
		}
	}
	
	//Pass the message to the receiver
	public void passMessage(String sender, String message) throws IOException {
		to = new Message(sender, message);
		output.writeObject(to);
		output.flush();
		System.out.println("\nMessage passed from: " + sender);
	}
}


