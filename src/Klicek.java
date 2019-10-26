
import lwjglutils.*;
import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

import java.io.*;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.stbi_image_free;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;
/**
 * GLSL sample:<br/>
 * Sending minimal geometry to GPU <br/>
 * Requires LWJGL3
 *
 * @author PGRF FIM UHK
 * @version 3.0
 * @since 2019-07-11
 */
public class Klicek {

    // The window handle
    private long window;
    int width, height;
    int vertexBuffer, indexBuffer;
    OGLTexture2D texture, textureColor, textureDepth,texRGB;
    int shaderProgram,shaderProgram2,test;
    float prom =0;

    OGLBuffers buffers;
    OGLTexture2D.Viewer textureViewer;
    OGLRenderTarget renderTarget;


    private void init() throws IOException {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(300, 300, "Hello World!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int width, int height) {
                if (width > 0 && height > 0 &&
                        (Klicek.this.width != width || Klicek.this.height != height)) {
                    Klicek.this.width = width;
                    Klicek.this.height = height;
            }
            }
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {

            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(0);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();


        OGLUtils.printOGLparameters();
        OGLUtils.printLWJLparameters();
        OGLUtils.printJAVAparameters();

        // Set the clear color
        glClearColor(0, 0, 0, 0);


        try {
            texture = new OGLTexture2D("textures/globe.jpg");

        } catch (IOException e) {

            e.printStackTrace();
        }

        texture.bind();

        createBuffers();


        shaderProgram = ShaderUtils.loadProgram("/mujShader/mujStart");
        /*shaderProgram2 = ShaderUtils.loadProgram("/lvl1basic/p01start/mujStart.vert",
                "/lvl1basic/p01start/mujStart.frag",
                null,null,null,null);*/


        // Shader program set
        glUseProgram(this.shaderProgram);
        //glUseProgram(this.shaderProgram2);
        textureViewer = new OGLTexture2D.Viewer();
        test = glGetUniformLocation(shaderProgram, "test");
    }

    void createBuffers() {
        // create and fill vertex buffer data
        float[] ctverec = {
                -1, -1,     0, 1,
                1, -1,      1, 1,
                1, 1,       1, 0,
                -1, 1,      0, 0,

        };

        int[] indexBufferData = { 0, 1, 2, 0,2,3 };
        lwjglutils.OGLBuffers.Attrib[] attributes = {
                new OGLBuffers.Attrib("inPosition", 2),
                //new OGLBuffers.Attrib("inNormal", 2),
                new OGLBuffers.Attrib("inTextureCoordinates", 2)

        };

        buffers = new OGLBuffers(ctverec, attributes, indexBufferData);
        System.out.println(buffers.toString());

    }

    void bindBuffers() {
        // internal OpenGL ID of a vertex shader input variable
        int locPosition = glGetAttribLocation(shaderProgram, "inPosition");

        glBindBuffer(GL_ARRAY_BUFFER, vertexBuffer);
        // bind the shader variable to specific part of vertex data (attribute)
        // - describe how many components of which type correspond to it in the
        // data, how large is one vertex (its stride in bytes) and at which byte
        // of the vertex the first component starts
        // 2 components, of type float, do not normalize (convert to [0,1]),
        // vertex of 8 bytes, start at the beginning (byte 0)
        glVertexAttribPointer(locPosition, 2, GL_FLOAT, false, 8, 0);
        glEnableVertexAttribArray(locPosition);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexBuffer);
    }

    private void loop() {
        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            glViewport(0, 0, width, height);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer

            // set the current shader to be used, could have been done only once (in
            // init) in this sample (only one shader used)
            glUseProgram(this.shaderProgram);
            // to use the default shader of the "fixed pipeline", call
            // glUseProgram(0);
            //glUseProgram(this.shaderProgram2);
            prom= (prom+0.1f)%255;
            glUniform1f(test, prom);

            // bind the vertex and index buffer to shader, could have been done only
            // once (in init) in this sample (only one geometry used)
            bindBuffers();
            //texture.bind();
            texture.bind(shaderProgram, "textureID", 0);

            buffers.draw(GL_TRIANGLES, shaderProgram);

            // draw
            glDrawElements(GL_TRIANGLES, 2, GL_UNSIGNED_SHORT, 0);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();
        }
    }

    public void run() {
        try {
            System.out.println("Hello LWJGL " + Version.getVersion() + "!");
            init();

            loop();

            // Free the window callbacks and destroy the window
            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            // Terminate GLFW and free the error callback
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }

    }



    public static void main(String[] args) {
        new Klicek().run();
    }

}
