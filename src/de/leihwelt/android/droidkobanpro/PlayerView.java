package de.leihwelt.android.droidkobanpro;

import android.app.Activity;
import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.hud.HUD;
import de.leihwelt.android.droidkobanpro.mainscreen.*;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.theme.Theme;
import de.leihwelt.android.gl.Font2D;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.Font2D.FontStyle;
import de.leihwelt.android.gl.Font2D.Text;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;
import de.leihwelt.android.objects.RenderObject;
import de.leihwelt.android.social.TwitterConnect;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.os.SystemClock;
import android.view.MotionEvent;

//public class PlayerView extends Activity {
//
//	private static final int LOADING_SCREEN_TIME = 1000;
//
//	public PlayerView() {
//
//		genericSetup();
//
//	}
//
//	private void genericSetup() {
//
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//
//		mGLSurfaceView = new PlayerViewView(this);
//		mGLSurfaceView.requestFocus();
//
//		setContentView(mGLSurfaceView);
//
//		StorageConnector.INSTANCE.setContext(this.getApplicationContext());
//		StorageConnector.INSTANCE.addStorageStrategy(AchievementUnlocker.INSTANCE);
//		StorageConnector.INSTANCE.addStorageStrategy(StatTracker.INSTANCE);
//		StorageConnector.INSTANCE.addStorageStrategy(AppSettings.INSTANCE);
//	}
//
//	@Override
//	protected void onResume() {
//
//		super.onResume();
//
//		mGLSurfaceView.onResume();
//
//		final long startTime = SystemClock.elapsedRealtime();
//		final PlayerView act = this;
//		Thread t = new Thread() {
//
//			@Override
//			public void run() {
//				while (SystemClock.elapsedRealtime() - startTime < LOADING_SCREEN_TIME) {
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//					}
//				}
//
//				act.finish();
//
//				Intent menu = new Intent(act, MainMenu.class);
//
//				act.startActivity(menu);
//			}
//
//		};
//		//t.start();
//	}
//
//	@Override
//	protected void onPause() {
//		super.onPause();
//		mGLSurfaceView.onPause();
//	}
//
//	private PlayerViewView mGLSurfaceView;
//}

class PlayerView extends GLSurfaceView {

	public PlayerView(Context ctx, AttributeSet attrs) {
		super(ctx, attrs);

		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);
		setZOrderOnTop(true);

		mRenderer = new PlayerViewRenderer(ctx);
		setRenderer(mRenderer);
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	public void onReset() {

	}

	@Override
	public boolean onTrackballEvent(MotionEvent e) {

		return true;
	}

	/*
	 * First check if the touch matches a UI control and if any modes have to be
	 * switched accordingly if not use the MotionEvent to control the actions
	 * for the current touchmode (play, move, zoom, rotate)
	 */
	@Override
	public boolean onTouchEvent(MotionEvent e) {

		return true;
	}

	private PlayerViewRenderer mRenderer;
}

class PlayerViewRenderer implements GLSurfaceView.Renderer {

	private Context context = null;

	private Texture loadingTexture = null;
	private FlatQuad quad = null;

	private int mWidth;
	private int mHeight;

	private AssetManager am = null;
	private ModelRenderer playerRenderer;

	private String modelNormal;

	private String tex;

	private float frameTime;
	private long lastFrameStop;

	public static float rot = -45.0f;

	public PlayerViewRenderer(Context context) {
		this.context = context;
	}

	public void set3DMode(GL10 gl) {
		float ratio = (float) mWidth / mHeight;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);
	}

	// TODO: fix this doesn't work
	public void set2DMode(GL10 gl, int width, int height) {

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		GLU.gluOrtho2D(gl, 0, width, height, 0);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glDisable(GL10.GL_DEPTH_TEST);

		gl.glColor4x(255, 255, 255, 255);

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
	}

	// TODO: fix this doesn't work
	public void end2DMode(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);

		gl.glPopMatrix();

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
	}

	public void onDrawFrame(GL10 gl) {

		try {
			checkReload(gl);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		set3DMode(gl);

		// set up modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_TEXTURE_2D);

		setupCamera(gl);

		gl.glTranslatef(0.0f, -1.0f, 0.0f);

		gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);

		gl.glEnable(GL10.GL_COLOR_MATERIAL);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glDisable(GL10.GL_CULL_FACE);

		this.playerRenderer.draw(gl);

		long frameStop = SystemClock.currentThreadTimeMillis();

		frameTime = (frameStop - lastFrameStop) * 0.001f;
		lastFrameStop = frameStop;

		rot += 70.0f * frameTime;

	}

	private void checkReload(GL10 gl) throws IOException {

		String model = PlayerModelSelector.INSTANCE.getPlayerModelNormal();
		String t = PlayerModelSelector.INSTANCE.getPlayerTex();

		if (!model.equals(this.modelNormal) || !t.equals(this.tex)) {

			this.modelNormal = model;
			this.tex = t;

			this.playerRenderer = new ModelRenderer(gl, am, modelNormal, tex);
		}

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {

		this.mWidth = width;
		this.mHeight = height;

		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glPushMatrix();
		gl.glLoadIdentity();

		GLU.gluOrtho2D(gl, 0, 320, 480, 0);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

	}

	private void setupCamera(GL10 gl) {

		GLU.gluLookAt(gl, 0.0f, 0.6f, -1.1f, 0, 0, 0, 0, 1, 0);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		// this.gl = gl;
		this.am = this.context.getAssets();

		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		// gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
		// gl.glClearColor(1.0f, 1.0f, 1.0f, 1);
		gl.glClearColor(0, 0, 0, 0);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);

		gl.glDisable(GL10.GL_DEPTH_TEST);

		// this.loadingTexture = Texture.load(gl,
		// am.open("textures/loading.png"));
		// this.quad = new FlatQuad(gl, 128);

		modelNormal = PlayerModelSelector.INSTANCE.getPlayerModelNormal();
		tex = PlayerModelSelector.INSTANCE.getPlayerTex();

		this.playerRenderer = new ModelRenderer(gl, am, modelNormal, tex);
		// this.playerRenderer.setScale(2.0f);

	}

}