package com.tutk.IOTC;
import com.tutk.IOTC.monitor.BaseMediaCodecMonitor;
import com.tutk.IOTC.monitor.I_MonitorExternalSetup;
import com.yuantuo.netsdk.TKCamHelper;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
/**
 * 软解码  后期和硬解码合并
 * @author syf
 */
public class MediaSoftCodecMonitor extends BaseMediaCodecMonitor implements I_MonitorExternalSetup	{
	private Rendering rendering = null;
	private Bitmap mLastFrame = null;
	private Rect mRectCanvas = new Rect();
	private Rect mRectMonitor = new Rect(); 
	private int mAVChannel = -1;
	private int vLeft, vTop, vRight, vBottom;
	private int mCurVideoWidth = 0,mCurVideoHeight=0;
	private float mCurrentScale = 1.0f;
	private PointF mMidPoint = new PointF();
	private PointF mMidPointForCanvas = new PointF();
	public MediaSoftCodecMonitor(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int width, int height) {
		super.surfaceChanged(arg0, arg1, width, height);
			mRectMonitor.set(0, 0, width, height);
			mRectCanvas.set(0, 0, width, height);
			if (mCurVideoWidth == 0 || mCurVideoHeight == 0) {
				if (height < width) { // landscape layout
					mRectCanvas.right = 4 * height / 3;
					mRectCanvas.offset((width - mRectCanvas.right) / 2, 0);
				} else { // portrait layout
					mRectCanvas.bottom = 3 * width / 4;
					mRectCanvas.offset(0, (height - mRectCanvas.bottom) / 2);
				}
			} else {
				if ((mRectMonitor.bottom - mRectMonitor.top) < (mRectMonitor.right - mRectMonitor.left)) { // landscape layout
					Log.i("IOTCamera", "Landscape layout");
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.right = (int) (mRectMonitor.bottom * ratio);
					mRectCanvas.offset((mRectMonitor.right - mRectCanvas.right) / 2, 0);
					Log.i("IOTCamera", "mRectCanvas.left"+mRectCanvas.left+"mRectCanvas.top"+mRectCanvas.top+"mRectCanvas.right"+mRectCanvas.right+"mRectCanvas.bottom"+mRectCanvas.bottom);
				} else { // portrait layout
					Log.i("IOTCamera", "Portrait layout");
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.bottom = (int) (mRectMonitor.right / ratio);
					Log.i("IOTCamera", "mRectCanvas.left"+mRectCanvas.left+"mRectCanvas.top"+mRectCanvas.top+"mRectCanvas.right"+mRectCanvas.right+"mRectCanvas.bottom"+mRectCanvas.bottom);
				}
			}
			vLeft = mRectCanvas.left;
			vTop = mRectCanvas.top;
			vRight = mRectCanvas.right;
			vBottom = mRectCanvas.bottom;
			mCurrentScale = 1.0f;
			parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
			parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
		}
	@Override
	public void attachCamera(TKCamHelper camera, int avChannel) {
		Log.d(TAG,"attachCamera");
		if(camera!=null){
			mCamera = camera;
			mAVChannel = avChannel;
			mCamera.registerIOTCListener(this);
			rendering = new Rendering();
			rendering.setPriority(Thread.MAX_PRIORITY);
			rendering.start();
		 }
	}
	@Override
	public  void deattachCamera() {
		if(mCamera !=null){
			mCamera.unregisterIOTCListener(this);
			mCamera = null;
		}
		if(rendering !=null){
			rendering.stopThread();
		}
	}
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		if (mAVChannel == avChannel) {
			mLastFrame = bmp;
			if (bmp.getWidth() > 0 && bmp.getHeight() > 0 &&
				(bmp.getWidth() != mCurVideoWidth || bmp.getHeight() != mCurVideoHeight)) {
				mCurVideoWidth = bmp.getHeight();
				mCurVideoHeight = bmp.getWidth();  //modifi syf
				mRectCanvas.set(0, 0, mRectMonitor.right, mRectMonitor.bottom);
				if ((mRectMonitor.bottom - mRectMonitor.top) < (mRectMonitor.right - mRectMonitor.left)) { // landscape layout
					Log.i(TAG, "Landscape layout");
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.right = (int) (mRectMonitor.bottom * ratio);
					mRectCanvas.offset((mRectMonitor.right - mRectCanvas.right) / 2, 0);
				} else { // portrait layout
					Log.i(TAG, "Portrait layout");
					double ratio = (double) mCurVideoWidth / mCurVideoHeight;
					mRectCanvas.bottom = (int) (mRectMonitor.right / ratio);
					//mRectCanvas.offset(0, (mRectMonitor.bottom - mRectCanvas.bottom) / 2);
				}
				vLeft = mRectCanvas.left;
				vTop = mRectCanvas.top;
				vRight = mRectCanvas.right;
				vBottom = mRectCanvas.bottom;
				mCurrentScale = 1.0f;
				parseMidPoint(mMidPoint, vLeft, vTop, vRight, vBottom);
				parseMidPoint(mMidPointForCanvas, vLeft, vTop, vRight, vBottom);
				Log.i(TAG, "Change canvas size (" + (mRectCanvas.right - mRectCanvas.left) + ", " + (mRectCanvas.bottom - mRectCanvas.top) + ")");
			}
		}
	}
	private class  Rendering extends Thread{
		private boolean mIsRunningThread = false;
		private Canvas videoCanvas = null ;
		private Matrix matrix = null;
		public synchronized void stopThread(){
			mIsRunningThread = false;
			try {
				mWaitObjectForStopThread.notify();
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		@Override
		public void run() {
			mIsRunningThread = true;
			while(mIsRunningThread){
				if(mLastFrame != null && !mLastFrame.isRecycled()){
					Log.e(TAG, "===valid data===");
					try {
						videoCanvas = mSurHolder.lockCanvas();
						matrix = new Matrix();
						if(videoCanvas!=null){
							matrix.postRotate(-90);
							Bitmap dstbmp = Bitmap.createBitmap(mLastFrame, 0, 0, mLastFrame.getWidth(), mLastFrame.getHeight(),
									matrix, true);
							videoCanvas.drawBitmap(dstbmp, null, mRectCanvas, null);
						}
					} catch (Exception e) {
						Log.e(TAG,"===Rendering ("+e.getMessage()+")===");
					}finally{
						if (videoCanvas != null){
							mSurHolder.unlockCanvasAndPost(videoCanvas);
						}
						videoCanvas = null ;
					}
				}
				try {
					synchronized (mWaitObjectForStopThread) {
						mWaitObjectForStopThread.wait(33);
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			Log.e(TAG,"===Rendering exit===");
		}
	}
	private void parseMidPoint(PointF point, float left, float top, float right, float bottom) {
		point.set((left + right) / 2, (top + bottom) / 2);
	}
}
