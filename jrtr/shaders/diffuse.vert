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
uniform vec4 camera;


// Input vertex attributes; passed in from host program to shader
// via vertex buffer objects
in vec3 normal;
in vec4 position;
in vec2 texcoord;
in vec4 color; // diffuse reflectance

vec4 l;


// Output variables for fragment shader
out float[MAX_LIGHTS] ndotl;
out vec4[MAX_LIGHTS] reflections;
out vec2 frag_texcoord;
out vec4 frag_color;
out vec4 frag_position;
out vec3 frag_normal;
out float[MAX_LIGHTS] lDistance;

void main()
{		
	// Compute dot product of normal and light direction
	// and pass color to fragment shader
	// Note: here we assume "lightDirection" is specified in camera coordinates,
	// so we transform the normal to camera coordinates, and we don't transform
	// the light direction, i.e., it stays in camera coordinates
	for (int i = 0;i < MAX_LIGHTS; i++) {
		lDistance[i] = length(lightPosition[i] - position);
		l = (lightPosition[i] - position) / lDistance[i];
		ndotl[i] = max(dot(modelview * vec4(normal,0),  l), 0);
		reflections[i] = clamp(reflect(modelview * position - lightPosition[i], normalize(vec4(normal,0))), 0 ,1);
    }
    
    frag_color = color;
    frag_position = modelview * position;
    frag_normal = normal;
    
    
	// Pass texture coordiantes to fragment shader, OpenGL automatically
	// interpolates them to each pixel  (in a perspectively correct manner) 
	frag_texcoord = texcoord;

	// Transform position, including projection matrix
	// Note: gl_Position is a default output variable containing
	// the transformed vertex position
	gl_Position = projection * modelview * position;
}
