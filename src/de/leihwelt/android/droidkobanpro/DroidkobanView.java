package de.leihwelt.android.droidkobanpro;

import javax.microedition.khronos.opengles.GL;

import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.gl.MatrixTrackingGL;
import android.content.Context;
import android.content.res.Configuration;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;

public class DroidkobanView extends GLSurfaceView {

	// private long lastMotionEvent = SystemClock.uptimeMillis();
	private DroidkobanGame game = DroidkobanGame.INSTANCE;
	private long lastMotionEvent = SystemClock.uptimeMillis();

	private boolean menuActivated = false;

	// private Context context = null;

	public DroidkobanRenderer getRenderer (){
		return this.mRenderer;
	}
	
	public DroidkobanView(Context context) {
		super(context);

		mRenderer = new DroidkobanRenderer(context);

		// setDebugFlags(DEBUG_CHECK_GL_ERROR | DEBUG_LOG_GL_CALLS);

		setRenderer(mRenderer);

		this.setGLWrapper(new GLSurfaceView.GLWrapper() {

			@Override
			public GL wrap(GL gl) {
				return new MatrixTrackingGL(gl);
			}
		});

	}

	public void onBackgroundsEnabled() {
		this.mRenderer.onBackgroundsEnabled();
	}

	@Override
	public void onResume() {
		
		this.mRenderer.moveByPath = StorageConnector.INSTANCE.getPreferences().getBoolean("dkMoveByPath", false);
		this.mRenderer.enable2Dmode = StorageConnector.INSTANCE.getPreferences().getBoolean("mode2d", false);
		
		super.onResume();
	}

	public boolean onKeyUp(int keyCode, KeyEvent event) {

		if (mRenderer.isDataLoaded()) {

			switch (keyCode) {
			case KeyEvent.KEYCODE_DPAD_DOWN:
			case KeyEvent.KEYCODE_S:
				game.movements.add(MovementCommand.DOWN);
				break;
			case KeyEvent.KEYCODE_DPAD_UP:
			case KeyEvent.KEYCODE_W:
				game.movements.add(MovementCommand.UP);
				break;
			case KeyEvent.KEYCODE_DPAD_LEFT:
			case KeyEvent.KEYCODE_A:
				game.movements.add(MovementCommand.LEFT);
				break;
			case KeyEvent.KEYCODE_DPAD_RIGHT:
			case KeyEvent.KEYCODE_D:
				game.movements.add(MovementCommand.RIGHT);
				break;
			}

		}

		return super.onKeyUp(keyCode, event);
	}

	// public void onReset() {
	// mRenderer.clearObjects();
	// game.resetCurrentLevel();
	// mRenderer.setupObjects();
	// }

	@Override
	public boolean onTrackballEvent(MotionEvent e) {

		if (mRenderer.isDataLoaded()) {

			long current = SystemClock.uptimeMillis();

			if (current - lastMotionEvent > 150) {

				float x = e.getX() * TRACKBALL_SCALE_FACTOR;
				float y = e.getY() * TRACKBALL_SCALE_FACTOR;

				if (Math.abs(x) > Math.abs(y) && Math.abs(x) > 5) {
					game.movements.add((x < 0.0f) ? MovementCommand.LEFT : MovementCommand.RIGHT);
				} else if (Math.abs(y) > 5) {
					game.movements.add((y < 0.0f) ? MovementCommand.UP : MovementCommand.DOWN);
				}

				lastMotionEvent = current;
			}
		}
		return true;
	}

	/*
	 * First check if the touch matches a UI control and if any modes have to be
	 * switched accordingly if not use the MotionEvent to control the actions
	 * for the current touchmode (play, move, zoom, rotate)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		if (mRenderer.isDataLoaded()) {

			float x = e.getX();
			float y = e.getY();

			if (mRenderer.showFgTexture > 0) {
				DroidkobanGame.INSTANCE.tapOnInfo();
			} else {// if (mRenderer.pick) {

				switch (e.getAction()) {

				case MotionEvent.ACTION_MOVE:

					float dx = x - mPreviousX;
					float dy = y - mPreviousY;

					switch (mRenderer.touchMode) {
					case DroidkobanRenderer.MODE_ROTATE:
						// mRenderer.userRotateAngle += dx * TOUCH_SCALE_FACTOR
						// *
						// 0.5;
						break;

					case DroidkobanRenderer.MODE_ZOOM:

						mRenderer.increaseZoom(dy * TOUCH_SCALE_FACTOR * 0.05);

						break;

					case DroidkobanRenderer.MODE_MOVE:

						mRenderer.mX += dx * TOUCH_SCALE_FACTOR * 0.1;
						mRenderer.mZ += dy * TOUCH_SCALE_FACTOR * 0.1;

						if (mRenderer.mX < -5.0f)
							mRenderer.mX = -5.0f;
						else if (mRenderer.mX > 5.0f)
							mRenderer.mX = 5.0f;

						if (mRenderer.mZ < -7.0f)
							mRenderer.mZ = -7.0f;
						else if (mRenderer.mZ > 3.0f)
							mRenderer.mZ = 3.0f;
						break;

					case DroidkobanRenderer.MODE_PLAY:

						if (this.mRenderer.pick && this.mRenderer.moveByPath) {
							mRenderer.pick = true;
							mRenderer.pickX = (int) x;
							mRenderer.pickY = (int) y;
						}

						break;
					}

					break;

				case MotionEvent.ACTION_DOWN:

					menuActivated = false;

					if (!mRenderer.pick)
						menuActivated = mRenderer.hud.onTouchEvent(e);

					
					Log.v("DOWN", "MENU ACTIVATED: " + menuActivated);

					if (!menuActivated && mRenderer.touchMode == DroidkobanRenderer.MODE_PLAY) {
						mRenderer.mTapX = x;
						mRenderer.mTapY = y;

						if (this.mRenderer.moveByPath) {
							mRenderer.pick = true;
							mRenderer.pickX = (int) x;
							mRenderer.pickY = (int) y;
						}
					} else {
						mRenderer.pick = false;
					}
					break;

				case MotionEvent.ACTION_UP:

					menuActivated = false;

					if (!mRenderer.pick)
						menuActivated = mRenderer.hud.onTouchEvent(e);

					if (!menuActivated && mRenderer.touchMode == DroidkobanRenderer.MODE_PLAY) {

						if (this.mRenderer.moveByPath) {
							mRenderer.pick = false;
							mRenderer.movePath = true;
						} else {
							float diffX = x - mRenderer.mTapX;
							float diffY = y - mRenderer.mTapY;

							if (Math.abs(diffX) > Math.abs(diffY) && Math.abs(diffX) > 10)
								game.movements.add((diffX > 0.0f) ? MovementCommand.RIGHT : MovementCommand.LEFT);
							else if (Math.abs(diffY) > 10)
								game.movements.add((diffY > 0.0f) ? MovementCommand.DOWN : MovementCommand.UP);
						}
					}

					mRenderer.pick = false;

					break;
				}
			}

			mPreviousX = x;
			mPreviousY = y;
			
			try {
				Thread.sleep(20);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
		}
		return true;
	}

	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private final float TRACKBALL_SCALE_FACTOR = 36.0f;
	private DroidkobanRenderer mRenderer;
	private float mPreviousX;
	private float mPreviousY;

}