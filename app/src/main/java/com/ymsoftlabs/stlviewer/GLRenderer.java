package com.ymsoftlabs.stlviewer;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLRenderer implements GLSurfaceView.Renderer {

    // used to move model from object space to world space
    private float[] mModelMatrix = new float[16];

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    private float[] mAccumulatedRotation = new float[16];
    private float[] mCurrentRotation = new float[16];
    private float[] mTemporaryMatrix = new float[16];

    private volatile float mAngle = 0.0f;
    public volatile float mDeltaX = 0.0f;
    public volatile float mDeltaY = 0.0f;

    private Cube mCube;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // background frame color
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        // Initialize the accumulated rotation matrix
        Matrix.setIdentityM(mAccumulatedRotation, 0);

        mCube = new Cube();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        GLES20.glViewport(0, 0, width, height);

        final float ratio = (float) width / height;

        Matrix.frustumM(mProjectionMatrix, 0,
                -ratio, ratio, // left , right
                -1.0f, 1.0f, // bottom, top
                3.0f, 7.0f); // near, far
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //float[] scratch = new float[16];

        // redraw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // camera position
        Matrix.setLookAtM(mViewMatrix, 0,
                0.0f, 0.0f, -3.0f, // eye XYZ
                0.0f, 0.0f, 0.0f, // look XYZ
                0.0f, 1.0f, 0.0f); // up XYZ

        // projection & view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Translate the object into the screen.
        Matrix.setIdentityM(mModelMatrix, 0);
        Matrix.translateM(mModelMatrix, 0, 0.0f, 0.8f, -3.5f);

        // Set a matrix that contains the current rotation.
        Matrix.setIdentityM(mCurrentRotation, 0);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaX, 0.0f, 1.0f, 0.0f);
        Matrix.rotateM(mCurrentRotation, 0, mDeltaY, 1.0f, 0.0f, 0.0f);
        mDeltaX = 0.0f;
        mDeltaY = 0.0f;

        // Multiply the current rotation by the accumulated rotation.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
        //Set the accumulated rotation to the result.
        System.arraycopy(mTemporaryMatrix, 0, mAccumulatedRotation, 0, 16);

        // Rotate the cube taking the overall rotation into account.
        Matrix.multiplyMM(mTemporaryMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
        System.arraycopy(mTemporaryMatrix, 0, mModelMatrix, 0, 16);

        // fix me
        Matrix.multiplyMM(mTemporaryMatrix, 0, mMVPMatrix, 0, mAccumulatedRotation, 0);
        mCube.draw(mTemporaryMatrix);
    }

    public static int loadShader(int type, String shaderCode) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);
        return shader;
    }
}
