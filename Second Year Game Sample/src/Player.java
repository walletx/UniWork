import java.util.Random;


public class Player {
	private String name;
	private Disc disc;
	private MoveBehaviour m;

	/**
	 * Constructor for Player
	 * @param name The name of the player
	 * @param disc The type of disc the player is using
	 * @param m The behaviour of the player, human or a specific AI
	 */
	public Player(String name, Disc disc, MoveBehaviour m) {
		this.name = name;
		this.disc = disc;
		this.m = m;
	}
	
	/**
	 * Returns the name of the player
	 * @return The name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the type of disc of this player
	 * @return The disc associated with this player
	 */
	public Disc getDisc() {
		return disc;
	}

	/**
	 * Returns the move made by this player
	 * @return The move made by this player
	 */
	public Move getMove(BoardView bv, Disc otherDisc) {
		try {
			return m.makeMove(bv, disc, otherDisc);
		} catch (Exception e) {
			Random rn = new Random();
			Move move = new Move(rn.nextInt(bv.getCols()));
			return move;
		}
	}
	
	/**
	 * Returns the move behaviour of the player
	 * @return The move behaviour of the player
	 */
	public MoveBehaviour getMoveBehaviour() {
		return m;
	}
}
