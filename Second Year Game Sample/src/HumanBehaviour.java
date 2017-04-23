import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;


public class HumanBehaviour implements MoveBehaviour {
	public Move makeMove(BoardView bv, Disc myDisc, Disc otherDisc) {
		// poll for input and make a move on that.
		System.out.print("Enter a column (0-6): ");

		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String col = null;
			
		try {
			//Reads the user input here, as a string, for example "0" or "3" or "6"
			//then it parses it to an integer and creates new move object
			col = br.readLine();
			Move m = new Move(Integer.parseInt(col));
				
			return m;
		} catch (IOException e) {
			System.out.println("Invalid input!");
		}
			
		return null;
	}
}
