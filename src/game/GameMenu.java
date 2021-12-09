package game;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.controllers.CreateController;
import connection.controllers.StartController;

/**
 * Class that is used to manage the Menu of the Game
 */
public class GameMenu extends JPanel {
	private final Launcher launcher;
	
	private final Color[] playerColors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557")
	};
	
	private final String[] playerNames = {
		"blue", "red", "green", "yellow"
	};
	
	private ImageIcon[] playersIcons;
	
	private final int DEFAULT_PLAYERS = 1;
	private final int MIN_PLAYERS = 2;
	private final int MAX_PLAYERS = 4;
	private int nPlayers;
	private String game_token;
	
	private final Dimension menuDimension;
	private ImageIcon menuImage;
	
	private JLabel titleLabel;
	private JPanel menuCenter;
	
	private JPanel numberPanel;
	private JLabel numberLabel;
	private JTextField numberField;
	
	private JPanel playerList;
	private JPanel[] playerPanels;
	private JTextField[] playerFields;
	
	private JPanel sharePanel;
	private JLabel shareLabel;
	private JTextField shareText;
	private JButton shareButton;
	
	private JButton playButton;
	
	private boolean hasInput;
	
	/**
	 * Creates a new instance of GameMenu
	 * @param menuDimension Dimension of the Menu
	 */
	public GameMenu(Launcher launcher, Dimension menuDimension) {
		this.launcher = launcher;
		this.menuDimension = menuDimension;  
        
		this.nPlayers = DEFAULT_PLAYERS;
		this.hasInput = false;
		
		this.setupMenu();
		this.setupImages();
	}
	
	public void setupImages() {
		try {
        	InputStream stream = getClass().getResourceAsStream("/menu-background.jpg");
			this.menuImage = new ImageIcon(ImageIO.read(stream));
		} catch (IOException e) {
			e.printStackTrace();
		}
        
        this.playersIcons = new ImageIcon[4];
        for(int i = 0; i < 4; i++) {
        	try {
            	InputStream stream = getClass().getResourceAsStream("/user-" + this.playerNames[i] + ".png");
            	this.playersIcons[i] = new ImageIcon(ImageIO.read(stream));
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        }
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(this.menuImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
    }
	
	/**
	 * Function that is waiting the user to click on the button "Play"
	 */
	public void waitMenu() {
		while(!this.hasInput) {
			try {
			    Thread.sleep(100);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	/**
	 * Retrieve the names of the Players
	 * @return Names of the Players
	 */
	public String[] getPlayers() {
		String[] playerNames = new String[this.nPlayers];
		
		for(int playerIndex = 0; playerIndex < this.nPlayers; playerIndex++) {
			playerNames[playerIndex] = playerFields[playerIndex].getText();
		}
		
		return playerNames;
	}
	
	/**
	 * Function that is used to display the panels of the Menu to be displayed
	 */
	public void setupMenu() {
		this.setLayout(null);
		this.setPreferredSize(this.menuDimension);
		this.setLocation(0, 0);
		
		this.setupTitle();
		this.setupMenuCenter();
		
		this.setBackground(Color.decode("#CDCBCB"));
		this.setOpaque(true);
		this.setVisible(true);
	}
	
	/**
	 * Function that is used to display the title of the Menu
	 */
	public void setupTitle() {
		this.titleLabel = new JLabel("Welcome to Lost Treasure");
		this.titleLabel.setFont(new Font("Algerian", Font.BOLD, 32));
		
		int titleWidth = (int) (this.menuDimension.width);
		int titleHeight = 50;
		this.titleLabel.setSize(titleWidth, titleHeight);
		
		int titlePositionX = (this.menuDimension.width - titleWidth) / 2;
		int titlePositionY = 30;
		this.titleLabel.setLocation(titlePositionX, titlePositionY);
		
		this.titleLabel.setHorizontalAlignment(JFormattedTextField.CENTER);
		this.titleLabel.setForeground(Color.WHITE);
		
		this.titleLabel.setVisible(true);
		this.add(this.titleLabel);
	}
	
	/**
	 * Function that is used to display the center of the Menu
	 */
	public void setupMenuCenter() {
		this.menuCenter = new JPanel();
		this.menuCenter.setLayout(null);
		
		int centerWidth = (int) (this.menuDimension.width * 0.4);
		int centerHeight = (int) (this.menuDimension.height * 0.8);
		this.menuCenter.setSize(centerWidth, centerHeight);
		
		int centerPositionX = (this.menuDimension.width - centerWidth) / 2;
		int centerPositionY = (this.menuDimension.height - centerHeight) / 2 + 20;
		this.menuCenter.setLocation(centerPositionX, centerPositionY);
		
		this.setupNumberPanel();
		this.setupPlayerList();
		this.setupShare();
		this.setupPlayButton();
		
		this.menuCenter.setOpaque(false);
		this.menuCenter.setVisible(true);
		this.add(menuCenter);
	}
	
	/**
	 * Function that is used to display the Panel that contains the input for the number of players
	 */
	public void setupNumberPanel() {
		this.numberPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
			}
	    };
		
		this.numberPanel.setLayout(null);
		
		int panelWidth = (int) this.menuCenter.getWidth() - 40;
		int panelHeight = 50;
		this.numberPanel.setSize(panelWidth, panelHeight);
		this.numberPanel.setLocation(20, 20);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.numberPanel.setBorder(cellBorder);
		this.numberPanel.setBackground(Color.decode("#f7ec9c"));
		
		this.setupNumberField();
		this.setupNumberLabel();
		
		this.numberPanel.setOpaque(true);
		this.numberPanel.setVisible(true);
		this.menuCenter.add(this.numberPanel);
	}
	
	/**
	 * Function that is used to display the Label that contains the text of the request to input the number of players
	 */
	public void setupNumberLabel() {
		this.numberLabel = new JLabel("Players:");
		this.numberLabel.setFont(new Font("Arial", Font.BOLD, 16));
		this.numberLabel.setForeground(Color.BLACK);
		
		int labelWidth = (this.numberPanel.getWidth() - this.numberField.getWidth()) - 50;
		int labelHeight = 30;
		this.numberLabel.setSize(labelWidth, labelHeight);
		this.numberLabel.setLocation(20, 10);
		
		this.numberLabel.setBackground(Color.decode("#EBC436"));
		
		this.numberLabel.setVisible(true);
		this.numberPanel.add(this.numberLabel);
	}
	
	/**
	 * Function that is used to display the input field to input the number of players
	 */
	public void setupNumberField() {
		this.numberField = new JTextField();
		
		Dimension fieldDimension = new Dimension(60, 26);
		this.numberField.setSize(fieldDimension);
		
		int fieldPositionX = (this.numberPanel.getWidth() - fieldDimension.width) - 20;
		int fieldPositionY = 12;
		this.numberField.setLocation(fieldPositionX, fieldPositionY);
		
		this.numberField.setFont(new Font("Arial", Font.BOLD, 14));
		this.numberField.setHorizontalAlignment(JFormattedTextField.CENTER);
		this.numberField.setEditable(false);
		this.numberField.setBackground(Color.WHITE);
		
		this.numberPanel.add(this.numberField);
	}
	
	/**
	 * Function that is used to display the Panel to show the list of players
	 */
	public void setupPlayerList() {
		this.playerList = new JPanel();
		this.playerList.setLayout(null);
		
		int listWidth = this.menuCenter.getWidth() - 40;
		int listHeight = (this.menuCenter.getHeight() - this.numberPanel.getHeight()) - 120;
		this.playerList.setSize(listWidth, listHeight);
		
		int listPositionX = 20;
		int listPositionY = this.numberPanel.getHeight() + 40;
		this.playerList.setLocation(listPositionX, listPositionY);
		
		this.playerPanels = new JPanel[MAX_PLAYERS];
		this.playerFields = new JTextField[MAX_PLAYERS];
		for(int playerIndex = 0; playerIndex < this.playerPanels.length; playerIndex++) {
			this.setupPlayerPanel(playerIndex);
		}
		
		this.showPlayers();
		
		this.playerList.setOpaque(false);
		this.playerList.setVisible(true);
		this.menuCenter.add(this.playerList);
	}
	
	/**
	 * Function that is used to display the Panel to show a single player input
	 * @param playerIndex Index of the player
	 */
	public void setupPlayerPanel(int playerIndex) {
		JPanel playerPanel = new JPanel();
		playerPanel.setLayout(null);
		
		int panelWidth = this.playerList.getWidth();
		int panelHeight = 40;
		playerPanel.setSize(panelWidth, panelHeight);
		
		int panelPositionX = 0;
		int panelPositionY = ((panelHeight + 5) * playerIndex) + 0;
		playerPanel.setLocation(panelPositionX, panelPositionY);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		playerPanel.setBorder(cellBorder);
		playerPanel.setBackground(Color.decode("#f7ec9c"));
		playerPanel.setVisible(false);
		
		this.playerPanels[playerIndex] = playerPanel;
		this.playerList.add(playerPanel);

		this.setupPlayerColor(playerIndex);
		this.setupPlayerField(playerIndex);
	}
	
	/**
	 * Function that is used to display the panel to show the player color
	 * @param playerIndex Index of the player
	 */
	public void setupPlayerColor(int playerIndex) {
		JPanel panelColor = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);

				g.drawImage(playersIcons[playerIndex].getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
			}
	    };
		
		int panelWidth = this.playerPanels[playerIndex].getHeight() - 14;
		int panelHeight = this.playerPanels[playerIndex].getHeight() - 14;
		panelColor.setSize(panelWidth, panelHeight);
		
		int panelPositionX = (this.playerPanels[playerIndex].getHeight() - panelWidth) / 2;
		int panelPositionY = (this.playerPanels[playerIndex].getHeight() - panelHeight) / 2;
		panelColor.setLocation(panelPositionX, panelPositionY);
		
		panelColor.setVisible(true);
		panelColor.setOpaque(false);
		
		this.playerPanels[playerIndex].add(panelColor);
	}
	
	/**
	 * Function that is used to display the field to input the player's name
	 * @param playerIndex Index of the player
	 */
	public void setupPlayerField(int playerIndex) {
		JTextField playerField = new JTextField();

		int fieldSize = this.playerPanels[playerIndex].getHeight() - 14;
		int fieldOffset = (this.playerPanels[playerIndex].getHeight() - fieldSize) / 2;
		
		int fieldWidth = this.playerPanels[playerIndex].getWidth() - fieldSize - (fieldOffset * 3);
		int fieldHeight = fieldSize;
		playerField.setSize(fieldWidth, fieldHeight);
		
		int fieldPositionX = fieldSize + (fieldOffset * 2);
		int fieldPositionY = fieldOffset;
		playerField.setLocation(fieldPositionX, fieldPositionY);

		Border fieldBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		Border fieldPadding = BorderFactory.createEmptyBorder(2, 6, 2, 6);
		playerField.setBorder(BorderFactory.createCompoundBorder(fieldBorder, fieldPadding));
		playerField.setBackground(this.playerColors[playerIndex]);
		playerField.setForeground(Color.BLACK);
		playerField.setEditable(false);
		
		this.playerFields[playerIndex] = playerField;
		this.playerPanels[playerIndex].add(playerField);
	}
	
	/**
	 * Function that is used to display the players list
	 */
	public void showPlayers() {
		for(int playerIndex = 0; playerIndex < this.MAX_PLAYERS; playerIndex++) {
			if(playerIndex < this.nPlayers) {
				if(!this.playerPanels[playerIndex].isVisible()) {
					this.playerPanels[playerIndex].setVisible(true);
				}
			} else {
				this.playerPanels[playerIndex].setVisible(false);
			}
		}
	}
	
	/**
	 * Function that is used to display the "Play" Button
	 */
	public void setupPlayButton() {
		this.playButton = new JButton("PLAY");
		
		int buttonWidth = this.menuCenter.getWidth() - 40;
		int buttonHeight = 40;
		this.playButton.setSize(new Dimension(buttonWidth, buttonHeight));
		
		int fieldPositionX = 20;
		int fieldPositionY = this.numberLabel.getHeight() + this.playerList.getHeight() + 80;
		this.playButton.setLocation(fieldPositionX, fieldPositionY);
		
		this.playButton.setFont(new Font("Arial", Font.BOLD, 18));
		this.playButton.setForeground(Color.BLACK);
		this.playButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.playButton.setBorder(cellBorder);
		this.playButton.setOpaque(true);
		this.playButton.setBackground(Color.decode("#f7ec9c"));
		this.playButton.setFocusPainted(false);
		
		this.playButton.setVisible(true);
		this.menuCenter.add(this.playButton);
		
		this.playButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) { 
				StompSession connectionSession = launcher.getSession();
				String user_token = launcher.getUserToken();
				String game_token = launcher.getGameToken();
				
				StartController startController = new StartController(launcher, connectionSession, user_token, game_token);
				if(startController.sendStart()) {
					System.out.println("START SUCCESS");
				} else {
					System.out.println("START ERROR");
				}
			}
			
			@Override
			public void mouseEntered(MouseEvent e) { 
				Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
				playButton.setBorder(cellBorder);
				
				playButton.setBackground(Color.decode("#d9ca63"));
			}

			@Override
			public void mouseExited(MouseEvent e) { 
				playButton.setBackground(Color.decode("#f7ec9c"));
			}

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }
        });
	}
	
	public void copyGame() {
		Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(
                new StringSelection(game_token),
                null
        );
	}
	
	public void setupShare() {
		this.sharePanel = new JPanel();
		this.sharePanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { 
				copyGame();
			}
        });
		
		this.sharePanel.setLayout(null);
		
		int panelWidth = this.menuCenter.getWidth() - 40;
		int panelHeight = 30;
		this.sharePanel.setSize(new Dimension(panelWidth, panelHeight));
		
		int panelPositionX = 20;
		int panelPositionY = this.numberLabel.getHeight() + this.playerList.getHeight() + 40;
		this.sharePanel.setLocation(panelPositionX, panelPositionY);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.sharePanel.setBorder(cellBorder);
		this.sharePanel.setOpaque(true);
		this.sharePanel.setBackground(Color.decode("#f7ec9c"));
		
		this.setupShareLabel();
		this.setupShareButton();
		
		this.sharePanel.setVisible(true);
		this.menuCenter.add(this.sharePanel);		
	}
	
	public void setupShareLabel() {
		this.shareLabel = new JLabel();
		this.shareLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { 
				copyGame();
			}
        });
		
		this.shareLabel.setSize(new Dimension(this.menuCenter.getWidth() - 50, 20));
		this.shareLabel.setLocation(10, 5);
		
		this.sharePanel.add(this.shareLabel);
	}
	
	public void setupShareButton() {
		this.shareButton = new JButton();
		this.shareButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) { 
				copyGame();
			}
        });
		
		this.shareButton.setSize(20, 20);
		this.shareButton.setLocation(this.sharePanel.getWidth() - 30, 5);
		this.shareButton.setOpaque(false);
		this.shareButton.setContentAreaFilled(false);
		this.shareButton.setBorderPainted(false);
		this.shareButton.setVisible(true);
		
		try {
        	InputStream stream = getClass().getResourceAsStream("/share.png");
        	ImageIcon shareImage = new ImageIcon(ImageIO.read(stream));
        	this.shareButton.setIcon(shareImage);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.sharePanel.add(this.shareButton);
	}
	
	public void updateList(String game_token, int game_size, ArrayList<String> game_players) {
		this.game_token = game_token;
		this.numberField.setText(Integer.toString(game_size));
		this.nPlayers = game_size;
		
		for(int playerIndex = 0; playerIndex < this.nPlayers; playerIndex++) {
			this.playerFields[playerIndex].setText(game_players.get(playerIndex));
		}
		
		this.shareLabel.setText(this.game_token);
		
		this.showPlayers();
	}
	
	public void startGame() {
		this.hasInput = true;
	}
	
	public void disableButton() {
		this.playButton.setVisible(false);
	}
}
