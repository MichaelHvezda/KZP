package klicovani3;

import helpers.OpenCVImageFormat;
import lwjglutils.*;
import opencvutils.VideoGrabber;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.opencv.core.Point3;
import pom.AbstractRenderer;
import transforms.Col;
import transforms.Point3D;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_FRAMEBUFFER;
import static org.lwjgl.opengl.GL30.glBindFramebuffer;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	private double ox, oy;
	private boolean mouseButton1 = false;
	private boolean prebarveniBool = false;

	private OGLBuffers buffers;
	private boolean pomocnaBul;
	private int shaderProgram, locMat, cervenaBarva,zelenaBarva,modraBarva,otoceni,vysecBarva,vysecKlic;

	private OGLTexture2D texture, texture2;
	private float cervenaBarvaHodnota = 0;
	private float zelenaBarvaHodnota = 0;
	private float modraBarvaHodnota = 0;
	//hodnota otočení barevné palety aby se klicovana barva nachazela v bode 0
	private float otoceniHodnota = 0;

	//velikost vyseče v barevném modelu, která se klicuje
	private float vysecKlicovani = 0;

	//velikost vyseče v barevném modelu, která se prebarvuje
	private float vysecPrebarveni = 0;

	private VysecKalkulator vysecKalkulator = new VysecKalkulator();
	private OGLRenderTarget renderTarget;
	private boolean draw = false;
	private VideoGrabber videoGrabber;
	private OpenCVImageFormat videoImageFormat;
	ByteBuffer buffer;
	private OGLTexture2D.Viewer textureViewer;
	
	private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				switch (key) {

				case GLFW_KEY_N:
					draw = false;
					break;
				case GLFW_KEY_Q:
					cervenaBarvaHodnota= (255+cervenaBarvaHodnota+0.5f)%255;
					break;
				case GLFW_KEY_W:
					cervenaBarvaHodnota= (255+cervenaBarvaHodnota-0.5f)%255;
					break;
				case GLFW_KEY_A:
					zelenaBarvaHodnota= (255+zelenaBarvaHodnota+0.5f)%255;
					break;
				case GLFW_KEY_S:
					zelenaBarvaHodnota= (255+zelenaBarvaHodnota-0.5f)%255;
					break;
				case GLFW_KEY_Z:
					modraBarvaHodnota= (255+modraBarvaHodnota+0.1f)%255;
					break;
				case GLFW_KEY_X:
					modraBarvaHodnota= (255+modraBarvaHodnota-0.1f)%255;
					break;
				case GLFW_KEY_M:
					prebarveniBool=!(prebarveniBool);
					break;
				case GLFW_KEY_L:
					pomocnaBul=!(pomocnaBul);
					break;




				}
			}
		}
	};
    
    private GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
    	@Override
    	public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 && 
            		(w != width || h != height)) {
            	width = w;
            	height = h;

            	if (textRenderer != null)
            		textRenderer.resize(width, height);

            }
        }
    };
    
    private GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
    	@Override
		public void invoke(long window, int button, int action, int mods) {
			mouseButton1 = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_1) == GLFW_PRESS;

			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS){
				mouseButton1 = true;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				ox = xBuffer.get(0);
				oy = yBuffer.get(0);

				//System.out.println("mous press on " + ox + " " + oy);
			}
			/*
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
				mouseButton1 = false;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);

				ox = x;
				oy = y;
        	}*/
		}
	};
	
    private GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
    	@Override
        public void invoke(long window, double x, double y) {
			if (mouseButton1) {

				ox = x;
				oy = y;
			}
    	}
    };
    
    private GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
        @Override public void invoke (long window, double dx, double dy) {
        }
    };
 
	@Override
	public GLFWKeyCallback getKeyCallback() {
		return keyCallback;
	}

	@Override
	public GLFWWindowSizeCallback getWsCallback() {
		return wsCallback;
	}

	@Override
	public GLFWMouseButtonCallback getMouseCallback() {
		return mbCallback;
	}

	@Override
	public GLFWCursorPosCallback getCursorCallback() {
		return cpCallbacknew;
	}

	@Override
	public GLFWScrollCallback getScrollCallback() {
		return scrollCallback;
	}

	void createBuffers() {
		// vertices are not shared among triangles (and thus faces) so each face
		// can have a correct normal in all vertices
		// also because of this, the vertices can be directly drawn as GL_TRIANGLES
		// (three and three vertices form one face) 
		// triangles defined in index buffer
				float[] cube = {


						-1, -1,     0, 1,
						1, -1,      1, 1,
						1, 1,       1, 0,
						-1, 1,      0, 0,
				};

				int[] indexBufferData = {0,1,2,0,2,3};

				
				
		OGLBuffers.Attrib[] attributes = {
				new OGLBuffers.Attrib("inPosition", 2),
				//new OGLBuffers.Attrib("inNormal", 3),
				new OGLBuffers.Attrib("inTextureCoordinates", 2)
		};

		buffers = new OGLBuffers(cube, attributes, indexBufferData);

		System.out.println(buffers.toString());
	}

	@Override
	public void init() {

		glClearColor(0,0,0,1);

		videoGrabber = new VideoGrabber("./res/textures/video2.mov");
		System.out.println("Video FPS: " + videoGrabber.getFPS());
		System.out.println("ss " + videoGrabber.getTotalFrameCount());
		System.out.println(videoGrabber.grabImage());
		videoImageFormat = new OpenCVImageFormat(3);
		createBuffers();
		buffer = videoGrabber.grabImage();

		createBuffers();
		
		shaderProgram = ShaderUtils.loadProgram("/mujShader3/mujStart1");
		
		glUseProgram(this.shaderProgram);
		

		
		renderTarget = new OGLRenderTarget(width, height);
		if (buffer != null) {
			System.out.format("%.1f s / %.1f s", videoGrabber.getCurrentVideoTime(), videoGrabber.getTotalVideoTime());
			System.out.println();
			//System.out.println( videoGrabber.getCurrentVideoTime()+"s / "+ videoGrabber.getTotalVideoTime()+ "s ");
			if (texture == null) {
				texture = new OGLTexture2D(videoGrabber.getWidth(), videoGrabber.getHeight(), videoImageFormat, buffer);
			} else {

				texture.setTextureBuffer(videoImageFormat, buffer);
			}
			//texture.bind(shaderProgram, "texture", 0);
		} else {
			System.out.println("sdadada");
			videoGrabber.rewind();
		}

		
		try {
			//texture = new OGLTexture2D("textures/foto.jpg");
			texture2= new OGLTexture2D("textures/back1.jpg");
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}


		//nastaveniShaderu();
		textureViewer = new OGLTexture2D.Viewer();
		textRenderer = new OGLTextRenderer(width, height);
		cervenaBarva = glGetUniformLocation(shaderProgram, "cervenaBarva");
		zelenaBarva = glGetUniformLocation(shaderProgram, "zelenaBarva");
		modraBarva = glGetUniformLocation(shaderProgram, "modraBarva");
		otoceni = glGetUniformLocation(shaderProgram, "otoceni");
		vysecKlic = glGetUniformLocation(shaderProgram, "vysecKlic");
		vysecBarva = glGetUniformLocation(shaderProgram, "vysecBarva");
}
	
	@Override
	public void display() {
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		glViewport(0, 0, width, height);

		if(!pomocnaBul){
			nastaveniShaderu();
		}


		if(pomocnaBul){
			// bind and draw
			buffers.draw(GL_TRIANGLES, shaderProgram);
			if (buffer != null) {
				System.out.format("%.1f s / %.1f s", videoGrabber.getCurrentVideoTime(), videoGrabber.getTotalVideoTime());
				System.out.println();
				//System.out.println( videoGrabber.getCurrentVideoTime()+"s / "+ videoGrabber.getTotalVideoTime()+ "s ");
				if (texture == null) {
					texture = new OGLTexture2D(videoGrabber.getWidth(), videoGrabber.getHeight(), videoImageFormat, buffer);
				} else {

					texture.setTextureBuffer(videoImageFormat, buffer);
				}
				//texture.bind(shaderProgram, "texture", 0);
			} else {
				System.out.println("sdadada");
				videoGrabber.rewind();
			}
		}

		// set the current shader to be used
		glUseProgram(shaderProgram);


		glUniform1f(cervenaBarva, cervenaBarvaHodnota);
		glUniform1f(zelenaBarva, zelenaBarvaHodnota);
		glUniform1f(modraBarva, modraBarvaHodnota);
		glUniform1f(otoceni, otoceniHodnota);
		glUniform1f(vysecKlic, vysecKlicovani);
		glUniform1f(vysecBarva, vysecPrebarveni);

		
		// set our render target (texture)
		renderTarget.bind();

		glClearColor(255, 255, 255, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		texture.bind(shaderProgram, "textureID", 0);
		texture2.bind(shaderProgram,"textureIP",1);
		


		
		// set the default render target (screen)
		glBindFramebuffer(GL_FRAMEBUFFER, 0);
		glViewport(0, 0, width, height);

		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		// use the result of the previous draw as a texture for the next
		//renderTarget.bindColorTexture(shaderProgram, "textureID", 0);
		//renderTarget.getColorTexture().bind(shaderProgram, "textureID", 0);
		// use the depth buffer from the previous draw as a texture for the next
		//renderTarget.bindDepthTexture(shaderProgram, "textureID", 0);
		
		//texRGB.bind(shaderProgram, "textureID", 0);
		

		buffers.draw(GL_TRIANGLES, shaderProgram);
		
		String text = new String(" [LMB] camera, WSAD, N - draw to texture");
		
		//textureViewer.view(textureColor, -1, -1, 1, height / (double) width);
		//textureViewer.view(texRGB, -1, 0, 1, height / (double) width);
		
		textRenderer.clear();
		textRenderer.setColor(Color.BLUE);
		textRenderer.addStr2D(2, 15, text);
		textRenderer.setColor(Color.BLUE);
		textRenderer.addStr2D(width-150, height-3,  "R: "+cervenaBarvaHodnota);

		textRenderer.addStr2D(width-100, height-3,  "G: "+zelenaBarvaHodnota);

		textRenderer.addStr2D(width-50, height-3,  "B: "+modraBarvaHodnota);
		textRenderer.draw();

		buffer = videoGrabber.grabImage();
	}

	private void nastaveniShaderu(){


		//System.out.println(ox + " sss");
		double pomerVysky = ox / width;
		double pomerSirky = oy / height;
		int vyska = (int)(texture.getWidth() * pomerVysky);
		int sirka = (int)(texture.getHeight() * pomerSirky);
		if (!(ox==0 && oy==0)){

			Point3D ssss = rgbtoYCrCb(getColor(texture,vyska,sirka));
			float ddd = vypocetOtoceni(ssss);
			System.out.println("yrb" + ssss.mul(100) + " otoceni " + ddd + " x: " + vypocetX(ssss.getY(),ssss.getZ(),ddd) + " z: " + vypocetZ(ssss.getY(),ssss.getZ(),ddd) + " xu: " + vypocetX(ssss.getY()-0.1,ssss.getZ()-0.1,ddd) + " zu: " + vypocetZ(ssss.getY()-0.1,ssss.getZ()-0.1,ddd));

			vysecKalkulator.vlozHodnotu(vypocetOtoceni(rgbtoYCrCb(getColor(texture,vyska,sirka))));



		}

		otoceniHodnota = vysecKalkulator.vratOtoceni();

		vysecKlicovani = vysecKalkulator.vratVysec();

		if(prebarveniBool){
			vysecPrebarveni = vysecKalkulator.vratPrebarveni();
		}else {
			vysecPrebarveni=0;
		}

		//System.out.println(otoceniHodnota);

	}

	//rgb: (116.0,197.0, 4.0,255.0)
	//yuv: ( 0.6,-0.1,-0.3, 1.0)
	//otoceni: -10.433102

	//vypocet otoceni
	private float vypocetOtoceni(Point3D a){
		//vypocet otoceni tak aby zadana hodnota po otoceni lezela na ose Z
		return (float) (((Math.PI)+(Math.atan((-a.getZ()+0.1)/(a.getY()-0.1)))));

		//pro osu X
		//return (float) (((Math.PI*2)+(Math.atan((a.getY())/a.getZ()))));
	}
	private double vypocetX(double cr, double cb, double otoceni) {
		return (cr * Math.cos(otoceni) - cb * Math.sin(otoceni));
	}

	private double vypocetZ(double cr, double cb, double otoceni) {
		return (cb *  Math.cos(otoceni) + cr *  Math.sin(otoceni));
	}

	private Col getColor(OGLTexture2D text){
		return getColor(text,200,200);
	}

	private Col getColor(OGLTexture2D text, int x,int y){
		//System.out.println("x " + x + " y " + y + " col " + new Col(text.toBufferedImage().getRGB(x,y)).getR()*255 +" "+ new Col(text.toBufferedImage().getRGB(x,y)).getG()*255 +" "+ new Col(text.toBufferedImage().getRGB(x,y)).getB()*255);

		return new Col(text.toBufferedImage().getRGB(x,y));
	}

	private Point3D rgbtoYCrCb(Col colorp) {
		//rozdeleni podle barev + prepocitani do rozsahu 0 az 255
		//nenasel jsem jiny vzorec -> moznost nahrazeni lepsim a dokonalejsim
		double r = (colorp.getR() * 255);
		double g = ( colorp.getG() * 255);
		double b = ( colorp.getB() * 255);

		//prepocitani barev na YUV
		double y = ((0.299*r + 0.587*g + 0.114*b));
		double cr = ((128 + 0.500*r - 0.419*g - 0.081*b)); //V
		double cb = ((128 - 0.169*r - 0.331*g + 0.500*b));  //U

		//propocitani y do rozsahu 0 az 1
		y = y/255;
		//prepocitani cr do rozsahu -0.5 az 0.5
		cr = ((cr / 255) - 0.5);
		//prepocitani cb do rozsahu -0.5 az 0.5
		cb = ((cb/255) - 0.5);

		//vraceni hodnot
		//System.out.println(y + " " + cr + " " + cb);
		return new Point3D(y, cr, cb);
	}
}