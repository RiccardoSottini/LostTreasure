package game;
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
	private boolean archeologistWon;
	private int archeologistPosition;
	
	private final Player player;
	private final char playerCode;
	
	private Dimension archeologistDimension;
	private Point archeologistCoord;
	private int archeologistOffset;
	
	private Cell archeologistCell;
	
	/**
	 * Creates a new instance of Archeologist
	 * @param player Player that has the Archeologist
	 * @param archeologistCode Code of the Archeologist
	 * @param archeologistColor Color of the Archeologist
	 */
	public Archeologist(Player player, int archeologistCode, CellColor archeologistColor) {
		this.archeologistCode = archeologistCode;
		this.archeologistColor = archeologistColor;
		this.archeologistWon = false;
		this.archeologistPosition = -1;
		
		this.player = player;
		this.playerCode = player.getCode();
		
		try {
        	InputStream stream = getClass().getResourceAsStream("/user-" + this.playerNames.get(this.playerCode) + ".png");
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
	 * @param archeologistCell Cell ahead to move the Archeologist
	 */
	public void moveArcheologist(Cell archeologistCell) {
		Cell oldCell = this.archeologistCell;
		
		if(oldCell != null) {
			oldCell.removeArcheologist(this, true);
		}
		
		this.archeologistCell = archeologistCell;
		this.archeologistCell.addArcheologist(this);
	}
	
	public void moveBase() {
		Cell oldCell = this.archeologistCell;
		
		if(oldCell != null) {
			oldCell.removeArcheologist(this, true);
		}
		
		this.archeologistCell = null;
		
		this.setPosition(-1);
		this.player.drawBaseCell(this.archeologistCode);
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
