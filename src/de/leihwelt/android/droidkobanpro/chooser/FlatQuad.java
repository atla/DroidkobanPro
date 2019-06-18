package de.leihwelt.android.droidkobanpro.chooser;

import javax.microedition.khronos.opengles.GL10;

import de.leihwelt.android.gl.GLMesh;

public class FlatQuad implements Renderer {

	float scale = 1.0f;
	float height = 1.0f;

	private GLMesh mesh = null;

	public FlatQuad(GL10 gl, float width, float height) {
		mesh = new GLMesh(gl, 6, false, true, false);

		mesh.texCoord(0.0f, 1.0f);
		mesh.vertex(0.0f, height, 0.0f);

		mesh.texCoord(1.0f, 1.0f);
		mesh.vertex(width, height, 0.0f);

		mesh.texCoord(1.0f, 0.0f);
		mesh.vertex(width, 0.0f, 0.0f);

		mesh.texCoord(1.0f, 0.0f);
		mesh.vertex(width, 0.0f, 0.0f);

		mesh.texCoord(0.0f, 0.0f);
		mesh.vertex(0.0f, 0.0f, 0.0f);

		mesh.texCoord(0.0f, 1.0f);
		mesh.vertex(0.0f, height, 0.0f);

	}

	public void draw(GL10 gl) {

		mesh.render(de.leihwelt.android.gl.GLMesh.GLPrimitiveType.Triangles);

	}

	@Override
	public int getTextureID() {
		return -1;
	}

	@Override
	public void reset(GL10 gl) {
		this.mesh.reset();
	}

	public void dispose() {
		this.mesh.dispose();

	}
}
