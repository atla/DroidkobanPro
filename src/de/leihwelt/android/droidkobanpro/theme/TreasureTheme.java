package de.leihwelt.android.droidkobanpro.theme;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.ModelRenderer;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.Renderer;

public class TreasureTheme implements Theme {

	private Renderer boxRenderer;
	private Renderer groundRenderer;
	private Renderer stoneRenderer;
	private Renderer targetRenderer;
	private ThemeColor color = new ThemeColor();

	public TreasureTheme(GL10 gl, AssetManager am) {

		Log.v("BeachTheme", "CONSTRUCTOR CALLED");

		color.red = 0.0f;
		color.green = 0.2f;
		color.blue = 0.0f;
		this.load(gl, am);
	}

	@Override
	public ThemeColor getThemeColor() {
		return this.color;
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
		gl.glClearColor(0.0f, 0.0f, 0.2f, 1);

	}

	@Override
	public boolean isBoxRotationEnabled() {
		return false;
	}

	@Override
	public void load(GL10 gl, AssetManager am) {
		ModelRenderer mr = new ModelRenderer(gl, am, "models/treasure/treasure.obj", "models/treasure/treasure.png");
		mr.setScale(0.5f);

		this.boxRenderer = mr;

		this.groundRenderer = new Quad(gl, 1.0f, am, "textures/treasure/floor.png");

		mr = new ModelRenderer(gl, am, "models/barrel/barrel.obj", "models/barrel/barrel.png");
		mr.setScale(0.5f);

		this.stoneRenderer = mr;

		mr = new ModelRenderer(gl, am, "models/chest/chest.obj", "models/chest/chest.png");
		mr.setScale(0.5f);

		this.targetRenderer = mr;

	}

	@Override
	public void reload(GL10 gl, AssetManager am) {
		this.boxRenderer.reset(gl, am);
		this.stoneRenderer.reset(gl, am);
		this.targetRenderer.reset(gl, am);
		this.groundRenderer.reset(gl, am);

	}

}
