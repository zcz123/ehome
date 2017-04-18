/**
 * Project Name:  iCam
 * File Name:     AlbumActivity.java
 * Package Name:  com.wulian.icam.view.setting
 * @Date:         2015年3月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.album;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.AlbumUtils;
import com.wulian.icam.utils.ImageLoader;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.PullListView;
import com.wulian.icam.view.widget.PullListView.OnRefreshListener;

/**
 * @ClassName: AlbumActivity
 * @Function: 相册
 * @Date: 2015年3月27日
 * @author: yuanjs
 * @email: yuanjsh@wuliangroup.cn
 */
public class AlbumActivity extends BaseFragmentActivity implements
		OnClickListener {
	private ImageView title_left;
	private PullListView albumListView;
	private TextView tv_album_empty; // 无相册时的视图
	private List<AlbumEntity> albumList; // 相册集
	private AlbumAdapter adapter; // 相册适配器
	private ProgressDialog progressDialog;
	private DeleteAllPicTask deleteAllPicTask; // 删除相册任务
	private Dialog dialog; // 仿iphone弹出窗体
	private View dialogContentView; // 友好对话框
	private View contentView; // 对话框视图
	private ImageLoader loader; // 图片加载器
	int totalCount = 0;// 图片数目总和
	/**
	 * 临时的辅助类，用于防止同一个文件夹的多次扫描
	 */
	private AlbumUtils mAlbumUtil;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			// disMissProDialog();
			initAdapter();
		}
	};

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		initView();
		initListener();
		albumList = new ArrayList<AlbumEntity>();
	}
	
	@Override
	protected void setViewContent() {
		setContentView(R.layout.activity_album);
	}
	
	@Override
	protected String getActivityTitle() {
		return getResources().getString(R.string.album);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// CustomToast.show(this, "加载相册");
		initData();
	}

	private void initData() {
		loader = new ImageLoader(this);
		mAlbumUtil = new AlbumUtils(this);
		initAlbumList();
	}

	/**
	 * @MethodName: initAlbumList
	 * @Function: 加载数据
	 * @author: yuanjs
	 * @date: 2015年4月2日
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private void initAlbumList() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			CustomToast.show(this, "暂无外部存储...");
			return;
		}
		// showProDialog();
		new Thread(new Runnable() {
			@Override
			public void run() {
				if (albumList != null) {
					albumList.clear();
					List<AlbumEntity> albumListcache = mAlbumUtil
							.getAlbums(Environment
									.getExternalStorageDirectory()
									.getAbsolutePath()
									+ APPConfig.ALBUM_DIR);
					ArrayList<Device> deviceList = app.getDeviceList();
					List<String> cacheList = new LinkedList<String>();
					int size = deviceList.size();
					int albumSize = albumListcache.size();
					if (albumSize > 0 && size > 0) {
						for (int i = 0; i < albumSize; i++) {
							for (int j = 0; j < size; j++) {
								if (cacheList.contains(deviceList.get(j)
										.getDevice_id())) {
									continue;
								}
								if (albumListcache
										.get(i)
										.getFileName()
										.equalsIgnoreCase(
												deviceList.get(j)
														.getDevice_id())) {
									cacheList.add(deviceList.get(j)
											.getDevice_id());
									albumListcache.get(i).setDeviceName(
											deviceList.get(j).getDevice_nick());
									albumList.add(albumListcache.get(i));
								}
							}
						}
						cacheList = null;
						deviceList = null;
						albumListcache = null;
					}
					// 通知Handler扫描图片完成
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	private void initAdapter() {
		albumListView.onRefreshComplete();
		albumListView.updateRefreshTime();
		if (albumList.size() == 0) {
			albumListView.setEmptyView(tv_album_empty);
		} else if (adapter == null) {
			adapter = new AlbumAdapter(albumListView, this, loader);
			adapter.setSourceData(albumList);
			albumListView.setAdapter(adapter);
		} else {
			// adapter = (AlbumAdapter) albumListView.getAdapter();
			// adapter.setSourceData(albumList);
			// adapter.notifyDataSetChanged();
			// 步骤：1、清空旧数据->2、获取新数据->3.设置新数据->4、刷新
			adapter.setSourceData(albumList);// 3、不是add、remove，刷新前要更新为新空间
			adapter.notifyDataSetChanged();// 4、刷新
			// 有个错误没有捕捉到:在子线程通知adapter更新
		}
	}

	private void initListener() {
		title_left.setOnClickListener(this);
	}

	private void initView() {
		title_left = (ImageView) this.findViewById(R.id.titlebar_back);
		albumListView = (PullListView) this.findViewById(R.id.lv_more_album);
		tv_album_empty = (TextView) this.findViewById(R.id.tv_album_empty);
		albumListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				initAlbumList();
			}
		});
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		// if (id == R.id.titlebar_operator) {
		// if (albumList != null && albumList.size() > 0)
		// showDialog();
		// } else
		if (id == R.id.titlebar_back) {
			this.finish();
		} else {
		}
	}

	/**
	 * @MethodName: deleteAllPic
	 * @Function: 删除所有图片对话框
	 * @author: yuanjs
	 * @date: 2015年3月27日
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private void showDialog() {
		if (dialog == null) {
			dialog = new AlertDialog.Builder(this, R.style.alertDialogIosAlert)
					.create();
		}

		if (dialogContentView == null) {
			dialogContentView = LinearLayout.inflate(this,
					R.layout.custom_alertdialog_notice_ios,// 默认就是注销警告
					(ViewGroup) this.findViewById(R.id.ll_custom_alertdialog));
			((TextView) dialogContentView.findViewById(R.id.tv_info))
					.setText("您真的要删除所有图片吗？");
			(dialogContentView.findViewById(R.id.btn_positive))
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							// DeleteAllPic(albumList);
							// deleteAllPicTask = new DeleteAllPicTask();
							// deleteAllPicTask.execute(filepath);
							dissMissDialog();
						}
					});
			(dialogContentView.findViewById(R.id.btn_negative))
					.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							dissMissDialog();
						}
					});
		}

		Utils.updateDialog2BottomDefault(dialog);
		dialog.show();
		Utils.updateDialogWidth2ScreenWidthDefault(this, dialog);
		dialog.setContentView(dialogContentView);
	}

	/**
	 * @MethodName: dissMissDialog
	 * @Function: 消除dialog
	 * @author: yuanjs
	 * @date: 2015年3月28日
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private void dissMissDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	/**
	 * @ClassName: DeleteAllPicTask
	 * @Function: 清空相册
	 * @date: 2015年3月28日
	 * @author: yuanjs
	 * @email: yuanjsh@wuliangroup.cn
	 */
	private class DeleteAllPicTask extends AsyncTask<String, Void, Void> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showProDialog();
		}

		@Override
		protected Void doInBackground(String... params) {
			String filepath = params[0];
			// loader.deletAllBitmapFromMemCache(albumList);
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			disMissProDialog();
			// albumList.clear();
			// initAdapter();
			super.onPostExecute(result);
		}
	}

	private void showProDialog() {
		if (progressDialog == null || contentView == null) {
			progressDialog = new ProgressDialog(AlbumActivity.this,
					R.style.dialog);
			contentView = getLayoutInflater().inflate(
					R.layout.custom_progress_dialog,
					(ViewGroup) findViewById(R.id.custom_progressdialog));
			((TextView) contentView.findViewById(R.id.tv_desc))
					.setText(getResources().getText(
							R.string.common_in_processing));
		}
		if (!progressDialog.isShowing()) {
			progressDialog.show();
			progressDialog.setContentView(contentView);
		}
	}

	private void disMissProDialog() {
		if (progressDialog != null && progressDialog.isShowing()) {
			progressDialog.dismiss();
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if (adapter != null) {
			adapter.cancelTask();
		}
	}
}