import java.util.Random;

public class NoviceAIBehaviour implements MoveBehaviour {
	public Move makeMove(BoardView bv, Disc myDisc, Disc otherDisc) {
		// Sets column in m to a random number from 0-#columns.
		Random rn = new Random();
		Move m = new Move(rn.nextInt(bv.getCols()));
		
		try {
			Thread.sleep(500);
		} catch (java.lang.InterruptedException e) {}
		
		return m;
	}
}