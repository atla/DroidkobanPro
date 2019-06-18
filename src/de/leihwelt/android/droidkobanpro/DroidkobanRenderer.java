package de.leihwelt.android.droidkobanpro;

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
import de.leihwelt.android.droidkobanpro.SoundManager;
import de.leihwelt.android.droidkobanpro.hud.HUD;
import de.leihwelt.android.droidkobanpro.storage.StorageConnector;
import de.leihwelt.android.droidkobanpro.theme.Theme;
import de.leihwelt.android.droidkobanpro.theme.Theme.ThemeColor;
import de.leihwelt.android.gl.FrustumCuller;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.Font2D.Text;
import de.leihwelt.android.objects.RenderObject;
import de.leihwelt.android.droidkobanpro.R;
public class DroidkobanRenderer implements GLSurfaceView.Renderer {

	private static final int STONE = 0;
	private static final int BOX = 1;
	private static final int GROUND = 2;
	private static final int TARGET = 5;

	public static final int MODE_PLAY = 0;
	public static final int MODE_MOVE = 1;
	public static final int MODE_ROTATE = 2;
	public static final int MODE_ZOOM = 3;

	public int touchMode = 0;

	public static final int FG_TEXTURE_NONE = 0;
	public static final int FG_TEXTURE_LEVEL_FINISHED = 1;

	private Context mContext;
	private DroidkobanGame game = DroidkobanGame.INSTANCE;

	private LinkedList<RenderObject> objects = new LinkedList<RenderObject>();

	private long lastFrameStop = SystemClock.currentThreadTimeMillis();
	private float frameTime = 0.5f;

	private FrustumCuller frustumCuller = new FrustumCuller();

	public HUD hud = null;

	private Theme theme = null;

	private Renderer[] renderObjects = new Renderer[6];

	private Quad playerShadow = null;
	private Quad waypoint = null;
	private ModelRenderer playerRenderer = null;
	private ModelRenderer playerRendererMove = null;
	private RenderObject playerObject = null;
	private Renderer pickRenderer = null;
	private AppSettings apps = AppSettings.INSTANCE;

	private boolean dataLoaded = false;

	private FlatQuad backgroundImage = null;
	private FlatQuad foregroundImage = null;
	public int showFgTexture = 0;

	public float mAngleX;
	public float mAngleY;

	public float userRotateAngle = 0.0f;

	public float mX = 0.0f;
	public float mZ = 0.0f;

	public float mCameraX = 0.0f;
	public float mCameraZ = 0.0f;

	public float mTapX = 0.0f;
	public float mTapY = 0.0f;

	private Texture uiUndoTexture;
	private Texture uiUndoTextureHighlight;
	private Texture uiZoomTexture;
	private Texture uiZoomTextureHighlight;
	private Texture uiResetTexture;
	private Texture uiResetTextureHighlight;
	private Texture uiSoundTexture;
	private Texture uiSoundTextureHighlight;
	private Texture uiPathTexture;
	private Texture uiPathTextureHighlight;
	private Texture ui2DMode;
	private Texture ui2DModeHighlight;
	private Texture fgLevelFinished;
	private Texture background;

	private int width;
	private int height;

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public float zoom = 2.5f;
	private float angle;

	private int fpsCount = 0;
	private long fpsReset = SystemClock.currentThreadTimeMillis();

	private GL10 glp = null;
	public boolean useCulling = true;

	private Texture loadingTexture;
	private FlatQuad quad;

	private Text text;
	private boolean portrait = true;
	private int width2D;
	private int height2D;
	private float left;
	private float bottom;
	private float top;
	private float near;
	private float far;
	private float right;
	private AssetManager am;
	boolean reload = false;

	public boolean enable2Dmode = false;

	private int nextPickId;

	private int lastPickedObject = -1;
	public boolean pick = false;
	public int pickX = -1;
	public int pickY = -1;
	public boolean movePath = false;
	public boolean moveByPath = false;
	public boolean creatingSurface = false;

	// private BloomShader shader;

	public DroidkobanRenderer(Context context) {
		this.mContext = context;

		// get game instance and level
		this.game = DroidkobanGame.INSTANCE;
		this.game.renderer = this;

	}

	public boolean isDataLoaded() {
		return this.dataLoaded;
	}

	public void set3DMode(GL10 gl) {
		// float ratio = (width < height) ? (float) width / height :
		// (float)height/width;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		// gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);

		if (enable2Dmode) {
			float factor = 3.0f + zoom;
			gl.glOrthof(left * factor, right * factor, bottom * factor, top * factor, near, far);
		} else
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
	public void set2DMode(GL10 gl) {

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

	public void setupObjects() {

		if (dataLoaded) {

			setToCurrentTheme();

			nextPickId = 1;

			Random r = new Random(222);

			// LevelData lvlData = this.game.getLevelData();
			byte[] lvl = this.game.level;

			int i = 0;
			int max = lvl.length;
			int fieldtype = -1, tex = 0;
			float x = 0.0f;
			float y = 0.0f;
			int width = this.game.levelWidth;
			int height = this.game.levelHeight;

			RenderObject ob = null;

			for (; i < max; ++i) {

				byte v = lvl[i];

				if (v == 1 && Maps.hasFieldAround(lvl, i, width, height)) {
					fieldtype = tex = STONE;
				} else if (v == 2 || v == 4) {
					fieldtype = tex = BOX;
				} else if (v == 0) {
					fieldtype = tex = GROUND;
				} else if (v == 3 || v == 4) {
					fieldtype = TARGET;
					tex = TARGET;
				}

				if (fieldtype >= 0) {
					x = (i % width) * 1.0f;
					y = (i / width) * 1.0f;
					ob = new RenderObject();
					ob.x = x;
					ob.z = y;
					ob.objectRendererIndex = fieldtype;
					ob.fieldId = i;

					if (fieldtype == GROUND || fieldtype == TARGET) {
						ob.pickId = nextPickId++;
					}

					this.objects.add(ob);

					// create a second object for floor beneath the box
					if (fieldtype == BOX) {

						if (theme.isBoxRotationEnabled())
							ob.rot = r.nextInt(18) * 20.0f;

						ob = new RenderObject();
						ob.x = x;
						ob.z = y;
						ob.objectRendererIndex = ((v == 2) ? GROUND : TARGET);
						// ob.texture = (v == 4 ? targetTexture :
						// groundTexture);
						ob.fieldId = i;
						ob.pickId = nextPickId++;
						this.objects.add(ob);
					}

					fieldtype = -1;
				}
			}
		}

		Comparator<RenderObject> comp = (new Comparator<RenderObject>() {

			@Override
			public int compare(RenderObject object1, RenderObject object2) {
				return object1.objectRendererIndex - object2.objectRendererIndex;
			}

		});

		// TODO: force close?
		Collections.sort(this.objects, comp);

		playerObject = DroidkobanGame.INSTANCE.playerObject;
		// playerObject.texture = droidTexture;
		playerObject.objectRendererIndex = 3;

		this.objects.add(playerObject);

	}

	private void printData(byte[] lvl, int width2, int height2) {

		StringBuilder b = new StringBuilder();

		for (int y = 0; y < width2; ++y) {
			for (int x = 0; x < height2; ++x) {
				b.append(Byte.toString(lvl[y * width + x]));
			}
			b.append("\n");
		}

		Log.v("PRINT DATA", "" + b.toString());
	}

	private void setToCurrentTheme() {

		if (DroidkobanGame.INSTANCE != null) {

			
			
			this.theme = DroidkobanGame.INSTANCE.getCurrentTheme(this.glp, this.am);
			this.theme.onInit(glp);
			
			
			this.renderObjects[0] = theme.getStoneRenderer();
			this.renderObjects[1] = theme.getBoxRenderer();
			this.renderObjects[2] = theme.getGroundRenderer();
			this.renderObjects[5] = theme.getTargetRenderer();
			
		}

	}

	public RenderObject findObject(int fieldId) {
		for (RenderObject o : this.objects) {
			if (o.fieldId == fieldId && o.objectRendererIndex != GROUND)
				return o;
		}

		return null;
	}

	public void clearObjects() {
		this.objects.clear();
		System.gc();
	}

	public void onDrawFrame(final GL10 gl) {
	
		if (movePath) {
			calculatePath();
		}

		final DroidkobanGame _game = this.game;

		if (this.moveByPath && pick)
			renderForPicking(gl);

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		set3DMode(gl);

		// set up modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		gl.glEnable(GL10.GL_TEXTURE_2D);

		renderBackground(gl);

		setupCamera(gl);

		this.frustumCuller.update(gl);

		gl.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);

		renderLevel(gl);

		renderUI(gl);

		long frameStop = SystemClock.currentThreadTimeMillis();

		frameTime = (frameStop - lastFrameStop) * 0.001f;
		lastFrameStop = frameStop;

		_game.compute(frameTime);

		for (final RenderObject o : this.objects) {
			if (o.action > 0)
				processAction(o);
		}

		moveCamera();

		calcFPS(frameStop);

	}

	private void calculatePath() {

		game.startMoving();

		/*
		 * for (RenderObject o : this.objects) { o.picked = false; }
		 */
		movePath = false;
	}

	private void moveCamera() {

		final float factor = (frameTime / 0.014f);
		final float factori = 1.0f / factor;

		float xf = Math.abs(mCameraX - playerObject.x);
		float zf = Math.abs(mCameraZ - playerObject.z);
		float xinc = (mCameraX < playerObject.x ? 1.0f : -1.0f) * (1.0f + xf * 3.0f) * frameTime * factori;
		float zinc = (mCameraZ < playerObject.z ? 1.0f : -1.0f) * (1.0f + zf * 3.0f) * frameTime * factori;

		if (xf > 0.05f)
			mCameraX += xinc;
		if (zf > 0.05f)
			mCameraZ += zinc;
	}

	private void renderLoadingScreen(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		// set up modelview matrix
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		this.quad.draw(gl);

		this.set2DMode(gl, 320, 480);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glLoadIdentity();
		this.loadingTexture.bind();

		gl.glTranslatef((320 - 128) / 2, (480 - 128) / 2, 0);
		this.quad.draw(gl);

		gl.glLoadIdentity();

		gl.glTranslatef(20.0f, 20.0f, 0.0f);

		gl.glPushMatrix();
		gl.glScalef(1.0f, -1.0f, 1.0f);
		this.text.render();
		gl.glPopMatrix();

		this.end2DMode(gl);
	}

	private void calcFPS(long frameStop) {

		this.fpsCount++;

		if (frameStop - this.fpsReset > 1000) {
			this.fpsReset = frameStop;
			this.hud.setFps(fpsCount);
			this.fpsCount = 0;
		}
	}

	private void processAction(final RenderObject o) {

		final float movementSpeed = 6.5f;

		switch (o.action) {

		case RenderObject.MOVE_TO_X:
			if (Math.abs(o.moveTo - o.x) > 0.2) {
				o.x += (o.x < o.moveTo ? 1.0f : -1.0f) * movementSpeed * frameTime;
			} else {
				o.x = o.moveTo;
				o.action = RenderObject.NO_ACTION;
				o.actionRenderIndex = -1;
			}
			break;

		case RenderObject.MOVE_TO_Y:
			if (Math.abs(o.moveTo - o.y) > 0.2) {
				o.y += (o.y < o.moveTo ? 1.0f : -1.0f) * movementSpeed * frameTime;
			} else {
				o.y = o.moveTo;
				o.action = RenderObject.NO_ACTION;
				o.actionRenderIndex = -1;
			}
			break;

		case RenderObject.MOVE_TO_Z:
			if (Math.abs(o.moveTo - o.z) > 0.2) {
				o.z += (o.z < o.moveTo ? 1.0f : -1.0f) * movementSpeed * frameTime;
			} else {
				o.z = o.moveTo;
				o.action = RenderObject.NO_ACTION;
				o.actionRenderIndex = -1;
			}
			break;

		case RenderObject.FLY_UP:
			if (Math.abs(o.moveTo - o.y) > 0.2) {
				o.y += 0.1f + movementSpeed * (o.y * 0.5f) * frameTime;
			} else {
				o.y = o.moveTo;
				o.action = RenderObject.NO_ACTION;
				o.actionRenderIndex = -1;
			}
			break;
		}
	}

	private void setupCamera(GL10 gl) {

		if (enable2Dmode)
			GLU.gluLookAt(gl, mCameraX, playerObject.y + 2.0f + zoom, mCameraZ - 0.01f, mCameraX, playerObject.y, mCameraZ, 0, 1, 0);
		else
			GLU.gluLookAt(gl, mCameraX, playerObject.y + 2.0f + zoom, mCameraZ - 2, mCameraX, playerObject.y, mCameraZ, 0, 1, 0);
	}

	private void renderLevel(final GL10 gl) {

		int currentObject = -1;
		Renderer renderObject = null;

		gl.glEnable(GL10.GL_TEXTURE_2D);

		if (apps.isEnableLighting()) {
			gl.glEnable(GL10.GL_LIGHTING);
			gl.glEnable(GL10.GL_LIGHT0);
			float[] direction = { 0.0f, 6.0f, -3.0f, 0 };
			gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, direction, 0);
		}

		gl.glEnable(GL10.GL_COLOR_MATERIAL);
		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for (final RenderObject o : this.objects) {

			if (this.frustumCuller.isSphereInFrustum(o.x, o.y, o.z, 0.6f)) {

				if (o.objectRendererIndex != currentObject) {
					currentObject = o.objectRendererIndex;
					renderObject = this.renderObjects[currentObject];
				}

				gl.glPushMatrix();

				if (renderObject == this.playerRenderer) {
					gl.glPushMatrix();
					gl.glEnable(GL10.GL_BLEND);

					gl.glTranslatef(o.x, 0.01f, o.z);

					this.playerShadow.draw(gl);

					gl.glDisable(GL10.GL_BLEND);
					gl.glPopMatrix();
				}

				gl.glTranslatef(o.x, o.y, o.z);

				if (o.rot > 0.0f)
					gl.glRotatef(o.rot, 0, 1, 0);

				if (o.actionRenderIndex >= 0) {
					this.renderObjects[o.actionRenderIndex].draw(gl);
				} else {
					renderObject.draw(gl);
				}

				gl.glPopMatrix();
			}
		}

		gl.glEnable(GL10.GL_BLEND);

		for (final RenderObject o : this.objects) {
			if (o.picked) {

				gl.glPushMatrix();

				gl.glTranslatef(o.x, 0.2f, o.z);

				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
				this.waypoint.draw(gl);

				gl.glTranslatef(0.0f, 0.2f, 0.0f);

				gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE);
				this.waypoint.draw(gl);

				gl.glPopMatrix();

			}
		}

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);
		gl.glDisable(GL10.GL_COLOR_MATERIAL);

		if (apps.isEnableLighting()) {
			gl.glDisable(GL10.GL_LIGHTING);
			gl.glDisable(GL10.GL_LIGHT0);
		}
	}

	private void renderForPicking(GL10 gl) {

		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

		set3DMode(gl);

		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		setupCamera(gl);

		gl.glRotatef(this.angle, 0.0f, 1.0f, 0.0f);

		int currentObject = -1;
		Renderer renderObject = null;

		gl.glDisable(GL10.GL_TEXTURE_2D);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		for (RenderObject o : this.objects) {

			if (o.pickId > 0) {

				if (o.objectRendererIndex != currentObject) {
					currentObject = o.objectRendererIndex;
					renderObject = this.renderObjects[currentObject];
				}

				gl.glPushMatrix();

				gl.glTranslatef(o.x, o.y, o.z);

				if (o.rot > 0.0f)
					gl.glRotatef(o.rot, 0, 1, 0);

				// if (o.actionRenderIndex >= 0) {
				// this.renderObjects[o.actionRenderIndex].draw(gl);
				// } else {

				o.setCodedColor(gl);

				// renderObject.draw(gl);
				this.pickRenderer.draw(gl);
				// }

				gl.glPopMatrix();
			}
		}

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);

		byte[] c = new byte[4];
		ByteBuffer pixels = ByteBuffer.wrap(c);

		gl.glReadPixels(pickX, height - pickY, 1, 1, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, pixels);

		int r = c[0];
		int g = c[1];
		int b = c[2];
		// int a = c[3];

		if (r < 0)
			r += 256;
		if (g < 0)
			g += 256;
		if (b < 0)
			b += 256;

		int rest = r % 20;

		if (rest < 10)
			r -= rest;
		else
			r += (20 - rest);

		if (r == 260)
			r = 0;

		rest = g % 20;

		if (rest < 10)
			g -= rest;
		else
			g += (20 - rest);

		if (g == 260)
			g = 0;

		rest = b % 20;

		if (rest < 10)
			b -= rest;
		else
			b += (20 - rest);

		if (b > 240)
			b = 240;

		int r2 = r / 20;
		int g2 = g / 20;
		int b2 = b / 20;

		int s = b2 * (12 * 12) + g2 * 12 + r2;

		this.lastPickedObject = s;

		// invoke listener
		this.select(this.lastPickedObject);

	}

	private void select(int i) {

		if (i != game.playerObject.fieldId) {

			int fieldId = -1, x = -1, y = -1;

			for (RenderObject o : this.objects) {

				if (o.pickId == i) {
					o.picked = true;
					fieldId = o.fieldId;
					x = (int) o.x;
					y = (int) o.z;
				}

			}

			if (fieldId > 0)
				game.addWaypoint(x, y, fieldId);

		}
	}

	public void renderUI(GL10 gl) {

		this.set2DMode(gl, this.width2D, this.height2D);

		hud.render(gl, frameTime);

		renderForeground(gl);

		this.end2DMode(gl);
	}

	private void renderForeground(GL10 gl) {
		if (this.showFgTexture > 0) {
			gl.glLoadIdentity();

			this.fgLevelFinished.bind();

			int offy = this.hud.isAchievementVisible() ? 107 : (height2D - 144) / 2;

			gl.glTranslatef(-2 + (int) (width2D - 320) / 2, offy, 0);

			this.foregroundImage.draw(gl);
		}
	}

	private void renderBackground(GL10 gl) {

		if (theme.providesCustomBackground()) {
			theme.renderBackground(gl, this);
		} else {

			gl.glPushMatrix();

			this.set2DMode(gl, this.width2D, this.height2D);

			gl.glLoadIdentity();
			this.background.bind();

			ThemeColor color = theme.getThemeColor();

			gl.glColor4f(color.red, color.green, color.blue, 1.0f);

			if (this.backgroundImage != null)
				this.backgroundImage.draw(gl);

			// gl.glLoadIdentity();
			// gl.glBindTexture(GL10.GL_TEXTURE_2D, this.background);
			//
			// // gl.glTranslatef(-2 + (int) (width2D - 320) / 2, 107, 0);
			//
			// this.backgroundImage.draw(gl);

			this.end2DMode(gl);

			gl.glPopMatrix();
		}
	}

	public int[] getConfigSpec() {
		// We want a depth buffer, don't care about the
		// details of the color buffer.
		int[] configSpec = { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
		return configSpec;
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {

		Log.v("DroidkobanRenderer", "onSurfaceChanged: " + gl);

		this.glp = gl;

		this.width = width;
		this.height = height;
		this.portrait = width < height;
		this.width2D = portrait ? 320 : 480;
		this.height2D = (int) (this.width2D * (height * 1.0f / width * 1.0f));

		// this.screenRatioAdjust = (portrait ? ((1.5f / (width/height)));

		if (apps.isEnableBackgrounds())
			this.backgroundImage = new FlatQuad(gl, this.width2D, this.height2D);

		this.hud.setScreenSize(this.width, this.height, this.width2D, this.height2D);
		// this.hud.setSize(width, height);

		gl.glViewport(0, 0, width, height);

		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);

		float fov = (width < height) ? 45 : 30;
		near = 1.0f;
		far = 50;
		float aspect = (float) width / (float) height;

		// TODO: store values;

		top = (float) Math.tan(Math.toRadians(fov)) * near;
		bottom = -top;
		left = aspect * bottom;
		right = aspect * top;

	}

	private void resetAllMeshesAndTexturesTo(GL10 gl) {

		for (Renderer r : this.renderObjects) {
			r.reset(gl, am);
		}

	}

	public void onBackgroundsEnabled() {
		this.backgroundImage = new FlatQuad(this.glp, this.width2D, this.height2D);
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {

		this.glp = gl;

		creatingSurface = true;

		gl.glDisable(GL10.GL_DITHER);
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

		gl.glClearColor(0.0f, 0.0f, 0.0f, 1);

		gl.glEnable(GL10.GL_CULL_FACE);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glEnable(GL10.GL_DEPTH_TEST);

		String renderer = gl.glGetString(GL10.GL_RENDERER);

		if (renderer.toLowerCase().contains("pixelflinger"))
			GLMesh.activeVBO = false;

		this.loadData(gl);

		setupLight(gl);

		// this.shader = new BloomShader ();
		// this.shader.onSurfaceCreated(gl, config);
		//		

		creatingSurface = false;

		DroidkobanGame.INSTANCE.surfaceCreatedFinished();

		Log.v("RENDERER", "SURFACE CREATED DONE");

	}

	private void setupLight(GL10 gl) {

		float[] lightColor = { 1.0f, 1.0f, 1.0f, 1 };
		float[] ambientLightColor = { 0.5f, 0.5f, 0.5f, 1.0f };

		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, ambientLightColor, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightColor, 0);
		gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightColor, 0);
	}

	private void loadData(GL10 gl) {

		if (!dataLoaded) {
			Log.v("RENDERER", "DATA NOT YET LOADED");

			this.am = game.activity.getAssets();

			this.foregroundImage = new FlatQuad(gl, 322.0f);

			String modelNormal = PlayerModelSelector.INSTANCE.getPlayerModelNormal();
			String modelMove = PlayerModelSelector.INSTANCE.getPlayerModelMove();
			String tex = PlayerModelSelector.INSTANCE.getPlayerTex();

			this.playerRenderer = new ModelRenderer(gl, am, modelNormal, tex);
			this.playerRenderer.setScale(0.3f);

			this.playerRendererMove = new ModelRenderer(gl, am, modelMove, tex);
			this.playerRendererMove.setScale(0.3f);

			this.renderObjects[3] = this.playerRenderer;
			this.renderObjects[4] = this.playerRendererMove;

			this.playerShadow = new Quad(gl, 0.8f, am, "textures/shadow.png");
			this.waypoint = new Quad(gl, 1.0f, am, "textures/sel.png");

			this.pickRenderer = new Quad(gl, 1.0f, am, "textures/beach/floor.png");

			loadTextures(gl);
			this.hud = new HUD();
			this.hud.init(gl, am);

			dataLoaded = true;

			setupHUD();

			this.clearObjects();
			this.setupObjects();

		} else {
			// recreate data
			Log.v("RENDERER", "RECREATING");

			this.am = game.activity.getAssets();

			if (this.backgroundImage != null)
				this.backgroundImage.reset(gl, am);

			this.foregroundImage.reset(gl, am);
			this.playerRenderer.reset(gl, am);
			this.playerRendererMove.reset(gl, am);

			this.playerShadow.reset(gl, am);
			this.waypoint.reset(gl, am);

			this.pickRenderer.reset(gl, am);

			try {
				fgLevelFinished.recreate(gl);
				background.recreate(gl);

				uiZoomTexture.recreate(gl);
				uiZoomTextureHighlight.recreate(gl);

				uiUndoTexture.recreate(gl);
				uiUndoTextureHighlight.recreate(gl);

				uiResetTexture.recreate(gl);
				uiResetTextureHighlight.recreate(gl);

				uiSoundTexture.recreate(gl);
				uiSoundTextureHighlight.recreate(gl);

				uiPathTexture.recreate(gl);
				uiPathTextureHighlight.recreate(gl);

				ui2DMode.recreate(gl);
				ui2DModeHighlight.recreate(gl);

			} catch (IOException e) {
			}

			this.hud.reset(gl, am);

			this.theme.reload(gl, am);
			this.setToCurrentTheme();

		}
	}

	private int getPlayerTexture() {

		SharedPreferences prefs = StorageConnector.INSTANCE.getPreferences();

		if (!prefs.contains("PLAYER_TEXTURE") || !prefs.contains("PLAYER_MODEL")) {

			SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putInt("PLAYER_MODEL", 0);
			edit.putInt("PLAYER_TEXTURE", 0);
			edit.commit();
		}

		return prefs.getInt("PLAYER_TEXTURE", 0);
	}

	private int getPlayerModel() {

		SharedPreferences prefs = StorageConnector.INSTANCE.getPreferences();

		if (!prefs.contains("PLAYER_TEXTURE") || !prefs.contains("PLAYER_MODEL")) {

			SharedPreferences.Editor edit = StorageConnector.INSTANCE.getEditor();
			edit.putInt("PLAYER_MODEL", 0);
			edit.putInt("PLAYER_TEXTURE", 0);
			edit.commit();
		}

		return prefs.getInt("PLAYER_MODEL", 0);
	}

	private void setupHUD() {

		final DroidkobanRenderer renderer = this;

		this.hud.setVibrator(DroidkobanGame.INSTANCE.vibrator);

		this.hud.addElement(uiUndoTexture, uiUndoTextureHighlight, false, false, new HUD.Action() {

			@Override
			public void toggleOn() {

			}

			@Override
			public void toggleOff() {
				// TODO Auto-generated method stub

			}

			@Override
			public void invoke() {
				DroidkobanGame.INSTANCE.undo();

				// renderer.hud.addAchievement("Gone in 60 seconds",
				// "Beat a level in under 60 seconds!");

			}
		});

		// this.hud.addElement(normalTexture, highlightTexture, toggable,
		// keepActiveOnToggle, action)
		this.hud.addElement(ui2DMode, ui2DModeHighlight, true, false, new HUD.Action() {

			@Override
			public void toggleOn() {

				enable2Dmode = true;

			}

			@Override
			public void toggleOff() {
				enable2Dmode = false;

			}

			@Override
			public void invoke() {
				// TODO Auto-generated method stub

			}
		});

		// this.hud.addElement(normalTexture, highlightTexture, toggable,
		// keepActiveOnToggle, action)
		this.hud.addElement(uiPathTexture, uiPathTextureHighlight, true, false, new HUD.Action() {

			@Override
			public void toggleOn() {

				StorageConnector.INSTANCE.getPreferences().edit().putBoolean("dkMoveByPath", true).commit();
				moveByPath = true;
				pick = false;
			}

			@Override
			public void toggleOff() {
				StorageConnector.INSTANCE.getPreferences().edit().putBoolean("dkMoveByPath", false).commit();
				moveByPath = false;
				pick = false;
			}

			@Override
			public void invoke() {
				// TODO Auto-generated method stub

			}
		});

		this.hud.getLastElement().active = StorageConnector.INSTANCE.getPreferences().getBoolean("dkMoveByPath", false);

		this.hud.addElement(uiZoomTexture, uiZoomTextureHighlight, true, true, new HUD.Action() {

			@Override
			public void toggleOn() {
				renderer.touchMode = DroidkobanRenderer.MODE_ZOOM;
			}

			@Override
			public void toggleOff() {
				renderer.touchMode = DroidkobanRenderer.MODE_PLAY;

			}

			@Override
			public void invoke() {
			}
		});

		// this.hud.addSpacingRight();

		this.hud.addElement(uiResetTexture, uiResetTextureHighlight, false, false, new HUD.Action() {

			@Override
			public void toggleOn() {
			}

			@Override
			public void toggleOff() {

			}

			@Override
			public void invoke() {

				AlertDialog.Builder builder = new AlertDialog.Builder(Droidkoban.INSTANCE);
				builder.setMessage("Are you sure you want to reset this level?").setCancelable(false).setPositiveButton("Yes",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								game.actions.add(DroidkobanGame.RESET_GAME);
							}
						}).setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

				AlertDialog alert = builder.create();

				alert.show();

			}
		}, true);

		this.hud.addElement(uiSoundTexture, uiSoundTextureHighlight, true, false, new HUD.Action() {

			@Override
			public void toggleOn() {
				SoundManager.INSTANCE.toggleMusic(renderer.mContext);
			}

			@Override
			public void toggleOff() {
				SoundManager.INSTANCE.toggleMusic(renderer.mContext);
			}

			@Override
			public void invoke() {
			}
		}, true);

		this.hud.getLastElement().active = !SoundManager.INSTANCE.isEnabled();

	}

	private void loadTextures(GL10 gl) {
		try {
			fgLevelFinished = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.lvlfin);

			background = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.bg_beach);

			uiZoomTexture = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.zoom);
			uiZoomTextureHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.zoom_h);

			uiUndoTexture = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.undo);
			uiUndoTextureHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.undo_h);

			uiResetTexture = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.reset);
			uiResetTextureHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.reset_h);

			uiSoundTexture = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.sound);
			uiSoundTextureHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.sound_h);

			uiPathTexture = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.path);
			uiPathTextureHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.path_h);

			ui2DMode = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.mode_2d);
			ui2DModeHighlight = new Texture(gl, Droidkoban.INSTANCE.getResources(), R.drawable.mode_2d_h);
		} catch (IOException e) {
		}

	}

	public static int loadTexture(GL10 gl, int texResource) {

		int tex = 0;
		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);

		tex = textures[0];
		gl.glBindTexture(GL10.GL_TEXTURE_2D, tex);

		InputStream is = Droidkoban.INSTANCE.getResources().openRawResource(texResource);
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(is);
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				Log.e("loadTexture", e.getMessage());
			}
		}

		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

		GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();

		return tex;
	}

	public void playerReachedField(int field) {

		for (RenderObject o : this.objects) {

			if (o.picked && o.fieldId == field) {
				o.picked = false;
			}
		}

	}

	public void removeAllWaypoints() {
		for (RenderObject o : this.objects) {
			o.picked = false;
		}
	}

	public void removeWaypoint(int fieldId) {

		for (RenderObject o : this.objects) {
			if (o.fieldId == fieldId)
				o.picked = false;
		}

	}
}