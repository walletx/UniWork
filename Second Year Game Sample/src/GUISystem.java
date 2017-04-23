
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

//Graphics class
//Windows, menus are created here
public class GUISystem {
    private JFrame window;
    private MainMenu mainMenu;
	
    private BoardPanel board;
    
    private Game currentlyRunning;

    /**
     * Constructor for graphics
     * Initialises frame, layers and menu.
     */
	public GUISystem() {
        // initialise frame
        window = new JFrame("Connect 4");
		
        window.setMinimumSize(new Dimension(1000, 700));

		mainMenu = new MainMenu(this);
		//window.getContentPane().add(mainMenu.getMenu());
		
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //window.setVisible(true);
        
        window.setLocationRelativeTo(null);
        
        currentlyRunning = null;
	}
	
	public static void main(String args[]) {
		final GUISystem graphics = new GUISystem();
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				graphics.display();
			}
		});
	}
	
	/**
	 * Stops the old game from running and creates a new game
	 * where the thread is separate to the GUI thread.
	 * 
	 * @param g New game object.
	 */
	public void newGame(Game g) {
		if (currentlyRunning != null) {
			// A game is running, we must stop it.
			currentlyRunning.terminateGame();
		}
		
		currentlyRunning = g;
		g.startGame();
		(new Thread(g)).start();
	}

	/**
	 * Get the height of the window jframe.
	 * @return Height of window jframe.
	 */
	public int getWindowHeight() {
		return window.getHeight();
	}
	
	/**
	 * Get the width of the window jframe.
	 * @return Width of window jframe.
	 */
	public int getWindowWidth() {
		return window.getWidth();
	}
	
	/**
	 * Get main menu object.
	 * @return Main menu object.
	 */
	public MainMenu getMainMenu() {
		return mainMenu;
	}
	
	 /**
	  * Get the window jframe.
	  * @return Window jframe.
	  */
	public JFrame getWindow() {
		return window;
	}
	
	public void setBoard(BoardPanel boardP) {
		board = boardP;
	}
	
	public void repaintBoard() {
		board.repaint();
	}
	
	public void manualHighlight() {
		board.manualHighlight();
	}
	
	/**
	 * Display the GUI by adding main menu to the jframe
	 * and setting it to visible.
	 */
	public void display() {
		window.getContentPane().add(mainMenu);
		window.setVisible(true);
	}
}
