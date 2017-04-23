import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */

//NOTE: Code involving calculation of Frenet frames and matrix multiplication
// is based on code supplied from the lectures
// Code is adjusted to account for x-z plane rather than x-y

public class Road {
	
	private static final int NUMSEGMENTS = 10;
    private List<Double> myPoints;
    private double myWidth;
    private Terrain myTerrain;
    
    /** 
     * Create a new road starting at the specified point
     */
    public Road(double width, double x0, double y0) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        myPoints.add(x0);
        myPoints.add(y0);
    }

    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(double width, double[] spine) {
        myWidth = width;
        myPoints = new ArrayList<Double>();
        for (int i = 0; i < spine.length; i++) {
            myPoints.add(spine[i]);
        }
    }
    
    public void setTerrain(Terrain terrain) {
    	myTerrain = terrain;
    }

    /**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return myWidth;
    }

    /**
     * Add a new segment of road, beginning at the last point added and ending at (x3, y3).
     * (x1, y1) and (x2, y2) are interpolated as bezier control points.
     * 
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @param x3
     * @param y3
     */
    public void addSegment(double x1, double y1, double x2, double y2, double x3, double y3) {
        myPoints.add(x1);
        myPoints.add(y1);
        myPoints.add(x2);
        myPoints.add(y2);
        myPoints.add(x3);
        myPoints.add(y3);        
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return myPoints.size() / 6;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public double[] controlPoint(int i) {
        double[] p = new double[2];
        p[0] = myPoints.get(i*2);
        p[1] = myPoints.get(i*2+1);
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = myPoints.get(i++);
        double y0 = myPoints.get(i++);
        double x1 = myPoints.get(i++);
        double y1 = myPoints.get(i++);
        double x2 = myPoints.get(i++);
        double y2 = myPoints.get(i++);
        double x3 = myPoints.get(i++);
        double y3 = myPoints.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public double[] point(double t, List<Double> array) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 6;
        
        double x0 = array.get(i++);
        double y0 = array.get(i++);
        double x1 = array.get(i++);
        double y1 = array.get(i++);
        double x2 = array.get(i++);
        double y2 = array.get(i++);
        double x3 = array.get(i++);
        double y3 = array.get(i++);
        
        double[] p = new double[2];

        p[0] = b(0, t) * x0 + b(1, t) * x1 + b(2, t) * x2 + b(3, t) * x3;
        p[1] = b(0, t) * y0 + b(1, t) * y1 + b(2, t) * y2 + b(3, t) * y3;        
        
        return p;
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private double b(int i, double t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    //Implemented de Casteljau's algorithm
    //Draw out the curve
    public void draw(GL2 gl) {
    	
    	double t;
    	double[] splinePoint;
    	double halfWidth = myWidth / 2.0;
    	
    	ArrayList<double[]> crossSection = new ArrayList<double[]>();
    	ArrayList<double[]> spinePointList = new ArrayList<double[]>();
        ArrayList<double[]> vertexList = new ArrayList<double[]>();
    	ArrayList<ArrayList<double[]>> meshPointList = new ArrayList<ArrayList<double[]>>();
    	
    	//Make road cross section using line as cross section
    	crossSection.add(new double[] {-halfWidth,0,0,1});
    	crossSection.add(new double[] {halfWidth,0,0,1});
    	
    	//Interpolate NUMSEGMENT points per segment of Bezier curve
    	for (int i = 0; i < NUMSEGMENTS*size(); i++) {
    		t = (double) i/NUMSEGMENTS;
    		splinePoint = point(t);
    		//gl.glVertex3d(splinePoint[0],0,splinePoint[1]);
    		spinePointList.add(splinePoint);
    	}
    	
    	//Mark the last control point, else array out of bound
    	splinePoint = controlPoint(size()*3);
		//gl.glVertex3d(controlPoint(size()*3)[0],0,controlPoint(size()*3)[1]);
		spinePointList.add(splinePoint);
    	//gl.glEnd();
    	
    	
    	//Move cross-section along spine and store in a vertex list
    	double[] pPrev;
    	double[] pCurr = spinePointList.get(0);
    	double[] pNext = spinePointList.get(1);
    	        	
        // first point is a special case
        vertexList.addAll(addPoints(crossSection, pCurr, pCurr, pNext));
        
        // mid points
        for (int i = 1; i < spinePointList.size() - 1; i++) {
            pPrev = pCurr;
            pCurr = pNext;
            pNext = spinePointList.get(i+1);
            vertexList.addAll(addPoints(crossSection, pPrev, pCurr, pNext));            
        }
        
        // last point is a special case
        pPrev = pCurr;
        pCurr = pNext;
        vertexList.addAll(addPoints(crossSection, pPrev, pCurr, pCurr));
        
                
        // for each point along the spine
        int n = crossSection.size();
        
        for (int i = 0; i < spinePointList.size() - 1; i++) {

            // for each point in the cross section
            for (int j = 0; j < crossSection.size(); j++) {
            	
                // create a quad joining this point and the next one
                // to the equivalent points in the next cross-section
            	// in anti-clockwise order
                
                ArrayList<double[]> quad = new  ArrayList<double[]>();                
                quad.add(vertexList.get(i * n + j));
                quad.add(vertexList.get(i * n + (j+1) % n));
                quad.add(vertexList.get((i+1) * n + (j+1) % n));
                quad.add(vertexList.get((i+1) * n + j));
                meshPointList.add(quad);
            }
            
        }
    	
        gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("road"));
        double xCoord = (meshPointList.get(0).get(0)[0] + meshPointList.get(0).get(1)[0])/2.0;
        double zCoord = (meshPointList.get(0).get(0)[2] + meshPointList.get(0).get(1)[2])/2.0;
        double altitude = myTerrain.altitude(Math.abs(xCoord), Math.abs(zCoord)) + 0.02;
       
        //Draw out the crossSections
        for (ArrayList<double[]> arrayList : meshPointList) {
        	gl.glBegin(GL2.GL_QUADS);
        	xCoord = arrayList.get(0)[0];
        	zCoord = arrayList.get(0)[2];
        	gl.glNormal3d(0, 1, 0);
			gl.glTexCoord2d(1, 0);
        	gl.glVertex3d(xCoord, altitude, zCoord);
        	
        	xCoord = arrayList.get(1)[0];
        	zCoord = arrayList.get(1)[2];
			gl.glTexCoord2d(0, 0);
        	gl.glVertex3d(xCoord, altitude, zCoord);
            
            xCoord = arrayList.get(2)[0];
        	zCoord = arrayList.get(2)[2];
			gl.glTexCoord2d(0, 1);
        	gl.glVertex3d(xCoord, altitude, zCoord);
            
            xCoord = arrayList.get(3)[0];
        	zCoord = arrayList.get(3)[2];
			gl.glTexCoord2d(1, 1);
        	gl.glVertex3d(xCoord, altitude, zCoord);
            gl.glEnd();

        }
    }
    
    /**
     * Transform the points in the cross-section using the Frenet frame
     * and add them to the vertex list.
     * 
     * @param crossSection The cross section
     * @param vertices The vertex list
     * @param pPrev The previous point on the spine
     * @param pCurr The current point on the spine
     * @param pNext The next point on the spine
     */
    private ArrayList<double[]> addPoints(List<double[]> crossSection,
    		double[] pPrev, double[] pCurr, double[] pNext) {

    	ArrayList<double[]> vertices = new ArrayList<double[]>();
    	
        // compute the Frenet frame as an affine matrix
        double[][] m = new double[4][4];

        // phi = pCurr        
        m[0][3] = pCurr[0];
        m[1][3] = 0;
        m[2][3] = pCurr[1];
        m[3][3] = 1;
        
        // k = pNext - pPrev (approximates the tangent)
       
        m[0][2] = pNext[0] - pPrev[0];
        m[1][2] = 0;
        m[2][2] = pNext[1] - pPrev[1];
        m[3][2] = 0;
      
        
        // normalise k
        double d = Math.sqrt(m[0][2] * m[0][2] + m[1][2] * m[1][2] + m[2][2] * m[2][2]);  
        m[0][2] /= d;
        m[1][2] /= d;
        m[2][2] /= d;
        
        // i = simple perpendicular to k
        m[0][0] = -m[2][2];
        m[1][0] =  0;
        m[2][0] =  m[0][2];
        m[3][0] =  0;
        
        // j = k x i
        m[0][1] = m[1][2] * m[2][0] - m[2][2] * m[1][0];
        m[1][1] = m[2][2] * m[0][0] - m[0][2] * m[2][0];
        m[2][1] = m[0][2] * m[1][0] - m[1][2] * m[0][0];
        m[3][1] =  0;
        
        // transform the points
       
        for (double[] cp : crossSection) {
        
            double[] q = multiply(m,cp);
           
            vertices.add(q);
        }
        
        return vertices;
    }
    
    public static double[] multiply(double[][] m, double[] v) {

        double[] u = new double[4];

        for (int i = 0; i < 4; i++) {
            u[i] = 0;
            for (int j = 0; j < 4; j++) {
                u[i] += m[i][j] * v[j];
            }
        }

        return u;
    }


}
