package com.ymsoftlabs.stlviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class GLView extends GLSurfaceView
{	
	private GLRenderer mRenderer;

    private static final int TOUCH_NONE = 0;
    private static final int TOUCH_DRAG = 1;
    private static final int TOUCH_ZOOM = 2;

    private int mTouchMode = TOUCH_NONE;
	
	// Offsets for touch events	 
    private float mPreviousX;
    private float mPreviousY;
    
    private float mDensity;
        	
	public GLView(Context context)
	{
		super(context);		
	}
	
	public GLView(Context context, AttributeSet attrs)
	{
		super(context, attrs);		
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) 
	{
		if (null == event) {
            return false;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                if ( TOUCH_NONE == mTouchMode && 1==event.getPointerCount() ) {
                    mTouchMode = TOUCH_DRAG;
                    mPreviousX = event.getX();
                    mPreviousY = event.getY();
                }
                break;
            case MotionEvent.ACTION_UP:
                if ( TOUCH_DRAG == mTouchMode ) {
                    mTouchMode = TOUCH_NONE;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if ( TOUCH_DRAG == mTouchMode ) {

                    float x = event.getX();
                    float y = event.getY();

                    float deltaX = (x - mPreviousX) / mDensity / 2f;
                    float deltaY = (y - mPreviousY) / mDensity / 2f;

                    mPreviousX = x;
                    mPreviousY = y;

                    if (null == mRenderer) return false;

                    if ( 1 == event.getPointerCount() ) { // rotate
                        mRenderer.mDeltaX += deltaX;
                        mRenderer.mDeltaY += deltaY;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_POINTER_UP:
                if ( TOUCH_ZOOM == mTouchMode ) {
                    mTouchMode = TOUCH_NONE;
                }
                break;
            default:
                return super.onTouchEvent(event);
        }

        return true;
	}

	// Hides superclass method.
	public void setRenderer(GLRenderer renderer, float density)
	{
		mRenderer = renderer;
		mDensity = density;
		super.setRenderer(renderer);
	}
}
