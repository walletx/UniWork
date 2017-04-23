import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 * Player2Options class which is shown on the GUI.
 *
 */
public class Player2Options extends JPanel {
	private JLabel p1Name;
	private JLabel p2Name;
	private JTextField p1UserName;
	private JTextField p2UserName;
	private Button startGameButton;
	private Button backButton;
	private JPanel discPanel1;
	private JPanel discPanel2;
	private Disc player1Disc;
	private Disc player2Disc;
	private JPanel chosenDisc1;
	private JPanel chosenDisc2;
	private ArrayList<JLabel> playableDiscs1;
	private ArrayList<JLabel> playableDiscs2;
	private int discIndex;

	
	/**
	 * Constructor for Player1Options
	 * Initialises all of Player1Options properties
	 * and also sets up the page.
	 * @param game The game system object
	 * @param graphics The graphics object
	 */
	public Player2Options(GUISystem graphics) {
		discPanel1 = new JPanel();
		discPanel1.setLayout(new GridLayout(3, 4));
		discPanel1.setBackground(new Color( 40,125,84));
		discPanel2 = new JPanel();
		discPanel2.setLayout(new GridLayout(3, 4));
		discPanel2.setBackground(new Color( 40,125,84));
		
		chosenDisc1 = new JPanel();
		chosenDisc2 = new JPanel();
		
		playableDiscs1 = new ArrayList<JLabel>();
		playableDiscs2 = new ArrayList<JLabel>();

		try {
			initDiscs();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		initLabels();
		initButtons();
		initText();
		initChosenDisc();
		
		setBounds(0,0, graphics.getWindowWidth(), graphics.getWindowHeight());
		setBackground(new Color( 40,125,84));
	    setLayout(new GridBagLayout());
	    
	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.CENTER;
	    c.ipadx = 0;
	    c.ipady = 20;
	    
	    c.gridx = 0;
	    c.gridy = 0;
	    this.add(p1Name, c);
	    
	    //c.insets = new Insets(0, -50, 0, 0);
	    c.gridx = 1;
	    c.gridy = 0;
	    this.add(p1UserName, c);
	    
	    c.insets = new Insets(50, 0, 0, 0);
	    c.gridx = 0;
	    c.gridy = 2;
	    this.add(discPanel1, c);
	    
	    c.gridx = 1;
	    this.add(chosenDisc1, c);

	    c.insets = new Insets(0, 30, 0, 0);
	    c.gridx = 2;
	    c.gridy = 0;
	    this.add(p2Name, c);
	    
	   // c.insets = new Insets(0, -50, 0, 0);
	    c.gridx = 3;
	    c.gridy = 0;
	    this.add(p2UserName, c);
	    
	    c.insets = new Insets(50, 0, 0, 0);
	    c.gridx = 3;
	    c.gridy = 2;
	    this.add(discPanel2, c);
	    
	    c.gridx = 2;
	    this.add(chosenDisc2, c);
	    
	    
	    //Insets is used to position start and back further down the page
	    c.insets = new Insets(150,0,0,0);
	    c.gridx = 0;
	    c.gridy = 3;
	    this.add(startGameButton, c);
	    c.gridx = 3;
	    c.gridy = 3;
	    this.add(backButton, c);
	   
	    setListeners(graphics);
	}

	/**
	 * Initialises the listeners on the page.
	 * 
	 * @param graphics The main graphics system
	 */
	public void setListeners(final GUISystem graphics) {
		startGameButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {	
		        String p1 = p1UserName.getText();
		        String p2 = p2UserName.getText();
		    	
		        Game game = new Game(graphics);
		        
		        game.getPlayers().add(new Player(p1, player1Disc, new HumanBehaviour()));
		        game.getPlayers().add(new Player(p2, player2Disc, new HumanBehaviour()));
		        
		        // BoardView bv = new BoardView(game.getBoard());
	        	BoardPanel boardPanel = new BoardPanel(game,
	        			                               game.getBoardView(),
	        			                               graphics);
	        	setVisible(false);
	        	graphics.getWindow().setContentPane(boardPanel);
	        	graphics.setBoard(boardPanel);	       
	        	
	        	graphics.newGame(game);
	        	
	        	//game.startGame(bv);
	        	
	        	//start the thread
	        	//(new Thread(game)).start();
	        }
		});

	    backButton.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent actionEvent) {
	    	   setVisible(false);
	    	   graphics.getMainMenu().setVisible(true);
	    	   graphics.getWindow().setContentPane(graphics.getMainMenu());
	       }
	    });
	    
	    p1UserName.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		p1UserName.setText("");
	    	}
	    });
	    
	    p2UserName.addMouseListener(new MouseAdapter() {
	    	public void mouseClicked(MouseEvent e) {
	    		p2UserName.setText("");
	    	}
	    });
	    
	    for(final JLabel l : playableDiscs1) {
	    	l.addMouseListener(new MouseAdapter() {
	    		public void mouseClicked(MouseEvent e) {
	    			discIndex = playableDiscs1.indexOf(l);
	    			BufferedImage img = null;
	    			Image scaledImg = null;

	    			switch(discIndex) {
		        	case 0:
		        		player1Disc = new Disc(Disc.Colour.RED);
		        		try {
							img = ImageIO.read(new File("red.jpg"));
						} catch (IOException e1) { }
						scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
						break;
		        	case 1:
		        		player1Disc = new Disc(Disc.Colour.PIKA);
		        		try {
							img = ImageIO.read(new File("pikachu.jpg"));
							scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
						} catch (IOException e1) { }
		        		break;
		        	case 2:
		        		player1Disc = new Disc(Disc.Colour.GOOGLE);
		        		try {
		        			img = ImageIO.read(new File("google.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 3:
		        		player1Disc = new Disc(Disc.Colour.ANDROID);
		        		try {
		        			img = ImageIO.read(new File("android.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 4:
		        		player1Disc = new Disc(Disc.Colour.BAT);
		        		try {
		        			img = ImageIO.read(new File("batman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 5:
		        		player1Disc = new Disc(Disc.Colour.SPIDER);
		        		try {
		        			img = ImageIO.read(new File("spiderman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	}
		        	
				JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
				chosenDisc1.remove(1);
				chosenDisc1.add(picLabel, 1);
				chosenDisc1.repaint();
				chosenDisc1.revalidate();
	    		}
	    	});
	    }
	    
	    for(final JLabel l : playableDiscs2) {
	    	l.addMouseListener(new MouseAdapter() {
	    		public void mouseClicked(MouseEvent e) {
	    			discIndex = playableDiscs2.indexOf(l);
	    			BufferedImage img = null;
	    			Image scaledImg = null;
	    			
	    			switch(discIndex) {
		        	
		        	case 0:
		        		player2Disc = new Disc(Disc.Colour.YELLOW);
		        		try {
		        			img = ImageIO.read(new File("yellow.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch(IOException e1) { }
		        		break;
		        	
		        	case 1:
		        		player2Disc = new Disc(Disc.Colour.DOGE);
		        		try {
		        			img = ImageIO.read(new File("doge.jpg"));
		    				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 2:
		        		player2Disc = new Disc(Disc.Colour.FACEBOOK);
		        		try {
		        			img = ImageIO.read(new File("facebook.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 3:
		        		player2Disc = new Disc(Disc.Colour.APPLE);
		        		try {
		        			img = ImageIO.read(new File("apple.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 4:
		        		player2Disc = new Disc(Disc.Colour.IRON);
		        		try {
		        			img = ImageIO.read(new File("ironman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	
		        	case 5:
		        		player2Disc = new Disc(Disc.Colour.SUPER);
		        		try {
		        			img = ImageIO.read(new File("superman.jpg"));
		        			scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		        		} catch (IOException e1) { }
		        		break;
		        	}
				JLabel picLabel2 = new JLabel(new ImageIcon(scaledImg));
				chosenDisc2.remove(1);
				chosenDisc2.add(picLabel2, 1);
				chosenDisc2.repaint();
				chosenDisc2.revalidate();
	    		}
	    	});
	    }
	    
	    
	}
	
	/**
	 * Sets up JLabels on the page.
	 */
	public void initLabels() {
		p1Name = new JLabel("Player 1: ");
		p2Name = new JLabel("Player 2: ");
		p1Name.setFont(new Font("Impact", Font.PLAIN, 36));
		p2Name.setFont(new Font("Impact", Font.PLAIN, 36));
	}
	
	/**
	 * Initialises the buttons on the page.
	 */
	public void initButtons() {
		startGameButton = new Button("Start Game", 200, 75);
		backButton = new Button("Back", 200, 75);
	}
	
	/**
	 * Initialises text on the page.
	 */
	public void initText() {
		p1UserName = new JTextField("Enter name");
		p1UserName.setFont(new Font("Impact", Font.PLAIN, 36));
		p1UserName.setBackground(new Color(104, 172, 139));

		p1UserName.setPreferredSize(new Dimension(200, 50));
		p2UserName = new JTextField("Enter name");
		p2UserName.setFont(new Font("Impact", Font.PLAIN, 36));
		p2UserName.setBackground(new Color(104, 172, 139));

		p2UserName.setPreferredSize(new Dimension(200, 50));
	}
	
	/**
	 * Initialises the disc images on the page.
	 * 
	 * @throws IOException
	 */
	public void initDiscs() throws IOException {
		for(int i = 0; i < 12; i++) {
			BufferedImage img;
			Image scaledImg = null;
			JLabel picLabel1 = null;
			JLabel picLabel2 = null;
			switch(i) {
			case 0:
				img = ImageIO.read(new File("red.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 1:
				img = ImageIO.read(new File("yellow.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			case 2:
				img = ImageIO.read(new File("pikachu.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 3:
				img = ImageIO.read(new File("doge.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			case 4:
				img = ImageIO.read(new File("google.jpg"));

				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 5:
				img = ImageIO.read(new File("facebook.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			case 6:
				img = ImageIO.read(new File("android.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 7:
				img = ImageIO.read(new File("apple.jpg"));
				scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			case 8:
				img = ImageIO.read(new File("batman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 9:
				img = ImageIO.read(new File("ironman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			case 10:
				img = ImageIO.read(new File("spiderman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel1 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs1.add(picLabel1);
				break;
			case 11:
				img = ImageIO.read(new File("superman.jpg"));
				scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
				picLabel2 = new JLabel(new ImageIcon(scaledImg));
				playableDiscs2.add(picLabel2);
				break;
			}
			if(picLabel1 != null) {
				discPanel1.add(picLabel1);
			}
			if(picLabel2 != null) {
				discPanel2.add(picLabel2); 
			}
		}
	}
	
	/**
	 * On entrance to Player2Options, initialises
	 * default discs for both players.
	 */
	public void initChosenDisc() {
		player1Disc = new Disc(Disc.Colour.RED);
		chosenDisc1.setLayout(new GridLayout(2, 1));
		chosenDisc1.setBackground(new Color( 40,125,84));
		JLabel string = new JLabel("Selection");
		string.setFont(new Font("Impact", Font.PLAIN, 28));
		chosenDisc1.add(string);

		player2Disc = new Disc(Disc.Colour.YELLOW);
		chosenDisc2.setLayout(new GridLayout(2, 1));
		chosenDisc2.setBackground(new Color( 40,125,84));
		JLabel string2 = new JLabel("Selection");
		string2.setFont(new Font("Impact", Font.PLAIN, 28));
		chosenDisc2.add(string2);
		
		
		BufferedImage img = null;
		Image scaledImg = null;
		try {
			img = ImageIO.read(new File("red.jpg"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		BufferedImage img2 = null;
		Image scaledImg2 = null;
		try {
			img2 = ImageIO.read(new File("yellow.jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		scaledImg = img.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JLabel picLabel = new JLabel(new ImageIcon(scaledImg));
		playableDiscs1.add(picLabel);
		chosenDisc1.add(picLabel);
		
		scaledImg2 = img2.getScaledInstance(70, 70, Image.SCALE_SMOOTH);
		JLabel picLabel2 = new JLabel(new ImageIcon(scaledImg2));
		playableDiscs2.add(picLabel2);
		chosenDisc2.add(picLabel2);
	
	}
}
