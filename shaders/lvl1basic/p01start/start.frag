#version 150
out vec4 outColor; // output from the fragment shader
in vec2 texCoord;
uniform sampler2D textureID;
void main() {
	outColor = texture(textureID, texCoord);
//	if(outColor.x*255<255){
	////		outColor= vec4(0,0,0,0);
	////	}
} 
