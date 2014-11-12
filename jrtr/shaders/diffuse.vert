#version 150
// GLSL version 1.50 
// Vertex shader for diffuse shading in combination with a texture map

#define MAX_LIGHTS 8

// Uniform variables, passed in from host program via suitable 
// variants of glUniform*
uniform mat4 projection;
uniform mat4 modelview;

uniform int nLights;
uniform vec4[MAX_LIGHTS] lightPosition;

// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;
in vec4 color; // diffuse reflectance

vec4 l;
// Output variables for fragment shader
out float[MAX_LIGHTS] ndotl;
out vec2 frag_texcoord;
out vec4 frag_color;

void main()
{		
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	for (int i = 0;i < nLights; i++) {
		//ndotl += max(dot(modelview * vec4(normal,0), lightPosition[i]),0);
		l = (lightPosition[i] - position) / length(lightPosition[i] - position);
		ndotl[i] = max(dot(modelview * vec4(normal,0),  l), 0);
    }
    
    frag_color = color;
    
	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
