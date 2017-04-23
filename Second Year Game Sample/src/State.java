import java.util.ArrayList;

/**
 * State class used for HardestAIBehaviour.java
 */
public class State {
	private ArrayList<State> childrenState;
	private BoardView bv;
	private int value;
	private Move move;
	private boolean win;
	
	/**
	 * Constructor for State
	 * 
	 * @param bv The current board view
	 * @param move The last move associated with this state
	 */
	public State(BoardView bv, Move move) {
		childrenState = new ArrayList<State>();
		this.bv = bv;
		this.move = move;
	}
	
	/**
	 * Add a child state to this object's children state
	 * @param child Child to add
	 */
	public void addChildState(State child) {
		childrenState.add(child);
	}
	
	/**
	 * Returns the childrenState array list.
	 * @return The childrenState array list.
	 */
	public ArrayList<State> getChildrenState() {
		return childrenState;
	}

	/**
	 * Gets the value of the state.
	 * @return Value of the state.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the value of the state.
	 * @param value Value to set.
	 */
	public void setValue(int value) {
		this.value = value;
	}
	
	/**
	 * Gets the boardview of the state.
	 * @return The boardview of the state.
	 */
	public BoardView getBoardView() {
		return bv;
	}
	
	/**
	 * Gets the move associated with this state.
	 * @return The move of this state.
	 */
	public Move getMove() {
		return move;
	}
	
	/**
	 * Set whether or not this state is a winning state.
	 * @param b Boolean value to set if won or not.
	 */
	public void setWin(boolean b) {
		win = b;
	}
	
	/**
	 * Get whether or not this state is a winning state.
	 * @return True if this state is a winning state, else false.
	 */
	public boolean getWin() {
		return win;
	}
}
