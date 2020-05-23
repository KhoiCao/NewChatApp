package login;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import client.Client;
import client.ConnectionReferences;

public class LoginManager {
	
	public interface IVerified {
		public void loginVerified(boolean verified, ConnectionReferences connRefs);
	}
	
	private Account acc;
	private Socket connection;
	private ObjectInputStream input;
	private ObjectOutputStream output;
	
	public LoginManager() { }
	
	// Request server for logging in or signing up
	public void requestAccount(String inputUsername, String inputPassword, boolean isLogin, IVerified iVerified) {
		try {
			acc = new Account(inputUsername, inputPassword, Client.getMyInetAddress(), isLogin);
			connectToServer();
			setupStreams();
			sendRequest();
			if (isLogin) returnResult(iVerified);
		} catch (EOFException eofException) {
			System.out.println("\nERROR: Already logged in as " + acc.getUsername());
			closeAll();
		} catch (UnknownHostException unknownHostException) {
			unknownHostException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} 
	}
	
	private void connectToServer() throws IOException, UnknownHostException {
		System.out.println("\nAttempting connection... ");
		connection = new Socket(Client.getHost(), Client.getPort());
		System.out.println("\nConnected to: " + connection.getInetAddress().getHostName());
	}
	
	private void setupStreams() throws IOException, UnknownHostException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		System.out.println("\nStreams are now setup.");	
	}
	
	private void sendRequest() throws IOException, EOFException {
		output.writeObject(acc);
		//output.flush();
	}
	
	private boolean receiveResult() throws EOFException {
		try {
			boolean result  = (boolean) input.readObject();
			if (result == true) {
				System.out.println("\nLogin Successful!");
				return true;
			} else {
				System.out.println("\nLogin Failed!");
				closeAll();
				return false;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	public void closeAll() {
		try {
			output.close();
			input.close();
			connection.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	private void returnResult(IVerified iVerified) throws EOFException {
		boolean result = receiveResult();
		if (!result) iVerified.loginVerified(result, null);
		else iVerified.loginVerified(result, new ConnectionReferences(output, input, connection));
	}
}
