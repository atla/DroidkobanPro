package de.leihwelt.android.gl;

import javax.microedition.khronos.opengles.GL10;

public class FrustumCuller {

	public float[][] frustum;
	public MatrixGrabber matrixGrabber;
	private float[] clip;

	public FrustumCuller() {
		matrixGrabber = new MatrixGrabber();
		frustum = new float[6][4];
		clip = new float[16];
	}

	public void update(GL10 gl) {

		matrixGrabber.getCurrentState(gl);
		float t;
		float[] projLocal = matrixGrabber.mProjection;
		float[] modlLocal = matrixGrabber.mModelView;
		float[] clipLocal = clip;

		/* Combine the two matrices (multiply projection by modelview) */
		clipLocal[0] = modlLocal[0] * projLocal[0] + modlLocal[1] * projLocal[4] + modlLocal[2] * projLocal[8] + modlLocal[3] * projLocal[12];
		clipLocal[1] = modlLocal[0] * projLocal[1] + modlLocal[1] * projLocal[5] + modlLocal[2] * projLocal[9] + modlLocal[3] * projLocal[13];
		clipLocal[2] = modlLocal[0] * projLocal[2] + modlLocal[1] * projLocal[6] + modlLocal[2] * projLocal[10] + modlLocal[3] * projLocal[14];
		clipLocal[3] = modlLocal[0] * projLocal[3] + modlLocal[1] * projLocal[7] + modlLocal[2] * projLocal[11] + modlLocal[3] * projLocal[15];

		clipLocal[4] = modlLocal[4] * projLocal[0] + modlLocal[5] * projLocal[4] + modlLocal[6] * projLocal[8] + modlLocal[7] * projLocal[12];
		clipLocal[5] = modlLocal[4] * projLocal[1] + modlLocal[5] * projLocal[5] + modlLocal[6] * projLocal[9] + modlLocal[7] * projLocal[13];
		clipLocal[6] = modlLocal[4] * projLocal[2] + modlLocal[5] * projLocal[6] + modlLocal[6] * projLocal[10] + modlLocal[7] * projLocal[14];
		clipLocal[7] = modlLocal[4] * projLocal[3] + modlLocal[5] * projLocal[7] + modlLocal[6] * projLocal[11] + modlLocal[7] * projLocal[15];

		clipLocal[8] = modlLocal[8] * projLocal[0] + modlLocal[9] * projLocal[4] + modlLocal[10] * projLocal[8] + modlLocal[11] * projLocal[12];
		clipLocal[9] = modlLocal[8] * projLocal[1] + modlLocal[9] * projLocal[5] + modlLocal[10] * projLocal[9] + modlLocal[11] * projLocal[13];
		clipLocal[10] = modlLocal[8] * projLocal[2] + modlLocal[9] * projLocal[6] + modlLocal[10] * projLocal[10] + modlLocal[11] * projLocal[14];
		clipLocal[11] = modlLocal[8] * projLocal[3] + modlLocal[9] * projLocal[7] + modlLocal[10] * projLocal[11] + modlLocal[11] * projLocal[15];

		clipLocal[12] = modlLocal[12] * projLocal[0] + modlLocal[13] * projLocal[4] + modlLocal[14] * projLocal[8] + modlLocal[15] * projLocal[12];
		clipLocal[13] = modlLocal[12] * projLocal[1] + modlLocal[13] * projLocal[5] + modlLocal[14] * projLocal[9] + modlLocal[15] * projLocal[13];
		clipLocal[14] = modlLocal[12] * projLocal[2] + modlLocal[13] * projLocal[6] + modlLocal[14] * projLocal[10] + modlLocal[15] * projLocal[14];
		clipLocal[15] = modlLocal[12] * projLocal[3] + modlLocal[13] * projLocal[7] + modlLocal[14] * projLocal[11] + modlLocal[15] * projLocal[15];

		/* Extract the numbers for the RIGHT plane */
		frustum[0][0] = clipLocal[3] - clipLocal[0];
		frustum[0][1] = clipLocal[7] - clipLocal[4];
		frustum[0][2] = clipLocal[11] - clipLocal[8];
		frustum[0][3] = clipLocal[15] - clipLocal[12];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[0][0] * frustum[0][0] + frustum[0][1] * frustum[0][1] + frustum[0][2] * frustum[0][2]);
		frustum[0][0] /= t;
		frustum[0][1] /= t;
		frustum[0][2] /= t;
		frustum[0][3] /= t;

		/* Extract the numbers for the LEFT plane */
		frustum[1][0] = clipLocal[3] + clipLocal[0];
		frustum[1][1] = clipLocal[7] + clipLocal[4];
		frustum[1][2] = clipLocal[11] + clipLocal[8];
		frustum[1][3] = clipLocal[15] + clipLocal[12];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[1][0] * frustum[1][0] + frustum[1][1] * frustum[1][1] + frustum[1][2] * frustum[1][2]);
		frustum[1][0] /= t;
		frustum[1][1] /= t;
		frustum[1][2] /= t;
		frustum[1][3] /= t;

		/* Extract the BOTTOM plane */
		frustum[2][0] = clipLocal[3] + clipLocal[1];
		frustum[2][1] = clipLocal[7] + clipLocal[5];
		frustum[2][2] = clipLocal[11] + clipLocal[9];
		frustum[2][3] = clipLocal[15] + clipLocal[13];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[2][0] * frustum[2][0] + frustum[2][1] * frustum[2][1] + frustum[2][2] * frustum[2][2]);
		frustum[2][0] /= t;
		frustum[2][1] /= t;
		frustum[2][2] /= t;
		frustum[2][3] /= t;

		/* Extract the TOP plane */
		frustum[3][0] = clipLocal[3] - clipLocal[1];
		frustum[3][1] = clipLocal[7] - clipLocal[5];
		frustum[3][2] = clipLocal[11] - clipLocal[9];
		frustum[3][3] = clipLocal[15] - clipLocal[13];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[3][0] * frustum[3][0] + frustum[3][1] * frustum[3][1] + frustum[3][2] * frustum[3][2]);
		frustum[3][0] /= t;
		frustum[3][1] /= t;
		frustum[3][2] /= t;
		frustum[3][3] /= t;

		/* Extract the FAR plane */
		frustum[4][0] = clipLocal[3] - clipLocal[2];
		frustum[4][1] = clipLocal[7] - clipLocal[6];
		frustum[4][2] = clipLocal[11] - clipLocal[10];
		frustum[4][3] = clipLocal[15] - clipLocal[14];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[4][0] * frustum[4][0] + frustum[4][1] * frustum[4][1] + frustum[4][2] * frustum[4][2]);
		frustum[4][0] /= t;
		frustum[4][1] /= t;
		frustum[4][2] /= t;
		frustum[4][3] /= t;

		/* Extract the NEAR plane */
		frustum[5][0] = clipLocal[3] + clipLocal[2];
		frustum[5][1] = clipLocal[7] + clipLocal[6];
		frustum[5][2] = clipLocal[11] + clipLocal[10];
		frustum[5][3] = clipLocal[15] + clipLocal[14];

		/* Normalize the result */
		t = (float) Math.sqrt(frustum[5][0] * frustum[5][0] + frustum[5][1] * frustum[5][1] + frustum[5][2] * frustum[5][2]);
		frustum[5][0] /= t;
		frustum[5][1] /= t;
		frustum[5][2] /= t;
		frustum[5][3] /= t;

	}

	public boolean isPointInFrustum(float x, float y, float z) {

		for (int p = 0; p < 6; p++)
			if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= 0)
				return false;
		return true;
	}

	public boolean isSphereInFrustum(float x, float y, float z, float radius) {

		for (int p = 0; p < 6; p++)
			if (frustum[p][0] * x + frustum[p][1] * y + frustum[p][2] * z + frustum[p][3] <= -radius)
				return false;
		return true;
	}
}
