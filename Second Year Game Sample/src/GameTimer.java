import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JLabel;

public class GameTimer implements ActionListener {
	static private int MS_FONT_SIZE = 5;
	
	private State state;
	private JLabel timerLabel;
	private int time;  // In ms.
	
	/**
	 * The state of the timer.
	 */
	public enum State {
		STOP, START;
	}
	
	/**
	 * Constructs a GameTimer object associated with timerLabel, sets
	 * the state to STOP, the time to 0, and writes the time.
	 * @param timerLabel JLabel object that holds the time (object to
	 *  write to).
	 */
	public GameTimer(JLabel timerLabel) {
		this.timerLabel = timerLabel;
		state = State.STOP;
		time = 0;
		
		writeTime();
	}
	
	public State getState() {
		return state;
	}
	
	public void setTimerLabel(JLabel timerLabel) {
		this.timerLabel = timerLabel;
	}
	
	@Override
	public void actionPerformed(ActionEvent a) {
		if (state.equals(State.STOP)) {
		} else {
			// Increment by 1 ms.
			time++;
		}
		
		writeTime();
	}

	/**
	 * Keeps the time and sets the state to STOP.
	 */
	public void stop() {
		state = State.STOP;
		writeTime();
	}
	
	/**
	 * Keeps the time and sets the state to START.
	 */
	public void start() {
		state = State.START;
		writeTime();
	}
	
	private String iTo2Dig(int n) {
		if (n < 10) return "0" + Integer.toString(n);
		else return Integer.toString(n);
	}
	
	private void writeTime() {
		int s = time / 100;
		int ms = time % 100;
		int min = s / 60;
		s = s % 60;
		int h = min / 60;
		h = h % 60;
		
		timerLabel.setText("<html>" + iTo2Dig(h) + ":" + iTo2Dig(min) + ":"
		                   + iTo2Dig(s) + ":" + "<font size='" + MS_FONT_SIZE
		                   + "'>." + iTo2Dig(ms) + "</font>");
	}
}
