import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.glu.GLU;

import com.jogamp.opengl.util.gl2.GLUT;


public class Camera implements GLEventListener, KeyListener {  
    private Terrain myTerrain;
    private boolean avatarOn;
    private double depth;
    private double width;
    private double[] cam; // position of the camera
    private double[] lookAt; // what the camera is looking at 
    private double[] avatarPos; // position of the avatar
    private double[] avatarCam; // position of avatarCam
    private double[] avaTorch; // position of torch
    private int slice; // slices for the circle for what the camera looks at
    private double theta;
    private static int totalSlices = 64; // amount of slices for the camera circle
    private static double angleStep = 2 * Math.PI / totalSlices; // how much rotation per slice
    private static double yBase = 0.7; // base height for camera and look at
    private static double lookDist = 0.5; // multiplier for how far the camera should look in world view
    private static double avatDist = 1.5; // multiplier for how far back the camera should look in avatar view
    private static double avatY = 0.1; // base avatar y value
    private static double moveMultiplier = 0.05; // multiplier for how far a single button press moves the camera
    private static final int x = 0;
    private static final int y = 1;
    private static final int z = 2;
  
	public Camera(Terrain t) {
		// initialise attributes 
		this.myTerrain = t;
		this.slice = 1;
		this.depth = myTerrain.size().height;
		this.width = myTerrain.size().width;
		
		// initial camera position is in the centre of the map
		this.cam = new double[3];
		this.cam[x] = width/2;
		this.cam[z] = depth/2;
		this.cam[y] = myTerrain.altitude(cam[x], cam[z]) + yBase;
     
        double angleStep = 2 * Math.PI / totalSlices;
        this.theta = slice * angleStep;
		
        // adjust lookAt according to initial camera position
        this.lookAt = new double[3];
		this.lookAt[z] = Math.sin(theta) * lookDist + cam[z];
        this.lookAt[x] = Math.cos(theta) * lookDist + cam[x];
        this.lookAt[y] = myTerrain.altitude(lookAt[x], lookAt[z]) + yBase;
        
        // world is initialy without avatar mode on
        this.avatarOn = false;
        
        // initial avatar position is in the center of the map
        this.avatarPos = new double[3];
        this.avatarPos[x] = width/2;
        this.avatarPos[z] = depth/2;
        this.avatarPos[y] = myTerrain.altitude(lookAt[x], lookAt[z]) + avatY;
        
        this.avatarCam = new double[3];
        this.avaTorch = new double[3];
	}

    public void keyPressed(KeyEvent e) {
    	 // calculate the potential amount moved by the camera
		 double distX = Math.cos(theta) * moveMultiplier;
		 double distZ = Math.sin(theta) * moveMultiplier;
		 
		 // behaves differently according to the button pressed
		 switch (e.getKeyCode()) {
			 case KeyEvent.VK_LEFT:
				 // move left one slice
				 if (isValidRotation(-1)) {
					 slice -= 1;
					 slice = slice % 64;
				 }
				 break;
	
			 case KeyEvent.VK_RIGHT:
				 // move right one slice
				 if (isValidRotation(1)) {
					 slice += 1;
					 slice = slice % 64;
				 }
				 break;
	
			 case KeyEvent.VK_DOWN:
				 // differing behaviour for when avatar is on or off
				 if (isValidLocation(-distX, -distZ)) {
					 // camera and avatar move by the calculated distance
					 if (avatarOn) {
			            avatarPos[x] -= distX;
			            avatarPos[z] -= distZ;
			            
			            // avatar position set so it is above the terrain
			            avatarPos[y] = myTerrain.altitude(avatarPos[x], avatarPos[z]) + avatY;
					 } else {
						 cam[x] -= distX;
						 cam[z] -= distZ;
						 
						 // cam position moved up by a constant yBase
				         cam[y] = myTerrain.altitude(cam[x], cam[z]) + yBase;
					 }
				 }

				 break;
	
			 case KeyEvent.VK_UP:
				 if (isValidLocation(distX, distZ)) {
					 if (avatarOn) {
			            avatarPos[x] += distX;
			            avatarPos[z] += distZ;
			            avatarPos[y] = myTerrain.altitude(avatarPos[x], avatarPos[z]) + avatY;
					 } else {
						cam[x] += distX;
						cam[z] += distZ;
					    cam[y] = myTerrain.altitude(cam[x], cam[z]) + yBase;
					 }
				 }
				 
				 break;
			 
			 // switch between 1st and 3rd person view
			 case KeyEvent.VK_S:
				 // switch the 1st/3rd person camera positions so that the 
				 // camera moves and looks from where it was before the switch
				 if (avatarOn) {
					 avatarOn = false;
					 cam[x] = avatarPos[x];
					 cam[z] = avatarPos[z];
					 cam[y] = myTerrain.altitude(cam[x], cam[z]) + yBase;
				 } else {
					 avatarOn = true;
					 avatarPos[x] = cam[x];
					 avatarPos[z] = cam[z];
					 avatarPos[y] = myTerrain.altitude(avatarPos[x], avatarPos[z]) + avatY;
				 }
				 break;
				
			 // switches between night mode and day time
			 case KeyEvent.VK_D:
				 myTerrain.switchNightMode();
				 break;
			 
			 // switches the rain on and off
			 case KeyEvent.VK_F:
				 myTerrain.switchRain();
				 break;	 
			 default:
				 break;
		 }
		
		// calculate the new angle 
        theta = slice * angleStep;
		
        if (avatarOn) {
        	// avatar position as the base coordinates and then minus by the distance
        	// between avatar and camera
        	avatarCam[z] = avatarPos[z] - Math.sin(theta) * avatDist;
        	avatarCam[x] = avatarPos[x] - Math.cos(theta) * avatDist;
        	
        	// avatar camera is always set to a certain height above the avatar
        	avatarCam[y] = avatarPos[y] + yBase;
        	
        	// calculate avatar torch position
        	if (myTerrain.isNightMode()) {
            	avaTorch[z] = avatarPos[z] + Math.sin(theta) * 0.8;
            	avaTorch[x] = avatarPos[x] + Math.cos(theta) * 0.8;
            	avaTorch[y] = avatarPos[y] + yBase;
        	}
        } else {
        	// look at has the camera position as a base and then distance added to it
        	lookAt[z] = Math.sin(theta) * lookDist + cam[z];
            lookAt[x] = Math.cos(theta) * lookDist + cam[x];
            lookAt[y] = myTerrain.altitude(lookAt[x], lookAt[z]) + yBase;
        }
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		GLU glu = new GLU();
		
	       
			// place our torch lighting if it is night mode
			if (myTerrain.isNightMode()) {
				// Some code borrowed from Week 5 lecture spotlighting
				float spotAngle = 10.0f; // Spotlight cone half-angle.
				float spotDirection[] = {0.0f, -1.0f, 0.0f}; // Spotlight direction.
				float spotExponent = 2.0f; // Spotlight exponent = attenuation factor.
		        gl.glMatrixMode(GL2.GL_MODELVIEW);
		        gl.glLoadIdentity();
				gl.glPushMatrix();
				
				// the position of the light changes depending on camera mode
				if (avatarOn) {
		        	gl.glTranslated(avaTorch[x], avaTorch[y], avaTorch[z]);
				} else {
					gl.glTranslated(lookAt[x], lookAt[y], lookAt[z]);
				}
				
		    	float lightPos[] = {0.0f, 4.0f, 0.0f, 1.0f}; // Spotlight position.
		    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);  
		
		    	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_CUTOFF, spotAngle);
		    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPOT_DIRECTION, spotDirection,0);    
	        	gl.glLightf(GL2.GL_LIGHT1, GL2.GL_SPOT_EXPONENT, spotExponent);
		        
		       gl.glPopMatrix();

			}
		
        gl.glMatrixMode(GL2.GL_PROJECTION);
        gl.glLoadIdentity();
        glu.gluPerspective(60, 4/3, 0.01, 500);
        
        // camera changes depending on whether it is 1st or 3rd person view
    	if (avatarOn) {
            glu.gluLookAt(avatarCam[x], avatarCam[y], avatarCam[z], avatarPos[x], avatarPos[y], avatarPos[z], 0, 1, 0);
    	} else {
            glu.gluLookAt(cam[x], cam[y], cam[z], lookAt[x], lookAt[y], lookAt[z], 0, 1, 0);
    	}
    	
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        gl.glLoadIdentity();
    	
       if (avatarOn) {
			// place avatar
			gl.glPushMatrix();
			gl.glLoadIdentity();
			gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("avatar"));
			GLUT glut = new GLUT();
			
			// rotate and move the avatar according to current position and rotation
			gl.glTranslated(avatarPos[x], avatarPos[y], avatarPos[z]);
			gl.glRotated(Math.toDegrees(-theta), 0, 1, 0);
			gl.glFrontFace(GL2.GL_CW);
			glut.glutSolidTeapot(0.2);
			gl.glFrontFace(GL2.GL_CCW);
			gl.glPopMatrix();
       }
	}

	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable arg0) {
		
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
		// change the aspect ratio if there is a reshape
		GLU glu = new GLU();
		glu.gluPerspective(60, width/height, 0.01, 500);
	}
	
	/**
	 * Determines whether moving a certain distance will be out of bounds of
	 * the world.
	 * 
	 * @param distX
	 * @param distZ
	 * @return boolean
	 */
	private boolean isValidLocation(double distX, double distZ) {
		double xCheck;
		double zCheck;
		
		// valid location changes depending on whether it is avatar mode or not
		if (avatarOn) {
			xCheck = distX + avatarPos[x];
			zCheck = distZ + avatarPos[z]; 
		} else {
			xCheck = distX + cam[x];
			zCheck = distZ + cam[z];
		}
        
        return (zCheck > 0 && xCheck > 0 && zCheck < width - 1 && xCheck < depth - 1);
	}
	
	/**
	 * Determines whether rotating a certain number of slices will be out of bounds
	 * of the world. Only applies to 1st person mode.
	 * 
	 * @param i
	 * @return boolean
	 */
	private boolean isValidRotation(int i) {        
        // valid rotation changes depending on whether it is avatar mode or not
        if (!avatarOn) {
    		double xCheck = 0;
    		double zCheck = 0;
    		
            double thetaCheck = (slice + i) * angleStep;
        	zCheck = Math.sin(thetaCheck) * lookDist + cam[z];
        	xCheck = Math.cos(thetaCheck) * lookDist + cam[x];

            return (zCheck > 0 && xCheck > 0 && zCheck < width - 1 && xCheck < depth - 1);
        } else {
        	return true;
        }
        
	}
}
