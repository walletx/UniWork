import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.awt.MouseInfo;
import java.awt.Point;
import java.lang.*;

/**
 * Game class consisting of the backend game system.
 * This runs in a thread different to the GUISystem.
 */
public class Game implements Runnable{
	private Board board;
	private ArrayList<Player> players;
	private int numTurn;
	private GUISystem graphics;
	private BoardView bv;

	private Player winner;
	private boolean draw;

	volatile private Move humanMove;
	
	volatile private boolean terminate;
	
	/**
	 * Constructor for GameSystem
	 */
	public Game(GUISystem graphics) {
		board = new Board();
		players = new ArrayList<Player>();
		numTurn = 0;
		humanMove = null;
		this.graphics = graphics;
		bv = new BoardView(board);

		winner = null;
		draw = false;
	}
	
	public void startGame() {
		graphics.getWindow().repaint();
	}
	
	public void run() {
		boolean stop = false;
		//extended from Runnable, separate thread for game logic
		//no mutex/semaphore used at the moment...might have a problem
		winner = null;
		draw = false;
		while(!getBoard().isFull()) {
			if (terminate) return;

			graphics.manualHighlight();
			
			Player currentPlayer = getPlayerTurn();
			Move move = null;
			while(move == null || !bv.isValidMove(move)) {
				if (terminate) return;

				if (currentPlayer.getMoveBehaviour() instanceof 
					HumanBehaviour) {
					move = humanMove;
				} else {
					move = currentPlayer.getMove(bv, getOtherPlayer(currentPlayer).getDisc());
				}
			}
			
			int row = getBoard().insertMove(move, currentPlayer.getDisc());
			
			if(getBoard().checkWin(row, move.getCol()) == true) {
				graphics.repaintBoard();
				Display.displayBoard(getBoard());
				//*Debugging*
				//board.printDiscs();
				System.out.println(currentPlayer.getName() + " wins!");
				stop = true;
				winner = currentPlayer;
				graphics.repaintBoard();
				break;
			}
			humanMove = null;
			
			System.out.println(currentPlayer.getName() + " Move!");
			
			if(currentPlayer.getName().equals("King Tee")) {
				AIPresetQuotes();
			}
			graphics.repaintBoard();
			Display.displayBoard(getBoard());
			
			//is this bad style? cuz this is game class, but i'm directly accessing numTurn
			numTurn++;
		}
		
		if (winner == null) draw = true;  // Game over but no winner.

		if(stop == true) {
			//stop gracefully;
			System.out.println("LOL");
			Sound fatal = new Sound("fatality.wav");
			fatal.playSound();
			fatal.clearAfterDelay(1200);
		}
	}
	
	public void terminateGame() {
		terminate = true;
	}
	
	
	
	/**
	 * Prompts the user for input to initialise the game.
	 * Sets the game mode, user's name, user's disc colour.
	 * @throws Exception 
	 */
	public void initGame() throws Exception {
		Scanner in = new Scanner(System.in);
		System.out.println("========== KING TEE'S CONNECT 4 ==========");
		System.out.println("Greetings hero, I am the princess fairy. In the land of CSESoc, we are being ruled over by King Tee!");
		System.out.println("We entrust our lives to you, please aid us in defeating King Tee in this formidable game to reclaim our land.");
		
		System.out.println("1 = VS King Tee");
		System.out.println("2 = VS Human");
		System.out.println("What is our hero's choice?");
		
		int nPlayers = in.nextInt();
		
		//Handles vsing AI
		if(nPlayers == 1) {
			String p1 = "";
			String d1 = "";
			System.out.print("Please your username: ");
			p1 = in.next();
			System.out.println(p1 + ", 1 = RED, 2 = YELLOW");
			System.out.println("What is your choice?");
			d1 = in.next();
			
			if(d1 == "1") {
				//player 1 wants red
				setPlayer(new Player(p1 , new Disc(Disc.Colour.RED), new HumanBehaviour()));
				setPlayer(new Player("King Tee", new Disc(Disc.Colour.YELLOW), new NoviceAIBehaviour()));
			} else {
				//player 1 wants yellow
				setPlayer(new Player(p1 , new Disc(Disc.Colour.YELLOW), new HumanBehaviour()));
				setPlayer(new Player("King Tee", new Disc(Disc.Colour.RED), new NoviceAIBehaviour()));
			}
			//handles multiplayer
		} else if(nPlayers == 2) {
			String p1 = "";
			String p2 = "";
			String d1 = "";
			System.out.print("Please enter Player 1's name: ");
			p1 = in.next();
			System.out.print("Please enter Player 2's name: ");
			p2 = in.next();
			System.out.println(p1 + ", 1 = RED, 2 = YELLOW");
			System.out.println("What is your choice?");
			d1 = in.next();
			
			if(d1 == "1") {
				setPlayer(new Player(p1 , new Disc(Disc.Colour.RED), new HumanBehaviour()));
				setPlayer(new Player(p2, new Disc(Disc.Colour.YELLOW), new HumanBehaviour()));
			} else {
				setPlayer(new Player(p1 , new Disc(Disc.Colour.YELLOW), new HumanBehaviour()));
				setPlayer(new Player(p2, new Disc(Disc.Colour.RED), new HumanBehaviour()));
			}
		} else {
			System.out.println("Invalid input...");
			throw new Exception();
		}
	}
	
	/*
	 * You can delete this if you want, it's for fun LOL
	 * Bad performance, since I load the quotes in every single iteration
	 * If we are actually taking this seriously, we can make a new quote class
	 * when we're almost at the end of the project
	 */
	public void AIPresetQuotes() {
		ArrayList<String> quotes = new ArrayList<String>();
		quotes.add("You shall never defeat me!");
		quotes.add("Good move, but mine's better!");
		quotes.add("You need more practice!");
		quotes.add("CSESoc is mine!");
		quotes.add("You will be my servant!");
		quotes.add("Join forces with me and we shall claim LawSoc too!");
		
		Random rn = new Random();
		int randomInt = rn.nextInt(6);
		
		System.out.println(quotes.get(randomInt));
	}
	
	/**
	 * Adds a new player to the array list.
	 * @param newPlayer Player to add
	 */
	public void setPlayer(Player newPlayer) {
		players.add(newPlayer);
	}
	
	/**
	 * Returns the player of which whose turn it is.
	 * Assumes that player 1 starts on turn 0.
	 * @return The player of this turn.
	 */
	public Player getPlayerTurn() {
		return players.get(numTurn % 2);
	}
	
	/**
	 * Returns the turn number of the game.
	 * @return The turn number of the game.
	 */
	public int getNumTurn() {
		return numTurn;
	}
	
	/**
	 * Returns the board object for the game.
	 * @return The board object for the game.
	 */
	public Board getBoard() {
		return board;
	}
	
	public BoardView getBoardView() {
		return bv;
	}
	
	/**
	 * Returns the array list of players of the game.
	 * @return Array list of players of the game.
	 */
	public ArrayList<Player> getPlayers() {
		return players;
	}
	
	/**
	 * Returns the Player object of the winner, or null if the game
	 * is not over yet.
	 * @return Player object of winner if the game has been won, null
	 *  otherwise.
	 */
	public Player getWinner() {
		return winner;
	}
	
	public Player getOtherPlayer(Player currentPlayer) {
		int i = 0;
		while(i < players.size()) {
			if(!players.get(i).equals(currentPlayer)) {
				return players.get(i);
			}
		}
		return null;
	}
	
	public void setHumanMove(Move m) {
		humanMove = m;
	}

	public boolean isDraw() {
		return draw;
	}
}
