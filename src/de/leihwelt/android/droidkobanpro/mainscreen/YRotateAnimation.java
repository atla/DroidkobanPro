/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.leihwelt.android.droidkobanpro.mainscreen;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.graphics.Camera;
import android.graphics.Matrix;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class YRotateAnimation extends Animation {

	private Camera mCamera;
	private boolean flip;

	public YRotateAnimation() {
//		mFromDegrees = fromDegrees;
//		mToDegrees = toDegrees;
//		mCenterX = centerX;
//		mCenterY = centerY;
//		mDepthZ = depthZ;
//		mReverse = reverse;
//
//		if (toDegrees < 90)
//			Log.v("Transform3D", "to degrees < 90  FROM: " + fromDegrees + " TO: " + toDegrees);
//		else
//			Log.v("Transform3D", "to degrees > 90  FROM: " + fromDegrees + " TO: " + toDegrees);
	}

	@Override
	public void initialize(int width, int height, int parentWidth, int parentHeight) {
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();

	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {


		final Camera camera = mCamera;
		final Matrix matrix = t.getMatrix();


		camera.save();
	
		camera.rotateY(20);
		camera.getMatrix(matrix);
		camera.restore();

	}

	public void setFlip(boolean b) {
		flip = b;

	}
}
