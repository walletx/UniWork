import java.util.Random;

/*
 * Hopefully we can work towards something that will use one of the connect four
 * analysis strategies out there.
 * 
*/

public class HarderAIBehaviour implements MoveBehaviour{
	
	private int tally;
	private int startrow;
	private int startcol;
	private Move move;
	private Random rn = new Random();

	@Override
	public Move makeMove(BoardView bv, Disc myDisc, Disc otherDisc) {
		
		//Aim for vertical win
		move = checkVertical(bv, myDisc.getColour());
		if(move != null) {System.out.println("VerticalWin"); return move;}
				
		//Check if opponent is close to winning vertically
		move = checkVertical(bv, otherDisc.getColour());
		if(move != null) {System.out.println("VerticalBlock"); return move;}
		
		//Check if opponent is nearly winning diagonally bottom left to top right
		move = checkDiagBLTR(bv, otherDisc.getColour());
		if(move != null) {System.out.println("Diag1"); return move;}

		//Check if opponent is nearly winning diagonally bottom right to top left
		move = checkDiagBRTL(bv, otherDisc.getColour());
		if(move != null) {System.out.println("Diag2"); return move;}
		
		//Check if opponent is nearly winning horizontally and hopefully stop that
		move = checkHorizontal(bv, otherDisc.getColour());
		if(move != null) {System.out.println("HorizontalBlock"); return move;}
		
		//Aim for horizontal win
		move = checkHorizontal(bv, myDisc.getColour());
		if(move != null) {System.out.println("HorizontalWin"); return move;}
		
		//Check if opponent is nearly winning diagonally bottom left to top right
		move = checkDiagBLTR(bv, otherDisc.getColour());
		if(move != null) {System.out.println("Diag1Win"); return move;}

		//Check if opponent is nearly winning diagonally bottom right to top left
		move = checkDiagBRTL(bv, otherDisc.getColour());
		if(move != null) {System.out.println("Diag2Win"); return move;}
	
		//If all else is futile we go back to random moves
		move = new Move(rn.nextInt(bv.getCols()));
		System.out.println("Random!");
		return move;
	}
	
	private Move checkVertical (BoardView bv, Disc.Colour toCheck) {
		Move m;
		for(int col = 0; col < bv.getCols(); col++) {
			tally = 1;
			startcol = 0;
			for(int row = 1; row < bv.getRows(); row++) {
				try {
					if (bv.getDiscAt(row, col) == bv.getDiscAt(row - 1,col)
							&& bv.getDiscAt(row, col) != null
							&& bv.getDiscAt(row,col).getColour() == toCheck) {
						startrow = row;
						tally++;
					}
				} catch (NullPointerException e) {
					
				}
			}
			
			if(tally == 3 && startrow >= 3) {
				m = new Move(col);
				if(bv.isValidMove(m) && bv.getDiscAt(startrow - 3, col) == null) {
					return m;
					}
				}
		}
		return null;
	}
	
	private Move checkHorizontal (BoardView bv, Disc.Colour toCheck) {
		Move m;
		for(int row = 0; row < bv.getRows(); row++) {
			tally = 1;
			startcol = 0;
			for(int col = 1; col < bv.getCols(); col++) {
				try {
					if (bv.getDiscAt(row, col) == bv.getDiscAt(row,col-1)
							&& bv.getDiscAt(row, col) != null
							&& bv.getDiscAt(row,col).getColour() == toCheck) {
						startcol = col;
						tally++;
					}
				} catch (NullPointerException e) {

				}
			}
			
			if(tally == 2) {
				m = new Move(startcol + 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == row) {
						return m;
				}
				
				m = new Move(startcol - 2);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == row) {
						return m;
				}
			}
			
			if(tally >= 3) {
				m = new Move(startcol + 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == row) {
						return m;
					} 
				
				m = new Move(startcol - 3);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == row) { 
						return m;
					}
				}
		}
		return null;
	}

	private Move checkDiagBLTR (BoardView bv, Disc.Colour toCheck) {
		
		Move m;
		for (int row = 3; row < bv.getRows(); row++) {
			tally = 1;
			startcol = 0;
			startrow = 0;
			for(int trow = row - 1, col = 1; trow >= 0; trow--, col++) {
				try {
					if (bv.getDiscAt(trow, col) == bv.getDiscAt(trow + 1, col - 1)
							&& bv.getDiscAt(trow, col) != null
							&& bv.getDiscAt(trow,col).getColour() == toCheck) {
						startcol = col;
						startrow = trow;
						tally++;
					}
				} catch (NullPointerException e) {

				}
			}
			
			if(tally >= 3) {
				m = new Move(startcol + 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 1) {
						return m;
					} 
				
				m = new Move(startcol - 3);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 3) { 
						return m;
					}
				}
		}
		
		for (int row = 0; row < bv.getRows() - 3; row++) {
			tally = 1;
			startcol = 0;
			startrow = 0;
			for(int trow = row + 1, col = bv.getCols() - 2; trow < bv.getRows(); trow++, col--) {
				try {
					if (bv.getDiscAt(trow, col) == bv.getDiscAt(trow - 1, col + 1)
							&& bv.getDiscAt(trow, col) != null
							&& bv.getDiscAt(trow,col).getColour() == toCheck) {
						startcol = col;
						startrow = trow;
						tally++;
					}
				} catch (NullPointerException e) {

				}
				
			}
			
			if(tally >= 3) {
				m = new Move(startcol - 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 1) {
						return m;
					} 
				
				m = new Move(startcol + 3);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 3) { 
						return m;
					}
				}
		}
		return null;
	}

	private Move checkDiagBRTL (BoardView bv, Disc.Colour toCheck) {
		Move m;
		for (int row = 0; row < bv.getRows() - 3; row++) {
			tally = 1;
			startcol = 0;
			startrow = 0;
			for(int trow = row + 1, col = 1; trow < bv.getRows(); trow++, col++) {
				try {
					if (bv.getDiscAt(trow, col) == bv.getDiscAt(trow - 1, col - 1)
							&& bv.getDiscAt(trow, col) != null
							&& bv.getDiscAt(trow,col).getColour() == toCheck) {
						startcol = col;
						startrow = trow;
						tally++;
					}
				} catch (NullPointerException e) {
					
				}
			}
			
			/*if(tally == 2) {
				m = new Move(startcol + 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 1) {
						return m;
				}
				
				m = new Move(startcol - 2);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 2) {
						return m;
				}
			}*/
			
			if(tally >= 3) {
				m = new Move(startcol + 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 1) {
						return m;
					} 
				
				m = new Move(startcol - 3);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 3) { 
						return m;
					}
				}
		}
		
		for (int row = 3; row < bv.getRows(); row++) {
			tally = 1;
			startcol = 0;
			startrow = 0;
			for(int trow = row - 1, col = bv.getCols() - 2; trow >= 0; trow--, col--) {
				try {
					if (bv.getDiscAt(trow, col) == bv.getDiscAt(trow + 1, col + 1)
							&& bv.getDiscAt(trow, col) != null
							&& bv.getDiscAt(trow,col).getColour() == toCheck) {
						startcol = col;
						startrow = trow;
						tally++;
					}
				} catch (NullPointerException e) {
					
				}
			}
			
			/*if(tally == 2) {
				m = new Move(startcol - 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 1) {
						return m;
				}
				
				m = new Move(startcol + 2);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 2) {
						return m;
				}
			}*/
			
			if(tally >= 3) {
				m = new Move(startcol - 1);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow - 1) {
						return m;
					} 
				
				m = new Move(startcol + 3);
				if(bv.isValidMove(m) && bv.previewRowMove(m) == startrow + 3) { 
						return m;
					}
				}
			
		}
		return null;
	}
}
