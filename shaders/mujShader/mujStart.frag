#version 150
out vec4 outColor; // output from the fragment shader
in vec2 texCoord;
//in vec4 color;
vec4 color;
uniform sampler2D textureID;
uniform sampler2D textureIP;
uniform float cervenaBarva;
uniform float zelenaBarva;
uniform float modraBarva;


vec3 YCrCbtoRGB(float y1,float cr1,float cb1){

	//rozdeleni podle barev/hodnot
	float y = y1;
	float cr = cr1;
	float cb = cb1;

	//prepocitani do rozsahu 0 az 255
	//nenasel jsem jiny vzorec -> moznost nahrazeni lepsim a dokonalejsim
	y = y * 255;
	cr = (cr + 0.5) * 255;
	cb = (cb + 0.5) * 255;


	//prepocitani na RGB model
	float r = y + 1.403*(cr - 128);
	float g = y - 0.344*(cb - 128) - 0.714*(cr - 128);
	float b = y + 1.770*(cb - 128);


	//overeni aby byla barva v rozsahu (moznost presazeni o tisiciny a tudiz nefunkcnost)
	if (255 < r) {
		r = 255;
	}

	//overeni aby byla barva v rozsahu (moznost presazeni o tisiciny a tudiz nefunkcnost)
	if (255 < g) {
		g = 255;
	}

	//overeni aby byla barva v rozsahu (moznost presazeni o tisiciny a tudiz nefunkcnost)
	if (255 < b) {
		b = 255;
	}

	//prepocitani hodnot do rozsahu 0 az 1
	r = r / 255;
	g = g / 255;
	b = b / 255;



	//vraceni hodnot
	return vec3(r,g,b);
}

vec3 RgbtoYCrCb(vec3 colorp) {
	//rozdeleni podle barev + prepocitani do rozsahu 0 az 255
	//nenasel jsem jiny vzorec -> moznost nahrazeni lepsim a dokonalejsim
	float r = (colorp.x * 255);
	float g = ( colorp.y * 255);
	float b = ( colorp.z * 255);

	//prepocitani barev na YUV
	float y = ((0.299*r + 0.587*g + 0.114*b));
	float cr = ((128 + 0.500*r - 0.419*g - 0.081*b)); //V
	float cb = ((128 - 0.169*r - 0.331*g + 0.500*b));  //U


	//propocitani y do rozsahu 0 az 1
	y = y/255;
	//prepocitani cr do rozsahu -0.5 az 0.5
	cr = ((cr / 255) - 0.5);
	//prepocitani cb do rozsahu -0.5 az 0.5
	cb = ((cb/255) - 0.5);


	//vraceni hodnot
	return vec3(y, cr, cb);
}

//otoceni os CR a cb
float VypocetX(float cr, float cb, float otoceni) {
	return (cr * cos(otoceni) - cb * sin(otoceni));
}

//otoceni os cr a CB
float VypocetZ(float cr, float cb, float otoceni) {
	return (cb * cos(otoceni) + cr * sin(otoceni));
}


void main() {

	outColor = texture(textureID, texCoord);
	vec4 outColor1 = texture(textureIP, texCoord);
	//prepocitani rgb hlavni textury na yuv
	vec3 yuv = RgbtoYCrCb(outColor.rgb);

	//rozdeleni yuv do promenych
	float y = yuv.x;
	float cr = yuv.y; //V
	float cb = yuv.z; //U

	//promena pro oteceni (moznost vytknout/vypocitat/dodat externě)
	float otoceni = 1.72;


	//otoceni os a prepocitani je na X a Z
	float x = VypocetX(cr, cb, otoceni);
	float z = VypocetZ(cr, cb, otoceni);

	//definice pomocne promene
	float pom = abs(z) / tan(3.14159265359 / 10);
	float pom1 = abs(z) / tan(3.14159265359 / 4);

	//definice finalni promene, ktera ukazuje jestli je barva v cilene vyseci nebo ne
	float kfg;
	float kfg1;

	//porovnani osy x a pom promene -> kfg nikdy nebude mensi nezly 0.0
	if (x < pom){
		kfg = 0.0;
	}else {
		kfg = x - pom;
	}

	//porovnani osy x a pom promene -> kfg nikdy nebude mensi nezly 0.0
	//porovnani pro posun barvy
	if (x < pom1){
		kfg1 = 0.0;
	}else {
		kfg1 = x - pom1;
	}
	if(kfg1==0){
		outColor= vec4(outColor);
	}

	//urceni jestli se bude odstranovat pixel a nahrazovat pozadim nebo se bude prekreslovat, popripade zustane nezmenen
	if(kfg==0 && kfg1 >0){
		//posunuti barvy do hranicnice podle osy x
		x = x - kfg1;
		float newCr, newCb;
		//vypocitani novych hodnot barvy
		newCr = VypocetX(x, z, -otoceni);
		newCb = VypocetZ(x, z, -otoceni);

		//prevrzeni nove barvy do RGB
		vec3 col = YCrCbtoRGB(y, newCr, newCb);


		//vraceni vysledne textury
		if(cervenaBarva==0){
			outColor = vec4(col,1);
		}else{
			outColor;
		}


	}else{

		//vymezeni pro nepocitani s tmavou a velmi svetlou barvou/hodnotami
		if (0.15 < y && y < 0.85){
			kfg = kfg;

		}else {

			kfg = 0;
		}



		//TODO moznost odstraneni upraveni (PROSIM nezapomenout jednou odstranit)
		if(zelenaBarva==0){

			//zjisteni jestli je barva ve vyseci nebo ne -> pokud je tak nastaveni alphy na 0.0
			if(0.0 < kfg){
				outColor= vec4(outColor1);
			}else{
				outColor= vec4(outColor);
			}
		}else{
			outColor= vec4(outColor);
		}
	}







}
