
package game;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.text.*;

import connection.controllers.CardController;
import connection.controllers.MessageController;

public class GameChat extends JPanel {
	private JPanel cardsPanel;
	
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
	
	private ArrayList<Card> card_list;
	private int cardIndex = 0;
	private JLabel cardLabel;
	private JLabel cardLeft;
	private JLabel cardRight;
	
	public GameChat(Launcher launcher, Dimension chatDimension, Point chatLocation) {
		this.launcher = launcher;
		this.chatDimension = chatDimension;
		this.chatLocation = chatLocation;
		
		this.card_list = new ArrayList<Card>();
		
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
		
		this.setupCards();
		this.setupPane();
		this.setupInput();
		this.setupButton();
	}
	
	public void setupCards() {
		this.cardsPanel = new JPanel();
		this.cardsPanel.setLayout(null);
		this.cardsPanel.setSize(this.getWidth() - (this.paddingX * 2), 140);
		this.cardsPanel.setBackground(Color.decode("#f7ec9c"));
		this.cardsPanel.setLocation(this.paddingX, this.paddingY + 5);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.WHITE, 1);
		this.cardsPanel.setBorder(cellBorder);
		
		this.cardLeft = new JLabel("<");
		this.cardLeft.setSize(16, 16);
		this.cardLeft.setLocation(8, this.cardsPanel.getHeight() - 20);
		this.cardLeft.setFont(new Font("Serif", Font.BOLD, 16));
		this.cardLeft.setVisible(true);
		
		this.cardLabel = new JLabel("", SwingConstants.CENTER);
		this.cardLabel.setSize(this.cardsPanel.getWidth() - 40, 16);
		this.cardLabel.setLocation(20, this.cardsPanel.getHeight() - 20);
		this.cardLabel.setFont(new Font("Serif", Font.BOLD, 12));
		this.cardLabel.setVisible(true);
		
		this.cardRight = new JLabel(">");
		this.cardRight.setSize(16, 16);
		this.cardRight.setLocation(this.cardsPanel.getWidth() - 20, this.cardsPanel.getHeight() - 20);
		this.cardRight.setFont(new Font("Serif", Font.BOLD, 16));
		this.cardRight.setVisible(true);
		
		this.cardLeft.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				moveLeft();
			}

			@Override
			public void mouseEntered(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }
		});
		
		this.cardRight.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				moveRight();
			}

			@Override
			public void mouseEntered(MouseEvent e) { }

			@Override
			public void mouseExited(MouseEvent e) { }
			
			@Override
			public void mousePressed(MouseEvent e) { }

			@Override
			public void mouseReleased(MouseEvent e) { }
		});
		
		this.cardsPanel.add(this.cardLeft);
		this.cardsPanel.add(this.cardLabel);
		this.cardsPanel.add(this.cardRight);
		
		this.add(this.cardsPanel);
	}
	
	public void setupPane() {
		this.chatPane = new JColorTextPane();
		this.chatPane.setPreferredSize(new Dimension(this.getWidth() - (this.paddingX * 2), 320));
		this.chatPane.setEditable(false);
		this.chatPane.setBackground(this.backgroundColor);
		
		DefaultCaret caret = (DefaultCaret) this.chatPane.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
		this.scrollPane = new JScrollPane(this.chatPane);
		this.scrollPane.setSize(new Dimension(this.getWidth() - (this.paddingX * 2), 320));
		this.scrollPane.setLocation(this.paddingX, 150 + this.paddingY);
		this.scrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		this.scrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
				
		this.add(this.scrollPane);
	}
	
	public void setupInput() {
		this.inputBox = new JTextPane();
		this.inputBox.setForeground(Color.white);
		this.inputBox.setBackground(this.backgroundColor);
		
		this.inputPane = new JScrollPane(this.inputBox);
		this.inputPane.setSize(new Dimension(this.getWidth() - (this.paddingX * 2), 80));
		this.inputPane.setLocation(this.paddingX, 150 + this.scrollPane.getHeight() + this.paddingY + 10);
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
		this.sendButton.setLocation(this.paddingX, 150 + this.scrollPane.getHeight() + this.inputPane.getHeight() + this.paddingY + 22);
		
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
	
	public void addMessage(int user_index, String user_name, String user_message) {
		this.chatPane.append(Color.white, user_name + ": " + user_message);
	}
	
	public void updateCards(LinkedHashMap<String, Integer> user_cards) {
		this.removeSelectedCards();
		this.unselectCards();
		
		for(Card card : this.card_list) {
			this.cardsPanel.remove(card);
		}
		
		this.cardsPanel.repaint();
		
		if(this.cardIndex >= user_cards.size() && user_cards.size() > 0) {
			this.cardIndex = user_cards.size() - 1;
		}
		
		this.card_list = new ArrayList<Card>();
		
		int cardPadding = 4;
		int cardWidth = this.cardsPanel.getWidth() - (cardPadding * 2);
		int cardHeight = this.cardsPanel.getHeight() - (cardPadding * 2) - 20;
		Dimension cardDimension = new Dimension(cardWidth, cardHeight);
		
		System.out.println(user_cards);
		
		int index = 0;
		for(String card_value : user_cards.keySet()) {
			Integer card_quantity = user_cards.get(card_value);
			Card cardPanel = new Card(this.launcher, cardDimension, new Point(cardPadding, cardPadding), card_value, card_quantity);
			
			if(this.cardIndex == index) {
				this.cardsPanel.add(cardPanel);
			}
			
			this.card_list.add(cardPanel);
			index++;
		}
		
		this.toggleCardControl();
		
		this.cardsPanel.repaint();
	}
	
	public Card getCurrentCard() {
		return this.card_list.get(this.cardIndex);
	}
	
	public void unselectCards() {
		for(Card card : this.card_list) {
			if(card.isSelected()) {
				CardController cardController = new CardController(launcher, launcher.getUserToken(), launcher.getGameToken(), card.getValue());
				cardController.sendUnselect();
				
				card.unselect();
			}
		}
	}
	
	public void moveLeft() {
		this.cardsPanel.remove(this.getCurrentCard());
		
		this.cardIndex--;
		this.toggleCardControl();
		this.unselectCards();

		this.cardsPanel.add(this.getCurrentCard());
		this.cardsPanel.repaint();
	}
	
	public void moveRight() {
		this.cardsPanel.remove(this.getCurrentCard());
		
		this.cardIndex++;
		this.toggleCardControl();
		this.unselectCards();

		this.cardsPanel.add(this.getCurrentCard());
		this.cardsPanel.repaint();
	}
 	
	public void toggleCardControl() {
		this.cardLeft.setVisible(this.card_list.size() > 1 && this.cardIndex > 0);
		this.cardRight.setVisible(this.card_list.size() > 1 && this.cardIndex < this.card_list.size() - 1);
		
		if(this.card_list.size() > 0) {
			this.cardLabel.setText(this.getCurrentCard().getName() + " (" + this.getCurrentCard().getQuantity() + ")");
		} 

		this.cardLabel.setVisible(this.card_list.size() > 0);
	}
	
	public void removeSelectedCards() {
		for(Card cardPanel : this.card_list) {
			if(cardPanel.isSelected()) {
				cardPanel.unselect();
			}
		}
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