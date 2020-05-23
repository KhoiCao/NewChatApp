
package friendList;

import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

import friendList.FriendItem.IFriendItemClick;

import java.awt.Color;

public class FriendListGUI extends JFrame implements IFriendItemClick {

	public interface IFriendList {
		public void onFriendListCLose();
		public void onFriendItemClick(String friendName);
		public void onRefreshClick();
		public void onAddFriendClick(String friendName);
	}
	
	private String myUsername;
	
	private JButton btnLogout, btnRefresh;
	private JLabel lblMyUsername, lblFriends, lblOnline, lblOffline;
	private JPanel pnlOnline, pnlOffline;
	
	private IFriendList iFriendList;
	
	// For testing
	public static void main(String[] args) {
		new FriendListGUI(/*null, */"RRaptor"/*, null*/, null);
	}
	
	public FriendListGUI(String myUsername, IFriendList iFriendList) { 
		this.myUsername = myUsername;
		this.iFriendList = iFriendList;
		
		//testData();
		
		SwingUtilities.invokeLater(
			new Runnable() {			
				@Override
				public void run() {
					initialize();					
				}
			}
		);
	}
	
	private void initialize() {
		setBounds(100, 100, 350, 600);
		setVisible(true);		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle(myUsername);
		setLocationRelativeTo(null);
		getContentPane().setLayout(null);
		
		btnLogout = new JButton("Logout");
		btnLogout.setBounds(241, 20, 85, 21);
		btnLogout.addActionListener(
			new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent e) {
					// Dispose off this frame
					dispose();
					
					// Signal Client that this friend list GUI is closed
					iFriendList.onFriendListCLose();
				}
			}
		);
		getContentPane().add(btnLogout);
		
		JTextField addFriendField = new JTextField();
		addFriendField.setBounds(10, 80, 200, 20);
		getContentPane().add(addFriendField);
		
		JButton btnAddFriend = new JButton("Add Friend");
		btnAddFriend.setBounds(225, 80, 100, 21);
		btnAddFriend.addActionListener(
			new ActionListener() {			
				@Override
				public void actionPerformed(ActionEvent e) {
					String newFriend = addFriendField.getText();
					if (newFriend.equals("")) {
						JOptionPane.showMessageDialog(FriendListGUI.this, "Please specify a username!");
					} else {
						iFriendList.onAddFriendClick(newFriend);
						addFriendField.setText("");
					}
				}
			}
		);
		getContentPane().add(btnAddFriend);
		
		lblMyUsername = new JLabel(myUsername); 
		lblMyUsername.setFont(new Font("Tahoma", Font.PLAIN, 20));
		lblMyUsername.setBounds(54, 20, 162, 25);
		getContentPane().add(lblMyUsername);
		
		pnlOnline = new JPanel();
		pnlOnline.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		pnlOnline.setBounds(10, 125, 316, 200);
		getContentPane().add(pnlOnline);
		pnlOnline.setLayout(new FlowLayout());
		
		pnlOffline = new JPanel(); 
		pnlOffline.setBorder(new LineBorder(Color.LIGHT_GRAY, 1, true));
		pnlOffline.setBounds(10, 353, 316, 200);
		getContentPane().add(pnlOffline);
		pnlOffline.setLayout(new FlowLayout());
		
		lblFriends = new JLabel("Friends");
		lblFriends.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblFriends.setBounds(10, 55, 78, 13);
		getContentPane().add(lblFriends);
		
		lblOnline = new JLabel("Online");
		lblOnline.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblOnline.setBounds(10, 110, 60, 13);
		getContentPane().add(lblOnline);
		
		lblOffline = new JLabel("Offline");
		lblOffline.setFont(new Font("Times New Roman", Font.BOLD, 15));
		lblOffline.setBounds(10, 340, 60, 13);
		getContentPane().add(lblOffline);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.setBounds(77, 50, 85, 21);
		btnRefresh.addActionListener(
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					iFriendList.onRefreshClick();
				}
			}
		);
		getContentPane().add(btnRefresh);


		System.out.println("\nFriend List GUI initialized.");
	}
	
	/*private void testData() {
		friendNames = new ArrayList<>();
		friendNames.add("khoicao");
		friendNames.add("kietran99");
		friendNames.add("luule");
		System.out.println("\nTest data created.");
	}*/
	
	public void displayFriends(ArrayList<FriendStatus> friendStatuses) {
		SwingUtilities.invokeLater(
			new Runnable() {		
				@Override
				public void run() {
					clearPanels();
					for(FriendStatus friend: friendStatuses) {
						if (friend.getUsername().equals("server")) return;
						FriendItem myFriend = new FriendItem(friend.getUsername(), FriendListGUI.this);
						if(friend.isOnline() == 1) pnlOnline.add(myFriend);
						else pnlOffline.add(myFriend);
					}
					System.out.println("\nFriends displayed.");
				}
			}
		);	
	}
	
	private void clearPanels() {
		// Clear online panel
		pnlOnline.removeAll();
		pnlOnline.validate();
		pnlOnline.repaint();
		
		// Clear offline panel
		pnlOffline.removeAll();
		pnlOffline.validate();
		pnlOffline.repaint();
	}
	
	@Override
	public void onFriendItemClick(String friendName) {		
		iFriendList.onFriendItemClick(friendName);
		System.out.println("\nFriend Name: " + friendName);
	}
	
}
