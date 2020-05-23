package server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import friendList.FriendStatus;
import login.Account;

public class DatabaseManager {
	
	private static final String databaseLocation = "D:\\MMT\\Ass1_v2.5\\Account.db";
	
	private Account account;
	private String friendList;
	
	public DatabaseManager(Account account) {
		this.account = account;
	}
	
	private Connection getConnection() throws SQLException, ClassNotFoundException {
		Class.forName("org.sqlite.JDBC");
		Connection dbConn = DriverManager.getConnection("jdbc:sqlite:" + databaseLocation);
		System.out.println("\nOpened database at " + databaseLocation + ".");	
		return dbConn;
	}
	
	public boolean verifyLogin() {
		Connection dbConnection = null;
		boolean valid = false;
		
		try {	
			dbConnection = getConnection();
			
			valid = searchForAccount(dbConnection);	
			
			if (valid) { 
				System.out.println("\nFound your account.");
				updateIPAndStatus(dbConnection); 
			} else System.out.println("\nERROR: Cannot find your account!");
						
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			classNotFoundException.printStackTrace();
		} finally {
			closeConnection(dbConnection);
		}
		
		System.out.println("\nLogin verified.");
		
		return valid;
	}
	
	// Search database for validity of the account
	private boolean searchForAccount(Connection dbConnection) throws SQLException {		
		boolean valid = false;
			
		String query = "SELECT USERNAME,PASSWORD FROM ACCOUNT WHERE USERNAME=? AND PASSWORD=?";
		           	        
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		preparedStatement.setString(1, account.getUsername());
		preparedStatement.setString(2, account.getPassword());
		ResultSet result = preparedStatement.executeQuery();
		        
	    while(result.next()) { valid = true; }
		        
	    result.close();
	    preparedStatement.close();
	    
		return valid;
	}
		
	// If user is valid set online flag to 1 and set current IP address
	private void updateIPAndStatus(Connection dbConnection) throws SQLException {
		String query = "UPDATE ACCOUNT SET ONLINE = 1, IP = ? WHERE USERNAME=?";		
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		preparedStatement.setString(1, account.getIPadd().getHostAddress());
		preparedStatement.setString(2, account.getUsername());
		preparedStatement.executeUpdate();
				
		preparedStatement.close();         
	}
	
	public /*void*/ boolean verifySignUp() {
		Connection dbConnection = null;
		boolean duplicate = false;
		
		try {	
			dbConnection = getConnection();
			
			duplicate = searchForDuplicate(dbConnection);
			
			// If account already exist
			if (duplicate) { 
				System.out.println("\nERROR: Account already exist!\nPlease choose another username!");
				return false;
				//return !duplicate;
			}	
			
			createNewAccount(dbConnection);
			//JOptionPane.showMessageDialog(null, "CONGRATULATIONS ON CREATING YOUR ACCOUNT!");
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			classNotFoundException.printStackTrace();
		} finally {
			closeConnection(dbConnection);
		}
		
		System.out.println("\nNew account created.");
		return true;
		//return duplicate;
	}
	
	private boolean searchForDuplicate(Connection dbConnection) throws SQLException {
		boolean duplicate = false;
		
		String query = "SELECT USERNAME FROM ACCOUNT WHERE USERNAME=?";
		           	        
	    PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
	    preparedStatement.setString(1, account.getUsername());
	    ResultSet result = preparedStatement.executeQuery();
	        
	    while(result.next()) { duplicate = true; }
	        
	    result.close();
	    preparedStatement.close();
	    
		return duplicate;
	}
	
	private void createNewAccount(Connection dbConnection) throws SQLException {
		String query = "INSERT INTO ACCOUNT VALUES (null,?,?,1,?,null)";
		PreparedStatement preparedStatement = dbConnection.prepareStatement(query);
		preparedStatement.setString(1, account.getUsername());
		preparedStatement.setString(2, account.getPassword());
		preparedStatement.setString(3, account.getIPadd().getHostAddress());
		preparedStatement.executeUpdate();
		
		preparedStatement.close();
	}
	
	public ArrayList<FriendStatus> filterFriendStatus() {
		Connection dbConnection = null;
		ArrayList<FriendStatus> statusList = new ArrayList<>();
				
		try {	
			dbConnection = getConnection();
			
			String[] idList = getIDList(dbConnection);
			if (idList.length == 0 ) {
				statusList.add(new FriendStatus("server",1,0,"111"));
				return statusList;
			}
			statusList = getFriendDatas(idList, dbConnection);
	        
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			classNotFoundException.printStackTrace();
		} finally {
			if (dbConnection != null) {
				try {
					dbConnection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		System.out.println("\nFiltered friend statuses.");
		
		return statusList;
	}
	
	//add new friend
	public void addNewFriend(String newFriend) {
		Connection dbConnection = null;
		PreparedStatement pst = null;
		ResultSet rs;
		int myID, newFriendID;
		String hFriendList;
		String sql1 = "SELECT ID FROM ACCOUNT WHERE USERNAME=?";
		
		String sql2 = "UPDATE ACCOUNT SET FRIEND=? WHERE USERNAME=?";
		
		String sql3 = "SELECT FRIEND FROM ACCOUNT WHERE ID=?";
		try {
			dbConnection = getConnection();
			
			//get the current user ID
			pst = dbConnection.prepareStatement(sql1);
			pst.setString(1, account.getUsername());
			rs = pst.executeQuery();
			myID = rs.getInt("ID");
			rs.close();
			pst.close();
			
			//get the new friend ID
			pst = dbConnection.prepareStatement(sql1);
			pst.setString(1, newFriend);
			rs = pst.executeQuery();
			newFriendID = rs.getInt("ID");
			rs.close();
			pst.close();
			
			//update the current user's friendlist
			if(friendList == null) friendList = "" + Integer.toString(newFriendID);
			else friendList = friendList + "," + Integer.toString(newFriendID);
			pst = dbConnection.prepareStatement(sql2);
			pst.setString(1, friendList);
			pst.setString(2, account.getUsername());
			pst.executeUpdate();
			pst.close();
			
			//get the new friend's friendlist
			pst = dbConnection.prepareStatement(sql3);
			pst.setInt(1, newFriendID);
			rs = pst.executeQuery();
			hFriendList = rs.getString("FRIEND");
			rs.close();
			pst.close();
			
			//update the new friend's friendlist
			if(hFriendList == null) hFriendList = "" + Integer.toString(myID);
			else hFriendList = hFriendList + "," + Integer.toString(myID);
			pst = dbConnection.prepareStatement(sql2);
			pst.setString(1, hFriendList);
			pst.setString(2, newFriend);
			pst.executeUpdate();
			pst.close();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			closePreparedStatement(pst);
			closeConnection(dbConnection);
		}
		
	}
	
	// Get list of friends' ID
	private String[] getIDList(Connection dbConnection) throws SQLException {
		String[] idList = {};
		
		String sql = "SELECT FRIEND FROM ACCOUNT WHERE USERNAME=?";
		PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
		preparedStatement.setString(1, account.getUsername());
		ResultSet result = preparedStatement.executeQuery();
        
		friendList = result.getString("FRIEND");
		if(friendList != null) idList = friendList.split(",");
        
        result.close();
        preparedStatement.close();
		
		return idList;
	}
	
	// Get list of friends' datas
	private ArrayList<FriendStatus> getFriendDatas(String[] idList, Connection dbConnection) throws SQLException {
		ArrayList<FriendStatus> statusList = new ArrayList<>();
		
		String sql = "SELECT * FROM ACCOUNT WHERE ID=?";
		PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
		ResultSet result = null;
		
		for(String i: idList) {	       	 
	       	 preparedStatement.setString(1, i);
	       	 result = preparedStatement.executeQuery();
	       	 
	       	 String friendname = result.getString("USERNAME");
	       	 String IP = result.getString("IP");
	       	 int ID = result.getInt("ID");
	       	 int isOnline = result.getInt("ONLINE");
	       	 
	       	 System.out.println("\nFriend name: "+ friendname + " ID: " + ID + " IP: " + IP + " Online: " + isOnline);
	       	 
	       	 FriendStatus thisFriend = new FriendStatus(friendname, isOnline, ID, IP);
	       	 statusList.add(thisFriend);
		}
		
		result.close();
		preparedStatement.close();
		
		return statusList;
	}
	
	// Set offline status when user logout
	public void setOffline() {	
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
        String sql = "UPDATE ACCOUNT SET ONLINE = 0 WHERE USERNAME=?";
        
		try {
			dbConnection = getConnection();
			
			preparedStatement = dbConnection.prepareStatement(sql);
			preparedStatement.setString(1, account.getUsername());
			preparedStatement.executeUpdate();
			
			preparedStatement.close();
		} catch (SQLException sqlException) {
			sqlException.printStackTrace();
		} catch (ClassNotFoundException classNotFoundException) {
			classNotFoundException.printStackTrace();
		} finally {
			closePreparedStatement(preparedStatement);
			closeConnection(dbConnection);
		}
	}
	
	private void closeConnection(Connection dbConnection) {
		if (dbConnection != null) {
			try {
				dbConnection.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void closePreparedStatement(PreparedStatement preparedStatement) {
		if (preparedStatement != null) {
			try {
				preparedStatement.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
}