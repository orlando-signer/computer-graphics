#version 150
// GLSL version 1.50
// Fragment shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables passed in from host program
uniform sampler2D myTexture;
uniform vec4[MAX_LIGHTS] lightColor;
uniform vec4[MAX_LIGHTS] lightPosition;
uniform int nLights;
uniform vec4 camera;
uniform mat4 modelview;
 
// phong shading
uniform float shininess;
uniform vec4 reflectionCoefficient;

vec4 e;
vec4 diffuse;
vec4 specular;

// Variables passed in from the vertex shader
in float[MAX_LIGHTS] ndotl;
in vec4[MAX_LIGHTS] reflections;
in vec2 frag_texcoord;
in vec4 frag_color;
in vec4 frag_position;
in vec3 frag_normal;
in float[MAX_LIGHTS] lDistance;


// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	// frag_shaded = ndotl * texture(myTexture, frag_texcoord);
	for (int i = 0; i < nLights; i++) {
	   e = camera -   gl_FragCoord;
	   diffuse = lightColor[i] * frag_color * ndotl[i] / (lDistance[i]*lDistance[i]) * 50 ;
	   specular = reflectionCoefficient * pow(reflections[i] * e, vec4(shininess,shininess,shininess,shininess));
	   specular = clamp(specular, 0,1);
	   
	   frag_shaded +=  lightColor[i] * (diffuse + specular); 
	}
}