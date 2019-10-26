#version 150
in vec2 inPosition; // input from the vertex buffer
in vec2 inTextureCoordinates;
out vec2 texCoord;
void main() {


	texCoord = inTextureCoordinates;



	gl_Position = vec4(inPosition, 0.0, 1.0);
} 
