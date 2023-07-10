#version 460 core
in vec2 texCoord;

//A uniform of the type sampler2D will have the storage value of our texture.
layout (location = 0) uniform sampler2D uTexture0;
layout (location = 1) uniform sampler2D uTexture1;
layout (location = 2) uniform sampler2D uTexture2;

out vec4 FragColor;

void main()
{
    vec2 yRotate = vec2(texCoord.x,1 - texCoord.y);
    //Here we sample the texture based on the Uv coordinates of the fragment
    vec4 a = texture(uTexture0, texCoord);
    vec4 b = texture(uTexture1, texCoord);
    vec4 c = texture(uTexture2, yRotate);
    //&& (c.y > 0.35f && c.z > 0.35f)
    if(c.w == 1 && c.y > 0.35f && c.z > 0.35f){
        FragColor = b;
    }else{
        FragColor = a;
    }

    //FragColor = vec4(255,0,255,255);

}