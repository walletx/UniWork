import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {

    private double[] myPos;
    private GLU glu;
    private GLUquadric quad;
    
    public Tree(double x, double y, double z) {
        myPos = new double[3];
        myPos[0] = x;
        myPos[1] = y;
        myPos[2] = z;
        glu = new GLU();
    }
    
    public double[] getPosition() {
        return myPos;
    }
    
    public void draw(GL2 gl) {
    	
    	double yB = myPos[1]; // y Base
    	double yH = yB + 1; // y Height
    	
    	double xB = myPos[0]; // x Base
    	double zB = myPos[2]; // z Base
    	
        gl.glPushMatrix();
        gl.glLoadIdentity();
        
        gl.glTranslated(xB, yH, zB);
        gl.glRotated(90, 1, 0, 0);
        
        // Create a tree using glu quadrics
        quad = glu.gluNewQuadric();
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("treetrunk"));   
        glu.gluQuadricTexture(quad, true);
        glu.gluCylinder(quad, 0.15, 0.15, 1, 32, 32);
        
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("treetop"));
        glu.gluQuadricTexture(quad, true);
        glu.gluSphere(quad, 0.4, 40, 40);
        gl.glPopMatrix();
    }
}
