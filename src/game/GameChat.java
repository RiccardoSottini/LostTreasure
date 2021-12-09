package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.Border;
import javax.swing.text.*;

import connection.controllers.MessageController;

public class GameChat extends JPanel {
	private JColorTextPane chatPane;
	private JScrollPane scrollPane;
	private JButton sendButton;
	private JTextPane inputBox;
	private JScrollPane inputPane;
	private BufferedImage backgroundImage;
	private Color backgroundColor = Color.black;
	
	private Dimension chatDimension;
	private Point chatLocation;
	
	private final int paddingX = 32;
	private final int paddingY = 30;
	
	private Launcher launcher;
	
	public GameChat(Launcher launcher, Dimension chatDimension, Point chatLocation) {
		this.launcher = launcher;
		this.chatDimension = chatDimension;
		this.chatLocation = chatLocation;
		
		try {
			InputStream stream = getClass().getResourceAsStream("/background-chat.png");
			this.backgroundImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		this.setupChat();
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(this.backgroundImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
	
	public int getWidth() {
		return this.chatDimension.width;
	}
	
	public int getHeight() {
		return this.chatDimension.height;
	}

	private void setupChat() {	
		this.setLayout(null);
		this.setSize(this.chatDimension);
		this.setLocation(this.chatLocation);
		
		this.setupPane();
		this.setupInput();
		this.setupButton();
	}
	
	public void setupPane() {
		this.chatPane = new JColorTextPane();
		this.chatPane.setPreferredSize(new Dimension(this.getWidth() - (this.paddingX * 2), 470));
		this.chatPane.setEditable(false);
		this.chatPane.setBackground(this.backgroundColor);
		
		DefaultCaret caret = (DefaultCaret) this.chatPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		this.scrollPane = new JScrollPane(this.chatPane);
		this.scrollPane.setSize(new Dimension(this.getWidth() - (this.paddingX * 2), 470));
		this.scrollPane.setLocation(this.paddingX, this.paddingY);
		this.scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				
		this.add(this.scrollPane);
	}
	
	public void setupInput() {
		this.inputBox = new JTextPane();
		this.inputBox.setBackground(Color.gray);
		this.inputBox.setForeground(Color.white);
		
		this.inputPane = new JScrollPane(this.inputBox);
		this.inputPane.setSize(new Dimension(this.getWidth() - (this.paddingX * 2), 80));
		this.inputPane.setLocation(this.paddingX, this.scrollPane.getHeight() + this.paddingY + 10);
		this.inputPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.inputPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		this.add(this.inputPane);
	}
	
	public void setupButton() {
		this.sendButton = new JButton("SEND");
		this.sendButton.setSize(this.getWidth() - (this.paddingX * 2), 40);
		this.sendButton.setFont(new Font("Arial", Font.BOLD, 18));
		this.sendButton.setForeground(Color.black);
		this.sendButton.setBackground(Color.decode("#f7ec9c"));
		this.sendButton.setHorizontalAlignment(JFormattedTextField.CENTER);
		this.sendButton.setLocation(this.paddingX, this.scrollPane.getHeight() + this.inputPane.getHeight() + this.paddingY + 22);
		
		this.sendButton.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(!inputBox.getText().equals("")) {
					MessageController messageController = new MessageController(launcher, inputBox.getText());
					messageController.sendMessage();
				}
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
				sendButton.setBorder(cellBorder);
				sendButton.setBackground(Color.decode("#d9ca63"));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				sendButton.setBackground(Color.decode("#f7ec9c"));
			}
			
			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }
		});
		
		this.add(this.sendButton);
	}
	
	public void addMessage(int user_index, String user_message) {
		this.chatPane.append(Color.white, user_index + ": " + user_message);
	}
}

class JColorTextPane extends JEditorPane {
	public void append(Color color, String message) {
		this.setForeground(color);
		
		if(getText().equals("")) {
			this.setText(this.getText() + message);
		} else {
			this.setText(this.getText() + "\n" + message);
		}
	}
}