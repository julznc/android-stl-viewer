package com.ymsoftlabs.stlviewer;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class Triangle {
    private FloatBuffer vertexBuffer;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords [] = { // counter clockwise order
             0.0f,  0.622008459f, 0.0f, // top
            -0.5f, -0.311004243f, 0.0f, // bottom left
             0.5f, -0.311004243f, 0.0f  // bottom right
    };

    // rgba color
    float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

    public Triangle() {
        // vertex byte buffer for shape coordinates // 4bytes per float
        ByteBuffer bb = ByteBuffer.allocateDirect(triangleCoords.length * 4);
        bb.order(ByteOrder.nativeOrder()); // device hw's native byte order

        vertexBuffer = bb.asFloatBuffer(); // float buffer from byte buffer
        vertexBuffer.put(triangleCoords); // add the coordinates to the buffer
        vertexBuffer.position(0); // first coordinate
    }
}
