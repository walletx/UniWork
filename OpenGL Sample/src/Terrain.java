import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;


/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private Dimension mySize;
    private double[][] myAltitude;
    private List<Tree> myTrees;
    private List<Road> myRoads;
    private boolean nightMode;
    private boolean rainMode;
    private List<VBOObject> myVBOObjects;
    private float[] mySunlight;
    private float[][] myRain;
    static int test = 0;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth) {
        mySize = new Dimension(width, depth);
        myAltitude = new double[width][depth];
        myTrees = new ArrayList<Tree>();
        myRoads = new ArrayList<Road>();
        myVBOObjects = new ArrayList<VBOObject>();
        mySunlight = new float[3];
        nightMode = false;
        rainMode = false;
        myRain = new float[width * 2][depth * 2];
        for (float[] i: myRain) {
        	for (float j : i) {
        		j = (float) (6 + Math.random()*2); 
        	}
        }
    }
    
    public Terrain(Dimension size) {
        this(size.width, size.height);
    }

    public Dimension size() {
        return mySize;
    }

    public List<Tree> trees() {
        return myTrees;
    }

    public List<Road> roads() {
        return myRoads;
    }
    
    public List<VBOObject> VBOObjects() {
        return myVBOObjects;
    }

    public float[] getSunlight() {
        return mySunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        mySunlight[0] = dx;
        mySunlight[1] = dy;
        mySunlight[2] = dz;        
    }
    
    /**
     * Resize the terrain, copying any old altitudes. 
     * 
     * @param width
     * @param height
     */
    public void setSize(int width, int height) {
        mySize = new Dimension(width, height);
        double[][] oldAlt = myAltitude;
        myAltitude = new double[width][height];
        
        for (int i = 0; i < width && i < oldAlt.length; i++) {
            for (int j = 0; j < height && j < oldAlt[i].length; j++) {
                myAltitude[i][j] = oldAlt[i][j];
            }
        }
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return myAltitude[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, double h) {
        myAltitude[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * TO BE COMPLETED
     * 
     * Based on description from 
     * http://bmia.bmt.tue.nl/people/BRomeny/Courses/8C080/Interpolation.pdf
     * 
     * @param x
     * @param z
     * @return
     */
    public double altitude(double x, double z) {
		
    	//Interpolate altitude for trees using squares
    	//Triangle is too annoying to deal with
    	
    	//Round down the given coordinate
    	int x1 = (int) x;
        int z1 = (int) z;
        
        //Get the set of coordinates 1 above
        int x2 = x1 + 1;
        int z2 = z1 + 1;
		
		//Handles edge case when x2,z2 out of bounds
		if(x == mySize.getWidth() - 1){x2 -= 2;}
		if(z == mySize.getHeight() - 1){z2 -= 2;}
        
        //Get the altitude of each point on the square
        double y1 = myAltitude[x1][z1];
    	double y2 = myAltitude[x2][z1];
    	double y3 = myAltitude[x1][z2];
    	double y4 = myAltitude[x2][z2];
    	
    	//Calculate the normalised area of the intersections
    	double NormAreaA = ((x2-x)*(z2-z))/((x2-x1)*(z2-z1));
    	double NormAreaB = ((x-x1)*(z2-z))/((x2-x1)*(z2-z1));
    	double NormAreaC = ((x2-x)*(z-z1))/((x2-x1)*(z2-z1));
    	double NormAreaD = ((x-x1)*(z-z1))/((x2-x1)*(z2-z1));
    	
    	//Interpolate the y-value
    	double altitude = y1 * NormAreaA + y2 * NormAreaB + y3 * NormAreaC + y4 * NormAreaD;
            	        
        return altitude;
    }

    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(double x, double z) {
        double y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        myTrees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(double width, double[] spine) {
        Road road = new Road(width, spine);
        road.setTerrain(this);
        myRoads.add(road);        
    }
    
    public void addVBOObject(float[] vertices, float[] texture) {
        VBOObject vboObject = new VBOObject(vertices, texture);
        vboObject.setTerrain(this);
        myVBOObjects.add(vboObject);      
    }

    /**
     * Initialises the terrain lighting.
     * 
     * @param gl
     */
    public void init(GL2 gl) {
    	
    	// Parts of lighting code borrowed from week 5 lecture code
        // enable lighting, turn on light 0
        gl.glEnable(GL2.GL_LIGHTING);
        gl.glEnable(GL2.GL_LIGHT0);
        
    	// initialise lighting settings
    	float light0DifAndSpec[] = { 0.2f, 0.2f, 0.2f, 1.0f };
    	float globAmb[] = { 0.2f, 0.2f, 0.2f, 1.0f };
    	
    	// light 0 settings
    	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_DIFFUSE, light0DifAndSpec,0);
    	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_SPECULAR, light0DifAndSpec,0);
    	gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_POSITION, mySunlight,0);

    	gl.glEnable(GL2.GL_LIGHT0); 
    	gl.glLightModelfv(GL2.GL_LIGHT_MODEL_AMBIENT, globAmb,0); 
    	gl.glLightModeli(GL2.GL_LIGHT_MODEL_TWO_SIDE, GL2.GL_TRUE);
    	gl.glLightModeli(GL2.GL_LIGHT_MODEL_COLOR_CONTROL, GL2.GL_SEPARATE_SPECULAR_COLOR);
        
        // torch lighting settings 
    	float lightAmb[] = {0.0f, 0.0f, 0.0f, 1.0f};
    	float light1DifAndSpec[] = {0.5f, 0.5f, 0.5f, 1.0f};

    	// light 1 settings
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightAmb,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_DIFFUSE, light1DifAndSpec,0);
    	gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, light1DifAndSpec,0);
    	
    	// Material properties.
        float[] rgba = {1.0f, 1.0f, 1.0f, 1.0f};
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_AMBIENT, rgba, 0);
        gl.glMaterialfv(GL.GL_FRONT, GL2.GL_SPECULAR, rgba, 0);
        gl.glMaterialf(GL.GL_FRONT, GL2.GL_SHININESS, 0.5f);
    }
    
    public void draw(GL2 gl) {
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        gl.glMatrixMode(GL2.GL_MODELVIEW);
        
        float normalAmb[] = { 0.6f, 0.6f, 0.6f, 1.0f };
        float nightAmb[] = { 0.05f, 0.05f, 0.05f, 1.0f };
    	
    	// Change ambient lighting depending on night mode
    	if (nightMode) {
    		gl.glEnable(GL2.GL_LIGHT1);
    		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, nightAmb,0);
    		
    	} else {
    		gl.glDisable(GL2.GL_LIGHT1);
    		gl.glLightfv(GL2.GL_LIGHT0, GL2.GL_AMBIENT, normalAmb,0);
    	}
       
    	gl.glTexEnvf(GL2.GL_TEXTURE_ENV, GL2.GL_TEXTURE_ENV_MODE, GL2.GL_MODULATE);
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("terrain"));
        
        //Draw mesh with triangle
        //Calculate normals for each surface - this isn't needed until lighting
        
    	for (int x = 0; x < mySize.getHeight() - 1; x++) {
    		
			for (int z = 0; z < mySize.getWidth() - 1; z++) {
				
				//calculate first face normal
				
				double[] vecU1 = {0, myAltitude[x][z+1] - myAltitude[x][z], 1};
				double[] vecV1 = {1, myAltitude[x+1][z] - myAltitude[x][z], 0};
				
				double[] normal = new double[3];
				normal[0] = vecU1[1] * vecV1[2] - vecU1[2] * vecV1[1];
				normal[1] = vecU1[2] * vecV1[0] - vecU1[0] * vecV1[2];
				normal[2] = vecU1[0] * vecV1[1] - vecU1[1] * vecV1[0];
				
				// individual triangle drawn method
            	gl.glBegin(GL2.GL_POLYGON);
    			gl.glNormal3d(normal[0], normal[1], normal[2]);
    			gl.glTexCoord2d(0, 1);
            	gl.glVertex3d(x, myAltitude[x][z], z);
    			gl.glTexCoord2d(1, 0);
            	gl.glVertex3d(x, myAltitude[x][z+1], z+1);
    			gl.glTexCoord2d(1, 1);
            	gl.glVertex3d(x+1, myAltitude[x+1][z], z);
        		gl.glEnd();
        		
				//calculate second face normal
				
        		double[] vecU2 = {-1, myAltitude[x][z+1] - myAltitude[x+1][z], 1};
        		double[] vecV2 = {0, myAltitude[x+1][z+1] - myAltitude[x+1][z], 1};
				
				normal[0] = vecU2[1] * vecV2[2] - vecU2[2] * vecV2[1];
				normal[1] = vecU2[2] * vecV2[0] - vecU2[0] * vecV2[2];
				normal[2] = vecU2[0] * vecV2[1] - vecU2[1] * vecV2[0];
        		
        		gl.glBegin(GL2.GL_POLYGON);
        		gl.glNormal3d(normal[0], normal[1], normal[2]);
    			gl.glTexCoord2d(1, 1);
            	gl.glVertex3d(x+1, myAltitude[x+1][z], z);
    			gl.glTexCoord2d(1, 0);
            	gl.glVertex3d(x, myAltitude[x][z+1], z+1);
    			gl.glTexCoord2d(0, 0);
            	gl.glVertex3d(x+1, myAltitude[x+1][z+1], z+1);
        		gl.glEnd();
        		
			}
			
    	}
    	
        //Draw all the trees
    	for (Tree t : myTrees) {
    		t.draw(gl);
    	}
    	
        gl.glEnable(GL.GL_CULL_FACE);
        //gl.glCullFace(GL.GL_BACK);

    	for (Road r: myRoads) {
    		r.draw(gl);
    	}
    	
    	gl.glDisable(GL.GL_CULL_FACE);
    	
    	for (VBOObject v: myVBOObjects) {
    		v.initialise(gl);
        	v.draw(gl);
    	}
    	
    	if (rainMode) {
        	drawRain(gl);
    	}
    }
    
    /**
     * Draws the rain particle effects.
     * @param gl
     */
    public void drawRain(GL2 gl) {
    	gl.glDisable(GL2.GL_TEXTURE_2D);
    	gl.glDisable(GL2.GL_LIGHTING);
    	// rain particles are updated each frame 
    	// moving at a constant speed 
    	for (int x = 0; x < myRain.length - 1; x++) {
			for (int z = 0; z < myRain[x].length - 1; z++) {
			      gl.glColor3f(173/255f, 216/255f, 230/255f);
			      gl.glBegin(GL2.GL_LINES);
			      gl.glVertex3f(x/2, myRain[x][z], z/2);
			      gl.glVertex3f(x/2, myRain[x][z]+0.2f, z/2);
			      gl.glEnd();
			      myRain[x][z] -= 0.15;
			      
			      if (myRain[x][z] < 0) {
			    	  // after reaching 0, the particle resets from a random
			    	  // location within a certain range 
			    	  myRain[x][z] = (float) (6 + Math.random() * 3); 
			      }
			}
    	}
    	gl.glEnable(GL2.GL_TEXTURE_2D);
    	gl.glEnable(GL2.GL_LIGHTING);
    }
    
    /**
     * Switches between day and night time.
     */
    public void switchNightMode() {
    	nightMode ^= true;
    }
    
    /**
     * Returns a boolean for whether it is night time or not.
     * 
     * @return boolean
     */
    public boolean isNightMode() {
    	return nightMode;
    }

    /**
     * Switches the rain on and off.
     */
	public void switchRain() {
		rainMode ^= true;
	}
}
