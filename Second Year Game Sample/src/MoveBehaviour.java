
public interface MoveBehaviour {
	/**
	 * Creates a move for the player
	 * @return Returns the created move object.
	 */
	public Move makeMove(BoardView bv, Disc myDisc, Disc otherDisc);
}
