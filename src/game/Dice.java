package game;
import java.awt.Color;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import connection.controllers.RollController;

/**
 * Class that is used to manage the Dice functions
 */
public class Dice extends JPanel implements MouseListener {
	private int diceValue;
	private boolean diceRoll;
	private final JPanel turnPanel;
	private final Launcher launcher;
	
	public final int[][][] pointPositions = {
		{ {11, 11} },
		{ {5, 5}, {17, 17} },
		{ {5, 5}, {11, 11}, {17, 17} },
		{ {5, 5}, {17, 5}, {5, 17}, {17, 17} },
		{ {5, 5}, {17, 5}, {11, 11}, {5, 17}, {17, 17} },
		{ {5, 5}, {17, 5}, {5, 11}, {17, 11}, {5, 17}, {17, 17} },
	};
	
	/**
	 * Creates a new instance of Dice
	 * @param turnPanel Panel used by the Player to show his turn
	 */
	public Dice(Launcher launcher, JPanel turnPanel) { 
		this.diceValue = 0;
		this.diceRoll = false;
		this.turnPanel = turnPanel;
		this.launcher = launcher;
		
		this.setupDice();
	}
	
	/**
	 * Creates a new instance of Dice
	 * @param diceValue Value to set to the Dice
	 */
	public Dice(Launcher launcher, int diceValue) {
		this.diceValue = diceValue;
		this.diceRoll = false;
		this.turnPanel = null;
		this.launcher = launcher;
		
		this.setupDice();
	}
	
	/**
	 * Function that setup the Dice display
	 */
	public void setupDice() {
		Border turnBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.setBorder(turnBorder);
		this.setBackground(Color.WHITE);
		
		this.setSize(26, 26);
		this.setLocation(2, 2);
		this.setOpaque(true);
		this.setVisible(true);
		this.setLayout(null);
		
		this.addMouseListener(this);
	}
	
	/**
	 * Function used to display the Dice
	 */
	public void drawDice() {
		this.removeAll();
		
		int[][] pointList = this.pointPositions[this.getValue() - 1];
		
		for(int p = 0; p < pointList.length; p++) {
			JPanel dicePoint = new JPanel();
			
			dicePoint.setSize(4, 4);
			dicePoint.setLocation(pointList[p][0], pointList[p][1]);
			dicePoint.setBackground(Color.BLACK);
			dicePoint.setOpaque(true);
			dicePoint.setLayout(null);
			dicePoint.setVisible(true);
			
			this.add(dicePoint);
		}
		
		this.repaint();
	}
	
	/**
	 * Function used to set the value to the Dice
	 */
	public void setValue(int diceValue) {
		this.diceValue = diceValue;
		this.diceRoll = true;
	}
	
	/**
	 * Function used to retrieve the value of the Dice
	 * @return Value of the Dice
	 */
	public int getValue() {
		return this.diceValue;
	}

	/**
	 * Function used to manage a click on the Dice
	 * @param e MouseEvent object to manage the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(!this.diceRoll) {
			RollController rollController = new RollController(launcher);
			rollController.sendRoll();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
}
