import java.nio.FloatBuffer;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import com.jogamp.common.nio.Buffers;

/**
 * COMMENT: Comment Triangle 
 *
 * 
 */



public class VBOObject {
	
	private Terrain myTerrain;
	
	private float[] positions;
	private float[] texture0;
	private float[] normals;
	
	//These are not vertex buffer objects, they are just java containers
	private FloatBuffer  posData;
	private FloatBuffer texture0Data;
	private FloatBuffer normalData;
	
	private int vertexPosLoc;
	private int texIn0Loc;
	private int normalLoc;
	
	private int texUnit0Loc;
	private int[] bufferIds;
	
	//Shader code based on provided code from lecture
	private static final String VERTEX_SHADER = "src/VertexShader.glsl";
	private static final String FRAGMENT_SHADER = "src/FragmentShader.glsl";
	 
	private int shaderprogram;
	
	public VBOObject(float[] vertices, float[] texture) {
		
		positions = vertices;
	    texture0 = texture;
	    normals = cubeFaceNormals(vertices);
	   
	    bufferIds = new int[1];
		 
	}
	
	public int getVerticesNum() {
		return positions.length;
	}
	
	public int getTextureNum() {
		return texture0.length;
	}
	
	public float[] verticesArray() {
		return positions;
	}
	
	public float[] textureArray() {
		return texture0;
	}
	
	private float[] cubeFaceNormals(float[] vertices) {
		
		float[] normals = new float[72];
		float[] v1, v2;
		int vindex, nindex;	

		for (int i = 0; i < vertices.length/12; i++) {			
			
			vindex = i*12;
			nindex = i*3;
			
			v1 = new float[]{vertices[vindex+3] - vertices[vindex],
							vertices[vindex+4] - vertices[vindex+1],
							vertices[vindex+5] - vertices[vindex+2]};
			
			v2 = new float[]{vertices[vindex+9] - vertices[vindex],
							 vertices[vindex+10] - vertices[vindex+1],
							 vertices[vindex+11] - vertices[vindex+2]};
			
			normals[nindex] = v1[1] * v2[2] - v1[2] * v2[1];
			normals[nindex+1] = v1[2] * v2[0] - v1[0] * v2[2];
			normals[nindex+2] = v1[0] * v2[1] - v1[1] * v2[0];
			
		}
		
		return normals;
	}
	
	private float[] setMaxAltitude(float []vertices) {
		float maxAlt = 0;
		float altitude;
		for (int i = 0; i < vertices.length; i += 3) {
			altitude = (float) myTerrain.altitude(vertices[i], vertices[i+2]);
			if (altitude > maxAlt) {
				maxAlt = altitude;
			}
		}
		
		for (int i = 0; i < vertices.length; i++) {
			if (i % 3 == 1) {
				vertices[i] += maxAlt;
			}
		}
		
		return vertices;
		
	}
	
	public void setTerrain(Terrain terrain) {
		myTerrain = terrain;
	}
	 
    public void initialise(GL2 gl) {
    	
    	 positions = setMaxAltitude(positions);
 	     posData = Buffers.newDirectFloatBuffer(positions);
 	     texture0Data = Buffers.newDirectFloatBuffer(texture0);
 	     normalData = Buffers.newDirectFloatBuffer(normals);

    	 gl.glEnable(GL2.GL_DEPTH_TEST); // Enable depth testing.
    	 
    	//Generate 1 VBO buffer and get their IDs
         gl.glGenBuffers(1,bufferIds,0);
        
    	 //This buffer is now the current array buffer
         //array buffers hold vertex attribute data
         gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
      
         //This is just setting aside enough empty space
         //for all our data
         gl.glBufferData(GL2.GL_ARRAY_BUFFER,    //Type of buffer  
        	        positions.length * (Float.SIZE/8) +  texture0.length * (Float.SIZE/8) 
        	        + normals.length * (Float.SIZE/8), //size needed
        	        null,    //We are not actually loading data here yet
        	        GL2.GL_STATIC_DRAW); //We expect once we load this data we will not modify it


         //Actually load the positions data
         gl.glBufferSubData(GL2.GL_ARRAY_BUFFER, 0, //From byte offset 0
        		 positions.length*(Float.SIZE/8),posData);

         //Actually load the texure data
         gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
        		 positions.length*(Float.SIZE/8),  //Load after the position data
        		 texture0.length*(Float.SIZE/8),texture0Data);
         
         gl.glBufferSubData(GL2.GL_ARRAY_BUFFER,
        		 positions.length*(Float.SIZE/8) + texture0.length*(Float.SIZE/8),  //Load after the position data
        		 normals.length*(Float.SIZE/8),normalData);
         
    	    	 
    	 try {
    		 shaderprogram = Shader.initShaders(gl,VERTEX_SHADER,FRAGMENT_SHADER);
   		    		 
         }
         catch (Exception e) {
             e.printStackTrace();
             System.exit(1);
         }
    	 
    	 texUnit0Loc = gl.glGetUniformLocation(shaderprogram, "texUnit0");

    }

    public void draw(GL2 gl) {
        
        //Use the shader
        gl.glUseProgram(shaderprogram);
        gl.glUniform1i(texUnit0Loc , 0);
        
        gl.glEnable(GL.GL_TEXTURE_2D);
        

    	gl.glBindTexture(GL2.GL_TEXTURE_2D, Texture.getTextureId("vbo"));
        
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,bufferIds[0]);
       
        
        vertexPosLoc = gl.glGetAttribLocation(shaderprogram,"vertexPos");
        texIn0Loc = gl.glGetAttribLocation(shaderprogram,"texIn0");
        normalLoc = gl.glGetAttribLocation(shaderprogram,"normalL");
               
   	    // Specify locations for the co-ordinates and color arrays.
        gl.glEnableVertexAttribArray(vertexPosLoc);
        gl.glEnableVertexAttribArray(texIn0Loc);
   	   	gl.glVertexAttribPointer(vertexPosLoc,3, GL.GL_FLOAT, false,0, 0); //last num is the offset
	   	gl.glVertexAttribPointer(texIn0Loc,2, GL.GL_FLOAT, false,0, positions.length*(Float.SIZE/8));
	   	gl.glVertexAttribPointer(normalLoc,3, GL.GL_FLOAT, false,0, positions.length*(Float.SIZE/8)+texture0.length*(Float.SIZE/8));

        gl.glDrawArrays(GL2.GL_QUADS, 0, 24);
    	  	
    	gl.glUseProgram(0);   
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER,0);
         
    }
    
    
}
