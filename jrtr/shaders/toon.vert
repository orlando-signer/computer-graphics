#version 150
in vec4 position;
in vec3 normal;

uniform mat4 projection;
uniform mat4 modelview;

out vec4 frag_position;
out vec3 frag_normal;


void main()
{
    frag_position = modelview *  position;
    frag_normal = normal;
    gl_Position = projection * modelview * position;

}