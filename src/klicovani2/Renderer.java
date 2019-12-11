package klicovani2;

import kmeans.Cluster;
import kmeans.KMeans;

import lwjglutils.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import pom.AbstractRenderer;
import transforms.Col;
import transforms.Point3D;
import transforms.Tuple3;

import java.awt.*;
import java.awt.image.AreaAveragingScaleFilter;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glUniform3i;
import static org.lwjgl.opengl.GL30.*;


/**
* 
* @author PGRF FIM UHK
* @version 2.0
* @since 2019-09-02
*/
public class Renderer extends AbstractRenderer{

	double ox, oy;
	boolean mouseButton1 = false;
	
OGLBuffers buffers;
	
	int shaderProgram, locMat, cervenaBarva,zelenaBarva,modraBarva,otoceni,cent1,cent2,cent3;
	
	OGLTexture2D texture, texture2;
	float cervenaBarvaHodnota =0;
	float zelenaBarvaHodnota = 0;
	float modraBarvaHodnota = 0;
	float otoceniHodnota=0;



	OGLRenderTarget renderTarget;
	boolean draw = false;

	OGLTexture2D.Viewer textureViewer;
	
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

		createBuffers();
		
		shaderProgram = ShaderUtils.loadProgram("/mujShader2/mujStart2");
		
		glUseProgram(this.shaderProgram);
		

		
		renderTarget = new OGLRenderTarget(width, height);


		
		try {
			texture = new OGLTexture2D("textures/foto.jpg");
			texture2= new OGLTexture2D("textures/back1.jpg");
		} catch (IOException e) {
			//  Auto-generated catch block
			e.printStackTrace();
		}
		cent1 = glGetUniformLocation(shaderProgram, "cent1");
		cent2 = glGetUniformLocation(shaderProgram, "cent2");
		cent3 = glGetUniformLocation(shaderProgram, "cent3");

		genericClass();
		textureViewer = new OGLTexture2D.Viewer();
		textRenderer = new OGLTextRenderer(width, height);
		//cervenaBarva = glGetUniformLocation(shaderProgram, "cervenaBarva");
		//zelenaBarva = glGetUniformLocation(shaderProgram, "zelenaBarva");
		//modraBarva = glGetUniformLocation(shaderProgram, "modraBarva");
		otoceni = glGetUniformLocation(shaderProgram, "otoceni");



}
	
	@Override
	public void display() {
		glDisable(GL_CULL_FACE);
		glEnable(GL_DEPTH_TEST);
		
		glViewport(0, 0, width, height);
		
		
		// set the current shader to be used
		glUseProgram(shaderProgram);


		//glUniform1f(cervenaBarva, cervenaBarvaHodnota);
		//glUniform1f(zelenaBarva, zelenaBarvaHodnota);
		//glUniform1f(modraBarva, modraBarvaHodnota);
		//glUniform1f(otoceni, otoceniHodnota);


		
		// set our render target (texture)
		renderTarget.bind();

		glClearColor(255, 255, 255, 1);
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
		
		texture.bind(shaderProgram, "textureID", 0);
		texture2.bind(shaderProgram,"textureIP",1);
		

		
		// bind and draw
		buffers.draw(GL_TRIANGLES, shaderProgram);
		
		//textureColor = renderTarget.getColorTexture();

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
		

		//textureViewer.view(textureColor, -1, -1, 1, height / (double) width);
		//textureViewer.view(texRGB, -1, 0, 1, height / (double) width);
		
		textRenderer.clear();

		textRenderer.setColor(Color.BLUE);
		textRenderer.addStr2D(width-150, height-3,  "R: "+cervenaBarvaHodnota);

		textRenderer.addStr2D(width-100, height-3,  "G: "+zelenaBarvaHodnota);

		textRenderer.addStr2D(width-50, height-3,  "B: "+modraBarvaHodnota);
		textRenderer.draw();
	}

	private void genericClass(){

		ArrayList list = new ArrayList<Point3D>();

		for(int i = 0; i<texture.getWidth();i=i+50){
			System.out.println(i);
			for(int u = 0; u<texture.getHeight();u=u+50){
				list.add(new Point3D(rgbToHsb(new Col(texture.toBufferedImage().getRGB(i,u)))));
				System.out.print(".");
			}
		}


		KMeans kMeans = new KMeans(list,3);
		ArrayList<Cluster> pointsClusters = kMeans.getPointsClusters();

        for (int i = 0 ; i < kMeans.k; i++)
        	System.out.println("Cluster " + i + ": " + pointsClusters.get(i));
           //System.out.println("Cluster " + i + ": " + pointsClusters.get(i));


		ArrayList<Point3D> listr =sort(kMeans.getPointsClusters());

		System.out.println();

		glUniform3fShort(cent1,listr.get(0));
		glUniform3fShort(cent2,listr.get(1));
		glUniform3fShort(cent3,listr.get(2));

		//otoceniHodnota =vypocetOtoceni(rgbtoYCrCb(getColor(texture)));

		//Point3D a =rgbToHsb(getColor(texture));
		//System.out.println("RGB: " +(getColor(texture).mul(255)) + " HSB: "+a);

	}






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

	private ArrayList<Point3D> sort(ArrayList<Cluster> a){
		ArrayList<Point3D> pointList = new ArrayList<>();

		for (int i=0;i<a.size();i++){
			pointList.add(a.get(i).getCentroid());
		}



		Collections.sort(pointList, (point,t1)->
				 Integer.valueOf((int)point.getX()).compareTo((int)t1.getX())
		);
		System.out.println(pointList);
		return pointList;

	}

	private void glUniform3fShort(int a,Point3D b){
		System.out.println(b);
		glUniform3f(a,
				(float) b.getX(),
				(float) b.getY(),
				(float) b.getZ());
	}

}