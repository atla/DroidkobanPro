package de.leihwelt.android.objects;

import javax.microedition.khronos.opengles.GL10;

public class RenderObject {
	public final static int NO_ACTION = 0;
	public final static int MOVE_TO_X = 1;
	public final static int MOVE_TO_Y = 2;
	public final static int MOVE_TO_Z = 3;
	public final static int FLY_UP = 4;

	public float x = 0.0f;
	public float y = 0.0f;
	public float z = 0.0f;

	public float moveTo = 0.0f;
	public float speedInc = 1.0f;
	public int action = 0;

	public float rot = -1.0f;
	public int fieldId = 0;
	public int objectRendererIndex = 0;
	public int actionRenderIndex = -1;

	public int pickId = -1;
	public boolean picked = false;

	// public int texture = 0;

	public void setCodedColor(GL10 gl) {
		int toCode = pickId;
		int b = toCode / (144);
		toCode %= 144;
		int g = toCode / 12;
		int r = toCode % 12;

		r *= 20;
		g *= 20;
		b *= 20;

		gl.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, 1.0f);
	}

}
