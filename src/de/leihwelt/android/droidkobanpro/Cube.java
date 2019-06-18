package de.leihwelt.android.droidkobanpro;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import com.openfeint.internal.resource.Resource;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;
import de.leihwelt.android.gl.Texture.TextureFilter;
import de.leihwelt.android.gl.Texture.TextureWrap;

public class Cube implements Renderer {

	private GLMesh mesh = null;
	private Texture texture = null;
	private int tex;
	private float size;
	private float height;

	public Cube(GL10 gl, float height, Resources r, int tex) {

		create(gl, height, r, tex);

	}

	// public Cube(GL10 gl, float height, Bitmap bitmap) {
	// create(gl, height, bitmap);
	// }

	public Cube(GL10 gl, float height, AssetManager am, String string) {
		createMesh(gl, height);

		try {
			texture = new Texture(gl, am, string);
		} catch (IOException e) {
		}
	}

	private void create(GL10 gl, float height, Resources r, int tex) {

		this.tex = tex;

		createMesh(gl, height);

		try {
			texture = new Texture(gl, r, tex);
		} catch (IOException e) {
		}
	}

	private void createMesh(GL10 gl, float height) {

		this.size = 0.5f;
		this.height = height;

		createMesh(gl);
	}

	private void createMesh(GL10 gl) {
		mesh = new GLMesh(gl, 5 * 6, false, true, true);
		// TOP
		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(-size, height, size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(size, height, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(size, height, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(size, height, -size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(-size, height, -size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, -1.0f, 0.0f);
		mesh.vertex(-size, height, size);

		// FRONT
		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(-size, height, -size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(size, height, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(size, 0.0f, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(size, 0.0f, -size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(-size, 0.0f, -size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, -1.0f);
		mesh.vertex(-size, height, -size);

		// BACK
		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(size, height, size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(-size, height, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(-size, 0.0f, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(-size, 0.0f, size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(size, 0.0f, size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 0.0f, 1.0f);
		mesh.vertex(size, height, size);

		// LEFT

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, 0.0f, -size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, 0.0f, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, height, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, height, size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, height, -size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(-1.0f, 0.0f, 0.0f);
		mesh.vertex(-size, 0.0f, -size);

		// RIGHT

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, 0.0f, size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, 0.0f, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, height, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, height, -size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, height, size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(1.0f, 0.0f, 0.0f);
		mesh.vertex(size, 0.0f, size);
	}

	public void draw(GL10 gl) {

		if (this.texture != null)
			texture.bind();

		mesh.render(GLPrimitiveType.Triangles);
	}

	@Override
	public int getTextureID() {
		return this.texture.getTextureId();
	}

	@Override
	public void reset(GL10 gl, AssetManager a) {
		this.mesh.dispose();
		this.createMesh(gl);
		try {
			this.texture.recreate(gl);
		} catch (IOException e) {
		}
	}
}
