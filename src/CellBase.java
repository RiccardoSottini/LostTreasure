import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to show a single Base Cell of the player
 */
public class CellBase extends JPanel implements MouseListener {
	private final Dimension cellDimension;
	private final Point cellPosition;
	private final Pawn cellPawn;
	
	/**
	 * Creates a new instance of CellBase
	 * @param cellDimension Dimension of the Cell
	 * @param cellPosition Position of the Cell
	 * @param cellPawn Pawn that is contained in this Cell
	 */
	public CellBase(Dimension cellDimension, Point cellPosition, Pawn cellPawn) {
		this.cellDimension = cellDimension;
		this.cellPosition = cellPosition;
		this.cellPawn = cellPawn;
		
		this.setupCell();
		this.addMouseListener(this);
	}
	
	/**
	 * Function that setup the Base Cell display
	 */
	public void setupCell() {
		this.setLayout(null);
		this.setSize(this.cellDimension);
		this.setLocation(this.cellPosition);
		
		Border cellBorder = BorderFactory.createLineBorder(Color.BLACK, 1);
		this.setBorder(cellBorder);
		
		this.setBackground(Color.decode("#F5F5F5"));
		this.setOpaque(true);
		this.setVisible(true);
	}

	/**
	 * Function that draw the pawn contained in the Base Cell
	 */
	public void drawPawn() {
		if(this.cellPawn.getPosition() == -1) {
			this.removeAll();
			
			int pawnOffset = 4;
			
			int pawnWidth = this.cellDimension.width / 2;
			int pawnHeight = this.cellDimension.height / 2;
			Dimension pawnDimension = new Dimension(pawnWidth, pawnHeight);
			
			int pawnPositionX = this.cellDimension.width / 4 - (pawnOffset + 1);
			int pawnPositionY = this.cellDimension.height / 4 - (pawnOffset + 1);
			Point pawnCoord = new Point(pawnPositionX, pawnPositionX);
			
			this.cellPawn.drawPawn(pawnDimension, pawnCoord, pawnOffset, this);
			
			this.repaint();
		}
	}
	
	/**
	 * Function used to manage the selection of a Pawn
	 * @param e MouseEvent object to manage the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		Player cellPlayer = this.cellPawn.getPlayer();
		
		if(cellPlayer.isPlayerTurn()) {
			if(!cellPlayer.isPawnSelected()) {
				if(cellPlayer.hasDice()) {
					Dice playerDice = cellPlayer.getDice();
					
					if(playerDice.getValue() == 6) {
						if(this.cellPawn.getPosition() == -1) {
							this.cellPawn.setPawnSelected();
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