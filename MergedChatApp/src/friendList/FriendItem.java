package friendList;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.SwingUtilities;

public class FriendItem extends JLabel {
	
	public interface IFriendItemClick {
		public void onFriendItemClick(String friendName);
	}
	
	private String friendName;
	
	public FriendItem(String friendName, IFriendItemClick iClick) {
		SwingUtilities.invokeLater(
			new Runnable() {
				
				@Override
				public void run() {
					FriendItem.this.friendName = friendName;
					initialize(iClick);
				}
			}
		);
	}
	
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.BLUE);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}
	
	private void initialize(IFriendItemClick iClick) {
		setText(" " + friendName);
		setPreferredSize(new Dimension(310, 20));
		setOpaque(true);
		repaint();
		
		addMouseListener(
			new MouseListener() {
				
				@Override
				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mousePressed(MouseEvent e) {
					iClick.onFriendItemClick(friendName);
				}
				
				@Override
				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			}
		);
	}
	
}
