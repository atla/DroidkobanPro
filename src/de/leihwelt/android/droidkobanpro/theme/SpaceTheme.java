package de.leihwelt.android.droidkobanpro.theme;

import java.io.IOException;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.res.AssetManager;
import android.opengl.GLU;
import android.os.SystemClock;
import android.util.Log;

import de.leihwelt.android.droidkobanpro.Cube;
import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.FlatQuad;
import de.leihwelt.android.droidkobanpro.FlatQuadCentered;
import de.leihwelt.android.droidkobanpro.ModelRenderer;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.Renderer;
import de.leihwelt.android.droidkobanpro.theme.Theme.ThemeColor;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.objects.RenderObject;

public class SpaceTheme implements Theme {

	private class Meteor {
		public float x;
		public float y;
		public float z;
		public float pitch;
		public float yaw;
		public float roll;

		public float speedX = 0.0f;
		public float speedY = 0.0f;
		public float speedZ = 0.0f;
		public long nextDirectionShift;
	}

	private Meteor[] meteors = new Meteor[5];

	private Renderer boxRenderer;
	private Renderer groundRenderer;
	private Renderer stoneRenderer;
	private Renderer targetRenderer;
	private ThemeColor color = new ThemeColor();
	private Texture background;
	private FlatQuad backgroundImage;

	private Renderer meteor;

	private de.leihwelt.android.droidkobanpro.FlatQuadCentered quad;

	private int width;
	private int height;
	private float frameTime = 0.0f;
	private long lastFrameStop = 0;

	public Context mContext;

	private QuadItem[] movingQuads = new QuadItem[AMOUNT];

	private QuadItem item = null;

	private Random r;
	private long frameStop;

	public final static int AMOUNT = 20;

	private Texture stars1;
	private Texture stars2;
	private long time;
	private float xdir;
	private float ydir;
	private float zdir;

	public static class QuadItem {
		float positionX = 0.0f;
		float positionY = 0.0f;
		float positionZ = 0.0f;

		float rotate = 0.0f;
		float speedX = 0.0f;
		float speedY = 0.0f;
		float speedZ = 0.0f;
		public long nextDirectionShift;
	}

	public static class FadingQuadItem {
		float positionX = 0.0f;
		float positionY = 0.0f;
		float positionZ = 0.0f;

		float rotate = 0.0f;

		public long nextDirectionShift;

		float lifeTime = 100.0f;
		boolean visible = false;
	}

	public SpaceTheme(GL10 gl, AssetManager am) {


		color.red = 0.0f;
		color.blue = 0.0f;
		color.green = 0.0f;
		this.load(gl, am);

		this.setupQuads();

		this.setupMeteors();

	}

	@Override
	public Renderer getBoxRenderer() {
		return this.boxRenderer;
	}

	@Override
	public ThemeColor getThemeColor() {
		return this.color;
	}

	@Override
	public Renderer getGroundRenderer() {
		return this.groundRenderer;
	}

	@Override
	public Renderer getStoneRenderer() {
		return this.stoneRenderer;
	}

	@Override
	public Renderer getTargetRenderer() {
		return this.targetRenderer;
	}

	@Override
	public boolean providesCustomBackground() {

		return true;
	}

	@Override
	public void renderBackground(GL10 gl, DroidkobanRenderer renderer) {

		renderer.set2DMode(gl);

		gl.glLoadIdentity();
		this.background.bind();

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		this.backgroundImage.draw(gl);

		gl.glMatrixMode(GL10.GL_PROJECTION);
		// gl.glPushMatrix();
		gl.glLoadIdentity();

		gl.glOrthof(0, renderer.getWidth(), renderer.getHeight(), 0, -150, 150);
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();

		this.renderQuads(gl);

		// gl.glPopMatrix();

		renderer.end2DMode(gl);

		renderMeteors(gl, renderer);

		frameStop = SystemClock.currentThreadTimeMillis();
		frameTime = (frameStop - lastFrameStop) * 0.001f;
		lastFrameStop = frameStop;

	}

	private void renderMeteors(GL10 gl, DroidkobanRenderer renderer) {
		renderer.set3DMode(gl);

		gl.glDisable(GL10.GL_DEPTH_TEST);

		for (Meteor m : meteors) {

			gl.glPushMatrix();
			gl.glTranslatef(m.x, m.y, m.z);
			gl.glRotatef(m.pitch, 1.0f, 0.0f, 0.0f);

			this.meteor.draw(gl);

			gl.glPopMatrix();

			m.yaw += 5.0f * frameTime;
			m.pitch += 5.0f * frameTime;
			m.roll += 5.0f * frameTime;
			m.x += m.speedX * frameTime;
			m.y += m.speedY * frameTime;
//			m.z += m.speedZ * frameTime;

//			if (m.z < -16.0f){
//				m.z = -16.0f;
//
//			}			
//			else if (m.z > -2.0f){
//				m.z = -2.0f;
//
//			}
				

			if (m.nextDirectionShift < time || isOutside(m)) {

				xdir = m.speedX > 0.0f ? -1.0f : 1.0f;
				ydir = m.speedY > 0.0f ? -1.0f : 1.0f;
				//zdir = m.speedZ > 0.0f ? -1.0f : 1.00f;

				m.speedX = 0.2f + (r.nextInt() % 0.8f) * xdir;
				m.speedY = 0.1f + (r.nextInt() % 0.8f) * ydir;
	//			m.speedZ = r.nextFloat() * 0.1f * zdir;

				m.nextDirectionShift = (time + 6000 + r.nextInt() % 10000);
			}

		}

		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	private boolean isOutside(Meteor item) {
		return (item.x < -15.0f || item.x > 15.0f || item.y < -15.0f || item.y > 15.0f);
	}

	@Override
	public void onInit(GL10 gl) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 1);
	}

	@Override
	public boolean isBoxRotationEnabled() {
		return false;
	}

	@Override
	public void load(GL10 gl, AssetManager am) {

		try {

			this.backgroundImage = new FlatQuad(gl, 855.0f, 855.0f);

			
			this.quad = new FlatQuadCentered(gl, 105.0f, 105.0f);

			ModelRenderer mr = new ModelRenderer(gl, am, "models/space/box.obj", "models/space/gem.png");
			mr.setScale(0.5f);
			this.boxRenderer = mr;

			this.groundRenderer = new Renderer() {

				@Override
				public void draw(GL10 gl) {

				}

				@Override
				public int getTextureID() {
					return -1;
				}

				@Override
				public void reset(GL10 gl, AssetManager am) {

				}

			};

			mr = new ModelRenderer(gl, am, "models/space/meteor.obj", "models/space/rock.png");
			mr.setScale(1.5f);

			this.meteor = mr;

			mr = new ModelRenderer(gl, am, "models/space/block.obj", "models/space/blocktex.png");
			mr.setScale(0.5f);

			this.stoneRenderer = mr;

			mr = new ModelRenderer(gl, am, "models/space/target.obj", "models/space/target.png");
			mr.setScale(0.7f);

			this.targetRenderer = mr;

			
			this.background = new Texture (gl, am, "textures/space/back.png");

			this.stars1 = new Texture (gl, am, "textures/space/star.png");
			this.stars2 = new Texture (gl, am, "textures/space/star2.png");

		} catch (IOException e) {
			Log.e("SpaceTheme", "Could not load default theme " + e.getMessage());
		}

	}

	@Override
	public void reload(GL10 gl, AssetManager am) {
		this.load(gl, am);
		
		this.backgroundImage.reset(gl, am);
		this.quad.reset(gl, am);
		this.boxRenderer.reset(gl, am);
		this.meteor.reset(gl, am);
		this.stoneRenderer.reset(gl, am);
		this.targetRenderer.reset(gl, am);
		try {
			this.background.recreate(gl);
			this.stars1.recreate(gl);
			this.stars2.recreate(gl);
		} catch (IOException e) {
		}		
		
	}

	public void set3DMode(GL10 gl) {
		float ratio = (float) width / height;
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-ratio, ratio, -1, 1, 1, 50);
	}

	public void setupQuads() {

		r = new Random(23223);
		time = SystemClock.currentThreadTimeMillis();

		for (int i = 0; i < AMOUNT; ++i) {
			QuadItem item = new QuadItem();

			item.positionX = (r.nextInt() % 500) * 1.0f;
			item.positionY = (r.nextInt() % 500) * 1.0f;
			item.positionZ = (r.nextInt() % 80) * 0.1f;
			item.rotate = (r.nextInt() % 360) * 1.0f;

			item.speedX = (r.nextInt() % 20.0f) * 1.0f;
			item.speedY = (r.nextInt() % 20.0f) * 1.0f;
			item.speedZ = r.nextFloat() * 0.5f;
			item.nextDirectionShift = (time + r.nextInt() % 4000);

			this.movingQuads[i] = item;

		}
	}

	public void setupMeteors() {

		r = new Random(23223);
		time = SystemClock.currentThreadTimeMillis();

		for (int i = 0; i < 5; ++i) {
			Meteor m = new Meteor();

			m.x = -5.0f + (r.nextInt() % 10) * 1.0f;
			m.y = -5.0f + (r.nextInt() % 10) * 1.0f;
			m.z = -6.0f + (r.nextInt() % 5) * 1.0f;

			m.pitch = (r.nextInt() % 360) * 1.0f;
			m.yaw = (r.nextInt() % 360) * 1.0f;
			m.roll = (r.nextInt() % 360) * 1.0f;

			m.speedX = 0.2f + (r.nextInt() % 0.8f) * 1.0f;
			m.speedY = 0.1f + (r.nextInt() % 0.8f) * 1.0f;
			m.speedZ = r.nextFloat() * 0.1f;
			m.nextDirectionShift = (time + r.nextInt() % 4000);

			meteors[i] = m;

		}
	}

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

	public void end2DMode(GL10 gl) {
		gl.glMatrixMode(GL10.GL_PROJECTION);

		gl.glPopMatrix();

		gl.glMatrixMode(GL10.GL_MODELVIEW);

		gl.glEnable(GL10.GL_DEPTH_TEST);
		gl.glDisable(GL10.GL_BLEND);
	}

	private void renderQuads(GL10 gl) {

		// gl.glDisable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_BLEND);

		gl.glBlendFunc(GL10.GL_DST_ALPHA, GL10.GL_ONE);
		// 

		gl.glColor4f(1.0f, 1.0f, 1.0f, 0.15f);

		this.stars1.bind();

		for (int i = 0; i < AMOUNT; ++i) {
			item = movingQuads[i];

			if (i == AMOUNT / 2) {
				this.stars2.bind();
			}

			gl.glPushMatrix();

			gl.glTranslatef(item.positionX, item.positionY, 0.0f);
			gl.glRotatef(item.rotate, 0.0f, 0.0f, 1.0f);
			gl.glScalef(item.positionZ, item.positionZ, 1.0f);
			this.quad.draw(gl);

			gl.glPopMatrix();

			item.rotate += 5.0f * frameTime;
			item.positionX += item.speedX * frameTime;
			item.positionY += item.speedY * frameTime;
			item.positionZ += item.speedZ * frameTime;

			if (item.positionZ < 1.0f)
				item.positionZ = 1.0f;
			else if (item.positionZ > 4.0f)
				item.positionZ = 4.0f;

			if (item.nextDirectionShift < time || isOutside(item)) {

				xdir = item.speedX > 0.0f ? -1.0f : 1.0f;
				ydir = item.speedY > 0.0f ? -1.0f : 1.0f;
				zdir = item.speedZ > 0.0f ? -1.0f : 1.00f;

				item.speedX = (r.nextInt() % 15.0f) * xdir;
				item.speedY = (r.nextInt() % 15.0f) * ydir;
				item.speedZ = r.nextFloat() * 0.3f * zdir;
				item.nextDirectionShift = (time + r.nextInt() % 4000);
			}

		}

		gl.glEnable(GL10.GL_TEXTURE_2D);

	}

	private boolean isOutside(QuadItem item) {
		return (item.positionX < 0.0f || item.positionX > 800 + 20.0f || item.positionY < 0.0f || item.positionY > 800 + 20.0f);
	}

}
