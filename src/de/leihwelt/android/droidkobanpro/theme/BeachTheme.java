package de.leihwelt.android.droidkobanpro.theme;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.ModelRenderer;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.Renderer;

public class BeachTheme implements Theme {

	private Renderer boxRenderer;
	private Renderer groundRenderer;
	private Renderer stoneRenderer;
	private Renderer targetRenderer;

	private ThemeColor color = new ThemeColor();

	public BeachTheme(GL10 gl, AssetManager am) {

		color.red = 0.3f;
		color.green = 0.3f;
		color.blue = 0.8f;

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
	public void onInit(GL10 gl) {
		gl.glClearColor(0.3f, 0.3f, 0.8f, 1);

	}

	@Override
	public boolean isBoxRotationEnabled() {
		return true;
	}

	@Override
	public ThemeColor getThemeColor() {
		return this.color;
	}

	@Override
	public void load(GL10 gl, AssetManager am) {

		ModelRenderer mr = new ModelRenderer(gl, am, "models/palme/palme.obj", "models/palme/palme.png");
		mr.setScale(0.8f);
		this.boxRenderer = mr;

		this.groundRenderer = new Quad(gl, 1.0f, am, "textures/beach/floor.png");

		mr = new ModelRenderer(gl, am, "models/beach/stones.obj", "models/beach/stones.png");
		mr.setScale(0.5f);

		this.stoneRenderer = mr;

		mr = new ModelRenderer(gl, am, "models/beach/basket.obj", "models/beach/basket.png");
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

	@Override
	public void renderBackground(GL10 gl, DroidkobanRenderer renderer) {
		// TODO Auto-generated method stub

	}

}
