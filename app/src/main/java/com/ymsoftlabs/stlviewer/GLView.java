package com.ymsoftlabs.stlviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLView extends GLSurfaceView{

    private final GLRenderer mRenderer;

    public GLView(Context context) {
        super(context);

        // create opengl es 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new GLRenderer();

        // set renderer for drawing on the surface view
        setRenderer(mRenderer);
    }
}
