
import javax.swing.*;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;


/**
 * Button class, creates a green, curved button
 * for GUI use.
 */
public class Button extends JButton {
	private Color backColor;
    private int x;
    private int y;
    private boolean isPressed;
    
    public Button(String title, int x, int y) {
        super(title);
        setPreferredSize(new Dimension(x, y) );
        this.x = x;
        this.y = y;
        backColor = new Color(104, 172, 139);
        isPressed = false;
    }


    /**
     * Paints the button.
     */
    @Override
    public void paint(Graphics g) {
    	Graphics2D g2d;
    	g2d = (Graphics2D) g;
    	
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
    	
        g2d.setColor(backColor);
        
        RoundRectangle2D r = new RoundRectangle2D.Double(0, 0, 200, 75, 25, 25);
        g2d.fill(r);

        g2d.setColor(Color.white);
        g2d.setFont( new Font("Impact", Font.PLAIN, 36));
        int lengthOfString = (int) g2d.getFontMetrics().getStringBounds(getText(), g2d).getWidth();
        int start = x/2 - lengthOfString/2;
        
        g2d.drawString(getText(), start, (int) (y*0.7));
    }
    
    /**
     * A selected button is pressed, and also
     * the background colour is set to a yellow tint.
     */
    public void setSelected() {
    	backColor = new Color(220, 169, 50);
    	isPressed = true;
    }
    
    /**
     * A button is unselected is not pressed,
     * and the background colour is set back to
     * the original green.
     */
    public void setUnselect() {
    	backColor = new Color(104, 172, 139);
    	isPressed = false;
    }
    
    /**
     * Gets the background colour of the button.
     * @return The background colour of the button.
     */
    public Color getBackColor() {
    	return backColor;
    }
    
    /**
     * Gets whether or not the button is pressed.
     * @return True if the button is pressed, otherwise false.
     */
    public boolean getIsPressed() {
    	return isPressed;
    }
    
    /**
     * Sets the background colour.
     * @param color The colour to set.
     */
    public void setBackColor(Color color) {
    	backColor = color;
    }
}
