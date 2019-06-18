package de.leihwelt.android.droidkobanpro.theme;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.util.Log;
import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.ModelRenderer;
import de.leihwelt.android.droidkobanpro.Quad;
import de.leihwelt.android.droidkobanpro.Renderer;
import de.leihwelt.android.droidkobanpro.theme.Theme.ThemeColor;

public class MayaTheme implements Theme {

	private Renderer boxRenderer;
	private Renderer groundRenderer;
	private Renderer stoneRenderer;
	private Renderer targetRenderer;
	private ThemeColor color = new ThemeColor();

	public MayaTheme(GL10 gl, AssetManager am) {

		color.red = 0.56f;
		color.green = 0.73f;
		color.blue = 0.49f;

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
	public void renderBackground(GL10 gl, DroidkobanRenderer renderer) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onInit(GL10 gl) {
		gl.glClearColor(0.32f, 0.25f, 0.44f, 1);

	}

	
	@Override
	public boolean isBoxRotationEnabled() {
		return false;
	}

	@Override
	public void load(GL10 gl, AssetManager am) {
		
			Log.v ("MAYA THEME", "LOAD");
		

			ModelRenderer mr = new ModelRenderer(gl, am, "models/maya/statue.obj", "models/maya/statuetex.png");
			mr.setScale(0.55f);
			
			this.boxRenderer = mr;

			this.groundRenderer = new Quad(gl, 1.0f, am, "textures/maya/ground.png");
			this.targetRenderer = new Quad(gl, 1.0f, am, "textures/maya/target.png");

			mr = new ModelRenderer(gl, am, "models/maya/stone.obj", "models/maya/steintex.png");
			mr.setScale(0.5f);
			
			this.stoneRenderer = mr;


	}

	@Override
	public void reload(GL10 gl, AssetManager am) {

		Log.v ("MAYA THEME", "LOAD");
		
		this.boxRenderer.reset(gl, am);
		this.stoneRenderer.reset(gl, am);
		this.targetRenderer.reset(gl, am);
		this.groundRenderer.reset(gl, am);

	}

	@Override
	public boolean providesCustomBackground() {
	
		return false;
	}

}
