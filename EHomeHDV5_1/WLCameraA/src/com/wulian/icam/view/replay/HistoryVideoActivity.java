/**
 * Project Name:  iCam
 * File Name:     VideoHistoryActivity.java
 * Package Name:  com.wulian.icam.view.replay
 * @Date:         2015年5月27日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.view.replay;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.wulian.icam.R;
import com.wulian.icam.h264decoder.SocketErrorCode;
import com.wulian.icam.h264decoder.SocketMsgApiType;
import com.wulian.icam.h264decoder.socket.DecoderSocketClient;
import com.wulian.icam.h264decoder.socket.DecoderSocketClient.Listener;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.MediaItem;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.base.BaseFragmentActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.PullListView;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.api.SipMsgApiType;

/**
 * @ClassName: HistoryVideoActivity
 * @Function: 视频回看历史
 * @Date: 2015年5月27日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class HistoryVideoActivity extends BaseFragmentActivity implements
		OnClickListener {
	private PullListView lv_history_video;
	private ImageView titlebar_back, titlebar_right;
	private HistoryVideoAdapter videoAdapter;
	private List<MediaItem> videoList;
	private DecoderSocketClient mClient;
	private Device device;
	String sipCallWithDomain;// xxx@wuliangruop.cn
	int seq = 1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history_video);
		onSendSipRemoteAccess();
		initViews();
		initListeners();
		initData();
		initWebData();
	}

	private void initViews() {
		lv_history_video = (PullListView) findViewById(R.id.lv_history_video);
		titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
		titlebar_right = (ImageView) findViewById(R.id.titlebar_right);

	}

	private void initListeners() {
		titlebar_back.setOnClickListener(this);
		titlebar_right.setOnClickListener(this);
	}

	private void initData() {
		videoList = new ArrayList<MediaItem>();
		MediaItem mi = new MediaItem();
		mi.setCreateTime("2015-5-2");
		mi.setMeidaType(MediaItem.TYPE_HEAD);
		videoList.add(mi);
		mi = new MediaItem();
		mi.setCreateTime("2015-5-21");
		mi.setMeidaType(MediaItem.TYPE_VIDEO);
		videoList.add(mi);
		mi = new MediaItem();
		mi.setCreateTime("2015-5-22");
		mi.setMeidaType(MediaItem.TYPE_VIDEO);
		videoList.add(mi);
		mi = new MediaItem();
		mi.setCreateTime("2015-5-23");
		mi.setMeidaType(MediaItem.TYPE_VIDEO);
		videoList.add(mi);
		videoAdapter = new HistoryVideoAdapter(this, videoList);
		lv_history_video.setAdapter(videoAdapter);
		device = (Device) getIntent().getSerializableExtra("device");
		sipCallWithDomain = device.getDevice_id() + "@"
				+ device.getSip_domain();
	}

	private void initWebData() {
		// 122 查询存储状态
		SipController.getInstance().sendMessage(
				sipCallWithDomain,
				SipHandler
						.QueryStorageStatus("sip:" + sipCallWithDomain, seq++),
				app.registerAccount());
	}

	@Override
	protected void SipDataReturn(boolean isSuccess, SipMsgApiType apiType,
			String xmlData, String from, String to) {
		super.SipDataReturn(isSuccess, apiType, xmlData, from, to);
		if (isSuccess) {
			dismissBaseDialog();
			switch (apiType) {
			case QUERY_STORAGE_STATUS:// 122 查询存储状态
				Utils.sysoInfo("查询存储状态:" + xmlData);
				// <storage num="1" type="SD" status="1", attr="rw"
				// totalsize="1000K" freesize="0K"></storage>
				Pattern pstatus = Pattern
						.compile("<storage.*status=\"(\\d)\"\\s+.*/?>(</storage>)?");
				Matcher matchers = pstatus.matcher(xmlData);
				if (matchers.find()) {
					String status = matchers.group(1).trim();
					if ("0".equals(status)) {
						CustomToast.show(this, R.string.replay_disk_no_format);
					} else if ("1".equals(status)) {
						CustomToast.show(this, R.string.replay_disk_no_found);
					} else if ("2".equals(status)) {
						if (!device.getIs_lan()) {
							CustomToast.show(this, R.string.play_temp_support_lan);
						} else {
							if (TextUtils.isEmpty(device.getIp())) {
								CustomToast.show(this,
										R.string.play_temp_support_lan);
								return;
							}
							String remoteIp = "https://" + device.getIp();
							mClient = new DecoderSocketClient(
									URI.create(remoteIp), new Listener() {

										@Override
										public void onMessage(
												SocketMsgApiType api,
												String message) {

										}

										@Override
										public void onH264StreamMessage(
												byte[] data) {

										}

										@Override
										public void onFileMessage(byte[] data) {

										}

										@Override
										public void onError(
												SocketErrorCode error) {

										}

										@Override
										public void onDisconnect(
												SocketErrorCode errorcode) {

										}

										@Override
										public void onConnect() {

										}
									}, null, null);
							mClient.connect();
						}
					}
				}
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.titlebar_back) {
			this.finish();
		} else if (id == R.id.titlebar_right) {
		} 
	}
}
