package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.controllers.CreateController;
import connection.controllers.JoinController;

public class GameHome extends JPanel {
	private Launcher launcher;
	private GameDashboard gameDashboard;
	private Dimension panelDimension;
	private int menuHeight;
	
	private BufferedImage backgroundImage;
	
	private JButton createButton;
	private JLabel orLabel;
	private JLabel codeLabel;
	private JTextField codeField;
	private JButton joinButton;
	
	public GameHome(Launcher launcher, GameDashboard gameDashboard, Dimension panelDimension, int menuHeight) {
		this.launcher = launcher;
		this.gameDashboard = gameDashboard;
		this.panelDimension = panelDimension;
		this.menuHeight = menuHeight;
		
		try {
			InputStream stream = getClass().getResourceAsStream("/menu-background.jpg");
			this.backgroundImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setupHome();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	private void setupHome() {
		this.setSize(new Dimension(this.panelDimension.width, this.panelDimension.height - this.menuHeight));
		this.setLocation(0, this.menuHeight);
		this.setLayout(null);
		
		this.setupCreate();
		this.setupOr();
		this.setupJoin();
	}
	
	private void setupCreate() {
		this.createButton = new JButton("Create a new Game");
		this.createButton.setBackground(Color.decode("#f7ec9c"));
		this.createButton.setFont(new Font("Arial", Font.BOLD, 20));
		this.createButton.setSize(new Dimension(this.panelDimension.width / 2, 60));
		this.createButton.setLocation(this.panelDimension.width / 4, 50);
		this.createButton.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.createButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StompSession connectionSession = launcher.getSession();
				String user_token = launcher.getUserToken();
				
				CreateController createController = new CreateController(launcher, connectionSession, user_token);
				if(createController.sendCreate()) {
					gameDashboard.toggleInput();
				} else {
					System.out.println("CREATE ERROR");
					System.out.println("ERROR: " + createController.getError());
				}
			}
		});
		
		this.add(this.createButton);
	}
	
	private void setupOr() {
		this.orLabel = new JLabel("OR");
		this.orLabel.setForeground(Color.WHITE);
		this.orLabel.setFont(new Font("Arial", Font.BOLD, 25));
		this.orLabel.setSize(new Dimension(40, 40));
		this.orLabel.setLocation(this.panelDimension.width / 2 - this.orLabel.getWidth(), this.panelDimension.height / 2 - this.menuHeight - 20);
		
		this.add(this.orLabel);
	}
	
	private void setupJoin() {
		this.codeLabel = new JLabel("Code:");
		this.codeLabel.setForeground(Color.white);
		this.codeLabel.setFont(new Font("Arial", Font.BOLD, 20));
		this.codeLabel.setSize(new Dimension(80, 25));
		this.codeLabel.setLocation(this.panelDimension.width / 4, this.panelDimension.height - this.menuHeight * 4);

		this.codeField = new JTextField();
		this.codeField.setBackground(Color.decode("#f7ec9c"));
		this.codeField.setSize(new Dimension(this.panelDimension.width / 2 - 80, 25));
		this.codeField.setLocation(this.panelDimension.width / 4 + 80, this.panelDimension.height - this.menuHeight * 4);
		this.codeField.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.black),
				BorderFactory.createEmptyBorder(3, 3, 3, 3))
		);
		
		this.joinButton = new JButton("Join Game");
		this.joinButton.setBackground(Color.decode("#f7ec9c"));		
		this.joinButton.setFont(new Font("Arial", Font.BOLD, 20));
		this.joinButton.setSize(new Dimension(this.panelDimension.width / 2, 60));
		this.joinButton.setLocation(this.panelDimension.width / 4, this.panelDimension.height - this.menuHeight * 4 + 40);
		this.joinButton.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StompSession connectionSession = launcher.getSession();
				String user_token = launcher.getUserToken();
				String game_token = codeField.getText();
				
				if(!game_token.equals("")) {
					JoinController joinController = new JoinController(launcher, connectionSession, user_token, game_token);
					if(joinController.sendJoin()) {
						gameDashboard.toggleInput();
					} else {
						System.out.println("JOIN ERROR");
						System.out.println("ERROR: " + joinController.getError());
					}
				}
			}
		});
		
		this.add(this.codeLabel);
		this.add(this.codeField);
		this.add(this.joinButton);
	}
}
