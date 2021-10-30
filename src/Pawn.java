import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import javax.swing.JPanel;

/**
 * Class that is used to show a single Pawn on the Game Board
 */
public class Pawn extends JPanel {
	private final Color[] colors = {
			Color.decode("#65CDD1"),
			Color.decode("#EE6E6E"),
			Color.decode("#89C66C"),
			Color.decode("#E8E557"),
			Color.WHITE
		};
	
	private final int pawnCode;
	private final CellColor pawnColor;
	private boolean pawnStatus;
	private boolean pawnWon;
	private int pawnPosition;
	
	private final Player player;
	private final char playerCode;
	
	private Dimension pawnDimension;
	private Point pawnCoord;
	private int pawnOffset;
	
	/**
	 * Creates a new instance of Pawn
	 * @param player Player that has the Pawn
	 * @param pawnCode Code of the Pawn
	 * @param pawnColor Color of the Pawn
	 */
	public Pawn(Player player, int pawnCode, CellColor pawnColor) {
		this.pawnCode = pawnCode;
		this.pawnColor = pawnColor;
		this.pawnStatus = false;
		this.pawnWon = false;
		this.pawnPosition = -1;
		
		this.player = player;
		this.playerCode = player.getCode();
	}
	
	/**
	 * Function that is used to draw the Pawn on its cell
	 * @param g Graphics component used by Java to draw the Panel
	 */
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        int pawnWidth = this.pawnDimension.width - (this.pawnOffset / 2);
        int pawnHeight = this.pawnDimension.height - (this.pawnOffset / 2);
        
        int pawnCenterWidth = (int) (this.pawnDimension.width - (this.pawnOffset * 2.5));
        int pawnCenterHeight = (int) (this.pawnDimension.height - (this.pawnOffset * 2.5));
        
        Graphics2D graphics2D = (Graphics2D) g;
        Ellipse2D.Double circle = new Ellipse2D.Double(0, 0, pawnWidth, pawnHeight);
        Ellipse2D.Double circleCenter = new Ellipse2D.Double(this.pawnOffset, this.pawnOffset, pawnCenterWidth, pawnCenterHeight);
        
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        graphics2D.setColor(Color.WHITE);
        graphics2D.fill(circle);
        
        graphics2D.setColor(colors[pawnColor.ordinal()]);
        graphics2D.fill(circleCenter);
        
        graphics2D.setColor(Color.BLACK);
        graphics2D.setStroke(new BasicStroke(1));
        graphics2D.draw(circle);  
        graphics2D.draw(circleCenter);
	}
	
	/*
	 * Function that setup the pawn to then display on its cell
	 * @param pawnDimension Dimension of the Pawn
	 * @param pawnCoord Position of the Pawn
	 * @param pawnOffset Shift in position to calculate its final Position
	 * @param cellPanel Panel of the Cell
	 */
	public void drawPawn(Dimension pawnDimension, Point pawnCoord, int pawnOffset, JPanel cellPanel) {
		this.pawnDimension = pawnDimension;
		this.pawnCoord = new Point((int)(pawnCoord.x + pawnOffset * 1.5), (int)(pawnCoord.y + pawnOffset + 2));
		this.pawnOffset = pawnOffset;
		
		this.setSize(this.pawnDimension);
		this.setLocation(this.pawnCoord);
		this.setOpaque(false);
		this.setVisible(true);
		
		cellPanel.add(this);
	}
	
	/**
	 * Function used to move the Pawn to a new Cell
	 * @param changePosition Number of cells that the Pawn has to move ahead
	 * @return Returns whether the pawn moved or not
	 */
	public boolean movePawn(int changePosition) {
		if(this.canMove(changePosition)) {
			int oldPosition = this.getPosition();
			int newPosition = this.addPosition(changePosition);
			
			Cell newCell = player.getCell(newPosition);
			Cell oldCell = player.getCell(oldPosition);
		
			if(newCell.canKill()) {
				boolean hasKilled = newCell.killPawns(this);
				this.player.setKill(hasKilled);
			}
			
			if(oldCell != null) {
				oldCell.removePawn(this, true);
			}
			
			newCell.addPawn(this);
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Function used to check whether the Pawn can move to a new Cell or not
	 * @param changePosition Number of cells that the Pawn has to move ahead
	 * @return Returns whether the pawn can be moved or not
	 */
	public boolean canMove(int changePosition) {
		if(!this.hasWon()) {
			int newPosition = this.pawnPosition + changePosition;
			
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
	 * Function used to move the Pawn by a certain number of positions
	 * @param changePosition Number of cells that the Pawn has to move ahead
	 * @return Returns the new Position of the Pawn
	 */
	public int addPosition(int changePosition) {
		int newPosition = this.pawnPosition + changePosition;
		
		if(this.getStatus() == false) {
			if(this.getPosition() == -1 && changePosition == 6) {
				this.setPosition(0);
				this.setStatus(true);
				this.player.drawBase();
			}
		} else if(newPosition <= 56) {
			this.pawnPosition = newPosition;
			
			if(this.getPosition() == 56) {
				this.setStatus(false);
				this.setWon(true);
				
				this.player.checkWon();
			}
		}
		
		return this.pawnPosition;
	}
	
	/**
	 * Set the pawn dead
	 */
	public void setDead() {
		this.setStatus(false);
		this.setPosition(-1);
		
		this.player.drawBase();
	}
	
	/**
	 * Set the status of the Pawn
	 * @param pawnStatus Status of the Pawn
	 */
	public void setStatus(boolean pawnStatus) {
		this.pawnStatus = pawnStatus;
	}
	
	/**
	 * Get the status of the Pawn
	 * @return Status of the Pawn
	 */
	public boolean getStatus() {
		return this.pawnStatus;
	}
	
	/**
	 * Set whether the Pawn has reached the end or not
	 * @param pawnWon variable to define whether the Pawn has reached the end or not
	 */
	public void setWon(boolean pawnWon) {
		this.pawnWon = pawnWon;
	}
	
	/**
	 * Return whether the Pawn has reached the end or not
	 * @return whether the Pawn has reached the end or not
	 */
	public boolean hasWon() {
		return this.pawnWon;
	}
	
	/**
	 * Set the a new position of the Pawn
	 * @param newPosition New Position of the Pawn
	 */
	public void setPosition(int newPosition) {
		this.pawnPosition = newPosition;
	}
	
	/**
	 * Get the Position of the Pawn
	 * @return Position of the Pawn
	 */
	public int getPosition() {
		return this.pawnPosition;
	}
	
	/**
	 * Set the Pawn to be selected by the Player
	 */
	public void setPawnSelected() {
		this.player.setPawnSelected(this);
	}
	
	/**
	 * Get the code of the Pawn
	 * @return Code of the Pawn
	 */
	public String getCode() {
		return this.playerCode + Integer.toString(this.pawnCode);
	}
	
	/**
	 * Get the Player that has this Pawn
	 * @return Player that has this Pawn
	 */
	public Player getPlayer() {
		return this.player;
	}
	
	/**
	 * Get the Player's Code that has this Pawn
	 * @return Player's Code that has this Pawn
	 */
	public char getPlayerCode() {
		return this.playerCode;
	}
}
