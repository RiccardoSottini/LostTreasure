import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to manage a single Player that is playing the Game
 */
public class Player {
	private final Color[] colors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557"),
		Color.WHITE
	};
	
	private final String playerName;
	private final Cell[] playerCells;
	private final char playerCode;
	private final CellColor playerColor;
	private boolean playerKill;
	private boolean playerWon;
	private int playerWonPosition;
	
	/* CHANGE */
	private JPanel playerLabel;
	private JLabel playerLabelName;
	
	private final Pawn[] pawns;
	private Pawn pawnSelected = null;
	
	private CellBase[] baseCells;
	
	private Dice dice = null;
	
	private boolean turnPlayer;
	private JPanel turnPanel;
	
	private final Launcher launcher;
	
	/**
	 * Creates a new instance of Player
	 * @param playerName Name of the Player
	 * @param playerCells Cells where the Player's pawn can move to
	 * @param playerCode Code of the Player
	 * @param playerColor Color of the Player
	 * @param launcher Launcher instance used to retrieve some data
	 */
	public Player(String playerName, Cell[] playerCells, char playerCode, CellColor playerColor, Launcher launcher) {
		this.playerName = playerName;
		this.playerCells = playerCells;
		this.playerCode = playerCode;
		this.playerColor = playerColor;
		this.playerKill = false;
		this.playerWon = false;
		
		this.launcher = launcher;
		
		this.pawns = new Pawn[4];
		this.baseCells = new CellBase[4];
		
		for(int p = 0; p < 4; p++) {
			this.pawns[p] = new Pawn(this, p, playerColor);
		}
		
		this.dice = null;
		this.turnPlayer = false;
	}
	
	/**
	 * Function used to make the user to play
	 */
	public void playerPlay() {
		this.checkWon();
		
		if(!this.hasWon()) {
			do {
				this.setTurn(true);
				
				while(!this.hasDice()) {
					try {
					    Thread.sleep(100);
					} catch (Exception e) {
					    e.printStackTrace();
					}
				}
				
				this.removePawnSelected();
				
				if(this.canMove()) {
					while(!this.playMove()) {
						try {
						    Thread.sleep(100);
						} catch (Exception e) {
						    e.printStackTrace();
						}
					}
				} else {
					this.setTurn(false);
				}
			} while(!this.hasWon() && this.isPlayerTurn());
		}
	}
	
	/**
	 * Function used to setup the Base of the Player
	 * @param cellDimension Dimension of the Base
	 * @param baseCenter Panel where to show the Base of the Player
	 */
	public void setupBase(Dimension cellDimension, JPanel baseCenter) {
		for(int baseIndex = 0; baseIndex < this.pawns.length; baseIndex++) {
			int cellPositionX = (baseIndex % 2 == 0) ? 0 : cellDimension.width;
			int cellPositionY = (baseIndex <= 1) ? 0 : cellDimension.height;
			Point cellPosition = new Point(cellPositionX, cellPositionY);

			this.baseCells[baseIndex] = new CellBase(cellDimension, cellPosition, this.pawns[baseIndex]);
			
			baseCenter.add(this.baseCells[baseIndex]);
		}
		
		this.drawBase();
	}
	
	/**
	 * Draw the Base of the Player
	 */
	public void drawBase() {
		for(int baseIndex = 0; baseIndex < this.baseCells.length; baseIndex++) {
			this.baseCells[baseIndex].drawPawn();
		}
	}
	
	/**
	 * Function used to display the label with the name of the Player and the Label for its turn
	 * @param labelDimension Dimension of the Label
	 * @param labelPosition Position of the Label
	 * @param playerList Panel used to display the list of all the players
	 */
	public void setupLabel(Dimension labelDimension, Point labelPosition, JPanel playerList) {
		this.playerLabel = new JPanel();
		this.playerLabel.setLayout(null);
		
		Border labelBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.playerLabel.setBorder(labelBorder);
		this.playerLabel.setBackground(colors[this.playerColor.ordinal()]);
		
		this.setupLabelTurn(labelDimension);
		this.setupLabelName(labelDimension);
		
		this.playerLabel.setSize(labelDimension);
		this.playerLabel.setLocation(labelPosition);
		this.playerLabel.setVisible(true);
		
		playerList.add(this.playerLabel);
	}
	
	/**
	 * Function used to display the panel that contains the Dice and displays whether it is the Player's turn or not
	 * @param labelDimension Dimension of the Label
	 */
	public void setupLabelTurn(Dimension labelDimension) {
		this.turnPanel = new JPanel();
		this.turnPanel.setLayout(null);
		
		Border turnBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.turnPanel.setBorder(turnBorder);
		this.turnPanel.setBackground(Color.WHITE);
		
		this.turnPanel.setSize(labelDimension.height - 20, labelDimension.height - 20);
		this.turnPanel.setLocation(10, 10);
		this.turnPanel.setVisible(true);
		
		this.playerLabel.add(this.turnPanel);
	}
	
	/**
	 * Function used to display the name of the Player
	 * @param labelDimension Dimension of the Label
	 */
	public void setupLabelName(Dimension labelDimension) {
		this.playerLabelName = new JLabel(this.playerName);
		this.playerLabelName.setFont(new Font("Arial", Font.PLAIN, 15));
		
		int labelSize = labelDimension.height - 20;
		int labelWidth = labelDimension.width - labelSize - 30;
		int labelHeight = labelSize;
		this.playerLabelName.setSize(labelWidth, labelHeight);
		
		int labelPositionX = labelSize + 20;
		int labelPositionY = 10;
		this.playerLabelName.setLocation(labelPositionX, labelPositionY);
		
		this.playerLabelName.setVisible(true);
		this.playerLabel.add(this.playerLabelName);
	}
	
	/**
	 * Check whether it is the Player's turn or not
	 * @return whether it is the Player's turn or not
	 */
	public boolean isPlayerTurn() {
		return this.turnPlayer;
	}
	
	/**
	 * Set whether it is the turn of the Player or not 
	 * @param turnPlayer variable used to check whether it is the turn of the Player or not
	 */
	public void setTurn(boolean turnPlayer) {
		this.turnPlayer = turnPlayer;
		
		if(this.turnPlayer) {
			Border labelBorder = BorderFactory.createLineBorder(Color.decode("#FFC300"), 2);
			this.turnPanel.setBorder(labelBorder);
			
			this.setDice();
		} else {
			try {
			    Thread.sleep(500);
			} catch (Exception e) {
			    e.printStackTrace();
			}
			
			Border labelBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
			this.turnPanel.setBorder(labelBorder);
			
			this.deleteDice();
			this.removePawnSelected();
		}
	}
	
	/**
	 * Function used to return the Dice used by the Player
	 * @return Dice used by the Player
	 */
	public Dice getDice() {
		return this.dice;
	}
	
	/**
	 * Create a new Dice
	 */
	public void setDice() {
		this.turnPanel.removeAll();
		
		this.dice = new Dice(this.turnPanel);
		this.turnPanel.add(this.dice);
		
		this.turnPanel.repaint();
	}
	
	/**
	 * Delete the current Dice used by the Player
	 */
	public void deleteDice() {
		if(this.dice != null) {
			this.turnPanel.remove(this.dice);
			this.dice = null;
			
			this.turnPanel.repaint();
		}
	}
	
	/**
	 * Check whether the Player has a Dice or not
	 * @return whether the Player has a Dice or not
	 */
	public boolean hasDice() {
		if(this.dice != null) {
			return this.dice.getValue() != 0;
		}
		
		return false;
	}
	
	/**
	 * Check whether the Player has selected a Pawn or not
	 * @return whether the Player has selected a Pawn or not
	 */
	public boolean isPawnSelected() {
		return pawnSelected != null;
	}
	
	/**
	 * Get the list of the Player's Pawn
	 * @return List of the Player's Pawn
	 */
	public Pawn[] getPawns() {
		return this.pawns;
	}
	
	/**
	 * Get the Pawn that is selected by the Player
	 * @return Pawn that is selected by the Player
	 */
	public Pawn getPawnSelected() {
		return pawnSelected;
	}
	
	/**
	 * Set a Pawn to be selected by the Player
	 * @param pawnSelected Pawn that is selected by the Player
	 */
	public void setPawnSelected(Pawn pawnSelected) {
		this.pawnSelected = pawnSelected;
	}
	
	/**
	 * Remove the reference to the Pawn that was previously selected
	 */
	public void removePawnSelected() {
		this.pawnSelected = null;
	}
	
	/**
	 * Check whether the Player can make its Pawn to move
	 * @return whether the Player can make its Pawn to move
	 */
	public boolean canMove() {
		if(this.hasDice()) {
			Dice playerDice = this.getDice();
			
			for(Pawn pawn : this.pawns) {
				if(pawn.canMove(playerDice.getValue())) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Function used to make a Move
	 * @return whether the Player has moved one of its Pawns or not
	 */
	public boolean playMove() {
		if(this.isPlayerTurn()) {
			if(this.isPawnSelected()) {
				if(this.hasDice()) {
					Pawn selectedPawn = this.getPawnSelected();
					int changePosition = this.getDice().getValue();
					
					if(selectedPawn.movePawn(changePosition)) {
						if(this.hasWon() || (this.getDice().getValue() != 6 && !this.checkKill() && selectedPawn.getPosition() != 56)) {
							this.setTurn(false);
						}
						
						return true;
					}
				}
			}
		}	
		
		return false;
	}
	
	/**
	 * Get the cell at a certain Position
	 * @param position Position of the Cell
	 * @return Cell at a certain Position
	 */
	public Cell getCell(int position) {
		if(position >= 0) {
			return this.playerCells[position];
		}
		
		return null;
	}
	
	/**
	 * Get the Code of the Player
	 * @return Code of the Player
	 */
	public char getCode() {
		return this.playerCode;
	}
	
	/**
	 * Returns whether the Player has killed someone or not
	 * @param playerKill variable to define whether the Player has killed someone or not
	 */
	public void setKill(boolean playerKill) {
		this.playerKill = playerKill;
	}
	
	/**
	 * Check whether the player has killed someone or not
	 * @return whether the player has killed someone or not
	 */
	public boolean checkKill() {
		boolean killValue = this.playerKill;
		this.setKill(false);
		
		return killValue;
	}
	
	/**
	 * Check whether the player has completed the game or not
	 */
	public void checkWon() {
		int counter = 0;
		
		for(Pawn pawn : this.pawns) {
			if(pawn.hasWon()) {
				counter++;
			}
		}
		
		this.playerWon = counter == 4;
		
		if(this.playerWon) {
			this.playerWonPosition = this.launcher.playersWon();
			
			this.setWonLabel();
		}
	}

	/**
	 * Function used to display the label displaying that the Player has won the Game
	 */
	public void setWonLabel() {
		this.turnPanel.removeAll();
		
		Border labelBorder = BorderFactory.createLineBorder(Color.BLACK, 0);
		this.turnPanel.setBorder(labelBorder);
		this.turnPanel.setBackground(colors[this.playerColor.ordinal()]);
		
		JLabel winLabel = new JLabel(this.playerWonPosition + ".");
		winLabel.setFont(new Font("Arial", Font.BOLD, 15));
		winLabel.setHorizontalAlignment(JLabel.CENTER);
		
		winLabel.setSize(this.turnPanel.getWidth(), this.turnPanel.getHeight());
		winLabel.setLocation(0, 0);
		winLabel.setVisible(true);
		
		this.turnPanel.add(winLabel);
		this.turnPanel.repaint();
	}

	/**
	 * Function used to display the label displaying that the Player has lost the Game
	 */
	public void setLoseLabel() {
		Border labelBorder = BorderFactory.createLineBorder(Color.BLACK, 0);
		this.turnPanel.setBorder(labelBorder);
		this.turnPanel.setBackground(colors[this.playerColor.ordinal()]);
		
		JLabel winLabel = new JLabel("Lost");
		winLabel.setFont(new Font("Arial", Font.BOLD, 14));
		winLabel.setHorizontalAlignment(JLabel.CENTER);
		
		winLabel.setSize(this.turnPanel.getWidth(), this.turnPanel.getHeight());
		winLabel.setLocation(0, 0);
		winLabel.setVisible(true);
		
		this.turnPanel.add(winLabel);
	}
	
	/**
	 * Function used to retrieve whether the Player has won the Game or not
	 * @return whether the Player has won the Game or not
	 */
	public boolean hasWon() {
		return this.playerWon;
	}
}
