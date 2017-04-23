import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JLabel;

/**
 * The BoardListener listens for hovers and clicks with the mouse
 * in the board. Hovering over a valid JLabel highlights the JLabel,
 * and clicking a JLabel when it is a human players turn, will take
 * that JLabel as the move.
 */
public class BoardListener implements MouseListener {
	private ArrayList<JLabel> columns;
	private BoardPanel bp;
	
	public BoardListener(BoardPanel bp, ArrayList<JLabel> columns) {
		this.columns = columns;
		this.bp = bp;
	}
	
	/**
	 * Detects the mouse click in the corresponding JLabel.
	 * A move object is created via this JLabel and
	 * is sent to the board panel.
	 */
	@Override
	public void mouseClicked(MouseEvent m) {
		if (!bp.playerIsHuman()) return;
		
		if (!(m.getComponent() instanceof JLabel)) return;

		JLabel col = (JLabel) m.getComponent();

		if (col == null) return;
		
		int columnNo = columns.indexOf(col);
		if (columnNo == -1) return;  // Did not enter a JLabel.
		
		bp.sendMove(new Move(columnNo));
	}

	@Override
	public void mouseEntered(MouseEvent m) {
		if (!bp.playerIsHuman()) {
			bp.setHighlight(-1);
			bp.repaint();
			return;
		}

		if (!(m.getComponent() instanceof JLabel)) return;
		
		JLabel col = (JLabel) m.getComponent();

		if (col == null) return;
		
		int columnNo = columns.indexOf(col);
		
		if (bp.getBv().isValidMove(new Move(columnNo))) {
			bp.setHighlight(columnNo);
		} else {
			bp.setHighlight(-1);
		}

		bp.repaint();
	}
	
	@Override
	public void mouseExited(MouseEvent m) {
		Point position = bp.getMousePosition();
		
		if (position == null || !bp.pointOnBoard(position.x, position.y)) {
 		    // In this case the mouse has exited the board.
			bp.setHighlight(-1);
			bp.repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent m) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent m) {
		// TODO Auto-generated method stub
	}
	

}