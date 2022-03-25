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
	private boolean hasInput = false;
	
	private JPanel menuPanel;
	private JLabel usernameLabel;
	private JButton settingsButton;
	private JButton leaderboardButton;
	private JButton homeButton;
	
	private GameSettings gameSettings;
	private GameLeaderboard gameLeaderboard;
	private GameHome gameHome;
	private String dashboardPage = "";

	public GameDashboard(Launcher launcher, Dimension panelDimension) {
		this.launcher = launcher;
		this.panelDimension = panelDimension;
		
		this.gameSettings = new GameSettings(launcher, this, panelDimension, this.MENU_HEIGHT);
		this.gameLeaderboard = new GameLeaderboard(launcher, this,  panelDimension, this.MENU_HEIGHT);
		this.gameHome = new GameHome(launcher, this,  panelDimension, this.MENU_HEIGHT);
		
		this.setupDashboard();
		
		this.runHome();
	}
	
	private void removePage() {
		if(this.dashboardPage.equals("home")) {
			this.remove(this.gameHome);
		} else if(this.dashboardPage.equals("settings")) {
			this.remove(this.gameSettings);
		} else if(this.dashboardPage.equals("leaderboard")) {
			this.remove(this.gameLeaderboard);
		}
	}
	
	public void runHome() {
		this.removePage();
		this.add(this.gameHome);
		
		this.dashboardPage = "home";
		
		this.revalidate();
		this.repaint();
	}
	
	public void runSettings() {
		this.removePage();
		this.add(this.gameSettings);
		
		this.dashboardPage = "settings";
		
		this.revalidate();
		this.repaint();
	}
	
	public void runLeaderboard() {
		this.removePage();
		this.gameLeaderboard.updateList();
		this.add(this.gameLeaderboard);
		
		this.dashboardPage = "leaderboard";
		
		this.revalidate();
		this.repaint();
	}
	
	private void setupDashboard() {
		this.setPreferredSize(this.panelDimension);
		this.setLocation(0, 0);
		this.setLayout(null);
		this.setFont(new Font("Snap ITC", Font.BOLD, 11));
		
		this.setupMenu();
		
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
		this.setupLeaderboard();
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
		
		this.settingsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runSettings();
			}
		});
		
		this.menuPanel.add(this.settingsButton);
	}
	
	private void setupLeaderboard() {
		this.leaderboardButton = new JButton();
		this.leaderboardButton.setBackground(Color.decode("#f7ec9c"));
		this.leaderboardButton.setIcon(new ImageIcon(getClass().getResource("/leadership.png")));
		this.leaderboardButton.setSize(new Dimension(this.MENU_HEIGHT - 10, this.MENU_HEIGHT - 10));
		this.leaderboardButton.setLocation(panelDimension.width - (leaderboardButton.getWidth() + 10) * 2, 5);
		
		this.leaderboardButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runLeaderboard();
			}
		});
		
		this.menuPanel.add(this.leaderboardButton);
	}
	
	private void setupHome() {
		this.homeButton = new JButton();
		this.homeButton.setBackground(Color.decode("#f7ec9c"));
		this.homeButton.setIcon(new ImageIcon(getClass().getResource("/home.png")));
		this.homeButton.setSize(new Dimension(this.MENU_HEIGHT - 10, this.MENU_HEIGHT - 10));
		this.homeButton.setLocation(panelDimension.width - (homeButton.getWidth() + 10), 5);
		
		this.homeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				runHome();
			}
		});
		
		this.menuPanel.add(this.homeButton);
	}
	
	public void setUsername(String username) {
		this.usernameLabel.setText(username);
		
		this.gameSettings.setUsername(username);
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
	
	public void toggleInput() {
		this.hasInput = true;
	}
}
