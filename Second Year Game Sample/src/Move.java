
public class Move {
	private int col;

	/**
	 * Constructor for move
	 * @param col The column from 0-6 of the board, 
	 * 				since the board has 7 columns.
	 */
	public Move(int col) {
		this.col = col;
	}
	
	/**
	 * Returns the column of this move
	 * @return The column of this move.
	 */
	public int getCol() {
		return col;
	}
}
