package de.leihwelt.android.droidkobanpro.hud;

import java.util.ArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import de.leihwelt.android.droidkobanpro.AppSettings;
import de.leihwelt.android.droidkobanpro.DroidkobanGame;
import de.leihwelt.android.droidkobanpro.FlatQuad;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.achievements.Achievement;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker;
import de.leihwelt.android.droidkobanpro.achievements.AchievementUnlocker.AchievementListener;
import de.leihwelt.android.droidkobanpro.stats.LevelStat;
import de.leihwelt.android.droidkobanpro.stats.StatTracker;
import de.leihwelt.android.gl.Font2D;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.ObjModelImporter;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.Font2D.FontStyle;
import de.leihwelt.android.gl.Font2D.Text;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;
import de.leihwelt.android.gl.Texture.TextureFilter;
import de.leihwelt.android.gl.Texture.TextureWrap;
import de.leihwelt.android.objects.RenderObject;

import android.os.*;

public class HUD implements AchievementListener {

	public static class AchievementBanner {
		public String title = "Generic";
		public String description = "achieved something";

		public long addTime = 0;
		public float offY = -120.0f;

		public float alpha = 1.0f;
		int points;
	}

	private Texture statsMoves = null;
	private Texture statsUndos = null;
	private Texture statsBoxes = null;
	private Texture statsTime = null;

	private Texture achievementBack = null;
	private Texture[] pointTextures = new Texture[3];

	private Text text = null;
	private Text achievementText = null;
	private Text achievementTitleText = null;

	private FlatQuad elementRenderer = null;
	private FlatQuad statRenderer = null;
	private FlatQuad achievement = null;
	private FlatQuad points = null;

	private GLMesh top = null;
	private GLMesh bottom = null;
	private GL10 gl = null;

	private int fps = 0;

	private AchievementBanner achieve = null;

	public interface Action {
		public void invoke();

		public void toggleOn();

		public void toggleOff();

	}

	public static final float HUD_SPACING = 15.0f;

	enum Position {
		LOWER_LEFT, LOWER_CENTER
	};

	enum Orientation {
		VERTICAL, HORIZONTAL
	};

	public static class Element {
		public Texture normalTexture;
		public Texture highlightTexture;
		public boolean toggable = true;
		public boolean keepActiveOnToggle = false;
		public boolean active = false;
		public Action action = null;
		public boolean alignRight = false;

	}

	public static Element SPACING_ELEMENT = new Element();

	private ArrayList<Element> elements = new ArrayList<Element>();

	private Position position = Position.LOWER_LEFT;
	private Orientation orientation = Orientation.HORIZONTAL;

	private float minElementSize = 36.0f;

	// TODO: to toggle again offy = -32.0f

	private float offy = 0.0f;
	private boolean active = true; // true
	private float width = 0.0f;
	private float height = 0.0f;

	private float screenWidth = 0.0f;
	private float screenHeight = 0.0f;

	private Vibrator vibrator = null;
	private boolean portrait = true;
	private boolean hidden = false;

	public HUD() {

		AchievementUnlocker.INSTANCE.addListener(this);
		
		this.hidden = AppSettings.INSTANCE.isHideUI();
	}

	public boolean isAchievementVisible() {
		return this.achieve != null;
	}

	public void init(GL10 gl, AssetManager am) {
		this.elementRenderer = new FlatQuad(gl, minElementSize);
		this.statRenderer = new FlatQuad(gl, 24.0f);
		this.achievement = new FlatQuad(gl, 320);
		this.points = new FlatQuad(gl, 32);

		this.top = new GLMesh(gl, 6, false, false, false);
		this.bottom = new GLMesh(gl, 6, false, false, false);

		buildFonts(gl);

		this.gl = gl;

		try {
			this.statsMoves = new Texture(gl, am, "textures/stats/moves.png");
			this.statsBoxes = new Texture(gl, am, "textures/stats/boxes.png");
			this.statsTime = new Texture(gl, am, "textures/stats/time.png");
			this.statsUndos = new Texture(gl, am, "textures/stats/undo.png");
			this.achievementBack = new Texture(gl, am, "textures/achievements/back.png");
			this.pointTextures[0] = new Texture(gl, am, "textures/achievements/five.png");
			this.pointTextures[1] = new Texture(gl, am, "textures/achievements/ten.png");
			this.pointTextures[2] = new Texture(gl, am, "textures/achievements/twenfive.png");

		} catch (IOException e) {
			Log.v("HUD", "Error loading stats textures");
		}
	}

	public void setScreenSize(float screenWidth, float screenHeight, float width, float height) {
		this.screenWidth = screenWidth;
		this.screenHeight = screenHeight;

		this.portrait = width < height;

		this.setSize(width, height);
		//		
		// // portrait
		// if (portrait){
		// this.setSize(320,480);
		// //this.setSize(width, width * (480.0f/320.0f));
		// }
		// // landscape
		// else{
		// this.setSize(480,320);
		// //this.setSize(width, width * (320.0f/480.0f));
		// }
	}

	private void buildFonts(GL10 gl) {

		float scale = 1.0f;// this.width / 320.0f;//(portrait ? 320.0f :
		// 480.0f);

		Font2D f = new Font2D(gl, "Sans", (int) (16 * scale), FontStyle.Plain);
		this.text = f.newText(gl);
		this.achievementText = f.newText(gl);

		f = new Font2D(gl, "Sans", (int) (20 * scale), FontStyle.Bold);
		this.achievementTitleText = f.newText(gl);
	}

	public void setVibrator(Vibrator vib) {
		this.vibrator = vib;
	}

	public void setFps(int fps) {
		this.fps = fps;
	}

	private void addAchievement(String title, String desc, int points) {
		this.achieve = new AchievementBanner();
		this.achieve.title = title;
		this.achieve.description = desc;
		this.achieve.addTime = SystemClock.elapsedRealtime();
		this.achieve.points = points;
	}

	public void addElement(final Texture uiUndoTexture, final Texture uiUndoTextureHighlight, boolean toggable, boolean keepActiveOnToggle, Action action) {
		this.addElement(uiUndoTexture, uiUndoTextureHighlight, toggable, keepActiveOnToggle, action, false);
	}

	public Element getLastElement() {
		return this.elements.get(this.elements.size() - 1);
	}

	public void addElement(final Texture normalTexture, final Texture highlightTexture, boolean toggable, boolean keepActiveOnToggle, Action action,
			boolean alignRight) {
		Element e = new Element();
		e.normalTexture = normalTexture;
		e.highlightTexture = highlightTexture;
		e.action = action;
		e.toggable = toggable;
		e.keepActiveOnToggle = keepActiveOnToggle;
		e.alignRight = alignRight;

		this.elements.add(e);
	}

	private float getStartingX() {

		switch (this.position) {
		case LOWER_LEFT:
			return HUD_SPACING / 2;
		case LOWER_CENTER:
			return this.width - 0.5f * elements.size() * minElementSize;
		}

		return 0.0f;
	}

	private float getStartingY() {

		switch (this.position) {
		case LOWER_LEFT:
		case LOWER_CENTER:
			return this.height - minElementSize;
		}

		return 0.0f;
	}

	private void setSize(float width, float height) {

		this.width = width;
		this.height = height;

		float w = width;// (width > height) ? height : width;
		float h = 30;

		if (top != null)
			top = new GLMesh(gl, 6, false, false, false);

		top.vertex(0.0f, h, 0.0f);
		top.vertex(w, h, 0.0f);
		top.vertex(w, 0.0f, 0.0f);
		top.vertex(w, 0.0f, 0.0f);
		top.vertex(0.0f, 0.0f, 0.0f);
		top.vertex(0.0f, h, 0.0f);

		h = minElementSize;

		if (bottom != null)
			bottom = new GLMesh(gl, 6, false, false, false);

		bottom.vertex(0.0f, height, 0.0f);
		bottom.vertex(w, height, 0.0f);
		bottom.vertex(w, height - h, 0.0f);
		bottom.vertex(w, height - h, 0.0f);
		bottom.vertex(0.0f, height - h, 0.0f);
		bottom.vertex(0.0f, height, 0.0f);

		this.achievement = new FlatQuad(gl, 320.0f);

		this.buildFonts(gl);
	}

	private float getIncreaseForX() {
		return minElementSize + 10.0f;
	}

	private float getIncreaseForY() {
		return 0.0f;
	}
	
	public void setHidden (boolean hidden){
		this.hidden = hidden;
	}

	public void render(GL10 gl, float frameTime) {

		long currentTime = SystemClock.elapsedRealtime();

		gl.glLoadIdentity();

		moveOffset(frameTime);

		if (!hidden) {

			float x = getStartingX();
			float y = getStartingY();

			renderBack(gl);

			renderStats(gl);

			float xInc = getIncreaseForX();
			float yInc = getIncreaseForY();

			gl.glLoadIdentity();
			gl.glPushMatrix();
			gl.glTranslatef(x, y - offy, 0.0f);

			for (Element e : this.elements) {
				if (!e.alignRight) {
					if (e != SPACING_ELEMENT) {
						if (e.active)
							e.highlightTexture.bind();
						else
							e.normalTexture.bind();

						this.elementRenderer.draw(gl);
					}
					gl.glTranslatef(xInc, yInc, 0.0f);
				}
			}

			gl.glPopMatrix();

			gl.glPushMatrix();
			gl.glTranslatef(this.width - minElementSize - getStartingX(), y - offy, 0.0f);

			for (Element e : this.elements) {
				if (e.alignRight) {
					if (e != SPACING_ELEMENT) {
						if (e.active)
							e.highlightTexture.bind();
						else
							e.normalTexture.bind();

						this.elementRenderer.draw(gl);
					}
					gl.glTranslatef(-xInc, yInc, 0.0f);
				}
			}

			gl.glPopMatrix();

			renderAchievements(gl, frameTime, currentTime);
		}
	}

	private int getPointIndex(int points) {
		if (points == 5)
			return 0;
		if (points == 10)
			return 1;

		return 2;
	}

	private AchievementBanner getTestAchievement() {
		AchievementBanner a = new AchievementBanner();
		a.title = "TestAchievement";
		a.description = "yeah got something special!";
		a.points = 25;
		a.offY = 0.0f;
		a.alpha = 1.0f;
		a.addTime = SystemClock.elapsedRealtime();

		return a;
	}

	private void renderAchievements(GL10 gl2, float frameTime, long currentTime) {

		final AchievementBanner a = this.achieve;

		if (a != null) {

			float offsetx = (this.width - 320.0f) * 0.5f;

			gl.glEnable(GL10.GL_BLEND);
			gl.glColor4f(1.0f, 1.0f, 1.0f, a.alpha);

			gl.glPushMatrix();
			gl.glLoadIdentity();

			gl.glTranslatef(offsetx, a.offY, 0.0f);

			this.achievementBack.bind();
			this.achievement.draw(gl2);

			gl.glPushMatrix();
			this.pointTextures[this.getPointIndex(a.points)].bind();
			gl.glTranslatef(45.0f, 53.0f, 0.0f);
			this.points.draw(gl2);
			gl.glPopMatrix();

			gl.glTranslatef(80.0f, 52.0f, 0.0f);
			gl.glScalef(1.0f, -1.0f, 1.0f);

			this.achievementTitleText.setText(a.title);
			this.achievementTitleText.render();

			gl.glTranslatef(-75.0f, -35.0f, 0.0f);

			this.achievementText.setText(a.description);
			this.achievementText.render();

			gl.glPopMatrix();

			if (a.offY < -15.0f) {
				a.offY += 500.0f * frameTime;
			} else {
				a.offY = 0.0f;

				if (currentTime - a.addTime > 5000) {
					a.alpha -= 0.65f * frameTime;
					if (a.alpha < 0.0f)
						this.achieve = null;
				}
			}

			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

			// gl.glScalef(1.0f, 1.0f, 1.0f);
		}

	}

	private void moveOffset(float frameTime) {

		/*
		 * final float movementSpeed = 150.0f;
		 * 
		 * if (this.active) { if (this.offy < -7.0f) this.offy += movementSpeed
		 * * frameTime; else this.offy = 0.0f; } else { if (this.offy > -32.0f)
		 * this.offy -= movementSpeed * frameTime; else this.offy = -32.0f; }
		 */
	}

	private void renderStats(GL10 gl) {

		LevelStat current = StatTracker.INSTANCE.getCurrent();
		String elapsed = StatTracker.INSTANCE.elapsedTimeString();
		final String tmp = "       ";

		renderStatItem(gl, this.statsMoves, 5.0f, 3.0f, Integer.toString(current.movesForCurrentLevel) + tmp);
		renderStatItem(gl, this.statsUndos, 70.0f, 3.0f, Integer.toString(current.undosForCurrentLevel) + tmp);
		renderStatItem(gl, this.statsBoxes, 130.0f, 3.0f, "" + current.boxesLeft + "-" + current.boxesInLevel + tmp);
		renderStatItem(gl, this.statsTime, 200.0f, 3.0f, elapsed + tmp);

		// renderFPS(gl, tmp);
	}

	private void renderFPS(GL10 gl, final String tmp) {
		gl.glPushMatrix();

		gl.glTranslatef(280, 7, 0.0f);
		text.setText("" + fps + tmp);
		gl.glScalef(1.0f, -1.0f, 1.0f);
		text.render();

		gl.glPopMatrix();
	}

	private void renderStatItem(GL10 gl, Texture tex, float x, float y, String d) {
		gl.glPushMatrix();

		gl.glTranslatef(x, y, 0.0f);

		tex.bind();
		this.statRenderer.draw(gl);

		gl.glTranslatef(28.0f, 4.0f, 0.0f);
		text.setText(d);

		gl.glScalef(1.0f, -1.0f, 1.0f);
		text.render();

		gl.glPopMatrix();
	}

	private void renderBack(GL10 gl) {

		gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);

		gl.glColor4f(0.0f, 0.0f, 0.0f, 0.5f);

		this.top.render(GLPrimitiveType.Triangles);

		gl.glPushMatrix();
		gl.glTranslatef(0.0f, -offy, 0.0f);

		this.bottom.render(GLPrimitiveType.Triangles);
		gl.glPopMatrix();

		gl.glEnable(GL10.GL_TEXTURE_2D);

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
	}

	public boolean onTouchEvent(MotionEvent e) {

		int x = (int) e.getX();
		int y = (int) e.getY();

		float sx = this.width / this.screenWidth;
		float sy = this.height / this.screenHeight;

		x *= sx;
		y *= sy;

		boolean wasActive = active;

		if (touchEventIsInside(x, y)) {

			Element element = findHitElement(x);

			if (element != null) {

				// Element element = this.elements.get(tilex);

				if (element != HUD.SPACING_ELEMENT) {

					switch (e.getAction()) {

					case MotionEvent.ACTION_MOVE:

						break;

					case MotionEvent.ACTION_DOWN:

						if (!element.toggable)
							element.active = true;

						break;

					case MotionEvent.ACTION_UP:

						if (!element.toggable) {
							element.active = false;

							if (element.action != null)
								element.action.invoke();
						} else {
							if (element.action != null) {
								if (element.active)
									element.action.toggleOff();
								else
									element.action.toggleOn();
							}

							element.active = !element.active;
						}

						if (this.vibrator != null)
							this.vibrator.vibrate(10);

						break;
					}

				}

				return true;
			}

		} else {
			// move down
		}
		return false;// (!wasActive && active);
	}

	public boolean isActive() {
		return active;
	}

	private Element findHitElement(int x) {

		int elementFromLeft = (int) (x / (minElementSize + HUD_SPACING));
		int elementFromRight = (int) ((width - x) / (minElementSize + HUD_SPACING));

		// search for element from left
		int i = 0;
		for (Element target : this.elements) {
			if (!target.alignRight) {
				if (elementFromLeft == i)
					return target;
				else
					++i;
			}
		}

		i = 0;
		for (Element target : this.elements) {
			if (target.alignRight) {
				if (elementFromRight == i)
					return target;
				else
					++i;
			}
		}

		return null;
	}

	private boolean touchEventIsInside(float x, float y) {

		if (y > this.getStartingY() - HUD_SPACING) {

			return true;

			// if (!this.active) {
			// this.active = true;
			// return false;
			// } else if (this.active && offy > -1.0f) {
			// return true;
			// }
		} else if (noElementIsActive()) {
			// this.active = false;
			// TODO: redo
		}

		return false;
	}

	private boolean noElementIsActive() {

		for (Element e : this.elements)
			if (e.active && e.keepActiveOnToggle)
				return false;

		return true;
	}

	public void addSpacing() {
		this.elements.add(HUD.SPACING_ELEMENT);

	}

	@Override
	public void gained(Achievement achievement) {
		this.addAchievement(achievement.title, achievement.description, achievement.points);

	}

	public void reset(GL10 gl, AssetManager am) {

		hidden = AppSettings.INSTANCE.isHideUI();

		this.gl = gl;

		//		
		this.elementRenderer.reset(gl, am);
		this.statRenderer.reset(gl, am);
		this.achievement.reset(gl, am);
		this.points.reset(gl, am);
		//
		// this.top = new GLMesh(gl, 6, false, false, false);
		// this.bottom = new GLMesh(gl, 6, false, false, false);

		this.setSize(width, height);

		this.achievement.reset(gl, am);
		try {

			this.statsMoves.recreate(gl);
			this.statsBoxes.recreate(gl);
			this.statsTime.recreate(gl);
			this.statsUndos.recreate(gl);
			this.achievementBack.recreate(gl);
			this.pointTextures[0].recreate(gl);
			this.pointTextures[1].recreate(gl);
			this.pointTextures[2].recreate(gl);
		} catch (IOException e) {

		}
		// try {
		//
		// for (Element e : this.elements) {
		// e.highlightTexture.recreate(gl);
		//
		// e.normalTexture.recreate(gl);
		// }
		// } catch (IOException e1) {
		//
		// }

	}

}
