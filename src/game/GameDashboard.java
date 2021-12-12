package game;

import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JComponent;
import java.awt.Image;
import java.awt.Graphics;

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.controllers.CreateController;
import connection.controllers.JoinController;

import javax.swing.JTextField;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;

public class GameDashboard extends JPanel {
	private Launcher launcher;
	private Dimension panelDimension;
	private final int MENU_HEIGHT = 60;
	private BufferedImage backgroundImage;
	private boolean hasInput = false;
	
	private JPanel menuPanel;
	private JLabel usernameLabel;
	private JButton settingsButton;
	private JButton leadershipButton;
	private JButton homeButton;
	
	private JPanel contentPanel;
	private JButton createButton;
	private JLabel orLabel;
	private JLabel codeLabel;
	private JTextField codeField;
	private JButton joinButton;

	public GameDashboard(Launcher launcher, Dimension panelDimension) {
		this.launcher = launcher;
		this.panelDimension = panelDimension;
		
		try {
			InputStream stream = getClass().getResourceAsStream("/menu-background.jpg");
			this.backgroundImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setupDashboard();
	}
	
	private void setupDashboard() {
		this.setPreferredSize(this.panelDimension);
		this.setLocation(0, 0);
		this.setLayout(null);
		this.setFont(new Font("Snap ITC", Font.BOLD, 11));
		
		this.setupMenu();
		this.setupContent();
		
		this.setVisible(true);
	}
	
	private void setupMenu() {
		this.menuPanel = new JPanel();
		this.menuPanel.setBackground(Color.decode("#f7ec9c"));
		this.menuPanel.setSize(new Dimension(this.panelDimension.width, this.MENU_HEIGHT));
		this.menuPanel.setLocation(0, 0);
		this.menuPanel.setLayout(null);
		
		this.setupUsername();
		this.setupSettings();
		this.setupLeadership();
		this.setupHome();
		
		this.add(this.menuPanel);
	}
	
	private void setupUsername() {
		this.usernameLabel = new JLabel();
		this.usernameLabel.setFont(new Font("Arial", Font.BOLD, 20));
		this.usernameLabel.setSize(new Dimension(300, this.MENU_HEIGHT - 20));
		this.usernameLabel.setLocation(10, 10);
		
		this.menuPanel.add(this.usernameLabel);
	}
	
	private void setupSettings() {
		this.settingsButton = new JButton();
		this.settingsButton.setBackground(Color.decode("#f7ec9c"));
		this.settingsButton.setIcon(new ImageIcon(getClass().getResource("/settings.png")));
		this.settingsButton.setSize(new Dimension(this.MENU_HEIGHT - 10, this.MENU_HEIGHT - 10));
		this.settingsButton.setLocation(panelDimension.width - (settingsButton.getWidth() + 10) * 3, 5);
		
		this.menuPanel.add(this.settingsButton);
	}
	
	private void setupLeadership() {
		this.leadershipButton = new JButton();
		this.leadershipButton.setBackground(Color.decode("#f7ec9c"));
		this.leadershipButton.setIcon(new ImageIcon(getClass().getResource("/leadership.png")));
		this.leadershipButton.setSize(new Dimension(this.MENU_HEIGHT - 10, this.MENU_HEIGHT - 10));
		this.leadershipButton.setLocation(panelDimension.width - (leadershipButton.getWidth() + 10) * 2, 5);
		
		this.menuPanel.add(this.leadershipButton);
	}
	
	private void setupHome() {
		this.homeButton = new JButton();
		this.homeButton.setBackground(Color.decode("#f7ec9c"));
		this.homeButton.setIcon(new ImageIcon(getClass().getResource("/home.png")));
		this.homeButton.setSize(new Dimension(this.MENU_HEIGHT - 10, this.MENU_HEIGHT - 10));
		this.homeButton.setLocation(panelDimension.width - (homeButton.getWidth() + 10), 5);
		
		this.menuPanel.add(this.homeButton);
	}
	
	private void setupContent() {
		this.contentPanel = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
			}
		};
		this.contentPanel.setSize(new Dimension(this.panelDimension.width, this.panelDimension.height - this.MENU_HEIGHT));
		this.contentPanel.setLocation(0, this.MENU_HEIGHT);
		this.contentPanel.setLayout(null);
		
		this.setupCreate();
		this.setupOr();
		this.setupJoin();
		
		this.add(this.contentPanel);
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
					toggleInput();
				} else {
					System.out.println("CREATE ERROR");
					System.out.println("ERROR: " + createController.getError());
				}
			}
		});
		
		this.contentPanel.add(this.createButton);
	}
	
	private void setupOr() {
		this.orLabel = new JLabel("OR");
		this.orLabel.setForeground(Color.WHITE);
		this.orLabel.setFont(new Font("Arial", Font.BOLD, 25));
		this.orLabel.setSize(new Dimension(40, 40));
		this.orLabel.setLocation(this.panelDimension.width / 2 - this.orLabel.getWidth(), this.panelDimension.height / 2 - this.MENU_HEIGHT - 20);
		
		this.contentPanel.add(this.orLabel);
	}
	
	private void setupJoin() {
		this.codeLabel = new JLabel("Code:");
		this.codeLabel.setForeground(Color.white);
		this.codeLabel.setFont(new Font("Arial", Font.BOLD, 20));
		this.codeLabel.setSize(new Dimension(80, 25));
		this.codeLabel.setLocation(this.panelDimension.width / 4, this.panelDimension.height - this.MENU_HEIGHT * 4);

		this.codeField = new JTextField();
		this.codeField.setBackground(Color.decode("#f7ec9c"));
		this.codeField.setSize(new Dimension(this.panelDimension.width / 2 - 80, 25));
		this.codeField.setLocation(this.panelDimension.width / 4 + 80, this.panelDimension.height - this.MENU_HEIGHT * 4);
		this.codeField.setBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.black),
				BorderFactory.createEmptyBorder(3, 3, 3, 3))
		);
		
		this.joinButton = new JButton("Join Game");
		this.joinButton.setBackground(Color.decode("#f7ec9c"));		
		this.joinButton.setFont(new Font("Arial", Font.BOLD, 20));
		this.joinButton.setSize(new Dimension(this.panelDimension.width / 2, 60));
		this.joinButton.setLocation(this.panelDimension.width / 4, this.panelDimension.height - this.MENU_HEIGHT * 4 + 40);
		this.joinButton.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.joinButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				StompSession connectionSession = launcher.getSession();
				String user_token = launcher.getUserToken();
				String game_token = codeField.getText();
				
				if(!game_token.equals("")) {
					JoinController joinController = new JoinController(launcher, connectionSession, user_token, game_token);
					if(joinController.sendJoin()) {
						toggleInput();
					} else {
						System.out.println("JOIN ERROR");
						System.out.println("ERROR: " + joinController.getError());
					}
				}
			}
		});
		
		this.contentPanel.add(this.codeLabel);
		this.contentPanel.add(this.codeField);
		this.contentPanel.add(this.joinButton);
	}
	
	public void setUsername(String username) {
		this.usernameLabel.setText(username);
	}
	
	public void waitDashboard() {
		while(!this.hasInput) {
			try {
			    Thread.sleep(100);
			} catch (Exception e) {
			    e.printStackTrace();
			}
		}
	}
	
	private void toggleInput() {
		this.hasInput = true;
	}
}
