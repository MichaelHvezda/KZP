package klicovaniKMeansNew;


import helpers.OpenCVImageFormat;
import kmeansUpraveny.Cluster;
import kmeansUpraveny.KMeans;
import lwjglutils.*;
import opencvutils.VideoGrabber;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import pom.AbstractRenderer;
import pom.Stopwatch;
import transforms.Col;
import transforms.Point3D;
import transforms.Vec3D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

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
	private int shaderProgramStart,shaderProgramKonec, cent1,cent2,cent3,colorBack;
	private OGLTexture2D texture, texture2;
	private VideoGrabber videoGrabber;
	private OpenCVImageFormat videoImageFormat;
	private ByteBuffer buffer;
	private OGLRenderTarget renderTarget;

	public Point3D[] kmeans = new Point3D[3];
	public Stopwatch stopwatch;

	private OGLRenderTarget saveRenderTarget;
	private int framePosition = 0;


	private final GLFWKeyCallback   keyCallback = new GLFWKeyCallback() {
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
    
    private final GLFWWindowSizeCallback wsCallback = new GLFWWindowSizeCallback() {
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
    
    private final GLFWMouseButtonCallback mbCallback = new GLFWMouseButtonCallback () {
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
	
    private final GLFWCursorPosCallback cpCallbacknew = new GLFWCursorPosCallback() {
    	@Override
        public void invoke(long window, double x, double y) {
			if (mouseButton1) {
				ox = x;
				oy = y;
			}
    	}
    };


    
    private final GLFWScrollCallback scrollCallback = new GLFWScrollCallback() {
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
		stopwatch = new Stopwatch();
		stopwatch.startTime();
		glClearColor(0,0,0,1);

		//prirazeni popredi
		videoGrabber = new VideoGrabber("./res/textures/video3.mp4");

		videoImageFormat = new OpenCVImageFormat(3);

		createBuffers();
		createKmeans();
		buffer = videoGrabber.grabImage();

		//nahrani shaderu
		shaderProgramStart = ShaderUtils.loadProgram("/newShaderKMeans/mujStart");
		shaderProgramKonec = ShaderUtils.loadProgram("/newShaderKMeans/mujKonec");

		glUseProgram(this.shaderProgramStart);
		width = 800;
		height = 600;

		renderTarget = new OGLRenderTarget(width, height, 3);

		try {
            //prirazeni pozadi
			texture2= new OGLTexture2D("textures/back1.jpg");
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}

		//umisteni promenych
		cent1 = glGetUniformLocation(shaderProgramStart, "cent1");
		cent2 = glGetUniformLocation(shaderProgramStart, "cent2");
		cent3 = glGetUniformLocation(shaderProgramStart, "cent3");

		textRenderer = new OGLTextRenderer(width, height);

		stopwatch.resetTime();
}
	
	@Override
	public void display() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);

		//rozdeloveni videa na jednotlive snimky
		if (buffer != null) {
			if (texture == null) {
				texture = new OGLTexture2D(videoGrabber.getWidth(), videoGrabber.getHeight(), videoImageFormat, buffer);
			} else {
				texture.setTextureBuffer(videoImageFormat, buffer);
			}
			framePosition++;
		} else {
			videoGrabber.rewind();
			stopwatch.resetTime();
			throw new RuntimeException("End it");
		}

		nastaveniShaderuStart();
		nastaveniShaderuEnd();

		//nahrani dalsiho snimku
		buffer = videoGrabber.grabImage();
	}

	private void nastaveniShaderuStart(){
		// set the current shader to be used
		glUseProgram(shaderProgramStart);


		// set our render target (texture)
		renderTarget.bind();

		glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		glUniform3fShort(cent1,kmeans[0]);
		glUniform3fShort(cent2,kmeans[1]);
		glUniform3fShort(cent3,kmeans[2]);
		texture.bind(shaderProgramStart, "uTexture0", 0);

		glViewport(0, 0, width, height);

		buffers.draw(GL_TRIANGLES, shaderProgramStart);

		calculateKmeans();
	}
	public void calculateKmeans(){
		for (int i = 0; i < 3; i++)
		{
			var avrColor = renderTarget.getColorTexture(i).getAvrColor();

			kmeans[i]= new Point3D(avrColor[0],avrColor[1],avrColor[2],1);
		}
	}


	private void nastaveniShaderuEnd(){
		glUseProgram(shaderProgramKonec);

		glBindFramebuffer(GL_FRAMEBUFFER, 0);

		glClearColor(152, 124, 126, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

		texture.bind(shaderProgramKonec, "uTexture0", 0);
		texture2.bind(shaderProgramKonec,"uTexture1",1);
		renderTarget.getColorTexture(2).bind(shaderProgramKonec,"uTexture2",2);
		// set the default render target (screen)
		buffers.draw(GL_TRIANGLES, shaderProgramKonec);

	}

	//pomocna promena pro posilani promenych do shaderu
	private void glUniform3fShort(int a,Point3D b){
		//System.out.println(b);
		glUniform3f(a,
				(float) b.getX(),
				(float) b.getY(),
				(float) b.getZ());
	}
	void createKmeans(){
		kmeans[0] = new Point3D(0.7f, 0.2f, 0.5f);
		kmeans[1] = new Point3D(1f, 0.5f, 0.7f);
		kmeans[2] = new Point3D(0.5f, 0.7f, 0.2f);
	}
}