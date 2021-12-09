package game;
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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.border.Border;

import connection.controllers.MoveController;

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
	
	private final Launcher launcher;
	private final ArrayList<Archeologist> archeologists;
	private final CellColor cellColor;
	private final CellType cellType;
	private final int cellIndex;
	private BufferedImage cellImage;
	
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
	public Cell(Launcher launcher, CellColor cellColor, CellType cellType, int cellIndex) {
		this.launcher = launcher;
		this.archeologists = new ArrayList<Archeologist>();
		this.cellColor = cellColor;
		this.cellType = cellType;
		this.cellIndex = cellIndex;
		
		this.addMouseListener(this);
		this.setupImage();
	}
	
	private void setupImage() {
		String image_name = "";
		
		if(this.cellColor == CellColor.Blue) {
    		if(cellType == CellType.Close && cellIndex == 0) {
				image_name = "street-blue-corner.jpg";
			} else if(cellType == CellType.Open && cellIndex == 0) {
				image_name = "street-blue-start.jpg";
			} else if(cellType == CellType.End) {
				image_name = "jungle-blue-center.png";
			} else {
				image_name = "street-blue.jpg";
			}
		} else if(this.cellColor == CellColor.Red) {
			if(cellType == CellType.Close && cellIndex == 0) {
				image_name = "street-red-corner.jpg";
			} else if(cellType == CellType.Open && cellIndex == 13) {
				image_name = "street-red-start.jpg";
			} else if(cellType == CellType.End) {
				image_name = "jungle-red-center.png";
			} else {
				image_name = "street-red.jpg";
			}
		} else if(this.cellColor == CellColor.Green) {
			if(cellType == CellType.Close && cellIndex == 0) {
				image_name = "street-green-corner.jpg";
			} else if(cellType == CellType.Open && cellIndex == 26) {
				image_name = "street-green-start.jpg";
			} else if(cellType == CellType.End) {
				image_name = "jungle-green-center.png";
			} else {
				image_name = "street-green.jpg";
			}
		} else if(this.cellColor == CellColor.Yellow) {
			if(cellType == CellType.Close && cellIndex == 0) {
				image_name = "street-yellow-corner.jpg";
			} else if(cellType == CellType.Open && cellIndex == 39) {
				image_name = "street-yellow-start.jpg";
			} else if(cellType == CellType.End) {
				image_name = "jungle-yellow-center.png";
			} else {
				image_name = "street-yellow.jpg";
			}
		} else if(this.cellType == CellType.Star) {
			image_name = "jungle-star.jpg";
		} else {
			Random rnd = new Random();
    		image_name = "jungle-tile" + String.valueOf(rnd.nextInt(5) + 1) + ".jpg";
		}
		
		try {
			InputStream stream = getClass().getResourceAsStream("/" + image_name);
			this.cellImage = ImageIO.read(stream);
		} catch (IOException e) {
			e.printStackTrace();
		}
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
        
        g.drawImage(cellImage, 0, 0, this.getWidth(), this.getHeight(), null);
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
	 * Function to draw the archeologists contained in this cell
	 */
	public void drawArcheologists() {
		this.removeAll();
		
		int nArcheologists = this.archeologists.size();
		
		for(int p = 0; p < nArcheologists; p++) {
			Archeologist archeologist = this.archeologists.get(p);
			int archeologistOffset;
			Dimension archeologistDimension;
			Point archeologistPosition;
			
			if(nArcheologists == 1) {
				archeologistOffset = 4;
				
				int archeologistWidth = this.cellWidth - (this.cellWidth / 4) - 2;
				int archeologistHeight = this.cellHeight - (this.cellHeight / 4) - 2;
				archeologistDimension = new Dimension(archeologistWidth, archeologistHeight);
				
				int archeologistPositionX = 0;
				int archeologistPositionY = 0;
				
				if(this.cellType == CellType.End) {
					archeologistPositionX = (int) ((cellDimension.width - archeologistWidth) / 2 - (archeologistOffset * 1.5));
					archeologistPositionY = (int) ((cellDimension.height - archeologistHeight) / 2 - (archeologistOffset * 1.5));
				}
				
				archeologistPosition = new Point(archeologistPositionX, archeologistPositionY);
				
				archeologist.drawArcheologist(archeologistDimension, archeologistPosition, archeologistOffset, this);
			} else {
				archeologistOffset = 2;
				
				int archeologistWidth = (this.cellWidth - (this.cellWidth / 4)) / 2;
				int archeologistHeight = (this.cellHeight - (this.cellWidth / 4)) / 2;
				archeologistDimension = new Dimension(archeologistWidth, archeologistHeight);
				
				int archeologistPositionX = 0;
				int archeologistPositionY = 0;
				
				if(this.cellType != CellType.End) {
					archeologistPositionX = (int) ((p % 2 == 1) ? (archeologistWidth + archeologistOffset * 1.5) : 0);
					archeologistPositionY = (int) ((p >= 2) ? (archeologistHeight + archeologistOffset * 1.5) : 0);
				} else {
					if(this.cellColor == CellColor.Blue) {
						archeologistPositionY = cellDimension.height - (archeologistHeight * 2);
					} else if(this.cellColor == CellColor.Red) {
						archeologistPositionX = archeologistWidth / 2;
					} else if(this.cellColor == CellColor.Green) {
						archeologistPositionY = archeologistHeight / 2;
					} else if(this.cellColor == CellColor.Yellow) {
						archeologistPositionX = cellDimension.width - (archeologistWidth * 2);
					}
					
					int displayOffsetX = (int) ((cellDimension.width - (archeologistWidth * nArcheologists) - ((nArcheologists - 1) * archeologistOffset)) / 2) - archeologistOffset;
					int displayOffsetY = (int) ((cellDimension.height - (archeologistHeight * nArcheologists) - ((nArcheologists - 1) * archeologistOffset)) / 2) - archeologistOffset;
					
					if(this.cellColor == CellColor.Blue || this.cellColor == CellColor.Green) {
						archeologistPositionX = (int) (displayOffsetX + (p * (archeologistWidth + archeologistOffset)));
					} else {
						archeologistPositionY = (int) (displayOffsetY + (p * (archeologistHeight + archeologistOffset)));
					}
				}
				
				archeologistPosition = new Point(archeologistPositionX, archeologistPositionY);
				
				archeologist.drawArcheologist(archeologistDimension, archeologistPosition, archeologistOffset, this);
			}
		}
		
		this.repaint();
	}
	
	/**
	 * Add a single Archeologist to this cell and draw it
	 * @param archeologist Archeologist to add
	 */
	public void addArcheologist(Archeologist archeologist) {
		this.archeologists.add(archeologist);
		this.drawArcheologists();
	}
	
	/**
	 * If a archeologist is killed by a player, it goes back to its base
	 * @param archeologist Archeologist to remove
	 * @param cellRefresh it says whether the cell has to be redrawn or not
	 */
	public void removeArcheologist(Archeologist archeologist, boolean cellRefresh) {
		for(int p = 0; p < this.archeologists.size(); p++) {
			if(archeologist == this.archeologists.get(p)) {
				this.archeologists.remove(this.archeologists.get(p));
			}
		}
		
		if(cellRefresh) {
			this.drawArcheologists();
		}
	}

	/**
	 * Returns the codes of the archeologists contained by the cell
	 */
	public ArrayList<String> getArcheologistCodes() {
		ArrayList<String> archeologistCodes = new ArrayList<String>();
		
		for(Archeologist archeologist : this.archeologists) {
			archeologistCodes.add(archeologist.getCode());
		}
		
		return archeologistCodes;
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
	 * Function used to manage the selection of a Archeologist
	 * @param e MouseEvent object to manage the mouse click
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int cell_index = this.cellIndex;
		String cell_type = (this.cellType == CellType.Open || this.cellType == CellType.Star) ? "open" : "close";
		
		MoveController moveController = new MoveController(launcher, cell_index, cell_type);
		moveController.sendMove();
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
