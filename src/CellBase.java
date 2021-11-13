import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to show a single Base Cell of the player
 */
public class CellBase extends JPanel implements MouseListener {
	private final Dimension cellDimension;
	private final Point cellPosition;
	private final Archeologist cellArcheologist;
	private ImageIcon cellImage;
	
	/**
	 * Creates a new instance of CellBase
	 * @param cellDimension Dimension of the Cell
	 * @param cellPosition Position of the Cell
	 * @param cellArcheologist Archeologist that is contained in this Cell
	 */
	public CellBase(Dimension cellDimension, Point cellPosition, Archeologist cellArcheologist) {
		this.cellDimension = cellDimension;
		this.cellPosition = cellPosition;
		this.cellArcheologist = cellArcheologist;
		
		this.setupCell();
		this.addMouseListener(this);
		
		try {
			InputStream stream = getClass().getResourceAsStream("grass.jpg");
			this.cellImage = new ImageIcon(ImageIO.read(stream));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		g.drawImage(cellImage.getImage(), 0, 0, this.getWidth(), this.getHeight(), null);
	}
	
	/**
	 * Function that setup the Base Cell display
	 */
	public void setupCell() {
		this.setLayout(null);
		this.setSize(this.cellDimension);
		this.setLocation(this.cellPosition);
		
		this.setOpaque(true);
		this.setVisible(true);
	}

	/**
	 * Function that draw the archeologist contained in the Base Cell
	 */
	public void drawArcheologist() {
		if(this.cellArcheologist.getPosition() == -1) {
			this.removeAll();
			
			int archeologistOffset = 4;
			
			int archeologistWidth = this.cellDimension.width / 2;
			int archeologistHeight = this.cellDimension.height / 2;
			Dimension archeologistDimension = new Dimension(archeologistWidth, archeologistHeight);
			
			int archeologistPositionX = this.cellDimension.width / 4 - (archeologistOffset + 1);
			int archeologistPositionY = this.cellDimension.height / 4 - (archeologistOffset + 1);
			Point archeologistCoord = new Point(archeologistPositionX, archeologistPositionY);
			
			this.cellArcheologist.drawArcheologist(archeologistDimension, archeologistCoord, archeologistOffset, this);
			
			this.repaint();
		}
	}
	
	/**
	 * Function used to manage the selection of a Archeologist
	 * @param e MouseEvent object to manage the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		if(this.cellArcheologist != null) {
			Player cellPlayer = this.cellArcheologist.getPlayer();
			
			if(cellPlayer.isPlayerTurn()) {
				if(!cellPlayer.isArcheologistSelected()) {
					if(cellPlayer.hasDice()) {
						Dice playerDice = cellPlayer.getDice();
						
						if(playerDice.getValue() == 6) {
							if(this.cellArcheologist.getPosition() == -1) {
								this.cellArcheologist.setArcheologistSelected();
							}
						}
					}
				}
			}
		}
	}

	@Override
	public void mousePressed(MouseEvent e) { }

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
}