package com.learnopengles.android.object;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class STLObject {
    private final Context mContext;

    private final FloatBuffer mPositions;
    private final FloatBuffer mNormals;

    private int mProgramHandle;

    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mColorHandle;

    private float[] mMVPMatrix = new float[16];
    private float[] mTemporaryMatrix = new float[16];

    private final int BYTESPERFLOAT = 4;
    private final int POSITIONDATASIZE = 3;
    private final int NORMALDATASIZE = 3;

    // X, Y, Z
    private final float[] mPositionData =
        {
                // Front face
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,

                // Right face
                1.0f, 1.0f, 1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                1.0f, -1.0f, -1.0f,
                1.0f, 1.0f, -1.0f,

                // Back face
                1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,
                1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, -1.0f,

                // Left face
                -1.0f, 1.0f, -1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, 1.0f, 1.0f,

                // Top face
                -1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,
                -1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, 1.0f,
                1.0f, 1.0f, -1.0f,

                // Bottom face
                1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
                1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, 1.0f,
                -1.0f, -1.0f, -1.0f,
        };

    private final float[] mNormalData =
        {
                // Front face
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,

                // Right face
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,
                1.0f, 0.0f, 0.0f,

                // Back face
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,
                0.0f, 0.0f, -1.0f,

                // Left face
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,
                -1.0f, 0.0f, 0.0f,

                // Top face
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,
                0.0f, 1.0f, 0.0f,

                // Bottom face
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f,
                0.0f, -1.0f, 0.0f
        };

    private static final String VERTEX_SHADER_CODE =
            "uniform mat4 u_MVPMatrix;" +
            "uniform mat4 u_MVMatrix;" +
            "attribute vec4 a_Position;" +
            "attribute vec3 a_Normal;" +
            "varying vec3 v_Position;" +
            "varying vec3 v_Normal;" +
            "varying vec4 v_Color;" +
            "void main() {" +
            "    v_Position = vec3(u_MVMatrix * a_Position);" +
            "    v_Normal = vec3(u_MVMatrix * vec4(a_Normal, 0.0));" +
            "    gl_Position = u_MVPMatrix * a_Position;" +
            "}";

    private static final String FRAGMENT_SHADER_CODE =
            "precision mediump float;" +
            "uniform vec3 u_LightPos;" +
            "varying vec3 v_Position;" +
            "varying vec3 v_Normal;" +
            "uniform vec4 u_Color;" +
            "void main() {" +
            "    float distance = length(u_LightPos - v_Position);" +
            "    vec3 lightVector = normalize(u_LightPos - v_Position);" +
            "    float diffuse = max(dot(v_Normal, lightVector), 0.0);" +
            "    diffuse = diffuse * (1.0 / (1.0 + (0.25 * distance)));" +
            "    diffuse = diffuse + 0.7;" +
            "    gl_FragColor = (diffuse * u_Color);" +
            "}";

    public STLObject(final Context context) {
        mContext = context;

        // Initialize the buffers.
        mPositions = ByteBuffer.allocateDirect(mPositionData.length * BYTESPERFLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(mPositionData).position(0);

        mNormals = ByteBuffer.allocateDirect(mNormalData.length * BYTESPERFLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormals.put(mNormalData).position(0);

        mProgramHandle = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgramHandle, loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE));
        GLES20.glAttachShader(mProgramHandle, loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE));
        GLES20.glLinkProgram(mProgramHandle);

        // Set program handles for drawing.
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_MVMatrix");
        mPositionHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Position");
        mNormalHandle = GLES20.glGetAttribLocation(mProgramHandle, "a_Normal");
        mColorHandle = GLES20.glGetUniformLocation(mProgramHandle, "u_Color");
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }

    public void draw(float[] modelMatrix, float[] viewMatrix, float[] projectionMatrix) {
        GLES20.glUseProgram(mProgramHandle);

        // Pass in the position information
        mPositions.position(0);
        GLES20.glVertexAttribPointer(mPositionHandle, POSITIONDATASIZE, GLES20.GL_FLOAT, false,
                0, mPositions);

        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Pass in the normal information
        mNormals.position(0);
        GLES20.glVertexAttribPointer(mNormalHandle, NORMALDATASIZE, GLES20.GL_FLOAT, false,
                0, mNormals);

        GLES20.glEnableVertexAttribArray(mNormalHandle);

        // uniform color
        GLES20.glUniform4f(mColorHandle, 0.0f, 1.0f, 1.0f, 1.0f); // rgba
        GLES20.glEnableVertexAttribArray(mColorHandle);

        // This multiplies the view matrix by the model matrix, and stores the result in the MVP matrix
        // (which currently contains model * view).
        Matrix.multiplyMM(mMVPMatrix, 0, viewMatrix, 0, modelMatrix, 0);

        // Pass in the modelview matrix.
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVPMatrix, 0);

        // This multiplies the modelview matrix by the projection matrix, and stores the result in the MVP matrix
        // (which now contains model * view * projection).
        Matrix.multiplyMM(mTemporaryMatrix, 0, projectionMatrix, 0, mMVPMatrix, 0);
        System.arraycopy(mTemporaryMatrix, 0, mMVPMatrix, 0, 16);

        // Pass in the combined matrix.
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        // Draw the cube.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, 36);
    }
}
