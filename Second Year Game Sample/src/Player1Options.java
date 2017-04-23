
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Player1Options class which is displayed on the GUI.
 */
public class Player1Options extends JPanel {
	private JPanel discPanel;
	private Disc player1Disc;
	private JPanel chosenDisc;
	private JLabel name;
	private JLabel disc;
	private JLabel AILevel;
	private JTextField userName;
	private Button startGameButton;
	private Button backButton;
	private Button easyAI;
	private Button normalAI;
	private Button hardAI;
	private ArrayList<Button> aiButtons;
	private ArrayList<JLabel> playableDiscs;
	private int discIndex;

	
	/**
	 * Constructor for Player1Options
	 * Initialises all of Player1Options properties
	 * and also sets up the page.
	 * @param game The game system object
	 * @param graphics The graphics object
	 */
	public Player1Options(GUISystem graphics) {
		aiButtons = new ArrayList<Button>();
		playableDiscs = new ArrayList<JLabel>();
		chosenDisc = new JPanel();
		discPanel = new JPanel();
		discPanel.setLayout(new GridLayout(3, 4));
		discPanel.setBackground(new Color( 40,125,84));
		
		initLabels();
		initButtons();
		initText();
		initChosenDisc();
		
		setBounds(0,0, graphics.getWindowWidth(), graphics.getWindowHeight());
		setBackground(new Color( 40,125,84));
	    setLayout(new GridBagLayout());
	    
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.FIRST_LINE_START;
	    //c.ipadx = 30;
	   // c.ipady = 20;
	    
	    c.gridx = 0;
	    c.gridy = 0;
	    this.add(name, c);
	    c.gridx = 1;
	    c.gridy = 0;
	    this.add(userName, c);
	    c.gridx = 0;
	    c.gridy = 1;
	    this.add(disc, c);
	    
	    c.gridx = 1;
	    c.gridy = 2;
	   
	    try {
			initDiscs();
		} catch (IOException e) {
			e.printStackTrace();
		}
	    this.add(discPanel, c);
	    
	    c.gridx = 2;
	    c.gridy = 2;
	    this.add(chosenDisc, c);
	    
	    c.gridx = 0;
	    c.gridy = 3;
	    this.add(AILevel, c);
	    
	    c.gridx = 0;
	    c.gridy = 4;
	    this.add(easyAI, c);
	  //  c.insets = new Insets(0, -70,0,0);
	    c.gridx = 1;
	    c.gridy = 4;
	    this.add(normalAI, c);
	    c.gridx = 2;
	    c.gridy = 4;
	   // c.insets = new Insets(0, -70,0,0);
	    this.add(hardAI, c);
	    //Insets is used to position start and back further down the page
	    c.insets = new Insets(100,0, 0,0);
	    c.gridx = 0;
	    c.gridy = 5;
	    this.add(startGameButton, c);
	   // c.insets = new Insets(100,-70, 0,0);
	    c.gridx = 2;
	    c.gridy = 5;
	    this.add(backButton, c);
	   
	    
	    setListeners(graphics);
	}

	/**
	 * Sets up listeners for every button for Player1Options.
	 * 
	 * @param game The game system object
	 * @param graphics Graphics object
	 */
	public void setListeners(final GUISystem graphics) {    
	    startGameButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {	
	        	if(aiAlreadySelected()) {
		        	String p1 = userName.getText();
		        	Button aiLevel = getAISelected();
		        	
		        	Game game = new Game(graphics);
		        	
		        	Disc aiDisc = player1Disc;

		        	Random random = new Random();
		        	
		        	while(aiDisc.getColour().equals(player1Disc.getColour())) {
			        	int randomAIDisc = random.nextInt(12)+1;
			        	switch(randomAIDisc) {
			        	case 1:
			        		aiDisc = new Disc(Disc.Colour.RED);
			        		break;
			        	case 2:
			        		aiDisc = new Disc(Disc.Colour.YELLOW);
			        		break;
			        	case 3:
			        		aiDisc = new Disc(Disc.Colour.PIKA);
			        		break;
			        	case 4:
			        		aiDisc = new Disc(Disc.Colour.DOGE);
			        		break;
			        	case 5:
			        		aiDisc = new Disc(Disc.Colour.GOOGLE);
			        		break;
			        	case 6:
			        		aiDisc = new Disc(Disc.Colour.FACEBOOK);
			        		break;
			        	case 7:
			        		aiDisc = new Disc(Disc.Colour.ANDROID);
			        		break;
			        	case 8:
			        		aiDisc = new Disc(Disc.Colour.APPLE);
			        		break;
			        	case 9:
			        		aiDisc = new Disc(Disc.Colour.BAT);
			        		break;
			        	case 10:
			        		aiDisc = new Disc(Disc.Colour.IRON);
			        		break;
			        	case 11:
			        		aiDisc = new Disc(Disc.Colour.SPIDER);
			        		break;
			        	case 12:
			        		aiDisc = new Disc(Disc.Colour.SUPER);
			        		break;
			        	}
		        	}
		        	
		        	game.getPlayers().add(new Player(p1, player1Disc, new HumanBehaviour()));
		        	
		        	if(aiLevel.equals(easyAI)) {
		        		game.getPlayers().add(new Player("King Tee", aiDisc, new NoviceAIBehaviour()));
		        	} else if(aiLevel.equals(normalAI)) {
		        		game.getPlayers().add(new Player("King Pongpol", aiDisc, new HarderAIBehaviour()));
		        	} else if(aiLevel.equals(hardAI)) {
		        		game.getPlayers().add(new Player("King Trisuwan", aiDisc, new HardestAIBehaviour()));
		        	}
		        	
		        	// BoardView bv = new BoardView(game.getBoard());
		        	BoardPanel boardPanel = new BoardPanel(game,
		        			                               game.getBoardView(),
		        			                               graphics);
		        	setVisible(false);
		        	graphics.getWindow().setContentPane(boardPanel);
		        	graphics.setBoard(boardPanel);	       
		        	
		        	graphics.newGame(game);
	        	} else {
	        		Game game = new Game(graphics);
	        		String p1 = userName.getText();
	        		game.getPlayers().add(new Player(p1, player1Disc, new HumanBehaviour()));
	        		
	        		Disc aiDisc = player1Disc;

		        	Random random = new Random();
		        	
		        	while(aiDisc.getColour().equals(player1Disc.getColour())) {
			        	int randomAIDisc = random.nextInt(12)+1;
			        	switch(randomAIDisc) {
			        	case 1:
			        		aiDisc = new Disc(Disc.Colour.RED);
			        		break;
			        	case 2:
			        		aiDisc = new Disc(Disc.Colour.YELLOW);
			        		break;
			        	case 3:
			        		aiDisc = new Disc(Disc.Colour.PIKA);
			        		break;
			        	case 4:
			        		aiDisc = new Disc(Disc.Colour.DOGE);
			        		break;
			        	case 5:
			        		aiDisc = new Disc(Disc.Colour.GOOGLE);
			        		break;
			        	case 6:
			        		aiDisc = new Disc(Disc.Colour.FACEBOOK);
			        		break;
			        	case 7:
			        		aiDisc = new Disc(Disc.Colour.ANDROID);
			        		break;
			        	case 8:
			        		aiDisc = new Disc(Disc.Colour.APPLE);
			        		break;
			        	case 9:
			        		aiDisc = new Disc(Disc.Colour.BAT);
			        		break;
			        	case 10:
			        		aiDisc = new Disc(Disc.Colour.IRON);
			        		break;
			        	case 11:
			        		aiDisc = new Disc(Disc.Colour.SPIDER);
			        		break;
			        	case 12:
			        		aiDisc = new Disc(Disc.Colour.SUPER);
			        		break;
			        	}
		        	}
		        	
	        		game.getPlayers().add(new Player("King Pongpol", aiDisc, new HarderAIBehaviour()));
	        		
	        		BoardPanel boardPanel = new BoardPanel(game,
                            game.getBoardView(),
                            graphics);
					setVisible(false);
					graphics.getWindow().setContentPane(boardPanel);
					graphics.setBoard(boardPanel);	       
					
					graphics.newGame(game);
	        	}
	        }
	    });

	    backButton.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent actionEvent) {
	    	   setVisible(false);
	    	   graphics.getMainMenu().setVisible(true);
	    	   graphics.getWindow().setContentPane(graphics.getMainMenu());
	       }
	    });
	    
	    userName.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		userName.setText("");
	    	}
	    });
	    
	    easyAI.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		if(aiAlreadySelected()) {
	    			Button temp = getAISelected();
	    			if(!temp.equals(easyAI)) {
	    				temp.setUnselect();
	    				temp.repaint();
	    			}
	    		}
	    		if(easyAI.getIsPressed() == false) {
	    			easyAI.setSelected();
	    		} else {
	    			easyAI.setUnselect();
	    		}
	    	}
	    });
	    
	    normalAI.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		if(aiAlreadySelected()) {
	    			Button temp = getAISelected();
	    			if(!temp.equals(normalAI)) {
	    				temp.setUnselect();
	    				temp.repaint();
	    			}
	    		}
	    		if(normalAI.getIsPressed() == false) {
	    			normalAI.setSelected();
	    		} else {
	    			normalAI.setUnselect();
	    		}
	    	}
	    });
	    
	    hardAI.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		if(aiAlreadySelected()) {
	    			Button temp = getAISelected();
	    			if(!temp.equals(hardAI)) {
	    				temp.setUnselect();
	    				temp.repaint();
	    			}
	    		}
	    		if(hardAI.getIsPressed() == false) {
	    			hardAI.setSelected();
	    		} else {
	    			hardAI.setUnselect();
	    		}
	    	}
	    });
	    
	    for(final JLabel l : playableDiscs) {
	    	l.addMouseListener(new MouseAdapter() {
	    		public void mouseClicked(MouseEvent e) {
	    			discIndex = playableDiscs.indexOf(l);
	    			BufferedImage img = null;
	    			Image scaledImg = null;
	    			
	    			switch(discIndex) {
		        	case 1:
		        		player1Disc = new Disc(Disc.Colour.RED);
		        		try {
							img = ImageIO.read(new File("red.jpg"));
						} catch (IOException e1) { }
						scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
						break;
		        	case 2:
		        		player1Disc = new Disc(Disc.Colour.YELLOW);
		        		try {
		        			img = ImageIO.read(new File("yellow.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch(IOException e1) { }
		        		break;
		        	case 3:
		        		player1Disc = new Disc(Disc.Colour.PIKA);
		        		try {
							img = ImageIO.read(new File("pikachu.jpg"));
							scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
						} catch (IOException e1) { }
		        		break;
		        	case 4:
		        		player1Disc = new Disc(Disc.Colour.DOGE);
		        		try {
		        			img = ImageIO.read(new File("doge.jpg"));
		    				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 5:
		        		player1Disc = new Disc(Disc.Colour.GOOGLE);
		        		try {
		        			img = ImageIO.read(new File("google.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 6:
		        		player1Disc = new Disc(Disc.Colour.FACEBOOK);
		        		try {
		        			img = ImageIO.read(new File("facebook.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 7:
		        		player1Disc = new Disc(Disc.Colour.ANDROID);
		        		try {
		        			img = ImageIO.read(new File("android.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 8:
		        		player1Disc = new Disc(Disc.Colour.APPLE);
		        		try {
		        			img = ImageIO.read(new File("apple.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 9:
		        		player1Disc = new Disc(Disc.Colour.BAT);
		        		try {
		        			img = ImageIO.read(new File("batman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 10:
		        		player1Disc = new Disc(Disc.Colour.IRON);
		        		try {
		        			img = ImageIO.read(new File("ironman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 11:
		        		player1Disc = new Disc(Disc.Colour.SPIDER);
		        		try {
		        			img = ImageIO.read(new File("spiderman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	case 12:
		        		player1Disc = new Disc(Disc.Colour.SUPER);
		        		try {
		        			img = ImageIO.read(new File("superman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	}
				JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
				chosenDisc.remove(1);
				chosenDisc.add(picLabel, 1);
				chosenDisc.repaint();
				chosenDisc.revalidate();
	    		}
	    	});
	    }
	}
	
	/**
	 * Initialises all the buttons for MainMenu.
	 * Sets the size and also the String displayed.
	 */
	public void initButtons() {
		startGameButton = new Button("Start Game", 200, 75);
		backButton = new Button("Back", 200, 75);
		easyAI = new Button("Easy", 200, 75);
		aiButtons.add(easyAI);
		normalAI = new Button("Normal", 200, 75);
		aiButtons.add(normalAI);
		hardAI = new Button("Hard", 200, 75);
		aiButtons.add(hardAI);
	}
		/**
	 * Initialises labels for MainMenu.
	 * Sets the type and size of font.
	 */
	public void initLabels() {
		name = new JLabel("Player 1:");
		name.setFont(new Font("Impact", Font.PLAIN, 36));
		disc = new JLabel("Your weapon: ");
		disc.setFont(new Font("Impact", Font.PLAIN, 36));
		AILevel = new JLabel("Difficulty");
		AILevel.setFont(new Font("Impact", Font.PLAIN, 36));
	}
	
		/**
	 * Initialises all discs and adds to DiscPanel for display.
	 */
	
	public void initDiscs() throws IOException {
		for(int i = 0; i < 12; i++) {
			BufferedImage img;
			Image scaledImg = null;
			switch(i) {
			case 0:
				img = ImageIO.read(new File("red.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			case 1:
				img = ImageIO.read(new File("yellow.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			case 2:
				img = ImageIO.read(new File("pikachu.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 3:
				img = ImageIO.read(new File("doge.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 4:
				img = ImageIO.read(new File("google.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 5:
				img = ImageIO.read(new File("facebook.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 6:
				img = ImageIO.read(new File("android.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 7:
				img = ImageIO.read(new File("apple.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				break;
			case 8:
				img = ImageIO.read(new File("batman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			case 9:
				img = ImageIO.read(new File("ironman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			case 10:
				img = ImageIO.read(new File("spiderman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			case 11:
				img = ImageIO.read(new File("superman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				break;
			}
			JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
			playableDiscs.add(picLabel);
			discPanel.add(picLabel);
		}
	}
	
	/**
	 * Initialises default disc as RED for Human Player.
	 */
	
	public void initChosenDisc() {
		chosenDisc.setLayout(new GridLayout(2, 1));
		chosenDisc.setBackground(new Color( 40,125,84));
		JLabel string = new JLabel("Selection");
		string.setFont(new Font("Impact", Font.PLAIN, 28));
		chosenDisc.add(string);
		
		BufferedImage img = null;
		Image scaledImg = null;
		try {
			img = ImageIO.read(new File("red.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
		playableDiscs.add(picLabel);
		chosenDisc.add(picLabel);	
		player1Disc = new Disc(Disc.Colour.RED);
	}
	
	/**
	 * Initialises text on the page.
	 */
	public void initText() {
		userName = new JTextField("Enter name");
		userName.setFont(new Font("Impact", Font.PLAIN, 36));
		userName.setBackground(new Color(104, 172, 139));
		userName.setPreferredSize(new Dimension(200, 50));
	}
	
	/**
	 * Returns whether or not an AI is already selected
	 * @return True if an AI is already selected, otherwise false.
	 */
	public boolean aiAlreadySelected() {
		for(Button b : aiButtons) {
			if(b.getIsPressed() == true) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Assuming an AI is already selected,
	 * will return the button which is selected.
	 * 
	 * @return The button which is already selected.
	 */
	public Button getAISelected() {
		for(Button b : aiButtons) {
			if(b.getIsPressed()) {
				return b;
			}
		}
		return null;
	}
	
}
