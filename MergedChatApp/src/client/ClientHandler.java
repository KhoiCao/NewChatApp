package client;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ClientHandler implements Runnable {

	private ObjectOutputStream output;
	private ObjectInputStream input;
	private ServerSocket server;
	private Socket connection;
	
	public ClientHandler(Socket connection) {
		this.connection = connection;	
	}
	
	public void run() {
		try {
			setupStreams();
			whileChatting();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			closeAll();
		}
	}
		
	// Get streams to send and receive data
	private void setupStreams() throws IOException {
		output = new ObjectOutputStream(connection.getOutputStream());
		output.flush();
		input = new ObjectInputStream(connection.getInputStream());
		System.out.println("\nStreams are now setup! \n");
	}
		
	// During the chat 
	private void whileChatting() throws IOException {
		String message = "\n You are now connected! ";
		sendMessage(message);
		//MultiThreadingServer.ableToType(true);
		do {			
			try {
				message = input.readObject().toString();
			} catch (ClassNotFoundException e) {
				System.out.println("Error getting message!");
			}
			//MultiThreadingServer.showMessage("\n" + message);			
		} while (!message.equals("[CLIENT] - END"));
	}
		
	// Close streams and sockets after you have done chatting
	private void closeAll() {
		//MultiThreadingServer.showMessage("\nClosing connections... \n");
		//MultiThreadingServer.ableToType(false);
		try {
			output.close();
			input.close();
			connection.close();
			server.close();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
		
	// Send message to a client
	private void sendMessage(String message) throws IOException {		
		output.writeObject("[SERVER] - " + message);
		//MultiThreadingServer.showMessage("\n[SERVER] - " + message);
	}
}
