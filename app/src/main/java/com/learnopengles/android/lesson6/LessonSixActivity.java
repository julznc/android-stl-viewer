package com.learnopengles.android.lesson6;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.learnopengles.android.R;

import java.io.File;

public class LessonSixActivity extends Activity 
{
	/** Hold a reference to our GLSurfaceView */
	private LessonSixGLSurfaceView mGLSurfaceView;
	private LessonSixRenderer mRenderer;

    private FileDialog mFileDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.lesson_six);

		mGLSurfaceView = (LessonSixGLSurfaceView)findViewById(R.id.gl_surface_view);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

		if (supportsEs2) 
		{
			// Request an OpenGL ES 2.0 compatible context.
			mGLSurfaceView.setEGLContextClientVersion(2);
			
			final DisplayMetrics displayMetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

			// Set the renderer to our demo renderer, defined below.
			mRenderer = new LessonSixRenderer(this);
			mGLSurfaceView.setRenderer(mRenderer, displayMetrics.density);					
		} 
		else 
		{
			// This is where you could create an OpenGL ES 1.x compatible
			// renderer if you wanted to support both ES 1 and ES 2.
			return;
		}

        File path = new File(Environment.getExternalStorageDirectory() + "//DIR//");
        mFileDialog = new FileDialog(this, path);
        mFileDialog.setFileEndsWith(".stl");
        mFileDialog.setFileSelectedListener(new FileDialog.FileSelectedListener() {
            public void fileSelected(File file) {
                Log.e(getClass().getName(), "selected file " + file.toString());
            }
        });

        final ImageButton loadButton = (ImageButton) findViewById(R.id.loadButton);
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mFileDialog.showDialog();
            }
        });
	}

	@Override
	protected void onResume() 
	{
		// The activity must call the GL surface view's onResume() on activity
		// onResume().
		super.onResume();
		mGLSurfaceView.onResume();
	}

	@Override
	protected void onPause() 
	{
		// The activity must call the GL surface view's onPause() on activity
		// onPause().
		super.onPause();
		mGLSurfaceView.onPause();
	}
}