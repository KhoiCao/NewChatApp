package client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ConnectionReferences {
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private Socket connection;
	
	public ConnectionReferences(ObjectOutputStream output, ObjectInputStream input, Socket connection) {
		this.output = output;
		this.input = input;
		this.connection = connection;
	}

	public ObjectOutputStream getOutput() {
		return output;
	}

	public ObjectInputStream getInput() {
		return input;
	}

	public Socket getConnection() {
		return connection;
	}
}
