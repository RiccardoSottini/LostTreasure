package game;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import connection.controllers.LeaderboardController;
import connection.controllers.LoginController;

public class GameLeaderboard extends JPanel {
	private final Launcher launcher;
	private final GameDashboard gameDashboard;
	private final Dimension panelDimension;
	private final int menuHeight;
	
	private BufferedImage backgroundImage;
	private JPanel scoreContent;
	
	private JButton refreshButton;
	private JButton websiteButton;
	
	private final int ENTRY_WIDTH = 350;
	private final int ENTRY_HEIGHT = 26;

	public GameLeaderboard(Launcher launcher, GameDashboard gameDashboard, Dimension panelDimension, int menuHeight) {
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

		this.setupLeaderboard();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this.panelDimension.width, this.panelDimension.height, this);
	}

	private void setupLeaderboard() {
		this.setSize(this.panelDimension.width, this.panelDimension.height);
		this.setLocation(0, this.menuHeight);
		this.setLayout(null);

		this.setupContent();
		this.setupRefresh();
		this.setupWebsite();
	}
	
	private void setupContent() {
		this.scoreContent = new JPanel();
		this.scoreContent.setPreferredSize(new Dimension(this.panelDimension.width, this.panelDimension.height - 80));
		this.scoreContent.setSize(new Dimension(this.panelDimension.width, this.panelDimension.height - 80));
		this.scoreContent.setOpaque(false);
		this.scoreContent.setBounds(this.panelDimension.width / 4, 60, this.panelDimension.width / 2, this.panelDimension.height - 110);
		
		this.add(this.scoreContent, Component.CENTER_ALIGNMENT);
	}

	private void setupRefresh() {
		this.refreshButton = new JButton("Refresh");
		this.refreshButton.setToolTipText("Will refetch the leaderboard data from the server and update it.");
		this.refreshButton.setBackground(Color.decode("#f7ec9c"));
		this.refreshButton.setIcon(new ImageIcon(getClass().getResource("/refresh.png")));
		this.refreshButton.setSize(ENTRY_WIDTH / 2 - 5, 30);
		this.refreshButton.setLocation(this.panelDimension.width / 2 + 5, 20);

		this.refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateList();
			}
		});

		this.add(this.refreshButton);
	}

	private void setupWebsite() {
		this.websiteButton = new JButton("Open website");
		this.websiteButton.setToolTipText("Opens Leaderboard webpage.");
		this.websiteButton.setBackground(Color.decode("#f7ec9c"));
		this.websiteButton.setIcon(new ImageIcon(getClass().getResource("/share.png")));
		this.websiteButton.setSize(ENTRY_WIDTH / 2 - 5, 30);
		this.websiteButton.setLocation(this.panelDimension.width / 2 - ENTRY_WIDTH / 2, 20);

		this.websiteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Desktop desk = Desktop.getDesktop();

				try {
					desk.browse(new URI("https://losttreasure.rf.gd/leaderboard.php"));
				} catch (Exception ex) {
					System.out.println("Cannot open the Lost Treasure Online Leaderboard");
				}
			}
		});

		this.add(this.websiteButton);
	}
	
	public void updateList() {	
		LeaderboardController leaderboardController = new LeaderboardController(launcher, launcher.getUserToken());
		
		if(leaderboardController.sendUpdate()) {
			int count = 1;
			LinkedHashMap<String, Integer> list = leaderboardController.getList();
			
			this.scoreContent.removeAll();
			
			for (Map.Entry<String, Integer> value : list.entrySet()) {
				String username = value.getKey();
				Integer score = value.getValue();
				
				JPanel listEntry = new JPanel();
				listEntry.setLayout(null);
				listEntry.setBackground(Color.decode("#f7ec9c"));
				listEntry.setPreferredSize(new Dimension(this.ENTRY_WIDTH, this.ENTRY_HEIGHT));
				listEntry.setSize(new Dimension(this.ENTRY_WIDTH, this.ENTRY_HEIGHT));
				listEntry.setMinimumSize(new Dimension(this.ENTRY_WIDTH, this.ENTRY_HEIGHT));
				
				JLabel usernameEntry = new JLabel(count + ". " + username);
				usernameEntry.setFont(new Font("Tahoma", Font.BOLD, 14));
				usernameEntry.setForeground(Color.BLACK);
				usernameEntry.setPreferredSize(new Dimension(this.ENTRY_WIDTH / 2, this.ENTRY_HEIGHT));
				usernameEntry.setSize(new Dimension(this.ENTRY_WIDTH / 2, this.ENTRY_HEIGHT));
				usernameEntry.setLocation(10, 0);
				
				JLabel scoreEntry = new JLabel(Integer.toString(score));
				scoreEntry.setFont(new Font("Tahoma", Font.BOLD, 14));
				scoreEntry.setForeground(Color.BLACK);
				scoreEntry.setPreferredSize(new Dimension(this.ENTRY_WIDTH / 2, this.ENTRY_HEIGHT));
				scoreEntry.setSize(new Dimension(this.ENTRY_WIDTH / 2, this.ENTRY_HEIGHT));
				scoreEntry.setLocation(this.ENTRY_WIDTH - 50, 0);
				
				listEntry.add(usernameEntry);
				listEntry.add(scoreEntry);
				
				this.scoreContent.add(listEntry);
				count++;
			}
			
			this.scoreContent.revalidate();
			this.scoreContent.repaint();
		} else {
			System.out.println("LEADERBOARD ERROR");
			System.out.println("ERROR: " + leaderboardController.getError());
		}
	}
}
