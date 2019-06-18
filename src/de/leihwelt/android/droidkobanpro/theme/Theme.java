package de.leihwelt.android.droidkobanpro.theme;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;


import de.leihwelt.android.droidkobanpro.DroidkobanRenderer;
import de.leihwelt.android.droidkobanpro.Renderer;

public interface Theme {

	public static class ThemeColor {
		public float red;
		public float green;
		public float blue;
	}
	
	public Renderer getGroundRenderer();

	public Renderer getStoneRenderer();

	public Renderer getBoxRenderer();

	public Renderer getTargetRenderer();

	public void renderBackground(GL10 gl, DroidkobanRenderer renderer);
	
	public void onInit (GL10 gl);

	public boolean isBoxRotationEnabled ();

	public ThemeColor getThemeColor ();
	
	public boolean providesCustomBackground ();
	
	
	public abstract void load (GL10 gl, AssetManager am);
	public abstract void reload (GL10 gl, AssetManager am);
	
}
