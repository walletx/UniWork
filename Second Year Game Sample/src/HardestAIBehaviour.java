import java.util.ArrayList;
import java.util.Random;

public class HardestAIBehaviour implements MoveBehaviour{

	@Override
	public Move makeMove(BoardView bv, Disc myDisc, Disc otherDisc) {
		State currState = new State(bv, null);
		
		minimax(currState, 6, true, myDisc, otherDisc);
		
		int value = Integer.MIN_VALUE;
		Move m = null;
		State chosenChild = null;
		
		for(State child : currState.getChildrenState()) {
			if(child.getValue() > value) {
				value = child.getValue();
				m = child.getMove();
				chosenChild = child;
			}
			Display.displayBV(child.getBoardView());
			System.out.println(child.getValue() + " " + child.getMove().getCol());
		}
		
		for(State c : chosenChild.getChildrenState()) {
			Display.displayBV(c.getBoardView());
			System.out.println(c.getValue());
		}
		
		return m;
	}
	
	public int minimax(State state, int depth, boolean side, Disc aiDisc, Disc humanDisc) {
		if(state.getWin() == true && side == false) {
			//System.out.println("HUMAN WIN");
			//Display.displayBV(state.getBoardView());
			return 10000 * depth;
		}
		
		if(state.getWin() == true && side == true) {
			//System.out.println("AI WIN");
			//Display.displayBV(state.getBoardView());
			return -10000 * depth;
		} 
		
		if(depth == 0) {
			//return evaluateBoard(state, aiDisc);
			return 0;
		}
		
		//ai side
		if(side == true) {
			initChildStates(state, aiDisc);
			int bestValue = Integer.MIN_VALUE;
			for(State child : state.getChildrenState()) {
				int val = minimax(child, depth - 1, false, aiDisc, humanDisc);
				bestValue = Math.max(bestValue, val);
			}
			state.setValue(bestValue);
			return bestValue;
		} else { 
			//human side
			initChildStates(state, humanDisc);
			int bestValue = Integer.MAX_VALUE;
			for(State child : state.getChildrenState()) {
				int val = minimax(child, depth-1, true, aiDisc, humanDisc);
				bestValue = Math.min(bestValue,  val);
			}
			state.setValue(bestValue);
			return bestValue;
		}		
	}

	public int evaluateBoard(State state, Disc myDisc) {
		int finalValue = 0;
		int aiScore = 0;
		int humanScore = 0;
		
		BoardView bv = state.getBoardView();
		
		for(int i = 0; i < bv.getRows(); i++) {
			for(int j = 0; j < bv.getCols(); j++) {
				if(bv.getDiscAt(i, j) == null) continue;
				
				if(bv.getDiscAt(i, j) != null && bv.getDiscAt(i, j).getColour().equals(myDisc.getColour())) {
					if(spotsLeftOf(bv, myDisc, i, j) > 3) {
						if(canInsertAtRow(bv, i, j-1)) {
							aiScore++;
							humanScore = 0;
						}
					}
					if(spotsRightOf(bv, myDisc, i, j) > 3) {
						if(canInsertAtRow(bv, i, j+1)) {
							aiScore++;
							humanScore = 0;
						}
					}
				}
				
				if(bv.getDiscAt(i, j) != null && !bv.getDiscAt(i, j).getColour().equals(myDisc.getColour())) {
					if(spotsLeftOf(bv, myDisc, i, j) > 3) {
						if(canInsertAtRow(bv, i, j-1)) {
							humanScore++;
							aiScore = 0;
						}
					}
					if(spotsRightOf(bv, myDisc, i, j) > 3) {
						if(canInsertAtRow(bv, i, j+1)) {
							humanScore++;
							aiScore = 0;
						}
					}
				}
			}
			
			finalValue = calculateScore(finalValue, humanScore, aiScore);
			humanScore = 0;
			aiScore = 0;
		}
		
		for(int j = 0; j < bv.getCols(); j++) {
			for(int i = bv.getRows() - 1; i >= 0; i--) {
				if(bv.getDiscAt(i, j) != null && bv.getDiscAt(i, j).getColour().equals(myDisc.getColour())) {
					aiScore++;
					humanScore = 0;
					
					if(i <= 2 && (humanScore == 1 || aiScore == 1)) {
						humanScore = 0;
						aiScore = 0;
					}
				}
				
				if(bv.getDiscAt(i, j) != null && !bv.getDiscAt(i, j).getColour().equals(myDisc.getColour())) {
					humanScore++;
					aiScore = 0;
					
					if(i <= 2 && (humanScore == 1 || aiScore == 1)) {
						humanScore = 0;
						aiScore = 0;
					}
				}
			}
			/*System.out.println("Column: " + j);
			System.out.println("HumanScore: " + humanScore);
			System.out.println("AIScore: " + aiScore);
			Display.displayBV(bv);*/
			finalValue = calculateScore(finalValue, humanScore, aiScore);
			
			humanScore = 0;
			aiScore = 0;
		}
		
		//System.out.println("HumanScore: " + humanScore);
		//System.out.println("AIScore: " + aiScore);
		//Display.displayBV(bv);
		return finalValue;
	}
	
	public int calculateScore(int finalValue, int humanScore, int aiScore) {
		if(humanScore == 1) {
			finalValue -= 10;
		} else if(humanScore == 2) {
			finalValue -= 60;
		} else if(humanScore == 3) {
			finalValue -= 2500;
		} else if(humanScore == 4) {
			finalValue -= 5000;
		} else if(humanScore == 5) {
			finalValue -= 20000;
		}
		
		if(aiScore == 1) {
			finalValue += 10;
		} else if(aiScore == 2) {
			finalValue += 50;
		} else if(aiScore == 3) {
			finalValue += 200;
		} else if(aiScore == 4) {
			finalValue += 1000;
		} else if(aiScore == 5) {
			finalValue += 10000;
		}
		return finalValue;
	}
	
	/**
	 * Given a boardview, row and column.
	 * Checks if a move can be inserted at that row.
	 * For it to be valid, the current position must be null
	 * and the position underneath must be occupied.
	 * 
	 * @param bv	The current boardview
	 * @param row	The intended row to insert
	 * @param col	The intended column to insert
	 * @return True if can insert at row, false otherwise
	 */
	public boolean canInsertAtRow(BoardView bv, int row, int col) {
		if(col == -1 || col == 7) return false;
		if(row == 5 && bv.getDiscAt(row, col) == null) return true;
		else if(row == 5) return false;
		
		if(bv.getDiscAt(row+1, col) != null && bv.getDiscAt(row, col) == null) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * Given a boardview, disc, row and column.
	 * Checks the number of spots to the left of the designated
	 * row and column
	 * 
	 * @param bv The current boardview
	 * @param myDisc The AI's disc object
	 * @param row The intended row to insert
	 * @param col The intended column to insert
	 * @return The number of spots to the left of our current spot
	 */
	public int spotsLeftOf(BoardView bv, Disc myDisc, int row, int col) {
		int spotsLeft = 0;
		for(int i = col; i >= 0; i--) {
			if(bv.getDiscAt(row, i) != null && 
					!bv.getDiscAt(row, i).getColour().equals(myDisc.getColour())) {
				return 0;
			}
			
			if(bv.getDiscAt(row, i) == null 
					|| bv.getDiscAt(row, i).getColour().equals(myDisc.getColour())) {
				spotsLeft++;
			}
		}
		return spotsLeft;
	}
	
	/**
	 * Given a boardview, disc, row and column.
	 * Checks the number of spots to the right of the designated
	 * row and column
	 * 
	 * @param bv The current boardview
	 * @param myDisc The AI's disc object
	 * @param row The intended row to insert
	 * @param col The intended column to insert
	 * @return The number of spots to the right of our current spot
	 */
	public int spotsRightOf(BoardView bv, Disc myDisc, int row, int col) {
		int spotsRight = 0;
		for(int i = col; i < bv.getCols(); i++) {
			if(bv.getDiscAt(row, i) != null && 
					!bv.getDiscAt(row, i).getColour().equals(myDisc.getColour())) {
				return 0;
			}
			
			if(bv.getDiscAt(row, i) == null 
					|| bv.getDiscAt(row, i).getColour().equals(myDisc.getColour())) {
				spotsRight++;
			}
		}
		return spotsRight;
	}
	
	public void initChildStates(State state, Disc disc) {
		ArrayList<Integer> listOfMoves;
		boolean win;
		
		listOfMoves = createRandomMoves();
		
		//for(int i = 0; i < state.getBoardView().getCols(); i++) {
		for(Integer i : listOfMoves) {
			Board cloneBoard = state.getBoardView().clone();
			
			//System.out.println("CLONED!!!");
			//Display.displayBoard(cloneBoard);
			
			Move move = new Move(i);
			
			if(cloneBoard.isValidMove(move)) {
				int row = cloneBoard.insertMove(move, disc);
				win = cloneBoard.checkWin(row, i);
				
				BoardView clonebv = new BoardView(cloneBoard);
				State child = new State(clonebv, move);
				state.addChildState(child);
				
				child.setWin(win);
				

				//System.out.println("NEW!!!");
				//Display.displayBoard(cloneBoard);
			}
		}
	}
	
	public ArrayList<Integer> createRandomMoves() {
		ArrayList<Integer> listOfMoves = new ArrayList<Integer>();
		
		while(listOfMoves.size() != 7) {
			Random random = new Random();
			int rNum = random.nextInt(7);
			
			if(!listOfMoves.contains(rNum)) {
				listOfMoves.add(rNum);
			}
		}
		return listOfMoves;
	}
}
