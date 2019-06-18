package de.leihwelt.android.droidkobanpro;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.opengles.GL10;

import de.leihwelt.android.droidkobanpro.hud.HUD;
import de.leihwelt.android.droidkobanpro.theme.*;
import de.leihwelt.android.gl.GLMesh;

import android.content.res.AssetManager;
import android.util.Log;

public enum RessourcePool {
	INSTANCE;
/*
	private boolean finishedLoading = false;

	public Theme[] themes = new Theme[3];
	public ModelRenderer playerRenderer;
	public ModelRenderer playerRendererMove;
	public HUD hud = null;

	private Map<String, String> preloadedModelData = new HashMap<String, String>();

	private AssetManager am = null;

	private RessourcePool() {

	}

	public boolean finishedLoading() {
		return this.finishedLoading;
	}

	public String loadObjString(String assetFile) {

		if (this.preloadedModelData.containsKey(assetFile))
			return this.preloadedModelData.get(assetFile);
		
		String line = "";

		try {
			InputStream in = am.open(assetFile);

			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));
				StringBuffer b = new StringBuffer();
				String l = reader.readLine();
				while (l != null) {
					b.append(l);
					b.append("\n");
					l = reader.readLine();
				}

				line = b.toString();
				reader.close();
			} catch (Exception ex) {
				throw new RuntimeException("couldn't load file mesh from input stream");
			}
		} catch (IOException e) {
			Log.e("RessourcePool", "could not load asset: " + assetFile);
		}

		// cache the loaded data
		this.preloadedModelData.put(assetFile, line);
		
		return line;
	}

	public void load(GL10 gl, AssetManager am) {
		this.am = am;
		this.finishedLoading = true;
		
		// preload player models
		this.loadObjString("models/droid/droid.obj");
		this.loadObjString("models/droid/droid_move.obj");
		// beach theme
		this.loadObjString("models/palme/palme.obj");
		this.loadObjString("models/beach/stones.obj");
		this.loadObjString("models/beach/basket.obj");
		// winter theme
		this.loadObjString("models/winter/cylinder.obj");		
		this.loadObjString("models/winter/pile2.obj");
		this.loadObjString("models/winter/snowman.obj");
		
		
//
//		try {
//
//			this.loadThemes(gl, am);
//
//			this.hud = new HUD(gl, am);
//			
//			this.playerRenderer = new ModelRenderer(gl, am.open("models/droid/droid.obj"), am.open("models/droid/droidt.png"));
//			this.playerRenderer.setScale(0.3f);	
//
//			this.playerRendererMove = new ModelRenderer(gl, am.open("models/droid/droid_move.obj"), am.open("models/droid/droidt.png"));
//			this.playerRendererMove.setScale(0.3f);
//
//		} catch (IOException e) {
//			Log.e("RessourcePool", "Error loading ressources!");
//		}

		
	}

	public void loadThemes(GL10 gl, AssetManager am) {
//		this.themes[0] = new DefaultTheme(gl, am);
//		this.themes[1] = new BeachTheme(gl, am);
//		this.themes[2] = new WinterTheme(gl, am);
	}
	*/
}
