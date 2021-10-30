import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to manage the Board of the Game and show its cells
 */
public class GameBoard extends JPanel {
	private final Color[] colors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557"),
		Color.WHITE
	};
	
	private final Color borderColor = Color.BLACK;
	
	private final int[][] openPositions = {
		{6, 13},	{6, 12},	{6, 11},	{6, 10}, 	{6, 9},
		{5, 8},		{4, 8},  	{3, 8},		{2, 8},		{1, 8},
		{0, 8},		{0, 7},		{0, 6},		
		{1, 6},		{2, 6},		{3, 6},		{4, 6},		{5, 6},
		{6, 5},		{6, 4},		{6, 3},		{6, 2},		{6, 1},
		{6, 0},		{7, 0},		{8, 0},
		{8, 1},		{8, 2},		{8, 3},		{8, 4},		{8, 5},
		{9, 6},		{10, 6},	{11, 6},	{12, 6},	{13, 6},
		{14, 6},	{14, 7},	{14, 8},
		{13, 8},	{12, 8},	{11, 8},	{10, 8},	{9, 8},
		{8, 9},		{8, 10},	{8, 11},	{8, 12},	{8, 13},
		{8, 14},	{7, 14},	{6, 14}
	};
	
	private final int[][] closePositions = {
		{7, 13},	{7, 12},	{7, 11},	{7, 10},	{7, 9},		{7, 8},
		{1, 7},		{2, 7},		{3, 7},		{4, 7},		{5, 7},		{6, 7},
		{7, 1},		{7, 2},		{7, 3},		{7, 4},		{7, 5},		{7, 6},
		{13, 7},	{12, 7},	{11, 7},	{10, 7},	{9, 7},		{8, 7}
	};
	
	private final int[][] basePositions = {
		{0, 9},		{0, 0},		{9, 0},		{9, 9}
	};
	
	private final int nCells = 15;
	private final int cellWidth = 40;
	private final int cellHeight = 40;
	
	private final int baseWidth = cellWidth * 6;
	private final int baseHeight = cellHeight * 6;
	
	private final int frameWidth = cellWidth * nCells;
	private final int frameHeight = cellHeight * nCells;
	
	private Player[] players;
	private Cell[] openCells;
	private Cell[][] closeCells;
	
	/**
	 * Creates a new instance of GameBoard
	 * @param players Players that are playing the game
	 * @param openCells cells that are accessible by each of the players
	 * @param closeCells cells that are accessible by certain players
	 */
	public GameBoard(Player[] players, Cell[] openCells, Cell[][] closeCells) {
		this.players = players;
		this.openCells = openCells;
		this.closeCells = closeCells;
		
		this.setupBoard();
	}
	
	/**
	 * Function used to display the panels of the Board to be displayed
	 */
	public JPanel setupBoard() {
		this.setLayout(null);
		this.setPreferredSize(new Dimension(this.frameWidth, this.frameHeight));
	
		for(int c = 0; c < this.openCells.length; c++) {
			Dimension cellDimension = new Dimension(this.cellWidth, this.cellHeight);
			
			int positionX = openPositions[c][0] * cellWidth;
			int positionY = openPositions[c][1] * cellHeight;
			Point cellPosition = new Point(positionX, positionY);
			
			this.openCells[c].setupCell(cellDimension, cellPosition, this);
		}
		
		for(int p = 0; p < closeCells.length; p++) {
			for(int c = 0; c < closeCells[p].length; c++) {				
				Dimension cellDimension = new Dimension(this.cellWidth, this.cellHeight);
				
				int positionIndex = (p * closeCells[p].length) + c;
				int positionX = closePositions[positionIndex][0] * cellWidth;
				int positionY = closePositions[positionIndex][1] * cellHeight;
				Point cellPosition = new Point(positionX, positionY);
				
				this.closeCells[p][c].setupCell(cellDimension, cellPosition, this);
			}
		}
		
		for(int baseIndex = 0; baseIndex < 4; baseIndex++) {
			this.add(this.setupBaseCenter(baseIndex));
			this.add(this.setupBase(baseIndex));
		}
		
		return this;
	}
	
	/**
	 * Function that is used to display the base for each of the players
	 * @param baseIndex Index of the Player
	 * @return Base Panel of a certain Player to be displayed on the Board
	 */
	public JPanel setupBase(int baseIndex) {
		JPanel basePanel = new JPanel();
		
		Dimension baseDimension = new Dimension(baseWidth, baseHeight);
		
		int positionX = basePositions[baseIndex][0] * cellWidth;
		int positionY = basePositions[baseIndex][1] * cellHeight;
		Point basePosition = new Point(positionX, positionY);
		
		Border baseBorder = BorderFactory.createLineBorder(this.borderColor, 2);
		basePanel.setBorder(baseBorder);
		basePanel.setBackground(colors[baseIndex]);
		
		basePanel.setSize(baseDimension);
		basePanel.setLocation(basePosition);
		basePanel.setOpaque(true);
		basePanel.setVisible(true);
		
		return basePanel;
	}
	
	/**
	 * Function that is used to display the center of the base for each of the players
	 * @param baseIndex Index of the Player
	 * @return Center of the Base Panel of a certain Player to be displayed on the Board
	 */
	public JPanel setupBaseCenter(int baseIndex) {
		JPanel baseCenter = new JPanel();
		baseCenter.setLayout(null);
		
		Dimension centerDimension = new Dimension(baseWidth - cellWidth * 2, baseHeight - cellHeight * 2);
		
		int positionX = (basePositions[baseIndex][0] + 1) * cellWidth;
		int positionY = (basePositions[baseIndex][1] + 1) * cellHeight;
		Point centerPosition = new Point(positionX, positionY);
		
		Border centerBorder = BorderFactory.createLineBorder(this.borderColor, 3);
		baseCenter.setBorder(centerBorder);
		baseCenter.setBackground(Color.decode("#F5F5F5"));
		
		baseCenter.setSize(centerDimension);
		baseCenter.setLocation(centerPosition);
		baseCenter.setOpaque(true);
		baseCenter.setVisible(true);
		
		if(baseIndex < this.players.length) {
			if(this.players[baseIndex] != null) {
				Dimension cellDimension = new Dimension(centerDimension.width / 2, centerDimension.height / 2);
				
				this.players[baseIndex].setupBase(cellDimension, baseCenter);
			}
		}
		
		return baseCenter;
	}
	
	/**
	 * Get the Width of the Board
	 * @return Width of the Board
	 */
	public int getFrameWidth() {
		return this.frameWidth;
	}
	
	/**
	 * Get the Height of the Board
	 * @return Height of the Board
	 */
	public int getFrameHeight() {
		return this.frameHeight;
	}
}
