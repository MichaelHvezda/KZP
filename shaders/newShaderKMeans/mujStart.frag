#version 460 core
in vec2 texCoord;

//A uniform of the type sampler2D will have the storage value of our texture.
uniform sampler2D uTexture0;

uniform vec3 cent1;
uniform vec3 cent2;
uniform vec3 cent3;

//out vec4 FragColor;
layout (location = 0) out vec4 color0;
layout (location = 1) out vec4 color1;
layout (location = 2) out vec4 color2;


float NaDruhou(float firstNumber, float scndNumber) {
	return ((firstNumber - scndNumber) * (firstNumber - scndNumber));
}

vec2 Vzdalenost(vec3 col, vec3 cent) {
	float pomZaporna;
	float pomKladna = NaDruhou(col.x, cent.x) + NaDruhou(col.y, cent.y) + NaDruhou(col.z, cent.z);
	if (col.x < cent.x) {
		pomZaporna = NaDruhou(col.x, cent.x - 360.0f) + NaDruhou(col.y, cent.y) + NaDruhou(col.z, cent.z);
	}
	else {
		pomZaporna = NaDruhou(col.x - 360.0f, cent.x) + NaDruhou(col.y, cent.y) + NaDruhou(col.z, cent.z);
	}

	if (pomKladna <= pomZaporna) {
		return vec2(pomKladna, 1);
	}
	else {
		return vec2(pomZaporna, -1);
	}
}

float MaxnIsZero(float maxn, float minn){

	if(maxn == 0.0f){
		return 0.0f;
	}
	return (1.0f - (minn / maxn));
}

vec3 RgbToHsb(vec3 a){
	float r = (a.r);
	float g = (a.g);
	float b = (a.b);

	float maxn = max(r, max(g, b));
	float minn = min(r, min(g, b));

	float h = 0.0f;
	if (maxn == r && g >= b)
	{
		if (maxn - minn == 0.0)
		{
			h = 0.0f;
		}else
		{
			h = 60.0f * ((g - b) / (maxn - minn));
		}
	}else if (maxn == r && g < b)
	{
		h = 60.0f * ((g - b) / (maxn - minn)) + 360.0f;
	}else if (maxn == g)
	{
		h = 60.0f * ((b - r) / (maxn - minn)) + 120.0f;
	}else if (maxn == b)
	{
		h = 60.0f * ((r - g) / (maxn - minn)) + 240.0f;
	}
	float s = MaxnIsZero(maxn,minn);
	//h - 0/360
	//s - 0/1
	//maxn - 0/1
	return vec3(h, s, maxn);
}

vec3 ToShaderRange(vec3 a){
	//h - 0/360
	//s - 0/1
	//maxn - 0/1
	return vec3( mod(a.x + 1.0f, 1.0f ) * 360.0f, a.y, a.z);
}

vec4 ToTextureRange(vec3 a,float b){
	//h - -1/1
	//s - 0/1
	//maxn - 0/1
	return vec4(mod( b+ ( a.x / 360.0f), 1.0f), a.y, a.z,1);
}

void main()
{
	vec4 a = texture(uTexture0, texCoord);
	vec3 hsb = RgbToHsb(a.xyz);

	vec3 cent1hsb = ToShaderRange(cent1);
	vec3 cent2hsb = ToShaderRange(cent2);
	vec3 cent3hsb = ToShaderRange(cent3);

	vec2 jedna = Vzdalenost(hsb, cent1hsb);
	vec2 dva = Vzdalenost(hsb, cent2hsb);
	vec2 tri = Vzdalenost(hsb, cent3hsb);

	float smal = min(min(jedna.x, dva.x), tri.x);
	//color0 = vec4(255, 125, 255, 255);
	//color1 = vec4(255, 125, 255, 255);
	//color2 = vec4(255, 125, 255, 255);
	//return;
	if (jedna.x == smal) {
		color0 = ToTextureRange(hsb,jedna.y);
		color1 = vec4(0, 0, 0, 0);
		color2 = vec4(0, 0, 0, 0);
		return;
	}
	else {
		color0 = vec4(0, 0, 0, 0);
	}

	if (dva.x == smal) {
		color1 = ToTextureRange(hsb,dva.y);
		color2 = vec4(0, 0, 0, 0);
		return;
	}
	else {
		color1 = vec4(0, 0, 0, 0);
	}

	if (tri.x == smal) {
		color2 = ToTextureRange(hsb,tri.y);
	}
	else {
		color2 = vec4(0, 0, 0, 0);
	}
}
