#version 150
in vec2 inPosition; // input from the vertex buffer
in vec2 inTextureCoordinates;
out vec2 texCoord;
uniform sampler2D textureIP;
//attribute vec4 Color;
//out vec4 color;
void main() {




    vec4 color = texture2D(textureIP, vec2(1 - inPosition.x, inPosition.y));


    gl_Position = vec4(inPosition, 0.0, 1.0);
	texCoord = inTextureCoordinates;
	//color = vec4(vertColor);



} 
