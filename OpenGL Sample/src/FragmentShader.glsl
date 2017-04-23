#version 120

varying vec2 texCoord0;
uniform sampler2D texUnit0;

varying vec3 N;
varying vec4 v;

void main (void) {	

vec4 ambient, globalAmbient;
    
    /* Compute the ambient and globalAmbient terms */
	ambient =  gl_LightSource[0].ambient * gl_FrontMaterial.ambient;
	globalAmbient = gl_LightModel.ambient * gl_FrontMaterial.ambient;

	
	/* Diffuse calculations */

	vec3 normal, lightDir, tempT; 
	
	vec4 diffuse, tempF;
	float NdotL;
	
	/* normal has been interpolated and may no longer be unit length so we need to normalise*/
	normal = normalize(N);
	
	
	/* normalize the light's direction. */
	tempT = gl_LightSource[0].position.xyz - vec3(v);
	lightDir = normalize(vec3(tempT));
    NdotL = max(dot(normal, lightDir), 0.0); 
    /* Compute the diffuse term */
     diffuse = NdotL * gl_FrontMaterial.diffuse * gl_LightSource[0].diffuse; 

    vec4 specular = vec4(0.0,0.0,0.0,1);
    float NdotHV;
    float NdotR;
    tempF = -v;
    vec3 dirToView = normalize(vec3(tempF));
    
    vec3 R = normalize(reflect(-lightDir,normal)); 
    vec3 H =  normalize(lightDir+dirToView); 
   
    /* compute the specular term if NdotL is  larger than zero */
    
	if (NdotL > 0.0) {
		NdotR = max(dot(R,dirToView ),0.0);
		
		//Can use the halfVector instead of the reflection vector if you wish 
		NdotHV = max(dot(normal, H),0.0);
		
		specular = gl_FrontMaterial.specular * gl_LightSource[0].specular * pow(NdotHV,gl_FrontMaterial.shininess);
	    
	}
	
	specular = clamp(specular,0,1);
	
	//Attenuation and spotlight code based on pseudocode from
	//https://www.opengl.org/discussion_boards/showthread.php/162199-Combining-texture-and-light
	
    float sourceDistance = distance(gl_LightSource[0].position.xyz, vec3(v));
    float theta = dot(lightDir, -gl_LightSource[0].spotDirection);
    float attenuationFactor = 1.0/(gl_LightSource[0].constantAttenuation +
                    gl_LightSource[0].linearAttenuation * sourceDistance +
                    gl_LightSource[0].quadraticAttenuation * pow(sourceDistance,2)) *
                    pow(theta, gl_LightSource[0].spotExponent);

                    
	
	float spotEffect = pow(max(dot(vec3(v),-lightDir),0), gl_LightSource[0].spotExponent);
	//float spotEffect = pow(max(dot(vec3(v),-gl_LightSource[0].spotDirection),0), gl_LightSource[0].spotExponent);
	
	vec4 textureCol = texture2D(texUnit0,texCoord0).rgba;
	//gl_FragColor =  gl_FrontMaterial.emission + globalAmbient + (attenuationFactor * spotEffect * (ambient + diffuse + specular)) * vec4(textureCol);
	gl_FragColor =  gl_FrontMaterial.emission + globalAmbient + (ambient + diffuse + specular) * vec4(textureCol);
	//gl_FragColor =  (gl_FrontMaterial.emission + globalAmbient + ambient + diffuse + specular) * vec4(textureCol);
    //gl_FragColor = texture2D(texUnit0,texCoord0);
}
