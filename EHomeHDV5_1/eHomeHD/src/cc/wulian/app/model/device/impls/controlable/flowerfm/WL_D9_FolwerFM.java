package cc.wulian.app.model.device.impls.controlable.flowerfm;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.CircleImageView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

/**
 * 发送数据： 1id : 表示播放直播电台(已预存的的频道及listView的id+1) 2 url name info :设置页播放指定地址的直播电台
 * url--在线声音的地址 name--声音名称 info--声音的备注信息 3xxxxxxxxxx: 增量获取当前直播电台的列表(10位时间戳) 4id:
 * 删除指定电台 5 : 清空所有电台
 *
 * X:获取当前的播放状态 YX : 切换列表 X: 0(上一电台) 1(下一电台) ZY : 表示播放状态 Y:(0:播放 1:暂停 2:停止)
 *
 * 接收数据: Xabbbcde{id order url name info pos} : 返回的播放器的状态 a -- 播放内容 (0代表正在播直播
 * 1:点播) bbb -- 音量(0~100) c -- 静音状态(0 取消静音 1 静音) d -- 播放模式(1 列表循环 2 随机循环 3 单曲循环
 * 4 列表顺序播放不循环播完停止) e -- 播放状态(0:播放 1:暂停 2:停止)
 *
 * id -- 对应内容id order--内容顺序 url -- 内容在线地址 info-- 内容备注信息 pos -- 播放到的时间位置(秒) 5id
 * url name info : 返回的当前电台列表 4id : 删除指定电台 5 : 清空所有电台
 *
 * @author created by wangbin on 11/19/2015
 *
 */

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_FLOWER_FM }, category = Category.C_OTHER)
public class WL_D9_FolwerFM extends ControlableDeviceImpl {

	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO = "G";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_LIST = "C";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_DELETE = "D";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_ADD = "B";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_EMPTY = "E";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_SWITCH = "Y";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY = "Z";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_SHORTCUT_PLAY = "W";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_MODE = "F";
	private static final String DATA_CTRL_SET_ON_DEMAND_RADIO_VOICE = "V";

	private static final String DATA_CTRL_GET_LIVE_RADIO_EMPTY = "X";
	private LinearLayout radioVoiceLayout;
	private LinearLayout radioControlLayout;
	private LinearLayout radioPictureLayout1;
	private LinearLayout radioPictureLayout2;
	private ImageButton radioMinImage;
	private ImageButton radioMaxImage;
	private ImageView radioVoiceImageView;
	private ImageView selectFMImageView;
	private ImageView defaultImageView;
	private TextView selectFMTextView;
	private ImageButton preRadioFM;
	private ImageButton stopRadioFM;
	private ImageButton playRadioFM;
	private ImageButton nextRadioFM;
	private ImageButton addRadioFM;
	private ImageButton addDiskRadioFM;// added by 殷田
	// private ImageButton defineRadioFM;
	private ImageButton playRadioMode;
	private SeekBar radioSeekBar;
	private SeekBar radioVoiceSeekBar;
	private SwipeMenuListView radioListView;
	private TextView radioPlayTime;
	private int progress;
	private TextView radioAllTime;
	private TextView radioListText;
	private LinearLayout radioDeleteAll;
	private TextView radioDeleteTextView;
	private ImageView radioDeleteImageView;
	private TextView radioEmptyDefaltTextView;
	private LinearLayout radioEmptyDefaltLayout;
	private LinearLayout radioMinddleLayout;
	private ImageView radioEmptyDefaltImageView;
	private Map<String, FlowerRadioInfo> radioMap = new ConcurrentHashMap<String, FlowerRadioInfo>();
	// private List<FlowerRadioInfo> radioList = new
	// ArrayList<FlowerRadioInfo>();
	private Preference preference = Preference.getPreferences();
	private RadioListAdapter radioListAdapter;
	private Handler handler = new Handler(Looper.getMainLooper());
	private TaskExecutor taskExecutor = TaskExecutor.getInstance();
	private static final int SMALL_OPEN_D = R.drawable.device_open_radio_icon;
	private static final int SMALL_CLOSE_D = R.drawable.device_close_radio_icon;
	private boolean isControlRadio = false;
	private boolean isRadioPlay = false;
	private boolean isRadioPause = false;
	private String playContent;
	private String playVoice;
	private String playNoVoice;
	private String playMode;
	private String playRadioID;
	private String overNumber;
	private int playRadioPos;
	private int playAllTime;
	private String playPictureUrl;
	private String playStatus;
	private String playRadioName;
	private String playRadioUrl;
	private String playRadioinfo;
	private WLDialog dialog;
	// private EditText addRadioName;
	// private EditText addRadioAddress;
	private JSONObject radioJsonObject;
	private int[] radioMOdeDrawableId = new int[4];
	private Map<String, Bitmap> drawableMap = new HashMap<String, Bitmap>();

	private String pluginName = "XMLY.zip";

	private Runnable updateProgressRunnable = new Runnable() {

		@Override
		public void run() {
			playRadioPos += 1;
			progress = (playRadioPos * 1000) / playAllTime;
			handler.post(new Runnable() {

				@Override
				public void run() {
					if (playRadioPos >= playAllTime) {
						taskExecutor.removeScheduled(updateProgressRunnable);
						stopRadioFM.setVisibility(View.VISIBLE);
						playRadioFM.setVisibility(View.GONE);
					}
					radioSeekBar.setProgress(progress);
					// radioPlayTime.setText(playRadioPos + " " + progress +
					// "%");
					radioPlayTime.setText(StringUtil.appendLeft(getTimeMinute(playRadioPos), 2, '0') + ":"
							+ StringUtil.appendLeft(getTimeSeconds(playRadioPos), 2, '0'));
				}
			});
		}
	};
	private Comparator<FlowerRadioInfo> comparator = new Comparator<FlowerRadioInfo>() {

		@Override
		public int compare(FlowerRadioInfo arg0, FlowerRadioInfo arg1) {
			if (Integer.parseInt(arg1.getId()) > Integer.parseInt(arg0.getId())) {
				return -1;
			} else {
				return 1;
			}
		}

	};

	private OnClickListener buttonLinester = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == radioVoiceImageView) {
				radioVoiceLayout.setVisibility(View.VISIBLE);
				radioControlLayout.setVisibility(View.GONE);
				int voiceProgress = StringUtil.toInteger(playVoice);
				radioVoiceSeekBar.setProgress(voiceProgress);
			} else if (v == radioMaxImage) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_VOICE + "100");
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
			} else if (v == radioMinImage) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_VOICE + "000");
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
			} else if (v == radioPictureLayout1 || v == radioPictureLayout2 || v == radioMinddleLayout
					|| v == radioEmptyDefaltLayout) {
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
			} else if (v == preRadioFM) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_SWITCH + "0");
			} else if (v == nextRadioFM) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_SWITCH + "1");
			} else if (v == stopRadioFM) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "0");
			} else if (v == playRadioFM) {
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "1");
			} else if (v == addRadioFM) {
				getPlugin("index.html");
			} else if (v == addDiskRadioFM) {
				getPlugin("disk_radio.html");
			}
			// else if(v == defineRadioFM){
			// createAddCustomRadioDialog();
			// }
			else if (v == playRadioMode) {
				if (StringUtil.equals(playMode, "1")) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_MODE + "2");
				} else if (StringUtil.equals(playMode, "2")) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_MODE + "3");
				} else if (StringUtil.equals(playMode, "3")) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_MODE + "4");
				} else if (StringUtil.equals(playMode, "4")) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_MODE + "1");
				}
			} else if (v == radioDeleteAll) {
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
				createClickDeleteDialog();
			}
		}
	};

	public WL_D9_FolwerFM(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		return DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "0";
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "1";
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "0";
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "1";
	}

	@Override
	public boolean isOpened() {
		return isRadioPlay;
	}

	@Override
	public boolean isClosed() {
		return isRadioPause;
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable icon = null;
		if (isOpened()) {
			icon = getDrawable(SMALL_OPEN_D);
		} else if (isClosed()) {
			icon = getDrawable(SMALL_OPEN_D);
		} else {
			icon = WL_D9_FolwerFM.this.getDefaultStateSmallIcon();
		}
		return icon;
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
		return inflater.inflate(R.layout.device_flower_fm_layout, null);
	}

	@Override
	public void onDetachView() {
		super.onDetachView();
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		radioListAdapter = new RadioListAdapter(mContext, null);
		getPlayList();
		controlDevice(ep, epType, DATA_CTRL_GET_LIVE_RADIO_EMPTY);

		radioVoiceLayout = (LinearLayout) view.findViewById(R.id.flower_fm_adjust_voice_layout);
		radioControlLayout = (LinearLayout) view.findViewById(R.id.flower_fm_control_layout);
		radioPictureLayout1 = (LinearLayout) view.findViewById(R.id.flower_fm_picture_layout1);
		radioPictureLayout2 = (LinearLayout) view.findViewById(R.id.flower_fm_picture_layout2);
		radioMinImage = (ImageButton) view.findViewById(R.id.flower_fm_adjust_voice_min_btn);
		radioMaxImage = (ImageButton) view.findViewById(R.id.flower_fm_adjust_voice_max_btn);
		radioVoiceImageView = (ImageView) view.findViewById(R.id.flower_fm_adjust_voice);
		selectFMImageView = (ImageView) view.findViewById(R.id.flower_fm_select_ImageView);
		defaultImageView = (ImageView) view.findViewById(R.id.flower_fm_select_default__ImageView);
		selectFMTextView = (TextView) view.findViewById(R.id.flower_fm_select_textview);
		preRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_pre_radio);
		playRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_play);
		stopRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_stop);
		nextRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_next_radio);
		// defineRadioFM = (ImageButton)
		// view.findViewById(R.id.flower_fm_defined_radio);
		playRadioMode = (ImageButton) view.findViewById(R.id.flower_fm_list_loop_play_mode);
		TypedArray radioModeArray = mContext.getResources().obtainTypedArray(R.array.radioMode);
		for (int i = 0; i < radioModeArray.length(); i++) {
			radioMOdeDrawableId[i] = radioModeArray.getResourceId(i, 0);
		}
		radioModeArray.recycle();
		addRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_add_radio);
		addDiskRadioFM = (ImageButton) view.findViewById(R.id.flower_fm_add_disk_radio);
		radioListView = (SwipeMenuListView) view.findViewById(R.id.flower_fm_list);

		radioAllTime = (TextView) view.findViewById(R.id.flower_fm_all_time_textview);
		radioPlayTime = (TextView) view.findViewById(R.id.flower_fm_play_time_textview);
		radioVoiceSeekBar = (SeekBar) view.findViewById(R.id.flower_fm_adjust_voice_seekbar);
		radioSeekBar = (SeekBar) view.findViewById(R.id.device_flower_radio_seekBar_custom);

		radioListText = (TextView) view.findViewById(R.id.flower_fm_radio_list_number);
		radioDeleteAll = (LinearLayout) view.findViewById(R.id.flower_fm_radio_list_delete_all);
		radioDeleteTextView = (TextView) view.findViewById(R.id.flower_fm_radio_list_delete_text);
		radioDeleteImageView = (ImageView) view.findViewById(R.id.flower_fm_radio_list_delete_imageview);
		radioEmptyDefaltLayout = (LinearLayout) view.findViewById(R.id.device_flower_radio_list_default_layout);
		radioMinddleLayout = (LinearLayout) view.findViewById(R.id.flower_fm_radio_middle_remind);

		radioSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				// radioPlayTime.setText((int)seekBar.getProgress()+"%");
				int changeRadioPaly = (int) (seekBar.getProgress() * playAllTime / 1000);
				radioPlayTime.setText(StringUtil.appendLeft(getTimeMinute(changeRadioPaly), 2, '0') + ":"
						+ StringUtil.appendLeft(getTimeSeconds(changeRadioPaly), 2, '0'));
				String data = StringUtil.appendLeft(changeRadioPaly + "", 4, '0');
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_SHORTCUT_PLAY + data);
				if (playRadioFM.getVisibility() == View.VISIBLE) {
					taskExecutor.removeScheduled(updateProgressRunnable);
				}

			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int arg1, boolean arg2) {
				int changeRadioPaly = (int) (seekBar.getProgress() * playAllTime / 1000);
				radioPlayTime.setText(StringUtil.appendLeft(getTimeMinute(changeRadioPaly), 2, '0') + ":"
						+ StringUtil.appendLeft(getTimeSeconds(changeRadioPaly), 2, '0'));
			}
		});
		radioVoiceSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int changeRadioPaly = seekBar.getProgress();
				String data = StringUtil.appendLeft(changeRadioPaly + "", 3, '0');
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_VOICE + data);
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			}
		});
		preRadioFM.setOnClickListener(buttonLinester);
		playRadioFM.setOnClickListener(buttonLinester);
		stopRadioFM.setOnClickListener(buttonLinester);
		nextRadioFM.setOnClickListener(buttonLinester);
		radioVoiceImageView.setOnClickListener(buttonLinester);
		radioPictureLayout1.setOnClickListener(buttonLinester);
		radioPictureLayout2.setOnClickListener(buttonLinester);
		radioMinImage.setOnClickListener(buttonLinester);
		radioMaxImage.setOnClickListener(buttonLinester);
		radioEmptyDefaltLayout.setOnClickListener(buttonLinester);
		radioMinddleLayout.setOnClickListener(buttonLinester);
		// defineRadioFM.setOnClickListener(buttonLinester);
		playRadioMode.setOnClickListener(buttonLinester);
		addRadioFM.setOnClickListener(buttonLinester);
		addDiskRadioFM.setOnClickListener(buttonLinester);
		radioDeleteAll.setOnClickListener(buttonLinester);

		radioListAdapter.setMenuCreator(creatLeftDeleteItem());
		radioListView.setAdapter(radioListAdapter);
		initSaveRadioList();
	}

	private void getPlayList() {
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				boolean isFirstTime = preference.getBoolean(gwID + IPreferenceKey.P_KEY_FLOWER_FM_ADD_FIRST_TIME, true);
				if (isFirstTime) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_LIST);
					preference.putBoolean(gwID + IPreferenceKey.P_KEY_FLOWER_FM_ADD_FIRST_TIME, false);
				} else {
					String preGateWayTime = preference.getString(gwID + IPreferenceKey.P_KEY_FLOWER_FM_ADD_TIME, "");
					if (!StringUtil.isNullOrEmpty(preGateWayTime)) {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_LIST + preGateWayTime);
					} else {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_LIST);
					}
				}
			}
		});
	}

	/**
	 * 创建左划删除item样式
	 */
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(mContext);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9, 0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(mContext, 90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}

	private void initSaveRadioList() {
		radioListAdapter.setOnMenuItemClickListener(new OnMenuItemClickListener() {

			@Override
			public void onMenuItemClick(int position, SwipeMenu menu, int index) {
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
				String selectId = radioListAdapter.getItem(position).getId();
				FlowerRadioInfo item = radioMap.get(selectId);
				switch (index) {
				case 0:
					if (item != null) {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_DELETE + selectId + "");
						radioMap.remove(selectId);
					}
					break;
				}
			}

		});
		// 解决左划删除与右划菜单栏冲突
		radioListView.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
			}
		});

		radioListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				radioVoiceLayout.setVisibility(View.GONE);
				radioControlLayout.setVisibility(View.VISIBLE);
				String selectId = radioListAdapter.getItem(position).getId();
				controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO + selectId);
				// radioListAdapter.setSelectRadioId(selectId);
			}
		});
	}

	@Override
	public void initViewStatus() {

		if (isControlRadio) {
			selectFMTextView.setVisibility(View.VISIBLE);
			selectFMTextView.setText(playRadioName);
			if (playAllTime == 0) {
				playAllTime = 1000;
			}
			if (playRadioPos != 0) {
				progress = (playRadioPos * 1000) / playAllTime;
			} else {
				progress = 0;
				radioSeekBar.setProgress(progress);
			}
			if (StringUtil.equals(playStatus, "0")) {
				stopRadioFM.setVisibility(View.GONE);
				playRadioFM.setVisibility(View.VISIBLE);
				radioListAdapter.setSelectRadioId(playRadioID, true);
				if (playRadioPos == playAllTime) {
					taskExecutor.removeScheduled(updateProgressRunnable);
				} else {
					taskExecutor.addScheduled(updateProgressRunnable, 0, 1000, TimeUnit.MILLISECONDS);
				}
			} else if (StringUtil.equals(playStatus, "1")) {
				stopRadioFM.setVisibility(View.VISIBLE);
				playRadioFM.setVisibility(View.GONE);
				radioSeekBar.setProgress(progress);
				radioListAdapter.setSelectRadioId(playRadioID, false);
				taskExecutor.removeScheduled(updateProgressRunnable);
			} else if (StringUtil.equals(playStatus, "2")) {
				stopRadioFM.setVisibility(View.VISIBLE);
				playRadioFM.setVisibility(View.GONE);
				radioSeekBar.setProgress(progress);
				radioListAdapter.setSelectRadioId(playRadioID, false);
				taskExecutor.removeScheduled(updateProgressRunnable);
			}
			if (StringUtil.equals(playMode, "1")) {
				playRadioMode.setBackgroundResource(radioMOdeDrawableId[0]);
			} else if (StringUtil.equals(playMode, "2")) {
				playRadioMode.setBackgroundResource(radioMOdeDrawableId[1]);
			} else if (StringUtil.equals(playMode, "3")) {
				playRadioMode.setBackgroundResource(radioMOdeDrawableId[2]);
			} else if (StringUtil.equals(playMode, "4")) {
				playRadioMode.setBackgroundResource(radioMOdeDrawableId[3]);
			}
			radioPlayTime.setText(StringUtil.appendLeft(getTimeMinute(playRadioPos), 2, '0') + ":"
					+ StringUtil.appendLeft(getTimeSeconds(playRadioPos), 2, '0'));
			radioAllTime.setText(StringUtil.appendLeft(getTimeMinute(playAllTime), 2, '0') + ":"
					+ StringUtil.appendLeft(getTimeSeconds(playAllTime), 2, '0'));
			// Bitmap bitmap = getImageViewBitmapDrawable(playRadioID);
			// if(bitmap != null){
			// selectFMImageView.setImageBitmap(getImageViewBitmapDrawable(playRadioID));
			// }
			if (StringUtil.equals(playRadioID, "-1")) {
				if (StringUtil.isNullOrEmpty(playPictureUrl) || StringUtil.equals(playPictureUrl, "null")) {
					defaultImageView.setVisibility(View.VISIBLE);
					selectFMImageView.setVisibility(View.GONE);
				} else {
					defaultImageView.setVisibility(View.GONE);
					selectFMImageView.setVisibility(View.VISIBLE);
					loadBitMapBigPicture(playPictureUrl, selectFMImageView);
				}
			} else {
				defaultImageView.setVisibility(View.GONE);
				selectFMImageView.setVisibility(View.VISIBLE);
				selectFMImageView.setTag(playRadioID);
				loadPicture(playRadioID, playPictureUrl, selectFMImageView);
			}
			radioJsonObject = new JSONObject();
			radioJsonObject.put("id", playRadioID);
			radioJsonObject.put("name", playRadioName);
			radioJsonObject.put("playStatus", playStatus);
			radioJsonObject.put("playTime", playRadioPos + "");
			radioJsonObject.put("allTime", playAllTime + "");
			radioJsonObject.put("iconUrl", playPictureUrl);
			radioJsonObject.put("overNumber", overNumber);
		} else {
			selectFMTextView.setVisibility(View.GONE);
		}
		int voiceProgress = StringUtil.toInteger(playVoice);
		radioVoiceSeekBar.setProgress(voiceProgress);
		List<FlowerRadioInfo> radioList = getRadioTaskList();
		overNumber = (100 - radioList.size()) + "";
		if (radioJsonObject == null) {
			radioJsonObject = new JSONObject();
			if (!radioList.isEmpty()) {
				radioJsonObject.put("id", radioList.get(0).getId());
				radioJsonObject.put("name", radioList.get(0).getName());
				radioJsonObject.put("playStatus", "1");
				radioJsonObject.put("playTime", "0");
				radioJsonObject.put("allTime", radioList.get(0).getInfoTime());
				radioJsonObject.put("iconUrl", radioList.get(0).getInfoUrl());
				radioJsonObject.put("overNumber", overNumber);
			}
		}
		radioJsonObject.put("overNumber", overNumber);
		if (radioList.isEmpty()) {
			radioEmptyDefaltLayout.setVisibility(View.VISIBLE);
			radioListView.setVisibility(View.GONE);
			radioDeleteAll.setEnabled(false);
			radioDeleteImageView.setImageResource(R.drawable.device_radio_delete_all_list);
			radioDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.progress_gray));
		} else {
			radioEmptyDefaltLayout.setVisibility(View.GONE);
			radioListView.setVisibility(View.VISIBLE);
			radioDeleteAll.setEnabled(true);
			radioDeleteImageView.setImageResource(R.drawable.device_radio_delete_all_list_black);
			radioDeleteTextView.setTextColor(mContext.getResources().getColor(R.color.black));
		}
		radioListAdapter.swapData(radioList);
		String hintStr = mContext.getResources().getString(R.string.flower_radio_number);
		hintStr = String.format(hintStr, radioList.size());
		radioListText.setText(hintStr);
	}

	@Override
	protected void refreshDeviceUpData() {
		loadRadioTaskMap();
		refreshDevice();
		controlDevice(ep, epType, DATA_CTRL_GET_LIVE_RADIO_EMPTY);
	}

	private void disassembleCompoundCmd(String epData) {

		overNumber = (100 - radioMap.size()) + "";
		if (StringUtil.isNullOrEmpty(epData)) {
			JsUtil.getInstance().execSavedCallback(gwID + "13", epData, JsUtil.ERROR, true);
			return;
		}
		if (epData.startsWith(DATA_CTRL_GET_LIVE_RADIO_EMPTY) && epData.length() > 9) {
			String[] playRadio = epData.substring(8).split(" ");
			if (StringUtil.equals(playRadio[0], "-1") && epData.length() < 18) {
				if (epData.length() >= 8) {
					playStatus = epData.substring(7, 8);
					if (StringUtil.equals(playStatus, "0")) {
						isRadioPlay = true;
						isRadioPause = false;
					} else {
						isRadioPlay = false;
						isRadioPause = true;
					}
					if (playRadio.length == 4 && StringUtil.isNullOrEmpty(playRadio[2])) {
						radioMap.clear();
						deleteRadioFile();
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_LIST);
					}
				} else {
					isRadioPlay = false;
					isRadioPause = true;
				}
				isControlRadio = false;
				return;
			}
			playContent = epData.substring(1, 2);
			playVoice = epData.substring(2, 5);
			playNoVoice = epData.substring(5, 6);
			playMode = epData.substring(6, 7);
			playStatus = epData.substring(7, 8);
			if (StringUtil.equals(playStatus, "0")) {
				isRadioPlay = true;
				isRadioPause = false;
			} else {
				isRadioPlay = false;
				isRadioPause = true;
			}
			String playRadioOrder = "";
			isControlRadio = true;
			if (playRadio.length == 6) {
				playRadioID = playRadio[0];
				playRadioOrder = playRadio[1];
				playRadioUrl = playRadio[2];
				if (StringUtil.isNullOrEmpty(playRadio[3])) {
					playRadioPos = 0;
				} else {
					double posDouble = StringUtil.toDouble(playRadio[3]);
					playRadioPos = (int) posDouble;
				}
				playRadioName = playRadio[4];
				playRadioinfo = playRadio[5];
				String[] playUrlAndTime = playRadioinfo.split(",");
				playPictureUrl = playUrlAndTime[0];
				if (playUrlAndTime.length > 1) {
					try {
						playAllTime = Integer.parseInt(playUrlAndTime[1]);
					} catch (Exception e) {
						playAllTime = 0;
					}
				}
				callbackHtmlEpdata(false);
			}else if (playRadio.length > 6) {
				playRadioID = playRadio[0];
				playRadioOrder = playRadio[1];

				playRadioUrl = playRadio[2];
				double posDouble = 0;
				int temp = 0; //记录播放到哪的时间的索引位置，为了下面显示音乐名称的索引做铺垫
				for (int i = 3; i < playRadio.length - 2; i++) {
					playRadioUrl = playRadioUrl + " " + playRadio[i];
					if (playRadio[i].endsWith(".mp3")){
						posDouble = StringUtil.toDouble(playRadio[i+1]);
						temp = i+1;
						break;
					}
				}
				playRadioPos = (int) posDouble;
				String playNameStr = playRadio[temp +1];
				for (int i = (temp + 2); i < playRadio.length - 1; i++) {
					playNameStr = playNameStr + " " + playRadio[i];
				}
				playRadioName = playNameStr.substring(1, playNameStr.length() - 1);
				playRadioinfo = playRadio[playRadio.length - 1];
				String[] playUrlAndTime = playRadioinfo.split(",");
				playPictureUrl = playUrlAndTime[0];
				if (playUrlAndTime.length > 1) {
					try {
						playAllTime = Integer.parseInt(playUrlAndTime[1]);
					} catch (Exception e) {
						playAllTime = 0;
					}
				}
				callbackHtmlEpdata(false);
			} else {
				isControlRadio = false;
			}
		} else {
			if (StringUtil.equals(epData, DATA_CTRL_SET_ON_DEMAND_RADIO_EMPTY)) {
				radioMap.clear();
				deleteRadioFile();
			} else if (epData.startsWith(DATA_CTRL_SET_ON_DEMAND_RADIO_DELETE)) {
				radioMap.remove(Integer.parseInt(epData.substring(1)) + "");
				deleteSingleRadio(epData.substring(1));
			} else if (epData.startsWith(DATA_CTRL_SET_ON_DEMAND_RADIO_ADD)
					|| epData.startsWith(DATA_CTRL_SET_ON_DEMAND_RADIO_LIST)) {
				String radioItemID = null;
				String playRadioOrder = null;
				String radioItemUrl = null;
				String radioItemName = null;
				String radioItemPictureUrl = null;
				int radioItemAllTime = 0;
				String radioIteminfo;
				if (epData.length() > 2) {
					String[] radioInfos = epData.substring(1).split(" ");
					if (StringUtil.equals(radioInfos[0], "-1")) {
						isControlRadio = false;
						return;
					} else {
						if (radioInfos.length > 2) {
							if (radioInfos.length == 5) {
								radioItemID = radioInfos[0];
								playRadioOrder = radioInfos[1];
								radioItemUrl = radioInfos[2];
								radioItemName = radioInfos[3];
								radioIteminfo = radioInfos[4];
								String[] playUrlAndTime = radioIteminfo.split(",");
								radioItemPictureUrl = playUrlAndTime[0];
								if (playUrlAndTime.length > 1 && !StringUtil.equals(playUrlAndTime[1], " ")) {
									double allTimeDouble = StringUtil.toDouble(playUrlAndTime[1]);
									radioItemAllTime = (int) allTimeDouble;
								}
							} else if (radioInfos.length > 5) {
								radioItemID = radioInfos[0];
								playRadioOrder = radioInfos[1];
//								radioItemUrl = radioInfos[2];
								radioItemUrl = radioInfos[2];
								double posDouble = 0;
								int temp = 0; //记录播放地址，为了下面显示音乐名称的索引做铺垫
								for (int i = 3; i < radioInfos.length - 1; i++) {
									radioItemUrl = radioItemUrl + " " + radioInfos[i];
									if (radioInfos[i].endsWith(".mp3")){
										temp = i;
										break;
									}
								}
								String playNameStr = radioInfos[temp+1];
								for (int i = (temp + 2); i < radioInfos.length - 1; i++) {
									playNameStr = playNameStr + " " + radioInfos[i];
								}

								radioItemName = playNameStr.replace("\"","");
								radioIteminfo = radioInfos[radioInfos.length - 1];
								String[] playUrlAndTime = radioIteminfo.split(",");
								radioItemPictureUrl = playUrlAndTime[0];
								if (playUrlAndTime.length > 1 && !StringUtil.equals(playUrlAndTime[1], " ")) {
									double allTimeDouble = StringUtil.toDouble(playUrlAndTime[1]);
									radioItemAllTime = (int) allTimeDouble;
								}
							}
							FlowerRadioInfo radioInfo = new FlowerRadioInfo();
							radioInfo.setId(radioItemID);
							radioInfo.setUrl(radioItemUrl);
							radioInfo.setName(radioItemName);
							radioInfo.setInfoUrl(radioItemPictureUrl);
							radioInfo.setInfoTime(radioItemAllTime);
							radioMap.put(radioItemID, radioInfo);
						}
					}
				}
			}
			isControlRadio = false;
			radioMapToListSaveFile();
			overNumber = (100 - radioMap.size()) + "";
			callbackHtmlEpdata(true);
		}
	}

	private void callbackHtmlEpdata(boolean isSendOverNumber) {
		JSONObject radioJsonObjectcallback = new JSONObject();
		if (isSendOverNumber) {
			radioJsonObjectcallback.put("cmdType", "O");
			radioJsonObjectcallback.put("overNumber", overNumber);
			JsUtil.getInstance().execSavedCallback(gwID + "13", radioJsonObjectcallback.toJSONString(), JsUtil.OK,
					false);
		} else {
			radioJsonObjectcallback.put("cmdType", "X");
			if (!StringUtil.isNullOrEmpty(playRadioID)) {
				radioJsonObjectcallback.put("id", playRadioID);
			}
			if (!StringUtil.isNullOrEmpty(playRadioName)) {
				radioJsonObjectcallback.put("name", playRadioName);
			}
			if (!StringUtil.isNullOrEmpty(playStatus)) {
				radioJsonObjectcallback.put("playStatus", playStatus);
			}
			radioJsonObjectcallback.put("playTime", playRadioPos);
			if (playAllTime != 0) {
				radioJsonObjectcallback.put("allTime", playAllTime);
			}
			if (!StringUtil.isNullOrEmpty(playPictureUrl)) {
				radioJsonObjectcallback.put("iconUrl", playPictureUrl);
			}
			JsUtil.getInstance().execSavedCallback(gwID + "13", radioJsonObjectcallback.toJSONString(), JsUtil.OK,
					false);
		}
	}

	private void radioMapToListSaveFile() {
		List<FlowerRadioInfo> radioList = CollectionsUtil.mapConvertToList(radioMap);
		String radioLocalFileStr = JSONArray.toJSONString(radioList);
		preference.putString(gwID + IPreferenceKey.P_KEY_FLOWER_FM_DEVICE_MAP, radioLocalFileStr);
		if (!StringUtil.isNullOrEmpty(getCurrentEpInfo().getTime()) && getCurrentEpInfo().getTime().length() > 10) {
			preference.putString(gwID + IPreferenceKey.P_KEY_FLOWER_FM_ADD_TIME, getCurrentEpInfo().getTime()
					.substring(0, 10));
		}
	}

	private void loadPicture(final String id, final String pictureUrl, final ImageView playingImageView) {
		if (StringUtil.isNullOrEmpty(pictureUrl) || StringUtil.equals(pictureUrl, "null")) {
			defaultImageView.setVisibility(View.VISIBLE);
			selectFMImageView.setVisibility(View.GONE);
			return;
		}
		if (drawableMap.containsKey(id)) {
			final Bitmap bitmap = drawableMap.get(id);
			if (bitmap != null && playingImageView.getTag() != null && playingImageView.getTag().equals(id)) {
				playingImageView.setImageBitmap(drawableMap.get(id));
			}
		} else {
			taskExecutor.execute(new Runnable() {
				@Override
				public void run() {
					final Bitmap bitmap = getBitMapFRomFileOrUrl(id, pictureUrl);
					handler.post(new Runnable() {
						@Override
						public void run() {
							if (bitmap != null && playingImageView.getTag() != null
									&& playingImageView.getTag().equals(id)) {
								playingImageView.setImageBitmap(bitmap);
							}
						}
					});
				}
			});
		}
	}

	private void loadBitMapBigPicture(final String pictureUrl, final ImageView playingImageView) {
		taskExecutor.execute(new Runnable() {
			@Override
			public void run() {
				byte[] bytes = HttpUtil.getPicture(pictureUrl);
				if (bytes != null) {
					final Bitmap bitmap = FileUtil.Bytes2Bitmap(bytes);
					if (bitmap != null) {
						handler.post(new Runnable() {
							@Override
							public void run() {
								playingImageView.setImageBitmap(bitmap);
							}
						});
					}
				}
			}
		});
	}

	private Bitmap getBitMapFRomFileOrUrl(final String id, final String pictureUrl) {
		final String fileName = id + ".png";
		final String floder = FileUtil.getDeviceRadioPicturePath();
		String filePath = floder + "/" + fileName;
		Bitmap bitMap = null;
		if (drawableMap.containsKey(id)) {
			bitMap = drawableMap.get(id);
		} else if (FileUtil.checkFileExistedAndAvailable(filePath)) {
			bitMap = BitmapFactory.decodeFile(filePath);
			drawableMap.put(id, bitMap);
		} else {
			byte[] bytes = HttpUtil.getPicture(pictureUrl);
			if (bytes != null) {
				bitMap = FileUtil.Bytes2Bitmap(bytes);
				FileUtil.saveBitmapToPng(bitMap, floder, fileName);
				drawableMap.put(id, bitMap);
			}
		}
		return bitMap;
	}

	// //获取存储在文件中的图片
	// private Bitmap getImageViewBitmapDrawable(String id) {
	// //先从图片资源map里面取值取不到就存在map里面，避免二次取
	// if(drawableMap.containsKey(id))
	// return drawableMap.get(id);
	// String fileName = FileUtil.getDeviceRadioPicturePath() + "/" + id
	// + ".jpg";
	// boolean isExit = FileUtil.checkFileExistedAndAvailable(fileName);
	// Bitmap bitmap = null;
	// if(isExit && BitmapFactory.decodeFile(fileName) != null){
	// bitmap = BitmapFactory.decodeFile(fileName);
	// drawableMap.put(id, bitmap);
	// }
	// return bitmap;
	// }
	private void deleteSingleRadio(String id) {
		String fileName = FileUtil.getDeviceRadioPicturePath() + "/" + id + ".png";
		boolean isExit = FileUtil.checkFileExistedAndAvailable(fileName);
		if (isExit) {
			FileUtil.delFile(fileName);
		}
		if (drawableMap.containsKey(id)) {
			drawableMap.remove(id);
		}
	}

	private void deleteRadioFile() {
		FileUtil.delAllFile(FileUtil.getDeviceRadioPicturePath());
		drawableMap.clear();
	}

	public static class FlowerRadioInfo {

		private static final long serialVersionUID = 1L;
		private String id;
		private String url;
		private String name;
		private String infoUrl;
		private int infoTime;

		public FlowerRadioInfo() {
			super();
		}

		public String getId() {
			return id;
		}

		public void setId(String id) {
			this.id = id;
		}

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getInfoUrl() {
			return infoUrl;
		}

		public void setInfoUrl(String infoUrl) {
			this.infoUrl = infoUrl;
		}

		public int getInfoTime() {
			return infoTime;
		}

		public void setInfoTime(int infoTime) {
			this.infoTime = infoTime;
		}

	}

	private class RadioListAdapter extends SwipeMenuAdapter<FlowerRadioInfo> {
		private String selectedRadioId;
		private boolean isPlay;

		public RadioListAdapter(Context context, List<FlowerRadioInfo> data) {
			super(context, data);
		}

		@Override
		protected View newView(int position, View convertView, ViewGroup parent) {
			FlowerFMListItem item = new FlowerFMListItem(mContext);
			View rootView = item.getView();
			rootView.setTag(item);
			return rootView;
		}

		@Override
		protected void bindView(Context mContext2, View convertView, int position, final FlowerRadioInfo info) {
			FlowerFMListItem item = (FlowerFMListItem) convertView.getTag();
			item.refresh(info);
			item.getRadioPlay().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "1");
				}
			});
			item.getRadioPause().setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					if (StringUtil.equals(selectedRadioId, info.getId())) {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_PLAY + "0");
					} else {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO + info.getId());
					}
				}
			});
			if (selectedRadioId != null) {
				if (StringUtil.equals(selectedRadioId, info.getId())) {
					item.setItemClicked(isPlay);
				} else {
					item.setItemUnClicked();
				}
			}
		}

		public void setSelectRadioId(String radioId, boolean isplay) {
			selectedRadioId = radioId;
			isPlay = isplay;
			notifyDataSetChanged();
		}

		private class FlowerFMListItem {

			private LayoutInflater inflater;
			private LinearLayout lineLayout;
			private FrameLayout itemIconLayout;
			private CircleImageView radioImageView;
			private CircleImageView radioPlay;
			private CircleImageView radioPause;
			private LinearLayout radioTimeLayout;
			private ImageView radioTimeIcon;
			private TextView radioName;
			private TextView radioTime;
			private ImageView radioOther;

			public FlowerFMListItem(Context context) {
				inflater = LayoutInflater.from(context);
				mResources = context.getResources();

				lineLayout = (LinearLayout) inflater.inflate(R.layout.device_flower_radio_list_item, null);
				itemIconLayout = (FrameLayout) lineLayout.findViewById(R.id.device_radio_item_icon_framelayout);
				radioImageView = (CircleImageView) lineLayout.findViewById(R.id.device_flower_radio_item_imageview);
				radioPlay = (CircleImageView) lineLayout.findViewById(R.id.device_flower_radio_item_imageview_play);
				radioPause = (CircleImageView) lineLayout.findViewById(R.id.device_flower_radio_item_imageview_pause);
				radioTimeLayout = (LinearLayout) lineLayout.findViewById(R.id.device_radio_item_time_layout);
				radioTimeIcon = (ImageView) lineLayout.findViewById(R.id.device_radio_item_time_icon);
				radioName = (TextView) lineLayout.findViewById(R.id.device_flower_radio_item_name);
				radioTime = (TextView) lineLayout.findViewById(R.id.device_flower_radio_item_other);
				radioOther = (ImageView) lineLayout.findViewById(R.id.device_flower_radio_item_info_imageview);
				radioOther.setVisibility(View.GONE);

			}

			public CircleImageView getRadioPlay() {
				return radioPlay;
			}

			public CircleImageView getRadioPause() {
				return radioPause;
			}

			public void refresh(final FlowerRadioInfo flowerRadioInfo) {
				if (!StringUtil.isNullOrEmpty(flowerRadioInfo.getName())) {
					radioName.setText(flowerRadioInfo.getName());
				} else {
					radioName.setText(mContext.getResources().getString(R.string.flower_radio_item_default_text)
							+ flowerRadioInfo.getId());
				}
				if (flowerRadioInfo.getInfoTime() != 0) {
					radioTimeLayout.setVisibility(View.VISIBLE);
					radioTime.setText(StringUtil.appendLeft(getTimeMinute(flowerRadioInfo.getInfoTime()), 2, '0') + ":"
							+ StringUtil.appendLeft(getTimeSeconds(flowerRadioInfo.getInfoTime()), 2, '0'));
				} else {
					radioTimeLayout.setVisibility(View.GONE);
				}
				radioImageView.setTag(flowerRadioInfo.getId());
				if (!StringUtil.equals(flowerRadioInfo.getInfoUrl(), "null")) {
					loadPicture(flowerRadioInfo.getId(), flowerRadioInfo.getInfoUrl(), radioImageView);
				} else {
					if (radioImageView.getTag() != null && radioImageView.getTag().equals(flowerRadioInfo.getId())) {
						radioImageView.setImageDrawable(mContext.getResources().getDrawable(
								R.drawable.device_flower_radio_defaule_ico));
					}
				}
			}

			public View getView() {
				return lineLayout;
			}

			public void setItemClicked(boolean isPlay) {
				radioName.setTextColor(mContext.getResources().getColor(R.color.v5_green_light));
				radioTime.setTextColor(mContext.getResources().getColor(R.color.v5_green_light));
				radioTimeIcon.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.device_radio_item_time_light));
				if (isPlay) {
					radioPlay.setVisibility(View.VISIBLE);
					radioPause.setVisibility(View.GONE);
				} else {
					radioPlay.setVisibility(View.GONE);
					radioPause.setVisibility(View.VISIBLE);
				}
			}

			public void setItemUnClicked() {
				radioName.setTextColor(mContext.getResources().getColor(R.color.black));
				radioTime.setTextColor(mContext.getResources().getColor(R.color.grey));
				radioTimeIcon.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.device_radio_item_time_grey));
				radioPlay.setVisibility(View.GONE);
				radioPause.setVisibility(View.VISIBLE);
			}

		}
	}

	private String getTimeMinute(int time) {
		int result = 0;
		if (time >= 60) {
			result = time / 60;
		}
		return result + "";
	}

	private String getTimeSeconds(int time) {
		int result = time;
		if (time >= 60) {
			result = time % 60;
		}
		return result + "";
	}

	// /**
	// * 在子线程里http获取网络Image
	// * @param urlpath
	// * @return
	// * @throws Exception
	// */
	// private static Bitmap getImage(String urlpath)
	// throws Exception {
	// URL url = new URL(urlpath);
	// HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	// conn.setRequestMethod("GET");
	// conn.setConnectTimeout(5 * 1000);
	// Bitmap bitmap = null;
	// if (conn.getResponseCode() == 200) {
	// InputStream inputStream = conn.getInputStream();
	// bitmap = BitmapFactory.decodeStream(inputStream);
	// }
	// return bitmap;
	// }

	private void createClickDeleteDialog() {
		WLDialog.Builder builder = new Builder(mContext);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View contentView = inflater.inflate(R.layout.common_dialog_text_prompt, null);
		TextView promptText = (TextView) contentView.findViewById(R.id.common_dialog_text_prompt);
		promptText.setText(mContext.getResources().getString(R.string.flower_radio_delete_all_prompt));
		builder.setContentView(contentView).setTitle(R.string.gateway_router_setting_dialog_toast)
				.setPositiveButton(R.string.common_ok).setNegativeButton(R.string.cancel)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						controlDevice(ep, epType, DATA_CTRL_SET_ON_DEMAND_RADIO_EMPTY);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		dialog = builder.create();
		dialog.show();
	}

	// private void createAddCustomRadioDialog() {
	// WLDialog.Builder builder = new WLDialog.Builder(mContext);
	// builder.setTitle(mContext.getResources().getString(R.string.house_rule_task_sensor_device_select_values))
	// .setContentView(createAddRadioView())
	// .setNegativeButton(mContext.getResources().getString(R.string.cancel))
	// .setPositiveButton(mContext.getResources().getString(R.string.common_ok))
	// .setListener(new MessageListener() {
	//
	// @Override
	// public void onClickPositive(View contentViewLayout) {
	// if(!StringUtil.isNullOrEmpty(addRadioName.getText().toString())
	// && !StringUtil.isNullOrEmpty(addRadioAddress.getText().toString())){
	// String epData = 6 + " " + addRadioAddress.getText().toString() + " " +
	// addRadioName.getText().toString() + " " +"info";
	// controlDevice(ep, epType, epData);
	// }
	// }
	//
	// @Override
	// public void onClickNegative(View contentViewLayout) {
	//
	// }
	// });
	// dialog = builder.create();
	// dialog.show();
	// }

	// private View createAddRadioView(){
	// LayoutInflater inflater = LayoutInflater.from(mContext);
	// View addRadioView =
	// inflater.inflate(R.layout.device_flower_fm_add_custom_radio, null);
	// addRadioName = (EditText)
	// addRadioView.findViewById(R.id.device_radio_name_edittext);
	// addRadioAddress = (EditText)
	// addRadioView.findViewById(R.id.device_radio_address_edittext);
	// return addRadioView;
	// }

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,
			final AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		View contentView = inflater.inflate(R.layout.scene_task_control_radio_flower, null);
		final ListView radioTaskListView = (ListView) contentView.findViewById(R.id.device_radio_task_list);
		final LinearLayout radioTaskListlayout = (LinearLayout) contentView
				.findViewById(R.id.device_radio_task_list_layout);
		TextView emptyTextView = (TextView) contentView.findViewById(R.id.device_radio_task_list_empty);
		final List<FlowerRadioInfo> radioTaskList = getRadioTaskList();
		final RadioTaskListAdapter radioTaskListAdapter = new RadioTaskListAdapter(mContext, radioTaskList);
		if (radioTaskList != null) {
			emptyTextView.setVisibility(View.GONE);
			radioTaskListView.setVisibility(View.VISIBLE);
			radioTaskListView.setAdapter(radioTaskListAdapter);
		} else {
			radioTaskListView.setVisibility(View.GONE);
			emptyTextView.setVisibility(View.VISIBLE);
		}
		// 定义CompoundButton的List存储
		final List<CompoundButton> mButtons = new ArrayList<CompoundButton>();
		// 初始化TypedArray容器来存储资源中的array文件
		TypedArray tArray = getResources().obtainTypedArray(R.array.radiotaskMode);
		final int length = tArray.length();
		for (int i = 0; i < length; i++) {
			int id = tArray.getResourceId(i, 0);
			CompoundButton button = (CompoundButton) contentView.findViewById(id);
			button.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					for (int i = 0; i < length; i++) {
						if (arg0.getId() == mButtons.get(i).getId()) {
							mButtons.get(i).setChecked(true);
							if (i == 2) {
								radioTaskListlayout.setVisibility(View.GONE);
								if (radioTaskList != null && radioTaskList.size() > 0) {
									autoActionInfo.setEpData("H" + radioTaskList.get(0).getId());
									radioTaskListAdapter.setSelectRadioId(radioTaskList.get(0).getId(), true);
								} else {
									autoActionInfo.setEpData("Z0");
								}
							} else {
								if (mButtons.get(0).isChecked()) {
									autoActionInfo.setEpData("Y0");
								} else if (mButtons.get(1).isChecked()) {
									autoActionInfo.setEpData("Y1");
								} else if (mButtons.get(3).isChecked()) {
									autoActionInfo.setEpData("Z1");
								}
								radioTaskListAdapter.setSelectRadioId(null, false);
								radioTaskListAdapter.swapData(radioTaskList);
								radioTaskListlayout.setVisibility(View.VISIBLE);
								radioTaskListlayout.setEnabled(false);
							}
						} else {
							mButtons.get(i).setChecked(false);
						}
					}
				}
			});
			mButtons.add(button);
		}
		// 必须通过recycle方法来给定相应属性的值
		tArray.recycle();
		if (!StringUtil.isNullOrEmpty(epData)) {
			if (epData.startsWith("H")) {
				radioTaskListlayout.setVisibility(View.GONE);
				mButtons.get(2).setChecked(true);
				if (!radioMap.isEmpty() && radioMap.containsKey(epData.substring(1))) {
					radioTaskListAdapter.setSelectRadioId(epData.substring(1), true);
				}
			} else {
				if (StringUtil.equals(epData, "Z0")) {
					mButtons.get(2).setChecked(true);
				} else if (StringUtil.equals(epData, "Z1")) {
					mButtons.get(3).setChecked(true);
				} else if (StringUtil.equals(epData, "Y1")) {
					mButtons.get(1).setChecked(true);
				} else if (StringUtil.equals(epData, "Y0")) {
					mButtons.get(0).setChecked(true);
				}
				radioTaskListlayout.setVisibility(View.VISIBLE);
				radioTaskListlayout.setEnabled(false);
			}
		} else {
			radioTaskListlayout.setVisibility(View.VISIBLE);
			radioTaskListlayout.setEnabled(false);
		}
		radioTaskListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String selectId = radioTaskListAdapter.getData().get(position).getId();
				radioTaskListAdapter.setSelectRadioId(selectId, true);
				autoActionInfo.setEpData("H" + selectId);
			}
		});
		holder.setContentView(contentView);
		return holder;
	}

	private void loadRadioTaskMap() {
		String radioresult = preference.getString(gwID + IPreferenceKey.P_KEY_FLOWER_FM_DEVICE_MAP, "[]");
		List<FlowerRadioInfo> radioList = JSONArray.parseArray(radioresult, FlowerRadioInfo.class);
		for (FlowerRadioInfo info : radioList) {
			radioMap.put(info.getId(), info);
		}
	}

	private List<FlowerRadioInfo> getRadioTaskList() {
		List<FlowerRadioInfo> radioTaskList = CollectionsUtil.mapConvertToList(radioMap);
		Collections.sort(radioTaskList, comparator);
		return radioTaskList;
	}

	private class RadioTaskListAdapter extends WLBaseAdapter<FlowerRadioInfo> {

		private String selectTaskradioId;
		private boolean isRemind;

		public RadioTaskListAdapter(Context context, List<FlowerRadioInfo> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater, ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.scene_task_control_radio_adapter_layout, null);
		}

		@Override
		protected void bindView(Context context, View view, int pos, FlowerRadioInfo item) {
			TextView radioName = (TextView) view.findViewById(R.id.scene_task_radio_intem_textview);
			LinearLayout radioLayout = (LinearLayout) view.findViewById(R.id.scene_task_radio_intem_select_layout);
			radioName.setText(item.getName());
			if (isRemind) {
				if (StringUtil.equals(item.id, selectTaskradioId)) {
					radioLayout.setVisibility(View.VISIBLE);
					radioName.setTextColor(mContext.getResources().getColor(R.color.v5_green_light));
				} else {
					radioName.setTextColor(mContext.getResources().getColor(R.color.black));
					radioLayout.setVisibility(View.GONE);
				}
			} else {
				radioName.setTextColor(mContext.getResources().getColor(R.color.black));
				radioLayout.setVisibility(View.GONE);
			}

		}

		public void setSelectRadioId(String radioId, boolean isremind) {
			selectTaskradioId = radioId;
			isRemind = isremind;
			notifyDataSetChanged();
		}
	}

	// 获取插件
	private void getPlugin(final String entryPager) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext, pluginName, new PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file = new File(model.getFolder(), entryPager);
						String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
						if (file.exists()) {
							uri = "file:///" + file.getAbsolutePath();
						} else if (LanguageUtil.isChina()) {
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}

						if (radioJsonObject != null) {
							SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEPARAM,
									radioJsonObject.toJSONString());
						} else {
							SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEPARAM, "");
						}
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, ep);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPTYPE, epType);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
						IntentUtil.startHtml5PlusActivity(mContext, uri);
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if (hint != null && hint.length() > 0) {
							Handler handler = new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}
}
