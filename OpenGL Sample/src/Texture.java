import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLProfile;

import com.jogamp.opengl.util.awt.ImageUtil;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

//Based on code from Week 8 Lecture

public class Texture {

	static private HashMap<String,Integer> textureMap = new HashMap<String,Integer>();
	
	public Texture(GL2 gl, String hashKey, String fileName, String extension) {
		TextureData data = null;
		try {
			 File file = new File(fileName);
			 
			 // read file into BufferedImage
			 BufferedImage img = ImageIO.read(file); 
			 ImageUtil.flipImageVertically(img);
			 
			//This library call flips all images the same way
			data = AWTTextureIO.newTextureData(GLProfile.getDefault(), img, false);
			
		} catch (IOException exc) {
			System.err.println(fileName);
            exc.printStackTrace();
            System.exit(1);
        }
		
		int[] textureID = new int[1];
		gl.glGenTextures(1, textureID, 0);
		gl.glBindTexture(GL.GL_TEXTURE_2D, textureID[0]);

		 // Build texture initialised with image data.
        gl.glTexImage2D(GL.GL_TEXTURE_2D, 0,
        				data.getInternalFormat(),
        				data.getWidth(),
        				data.getHeight(),
        				0,
        				data.getPixelFormat(),
        				data.getPixelType(),
        				data.getBuffer());
		
        setFilters(gl);
        
        textureMap.put(hashKey, textureID[0]);
        		
	}
	
    private void setFilters(GL2 gl){
    	// Build the texture from data.
    	// Enable bilinear filtering
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
    	
    	//Enable mip mapping
    	gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR_MIPMAP_LINEAR);
    	gl.glGenerateMipmap(GL2.GL_TEXTURE_2D);
    }
	
	public static int getTextureId(String hashKey) {
		return textureMap.get(hashKey);
	}
	
		
	public static void release(GL2 gl, String hashKey) {
		Integer texID = textureMap.get(hashKey);
		if (texID != null) {
			int[] textureID = {texID.intValue()};
			gl.glDeleteTextures(1, textureID, 0);
			textureMap.remove(hashKey);
		}
	}
}
