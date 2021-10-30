import java.awt.BasicStroke;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 * Class that is used to show a single Cell for the Game
 */
public class Cell extends JPanel implements MouseListener  {
	
	/**
	 * The 4 colours that are used to give each player a different colour.
	 */
	private final Color[] colors = {
		Color.decode("#65CDD1"),
		Color.decode("#EE6E6E"),
		Color.decode("#89C66C"),
		Color.decode("#E8E557"),
		Color.WHITE
	};
	
	private final char[] playerCodes = {'B', 'R', 'G', 'Y'};
	
	private final Color borderColor = Color.BLACK;
	
	private final double[][][] endPositions = {
		{ {0.0, 1.0}, {1.0, 1.0}, {0.5, 0.0} },
		{ {0.0, 0.0}, {0.0, 1.0}, {1.0, 0.5} },
		{ {0.0, 0.0}, {1.0, 0.0}, {0.5, 1.0} },
		{ {1.0, 0.0}, {1.0, 1.0}, {0.0, 0.5} }
	};
	
	private final ArrayList<Pawn> pawns;
	private final CellColor cellColor;
	private final CellType cellType;
	
	private Dimension cellDimension;
	private Point cellPosition;
	private int cellWidth = 46;
	private int cellHeight = 46;
	
	private JPanel boardPanel;
	
	/**
	 * Creates a new instance of Cell
	 * @param cellColor Color of the Cell
	 * @param cellType Type of the Cell
	 */
	public Cell(CellColor cellColor, CellType cellType) {
		this.pawns = new ArrayList<Pawn>();
		this.cellColor = cellColor;
		this.cellType = cellType;
		
		this.addMouseListener(this);
	}
	
	/**
	 * Function to create the 4 triangles which are the winning point 
	 * @return the polygon (triangle)
	 */
	private Shape createTriangle() {
        Polygon polygon = new Polygon();
        int index = cellColor.ordinal();
        
        for(int p = 0; p < 3; p++) {
        	int positionX = (int) (cellDimension.width * endPositions[index][p][0]);
        	int positionY = (int) (cellDimension.height * endPositions[index][p][1]);
        	
        	polygon.addPoint(positionX, positionY);
        }
        
        return polygon;
    }
	
	/**
	 * Function that is used to draw the Cell on the Frame
	 * @param g Graphics component used by Java to draw the Panel
	 */
	@Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        if(this.cellType == CellType.End) {
        	Graphics2D graphics2D = (Graphics2D) g;
        	Shape triangleShape = this.createTriangle();
        	
        	graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        	graphics2D.setColor(colors[cellColor.ordinal()]);
        	graphics2D.fill(triangleShape);
        	
            int index = cellColor.ordinal();
            
        	for(int line = 0; line < 3; line++) {
        		int nextLine = (line + 1) % 3;
        		
        		int x1 = (int) (cellDimension.width * endPositions[index][line][0]);
        		int y1 = (int) (cellDimension.height * endPositions[index][line][1]);
        		int x2 = (int) (cellDimension.width * endPositions[index][nextLine][0]);
        		int y2 = (int) (cellDimension.height * endPositions[index][nextLine][1]);
        		
                graphics2D.setColor(this.borderColor);
                graphics2D.setStroke(new BasicStroke(2));
        		graphics2D.drawLine(x1, y1, x2, y2);
        	}
        } else if(this.cellType == CellType.Star) {
        	Graphics2D graphics2D = (Graphics2D) g;
        	
        	graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        	
        	final int centerX = this.getWidth() / 2;
        	final int centerY = (this.getHeight() / 2) + 2;
        	
        	final int nSpikes = 5;
        	final int nPoints = (nSpikes * 2) + 1;
        	
        	final double SPIKINESS = 0.5;
        	final int RADIUS = (this.getWidth() / 2) - 2;
        	
            int xPoint[] = new int[nPoints];
            int yPoint[] = new int[nPoints];
            
            for (int p = 0; p < nPoints; p++) {
                double iRadius = (p % 2 == 0) ? RADIUS : (RADIUS * SPIKINESS);
                double angle = (p * 360.0) / (2 * nSpikes);

                xPoint[p] = (int) (centerX + iRadius * Math.cos(Math.toRadians(angle - 90)));
                yPoint[p] = (int) (centerY + iRadius * Math.sin(Math.toRadians(angle - 90)));
            }

            graphics2D.setStroke(new BasicStroke(2.f));
            graphics2D.setColor(Color.black);
            graphics2D.drawPolyline(xPoint, yPoint, nPoints);
        }
    }
	
	/**
	 * Creates a cell and gives it a dimension and position
	 * @param cellDimension Size of the cell
	 * @param cellPosition Coordinates of the cell
	 * @param boardPanel Panel used to display the cells
	 */
	public void setupCell(Dimension cellDimension, Point cellPosition, JPanel boardPanel) {
		this.cellWidth = cellDimension.width;
		this.cellHeight = cellDimension.height;
		
		this.cellDimension = cellDimension;
		this.cellPosition = cellPosition;
		this.boardPanel = boardPanel;
		
		if(this.cellType == CellType.End) {
			int cellPositionX, cellPositionY;
			int cellWidth, cellHeight;
			
			if(this.cellColor == CellColor.Blue || this.cellColor == CellColor.Green) {
				cellPositionX = cellPosition.x - cellDimension.width;
				cellPositionY = (this.cellColor == CellColor.Blue) ? cellPosition.y - cellDimension.height / 2 : cellPosition.y;
				
				cellWidth = cellDimension.width * 3;
				cellHeight = (int) (cellDimension.height * 1.5);
			} else {
				cellPositionX = (this.cellColor == CellColor.Yellow) ? cellPosition.x - cellDimension.width / 2 : cellPosition.x;
				cellPositionY = cellPosition.y - cellDimension.height;
				
				cellWidth = (int) (cellDimension.width * 1.5);
				cellHeight = (int) (cellDimension.height * 3);
			}
			
			this.cellDimension = new Dimension(cellWidth, cellHeight);
			this.cellPosition = new Point(cellPositionX, cellPositionY);
			
			this.setOpaque(false);
		} else {
			Border cellBorder = BorderFactory.createLineBorder(this.borderColor, 1);
			this.setBorder(cellBorder);
			
			this.setBackground(colors[cellColor.ordinal()]);
			this.setOpaque(true);
		}
		
		this.setLayout(null);
		this.setSize(this.cellDimension);
		this.setLocation(this.cellPosition);
		this.setVisible(true);
		
		boardPanel.add(this);
	}
	
	/**
	 * Function to draw the pawns contained in this cell
	 */
	public void drawPawns() {
		this.removeAll();
		
		int nPawns = this.pawns.size();
		
		for(int p = 0; p < nPawns; p++) {
			Pawn pawn = this.pawns.get(p);
			int pawnOffset;
			Dimension pawnDimension;
			Point pawnPosition;
			
			if(nPawns == 1) {
				pawnOffset = 4;
				
				int pawnWidth = this.cellWidth - (this.cellWidth / 4) - 2;
				int pawnHeight = this.cellHeight - (this.cellHeight / 4) - 2;
				pawnDimension = new Dimension(pawnWidth, pawnHeight);
				
				int pawnPositionX = 0;
				int pawnPositionY = 0;
				
				if(this.cellType == CellType.End) {
					pawnPositionX = (int) ((cellDimension.width - pawnWidth) / 2 - (pawnOffset * 1.5));
					pawnPositionY = (int) ((cellDimension.height - pawnHeight) / 2 - (pawnOffset * 1.5));
				}
				
				pawnPosition = new Point(pawnPositionX, pawnPositionY);
				
				pawn.drawPawn(pawnDimension, pawnPosition, pawnOffset, this);
			} else {
				pawnOffset = 2;
				
				int pawnWidth = (this.cellWidth - (this.cellWidth / 4)) / 2;
				int pawnHeight = (this.cellHeight - (this.cellWidth / 4)) / 2;
				pawnDimension = new Dimension(pawnWidth, pawnHeight);
				
				int pawnPositionX = 0;
				int pawnPositionY = 0;
				
				if(this.cellType != CellType.End) {
					pawnPositionX = (int) ((p % 2 == 1) ? (pawnWidth + pawnOffset * 1.5) : 0);
					pawnPositionY = (int) ((p >= 2) ? (pawnHeight + pawnOffset * 1.5) : 0);
				} else {
					if(this.cellColor == CellColor.Blue) {
						pawnPositionY = cellDimension.height - (pawnHeight * 2);
					} else if(this.cellColor == CellColor.Red) {
						pawnPositionX = pawnWidth / 2;
					} else if(this.cellColor == CellColor.Green) {
						pawnPositionY = pawnHeight / 2;
					} else if(this.cellColor == CellColor.Yellow) {
						pawnPositionX = cellDimension.width - (pawnWidth * 2);
					}
					
					int displayOffsetX = (int) ((cellDimension.width - (pawnWidth * nPawns) - ((nPawns - 1) * pawnOffset)) / 2) - pawnOffset;
					int displayOffsetY = (int) ((cellDimension.height - (pawnHeight * nPawns) - ((nPawns - 1) * pawnOffset)) / 2) - pawnOffset;
					
					if(this.cellColor == CellColor.Blue || this.cellColor == CellColor.Green) {
						pawnPositionX = (int) (displayOffsetX + (p * (pawnWidth + pawnOffset)));
					} else {
						pawnPositionY = (int) (displayOffsetY + (p * (pawnHeight + pawnOffset)));
					}
				}
				
				pawnPosition = new Point(pawnPositionX, pawnPositionY);
				
				pawn.drawPawn(pawnDimension, pawnPosition, pawnOffset, this);
			}
		}
		
		this.repaint();
	}
	
	/**
	 * Add a single Pawn to this cell and draw it
	 * @param pawn Pawn to add
	 */
	public void addPawn(Pawn pawn) {
		this.pawns.add(pawn);
		this.drawPawns();
	}
	
	/**
	 * If a pawn is killed by a player, it goes back to its base
	 * @param pawn Pawn to remove
	 * @param cellRefresh it says whether the cell has to be redrawn or not
	 */
	public void removePawn(Pawn pawn, boolean cellRefresh) {
		for(int p = 0; p < this.pawns.size(); p++) {
			if(pawn == this.pawns.get(p)) {
				this.pawns.remove(this.pawns.get(p));
			}
		}
		
		if(cellRefresh) {
			this.drawPawns();
		}
	}
	
	/**
	 * Select a single pawn contained in this cell of the Player who is currently playing
	 */
	public void selectPawn() {
		for(Pawn pawn : this.pawns) {
			Player playerPawn = pawn.getPlayer();
			
			if(playerPawn.isPlayerTurn()) {
				playerPawn.setPawnSelected(pawn);
				return;
			}
		}
	}
	
	/**
	 * Function that allows the player to kill other Players' pawns
	 * @param selectedPawn The Pawn selected by the Player
	 * @return hasKilled It says whether a pawn was killed or not
	 */
	public boolean killPawns(Pawn selectedPawn) {
		boolean hasKilled = false;
		
		if(this.canKill()) {
			int[] playerPawns = new int[4];
			
			for(int playerIndex = 0; playerIndex < this.playerCodes.length; playerIndex++) {
				playerPawns[playerIndex] = 0;
				
				for(Pawn comparedPawn : this.pawns) {
					if(comparedPawn.getPlayerCode() == playerCodes[playerIndex]) {
						playerPawns[playerIndex]++;
					}
				}
			}
			
			boolean playerKill = true;
			
			for(int playerIndex = 0; playerIndex < playerPawns.length; playerIndex++) {
				if(playerPawns[playerIndex] > 1) {
					playerKill = false;
					break;
				}
			}
			
			if(playerKill) {
				for(int p = 0; p < this.pawns.size(); p++) {
					Pawn comparedPawn = this.pawns.get(p);
					
					if(comparedPawn.getPlayerCode() != selectedPawn.getPlayerCode()) {
						comparedPawn.setDead();
						this.removePawn(comparedPawn, false);
						
						hasKilled = true;
					}
				}
			

				this.drawPawns();
			}
		}
		
		return hasKilled;
	}
	
	/**
	 * Returns the pawns contained by the cell
	 */
	public ArrayList<Pawn> getPawns() {
		return this.pawns;
	}

	/**
	 * Returns the codes of the pawns contained by the cell
	 */
	public ArrayList<String> getPawnCodes() {
		ArrayList<String> pawnCodes = new ArrayList<String>();
		
		for(Pawn pawn : this.pawns) {
			pawnCodes.add(pawn.getCode());
		}
		
		return pawnCodes;
	}
	
	/**
	 * Function that returns whether a player can kill on this cell
	 * @return returns whether a player can kill on this cell
	 */
	public boolean canKill() {
		return cellType == CellType.Open && cellColor == CellColor.White;
	}
	
	/**
	 * Function that returns the color of this cell
	 * @return returns the color of this cell
	 */
	public CellColor getColor() {
		return this.cellColor;
	}
	
	/**
	 * Function that returns the type of this cell
	 * @return returns the type of this cell
	 */
	public CellType getType() {
		return this.cellType;
	}

	/**
	 * Function used to manage the selection of a Pawn
	 * @param e MouseEvent object to manage the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		selectPawn();
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
