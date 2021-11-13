import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

/**
 * Class that is used to show a single Archeologist on the Game Board
 */
public class Archeologist extends JPanel {
	private final Color[] colors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557"),
		Color.WHITE
	};
	
	HashMap<Character, String> playerNames = new HashMap<Character, String>() {{
        put('B', "blue");
        put('R', "red");
        put('G', "green");
        put('Y', "yellow");
    }};
	
	private ImageIcon playerIcon;
	
	private final int archeologistCode;
	private final CellColor archeologistColor;
	private boolean archeologistStatus;
	private boolean archeologistWon;
	private int archeologistPosition;
	
	private final Player player;
	private final char playerCode;
	
	private Dimension archeologistDimension;
	private Point archeologistCoord;
	private int archeologistOffset;
	
	/**
	 * Creates a new instance of Archeologist
	 * @param player Player that has the Archeologist
	 * @param archeologistCode Code of the Archeologist
	 * @param archeologistColor Color of the Archeologist
	 */
	public Archeologist(Player player, int archeologistCode, CellColor archeologistColor) {
		this.archeologistCode = archeologistCode;
		this.archeologistColor = archeologistColor;
		this.archeologistStatus = false;
		this.archeologistWon = false;
		this.archeologistPosition = -1;
		
		this.player = player;
		this.playerCode = player.getCode();
		
		try {
        	InputStream stream = getClass().getResourceAsStream("user-" + this.playerNames.get(this.playerCode) + ".png");
        	this.playerIcon = new ImageIcon(ImageIO.read(stream));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Function that is used to draw the Archeologist on its cell
	 * @param g Graphics component used by Java to draw the Panel
	 */
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(this.playerIcon.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	/*
	 * Function that setup the Archeologist to then display on its cell
	 * @param archeologistDimension Dimension of the Archeologist
	 * @param archeologistCoord Position of the Archeologist
	 * @param archeologistOffset Shift in position to calculate its final Position
	 * @param cellPanel Panel of the Cell
	 */
	public void drawArcheologist(Dimension archeologistDimension, Point archeologistCoord, int archeologistOffset, JPanel cellPanel) {
		this.archeologistDimension = archeologistDimension;
		this.archeologistCoord = new Point((int)(archeologistCoord.x + archeologistOffset * 1.5), (int)(archeologistCoord.y + archeologistOffset + 2));
		this.archeologistOffset = archeologistOffset;
		
		this.setSize(this.archeologistDimension);
		this.setLocation(this.archeologistCoord);
		this.setOpaque(false);
		this.setVisible(true);
		
		cellPanel.add(this);
	}
	
	/**
	 * Function used to move the Archeologist to a new Cell
	 * @param changePosition Number of cells that the Archeologist has to move ahead
	 * @return Returns whether the archeologist moved or not
	 */
	public boolean moveArcheologist(int changePosition) {
		if(this.canMove(changePosition)) {
			int oldPosition = this.getPosition();
			int newPosition = this.addPosition(changePosition);
			
			Cell newCell = player.getCell(newPosition);
			Cell oldCell = player.getCell(oldPosition);
		
			if(newCell.canKill()) {
				boolean hasKilled = newCell.killArcheologists(this);
				this.player.setKill(hasKilled);
			}
			
			if(oldCell != null) {
				oldCell.removeArcheologist(this, true);
			}
			
			newCell.addArcheologist(this);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Function used to check whether the Archeologist can move to a new Cell or not
	 * @param changePosition Number of cells that the Archeologist has to move ahead
	 * @return Returns whether the archeologist can be moved or not
	 */
	public boolean canMove(int changePosition) {
		if(!this.hasWon()) {
			int newPosition = this.archeologistPosition + changePosition;
			
			if(this.getStatus()) {
				if(newPosition <= 56) {
					return true;
				}
			} else {
				if(changePosition == 6 && newPosition <= 56) {
					return true;
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Function used to move the Archeologist by a certain number of positions
	 * @param changePosition Number of cells that the Archeologist has to move ahead
	 * @return Returns the new Position of the Archeologist
	 */
	public int addPosition(int changePosition) {
		int newPosition = this.archeologistPosition + changePosition;
		
		if(this.getStatus() == false) {
			if(this.getPosition() == -1 && changePosition == 6) {
				this.setPosition(0);
				this.setStatus(true);
				this.player.drawBase();
			}
		} else if(newPosition <= 56) {
			this.archeologistPosition = newPosition;
			
			if(this.getPosition() == 56) {
				this.setStatus(false);
				this.setWon(true);
				
				this.player.checkWon();
			}
		}
		
		return this.archeologistPosition;
	}
	
	/**
	 * Set the Archeologist dead
	 */
	public void setDead() {
		this.setStatus(false);
		this.setPosition(-1);
		
		this.player.drawBase();
	}
	
	/**
	 * Set the status of the Archeologist
	 * @param archeologistStatus Status of the Archeologist
	 */
	public void setStatus(boolean archeologistStatus) {
		this.archeologistStatus = archeologistStatus;
	}
	
	/**
	 * Get the status of the Archeologist
	 * @return Status of the Archeologist
	 */
	public boolean getStatus() {
		return this.archeologistStatus;
	}
	
	/**
	 * Set whether the Archeologist has reached the end or not
	 * @param archeologistWon variable to define whether the Archeologist has reached the end or not
	 */
	public void setWon(boolean archeologistWon) {
		this.archeologistWon = archeologistWon;
	}
	
	/**
	 * Return whether the Archeologist has reached the end or not
	 * @return whether the Archeologist has reached the end or not
	 */
	public boolean hasWon() {
		return this.archeologistWon;
	}
	
	/**
	 * Set the a new position of the Archeologist
	 * @param newPosition New Position of the Archeologist
	 */
	public void setPosition(int newPosition) {
		this.archeologistPosition = newPosition;
	}
	
	/**
	 * Get the Position of the Archeologist
	 * @return Position of the Archeologist
	 */
	public int getPosition() {
		return this.archeologistPosition;
	}
	
	/**
	 * Set the Archeologist to be selected by the Player
	 */
	public void setArcheologistSelected() {
		this.player.setArcheologistSelected(this);
	}
	
	/**
	 * Get the code of the Archeologist
	 * @return Code of the Archeologist
	 */
	public String getCode() {
		return this.playerCode + Integer.toString(this.archeologistCode);
	}
	
	/**
	 * Get the Player that has this Archeologist
	 * @return Player that has this Archeologist
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Get the Player's Code that has this Archeologist
	 * @return Player's Code that has this Archeologist
	 */
	public char getPlayerCode() {
		return this.playerCode;
	}
}
