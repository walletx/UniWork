import java.awt.Point;
import java.util.ArrayList;

/**
 * Board objects maintain a Connect 4 board and allow for the
 * insertion of discs into columns.
 */
public class Board implements Cloneable {
	private static int ROWS = 6;
	private static int COLS = 7;
	private static int WIN_LEN = 4;
	
	private Disc[][] board;
	
	private ArrayList<Point> winningDiscs;
	
	/**
	 * Constructs an empty Board object.
	 */
	public Board() {
		board = new Disc[ROWS][COLS];
		winningDiscs = new ArrayList<Point>();
		
		// Initialise the board to contain all null.
		for (int row = 0, col; row < ROWS; row++) {
			for (col = 0; col < COLS; col++) {
				board[row][col] = null;
			}
		}
	}
	
	/**
	 * Returns the Disc object located at (row, col).
	 * @param row Row component of coordinates.
	 * @param col Column component of coordinates.
	 * @return Disc object located at (row, col).
	 */
	public Disc getDiscAt(int row, int col) {
		return board[row][col];
	}
	
	/**
	 * Returns true if the board is full.
	 * @return True if the entire board is filled with Disc objects,
	 *  false otherwise.
	 */
	public boolean isFull() {
		for (int i = 0; i < ROWS; i++) {
			for (int j = 0; j < COLS; j++) {
				if (board[i][j] == null) return false;
			}
		}
		
		return true;
	}
	
	/**
	 * Return true if the specified move is valid, false otherwise.
	 * The move is valid if the position in the top row at column col
	 * is empty (there is still room for the disc).
	 * @param move Move to check validity for.
	 * @return True if inserting a disc at col is a valid move, false
	 *  otherwise.
	 */
	public boolean isValidMove(Move move) {
		return move.getCol() >= 0 && move.getCol() < COLS
		       && (board[0][move.getCol()] == null);
	}
	
	/**
	 * Insert disc into the board according to move (i.e. at the
	 * specified column in the Move object).
	 * @.precondition move is a valid move (can be determined by
	 *  the isValidMove method).
	 * @param move Move to make.
	 * @param disc Disc to insert.
	 * @return The row in which the disc was inserted into.
	 */
	public int insertMove(Move move, Disc disc) {
		int row, col = move.getCol();

		for (row = 0; row < ROWS - 1; row++) {
			if (board[row + 1][col] != null) {
				// Cannot go further down the column.
				board[row][col] = disc;
				return row;
			}
		}

		// disc is to go in the bottom row (ROWS - 1).
		board[row][col] = disc;
		
		return row;
	}
	
	/**
	 * Return true if there exists a run of 4 same coloured Discs
	 * around (row, col) (in any direction), false otherwise. This
	 * method should be called after every insertion on the location
	 * of insertion.
	 * @param row Row component of coordinates.
	 * @param col Column component of coordinates.
	 * @return True if the game is complete (search only around
	 *  coordinates), otherwise false.
	 */
	public boolean checkWin(int row, int col) {
		// The disc to check for.
		Disc disc = board[row][col];
		
		// Temporary row/col counters, and current line length counter.
		int tRow, tCol, len = 0;
		
		// Check horizontally, left to right.
		for (tCol = col - (WIN_LEN - 1); tCol < col + WIN_LEN
				                         && tCol < COLS; tCol++) {
			if (tCol < 0) {
				continue;
			} 
			
			if (board[row][tCol] != null && board[row][tCol].equals(disc)) {
				addWinningDisc(row, tCol);
				len++;				
			} else {
				len = 0;
			}

			if (len == WIN_LEN) {
				return true;
			}
		}
		
		len = 0;

		// Check vertically, top to bottom.
		for (tRow = row - (WIN_LEN - 1); tRow < row + WIN_LEN
				                         && tRow < ROWS; tRow++) {
			if (tRow < 0) {
				continue;
			}

			if (board[tRow][col] != null && board[tRow][col].equals(disc)) {
				addWinningDisc(tRow, col);
				len++;
			} else {
				len = 0;
			}

			if (len == WIN_LEN) {
				return true;
			}
		}
		
		len = 0;
		
		// In diagonal checks, the condition of tRow < row +
		// WIN_LEN implies tCol < col + WIN_LEN as
		// they are incremented together starting from same offset.
		
		// Check diagonally, top left to bottom right.
		for (tRow = row - (WIN_LEN - 1), tCol = col - (WIN_LEN - 1);
			 tRow < row + WIN_LEN && tRow < ROWS && tCol < COLS;
			 tRow++, tCol++) {
			if (tRow < 0) {
				continue;
			}
			
			if (tCol < 0) {
				continue;
			}

			if (board[tRow][tCol] != null && board[tRow][tCol].equals(disc)) {
				addWinningDisc(tRow, tCol);
				len++;
			} else {
				len = 0;
			}

			if (len == WIN_LEN) {
				return true;
			}
			
		}

		len = 0;

		// Check diagonally, bottom left to top right.
		for (tRow = row + (WIN_LEN - 1), tCol = col - (WIN_LEN - 1);
			 tRow > row - WIN_LEN && tRow >= 0 && tCol < COLS;
			 tRow--, tCol++) {
			if (tRow > ROWS - 1) {
				continue;
			}
			
			if (tCol < 0) {
				continue;
			}
			
			if (board[tRow][tCol] != null && board[tRow][tCol].equals(disc)){
				addWinningDisc(tRow, tCol);
				len++;
			} else {
				len = 0;
			}

			if (len == WIN_LEN) {
				return true;
			}
		}
		
		// Could not find a run of 4.
		return false;
	}
	
	@Override
	public String toString() {
		String s = "";
		for (int r = 0; r < ROWS; r++) {
			for (int c = 0; c < COLS; c++) {
				s += "[";
				if (board[r][c] == null) {
					s += " ";
				} else {
					switch (board[r][c].getColour()) {
					case YELLOW:
						s += "Y";
						break;
					case RED:
						s += "R";
						break;
					default:
						s += "P";  // It is not a colour.
						break;
					}
				}
				
				s += "] ";
			}
			
			s += "\n";
		}
		
		return s;
	}
    
	/**
	 * Returns the number of rows in the board.
	 * @return Number of rows in the board.
	 */
	public int getBoardRow() {
		return ROWS;
	}
	
	/**
	 * Returns the number of columns in the board.
	 * @return Number of columns in the board.
	 */
	public int getBoardCol() {
		return COLS;
	}
	
	public Disc[][] getDiscBoard() {
		return board;
	}
	
	public void addWinningDisc(int row, int col) {
		Point p = new Point(row, col);
		winningDiscs.add(p);
	}
	
	public void printDiscs() {
		if(winningDiscs.size()>0) {
		for(int i = 0; i < winningDiscs.size(); i++) {
			System.out.println("Point " + i + " X: " + winningDiscs.get(i).getX());
			System.out.println("Point " + i + " Y: " + winningDiscs.get(i).getY());	
		}
	}
	}
	
	public ArrayList<Point> getWinningDiscs() {
		return this.winningDiscs;
	}
	
}
