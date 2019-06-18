package de.leihwelt.android.droidkobanpro.chooser;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.Random;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import android.app.*;
import android.app.AlertDialog.*;
import android.content.DialogInterface.*;
import android.content.DialogInterface;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.ModelRenderer;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.Font2D.Text;

public class DroidkobanChooserRenderer implements GLSurfaceView.Renderer {

	private Context mContext;
	private ModelRenderer[] playerRenderer = new ModelRenderer[DroidkobanChooser.MAX_TEXTURE_CHOICES];
	// private RenderObject playerObject = null;

	public float mAngleX;
	public float mAngleY;

	public float userRotateAngle = 0.0f;

	public float mX = 0.0f;
	public float mZ = 0.0f;

	public float mCameraX = 0.0f;
	public float mCameraZ = 0.0f;

	public float mTapX = 0.0f;
	public float mTapY = 0.0f;

	private int width;
	private int height;

	public float zoom = 2.5f;

	public boolean useCulling = true;

	private boolean portrait = true;
	private int width2D;
	private float left;
	private float bottom;
	private float top;
	private float near;
	private float far;
	private float right;
	private AssetManager am;
	boolean reload = false;

	public boolean pick = false;
	public int pickX = -1;
	public int pickY = -1;
	public boolean movePath = false;
	private float frameTime;
	private long lastFrameStop;

	public float offset = 0.0f;
	private Texture background;
	private FlatQuad backgroundImage;
	private float size;
	public static int offsetTarget = 0;

	public static float rot = -45.0f;

	private int model = 0;

	public DroidkobanChooserRenderer(Context context, int model) {
		this.mContext = context;
		this.model = model;

	}

	public void set3DMode(GL10 gl) {
		// float ratio = (width < height) ? (float) width / height :
		// (float)height/width;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);

		gl.glFrustumf(left, right, bottom, top, near, far);

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

	public void increaseZoom(double d) {
		this.zoom += d;

		if (this.zoom < 0.0f)
			this.zoom = 0.0f;
		else if (this.zoom > 12.0f)
			this.zoom = 12.0f;

		// this.useCulling = (this.zoom < 6.0f);
	}

	public void onDrawFrame(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		set3DMode(gl);

		// set up modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_TEXTURE_2D);

		// if (apps.isEnableBackgrounds())

		setupCamera(gl);

		renderLevel(gl);

		// renderUI(gl);

		long frameStop = SystemClock.currentThreadTimeMillis();

		frameTime = (frameStop - lastFrameStop) * 0.001f;
		lastFrameStop = frameStop;

		float diff = (offsetTarget * 1.5f) - offset;

		if (diff > 0.005f) {
			offset += 35.0f * frameTime * (Math.abs(diff * 0.7f));
		} else if (diff < 0.05f) {
			offset -= 35.0f * frameTime * (Math.abs(diff * 0.7f));
		} else {
			offset = offsetTarget * 1.5f;
		}

		rot += 70.0f * frameTime;

	}

	private void setupCamera(GL10 gl) {

		GLU.gluLookAt(gl, 0, 0.5f, 1.5f, 0, 0.0f, 0, 0, 1, 0);
	}

	private void renderLevel(GL10 gl) {

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glPushMatrix();

		gl.glTranslatef(-offset, -0.5f, 0.0f);

		gl.glDisable(GL10.GL_CULL_FACE);

		// if (o.rot > 0.0f)
		// gl.glRotatef(o.rot, 0, 1, 0);

		// Log.v ("DroidkobanChooser", "offset: " + offset);
		// Log.v ("DroidkobanChooser", "offsetTarget: " + offsetTarget);

		for (int i = 0; i < DroidkobanChooser.MAX_TEXTURE_CHOICES; ++i) {

			float z = 0 + (Math.abs(i * 1.5f - offset)) * 2.0f;
			if (z > 2.0f)
				z = 2.0f;

			gl.glPushMatrix();
			gl.glTranslatef(i * 1.5f, 0.0f, -z);

			if (i == offsetTarget)
				gl.glRotatef(rot, 0.0f, 1.0f, 0.0f);

			this.playerRenderer[i].draw(gl);
			gl.glPopMatrix();
		}

		gl.glPopMatrix();

		gl.glEnable(GL10.GL_BLEND);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);

	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {

		this.width = width;
		this.height = height;
		this.portrait = width < height;
		this.width2D = portrait ? 320 : 480;

		gl.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);

		float fov = (width < height) ? 45 : 30;
		near = 1.0f;
		far = 50;
		float aspect = (float) width / (float) height;

		top = (float) Math.tan(Math.toRadians(fov)) * near;
		bottom = -top;
		left = aspect * bottom;
		right = aspect * top;

		this.width = width;
		this.height = height;

		this.size = (width > height) ? width + 3 * 160.0f : height;

		this.backgroundImage = new FlatQuad(gl, size, size);

	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		long time = SystemClock.currentThreadTimeMillis();

		this.am = DroidkobanChooser.DROIDKOBAN.getAssets();

		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		String renderer = gl.glGetString(GL10.GL_RENDERER);

		if (renderer.toLowerCase().contains("pixelflinger"))
			GLMesh.activeVBO = false;

		// try {
		//
		// // this.background = Texture.load(gl, am.open("back.png"));
		// } catch (IOException e) {

		// }

		this.loadData(gl);

		Log.v("DroidkobanRenderer", "onSurfaceCreated FINISHED time: " + (SystemClock.currentThreadTimeMillis() - time));
	}

	private void loadData(GL10 gl) {

		this.am = DroidkobanChooser.DROIDKOBAN.getAssets();


			this.playerRenderer[0] = new ModelRenderer(gl, am, "models/droids/" + model + "/m.obj", "models/droids/" + model + "/0.png");
			this.playerRenderer[0].setScale(0.3f);

			GLMesh m = this.playerRenderer[0].getMesh();

			for (int i = 1; i < DroidkobanChooser.MAX_TEXTURE_CHOICES; ++i) {
				this.playerRenderer[i] = new ModelRenderer(gl, am, m, "models/droids/" + model + "/" + i + ".png");
				this.playerRenderer[i].setScale(0.3f);

			}


	}
}