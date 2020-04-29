#version 150
out vec4 outColor; // output from the fragment shader
in vec2 texCoord;
//in vec4 color;
vec4 color;


// pozadí
uniform sampler2D textureID;

// popredi
uniform sampler2D textureIP;
uniform float otoceni;
uniform vec3 cent1;
uniform vec3 cent2;
uniform vec3 cent3;
uniform vec3 colorBack;

//prepocet barevneho modelu
vec3 RgbToHsb(vec3 colorp){
float r = (colorp.r);
float g = (colorp.g);
float b = (colorp.b);

float maxn = max(r, max(g, b));
float minn = min(r, min(g, b));

float h = 0.0;
if (maxn == r && g >= b)
{
	if (maxn - minn == 0.0)
	{
		h = 0.0;
	}else
	{
		h = 60.0 * ((g - b) / (maxn - minn));
	}
}else if (maxn == r && g < b)
{
	h = 60.0 * ((g - b) / (maxn - minn)) + 360.0;
}else if (maxn == g)
{
	h = 60.0 * ((b - r) / (maxn - minn)) + 120.0;
}else if (maxn == b)
{
	h = 60.0 * ((r - g) / (maxn - minn)) + 240.0;
}

float s = (maxn == 0.0) ? 0.0 : (1.0 - (minn / maxn));

vec3 vysledek = vec3(h, s*100.0, maxn*100.0);

return vysledek;
}

//vypocet vzdalenosti dvou bodu
float vzdalenost(vec3 col,vec3 cent){

	return (((col.x - cent.x) * (col.x - cent.x))
	+ ((col.y - cent.y) *  (col.y - cent.y))
	+ ((col.z - cent.z) *  (col.z - cent.z)));
}



vec3 barva (float a,float b, float c){
	if(a<b && a<c){
		return vec3(255,0,0);
	}

	if(b<a && b<c){
		return vec3(0,255,0);
	}

	if(c<a && c<b){
		return vec3(0,0,255);
	}

	return vec3(255,255,255);
}

float small(float a,float b, float c){
	return min(min(a,b),c);
}

void main() {

	//zjisteni barvy popredi a pozadi
	outColor = texture(textureID, texCoord);
	vec4 outColor1 = texture(textureIP, texCoord);

	//prepocet popredi
	vec3 hsb = RgbToHsb(outColor.rgb);
	vec3 colBack = RgbToHsb(colorBack);

	//zjisteni vzdalenosti od centroid
	float jednaBack = vzdalenost(colBack,cent1);
	float dvaBack = vzdalenost(colBack,cent2);
	float triBack = vzdalenost(colBack,cent3);

	//zjisteni vzdalenosti od centroid pozadi
	float jedna = vzdalenost(hsb,cent1);
	float dva = vzdalenost(hsb,cent2);
	float tri = vzdalenost(hsb,cent3);

	//zjisteni nejmensi vzdalenosti
	float smal = small(jedna,dva,tri);
	float smalBack = small(jednaBack,dvaBack,triBack);

	//urceni jestli zobrazovat popredi nebo pozadi
	if(jedna==smal){
		if(jednaBack==smalBack){
			outColor = outColor1;
		}
	outColor;
	}

	if(dva==smal){
		if(dvaBack==smalBack){
			outColor = outColor1;
		}
	outColor;
	}

	if(tri==smal){
		if(triBack==smalBack){
			outColor = outColor1;
		}
	outColor;
	}
}
