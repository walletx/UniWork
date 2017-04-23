import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.Timer;

/**
 * The BoardPanel class extends JPanel to display the board from a
 * supplied BoardView.
 */
public class BoardPanel extends JPanel implements ActionListener {
	private GUISystem graphics;
	private Game game;
	
	private BoardView bv;
	private Dimension boardDim;
	private int sideMargin;
	private int topMargin;
	private int cellMargin;
	private int cellSize;
	private Color bg;
	
	private ArrayList<JLabel> columns;
	private int highlightCol;
	private BoardListener bl;
	
	private ArrayList<JLabel> playerColumns;
	
	private JLabel turnLabel;
	
	private JLabel timerLabel;
	private GameTimer timer;
	
	private JLabel menu;
	private JLabel reset;
	private JLabel quit;
	
	private boolean gameOver;

	/**
	 * Constructs a new BoardPanel tied to the supplied BoardView.
	 * @param bv BoardView to display.
	 */
	public BoardPanel(Game game, BoardView bv, GUISystem graphics) {
		this.game = game;
		this.graphics = graphics;
		this.bv = bv;
		
		this.gameOver = false;
		
		
		boardDim = new Dimension();
		
		setDimVars();


		bg = new Color(104, 172, 139);
		setBackground(bg);
		
		columns = new ArrayList<JLabel>(bv.getCols());
		this.setLayout(null);
		//Add buttons to go to Menu, Reset and Quit
		addButtons();
		setButtons();
		
		// Set up the columns so they can be clicked.
		bl = new BoardListener(this, columns);
		for (int i = 0; i < bv.getCols(); i++) {
			JLabel col = new JLabel();
			col.addMouseListener(bl);
			columns.add(col);

			this.add(col);
		}
		
		setColumns();
		
		highlightCol = -1;
		
		playerColumns = new ArrayList<JLabel>();
		
		Iterator<Player> it = game.getPlayers().iterator();
		while (it.hasNext()) {
			Player p = it.next();
			JLabel l = new JLabel(p.getName());

			l.setHorizontalAlignment(SwingConstants.CENTER);
			l.setVerticalAlignment(SwingConstants.TOP);
			
			playerColumns.add(l);
			this.add(l);
		}
		
		setPlayerColumns();
		
		turnLabel = new JLabel("Turn 1");

		turnLabel.setHorizontalAlignment(SwingConstants.CENTER);
		turnLabel.setVerticalAlignment(SwingConstants.CENTER);

		turnLabel.setFont(new Font("Impact", Font.BOLD, 40));
		turnLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		
		setTurnLabel();

		this.add(turnLabel);
		
		timerLabel = new JLabel("");
		timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
		timerLabel.setVerticalAlignment(SwingConstants.CENTER);

		timerLabel.setFont(new Font("Impact", Font.BOLD, 40));
		//timerLabel.setText("<html>00:00:00<font size='5'>.00</font>");
		timerLabel.setBorder(BorderFactory.createLineBorder(Color.black, 3));

		setTimerLabel();
		
		timer = new GameTimer(timerLabel);
		timer.start();
		new Timer(1, timer).start();

		this.add(timerLabel);
		
		// TODO: Should a Dimension object even be used in this way?
		// dim = new Dimension(bv.getCols() * cellSize + margin * 2 ,
		//		            bv.getRows() * cellSize + margin * 2);

		// Should this be here?
		setBorder(BorderFactory.createLineBorder(Color.black));
		
		// TODO: Might want to remove the following line?
		repaint();
	}
	
	/**
	 * Paint the board and the discs inside.
	 */
	public void paintComponent(Graphics g) {
		setDimVars();
		
		setColumns();
		setPlayerColumns();
		setButtons();
		setTurnLabel();
		setTimerLabel();

		super.paintComponent(g);
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			                RenderingHints.VALUE_ANTIALIAS_ON);
		
		// Check if the timer needs stopping. Check if highlighting
		// needs to stop.
		if (game.getWinner() != null || game.isDraw()) {
			timer.stop();
			highlightCol = -1;
		}

		// Should make a numPlayers in game and change to that.
		// Player columns.
		for (int i = 0; i < 2; i++) {
			JLabel l = playerColumns.get(i);
			switch (game.getPlayers().get(i).getDisc().getColour()) {
			case YELLOW:
				g2.setColor(Color.YELLOW);
				fillDiscUnderName(g2, i, l);
				break;
				
			case RED:
				g2.setColor(Color.RED);
				fillDiscUnderName(g2, i, l);
				break;
			
			case PIKA:
				Image img1 = new ImageIcon("pikachu.jpg").getImage();
				drawImageUnderName(g, img1, i, l);
				break;
				
			case DOGE:
				Image img2 = new ImageIcon("doge.jpg").getImage();
				drawImageUnderName(g, img2, i, l);
				break;
				
			case GOOGLE:
				Image img3 = new ImageIcon("google.jpg").getImage();
				drawImageUnderName(g, img3, i, l);
				break;
				
			case FACEBOOK:
				Image img4 = new ImageIcon("facebook.jpg").getImage();
				drawImageUnderName(g, img4, i, l);
				break;
				
			case ANDROID:
				Image img5 = new ImageIcon("android.jpg").getImage();
				drawImageUnderName(g, img5, i, l);
				break;
				
			case APPLE:
				Image img6 = new ImageIcon("apple.jpg").getImage();
				drawImageUnderName(g, img6, i, l);
				break;
			
			case BAT:
				Image img7 = new ImageIcon("batman.jpg").getImage();
				drawImageUnderName(g, img7, i, l);
				break;
				
			case IRON:
				Image img8 = new ImageIcon("ironman.jpg").getImage();
				drawImageUnderName(g, img8, i, l);
				break;
				
			case SPIDER:
				Image img9 = new ImageIcon("spiderman.jpg").getImage();
				drawImageUnderName(g, img9, i, l);
				break;
				
			case SUPER:
				Image img10 = new ImageIcon("superman.jpg").getImage();
				drawImageUnderName(g, img10, i, l);
				break;
			}

			g2.setColor(Color.BLACK);
			g2.setStroke(new BasicStroke(4));
			g2.drawOval(getRightX() + sideMargin + l.getSize().width / 2 +
					    i * (l.getSize().width + sideMargin) - 
					    (cellSize - cellMargin * 2) / 2,
					    getTopY() + l.getSize().height / 2 -
					    (cellSize - cellMargin * 2) / 2,
					    cellSize - cellMargin * 2, cellSize - cellMargin * 2);
		}
		
		// Increment the turn.
		if (game.isDraw()) {
			// Not + 1 since game increments turn number on draw.
			turnLabel.setText("<html><center> Draw!<br>" +
					          (int)(game.getNumTurn())
					          + " turns</center></html>");
			
		} else if (game.getWinner() != null) {
			gameOver = true;
			turnLabel.setText("<html><center>" + game.getWinner().getName() +
					          " wins!<br>" + (int)(game.getNumTurn() + 1) +
					          " turns" + "</center></html>");
		} else {
			turnLabel.setText("Turn " + (int)(game.getNumTurn() + 1));
		}
		
		// Borders around the board.
		g2.setStroke(new BasicStroke(5));
		g2.setColor(Color.BLACK);
		
		// Horizontal.
		g2.drawLine(0 + sideMargin, topMargin + 0 * cellSize,
				    (int)boardDim.width + sideMargin, topMargin + 0 * cellSize);
		g2.drawLine(0 + sideMargin, topMargin + bv.getRows() * cellSize,
					(int)boardDim.width + sideMargin,
					topMargin + bv.getRows() * cellSize);

		// Vertical.
		g2.drawLine(sideMargin + 0 * cellSize, 0 + topMargin,
					sideMargin + 0 * cellSize, 
					(int)boardDim.height + topMargin);
		g2.drawLine(sideMargin + bv.getCols() * cellSize, 0 + topMargin,
					sideMargin + bv.getCols() * cellSize, 
					(int)boardDim.height + topMargin);
		
		// For the slots.
		g2.setStroke(new BasicStroke(4));
		
		// Insert the discs.
		for (int row = 0; row < bv.getRows(); row++) {
			for (int col = 0; col < bv.getCols(); col++) {
				g2.setColor(new Color(40, 125, 84)); // Board color
				if (col == highlightCol) g2.setColor(Color.GREEN);

				g2.fillRect(sideMargin + col * cellSize, topMargin + row * cellSize,
						    cellSize, cellSize);

				if (bv.getDiscAt(row, col) == null) {
					g2.setColor(bg);
					g2.fillOval(sideMargin + col * cellSize + cellMargin,
						    topMargin + row * cellSize + cellMargin,
						    cellSize - cellMargin * 2,
						    cellSize - cellMargin * 2);
				} else {
					switch (bv.getDiscAt(row, col).getColour()) {
					case YELLOW:
						g2.setColor(Color.YELLOW);
						colourOval(g2, col, row);
						break;
					case RED:
						g2.setColor(Color.RED);
						colourOval(g2, col, row);
						break;
						
					case PIKA:
						Image img1 = new ImageIcon("pikachu.jpg").getImage();
						drawOvalImage(g2, img1, col, row);
						break;
						
					case DOGE:
						Image img2 = new ImageIcon("doge.jpg").getImage();
						drawOvalImage(g2, img2, col, row);
						break;
						
					case GOOGLE:
						Image img3 = new ImageIcon("google.jpg").getImage();
						drawOvalImage(g2, img3, col, row);
						break;
						
					case FACEBOOK:
						Image img4 = new ImageIcon("facebook.jpg").getImage();
						drawOvalImage(g2, img4, col, row);
						break;
						
					case ANDROID:
						Image img5 = new ImageIcon("android.jpg").getImage();
						drawOvalImage(g2, img5, col, row);
						break;
						
					case APPLE:
						Image img6 = new ImageIcon("apple.jpg").getImage();
						drawOvalImage(g2, img6, col, row);
						break;
					
					case BAT:
						Image img7 = new ImageIcon("batman.jpg").getImage();
						drawOvalImage(g2, img7, col, row);
						break;
						
					case IRON:
						Image img8 = new ImageIcon("ironman.jpg").getImage();
						drawOvalImage(g2, img8, col, row);
						break;
						
					case SPIDER:
						Image img9 = new ImageIcon("spiderman.jpg").getImage();
						drawOvalImage(g2, img9, col, row);
						break;
						
					case SUPER:
						Image img10 = new ImageIcon("superman.jpg").getImage();
						drawOvalImage(g2, img10, col, row);
						break;
					}
				}

				// Border around slot.
				g2.setColor(Color.BLACK);
				g2.drawOval(sideMargin + col * cellSize + cellMargin,
						    topMargin + row * cellSize + cellMargin,
						    cellSize - cellMargin * 2,
						    cellSize - cellMargin * 2);
						    
				
			}
				//Highlight winning run of 4
				if(gameOver == true) {
					g2.setColor(Color.white);
					int j;
					for(j = (int) bv.getWinningDiscs().size() - 4; j < bv.getWinningDiscs().size(); j++) {							
						g2.drawOval(sideMargin + (int) bv.getWinningDiscs().get(j).getY() * cellSize + cellMargin,
								    topMargin + (int) bv.getWinningDiscs().get(j).getX() * cellSize + cellMargin,
								    cellSize - cellMargin * 2,
								    cellSize - cellMargin * 2);
					}
					
				}
		}
	}
	
	private void colourOval(Graphics g2, int col, int row) {
		g2.fillOval(sideMargin + col * cellSize + cellMargin,
			    topMargin + row * cellSize + cellMargin,
			    cellSize - cellMargin * 2,
			    cellSize - cellMargin * 2);
	}
	
	private void fillDiscUnderName(Graphics g2, int i, JLabel l) {
		g2.fillOval(getRightX() + sideMargin + l.getSize().width / 2 +
			    i * (l.getSize().width + sideMargin) - 
			    (cellSize - cellMargin * 2) / 2,
			    getTopY() + l.getSize().height / 2 -
			    (cellSize - cellMargin * 2) / 2,
			    cellSize - cellMargin * 2, cellSize - cellMargin * 2);
	}
	
	private void drawOvalImage(Graphics g2, Image img, int col, int row) {
		g2.drawImage(img, sideMargin + col * cellSize + cellMargin,
		topMargin + row * cellSize + cellMargin,
		cellSize - cellMargin * 2,
		cellSize - cellMargin * 2, null);
	}
	
	private void drawImageUnderName(Graphics g2, Image img, int i, JLabel l) {
		g2.drawImage(img, getRightX() + sideMargin + l.getSize().width / 2 +
			    i * (l.getSize().width + sideMargin) - 
			    (cellSize - cellMargin * 2) / 2,
			    getTopY() + l.getSize().height / 2 -
			    (cellSize - cellMargin * 2) / 2,
			    cellSize - cellMargin * 2, cellSize - cellMargin * 2, null);
	}

	// TODO: Should this even be here?
	@Override
	public void actionPerformed(ActionEvent arg0) {
		repaint();
	}
	
	/**
	 * Notifies the game of the human move m (i.e. send human input
	 * to the game).
	 * @param m Move to send.
	 */
	public void sendMove(Move m) {
		game.setHumanMove(m);
	}
	
	/**
	 * Manually called to check whether a JLabel needs highlighting
	 * based on coordinates of the mouse. This exists because
	 * highlighting is not updated at the beginning of a turn.
	 * @param x x coordinate of mouse (xOnScreen).
	 * @param y y coordinate of mouse (yOnScreen).
	 */
	public void manualHighlight() {
		Point position = getMousePosition();

		if (position == null || !playerIsHuman() ||
		    !pointOnBoard(position.x, position.y)) {
			// All highlighting is off when it's the AI's turn.
			// If point is not on board, nothing to highlight.
			setHighlight(-1);
		} else {
			// Player turn and hovering over a JLabel - work out which
			// one and check if it is a valid move. If so, highlight.
				
			// Solve for columnNo.
			// x = leftMargin + cellSize * columnNo
				
			int columnNo = (position.x - getLeftX()) / getCellSize();
			if (getBv().isValidMove(new Move(columnNo))) {
				setHighlight(columnNo);
			} else {
				setHighlight(-1);
			}
		}
			
		repaint();
	}

	/**
	 * Returns true if the point given by (x, y) is on the board.
	 * @param x x component of coordinate.
	 * @param y y component of coordinate.
	 * @return True if (x, y) is on the board, false otherwise.
	 */
	public boolean pointOnBoard(int x, int y) {
		return !(x <= getLeftX() || x >= getRightX()
		         || y <= getTopY() || y >= getBottomY());
	}
	
	/**
	 * Set the JLabel to highlight on repaints. A value of -1
	 * means no columns are to be highlighted.
	 * @param col Columns number to highlight.
	 */
	public void setHighlight(int col) {
		this.highlightCol = col;
	}
	
	public BoardView getBv() {
		return bv;
	}
	
	/**
	 * Adds Reset, Menu, Quit buttons onto the board panel.
	 */
	private void addButtons() {
	    final JLabel reset = new JLabel("Reset");
	    reset.setFont(new Font("Impact", Font.PLAIN, 28));
	    reset.setForeground(Color.white);
	    reset.setOpaque(true);
	    reset.setBackground(new Color(40,125,84));
	    reset.setVerticalTextPosition(SwingConstants.CENTER);
	    reset.setHorizontalAlignment(SwingConstants.CENTER);
	    reset.setVisible(true);
	    this.reset = reset;
	    
	    final JLabel menu = new JLabel("Menu");
	    menu.setFont(new Font("Impact", Font.PLAIN, 28));
	    menu.setForeground(Color.white);
	    menu.setOpaque(true);
	    menu.setBackground(new Color(40,125,84));
	    menu.setVerticalAlignment(SwingConstants.CENTER);
	    menu.setHorizontalAlignment(SwingConstants.CENTER);
	    this.menu = menu;
	    
	    final JLabel quit = new JLabel("Quit");
	    quit.setFont(new Font("Impact", Font.PLAIN, 28));
	    quit.setForeground(Color.white);
	    quit.setOpaque(true);
	    quit.setBackground(new Color(40,125,84));
	    quit.setVerticalAlignment(SwingConstants.CENTER);
	    quit.setHorizontalAlignment(SwingConstants.CENTER);
	    this.quit = quit;
	    
	    add(menu);
	    add(reset);
	    add(quit);
	    
	    menu.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		graphics.getMainMenu().setVisible(true);
	    		graphics.getWindow().setContentPane(graphics.getMainMenu());
	    	}
	    });    
	    
	    reset.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		ArrayList<Player> oldPlayers = game.getPlayers();
	    		Game newGame = new Game(graphics);
	    		newGame.setPlayer(oldPlayers.get(0));
	    		newGame.setPlayer(oldPlayers.get(1));
	    		
	    		// BoardView nbv = new BoardView(newGame.getBoard());
	        	BoardPanel boardPanel = new BoardPanel(newGame,
	        			                               newGame.getBoardView(),
	        			                               graphics);
	        	setVisible(false);
	        	graphics.getWindow().setContentPane(boardPanel);
	        	graphics.setBoard(boardPanel);	       
	        	
	        	graphics.newGame(newGame);
	    	}
	    });
	    
	    quit.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		System.exit(0);
	    	}
	    });
	}
	
	/**
	 * Returns true if it is a human's turn.
	 * @return True if current player is a human, false otherwise.
	 */
	public boolean playerIsHuman() {
		return game.getPlayerTurn().getMoveBehaviour() instanceof
			   HumanBehaviour;
	}
	
	private void setDimVars() {
		cellSize = (int) Math.min(0.09 * graphics.getWindowWidth(),
				                  0.129 * graphics.getWindowHeight());
		cellMargin = 9;

		boardDim.height = bv.getRows() * cellSize;
		boardDim.width = bv.getCols() * cellSize;
		
		sideMargin = (graphics.getWindowWidth()) / 100;
		topMargin = (graphics.getWindowHeight() - boardDim.height - 75) / 5;
		topMargin = (graphics.getWindowHeight()) / 50;
	}
	
	private void setColumns() {
		int i = 0;
		for (JLabel col : columns) {
			col.setSize(cellSize, boardDim.height);
			col.setLocation(sideMargin + cellSize * i, topMargin);	
			i++;
		}
	}
	
	private void setButtons() {
	    Insets insets = getInsets();

	    int buttonWidth = ((graphics.getWindowWidth() / 100) * 60) / 3;
	    int buttonHeight = Math.min((((graphics.getWindowHeight() - getBottomY()) / 100)
	    		           * 75), 120);
	    int nButtons = 3;
	    
	    int yLoc = getBottomY() + (graphics.getWindowHeight() -
	    		   getBottomY() - buttonHeight) / 2 + insets.top;
	    
	    
	    int gap = (graphics.getWindowWidth() - (3 * buttonWidth)) /
	    		  (nButtons + 1);
	    int initGap = (graphics.getWindowWidth() - (3 * buttonWidth +
	    		      (gap * 2) + insets.left + insets.right)) / 2;
	    menu.setBounds(initGap - sideMargin / 2,  // Not sure why margin / 2.
	    		       yLoc, buttonWidth,
	    		       buttonHeight);
	    // Positions now based on previous button.
	    reset.setBounds(menu.getBounds().x + menu.getBounds().width + gap,
	    		        yLoc, buttonWidth,
	    		        buttonHeight);
	    quit.setBounds(reset.getBounds().x + reset.getBounds().width + gap,
	    		       yLoc, buttonWidth,
	    		       buttonHeight);
	}
	
	private void setTimerLabel() {
		timerLabel.setLocation(getRightX() + sideMargin,
				          topMargin + playerColumns.get(0).getSize().height +
				          topMargin + turnLabel.getSize().height + topMargin);
		timerLabel.setSize((graphics.getWindowWidth() - (getRightX())) -
				          2 * sideMargin - sideMargin,  // Not sure why extra '- margin'
				          boardDim.height / 3 - topMargin);
	}
	
	private void setTurnLabel() {
		turnLabel.setLocation(getRightX() + sideMargin,
				              topMargin + playerColumns.get(0).getSize().height +
				              topMargin);
		turnLabel.setSize((graphics.getWindowWidth() - (getRightX())) -
				          2 * sideMargin - sideMargin,  // Not sure why extra '- margin'
				          (boardDim.height) / 3 - topMargin);
	}
	
	private void setPlayerColumns() {
		int i = 0;
		Iterator<Player> itP = game.getPlayers().iterator();
		Iterator<JLabel> itL = playerColumns.iterator();
		JLabel l;
		Player p;
		for (; itL.hasNext() && itP.hasNext(); i++) {
            p = itP.next();
            l = itL.next();

			if (p.equals(game.getPlayerTurn()) || game.isDraw()) {
				// If it's the player's turn (includes if player won)
				// OR it's a draw - do bold. In a draw both players
				// names are highlighted.
				l.setFont(new Font("Impact", Font.BOLD, 20));
			} else {
				l.setFont(new Font("Impact", Font.PLAIN, 20));
			}

			l.setSize((graphics.getWindowWidth() - (getRightX())) / 2 -
					  2 * sideMargin, boardDim.height / 3);
			l.setLocation(getRightX() + sideMargin + i * sideMargin + 
					      i * (int)l.getSize().getWidth(), getTopY());

			l.setBorder(BorderFactory.createLineBorder(Color.black, 3));
		}
	}

	private int getLeftX() {
		return sideMargin;
	}
	
	private int getRightX() {
		return sideMargin + cellSize * bv.getCols();
	}

	private int getTopY() {
		return topMargin;
	}

	private int getBottomY() {
		return topMargin + cellSize * bv.getRows();
	}
	
	private int getCellSize() {
		return cellSize;
	}
	
}
