import java.io.File;
import java.io.FileNotFoundException;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLProfile;
import javax.media.opengl.awt.GLJPanel;
import javax.swing.JFrame;

import org.json.JSONException;

import com.jogamp.opengl.util.FPSAnimator;



/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
@SuppressWarnings("serial")
public class Game extends JFrame implements GLEventListener{

    private Terrain myTerrain;
    private Camera myCamera;
    
    public Game(Terrain terrain) {
    	super("Assignment 2");
        myTerrain = terrain;
        myCamera = new Camera(myTerrain);
    }
    
    /** 
     * Run the game.
     *
     */
    @SuppressWarnings("unused")
	public void run() {
    	  GLProfile glp = GLProfile.getDefault();
          GLCapabilities caps = new GLCapabilities(glp);
          GLJPanel panel = new GLJPanel();
          panel.addGLEventListener(this);
          panel.addGLEventListener(myCamera);
          panel.addKeyListener(myCamera);
 
          // Add an animator to call 'display' at 60fps        
          FPSAnimator animator = new FPSAnimator(60);
          animator.add(panel);
          animator.start();

          getContentPane().add(panel);
          setSize(800, 600);        
          setVisible(true);
          setDefaultCloseOperation(EXIT_ON_CLOSE);        
    }
    
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     * @throws JSONException 
     */
    public static void main(String[] args) throws FileNotFoundException, JSONException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        Game game = new Game(terrain);
        game.run();
    }

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
        GL2 gl = drawable.getGL().getGL2();
		myTerrain.draw(gl);
	}

	@Override
	public void dispose(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		
	}

	@SuppressWarnings("unused")
	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
    	GL2 gl = drawable.getGL().getGL2();

    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	
    	// initialising the textures
    	Texture terrainTex = new Texture(gl,"terrain","terrain.jpg","jpg");
    	Texture treeTopTex = new Texture(gl, "treetop","treetop.jpg","jpg");
    	Texture treeTrunkTex = new Texture(gl, "treetrunk","treetrunk.jpg","jpg");
    	Texture roadTex = new Texture(gl, "road","road.jpg","jpg");
    	Texture avatarTex = new Texture(gl, "avatar", "avatar.jpg", "jpg");
    	Texture vboTex = new Texture(gl, "vbo", "vbo.jpg", "jpg");
    	
    	gl.glEnable(GL.GL_DEPTH_TEST);

    	myTerrain.init(gl);
         
        gl.glEnable(GL2.GL_NORMALIZE);
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width,
			int height) {
	}
}
