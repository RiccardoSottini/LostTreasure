package game;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
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
	
	private int[][] labelPositions;
	private final int labelWidth = 240;
	private final int labelHeight = 40;
	
	private final int nCells = 15;
	private final int cellWidth = 40;
	private final int cellHeight = 40;
	
	private final int baseWidth = cellWidth * 6;
	private final int baseHeight = cellHeight * 6;
	
	private final int frameWidth = cellWidth * nCells + 200;
	private final int frameHeight = cellHeight * nCells + 100;
	
	private final int chatWidth = 178;
	private final int chatHeight = frameHeight - 20;
	
	private Player[] players;
	private Cell[] openCells;
	private Cell[][] closeCells;
	
	private final int frameOffsetX = 100;
	private final int frameOffsetY = 50;
	
	private BufferedImage boardImage;
	
	private final Launcher launcher;
	private GameChat gameChat;
	
	/**
	 * Creates a new instance of GameBoard
	 * @param openCells cells that are accessible by each of the players
	 * @param closeCells cells that are accessible by certain players
	 */
	public GameBoard(Launcher launcher, Cell[] openCells, Cell[][] closeCells) {
		this.launcher = launcher;
		this.openCells = openCells;
		this.closeCells = closeCells;
		
		this.labelPositions = new int[][] {
			{ 100, this.getFrameHeight() - 50 },
			{ 100, 10 },
			{ this.getFrameWidth() - this.labelWidth - 100, 10},
			{ this.getFrameWidth() - this.labelWidth - 100, this.getFrameHeight() - 50}
		};
		
		this.setupBoard();
	}
	
	/**
	 * Function used to display the panels of the Board to be displayed
	 */
	public JPanel setupBoard() {
		this.setLayout(null);
		this.setPreferredSize(new Dimension(this.frameWidth + 198, this.frameHeight));
		this.setAlignmentX(Component.CENTER_ALIGNMENT);
	
		for(int c = 0; c < this.openCells.length; c++) {
			Dimension cellDimension = new Dimension(this.cellWidth, this.cellHeight);
			
			int positionX = frameOffsetX + openPositions[c][0] * cellWidth;
			int positionY = frameOffsetY + openPositions[c][1] * cellHeight;
			Point cellPosition = new Point(positionX, positionY);
			
			this.openCells[c].setupCell(cellDimension, cellPosition, this);
		}
		
		for(int p = 0; p < closeCells.length; p++) {
			for(int c = 0; c < closeCells[p].length; c++) {				
				Dimension cellDimension = new Dimension(this.cellWidth, this.cellHeight);
				
				int positionIndex = (p * closeCells[p].length) + c;
				int positionX = frameOffsetX + closePositions[positionIndex][0] * cellWidth;
				int positionY = frameOffsetY + closePositions[positionIndex][1] * cellHeight;
				Point cellPosition = new Point(positionX, positionY);
				
				this.closeCells[p][c].setupCell(cellDimension, cellPosition, this);
			}
		}
		
		try {
			InputStream stream = getClass().getResourceAsStream("/board.png");
			this.boardImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return this;
	}
	
	public void startGame(Player[] players) {
		this.players = players;
		
		for(int baseIndex = 0; baseIndex < 4; baseIndex++) {
			this.add(this.setupBaseCenter(baseIndex));
			this.add(this.setupBase(baseIndex));
		}
		
		this.setupChat();
		this.launcher.updateTurn(0);
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(boardImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
	
	/**
	 * Function that is used to display the base for each of the players
	 * @param baseIndex Index of the Player
	 * @return Base Panel of a certain Player to be displayed on the Board
	 */
	public JPanel setupBase(int baseIndex) {
		JPanel basePanel = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);

				try {
					InputStream stream = getClass().getResourceAsStream("/base.png");
					ImageIcon image = new ImageIcon(ImageIO.read(stream));
					g.drawImage(image.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		};
		
		Dimension baseDimension = new Dimension(baseWidth, baseHeight);
		
		int positionX = frameOffsetX + basePositions[baseIndex][0] * cellWidth;
		int positionY = frameOffsetY + basePositions[baseIndex][1] * cellHeight;
		Point basePosition = new Point(positionX, positionY);
		
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
		
		int positionX = frameOffsetX + (basePositions[baseIndex][0] + 1) * cellWidth;
		int positionY = frameOffsetY + (basePositions[baseIndex][1] + 1) * cellHeight;
		Point centerPosition = new Point(positionX, positionY);
		
		Border centerBorder = BorderFactory.createLineBorder(this.borderColor, 3);
		baseCenter.setBorder(centerBorder);
		baseCenter.setBackground(Color.decode("#F5F5F5"));
		
		baseCenter.setSize(centerDimension);
		baseCenter.setLocation(centerPosition);
		baseCenter.setOpaque(true);
		baseCenter.setVisible(true);
		
		Dimension cellDimension = new Dimension(centerDimension.width / 2, centerDimension.height / 2);
		
		if(baseIndex < this.players.length) {
			if(this.players[baseIndex] != null) {
				this.players[baseIndex].setupBase(cellDimension, baseCenter);
			}
		} else {
			for(int index = 0; index < 4; index++) {
				int cellPositionX = (index % 2 == 0) ? 0 : cellDimension.width;
				int cellPositionY = (index <= 1) ? 0 : cellDimension.height;
				Point cellPosition = new Point(cellPositionX, cellPositionY);

				CellBase cellBase = new CellBase(this.launcher, index, cellDimension, cellPosition, null);
				baseCenter.add(cellBase);
			}
		}
		
		return baseCenter;
	}
	
	public void setupChat() {
		Dimension chatDimension = new Dimension(this.chatWidth, this.chatHeight);
		Point chatLocation = new Point(this.frameWidth + 10, 10);
		
		this.gameChat = new GameChat(this.launcher, chatDimension, chatLocation);

		this.add(this.gameChat);
		
		for(int p = 0; p < this.players.length; p++) {
			Dimension labelDimension = new Dimension(this.labelWidth, this.labelHeight);
			Point labelPosition = new Point(this.labelPositions[p][0], this.labelPositions[p][1]);
			
			this.players[p].setupLabel(labelDimension, labelPosition, this);
		}
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
	
	public int getLabelWidth() {
		return this.labelWidth;
	}
	
	public int getLabelHeight() {
		return this.labelHeight;
	}
	
	public Dimension getLabelDimension() {
		return new Dimension(this.labelWidth, this.labelHeight);
	}
	
	/**
	 * Send message on the chatbox
	 * @param user_index Index of the User
	 * @param user_message Message that the user wants to send
	 */
	public void addMessage(int user_index, String user_name, String user_message) {
		this.gameChat.addMessage(user_index, user_name, user_message);
	}
	
	public void updateCards(LinkedHashMap<String, Integer> user_cards) {
		this.gameChat.updateCards(user_cards);
	}
	
	public void removeSelectedCards() {
		this.gameChat.removeSelectedCards();
	}
}
