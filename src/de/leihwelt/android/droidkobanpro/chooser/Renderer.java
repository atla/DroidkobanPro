package de.leihwelt.android.droidkobanpro.chooser;

import javax.microedition.khronos.opengles.GL10;

public interface Renderer {
	public void draw(GL10 gl);
	public int getTextureID ();
	public void reset(GL10 gl);
}
