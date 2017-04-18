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

package cc.wulian.app.model.device.view;

import android.graphics.Camera;
import android.graphics.Matrix;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * An animation that rotates the view on the Y axis between two specified
 * angles. This animation also adds a translation on the Z axis (depth) to
 * improve the effect.
 */
public class Rotate3dAnimation extends Animation
{
	public static final int ROTATE_X = 0;
	public static final int ROTATE_Y = 1;
	public static final int ROTATE_Z = 2;
	
	private final float mFromDegrees;
	private final float mToDegrees;
	private final float mCenterX;
	private final float mCenterY;
	private final float mDepthZ;
	private final boolean mReverse;
	private Camera mCamera;
	private final int mRotateOri;

	/**
	 * Creates a new 3D rotation on the Y axis. The rotation is defined by its
	 * start angle and its end angle. Both angles are in degrees. The rotation is
	 * performed around a center point on the 2D space, definied by a pair of X
	 * and Y coordinates, called centerX and centerY. When the animation starts, a
	 * translation on the Z axis (depth) is performed. The length of the
	 * translation can be specified, as well as whether the translation should be
	 * reversed in time.
	 * 
	 * @param fromDegrees
	 *          the start angle of the 3D rotation
	 * @param toDegrees
	 *          the end angle of the 3D rotation
	 * @param centerX
	 *          the X center of the 3D rotation
	 * @param centerY
	 *          the Y center of the 3D rotation
	 * @param reverse
	 *          true if the translation should be reversed, false otherwise
	 * @param raoteOri TODO
	 */
	public Rotate3dAnimation( float fromDegrees, float toDegrees, float centerX, float centerY, float depthZ, boolean reverse, int raoteOri )
	{
		mFromDegrees = fromDegrees;
		mToDegrees = toDegrees;
		mCenterX = centerX;
		mCenterY = centerY;
		mDepthZ = depthZ;
		mReverse = reverse;
		mRotateOri = raoteOri;
	}

	@Override
	public void initialize( int width, int height, int parentWidth, int parentHeight )
	{
		super.initialize(width, height, parentWidth, parentHeight);
		mCamera = new Camera();
	}

	@Override
	protected void applyTransformation( float interpolatedTime, Transformation t )
	{
		final float fromDegrees = mFromDegrees;
		float degrees = fromDegrees + ((mToDegrees - fromDegrees) * interpolatedTime);

		final float centerX = mCenterX;
		final float centerY = mCenterY;
		final Camera camera = mCamera;

		final Matrix matrix = t.getMatrix();

		camera.save();
		if (mReverse)
		{
			camera.translate(0.0f, 0.0f, mDepthZ * interpolatedTime);
		}
		else
		{
			camera.translate(0.0f, 0.0f, mDepthZ * (1.0f - interpolatedTime));
		}
		final int rotateOri = mRotateOri;
		switch (rotateOri){
			case ROTATE_X:
				camera.rotateX(degrees);
				break;
			case ROTATE_Z:
				camera.rotateZ(degrees);
				break;
			case ROTATE_Y:
			default :
				camera.rotateY(degrees);
				break;
		}
		camera.getMatrix(matrix);
		camera.restore();
		
		matrix.preTranslate(-centerX, -centerY);
		matrix.postTranslate(centerX, centerY);
	}

	public float getFromDegrees(){
		return mFromDegrees;
	}

	public float getToDegrees(){
		return mToDegrees;
	}

	public float getCenterX(){
		return mCenterX;
	}

	public float getCenterY(){
		return mCenterY;
	}

	public float getDepthZ(){
		return mDepthZ;
	}

	public boolean isReverse(){
		return mReverse;
	}

	public int getRotateOri(){
		return mRotateOri;
	}
}
