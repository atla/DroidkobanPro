package de.leihwelt.android.droidkobanpro.theme;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.util.Log;

import de.leihwelt.android.droidkobanpro.Cube;
import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.Renderer;
import de.leihwelt.android.droidkobanpro.theme.Theme.ThemeColor;

public class DefaultTheme implements Theme {

	private Renderer boxRenderer;
	private Renderer groundRenderer;
	private Renderer stoneRenderer;
	private Renderer targetRenderer;
	private ThemeColor color = new ThemeColor();

	public DefaultTheme(GL10 gl, AssetManager am) {

		color.red = 0.0f;
		color.blue = 0.0f;
		color.green = 0.0f;

		this.load(gl, am);
	}

	@Override
	public Renderer getBoxRenderer() {
		return this.boxRenderer;
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

		return false;
	}

	@Override
	public void renderBackground(GL10 gl, DroidkobanRenderer renderer) {
		// TODO Auto-generated method stub

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
	public ThemeColor getThemeColor() {
		return this.color;
	}

	@Override
	public void load(GL10 gl, AssetManager am) {

		this.boxRenderer = new Cube(gl, 0.8f, am, "textures/wood.png");
		this.groundRenderer = new Quad(gl, 1.0f, am, "textures/floor.png");
		this.stoneRenderer = new Cube(gl, 0.5f, am, "textures/stone.png");
		this.targetRenderer = new Quad(gl, 1.0f, am, "textures/target.png");

	}

	@Override
	public void reload(GL10 gl, AssetManager am) {
		this.boxRenderer.reset(gl, am);
		this.stoneRenderer.reset(gl, am);
		this.targetRenderer.reset(gl, am);
		this.groundRenderer.reset(gl, am);

	}

}
