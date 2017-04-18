/**
 * Project Name:  iCam
 * File Name:     AlbumFragment.java
 * Package Name:  com.wulian.icam.view.main
 * @Date:         2015年6月9日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.album;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.AlbumEntity;
import com.wulian.icam.model.Device;
import com.wulian.icam.utils.AlbumUtils;
import com.wulian.icam.utils.ImageLoader;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.widget.PullListView;
import com.wulian.icam.view.widget.PullListView.OnRefreshListener;

/**
 * @ClassName: AlbumFragment
 * @Function: 相册
 * @Date: 2015年6月9日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AlbumFragment extends Fragment {
	View fragmentView;
	private PullListView albumListView;
	private TextView tv_album_empty; // 无相册时的视图
	private List<AlbumEntity> albumList = new ArrayList<AlbumEntity>(); // 相册集
	private AlbumAdapter adapter; // 相册适配器

	private ImageLoader loader; // 图片加载器
	int totalCount = 0;// 图片数目总和
	private AlbumUtils mAlbumUtil;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			initAdapter();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader = new ImageLoader(getActivity());
		mAlbumUtil = new AlbumUtils(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		fragmentView = inflater.inflate(R.layout.fragment_album, container,
				false);
		initView(fragmentView);
		return fragmentView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		// Fragment Touch 防止 事件泄露
		// 参考：http://www.cnblogs.com/qixing/p/4022850.html
		view.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onResume() {
		Utils.sysoInfo("onResume AlbumFragment");
		super.onResume();
		initAlbumList();// 刷新最新的数据
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
			return;
		}

		new Thread(new Runnable() {
			@Override
			public void run() {
				if (albumList != null) {
					// 1、清空旧的数据
					albumList.clear();
				}
				ArrayList<Device> devices = ICamGlobal.getInstance()
						.getDeviceList();
				if (devices != null && devices.size() > 0) {// 确保已经加载了类表
					// 2、不是add、remove，而是新内存空间
					albumList = mAlbumUtil.getAlbums(Environment
							.getExternalStorageDirectory().getAbsolutePath()
							+ APPConfig.ALBUM_DIR);
					removeOthers();
					updateNickNameForAlbum();
					mHandler.sendEmptyMessage(0);
				}
			}
		}).start();
	}

	private void updateNickNameForAlbum() {
		for (int i = 0, albumSize = albumList.size(); i < albumSize; i++) {// 遍历相册,优化相册名
			updateNickName(albumList.get(i));
		}
	}

	private void updateNickName(AlbumEntity albumEntity) {
		ArrayList<Device> devices = ICamGlobal.getInstance()
				.getDeviceList();
		if (devices != null) {
			for (Device device : devices) {
				if (device.getDevice_id().equalsIgnoreCase(
						albumEntity.getFileName())) {
					albumEntity.setDeviceName(device.getDevice_nick());
					break;
				}
			}
		}
	}

	/**
	 * @Function 移除不在设备列表的相册
	 * @author Wangjj
	 * @date 2015年6月16日
	 */

	public void removeOthers() {
		ArrayList<Device> devices = ICamGlobal.getInstance()
				.getDeviceList();
		if (devices != null && devices.size() > 0) {
			List<AlbumEntity> albumListOther = new ArrayList<AlbumEntity>();
			for (AlbumEntity ae : albumList) {
				if (!isInDevielist(devices, ae.getFileName())) {
					albumListOther.add(ae);
				}
			}
			albumList.removeAll(albumListOther);
		}
	}

	private boolean isInDevielist(List<Device> devices, String dirName) {
		for (Device device : devices) {
			if (device.getDevice_id().equalsIgnoreCase(dirName)) {
				return true;
			}
		}
		return false;
	}

	private void initAdapter() {
		albumListView.onRefreshComplete();
		albumListView.updateRefreshTime();
		if (albumList.size() == 0) {
			// albumListView.setEmptyView(tv_album_empty);// 无法下拉刷新
			tv_album_empty.setVisibility(View.VISIBLE);
		} else {
			tv_album_empty.setVisibility(View.GONE);
		}
		if (adapter == null) {
			adapter = new AlbumAdapter(albumListView, getActivity(), loader);
			adapter.setSourceData(albumList);
			albumListView.setAdapter(adapter);
		} else {
			// 步骤：1、清空旧数据->2、获取新数据->3.设置新数据->4、刷新
			adapter.setSourceData(albumList);// 3、不是add、remove，刷新前要更新为新空间
			adapter.notifyDataSetChanged();// 4、刷新
			// 有个错误没有捕捉到:在子线程通知adapter更新
		}
	}

	/**
	 * @Function 逻辑变更:列表数据加载后更新列表名称（完善性操作） ==》只能显示设备列表中的相册，即列表加载后才能初始化相册（决定性操作）
	 * @author Wangjj
	 * @date 2015年6月16日
	 */

	public void onDeviceDataLoad() {
		// if (albumList.size() > 0 && adapter != null) {
		// updateNickNameForAlbum();
		// adapter.notifyDataSetChanged();
		// }

		initAlbumList();// 刷新最新的数据
	}

	private void initView(View view) {

		tv_album_empty = (TextView) view.findViewById(R.id.tv_album_empty);
		albumListView = (PullListView) view.findViewById(R.id.lv_more_album);
		albumListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				initAlbumList();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (adapter != null) {
			adapter.cancelTask();
		}
	}
}
