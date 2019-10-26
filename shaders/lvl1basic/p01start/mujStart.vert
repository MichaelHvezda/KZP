#version 150
in vec2 inPosition; // input from the vertex buffer
//in vec2 inTextureCoordinates;
//out vec2 texCoord;
//attribute vec4 Color;
out vec4 color;
void main() {


	//texCoord = inTextureCoordinates;
	//color = vec4(vertColor);


	gl_Position = vec4(inPosition, 0.0, 1.0);
} 
