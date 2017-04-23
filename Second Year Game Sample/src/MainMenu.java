import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class MainMenu extends JPanel {
	private JLabel name;
	private Player1Options p1Settings;
	private Player2Options p2Settings;
	private Button singlePlayerButton;
	private Button twoPlayerButton;
	private Button exitButton;
	
	/**
	 * Constructor for MainMenu
	 * Initialises the properties of main menu
	 * and sets it up.
	 * @param game
	 * @param graphics
	 */
	public MainMenu(GUISystem graphics) {
		name = new JLabel("Connect Four");
		name.setFont(new Font("Impact", Font.PLAIN, 48));
		singlePlayerButton = new Button("1 Player", 200, 75);
		twoPlayerButton = new Button("2 Player", 200, 75);
		exitButton = new Button("Quit", 200, 75);
		
		setBounds(0,0, graphics.getWindowWidth(), graphics.getWindowHeight());
		setBackground(new Color( 40,125,84));
	    setLayout(new GridBagLayout());

	    GridBagConstraints c = new GridBagConstraints();
	    c.fill = GridBagConstraints.FIRST_LINE_START;
	    c.ipadx = 30;
	    c.ipady = 20;
	    c.gridx = 0;
	    c.gridy = 0;
	   
	    this.add(name);
	    c.gridx = 0;
	    c.gridy = 1;
	    this.add(singlePlayerButton, c);
	    c.gridy = 2;
	    this.add(twoPlayerButton, c);
	    c.gridy = 3;
	    this.add(exitButton, c);
	    
	    setListeners(graphics);
	}
	
	/**
	 * Sets up the listeners for every button in MainMenu
	 * 
	 * @param game The game system object
	 * @param graphics Graphics object
	 */
	public void setListeners(final GUISystem graphics) {    
	    singlePlayerButton.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent actionEvent) {	
	        	setVisible(false);
	            p1Settings = new Player1Options(graphics);
	            graphics.getWindow().setContentPane(p1Settings);
	        }
	    });

	    twoPlayerButton.addActionListener(new ActionListener() {
	       public void actionPerformed(ActionEvent actionEvent) {
	    	   setVisible(false);
	    	   p2Settings = new Player2Options(graphics);
	    	   graphics.getWindow().setContentPane(p2Settings);
	       }
	    });

	    exitButton.addActionListener(new ActionListener() {
	       @Override
	       public void actionPerformed(ActionEvent actionEvent) {
	           System.exit(0);
	       }
	    });
	}
}
