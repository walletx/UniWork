#version 120

attribute vec2 texIn0;
attribute vec4 vertexPos;
attribute vec3 normalL;

varying vec2 texCoord0;
varying vec3 N; 
varying vec4 v; 

void main (void) {	
   
    v = gl_ModelViewMatrix * vertexPos;
    N = vec3(normalize(gl_NormalMatrix * normalize(normalL)));    
    	    
	gl_Position = gl_ModelViewProjectionMatrix * vertexPos;
		
	texCoord0 = vec2(texIn0);
}