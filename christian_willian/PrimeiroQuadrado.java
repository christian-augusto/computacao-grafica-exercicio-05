package christian_willian;

import com.jogamp.opengl.*;
import com.jogamp.common.nio.Buffers;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.Animator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.FloatBuffer;

public class PrimeiroQuadrado implements GLEventListener {

    private GL2 gl;
    private static int width = 600;
    private static int height = 600;

    private int vPosition = 0;

    private final int NUMBUFFERS = 1;
    private int[] myBuffers = new int[NUMBUFFERS];

    private int numVertices;

    private int shaderProgram;
    private int vertShader;
    private int fragShader;

    public static void main(String[] args) {

        GLCapabilities caps = new GLCapabilities(GLProfile.get(GLProfile.GL2));

        GLWindow glWindow = GLWindow.create(caps);

        // Setup the GLWindow
        glWindow.setTitle("Quadrado");
        glWindow.setSize(width, height);
        glWindow.setUndecorated(false);
        glWindow.setPointerVisible(true);
        glWindow.setVisible(true);

        // Finally we connect the GLEventListener application code to the GLWindow.

        glWindow.addGLEventListener(new PrimeiroQuadrado());
        Animator animator = new Animator();
        animator.add(glWindow);
        animator.start();
    }

    public void init(GLAutoDrawable drawable) {
        System.out.println("init");

        gl = drawable.getGL().getGL2();

        float[] vertices = {
                -0.50f, -0.50f, // A
                0.50f, -0.50f, // B
                0.50f, 0.50f, // C
                -0.50f, 0.50f, // D
        };

        FloatBuffer verticesFB = Buffers.newDirectFloatBuffer(vertices);

        numVertices = vertices.length / 2;

        gl.glGenBuffers(NUMBUFFERS, myBuffers, 0);
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, myBuffers[0]);
        gl.glBufferData(GL2.GL_ARRAY_BUFFER, vertices.length * (Float.SIZE / Byte.SIZE), verticesFB,
                GL2.GL_STATIC_DRAW);
        verticesFB = null; // It is OK to release CPU vertices memory after transfer to GPU

        vertShader = loadShader(GL2.GL_VERTEX_SHADER, "./res/primitivas.vert");
        fragShader = loadShader(GL2.GL_FRAGMENT_SHADER, "./res/primitivas.frag");

        // Each shaderProgram must have one vertex shader and one fragment shader.
        shaderProgram = gl.glCreateProgram();
        gl.glAttachShader(shaderProgram, vertShader);
        gl.glAttachShader(shaderProgram, fragShader);

        gl.glLinkProgram(shaderProgram);

        gl.glUseProgram(shaderProgram);

        gl.glVertexAttribPointer(vPosition, 2, GL2.GL_FLOAT, false, 0, 0);

        gl.glEnableVertexAttribArray(vPosition);
        System.out.println("vPosition: " + vPosition);
    }

    public void reshape(GLAutoDrawable drawable, int x, int y, int w, int h) {
        System.out.println("Reshape " + x + "/" + y + " " + w + "x" + h);
    }

    public void display(GLAutoDrawable drawable) {
        System.out.println("display");

        // Clear screen
        gl.glClear(GL2.GL_COLOR_BUFFER_BIT);

        // gl.glBindVertexArray(vaos[0]);

        gl.glDrawArrays(GL2.GL_QUADS, 0, numVertices);
        gl.glFlush();
    }

    public void dispose(GLAutoDrawable drawable) {
        System.out.println("Dispose");
        System.out.println("cleanup, remember to release shaders");
        gl.glUseProgram(shaderProgram);
        gl.glDetachShader(shaderProgram, vertShader);
        gl.glDeleteShader(vertShader);
        gl.glDetachShader(shaderProgram, fragShader);
        gl.glDeleteShader(fragShader);
        gl.glDeleteProgram(shaderProgram);
        System.exit(0);
    }

    public int loadShader(int type, String filename) {
        System.out.println("loadShader");
        int shader;

        // Create GPU shader handle
        shader = gl.glCreateShader(type);

        // Read shader file
        String[] vlines = new String[1];
        vlines[0] = "";
        String line;

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            while ((line = reader.readLine()) != null) {
                vlines[0] += line + "\n"; // insert a newline character after each line
            }
            reader.close();
        } catch (Exception e) {
            System.err.println("Fail reading shader file");
        }

        gl.glShaderSource(shader, vlines.length, vlines, null);

        // Compile shader
        gl.glCompileShader(shader);

        return shader;
    }

}
