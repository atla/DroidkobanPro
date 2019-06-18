package de.leihwelt.android.gl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.microedition.khronos.opengles.GL10;

import de.leihwelt.android.droidkobanpro.Droidkoban;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;

public class Texture {

	private static Texture lastTexture = null;

	public enum TextureFilter {
		Nearest, Linear, MipMap
	}

	public enum TextureWrap {
		ClampToEdge, Wrap
	}

	private int handle;
	private GL10 gl;

	// original image size
	private int height;
	private int width;

	private String file = null;
	private int resource = -1;
	private AssetManager am;
	private Resources r;

//	public static Texture load(GL10 gl, InputStream textureData) {
//		Bitmap bitmap = BitmapFactory.decodeStream(textureData);
//		Texture tex = new Texture(gl, bitmap, TextureFilter.Linear, TextureFilter.Linear, TextureWrap.Wrap, TextureWrap.Wrap);
//		bitmap.recycle();
//		return tex;
//	}

	public int getTextureId() {
		return this.handle;
	}

	public Texture(GL10 gl, AssetManager am, String file) throws IOException {
		this.file = file;
		this.am = am;

		recreate(gl);
	}

	public Texture(GL10 gl, Resources r, int resource) throws IOException {
		this.resource = resource;
		this.r = r;

		recreate(gl);
	}

	public void recreate(GL10 gl) throws IOException {

		Bitmap bitmap = null;

		if (r != null && resource > 0) {
			bitmap = BitmapFactory.decodeStream(r.openRawResource(resource));
		} else if (am != null && file != null) {
			bitmap = BitmapFactory.decodeStream(am.open(file));
		}

		if (bitmap != null) {
			create(gl, bitmap, TextureFilter.Linear, TextureFilter.Linear, TextureWrap.Wrap, TextureWrap.Wrap);
			bitmap.recycle();
		}

	}

	private Texture(GL10 gl, Bitmap image, TextureFilter minFilter, TextureFilter maxFilter, TextureWrap sWrap, TextureWrap tWrap) {
		create(gl, image, minFilter, maxFilter, sWrap, tWrap);
	}

	private void create(GL10 gl, Bitmap image, TextureFilter minFilter, TextureFilter maxFilter, TextureWrap sWrap, TextureWrap tWrap) {
		this.gl = gl;

		int[] textures = new int[1];
		gl.glGenTextures(1, textures, 0);
		handle = textures[0];

		this.width = image.getWidth();
		this.height = image.getHeight();

		gl.glBindTexture(GL10.GL_TEXTURE_2D, handle);
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, getTextureFilter(minFilter));
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, getTextureFilter(maxFilter));
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, getTextureWrap(sWrap));
		gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, getTextureWrap(tWrap));
		gl.glMatrixMode(GL10.GL_TEXTURE);
		gl.glLoadIdentity();
		buildMipmap(gl, image);
		image.recycle();
	}

	public Texture(GL10 gl, int width, int height, TextureFilter minFilter, TextureFilter maxFilter, TextureWrap sWrap, TextureWrap tWrap) {
		Bitmap.Config config = Bitmap.Config.ARGB_8888;
		Bitmap image = Bitmap.createBitmap(width, height, config);

		create(gl, image, minFilter, maxFilter, sWrap, tWrap);
	}

	private int getTextureFilter(TextureFilter filter) {
		if (filter == TextureFilter.Linear)
			return GL10.GL_LINEAR;
		else if (filter == TextureFilter.Nearest)
			return GL10.GL_NEAREST;
		else
			return GL10.GL_LINEAR_MIPMAP_NEAREST;
	}

	private int getTextureWrap(TextureWrap wrap) {
		if (wrap == TextureWrap.ClampToEdge)
			return GL10.GL_CLAMP_TO_EDGE;
		else
			return GL10.GL_REPEAT;
	}

	private void buildMipmap(GL10 gl, Bitmap bitmap) {

		int level = 0;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		while (height >= 1 || width >= 1 && level < 4) {
			GLUtils.texImage2D(GL10.GL_TEXTURE_2D, level, bitmap, 0);
			if (height == 1 || width == 1) {
				break;
			}

			level++;
			if (height > 1)
				height /= 2;
			if (width > 1)
				width /= 2;

			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}

	/**
	 * Draws the given image to the texture
	 * 
	 * @param gl
	 * @param bitmap
	 * @param x
	 * @param y
	 */
	public void draw(Object bmp, int x, int y) {
		gl.glBindTexture(GL10.GL_TEXTURE_2D, handle);
		Bitmap bitmap = (Bitmap) bmp;
		int level = 0;
		int height = bitmap.getHeight();
		int width = bitmap.getWidth();

		while (height >= 1 || width >= 1 && level < 4) {
			GLUtils.texSubImage2D(GL10.GL_TEXTURE_2D, level, x, y, (Bitmap) bitmap);
			if (height == 1 || width == 1) {
				break;
			}

			level++;
			if (height > 1)
				height /= 2;
			if (width > 1)
				width /= 2;

			Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, width, height, true);
			bitmap.recycle();
			bitmap = bitmap2;
		}
	}

	/**
	 * Binds the texture
	 * 
	 * @param gl
	 */
	public void bind() {

		if (Texture.lastTexture != this) {
			gl.glBindTexture(GL10.GL_TEXTURE_2D, handle);
			Texture.lastTexture = this;
		}

	}

	/**
	 * Disposes the texture and frees the associated resourcess
	 * 
	 * @param gl
	 */
	public void dispose() {

		int[] textures = { handle };
		gl.glDeleteTextures(1, textures, 0);
		handle = 0;
	}

	public int getHeight() {
		return height;
	}

	public int getWidth() {
		return width;
	}
}