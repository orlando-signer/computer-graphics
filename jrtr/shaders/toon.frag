#version 150
#define MAX_LIGHTS 8

in vec4 frag_position;
in vec3 frag_normal;

uniform vec4[MAX_LIGHTS] lightPosition;
uniform mat4 modelview;

uniform int nLights;


out vec4 frag_shaded;

void main()
{
    float intensity = 0;
    vec4 color;
    vec4 n = normalize(vec4(frag_normal,0));
    for(int i = 0; i < nLights; i++){
        intensity = intensity + dot(normalize(lightPosition[i] - frag_position),  normalize(modelview * n));
    }
    
    
    if (intensity > 0.95)
        color = vec4(1.0,0.5,0.5,1.0);
    else if (intensity > 0.5)
        color = vec4(0.6,0.3,0.3,1.0);
    else if (intensity > 0.25)
        color = vec4(0.4,0.2,0.2,1.0);
    else
        color = vec4(0.2,0.1,0.1,1.0);
    frag_shaded = color;

}
