package com.ymsoftlabs.stlviewer;

import android.app.ProgressDialog;
import android.content.Context;
import android.opengl.GLES20;
import android.opengl.Matrix;
import android.os.AsyncTask;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.FloatBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class STLObject {

    private FloatBuffer mPositions;
    private FloatBuffer mNormals;

    private int mProgramHandle;

    private int mMVPMatrixHandle;
    private int mMVMatrixHandle;
    private int mPositionHandle;
    private int mNormalHandle;
    private int mColorHandle;

    private float[] mMVPMatrix = new float[16];
    private float[] mTemporaryMatrix = new float[16];

    private byte[] stlBytes = null;
    private List<Float> mVertexList = new ArrayList<Float>();
    private List<Float> mNormalList = new ArrayList<Float>();

    private int mVertexCount = 0;
    private volatile float[] mMaxMinXYZ = new float[6];

    private final int BYTESPERFLOAT = 4;
    private final int POSITIONDATASIZE = 3;
    private final int NORMALDATASIZE = 3;

    // X, Y, Z
    private float[] mPositionData =
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

    private float[] mNormalData =
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

    public STLObject() {

        // Initialize the buffers.
        mPositions = ByteBuffer.allocateDirect(mPositionData.length * BYTESPERFLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mPositions.put(mPositionData).position(0);

        mNormals = ByteBuffer.allocateDirect(mNormalData.length * BYTESPERFLOAT)
                .order(ByteOrder.nativeOrder()).asFloatBuffer();
        mNormals.put(mNormalData).position(0);

        mVertexCount = mPositionData.length / 3;
    }

    public void loadHandles() {

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
        if (mVertexCount < 3)
            return;

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
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, mVertexCount);
    }

    public boolean processSTL(final File stlFile, final Context context, final GLRenderer renderer) {
        mMaxMinXYZ[0] = Float.MIN_VALUE;
        mMaxMinXYZ[1] = Float.MIN_VALUE;
        mMaxMinXYZ[2] = Float.MIN_VALUE;
        mMaxMinXYZ[3] = Float.MAX_VALUE;
        mMaxMinXYZ[4] = Float.MAX_VALUE;
        mMaxMinXYZ[5] = Float.MAX_VALUE;

        final ProgressDialog progressDialog = prepareProgressDialog(context);

        final AsyncTask<File, Integer, Integer> task = new AsyncTask<File, Integer, Integer>() {

            @Override
            protected Integer doInBackground(File... notused) {
                mVertexList.clear();
                mNormalList.clear();
                mVertexCount = 0;
                int vertexCount;
                try {
                    stlBytes = new byte[(int)stlFile.length()];
                    DataInputStream dis = new DataInputStream(new FileInputStream(stlFile));
                    dis.readFully(stlBytes);

                    // determine if it is ASCII or binary STL
                    Charset charset = Charset.forName("UTF-8");
                    CharBuffer decode = charset.decode(ByteBuffer.wrap(stlBytes, 0, 80));
                    String headerString = decode.toString();
                    int index = 0;
                    while(Character.isWhitespace(headerString.charAt(index)) && index < 80) {
                        index++;
                    }
                    String firstWord = headerString.substring(index);
                    boolean isASCII = (firstWord.toLowerCase().startsWith("solid"));

                    if (isASCII) {
                        vertexCount = readASCII();
                    } else {
                        vertexCount = readBinary();
                    }


                } catch (Exception ignored) {
                    vertexCount = 0;
                }

                if ( vertexCount > 2 ) {
                    mPositionData = new float[mVertexList.size()];
                    for (int i=0; i<mVertexList.size(); i++) {
                        mPositionData[i] = mVertexList.get(i);
                    }

                    mNormalData = new float[mNormalList.size()];
                    for (int i=0; i<mNormalList.size(); i++) {
                        mNormalData[i] = mNormalList.get(i);
                    }

                    mPositions = ByteBuffer.allocateDirect(mPositionData.length * BYTESPERFLOAT)
                            .order(ByteOrder.nativeOrder()).asFloatBuffer();
                    mPositions.put(mPositionData).position(0);

                    mNormals = ByteBuffer.allocateDirect(mNormalData.length * BYTESPERFLOAT)
                            .order(ByteOrder.nativeOrder()).asFloatBuffer();
                    mNormals.put(mNormalData).position(0);

                    mVertexCount = vertexCount;

                }
                return vertexCount;
            }

            Integer readASCII() throws Exception {

                String stlText = new String(stlBytes);
                String[] stlLines = stlText.split("\n");
                progressDialog.setMax(stlLines.length);

                int vertexCount = 0;
                float nx=0.0f, ny=0.0f, nz=0.0f;

                for (int i = 0; i < stlLines.length; i++) {
                    String strLine = stlLines[i].trim();
                    if (strLine.startsWith("facet normal ")) {
                        strLine = strLine.replaceFirst("facet normal ", "");
                        String[] normalValue = strLine.split(" ");
                        nx = Float.parseFloat(normalValue[0]);
                        ny = Float.parseFloat(normalValue[1]);
                        nz = Float.parseFloat(normalValue[2]);
                    } else if (strLine.startsWith("vertex ")) {
                        strLine = strLine.replaceFirst("vertex ", "");
                        String[] vertexValue = strLine.split(" ");
                        float vx = Float.parseFloat(vertexValue[0]);
                        float vy = Float.parseFloat(vertexValue[1]);
                        float vz = Float.parseFloat(vertexValue[2]);

                        adjustMaxMin(vx, vy, vz);

                        mVertexList.add(vx);
                        mVertexList.add(vy);
                        mVertexList.add(vz);
                        ++vertexCount;
                        mNormalList.add(nx);
                        mNormalList.add(ny);
                        mNormalList.add(nz);
                    }

                    if (i % stlLines.length == 0) {
                        publishProgress(i);
                    }
                }

                return vertexCount;
            }

            Integer readBinary() throws Exception {
                int totalTriangles = getIntWithLittleEndian(80);
                int vertexCount = 0;
                progressDialog.setMax(totalTriangles);
                int offset = 80 +4;
                for (int i = 0; i < totalTriangles; i++) {
                    float nx = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;
                    float ny = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;
                    float nz = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;

                    for (int v = 0; v < 3; ++v) {
                        float vx = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;
                        float vy = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;
                        float vz = Float.intBitsToFloat(getIntWithLittleEndian(offset)); offset += 4;
                        adjustMaxMin(vx, vy, vz);
                        mVertexList.add(vx);
                        mVertexList.add(vy);
                        mVertexList.add(vz);
                        ++vertexCount;
                        mNormalList.add(nx);
                        mNormalList.add(ny);
                        mNormalList.add(nz);
                    }

                    offset += 2;

                    if (i % (totalTriangles>>5) == 0) {
                        publishProgress(i);
                    }

                }

                return vertexCount;
            }

            @Override
            public void onProgressUpdate(Integer... values) {
                progressDialog.setProgress(values[0]);
            }

            @Override
            protected void onPostExecute(Integer result) {
                progressDialog.dismiss();

                /*
                String minmax = String.format("x(%f,%f), y(%f,%f), z(%f,%f)",
                        mMaxMinXYZ[3], mMaxMinXYZ[0],
                        mMaxMinXYZ[4], mMaxMinXYZ[1],
                        mMaxMinXYZ[5], mMaxMinXYZ[2]);
                Log.d(getClass().getName(), "minmax: " + minmax);
                */
                float cX = ( mMaxMinXYZ[0] + mMaxMinXYZ[3] ) * 0.5f;
                float cY = ( mMaxMinXYZ[1] + mMaxMinXYZ[4] ) * 0.5f;
                float cZ = ( mMaxMinXYZ[2] + mMaxMinXYZ[5] ) * 0.5f;

                //lookAt(0.0f, 0.0f, 0.0f, 0.0f, 0.0f, -5.0f);
                renderer.lookAt(cX, cY, mMaxMinXYZ[2]*3.0f, 0.0f, 0.0f, 0.0f);
            }
        };

        try {
            task.execute(stlFile);
        } catch (Exception e) {
            return false;
        }

        return true;
    }

    private static ProgressDialog prepareProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("STL file loading...");
        progressDialog.setMax(0);
        progressDialog.setMessage("Please wait a moment.");
        progressDialog.setIndeterminate(false);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);

        progressDialog.show();

        return progressDialog;
    }

    private int getIntWithLittleEndian(int offset) {
        return (0xff & stlBytes[offset]) | ((0xff & stlBytes[offset + 1]) << 8) | ((0xff & stlBytes[offset + 2]) << 16) | ((0xff & stlBytes[offset + 3]) << 24);
    }

    private void adjustMaxMin(float x, float y, float z) {
        if (x > mMaxMinXYZ[0]) mMaxMinXYZ[0] = x;
        if (y > mMaxMinXYZ[1]) mMaxMinXYZ[1] = y;
        if (z > mMaxMinXYZ[2]) mMaxMinXYZ[2] = z;

        if (x < mMaxMinXYZ[3]) mMaxMinXYZ[3] = x;
        if (y < mMaxMinXYZ[4]) mMaxMinXYZ[4] = y;
        if (z < mMaxMinXYZ[5]) mMaxMinXYZ[5] = z;
    }

}
