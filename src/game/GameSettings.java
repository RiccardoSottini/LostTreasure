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
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import connection.controllers.LoginController;
import connection.controllers.PasswordController;
import connection.controllers.UsernameController;

public class GameSettings extends JPanel {
	private final Launcher launcher;
	private final Dimension panelDimension;
	private final GameDashboard gameDashboard;
	private final int menuHeight;
	
	private JLabel titleLabel;
	
	private JPanel usernamePanel;
	private JLabel usernameLabel;
	private JTextField usernameField;

	private JPanel passwordPanel;
	private JLabel passwordLabel;
	private JPasswordField passwordField;
	
	private JPanel passwordPanel_confirm;
	private JLabel passwordLabel_confirm;
	private JPasswordField passwordField_confirm;
	
	private JButton changeUsernameButton;
	private JButton changePasswordButton;

	private BufferedImage backgroundImage;	
	
	private String username;
	
	public GameSettings(Launcher launcher, GameDashboard gameDashboard, Dimension panelDimension, int menuHeight) {
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
		
		this.setupSettings();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);
	}
	
	private void setupSettings() {
		this.setSize(new Dimension(this.panelDimension.width, this.panelDimension.height - this.menuHeight));
		this.setLocation(0, this.menuHeight);
		this.setLayout(null);
		
		this.setupTitle();

		this.setupUsernameField();
        this.setupChangeButtonUsername();
		
        this.setupPasswordField();
		this.setupConfirmPasswordField();
		this.setupPasswordButton();
		
		this.setVisible(true);
	}
	
	private void setupTitle() {
		this.titleLabel = new JLabel("Settings Page");
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
		this.usernamePanel.setSize(new Dimension(this.panelDimension.width / 2 + 50, 40));
		this.usernamePanel.setLocation((this.panelDimension.width - this.usernamePanel.getWidth()) / 2, 100);
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
		this.usernameField.setBorder(
			BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.black),
			BorderFactory.createEmptyBorder(3, 3, 3, 3))
		);
		
		this.usernamePanel.add(this.usernameLabel);
		this.usernamePanel.add(this.usernameField);
		this.add(this.usernamePanel);
	}
	
	public void setupChangeButtonUsername() {
		this.changeUsernameButton = new JButton("Change Username");
		this.changeUsernameButton.setSize(new Dimension(this.panelDimension.width / 3, 40));
		this.changeUsernameButton.setLocation((this.panelDimension.width - this.changeUsernameButton.getWidth()) / 2, 150 );
		
		this.changeUsernameButton.setFont(new Font("Arial", Font.BOLD, 18));
		this.changeUsernameButton.setForeground(Color.BLACK);
		this.changeUsernameButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		this.changeUsernameButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		this.changeUsernameButton.setBackground(Color.decode("#f7ec9c"));
		this.changeUsernameButton.setFocusPainted(false);
		this.changeUsernameButton.setVisible(true);
		
		this.changeUsernameButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String username = usernameField.getText();
				
				UsernameController usernameController = new UsernameController(launcher, username);
				if(usernameController.sendUsername()) {
					launcher.setUsername(usernameController.getUsername());
					gameDashboard.runHome();
				} else {
					System.out.println("USERNAME ERROR");
					System.out.println("ERROR: " + usernameController.getError());
				}
			}
		});
		
		this.add(this.changeUsernameButton);
	}
	
	private void setupPasswordField() {
		this.passwordPanel = new JPanel();
		this.passwordPanel.setSize(new Dimension(this.panelDimension.width /2 + 50  , 40));
		this.passwordPanel.setLocation((this.panelDimension.width - this.passwordPanel.getWidth()) / 2, 225);
		this.passwordPanel.setBackground(Color.decode("#f7ec9c"));
		this.passwordPanel.setBorder(BorderFactory.createLineBorder(Color.black));
		this.passwordPanel.setLayout(null);
		
		this.passwordLabel = new JLabel("New Password:");
		this.passwordLabel.setSize(new Dimension(this.passwordPanel.getWidth() / 4, 40));
		this.passwordLabel.setLocation(10, 0);
		this.passwordLabel.setForeground(Color.black);
		
		this.passwordField = new JPasswordField();
		this.passwordField.setSize(new Dimension(this.passwordPanel.getWidth() / 4 * 3 - 10, 25));
		this.passwordField.setLocation(this.passwordPanel.getWidth() / 4, (this.passwordPanel.getHeight() - this.passwordField.getHeight()) / 2);
		this.passwordField.setBackground(Color.LIGHT_GRAY);
		this.passwordField.setBorder(
			BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.black),
			BorderFactory.createEmptyBorder(3, 3, 3, 3))
		);
		
		this.passwordPanel.add(this.passwordLabel);
		this.passwordPanel.add(this.passwordField);
		
		this.add(this.passwordPanel);
	}
	
	private void setupConfirmPasswordField() {
		this.passwordPanel_confirm = new JPanel();
		this.passwordPanel_confirm.setSize(new Dimension(this.panelDimension.width /2 + 50  , 40));
		this.passwordPanel_confirm.setLocation((this.panelDimension.width - this.passwordPanel.getWidth()) / 2, 270);
		this.passwordPanel_confirm.setBackground(Color.decode("#f7ec9c"));
		this.passwordPanel_confirm.setBorder(BorderFactory.createLineBorder(Color.black));
		this.passwordPanel_confirm.setLayout(null);
		
		this.passwordLabel_confirm = new JLabel("Confirm Password:");
		this.passwordLabel_confirm.setSize(new Dimension(this.passwordPanel_confirm.getWidth() / 4, 40));
		this.passwordLabel_confirm.setLocation(10, 0);
		this.passwordLabel_confirm.setForeground(Color.black);
		
		this.passwordField_confirm = new JPasswordField();
		this.passwordField_confirm.setSize(new Dimension(this.passwordPanel_confirm.getWidth() / 4 * 3 - 10, 25));
		this.passwordField_confirm.setLocation(this.passwordPanel_confirm.getWidth() / 4, (this.passwordPanel_confirm.getHeight() - this.passwordField_confirm.getHeight()) / 2);
		this.passwordField_confirm.setBackground(Color.LIGHT_GRAY);
		this.passwordField_confirm.setBorder(
			BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(Color.black),
			BorderFactory.createEmptyBorder(3, 3, 3, 3))
		);
		
		this.passwordPanel_confirm.add(this.passwordLabel_confirm);
		this.passwordPanel_confirm.add(this.passwordField_confirm);
		
		this.add(this.passwordPanel_confirm);
	}
	
	public void setupPasswordButton() {
		this.changePasswordButton = new JButton("Change Password");
		this.changePasswordButton.setSize(new Dimension(this.panelDimension.width / 3, 40));
		this.changePasswordButton.setLocation((this.panelDimension.width - this.changePasswordButton.getWidth()) / 2, 320 );
		
		this.changePasswordButton.setFont(new Font("Arial", Font.BOLD, 18));
		this.changePasswordButton.setForeground(Color.BLACK);
		this.changePasswordButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		
		this.changePasswordButton.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
		this.changePasswordButton.setBackground(Color.decode("#f7ec9c"));
		this.changePasswordButton.setFocusPainted(false);
		this.changePasswordButton.setVisible(true);
		
		this.changePasswordButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String password = passwordField.getText();
				String password_confirm = passwordField_confirm.getText();
				
				if(!password.isEmpty() && password.equals(password_confirm)) {
					PasswordController passwordController = new PasswordController(launcher, password);
					if(passwordController.sendPassword()) {
						gameDashboard.runHome();
					} else {
						System.out.println("PASSWORD ERROR");
						System.out.println("ERROR: " + passwordController.getError());
					}
				}
			}
		});
		
		this.add(this.changePasswordButton);
	}
	
	public void setUsername(String username) {
		this.usernameField.setText(username);
	}
}