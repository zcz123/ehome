/**
 * Project Name:  iCam
 * File Name:     AlbumPicActivity.java
 * Package Name:  com.wulian.icam.view.album
 * @Date:         2015年3月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.album;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.utils.AlbumUtils;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.utils.ImageLoader;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomViewPager;

/**
 * @ClassName: AlbumPicActivity
 * @Function: 相册左右滑动预览
 * @Date: 2015年3月27日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class AlbumPicActivity extends BaseFragmentActivity implements
		OnClickListener {
	private ImageView title_right, title_left;
	private CustomViewPager viewPager;
	private AlbumEntity albumEntity;
	private int targetPositon;
	private PagerViewAdapter albumAdapter;
	private TextView tv_title;
	private TextView tv_subTitle;
	private TextView album_count;
	private int currentPostion;// 当前显示的图片位置
	private ImageLoader mLoader;
	private List<String> allJpgFilePathList; // 当前文件夹下所有图片路径
	private AlbumUtils mAlbumUtil;
	//private View notifyView;
	//private AlertDialog dialogNotify;
	private Dialog mDeleteDialog;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_album_pic);
		initView();
		initData();
		getAllDir();
	}

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:// 加载结束
				updatePositionAndTitle(targetPositon);
				break;
			case 1:// 删除照片
				int position = (int) msg.obj;// 刚刚删除的位置
				int newSize = allJpgFilePathList.size();
				if (newSize > 0) {
					if (position == newSize)// 刚删除的是尾部照片
						updatePositionAndTitle(position - 1);//最后下标相应少一个
					else
						updatePositionAndTitle(position);
				} else {
					AlbumPicActivity.this.finish();
				}
				dismissBaseDialog();
				break;
			default:
				break;
			}
			initAdapter();
		}
	};

	private void initAdapter() {
		if (allJpgFilePathList != null && allJpgFilePathList.size() > 0) {
			if (albumAdapter == null) {
				albumAdapter = new PagerViewAdapter(this, mLoader, viewPager);
				albumAdapter.setSourceData(allJpgFilePathList);
				viewPager.setAdapter(albumAdapter);
				viewPager.setCurrentItem(targetPositon, true);
			} else {
				albumAdapter = (PagerViewAdapter) viewPager.getAdapter();
				albumAdapter.setSourceData(allJpgFilePathList);
				albumAdapter.notifyDataSetChanged();
			}
		}
	}

	private void initData() {
		viewPager.setPageMargin(this.getResources().getDimensionPixelSize(
				R.dimen.pagerview_margin));
		mAlbumUtil = new AlbumUtils(this);
		albumEntity = (AlbumEntity) getIntent().getExtras().getSerializable(
				"AlbumEntity");
		targetPositon = getIntent().getIntExtra("position", 0);
		if (albumEntity.getDeviceName() != null) {
			tv_title.setText(albumEntity.getDeviceName());
		} else {
			tv_title.setText(albumEntity.getFileName());
		}
		mLoader = new ImageLoader(this);
	}

	private void initView() {
		title_right = (ImageView) this.findViewById(R.id.titlebar_operator);
		title_left = (ImageView) this.findViewById(R.id.titlebar_back);
		viewPager = (CustomViewPager) this.findViewById(R.id.album_pic);
		tv_title = (TextView) this.findViewById(R.id.titlebar_title);
		tv_subTitle = (TextView) this.findViewById(R.id.titlebar_sub_title);
		album_count = (TextView) this.findViewById(R.id.album_count);

		title_right.setOnClickListener(this);
		title_left.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_operator) {
			showDeleteDialog();
		} else if (id == R.id.titlebar_back) {
			this.finish();
		} else {
		}
	}

	/**
	 * @MethodName: deletePic
	 * @Function: 删除当前图片
	 * @author: yuanjs
	 * @date: 2015年4月2日
	 * @email: yuanjsh@wuliangroup.cn
	 * @param position
	 */
	private void deletePic(final int position) {
		showBaseDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				// 删除缓存、文件、列表
				mLoader.deleteBitmapFromMemCache(allJpgFilePathList
						.get(position));
				mAlbumUtil.deletePicByPath(allJpgFilePathList.get(position));
				allJpgFilePathList.remove(position);
				Message msg = mHandler.obtainMessage();
				msg.what = 1;
				msg.obj = position;
				mHandler.sendMessage(msg);
			}
		}).start();
	}

	/**
	 * @MethodName: getAllDir
	 * @Function: 初始化该文件夹下所有图片路径
	 * @author: yuanjs
	 * @date: 2015年4月1日
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private void getAllDir() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (allJpgFilePathList != null) {
					allJpgFilePathList.clear();
				}
				allJpgFilePathList = mAlbumUtil.loadJpgs(albumEntity.getPath());
				mHandler.sendEmptyMessage(0);
			}
		}).start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mLoader.cancelTask();
		mLoader.deletAllBitmapFromMemCache();
	}

	public void updatePositionAndTitle(int positon) {
		int size = allJpgFilePathList.size();
		long time = new File(allJpgFilePathList.get(positon)).lastModified();
		String timeStr = new SimpleDateFormat("yyyy-MM-dd HH:mm",
				Locale.ENGLISH).format(new Date(time));
		tv_subTitle.setText(timeStr);
		album_count.setText(positon + 1 + "/" + size);
		currentPostion = positon;
	}

	private void showDeleteDialog() {
		Resources rs = getResources();
		mDeleteDialog = DialogUtils.showCommonDialog(this, true,
				null,
				rs.getString(R.string.album_delete_this_photo_confirm),
				null,
				null, new OnClickListener() {

					@Override
					public void onClick(View v) {
						int id = v.getId();
						if (id == R.id.btn_positive) {
							mDeleteDialog.dismiss();
							deletePic(currentPostion);
						} else if (id == R.id.btn_negative) {
							mDeleteDialog.dismiss();
						} else {
						}
					}
				});
		
		
		
		
//		if (dialogNotify == null) {
//			dialogNotify = new AlertDialog.Builder(this, R.style.alertDialog)
//					.create();
//		}
//		notifyView = LinearLayout.inflate(this,
//				R.layout.custom_alertdialog_notice,
//				(ViewGroup) findViewById(R.id.ll_custom_alertdialog));
//		TextView notify = (TextView) notifyView.findViewById(R.id.tv_info);
//		notify.setText(this.getResources().getString(R.string.album_delete_this_photo_confirm));
//		((Button) notifyView.findViewById(R.id.btn_positive))
//				.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						deletePic(currentPostion);
//						dismissDailog();
//					}
//				});
//		((Button) notifyView.findViewById(R.id.btn_negative))
//				.setOnClickListener(new OnClickListener() {
//					@Override
//					public void onClick(View v) {
//						dismissDailog();
//					}
//				});
//		if (!dialogNotify.isShowing()) {
//			dialogNotify.show();
//			dialogNotify.setContentView(notifyView);
//		}

	}

//	private void dismissDailog() {
//		if (dialogNotify != null && dialogNotify.isShowing()) {
//			dialogNotify.dismiss();
//			dialogNotify = null;
//		}
//	}
}
