package de.leihwelt.android.droidkobanpro;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;

public class FlatQuad implements Renderer {

	float scale = 1.0f;
	float height = 1.0f;
	

	private GLMesh mesh = null;
	private float width;

	public FlatQuad(GL10 gl, float size) {
		this (gl, size, size);
	}

	public FlatQuad(GL10 gl, float width, float height) {
		
		this.width = width;
		this.height = height;
		
		createMesh(gl, width, height);
	}

	private void createMesh(GL10 gl, float width, float height) {
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

		//	
		gl.glDisable(GL10.GL_CULL_FACE);

		mesh.render(GLPrimitiveType.Triangles);

	}

	@Override
	public int getTextureID() {
		return -1;
	}

	
	@Override
	public void reset(GL10 gl, AssetManager am) {
		createMesh(gl, width, height);
	}
}
