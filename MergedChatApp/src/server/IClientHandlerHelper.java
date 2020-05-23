package server;

public interface IClientHandlerHelper {
	public void transferMsg(String receiver, String sender, String message);
	public boolean alreadyLogin(String username);
	public void onClientTerminated(String username);
}
