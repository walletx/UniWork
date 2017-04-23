//NOTE: You could of course replace the entire inner loop with the commented out displayRow() if you think it's better
//NOTE: Feel free to change or scrap the entire thing if you hate it
//NOTE: This thing has to read the entire board each time (another way might be to maintain a different board representation in Display and parse in a Move instead)
//NOTE: Of course this is only temporary so might not need to worry about it that much

public class Display {
	
	private static char RED_COUNTER = 'R';
	private static char YELLOW_COUNTER = 'Y';
	private static char NO_COUNTER = ' ';
	
	/**
	 * Displays the terminal version of the board.
	 * Given a board object, this functions displays the
	 * current state of the board and outputs it into the 
	 * terminal.
	 * @param board Current state of the board.
	 */
	public static void displayBoard(Board board) {
		Disc disc;
		char toPrint = ' ';
		//displayBorder();
		for (int row = 0; row < board.getBoardRow(); row++) {
			//System.out.print("|");
			for (int col = 0; col < board.getBoardCol(); col++) {
				System.out.print("|");
				disc = board.getDiscAt(row,col);
				if (disc == null) {
					toPrint = NO_COUNTER;
				} else {
					switch (disc.getColour()) {
					case RED: toPrint = RED_COUNTER;
						break;
					case YELLOW: toPrint = YELLOW_COUNTER;
						break;
					}
				}
				System.out.print(toPrint);
			}
			System.out.println("|");
		}
		//displayBorder();
	}
	
	/**
	 * Displays the terminal version of the boardview.
	 * Given a boardview object, this function displays
	 * the current state of the board and outputs it into
	 * the terminal
	 * @param board Current state of the board view.
	 */
	public static void displayBV(BoardView board) {
		Disc disc;
		char toPrint = ' ';
		//displayBorder();
		for (int row = 0; row < board.getRows(); row++) {
			//System.out.print("|");
			for (int col = 0; col < board.getCols(); col++) {
				System.out.print("|");
				disc = board.getDiscAt(row,col);
				if (disc == null) {
					toPrint = NO_COUNTER;
				} else {
					switch (disc.getColour()) {
					case RED: toPrint = RED_COUNTER;
						break;
					case YELLOW: toPrint = YELLOW_COUNTER;
						break;
					}
				}
				System.out.print(toPrint);
			}
			System.out.println("|");
		}
		//displayBorder();
	}
}
