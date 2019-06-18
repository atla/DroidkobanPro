package de.leihwelt.android.droidkobanpro;

import java.io.IOException;
import java.io.InputStream;
import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;
import de.leihwelt.android.gl.Texture.TextureFilter;
import de.leihwelt.android.gl.Texture.TextureWrap;

public class Quad implements Renderer {

	float scale = 1.0f;
	float height = 1.0f;

	private GLMesh mesh = null;
	private Texture texture = null;
	private float size;

//	public Quad(GL10 gl, float size) {
//		this(gl, size, null);
//	}

	public Quad(GL10 gl, float size, AssetManager am, String texture) {

		this.size = size / 2.0f;
		

		createMesh(gl);
		
		try {
			this.texture = new Texture (gl, am, texture);
		} catch (IOException e) {
		}

	}

	
	public void draw(GL10 gl) {

		if (texture != null)
			texture.bind();

		mesh.render(GLPrimitiveType.Triangles);
	}

	@Override
	public int getTextureID() {
		return this.texture.getTextureId();
	}

	@Override
	public void reset(GL10 gl, AssetManager am) {
		this.mesh.dispose();
		

		this.createMesh(gl);
		try {
			this.texture.recreate(gl);
		} catch (IOException e) {
		}
	}

//	private void loadTexture(GL10 gl) {
//
//		Bitmap bitmap = BitmapFactory.decodeStream(tex);
//		texture = new Texture(gl, bitmap, TextureFilter.Linear, TextureFilter.Linear, TextureWrap.Wrap, TextureWrap.Wrap);
//		bitmap.recycle();
//
//	}

	private void createMesh(GL10 gl) {
		mesh = new GLMesh(gl, 6, false, true, true);


		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(-size, 0.0f, size);

		mesh.texCoord(1.0f, 1.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(size, 0.0f, size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(size, 0.0f, -size);

		mesh.texCoord(1.0f, 0.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(size, 0.0f, -size);

		mesh.texCoord(0.0f, 0.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(-size, 0.0f, -size);

		mesh.texCoord(0.0f, 1.0f);
		mesh.normal(0.0f, 2.0f, -4.0f);
		mesh.vertex(-size, 0.0f, size);
	}

}
