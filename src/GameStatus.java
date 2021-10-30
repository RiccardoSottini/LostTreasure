import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to manage the Status Panel of the Game
 */
public class GameStatus extends JPanel {
	private final Dimension panelSize;
	private final Point panelPosition;
	
	Player[] players;
	private JPanel playerList;
	
	/**
	 * Creates new instance of GameStatus
	 * @param players List of the players
	 * @param panelSize Size of the Game Status Panel
	 * @param panelPosition Position of the Game Stauts Panel
	 */
	public GameStatus(Player[] players, Dimension panelSize, Point panelPosition) {
		this.players = players;
		this.panelSize = panelSize;
		this.panelPosition = panelPosition;
		
		this.setupPanel();
	}
	
	/**
	 * Function that is used to display the Game Status panel
	 */
	private void setupPanel() {
		this.setPreferredSize(new Dimension(this.panelSize.width, this.panelSize.height));
		this.setLocation(this.panelPosition.x, this.panelPosition.y);
		this.setLayout(null);
		
		Border panelBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.setBorder(panelBorder);
		
		this.setBackground(Color.decode("#A9A9A9"));
		this.setOpaque(true);
		this.setVisible(true);
		
		this.setupList();
	}
	
	/**
	 * Function that is used to display the list of the players
	 */
	private void setupList() {
		this.playerList = new JPanel();
		
		int listWidth = (int)(this.panelSize.width * 0.75);
		int listHeight = this.players.length * 50;
		int listOffset = (int)(this.panelSize.width * 0.125);
		
		Dimension listSize = new Dimension(listWidth, listHeight);
		Point listPosition = new Point(listOffset, listOffset);
		this.playerList.setSize(listSize);
		this.playerList.setLocation(listPosition);
		this.playerList.setLayout(null);
		
		this.playerList.setOpaque(true);
		this.playerList.setVisible(true);
		
		for(int p = 0; p < this.players.length; p++) {
			Dimension labelDimension = new Dimension(listWidth, 50);
			Point labelPosition = new Point(0, p * 50);
			
			this.players[p].setupLabel(labelDimension, labelPosition, this.playerList);
		}
		
		this.add(this.playerList);
	}
	
	/**
	 * Function that is used to display the Ending label at the end of a Game
	 */
	public void setFinished() {
		JLabel finishedLabel = new JLabel("The Game is Ended");
		
		finishedLabel.setFont(new Font("Arial", Font.BOLD, 14));
		finishedLabel.setHorizontalAlignment(JLabel.CENTER);
		
		int labelWidth = (int)(this.panelSize.width * 0.75);
		int labelHeight = 30;
		int labelOffset = (int)(this.panelSize.width * 0.125);
		finishedLabel.setSize(labelWidth, labelHeight);
		int labelPositionX = labelOffset;
		int labelPositionY = this.playerList.getHeight() + (labelOffset * 2);
		finishedLabel.setLocation(labelPositionX, labelPositionY);
		finishedLabel.setVisible(true);
		
		this.add(finishedLabel);
		this.repaint();
	}
}
