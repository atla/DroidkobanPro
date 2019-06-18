package de.leihwelt.android.droidkobanpro;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;

import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;


public class FlatQuadCentered implements Renderer {

	float scale = 1.0f;
	float height = 1.0f;

	private GLMesh mesh = null;

	public FlatQuadCentered(GL10 gl, float width, float height) {
		
		this.height = height;
		
		mesh = new GLMesh(gl, 6, false, true, false);

		mesh.texCoord(0.0f, 1.0f);
		mesh.vertex(-width/2.0f, height/2.0f, 0.0f);

		mesh.texCoord(1.0f, 1.0f);
		mesh.vertex(width/2.0f, height/2.0f, 0.0f);

		mesh.texCoord(1.0f, 0.0f);
		mesh.vertex(width/2.0f, -height/2.0f, 0.0f);

		mesh.texCoord(1.0f, 0.0f);
		mesh.vertex(width/2.0f, -height/2.0f, 0.0f);

		mesh.texCoord(0.0f, 0.0f);
		mesh.vertex(-width/2.0f, -height/2.0f, 0.0f);

		mesh.texCoord(0.0f, 1.0f);
		mesh.vertex(-width/2.0f, height/2.0f, 0.0f);

	}

	public void draw(GL10 gl) {

		gl.glPushMatrix();

		gl.glDisable(GL10.GL_CULL_FACE);

		mesh.render(GLPrimitiveType.Triangles);

		gl.glPopMatrix();

	}

	@Override
	public int getTextureID() {
		return -1;
	}

	@Override
	public void reset(GL10 gl, AssetManager am) {
		this.mesh.reset();
	}

	public void dispose() {
		this.mesh.dispose();

	}
}
