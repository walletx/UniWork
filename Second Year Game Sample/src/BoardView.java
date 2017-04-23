import java.awt.Point;
import java.util.ArrayList;

/**
 * An interface for players to access the board. 
 */
public class BoardView implements Cloneable {
	private Board board;
	
	/**
	 * Constructs a BoardView object with the Board to use.
	 * @param board Board to use.
	 */
	public BoardView(Board board) {
		this.board = board;
	}
	
	/**
	 * Returns the Disc object located at (row, col).
	 * @param row Row component of coordinates.
	 * @param col Column component of coordinates.
	 * @return Disc object located at (row, col).
	 */
	public Disc getDiscAt(int row, int col) {
		return board.getDiscAt(row, col);
	}
	
	/**
	 * Return true if the specified move is valid, false otherwise.
	 * @param move Move to check.
	 * @return True if inserting a disc with move is valid, false
	 *  otherwise.
	 */
	public boolean isValidMove(Move move) {
		return board.isValidMove(move);
	}
	
	public int getRows() {
		return board.getBoardRow();
	}

	public int getCols() {
		return board.getBoardCol();
	}
    
    /**
     * Gets the row of a move object.
     * The move is not performed.
     * @param move Move object 
     * @return The row corresponding to the move
     */
	public int previewRowMove(Move move) {
		int row, col = move.getCol();

		for (row = 0; row < board.getBoardRow() - 1; row++) {
			if (board.getDiscAt(row+1, col) != null) {
				// Cannot go further down the column.
				return row;
			}
		}
        
		return row;
	}

	/**
	 * Creates a replica of a board object.
	 */
	public Board clone() {
		Board temp = new Board();
		
		for(int i = temp.getBoardRow() -1; i >= 0; i--) {
			 for(int j = 0; j < temp.getBoardCol(); j++) {
				 if(board.getDiscBoard()[i][j] != null) {
					 temp.insertMove(new Move(j), board.getDiscAt(i, j));
				 }
			 }
		}
		return temp;
	}
	
	/**
	 * Get winning disc array from the board.
	 * @return The array list of the winning discs.
	 */
	public ArrayList<Point> getWinningDiscs() {
		return board.getWinningDiscs();
	}
}
