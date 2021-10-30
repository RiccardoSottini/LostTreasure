import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
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

/**
 * Class that is used to manage the Menu of the Game
 */
public class GameMenu extends JPanel {
	private final Color[] playerColors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557")
	};
	
	private final int MIN_PLAYERS = 2;
	private final int MAX_PLAYERS = 4;
	private int nPlayers;
	
	private final Dimension menuDimension;
	
	private JLabel titleLabel;
	private JPanel menuCenter;
	
	private JPanel numberPanel;
	private JLabel numberLabel;
	private JSpinner numberField;
	
	private JPanel playerList;
	private JPanel[] playerPanels;
	private JTextField[] playerFields;
	
	private JButton playButton;
	
	private boolean hasInput;
	
	/**
	 * Creates a new instance of GameMenu
	 * @param menuDimension Dimension of the Menu
	 */
	public GameMenu(Dimension menuDimension) {
		this.menuDimension = menuDimension;
		this.nPlayers = MIN_PLAYERS;
		this.hasInput = false;
		
		this.setupMenu();
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
		this.titleLabel = new JLabel("Lost Treasure");
		this.titleLabel.setFont(new Font("Arial", Font.BOLD, 40));
		
		int titleWidth = (int) (this.menuDimension.width * 0.5);
		int titleHeight = 50;
		this.titleLabel.setSize(titleWidth, titleHeight);
		
		int titlePositionX = (this.menuDimension.width - titleWidth) / 2;
		int titlePositionY = 15;
		this.titleLabel.setLocation(titlePositionX, titlePositionY);
		
		this.titleLabel.setHorizontalAlignment(JFormattedTextField.CENTER);
		this.titleLabel.setForeground(Color.BLACK);
		
		this.titleLabel.setVisible(true);
		this.add(this.titleLabel);
	}
	
	/**
	 * Function that is used to display the center of the Menu
	 */
	public void setupMenuCenter() {
		this.menuCenter = new JPanel();
		this.menuCenter.setLayout(null);
		
		int centerWidth = (int) (this.menuDimension.width * 0.5);
		int centerHeight = (int) (this.menuDimension.height * 0.8);
		this.menuCenter.setSize(centerWidth, centerHeight);
		
		int centerPositionX = (this.menuDimension.width - centerWidth) / 2;
		int centerPositionY = (this.menuDimension.height - centerHeight) / 2 + 20;
		this.menuCenter.setLocation(centerPositionX, centerPositionY);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.menuCenter.setBorder(cellBorder);
		this.menuCenter.setBackground(Color.decode("#2F6489"));
		
		this.setupNumberPanel();
		this.setupPlayerList();
		this.setupPlayButton();
		
		this.menuCenter.setOpaque(true);
		this.menuCenter.setVisible(true);
		this.add(menuCenter);
	}
	
	/**
	 * Function that is used to display the Panel that contains the input for the number of players
	 */
	public void setupNumberPanel() {
		this.numberPanel = new JPanel();
		this.numberPanel.setLayout(null);
		
		int panelWidth = (int) this.menuCenter.getWidth() - 40;
		int panelHeight = 50;
		this.numberPanel.setSize(panelWidth, panelHeight);
		this.numberPanel.setLocation(20, 20);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.numberPanel.setBorder(cellBorder);
		this.numberPanel.setBackground(Color.decode("#EBC436"));

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
		this.numberLabel = new JLabel("Number of Players:");
		numberLabel.setFont(new Font("Arial", Font.BOLD, 15));
		
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
		SpinnerModel fieldModel = new SpinnerNumberModel(MIN_PLAYERS, MIN_PLAYERS, MAX_PLAYERS, 1);
		this.numberField = new JSpinner(fieldModel);
		
		this.numberField.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSpinner numberField = (JSpinner) e.getSource();
                
                nPlayers = (int) numberField.getValue();
                showPlayers();
            }
        });
		
		Dimension fieldDimension = new Dimension(60, 26);
		this.numberField.setSize(fieldDimension);
		
		int fieldPositionX = (this.numberPanel.getWidth() - fieldDimension.width) - 20;
		int fieldPositionY = 12;
		this.numberField.setLocation(fieldPositionX, fieldPositionY);
		
		JFormattedTextField numberText = ((JSpinner.DefaultEditor) this.numberField.getEditor()).getTextField();
		numberText.setFont(new Font("Arial", Font.BOLD, 14));
		numberText.setHorizontalAlignment(JFormattedTextField.CENTER);
		numberText.setEditable(false);
		numberText.setBackground(Color.WHITE);
		
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
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.playerList.setBorder(cellBorder);
		this.playerList.setBackground(Color.decode("#B18E56"));
		
		this.playerPanels = new JPanel[MAX_PLAYERS];
		this.playerFields = new JTextField[MAX_PLAYERS];
		for(int playerIndex = 0; playerIndex < this.playerPanels.length; playerIndex++) {
			this.setupPlayerPanel(playerIndex);
		}
		
		this.showPlayers();
		
		this.playerList.setOpaque(true);
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
		
		int panelWidth = this.playerList.getWidth() - 20;
		int panelHeight = 40;
		playerPanel.setSize(panelWidth, panelHeight);
		
		int panelPositionX = 10;
		int panelPositionY = ((panelHeight + 5) * playerIndex) + 10;
		playerPanel.setLocation(panelPositionX, panelPositionY);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		playerPanel.setBorder(cellBorder);
		playerPanel.setBackground(Color.decode("#EBC436"));
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
		JPanel panelColor = new JPanel();
		
		int panelWidth = this.playerPanels[playerIndex].getHeight() - 14;
		int panelHeight = this.playerPanels[playerIndex].getHeight() - 14;
		panelColor.setSize(panelWidth, panelHeight);
		
		int panelPositionX = (this.playerPanels[playerIndex].getHeight() - panelWidth) / 2;
		int panelPositionY = (this.playerPanels[playerIndex].getHeight() - panelHeight) / 2;
		panelColor.setLocation(panelPositionX, panelPositionY);
		
		Border panelBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		panelColor.setBorder(panelBorder);
		panelColor.setBackground(this.playerColors[playerIndex]);
		panelColor.setVisible(true);
		
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
		
		this.playButton.setFont(new Font("Arial", Font.BOLD, 16));
		this.playButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
		this.playButton.setBorder(cellBorder);
		this.playButton.setOpaque(true);
		this.playButton.setBackground(Color.decode("#EBC436"));
		this.playButton.setFocusPainted(false);
		
		this.playButton.setVisible(true);
		this.menuCenter.add(this.playButton);
		
		this.playButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) { 
				hasInput = true;
			}

			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }
			
			@Override
			public void mouseEntered(MouseEvent e) { 
				Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 2);
				playButton.setBorder(cellBorder);
				
				playButton.setBackground(Color.decode("#B58E0F"));
			}

			@Override
			public void mouseExited(MouseEvent e) { 
				playButton.setBackground(Color.decode("#EBC436"));
			}
        });
	}
}
