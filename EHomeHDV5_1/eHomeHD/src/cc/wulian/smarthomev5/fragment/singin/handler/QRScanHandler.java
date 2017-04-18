package cc.wulian.smarthomev5.fragment.singin.handler;

import java.util.Vector;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import cc.wulian.smarthomev5.fragment.singin.IQRScanHandlerResult;

import com.actionbarsherlock.app.SherlockFragment;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.library.R;
import com.google.zxing.client.android.camera.CameraManager;

public class QRScanHandler extends Handler {

	private static final String TAG = QRScanHandler.class.getSimpleName();

	private final IQRScanHandlerResult scanHandlerResult;
	private final DecodeThread decodeThread;
	private State state;

	private enum State
	{
		PREVIEW, SUCCESS, DONE
	}

	public QRScanHandler(IQRScanHandlerResult scanHandlerResult, Vector<BarcodeFormat> decodeFormats, String characterSet )
	{
		this.scanHandlerResult = scanHandlerResult;
		decodeThread = new DecodeThread(this.scanHandlerResult, decodeFormats, characterSet);
		decodeThread.start();
		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}
	@Override
	public void handleMessage( Message message ){
		int msgID = message.what;
		if (msgID == R.id.auto_focus){//聚焦
			if (state == State.PREVIEW){
				CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			}
		}
		else if (msgID == R.id.restart_preview){
			restartPreviewAndDecode();
		}
		else if (msgID == R.id.decode_succeeded){//解析成功,回送数据
			state = State.SUCCESS;
			Bundle bundle = message.getData();
			Bitmap barcode = bundle == null ? null : (Bitmap) bundle.getParcelable(DecodeThread.BARCODE_BITMAP);
			scanHandlerResult.handleDecode((Result) message.obj, barcode);
		}
		else if (msgID == R.id.decode_failed){
			// We're decoding as fast as possible, so when one decode fails, start another.
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
		}
		else if (msgID == R.id.return_scan_result){
			scanHandlerResult.setResult(Activity.RESULT_OK, (Intent) message.obj);
		}
	}

	public void quitSynchronously(){
		state = State.DONE;
		CameraManager.get().stopPreview();
		Message quit = Message.obtain(decodeThread.getHandler(), R.id.quit);
		quit.sendToTarget();
		try{
			decodeThread.join();
		}
		catch (InterruptedException e){
			// continue
		}
		// Be absolutely sure we don't send any queued up messages
		removeMessages(R.id.decode_succeeded);
		removeMessages(R.id.decode_failed);
	}

	private void restartPreviewAndDecode(){
		if (state == State.SUCCESS){
			state = State.PREVIEW;
			CameraManager.get().requestPreviewFrame(decodeThread.getHandler(), R.id.decode);
			CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			scanHandlerResult.drawViewfinder();
		}
	}
}
