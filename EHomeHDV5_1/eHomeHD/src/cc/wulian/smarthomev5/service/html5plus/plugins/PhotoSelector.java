package cc.wulian.smarthomev5.service.html5plus.plugins;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.IActivityCallerWithResult;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.service.html5plus.core.IOnActivityResultCallback;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;
import cc.wulian.smarthomev5.utils.DrawableUtil;
import cc.wulian.smarthomev5.utils.FileUtil;

public class PhotoSelector implements IOnActivityResultCallback {

	public static final int PIC_FROM＿LOCALPHOTO = 0;
	public static final int PIC_FROM_CAMERA = 1;
	private final int REQUEST_CROP_PHOTO = 2;
	private IActivityCallerWithResult caller;
	public static final String TEMP_HEAD_IMG = "temp_head.png";
	private String toPath = null;
	protected AccountManager mAccountManger = AccountManager.getAccountManger();
	private PhotoSelectCallback photoSelectallback;

	private File mSavePhotoFile;

	public PhotoSelector(IActivityCallerWithResult activity, String filepath) {
		this.caller = activity;
		activity.setOnActivityResultCallback(this);
		this.toPath = filepath;
	}
	public PhotoSelector(IActivityCallerWithResult activity, String filepath,int type) {
		this.caller = activity;
		activity.setOnActivityResultCallback(this);
		this.toPath = filepath;
		doHandlerPhoto(type);
	}


	/**
	 * 更换头像
	 * 
	 * @param v
	 */
	public void iniPopupWidow(View view) {

		final DownUpMenuList downMenu = new DownUpMenuList(
				caller.getMyContext());
		DownUpMenuItem chooseFromAlbumItem = new DownUpMenuItem(
				caller.getMyContext()) {

			@Override
			public void initSystemState() {
				mTitleTextView
						.setText(caller
								.getMyContext()
								.getResources()
								.getString(
										R.string.set_account_manager_get_picture_from_album));
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_topcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				doHandlerPhoto(PIC_FROM＿LOCALPHOTO);// 从相册中去获取
				downMenu.dismiss();
			}
		};
		// 从相册选择item
		DownUpMenuItem chooseFromCameraItem = new DownUpMenuItem(
				caller.getMyContext()) {

			@Override
			public void initSystemState() {
				mTitleTextView
						.setText(caller
								.getMyContext()
								.getResources()
								.getString(
										R.string.set_account_manager_get_picture_from_camera));
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_bottomcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				doHandlerPhoto(PIC_FROM_CAMERA);// 用户点击了从照相机获取
				downMenu.dismiss();
			}
		};
		// 拍照item
		DownUpMenuItem cancelItem = new DownUpMenuItem(caller.getMyContext()) {

			@Override
			public void initSystemState() {
				mTitleTextView.setText(caller.getMyContext().getResources()
						.getString(R.string.cancel));
				linearLayout.setPadding(0, 30, 0, 0);
				mTitleTextView
						.setBackgroundResource(R.drawable.downup_menu_item_allcircle);
				downup_menu_view.setVisibility(View.GONE);
			}

			@Override
			public void doSomething() {
				downMenu.dismiss();
			}
		};

		ArrayList<DownUpMenuItem> menuItems = new ArrayList<DownUpMenuList.DownUpMenuItem>();
		menuItems.add(chooseFromAlbumItem);
		menuItems.add(chooseFromCameraItem);
		menuItems.add(cancelItem);
		downMenu.setMenu(menuItems);
		downMenu.showBottom(view);
	}

	/**
	 * 根据不同方式选择图片设置ImageView
	 * 
	 * @param type
	 *            0-本地相册选择，非0为拍照
	 */
	private void doHandlerPhoto(int type) {
		try {
			mSavePhotoFile = new File(this.toPath);
			if (!mSavePhotoFile.getParentFile().exists()) {
				mSavePhotoFile.getParentFile().mkdirs();
			}
			if (!mSavePhotoFile.exists()) {
				mSavePhotoFile.createNewFile();
			}
			if (type == PIC_FROM＿LOCALPHOTO) {
				Intent intent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				caller.myStartActivityForResult(intent, PIC_FROM＿LOCALPHOTO);
			} else {
				Intent cameraIntent = new Intent(
						MediaStore.ACTION_IMAGE_CAPTURE);
				if (mSavePhotoFile != null) {
					cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
							Uri.fromFile(mSavePhotoFile));
					caller.myStartActivityForResult(cameraIntent,
							PIC_FROM_CAMERA);
				}
			}

		} catch (Exception e) {
			Log.i("HandlerPicError", "处理图片出现错误");
		}
	}

	public void startPhotoZoom(Uri uri, int width, int height) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪
		intent.putExtra("crop", "true");
		intent.putExtra("scale", true);// 去黑边
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", width / height);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", width);
		intent.putExtra("outputY", height);
		// 图片格式
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true);// 取消人脸识别
		intent.putExtra("return-data", true);// true:返回uri，false：不返回uri
		// 同一个地址下 裁剪的图片覆盖之前得到的图片
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mSavePhotoFile));
		caller.myStartActivityForResult(intent, REQUEST_CROP_PHOTO);
	}

	public void doWhatOnActivityResult(int requestCode, int resultCode,
			Intent data) {
		try {
			if (resultCode == Activity.RESULT_OK
					|| resultCode == Activity.RESULT_CANCELED) {
				switch (requestCode) {
				case PIC_FROM_CAMERA: // 拍照
					// 注意，如果拍照的时候设置了MediaStore.EXTRA_OUTPUT，data.getData=null
					startPhotoZoom(Uri.fromFile(mSavePhotoFile), 256, 256);
					break;
				case PIC_FROM＿LOCALPHOTO:// 选择图片
					startPhotoZoom(data.getData(), 256, 256);
					break;
				case REQUEST_CROP_PHOTO:
					Bundle extras = data.getExtras();
					if (extras != null) {
						Bitmap bitmap = extras.getParcelable("data");
						if (bitmap != null && bitmap.getByteCount() > 0) {
							Bitmap head180 = DrawableUtil.resizeImage(bitmap,
									180, 180);
							FileUtil.saveBitmapToPng(head180, this.toPath);
							if (photoSelectallback != null)
								photoSelectallback.doWhatOnSuccess(this.toPath);
						} else {
							if (photoSelectallback != null)
								photoSelectallback
										.doWhatOnFailed(new Exception(
												MainApplication
														.getApplication()
														.getString(
																R.string.html_user_hint_select_icon_fail)));
						}
					}else{
						photoSelectallback.doWhatOnSuccess(this.toPath);
					}
					break;
				}
			}
		} catch (Exception e) {
			Log.e("error", "go back when you should choose photo");
		}
	}

	public void setPhotoSelectCallback(PhotoSelectCallback callback) {
		this.photoSelectallback = callback;
	}

	public interface PhotoSelectCallback {
		public void doWhatOnSuccess(String path);

		public void doWhatOnFailed(Exception e);
	}

}
