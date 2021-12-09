package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;

import org.springframework.messaging.simp.stomp.StompSession;

import connection.controllers.LoginController;
import connection.controllers.StartController;

public class GameLogin extends JPanel {
	private final Launcher launcher;
	private final Dimension panelDimension;
	
	private JLabel titleLabel;
	
	private JPanel usernamePanel;
	private JLabel usernameLabel;
	private JTextField usernameField;
	
	private JPanel passwordPanel;
	private JLabel passwordLabel;
	private JTextField passwordField;
	
	private JButton changeButton;
	private JButton loginButton;
	
	private boolean hasInput = false;
	
	private BufferedImage backgroundImage;
	
	public GameLogin(Launcher launcher, Dimension panelDimension) {
		this.launcher = launcher;
		this.panelDimension = panelDimension;
		
		try {
			InputStream stream = getClass().getResourceAsStream("/menu-background.jpg");
			this.backgroundImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setupLogin();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	private void setupLogin() {
		this.setPreferredSize(this.panelDimension);
		this.setLocation(0, 0);
		this.setLayout(null);
		
		this.setupTitle();
		this.setupUsernameField();
		this.setupPasswordField();
		this.setupChange();
		this.setupLoginButton();
		
		this.setVisible(true);
	}
	
	private void setupTitle() {
		this.titleLabel = new JLabel("Lost Treasure's Login Page");
		this.titleLabel.setFont(new Font("Algerian", Font.BOLD, 32));
		
		int titleWidth = (int) (this.panelDimension.width);
		int titleHeight = 50;
		this.titleLabel.setSize(titleWidth, titleHeight);
		
		int titlePositionX = (this.panelDimension.width - titleWidth) / 2;
		int titlePositionY = 30;
		this.titleLabel.setLocation(titlePositionX, titlePositionY);
		
		this.titleLabel.setHorizontalAlignment(JFormattedTextField.CENTER);
		this.titleLabel.setForeground(Color.WHITE);
		
		this.add(this.titleLabel);
	}
	
	private void setupUsernameField() {
		this.usernamePanel = new JPanel();
		this.usernamePanel.setSize(new Dimension(this.panelDimension.width / 2, 40));
		this.usernamePanel.setLocation((this.panelDimension.width - this.usernamePanel.getWidth()) / 2, 120);
		this.usernamePanel.setBackground(Color.decode("#f7ec9c"));
		this.usernamePanel.setBorder(BorderFactory.createLineBorder(Color.black));
		this.usernamePanel.setLayout(null);
		
		this.usernameLabel = new JLabel("Username:");
		this.usernameLabel.setSize(new Dimension(this.usernamePanel.getWidth() / 4, 40));
		this.usernameLabel.setLocation(10, 0);
		this.usernameLabel.setForeground(Color.black);
		
		this.usernameField = new JTextField();
		this.usernameField.setSize(new Dimension(this.usernamePanel.getWidth() / 4 * 3 - 10, 25));
		this.usernameField.setLocation(this.usernamePanel.getWidth() / 4, (this.usernamePanel.getHeight() - this.usernameField.getHeight()) / 2);
		this.usernameField.setBackground(Color.LIGHT_GRAY);
		this.usernameField.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.usernamePanel.add(this.usernameLabel);
		this.usernamePanel.add(this.usernameField);
		this.add(this.usernamePanel);
	}
	
	private void setupPasswordField() {
		this.passwordPanel = new JPanel();
		this.passwordPanel.setSize(new Dimension(this.panelDimension.width / 2, 40));
		this.passwordPanel.setLocation((this.panelDimension.width - this.passwordPanel.getWidth()) / 2, 175);
		this.passwordPanel.setBackground(Color.decode("#f7ec9c"));
		this.passwordPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		this.passwordPanel.setLayout(null);
		
		this.passwordLabel = new JLabel("Password:");
		this.passwordLabel.setSize(new Dimension(this.passwordPanel.getWidth() / 4, 40));
		this.passwordLabel.setLocation(10, 0);
		this.passwordLabel.setForeground(Color.black);
		
		this.passwordField = new JTextField();
		this.passwordField.setSize(new Dimension(this.passwordPanel.getWidth() / 4 * 3 - 10, 25));
		this.passwordField.setLocation(this.passwordPanel.getWidth() / 4, (this.passwordPanel.getHeight() - this.passwordField.getHeight()) / 2);
		this.passwordField.setBackground(Color.LIGHT_GRAY);
		this.passwordField.setBorder(BorderFactory.createLineBorder(Color.black));
		
		this.passwordPanel.add(this.passwordLabel);
		this.passwordPanel.add(this.passwordField);
		this.add(this.passwordPanel);
	}
	
	private void setupChange() {
		this.changeButton = new JButton("Click here if you don't already have an account!");
		this.changeButton.setSize(new Dimension(this.panelDimension.width, 20));
		this.changeButton.setLocation(0, 275);
		this.changeButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		this.changeButton.setFont(new Font("Arial", Font.BOLD, 20));
		this.changeButton.setForeground(Color.WHITE);
		this.changeButton.setContentAreaFilled(false);
		this.changeButton.setBorderPainted(false);		
		this.changeButton.setOpaque(false);
		
		this.changeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println("CHANGE TO REGISTER");
			}
		});

		this.add(this.changeButton);
	}
	
	/**
	 * Function that is used to display the "Play" Button
	 */
	public void setupLoginButton() {
		this.loginButton = new JButton("LOGIN");
		this.loginButton.setSize(new Dimension(this.panelDimension.width / 2, 40));
		this.loginButton.setLocation((this.panelDimension.width - this.loginButton.getWidth()) / 2, 300);
		
		this.loginButton.setFont(new Font("Arial", Font.BOLD, 18));
		this.loginButton.setForeground(Color.BLACK);
		this.loginButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		this.loginButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		this.loginButton.setBackground(Color.decode("#f7ec9c"));
		this.loginButton.setFocusPainted(false);
		this.loginButton.setVisible(true);
		
		this.loginButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				String password = passwordField.getText();
				
				LoginController loginController = new LoginController(launcher.getSession(), username, password);
				if(loginController.sendLogin()) {
					launcher.setUserToken(loginController.getToken());
					toggleInput();
				} else {
					System.out.println("LOGIN ERROR");
					System.out.println("ERROR: " + loginController.getError());
				}
			}
		});
		
		this.add(this.loginButton);
	}
	
	public void waitLogin() {
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
