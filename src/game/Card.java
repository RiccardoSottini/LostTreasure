package game;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import connection.controllers.CardController;
import connection.controllers.MessageController;

public class Card extends JPanel {
	private Launcher launcher;
	private Dimension cardDimension;
	private Point cardPosition;
	private BufferedImage cardImage;
	
	private String card_value;
	private int card_quantity;
	
	private Boolean card_selected = false;
	
	public Card(Launcher launcher, Dimension cardDimension, Point cardPosition, String card_value, int card_quantity) {
		this.launcher = launcher;
		this.cardDimension = cardDimension;
		this.cardPosition = cardPosition;
		
		this.card_value = card_value;
		this.card_quantity = card_quantity;
		
		try {
			InputStream stream = getClass().getResourceAsStream("/" + card_value + ".jpg");
			this.cardImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		this.setupCard();
	}
	
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(this.cardImage, 0, 0, this.getWidth(), this.getHeight(), null);
    }
	
	public void setupCard() {
		this.setLayout(null);
		this.setBackground(Color.decode("#d9ca63"));
		this.setSize(cardDimension);
		this.setLocation(cardPosition);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.setBorder(cellBorder);
		
		this.addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				CardController cardController = new CardController(launcher, launcher.getUserToken(), launcher.getGameToken(), card_value);
				
				if(!isSelected()) {
					launcher.removeSelectedCards();
					select();

					cardController.sendSelect();
				} else {
					unselect();
					
					cardController.sendUnselect();
				}
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
	}
	
	public String getName() {
		switch(this.card_value) {
			case "card_multiply":
				return "card x2";
			case "card_reverse":
				return "Reverse";
			case "card_skip":
				return "Skip";
			case "card_extra":
				return "+1 Die";
		}
		
		return "";
	}
	
	public String getValue() {
		return this.card_value;
	}
	
	public int getQuantity() {
		return this.card_quantity;
	}
 	
	public Boolean isSelected() {
		return this.card_selected;
	}
	
	public void select() {
		this.card_selected = true;
		
		Border cellBorder = BorderFactory.createLineBorder(Color.RED, 1);
		this.setBorder(cellBorder);
	}
	
	public void unselect() {
		this.card_selected = false;
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.setBorder(cellBorder);
	}
}
