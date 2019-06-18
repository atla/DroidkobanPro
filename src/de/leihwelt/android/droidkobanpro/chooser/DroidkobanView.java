package de.leihwelt.android.droidkobanpro.chooser;

import javax.microedition.khronos.opengles.GL;

import android.content.Context;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class DroidkobanView extends GLSurfaceView {

	public DroidkobanView(Context context, int model) {
		super(context);

		mRenderer = new DroidkobanChooserRenderer(context, model);

		setRenderer(mRenderer);

	}

	@Override
	public void onResume() {

	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		return super.onKeyUp(keyCode, event);
	}

	@Override
	public boolean onTrackballEvent(MotionEvent e) {

		return super.onTrackballEvent(e);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {

		float x = e.getX();

		switch (e.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mPreviousX = x;
			break;

		case MotionEvent.ACTION_UP:
			float xDiff = (mPreviousX - x);

			DroidkobanChooserRenderer.offsetTarget = DroidkobanChooserRenderer.offsetTarget + (xDiff < 0.0f ? -1 : 1);

			if (DroidkobanChooserRenderer.offsetTarget < 0)
				mRenderer.offsetTarget = 0;
			else if (DroidkobanChooserRenderer.offsetTarget > DroidkobanChooser.MAX_TEXTURE_CHOICES - 1)
				mRenderer.offsetTarget = DroidkobanChooser.MAX_TEXTURE_CHOICES - 1;
			else
				mRenderer.rot = -45.0f;

			break;
		}
		return true;
	}

	private DroidkobanChooserRenderer mRenderer;
	private float mPreviousX = 0.0f;

}