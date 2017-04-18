/*
 * Copyright (C) 2010 ZXing authors
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

package cc.wulian.smarthomev5.fragment.singin.handler;

import java.util.Hashtable;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;

import cc.wulian.smarthomev5.fragment.singin.IQRScanHandlerResult;
import cc.wulian.smarthomev5.fragment.singin.QRScanFragmentV5;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.library.R;
import com.google.zxing.client.android.camera.CameraManager;
import com.google.zxing.client.android.camera.PlanarYUVLuminanceSource;

final class DecodeHandler extends Handler
{

	private static final String TAG = DecodeHandler.class.getSimpleName();

	private final IQRScanHandlerResult scanHandlerResult;
	private final MultiFormatReader multiFormatReader;

	DecodeHandler(IQRScanHandlerResult scanHandlerResult, Hashtable<DecodeHintType, Object> hints )
	{
		this.scanHandlerResult = scanHandlerResult;
		multiFormatReader = new MultiFormatReader();
		multiFormatReader.setHints(hints);
	}

	@Override
	public void handleMessage( Message message ){
		int msgID = message.what;
		if (msgID == R.id.decode){
			decode((byte[]) message.obj, message.arg1, message.arg2);
		}
		else if (msgID == R.id.quit){
			Looper.myLooper().quit();
		}
	}

	/**
	 * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader objects from one decode to the next.
	 * 
	 * @param data
	 *          The YUV preview frame.
	 * @param width
	 *          The width of the preview frame.
	 * @param height
	 *          The height of the preview frame.
	 */
	private void decode( byte[] data, int width, int height ){
		long start = System.currentTimeMillis();
		Result rawResult = null;
		// /start rotate 90 2013.02.04///
		if (!CameraManager.get().isLandscape()){
			byte[] rotatedData = new byte[data.length];
			for (int y = 0; y < height; y++){
				for (int x = 0; x < width; x++){
					int index1=(x * height + height - y - 1);
					int index2=(x + y * width);
					if(index1<rotatedData.length&&index2<data.length){
						rotatedData[index1] = data[index2];
					}
				}
			}
			int tmp = width; // Here we are swapping, that's the difference to #11
			width = height;
			height = tmp;
			data = rotatedData;
		}
		// //end rotate////
		PlanarYUVLuminanceSource source = CameraManager.get().buildLuminanceSource(data, width, height);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		if(bitmap!=null){
			Log.d(TAG, "decode: bitmap is exists");
		}else {
			Log.d(TAG, "decode: bitmap is null");
		}
		try{
			rawResult = multiFormatReader.decodeWithState(bitmap);
		}
		catch (ReaderException re){
			Log.d(TAG, "decode: ",re);
		}
		finally{
			multiFormatReader.reset();
		}

		if (rawResult != null){
			long end = System.currentTimeMillis();
			Log.d(TAG, "Found barcode (" + (end - start) + " ms):\n" + rawResult.toString());
			Message message = Message.obtain(scanHandlerResult.getHandler(), R.id.decode_succeeded, rawResult);
			Bundle bundle = new Bundle();
			bundle.putParcelable(DecodeThread.BARCODE_BITMAP, source.renderCroppedGreyscaleBitmap());

			message.setData(bundle);
			// Log.d(TAG, "Sending decode succeeded message...");
			message.sendToTarget();
		}
		else{
			Message message = Message.obtain(scanHandlerResult.getHandler(), R.id.decode_failed);
			message.sendToTarget();
		}
	}


}
