package com.ymsoftlabs.stlviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLView extends GLSurfaceView{

    private final GLRenderer mRenderer;

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    public GLView(Context context) {
        super(context);

        // create opengl es 2.0 context
        setEGLContextClientVersion(2);

        mRenderer = new GLRenderer();

        // set renderer for drawing on the surface view
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        float x = e.getX();
        float y = e.getY();

        switch (e.getAction()) {
            case MotionEvent.ACTION_MOVE:
                float dx = x - mPreviousX;
                float dy = y - mPreviousY;

                if (y < getHeight() / 2) { // below mid-line
                    dx = dx * - 1; // reverse
                }

                if (x > getWidth() / 2) { // right of mid-line
                    dy = dy * -1; // reverse
                }

                mRenderer.setAngle( mRenderer.getAngle() +
                        ((dx + dy) * TOUCH_SCALE_FACTOR));
                requestRender();
        }

        mPreviousX = x;
        mPreviousY = y;

        return true;
    }
}
