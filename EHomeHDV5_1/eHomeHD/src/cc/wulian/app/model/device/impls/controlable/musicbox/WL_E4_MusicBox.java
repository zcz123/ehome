package cc.wulian.app.model.device.impls.controlable.musicbox;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.MusicDao;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_MUSIC_BOX }, category = Category.C_OTHER)
public class WL_E4_MusicBox extends ControlableDeviceImpl {

	private static final String DATA_CTRL_STATE_OPEN_20 = "20";
	private static final String DATA_CTRL_STATE_CLOSE_21 = "21";

	// 2Y：表示播放状态，2为标记，Y:0 播放 1 暂停 2 停止
	public static final String DATA_CTRL_SET_PLAY_20 = "20";
	public static final String DATA_CTRL_SET_PAUSE_21 = "21";
	public static final String DATA_CTRL_SET_STOP_22 = "22";

	// 3Y：表示上下曲切换，3为标记，Y:0 上一曲 1 下一曲
	public static final String DATA_CTRL_SET_PREVIOUS_30 = "30";
	public static final String DATA_CTRL_SET_NEXT_31 = "31";

	// 4YYY：表示音量调整，4为标记，YYY：音量 0~100
	public static final String DATA_CTRL_SET_VOLUM_4 = "4";

	// 5Y：表示静音功能，5为标记，Y：0 取消静音 1 静音
	// public static final String DATA_CTRL_SET_MUTE_51 = "51";
	// public static final String DATA_CTRL_SET_CANCE_LMUTE_50 = "50";

	// 6：表示获取当前状态
	public static final String DATA_CTRL_GET_STATUS_6 = "6";

	// 7：表示当前歌曲下载
	public static final String DATA_CTRL_SET_DOWNLOAD_7 = "7";

	// 8Y：表示循环模式切换，8为标记，Y：1 列表循环 2 随机循环 3 单曲播放
	public static final String DATA_CTRL_SET_LIST_MODEL_81 = "81";
	public static final String DATA_CTRL_SET_RANDOM_MODEL_82 = "82";
	public static final String DATA_CTRL_SET_ONE_MODEL_83 = "83";

	// 9Y：表示模式切换，9为标记，Y：1 AP模式 2 联网模式 3 WPS模式 4 SD卡模式
	public static final String DATA_CTRL_SET_AP_MODEL_91 = "91";
	public static final String DATA_CTRL_SET_STA_MODEL_92 = "92";
	// public static final String DATA_CTRL_SET_WPS_MODEL_93 = "93";
	public static final String DATA_CTRL_SET_SD_MODEL_94 = "94";

	private MusicBoxRecordsAdapter mMusicBoxRecordsAdapter;
	// 用于统计点击播放模式次数的变量,原始代码,主要实现的是本地切换模式方式
	private int count = 0;

	private Button modelSDButton;
	private Button modelAPButton;
	private Button modelSTAButton;

	private ListView musicListView;
	private ImageButton mPrevious;
	private ImageButton mStart;
	private ImageButton mPause;
	private ImageButton mNext;
	private ImageView mRepeatModel;
	private SeekBar mSeekBar;
	private ImageView mMute;
	private ImageView mdownload;
	// 当前音量
	private String cc_c;
	// 当前模式
	private String bb;
	// 当前歌曲
	private String hhhh;

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {

			if (arg0 == modelSDButton) {
				controlDevice(ep, epType, DATA_CTRL_SET_SD_MODEL_94);

			} else if (arg0 == modelAPButton) {
				controlDevice(ep, epType, DATA_CTRL_SET_AP_MODEL_91);

			} else if (arg0 == modelSTAButton) {
				controlDevice(ep, epType, DATA_CTRL_SET_STA_MODEL_92);

			} else if (arg0 == mPrevious) {
				controlDevice(ep, epType, DATA_CTRL_SET_PREVIOUS_30);

			} else if (arg0 == mStart) {
				controlDevice(ep, epType, DATA_CTRL_SET_PLAY_20);

			} else if (arg0 == mPause) {
				controlDevice(ep, epType, DATA_CTRL_SET_PAUSE_21);

			} else if (arg0 == mNext) {
				controlDevice(ep, epType, DATA_CTRL_SET_NEXT_31);

			} else if (arg0 == mRepeatModel) {
				count++;
				repeatHelper();

			} else if (arg0 == mMute) {
				count++;

			} else if (arg0 == mdownload) {
				controlDevice(ep, epType, DATA_CTRL_SET_DOWNLOAD_7);

			}
		}
	};
	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			int i = arg0.getProgress();
			String str = String.valueOf(i);
			String YYY = null;
			if (str.length() == 1) {
				YYY = "00" + str;
			} else if (str.length() == 2) {
				YYY = "0" + str;
			} else if (str.length() == 3) {
				YYY = "100";
			}
			controlDevice(ep, epType, DATA_CTRL_SET_VOLUM_4 + YYY);

		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			if (arg2) {

			}
		}
	};

	public WL_E4_MusicBox(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		return getOpenSendCmd();
	}
	@Override
	public String getStopProtocol() {
		return getStopSendCmd();
	}
	@Override
	public String getCloseProtocol() {
		return getCloseSendCmd();
	}
	
	@Override
	public String getStopSendCmd() {
		return DATA_CTRL_SET_STOP_22;
	}
	
	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_20;
	}

	/**
	 * 存在bug,正确命令为本地模式时发送22的停止命令,在联网和直连模式下发送21的暂停命令
	 */
	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_21;
	}

	@Override
	public boolean isOpened() {
		return !isClosed();
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData)) {

			return true;
		}

		return (epData.startsWith("06") && epData.startsWith("00", 6))
				|| (epData.startsWith("06") && epData.startsWith("02", 6))
				|| epData.startsWith("0201")// 02为标记，xx：00 播放 01 暂停 02 停止
				|| epData.startsWith("0202");

	}

	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(DeviceMusicBoxFragment.GWID, gwID);
		intent.putExtra(DeviceMusicBoxFragment.DEVICEID, devID);
		intent.putExtra(DeviceMusicBoxFragment.DEVICE_MUSIC_BOX, type);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				DeviceMusicBoxFragment.class.getName());
		return intent;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	

	// 播放模式循环切换,count是做本地轮换的,未根据网关返值做变换,存在问题
	private void repeatHelper() {

		if ((count % 3) == 0) {
			mRepeatModel.setImageDrawable(getResources().getDrawable(
					R.drawable.device_musicbox_list));
			controlDevice(ep, epType, DATA_CTRL_SET_LIST_MODEL_81);

		} else if ((count % 3) == 1) {
			controlDevice(ep, epType, DATA_CTRL_SET_RANDOM_MODEL_82);
			mRepeatModel.setImageDrawable(getResources().getDrawable(
					R.drawable.device_musicbox_random));

		} else if ((count % 3) == 2) {
			controlDevice(ep, epType, DATA_CTRL_SET_ONE_MODEL_83);
			mRepeatModel.setImageDrawable(getResources().getDrawable(
					R.drawable.device_musicbox_one));

		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_music_box, null);

	}

	@SuppressLint("NewApi")
	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		controlDevice(ep, epType, DATA_CTRL_GET_STATUS_6);

		modelSDButton = (Button) view
				.findViewById(R.id.device_musicbox_model_big_sd);
		modelSDButton.setOnClickListener(mClickListener);
		modelAPButton = (Button) view
				.findViewById(R.id.device_musicbox_model_big_ap);
		modelAPButton.setOnClickListener(mClickListener);
		modelSTAButton = (Button) view
				.findViewById(R.id.device_musicbox_model_big_sta);
		modelSTAButton.setOnClickListener(mClickListener);
		mPrevious = (ImageButton) view
				.findViewById(R.id.device_musicbox_previous);
		mPrevious.setOnClickListener(mClickListener);
		mStart = (ImageButton) view.findViewById(R.id.device_musicbox_start);
		mStart.setOnClickListener(mClickListener);
		mPause = (ImageButton) view.findViewById(R.id.device_musicbox_pause);
		mPause.setOnClickListener(mClickListener);
		mNext = (ImageButton) view.findViewById(R.id.device_musicbox_next);
		mNext.setOnClickListener(mClickListener);
		mRepeatModel = (ImageView) view
				.findViewById(R.id.device_musicbox_repeat_model);
		mRepeatModel.setOnClickListener(mClickListener);
		mSeekBar = (SeekBar) view
				.findViewById(R.id.device_musicbox_ajust_volume_seekbar);
		mSeekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		musicListView = (ListView) view
				.findViewById(R.id.device_musicbox_song_list);
		mMusicBoxRecordsAdapter = new MusicBoxRecordsAdapter(mContext);
		musicListView.setAdapter(mMusicBoxRecordsAdapter);
		if (mMusicBoxRecordsAdapter.getCount() == 0) {
			musicListView.setBackground(null);

		}
		mMute = (ImageView) view.findViewById(R.id.device_musicbox_mute_view);
		mMute.setOnClickListener(mClickListener);
		mdownload = (ImageView) view
				.findViewById(R.id.device_musicbox_download);
		mdownload.setOnClickListener(mClickListener);

	}

	// 解析协议
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (StringUtil.isNullOrEmpty(epData)) {
			return;

		}
		// 当前歌曲数
		// 01xxxx：表示选取播放，01为标记，xxxx表示要选的曲目数
		if (epData.startsWith("01")) {

		}
		// 02xx：表示播放状态，02为标记，xx：00 播放 01 暂停 02 停止
		else if (epData.startsWith("02") && epData.length() >= 4) {
			if (epData.substring(2, 4).equals("00")) {

			}
			if (epData.substring(2, 4).equals("01")) {

			}
			if (epData.substring(2, 4).equals("02")) {

			}
		}
		// 03xx：表示上下曲切换，03为标记，xx：00 上一曲 01 下一曲
		else if (epData.startsWith("03") && epData.length() >= 4) {
			if (epData.substring(2, 4).equals("00")) {

			}
			if (epData.substring(2, 4).equals("01")) {

			}
		}
		// 04xx：表示音量调整，04为标记，xx：0x00~0x64
		else if (epData.startsWith("04") && epData.length() >= 4) {
			cc_c = epData.substring(2, 4);
			int i = StringUtil.toInteger(cc_c, 16);
			if (i <= 100) {
				mSeekBar.setProgress(i);

			}
			if (i != 0)
				mMute.setSelected(false);

		}
		// 05xx：表示静音功能，05为标记，xx：00 取消静音 01 静音
		else if (epData.startsWith("05") && epData.length() >= 4) {
			if (epData.substring(2, 4).equals("00")) {

			}
			if (epData.substring(2, 4).equals("01")) {
				mMute.setSelected(true);

			}
		}
		// 06bbcceehhhhiiiikkllmmoo
		else if (epData.startsWith("06")) {
			if (epData.length() < 24)
				return;

			bb = epData.substring(2, 4);

			cc_c = epData.substring(4, 6);

			int progress = StringUtil.toInteger(cc_c, 16);
			if ((progress > 0) && (progress <= 100)) {
				mSeekBar.setProgress(progress);

			} else if (progress == 0) {
				mSeekBar.setProgress(0);

			}

			String ee = epData.substring(6, 8);

			// iiii：歌曲总数
			// String iiii = epData.substring(12, 16);
			String kk = epData.substring(16, 18);
			String ll = epData.substring(18, 20);
			String mm = epData.substring(20, 22);
			String oo = epData.substring(22, 24);

			if (ll.equals("00")) {

			} else if (ll.equals("01")) {

			} else if (ll.equals("02")) {
				mdownload.setVisibility(View.VISIBLE);

			} else if (ll.equals("03")) {
				mdownload.setVisibility(View.INVISIBLE);

			}

			if (mm.equals("00")) {
				if (bb != null && (bb.equals("11") || bb.equals("12")))
					mdownload.setImageDrawable(getResources().getDrawable(
							R.drawable.device_musicbox_download_normal));

			} else if (mm.equals("01")) {
				if (bb != null && (bb.equals("11") || bb.equals("12")))
					mdownload.setImageDrawable(getResources().getDrawable(
							R.drawable.device_musicbox_download_pressed));

			} else if (mm.equals("02")) {
				if (bb != null && (bb.equals("11") || bb.equals("12")))
					mdownload.setImageDrawable(getResources().getDrawable(
							R.drawable.device_musicbox_download_normal));
				Toast.makeText(mContext, R.string.device_musicbox_complete,
						Toast.LENGTH_SHORT).show();

			} else if (mm.equals("ff")) {
				if (bb != null && (bb.equals("11") || bb.equals("12")))
					Toast.makeText(mContext, R.string.device_musicbox_fail,
							Toast.LENGTH_SHORT).show();
				mdownload.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_download_normal));

			}

			// bb
			if (bb.equals("00")) {
				Toast.makeText(mContext, "No network", Toast.LENGTH_SHORT)
						.show();
				return;

			} else if (bb.equals("01")) {

			} else if (bb.equals("02")) {

			} else if (bb.equals("11")) {
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_pressed);
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_normal);

				mNext.setVisibility(View.INVISIBLE);
				mPrevious.setVisibility(View.INVISIBLE);
				mRepeatModel.setVisibility(View.INVISIBLE);
				mRepeatModel.setVisibility(View.GONE);
				musicListView.setVisibility(View.INVISIBLE);
				mdownload.setVisibility(View.VISIBLE);

			} else if (bb.equals("12")) {
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_normal);
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_pressed);

				mNext.setVisibility(View.INVISIBLE);
				mPrevious.setVisibility(View.INVISIBLE);
				mRepeatModel.setVisibility(View.INVISIBLE);
				mRepeatModel.setVisibility(View.GONE);
				musicListView.setVisibility(View.INVISIBLE);
				mdownload.setVisibility(View.VISIBLE);

			} else if (bb.equals("FF")) {
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_pressed);
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_normal);

				mNext.setVisibility(View.VISIBLE);
				mPrevious.setVisibility(View.VISIBLE);
				mRepeatModel.setVisibility(View.VISIBLE);
				musicListView.setVisibility(View.VISIBLE);
				mdownload.setVisibility(View.INVISIBLE);
				mdownload.setVisibility(View.GONE);

				hhhh = epData.substring(8, 12);
				mMusicBoxRecordsAdapter.setCurrentItemId(hhhh);
				int idRequest = StringUtil.toInteger(hhhh, 16);
				String strRequest = StringUtil.toDecimalString(idRequest, 4);

				if (ee.equals("01")) { // 在歌曲播放状态情况下
					int checkIndex = mMusicBoxRecordsAdapter
							.checkSongExist(hhhh);
					mMusicBoxRecordsAdapter.notifyDataSetChanged();
					musicListView.setSelection(checkIndex);
					if (checkIndex == -1) { // 歌曲不存在请求歌曲
						controlDevice(ep, epType, "F" + strRequest);

					} else if (oo.equals("01")) { // 有更新
						controlDevice(ep, epType, "F" + strRequest);

					}
				}

			}
			// ee：播放状态（0x00 停止 0x01 播放 0x02 暂停）
			if (ee.equals("00")) {

			} else if (ee.equals("01")) {

			} else if (ee.equals("02")) {

			}

			// kk：循环模式（0x00 列表循环 0x01 随机循环 0x02 单曲循环）；
			if (kk.equals("00")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_list));

			} else if (kk.equals("01")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_random));

			} else if (kk.equals("02")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_one));

			}

		}

		// 07：表示当前歌曲下载
		else if (epData.startsWith("07")) {
			mdownload.setImageDrawable(getResources().getDrawable(
					R.drawable.device_musicbox_download_pressed));

		}
		// 08xx：表示循环模式切换，08为标记，xx：01 列表循环 02 随机循环 03 单曲播放
		else if (epData.startsWith("08") && epData.length() >= 4) {

			if (epData.substring(2, 4).equals("01")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_list));

			}
			if (epData.substring(2, 4).equals("02")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_random));

			}
			if (epData.substring(2, 4).equals("03")) {
				mRepeatModel.setImageDrawable(getResources().getDrawable(
						R.drawable.device_musicbox_one));

			}
		}
		// 表示模式切换，09为标记，xx：01 AP模式 02 联网模式 0 3 WPS模式 04 SD卡模式
		else if (epData.startsWith("09") && epData.length() >= 4) {
			if (epData.substring(2, 4).equals("01")) {
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_pressed);
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_normal);

				modelSDButton.setEnabled(true);
				modelSTAButton.setEnabled(true);

			} else if (epData.substring(2, 4).equals("02")) {
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_normal);
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_pressed);

				mdownload.setVisibility(View.GONE);
				modelSDButton.setEnabled(true);
				modelAPButton.setEnabled(true);

			} else if (epData.substring(2, 4).equals("04")) {
				modelSDButton
						.setBackgroundResource(R.drawable.device_musicbox_sd_pressed);
				modelAPButton
						.setBackgroundResource(R.drawable.device_musicbox_ap_normal);
				modelSTAButton
						.setBackgroundResource(R.drawable.device_musicbox_sta_normal);

				mdownload.setVisibility(View.GONE);
				modelAPButton.setEnabled(true);
				modelSTAButton.setEnabled(true);

			}
		} else if (epData.startsWith("0B")) {
			DeviceMusicBoxFragment.ob = true;
			if (DeviceMusicBoxFragment.ob && DeviceMusicBoxFragment.oc) {
				Toast.makeText(mContext, R.string.device_E4_change_success,
						Toast.LENGTH_SHORT).show();
				DeviceMusicBoxFragment.ob = false;

			}

		} else if (epData.startsWith("0C")) {
			DeviceMusicBoxFragment.oc = true;
			if (DeviceMusicBoxFragment.ob && DeviceMusicBoxFragment.oc) {
				Toast.makeText(mContext, R.string.device_E4_change_success,
						Toast.LENGTH_SHORT).show();
				DeviceMusicBoxFragment.oc = false;

			}
		}
		// 解析存放歌曲
		// 0Fxxxxyyzz...：表示读取文件名，0F为标记，xxxx 当前曲目，yy 文件名长度，zz...
		else if (epData.startsWith("0F")) {
			if (epData == null || epData.length() < 10 || bb == null
					|| !bb.equals("FF"))
				return;

			String xxxx = epData.substring(2, 6);

			String zz = epData.substring(8);

			String songID = xxxx;
			String songName = StringUtil.getStringUTF8(zz);

			modelSDButton
					.setBackgroundResource(R.drawable.device_musicbox_sd_pressed);
			modelAPButton
					.setBackgroundResource(R.drawable.device_musicbox_ap_normal);
			modelSTAButton
					.setBackgroundResource(R.drawable.device_musicbox_sta_normal);

			mNext.setVisibility(View.VISIBLE);
			mPrevious.setVisibility(View.VISIBLE);
			mRepeatModel.setVisibility(View.VISIBLE);
			musicListView.setVisibility(View.VISIBLE);
			mdownload.setVisibility(View.INVISIBLE);
			mdownload.setVisibility(View.GONE);

			MusicBoxRecordEntity mMusicBoxRecordEntity = new MusicBoxRecordEntity();
			mMusicBoxRecordEntity.setGwID(gwID);
			mMusicBoxRecordEntity.setDevID(devID);
			mMusicBoxRecordEntity.setEp(ep);
			mMusicBoxRecordEntity.setSongID(songID);
			mMusicBoxRecordEntity.setSongName(songName);

			int checkIndex = mMusicBoxRecordsAdapter
					.checkSongExist(mMusicBoxRecordEntity.getSongID());
			if (checkIndex == -1) {
				mMusicBoxRecordsAdapter.add(mMusicBoxRecordEntity);
				// 主动去设定某条ListItem的位置
				musicListView
						.setSelection(mMusicBoxRecordsAdapter.getCount() - 1);
			} else {
				mMusicBoxRecordsAdapter
						.updateMusicBoxRecordData(mMusicBoxRecordEntity);
				mMusicBoxRecordsAdapter.notifyDataSetChanged();
				musicListView.setSelection(checkIndex);
			}

		}
	}

	/**
	 * 提供方法,用户按音量键调节音乐盒大小时调用此方法 upVolume() 增大音量 downVolume() 减小音量
	 */
	public void upVolume() {
		int addvolume = StringUtil.toInteger(cc_c, 16) + 7;

		if (addvolume > 0 && addvolume < 100) {
			controlDevice(ep, epType,
					"4" + StringUtil.toDecimalString(addvolume, 3));

		} else if (addvolume >= 100) {
			controlDevice(ep, epType, "4099");

		}
	}

	public void downVolume() {
		int subvolume = StringUtil.toInteger(cc_c, 16) - 7;
		if (subvolume > 0 && subvolume < 100) {
			controlDevice(ep, epType,
					"4" + StringUtil.toDecimalString(subvolume, 3));

		} else if (subvolume <= 0) {
			controlDevice(ep, epType, "4000");

		}
	}
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		
		if(item == null){
			ShortCutControlableDeviceSelectDataItem shortCutItem = new ShortCutControlableDeviceSelectDataItem(inflater.getContext());
			shortCutItem.setStopVisiable(true);
			shortCutItem.setCloseVisiable(false);
			item = shortCutItem;
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		return getDefaultShortCutControlView(item, inflater);
	}
	// 适配
	private class MusicBoxRecordsAdapter extends BaseAdapter {

		private Context mContext;
		private LayoutInflater inflater;
		private String currentItemId;
		private MusicDao musicDao = MusicDao.getInstance();
		private List<MusicBoxRecordEntity> list = musicDao.findSongName();

		public String getCurrentItemId() {
			return currentItemId;
		}

		public void setCurrentItemId(String currentItemId) {
			this.currentItemId = currentItemId;
			notifyDataSetChanged();

		}

		public MusicBoxRecordsAdapter(Context context) {
			super();
			this.mContext = context;
			this.inflater = LayoutInflater.from(this.mContext);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public MusicBoxRecordEntity getItem(int arg0) {
			// 获取数据集中与指定索引对应的数据项
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			// 取在列表中与指定索引对应的行id
			return 0;
		}
		
		public View getView(final int position, View convertView,
				ViewGroup parent) {
			final MusicBoxRecordEntity musicBoxRecordEntityItem = getItem(position);// 拿到自己的一首歌
			View view = null;
			if (convertView == null) {
				view = this.inflater.inflate(
						R.layout.device_musicbox_list_item, null);
			} else {
				view = convertView;
			}
			TextView mTextViewItem = (TextView) view
					.findViewById(R.id.songtext);
			Collections.sort(list, new MyComparator());
			mTextViewItem.setText(musicBoxRecordEntityItem.getSongName());
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					String str = musicBoxRecordEntityItem.getSongID();
					int num = StringUtil.toInteger(str, 16);
					controlDevice(ep, epType,
							"1" + StringUtil.toDecimalString(num, 4));

				}
			});
			view.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View arg0) {
					Dialog builder = new AlertDialog.Builder(mContext)
							.setTitle(R.string.device_songname_refresh_title)
							.setMessage(
									R.string.device_songname_refresh_message)
							.setPositiveButton(R.string.device_ok,
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface arg0, int arg1) {
											String str = musicBoxRecordEntityItem
													.getSongID();
											int num = StringUtil.toInteger(str,
													16);
											String strRequest = StringUtil
													.toDecimalString(num, 4);
											controlDevice(ep, epType, "F"
													+ strRequest);
											arg0.cancel();
										}
									}).create();
					builder.show();
					return true;
				}
			});
			if (musicBoxRecordEntityItem.getSongID() != null
					&& (musicBoxRecordEntityItem.getSongID()
							.equals(currentItemId))) {
				mTextViewItem.setTextColor(Color.GREEN);

			} else {
				mTextViewItem.setTextColor(Color.WHITE);

			}
			return view;
		}

		public void updateMusicBoxRecordData(MusicBoxRecordEntity entity) {
			for (int i = 0; i < list.size(); i++) {
				MusicBoxRecordEntity en = list.get(i);
				if (entity.getSongID().equals(en.getSongID())) {
					// 替换当期songName
					en.setSongName(entity.getSongName());
					musicDao.update(entity);
					notifyDataSetChanged();
					break;
				}
			}
		}

		public void add(MusicBoxRecordEntity entity) {
			musicDao.insert(entity);
			list.add(entity);
			Collections.sort(list, new MyComparator());
			notifyDataSetChanged();

		}

		public int checkSongExist(String songID) {
			for (int i = 0; i < list.size(); i++) {
				MusicBoxRecordEntity en = list.get(i);
				if (songID.equals(en.getSongID())) {
					return i;
				}
			}
			return -1;
		}

		public class MyComparator implements Comparator<MusicBoxRecordEntity> {

			@Override
			public int compare(MusicBoxRecordEntity arg0,
					MusicBoxRecordEntity arg1) {
				return arg0.getSongID().compareTo(arg1.getSongID());
			}

		}
	}
}
