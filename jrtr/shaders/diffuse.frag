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
// phong shading
uniform float shininess;
uniform vec4 reflectionCoefficient;

vec4 r; // reflection vector
vec4 e;
vec4 diffuse;
vec4 specular;

// Variables passed in from the vertex shader
in float[MAX_LIGHTS] ndotl;
in vec2 frag_texcoord;
in vec4 frag_color;
in vec4 frag_position;
in vec3 frag_normal;

// Output variable, will be written to framebuffer automatically
out vec4 frag_shaded;

void main()
{		
	// The built-in GLSL function "texture" performs the texture lookup
	// frag_shaded = ndotl * texture(myTexture, frag_texcoord);
	for (int i = 0; i < nLights; i++) {
	   e = camera - frag_position;
	   r = reflect((lightPosition[i] - frag_position), vec4(frag_normal,0));
	   diffuse = ndotl[i] * 0.1 * frag_color;
	   specular = reflectionCoefficient * pow(r * e, vec4(shininess, shininess,shininess,shininess));
	   
	   frag_shaded +=  lightColor[i] * (diffuse + specular); 
	}
}