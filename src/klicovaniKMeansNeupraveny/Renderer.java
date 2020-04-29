package klicovaniKMeansNeupraveny;



import kmeansNeupraveny.Cluster;
import kmeansNeupraveny.KMeans;
import lwjglutils.*;
import opencvutils.VideoGrabber;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import pom.AbstractRenderer;

import transforms.Col;
import transforms.Point3D;

import helpers.OpenCVImageFormat;

import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.util.ArrayList;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	private double ox, oy;
	private boolean mouseButton1 = false;
	private OGLBuffers buffers;
	private int shaderProgram,cent1,cent2,cent3,colorBack;
	private OGLTexture2D texture, texture2;
	private  VideoGrabber videoGrabber;
	private OpenCVImageFormat videoImageFormat;
	private ByteBuffer buffer;
	private OGLRenderTarget renderTarget;
	
	private GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
		@Override
		public void invoke(long window, int key, int scancode, int action, int mods) {
			if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if (action == GLFW_PRESS || action == GLFW_REPEAT){
				switch (key) {

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
			}
			
			if (button==GLFW_MOUSE_BUTTON_1 && action == GLFW_RELEASE){
				mouseButton1 = false;
				DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
				DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
				glfwGetCursorPos(window, xBuffer, yBuffer);
				double x = xBuffer.get(0);
				double y = yBuffer.get(0);

				ox = x;
				oy = y;
        	}
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

		//prirazeni popredi
		videoGrabber = new VideoGrabber("./res/textures/video2.mov");

		videoImageFormat = new OpenCVImageFormat(3);

		createBuffers();

		buffer = videoGrabber.grabImage();

		//nahrani shaderu
		shaderProgram = ShaderUtils.loadProgram("/mujShaderKMeansNeupraveny/mujStart");

		glUseProgram(this.shaderProgram);

		renderTarget = new OGLRenderTarget(width, height);

		try {
			//prirazeni pozadi
			texture2= new OGLTexture2D("textures/back1.jpg");
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		//umisteni promenych
		cent1 = glGetUniformLocation(shaderProgram, "cent1");
		cent2 = glGetUniformLocation(shaderProgram, "cent2");
		cent3 = glGetUniformLocation(shaderProgram, "cent3");
		colorBack = glGetUniformLocation(shaderProgram, "colorBack");

		nastaveniShaderu();

		textRenderer = new OGLTextRenderer(width, height);

		glUniform3fShort(colorBack,new Point3D(new Col(texture.toBufferedImage().getRGB(10,10))));
}
	
	@Override
	public void display() {
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		glViewport(0, 0, width, height);

		// set the current shader to be used
		glUseProgram(shaderProgram);

		//rozdeloveni videa na jednotlive snimky
		if (buffer != null) {
			if (texture == null) {
				texture = new OGLTexture2D(videoGrabber.getWidth(), videoGrabber.getHeight(), videoImageFormat, buffer);
			} else {
				texture.setTextureBuffer(videoImageFormat, buffer);
			}
		} else {
			videoGrabber.rewind();
		}

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

		buffers.draw(GL_TRIANGLES, shaderProgram);

		//nahrani dalsiho snimku
		buffer = videoGrabber.grabImage();
	}

	private void nastaveniShaderu(){

		//rozdeloveni videa na jednotlive snimky
		if (buffer != null) {
			if (texture == null) {
				texture = new OGLTexture2D(videoGrabber.getWidth(), videoGrabber.getHeight(), videoImageFormat, buffer);
			} else {
				texture.setTextureBuffer(videoImageFormat, buffer);
			}
		} else {
			videoGrabber.rewind();
		}

		//vytvoreni seznamu pro hodnoty vzorku
		ArrayList list = new ArrayList<Point3D>();

		//prohledani textury pro vytvoreni k-means
		for(int i = 0; i<texture.getWidth();i=i+100){
			System.out.println(i);
			for(int u = 0; u<texture.getHeight();u=u+100){
				list.add(new Point3D(rgbToHsb(new Col(texture.toBufferedImage().getRGB(i,u)))));
				System.out.println(".");
			}
			System.out.println(i);
		}

		//vytvoreni k-means
		KMeans kMeans = new KMeans(list,3);
		ArrayList<Cluster> pointsClusters = kMeans.getClusters();

		//vypsani k-means do konzole
        for (int i = 0 ; i < kMeans.getClustersCount(); i++){
			System.out.println("Cluster " + i + ": " + pointsClusters.get(i).getCentroid());
		}

		//odeslani centoid do shaderu
		glUniform3fShort(cent1,pointsClusters.get(0).getCentroid());
		glUniform3fShort(cent2,pointsClusters.get(1).getCentroid());
		glUniform3fShort(cent3,pointsClusters.get(2).getCentroid());
	}






	//prepocet na hsb barevny model
	private Point3D rgbToHsb(Col a)	{
		float r = ((float)a.getR());
		float g = ((float)a.getG());
		float b = ((float)a.getB());

		float maxn = Math.max(r, Math.max(g, b));
		float minn = Math.min(r, Math.min(g, b));

		float h = 0.0f;
		if (maxn == r && g >= b)
		{
			if (maxn - minn == 0.0)
			{
				h = 0.0f;
			}else
			{
				h = (float) (60.0 * ((g - b) / (maxn - minn)));
			}
		}else if (maxn == r && g < b)
		{
			h = (float)(60.0 * ((g - b) / (maxn - minn)) + 360.0);
		}else if (maxn == g)
		{
			h = (float)(60.0 * ((b - r) / (maxn - minn)) + 120.0);
		}else if (maxn == b)
		{
			h = (float)(60.0 * ((r - g) / (maxn - minn)) + 240.0);
		}

		float s = (float) ((maxn == 0.0) ? 0.0 : (1.0 - ((float)minn / (float)maxn)));

		return new Point3D((float)h, (float)(s*100.0), (float)(maxn*100.0));
	}



	//pomocna promena pro posilani promenych do shaderu
	private void glUniform3fShort(int a,Point3D b){
		//System.out.println(b);
		glUniform3f(a,
				(float) b.getX(),
				(float) b.getY(),
				(float) b.getZ());
	}

}