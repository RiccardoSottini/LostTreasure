package game;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSession.Subscription;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import connection.ConnectionSessionHandler;
import connection.Request;
import connection.Response;
import connection.controllers.LoginController;
import connection.controllers.UpdateController;

/**
 * List of the Cell Colors
 */
enum CellColor {
	Blue,
	Red, 
	Green,
	Yellow,
	White
};

/**
 * List of the Cell Types
 */
enum CellType {
	Open,
	Star,
	Close,
	End
};

/**
 * Class used to run the Game
 */
public class Launcher extends JFrame {
	@Autowired
	private WebSocketStompClient connectionClient;
	
	@Autowired
	private StompSession connectionSession;
	
	private final char[] playerCodes = {'B', 'R', 'G', 'Y'};
	
	private final int MAX_PLAYERS = 4;
	private final int OPEN_CELLS = 52;
	private final int WIDTH = 905;
	private final int HEIGHT = 465;

	private Cell[] openCells;
	private Cell[][] closeCells;
	
	private int nPlayers;
	private Player[] players;
	private String[] playerNames;
	
	private GameLogin gameLogin;
	private GameDashboard gameDashboard;
	private GameMenu gameMenu;
	private GameBoard gameBoard;
	
	private String user_token;
	
	private String game_token;
	private int game_size;
	private ArrayList<String> game_players;
	private int user_index;
	
	private UpdateController updateController;
	private boolean is_finished = false;
	
	/**
	 * Creates an instance of the Launcher class
	 */
	public Launcher() {	
		try {
			this.setupConnection("ws://localhost:8080/ws-connect");
		} catch(Exception e) {
			System.out.println("Connection error: " + e.getMessage());
		}
		
		this.setupFrame();
		
		this.gameLogin = new GameLogin(this, new Dimension(this.WIDTH, this.HEIGHT));
		this.gameDashboard = new GameDashboard(this, new Dimension(this.WIDTH, this.HEIGHT));
		this.gameMenu = new GameMenu(this, new Dimension(this.WIDTH, this.HEIGHT));
		
		this.runLogin();
		this.runDashboard();
		this.runMenu();
		this.runGame();
	}
	
	private void centerScreen() {
        Dimension dimension = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - this.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - this.getHeight()) / 2);

        setLocation(x, y);
    }
	
	/**
	 * Setup the connection to the Server
	 * @param URL of the Server
	 */
	public void setupConnection(String URL) throws InterruptedException, ExecutionException {
		WebSocketClient client = new StandardWebSocketClient();
		this.connectionClient = new WebSocketStompClient(client);
		this.connectionClient.setMessageConverter(new StringMessageConverter());

		StompSessionHandler sessionHandler = new ConnectionSessionHandler();
		this.connectionSession = this.connectionClient.connect(URL, sessionHandler).get();
	}
	
	public void runLogin() {
		this.add(this.gameLogin);
		
		this.pack();
		this.setVisible(true);
		this.centerScreen();
		
		this.gameLogin.waitLogin();
		
		this.setVisible(false);
		this.remove(this.gameLogin);
	}
	
	public void runDashboard() {
		this.gameDashboard.setUsername(this.getUserToken());
		this.add(this.gameDashboard);
		
		this.pack();
		this.setVisible(true);
		
		this.gameDashboard.waitDashboard();
		
		this.setVisible(false);
		this.remove(this.gameDashboard);
	}
	
	/**
	 * Function used to display the Menu of the Game 
	 */
	public void runMenu() {
		this.add(this.gameMenu);
		
		this.pack();
		this.setVisible(true);
		
		this.gameMenu.waitMenu();
		this.playerNames = this.gameMenu.getPlayers();
		this.nPlayers = this.playerNames.length;
		
		this.setVisible(false);
		this.remove(this.gameMenu);
	}
	
	/**
	 * Function used to display the Board of the Game
	 */
	public void runGame() {
		this.setupGame();
		
		this.gameBoard = new GameBoard(this, this.players, this.openCells, this.closeCells);
		this.add(this.gameBoard, BorderLayout.CENTER);
		this.gameBoard.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		this.pack();
		this.setVisible(true);
		this.centerScreen();
	}
	
	/**
	 * Function that is used to setup the Frame
	 */
	public void setupFrame() {
		this.setTitle("Lost Treasure");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  
		this.setResizable(false);                              
		this.setLocationRelativeTo(null);
		
		try {
			BufferedImage image = ImageIO.read(getClass().getResource("/icon.jpg"));
			this.setIconImage(image);
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
	
	/**
	 * Function used to setup the variables to make the game run
	 */
	public void setupGame() {
		this.openCells = new Cell[OPEN_CELLS];
		this.closeCells = new Cell[MAX_PLAYERS][6];
		this.players = new Player[nPlayers];
		
		this.setupCells();
		this.setupPlayers();
		this.setupFrame();
	}
	
	/**
	 * Function used to setup the cells used by the logic of the Game
	 */
	public void setupCells() {
		for(int p = 0; p < MAX_PLAYERS; p++) {
			this.closeCells[p] = new Cell[6];
			
			for(int c = 0; c < 13; c++) {
				int cellIndex = (p * 13) + c;
				CellColor cellColor = CellColor.White;
				CellType cellType = CellType.Open;
				
				if(c == 0) {
					cellColor = CellColor.values()[cellIndex / 13];
				} else if(c == 8) {
					cellType = CellType.Star;
				}
				
				this.openCells[cellIndex] = new Cell(this, cellColor, cellType, cellIndex);
			}
			
			for(int c = 0; c < 6; c++) {
				CellColor cellColor = CellColor.values()[p];
				CellType cellType = CellType.Close;
				
				if(c == 5) {
					cellType = CellType.End;
				}
				
				this.closeCells[p][c] = new Cell(this, cellColor, cellType, c);
			}
		}
	}
	
	/**
	 * Function that is used to setup the Players classes that are used by the logic of the Game
	 */
	public void setupPlayers() {
		for(int playerIndex = 0; playerIndex < nPlayers; playerIndex++) {
			Cell[] playerCells = new Cell[57];
			char playerCode = playerCodes[playerIndex];
			CellColor playerColor = CellColor.values()[playerIndex];
			
			for(int c = 0; c <= 50; c++) {
				int cellIndex = ((playerIndex * 13) + c) % 52;
				playerCells[c] = this.openCells[cellIndex];
			}
			
			for(int c = 0; c < 6; c++) {
				int cellIndex = c + 51;
				playerCells[cellIndex] = this.closeCells[playerIndex][c];
			}
			
			this.players[playerIndex] = new Player(this.playerNames[playerIndex], playerCells, playerCode, playerColor, this);
		}
	}
	
	/**
	 * Function that is used to print the cells on the console
	 */
	public void printCells() {
		for(int c = 0; c < OPEN_CELLS; c++) {
			System.out.println("Cell #" + c + ", Color: " + openCells[c].getColor() + ", Type: " + openCells[c].getType());
			
			for(String archeologistCode : openCells[c].getArcheologistCodes()) {
				System.out.println("Archeologist Code: " + archeologistCode);
			}
		}
		
		System.out.println("-----------");
		
		for(int p = 0; p < nPlayers; p++) {
			for(int c = 0; c < 6; c++) {
				System.out.println("Close Cell #" + playerCodes[p] + c + ", Color: " + closeCells[p][c].getColor() + ", Type: " + closeCells[p][c].getType());
				
				for(String archeologistCode : closeCells[p][c].getArcheologistCodes()) {
					System.out.println("Archeologist Code: " + archeologistCode);
				}
			}
			
			System.out.println("-----------");
		}
		
		System.out.println();
	}
	
	public StompSession getSession() {
		return this.connectionSession;
	}
	
	public String getUserToken() {
		return this.user_token;
	}
	
	public String getGameToken() {
		return this.game_token;
	}
	
	public void setUserToken(String user_token) {
		this.user_token = user_token;
	}
	
	public void setGameToken(String game_token) {
		this.game_token = game_token;
	}
	
	public void setGameSize(int game_size) {
		this.game_size = game_size;
	}
	
	public void setGamePlayers(ArrayList<String> game_players) {
		this.game_players = game_players;
	}
	
	public void setFinished(boolean is_finished) {
		this.is_finished = is_finished;
	}
	
	public boolean isFinished() {
		return this.is_finished;
	}
	
	public void updateList(HashMap<String, String> response_list) {
		String game_token = response_list.get("game_token");
		int game_size = Integer.parseInt(response_list.get("game_size"));
		ArrayList<String> game_players = new ArrayList<String>();
		
		for(int p = 0; p < game_size; p++) {
			game_players.add(response_list.get("player_" + p));
		}
		
		this.setGameToken(game_token);
		this.setGameSize(game_size);
		this.setGamePlayers(game_players);
		
		if(response_list.containsKey("user_host")) {
			String user_host = response_list.get("user_host");
			if(!user_host.equals(this.getUserToken())) {
				this.gameMenu.disableButton();
			}
		}
		
		if(response_list.containsKey("user_index")) {
			this.user_index = Integer.parseInt(response_list.get("user_index"));
		}
		
		this.gameMenu.updateList(game_token, game_size, game_players);
	}
	
	public void startGame(HashMap<String, String> response_list) {
		this.updateController = new UpdateController(this, this.connectionSession, this.getUserToken());
		
		this.updateList(response_list);
		this.gameMenu.startGame();
	}
	
	public void updateTurn(int turn_index) {
		for(int p = 0; p < this.players.length; p++) {
			if(p == turn_index) {
				this.players[p].setTurn(true);
			} else {
				this.players[p].setTurn(false);
			}
		}
	}
	
	public void updateDices(int diceValue, int turn_index, boolean can_move) {
		Player player = this.players[turn_index];
		player.setCanMove(can_move);
		
		Dice dice = player.getDice();
		dice.setValue(diceValue);
		dice.drawDice();
	}
	
	public void updateArcheologist(int turn_index, int archeologist_index, int cell_index, String cell_type) {
		if(cell_type.equals("open")) {
			Archeologist archeologist = this.players[turn_index].getArcheologist(archeologist_index);
			archeologist.moveArcheologist(openCells[cell_index]);
		} else if(cell_type.equals("close")) {
			Archeologist archeologist = this.players[turn_index].getArcheologist(archeologist_index);
			archeologist.moveArcheologist(closeCells[turn_index][cell_index]);
		}
	}
	
	public void updateKill(int kill_user_index, int kill_archeologist_index) {
		Player player = this.players[kill_user_index];
		
		if(player != null) {
			Archeologist archeologist = player.getArcheologist(kill_archeologist_index);
			archeologist.moveBase();
		}
	}
	
	public void updateWon(int user_index, int won_index, boolean is_finished) {
		Player playerWon = this.players[user_index];
		playerWon.setWon(true, won_index);
		
		if(is_finished) {
			this.setFinished(true);
			
			for(Player player : this.players) {
				if(!player.hasWon()) {
					player.setLoseLabel();
				}
			}
		}
	}
	
	public void updateMessage(int user_index, String user_message) {
		this.gameBoard.addMessage(user_index, user_message);
	}
	
	/**
	 * Main function used to run the entire program
	 * @param args Command Line arguments - in this case they are ignored by the program
	 */
	public static void main(String[] args) {	
		Launcher launcher = new Launcher();
	}
}
