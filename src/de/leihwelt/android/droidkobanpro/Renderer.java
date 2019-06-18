package de.leihwelt.android.droidkobanpro;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;

public interface Renderer {
	public void draw(GL10 gl);
	public int getTextureID ();
	public void reset(GL10 gl, AssetManager am);
}
