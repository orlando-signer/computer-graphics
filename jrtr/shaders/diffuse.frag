#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;
uniform vec4[MAX_LIGHTS] lightColor;
uniform float[MAX_LIGHTS] reflectionCoefficient;
uniform int nLights;


// Variables passed in from the vertex shader
in float[MAX_LIGHTS] ndotl;
in vec2 frag_texcoord;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	// frag_shaded = ndotl[0] * texture(myTexture, frag_texcoord);
	for (int i = 0; i < nLights; i++){
	   frag_shaded += ( reflectionCoefficient[i] * ndotl[i] ) * lightColor[i]; 
	}
	//frag_shaded = max(frag_shaded, vec4(0,1,1,1));
	//frag_shaded = vec4(0,1,1,1);
}