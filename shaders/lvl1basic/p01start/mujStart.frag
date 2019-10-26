#version 150
out vec4 outColor; // output from the fragment shader
in vec2 texCoord;
in vec4 color;
uniform sampler2D textureID;
void main() {
	if(outColor.x*255<150){
		outColor = vec4(color);
	}

} 
