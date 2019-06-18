package de.leihwelt.android.droidkobanpro;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.khronos.opengles.GL10;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import de.leihwelt.android.gl.GLMesh;
import de.leihwelt.android.gl.ObjModelImporter;
import de.leihwelt.android.gl.Texture;
import de.leihwelt.android.gl.GLMesh.GLPrimitiveType;
import de.leihwelt.android.gl.Texture.TextureFilter;
import de.leihwelt.android.gl.Texture.TextureWrap;

public class ModelRenderer implements Renderer {

	private GLMesh mesh = null;
	private Texture texture = null;

	private float scale = 0.35f;

	private String meshData;
	private String tex;

	public ModelRenderer(GL10 gl, AssetManager am, String mesh, String tex) {

		try {
			this.meshData = ObjModelImporter.getMeshString(am.open(mesh));
		} catch (IOException e) {
		}

		this.tex = tex;

		loadMesh(gl);
		loadTexture(gl, am);
	}
	

	public ModelRenderer(GL10 gl, AssetManager am,  GLMesh mesh, String tex) {

		this.mesh = mesh;
		
		this.tex = tex;
		
		loadTexture (gl, am);
	}

	private void loadMesh(GL10 gl) {
		this.mesh = ObjModelImporter.loadObj(gl, meshData);

	}

	private void loadTexture(GL10 gl, AssetManager am) {

		try {
			texture = new Texture(gl, am, tex);
		} catch (IOException e) {
		}

	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	@Override
	public void draw(GL10 gl) {
		gl.glScalef(scale, scale, scale);

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
		this.texture.dispose();

		this.loadMesh(gl);
		this.loadTexture(gl, am);
	}

	public GLMesh getMesh() {
		return this.mesh;
	}
}
