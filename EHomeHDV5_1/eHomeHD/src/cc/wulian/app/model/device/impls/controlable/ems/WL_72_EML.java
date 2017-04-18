package cc.wulian.app.model.device.impls.controlable.ems;

import java.io.File;

import com.yuantuo.customview.ui.WLDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * <p>
 * send control : <br>
 * 10:关 <br>
 * 11:开 <br>
 * 12:查询 <br>
 * 13:切换 <br>
 * 2：读所有计量数据 3xxxx:设置最大保护功率，xxxx:最大功率保护关断功率。如3000表示3000W时自动关断 范围500~6000,默认6KW
 * 4xxxx:设置主动上报的功率阀值。xxxx:功率阀值上报最小10W.默认10W变化才上报 5：消除过流报警
 * <p>
 * receive data : (十六进制) <br>
 * TT01xxaaaayyyyzzzzzz: TT代表设备类型，01表示当前状态 xx表示开关状态（00：关，01：开）
 * aaaa表示当前电流（扩大100倍） yyyy表示当前功率（单位W，0x0000-0xFFFF）；
 * zzzzzz表示当前累计电量（单位WH，0x000000-0xF42400，最大16000KWH）；
 * TT03xxxx：01代表设置最大保护功率的命令码，xxxx表示设置的最大功率值（默认6000W）
 * TT04xxxx：设备的状态上报机制：02代表设置上报功率阀值命令码，xxxx表示设置的功率阀值（默认10W） TT05xx：代表过流报警识别
 * xx：00表示消除报警，01报警
 * 
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_EML_1 }, category = Category.C_CONTROL)
public class WL_72_EML extends WL_15_Ems {

	private static final String DATA_CTRL_PREFIX_3001 = "3001"; // 30:设备类型，01：当前状态

	private static final String DATA_CTRL_PREFIX_01 = "01"; // 最大的保护功率
	private static final String DATA_CTRL_PREFIX_02 = "02"; //

	private static final String DATA_PROTOCOL_OPEN = DATA_CTRL_PREFIX_3001
			+ DATA_CTRL_STATE_OPEN_01;
	private static final String DATA_PROTOCOL_CLOSE = DATA_CTRL_PREFIX_3001
			+ DATA_CTRL_STATE_CLOSE_00;
	// 功率阀值

	// 小图
	private static int SMALL_OPEN_D = R.drawable.device_button_1_open;
	private static int SMALL_CLOSE_D = R.drawable.device_button_1_close;

	// 大图
	private static int BIG_OPEN_D = R.drawable.device_measure_switch_open;
	private static int BIG_CLOSE_D = R.drawable.device_measure_switch_close;
	
	//有效电流
	private  static final int DATA_WL_72_ELECTRIC_MIN = 0; 
	private  static final int DATA_WL_72_ELECTRIC_MIDDLE = 1800; 
	private  static final int DATA_WL_72_ELECTRIC_BIG =3000; 

	private String mLastEpData; // EpData
	private String mControlMode; // 开关状态
	private int mW; // 当前功率 单位W
	private String mKWH; // 累计电量 单位WH
	private int mA; // 当前电流
	private TextView now_power;
	private TextView all_electic;
	private WLDialog dialog;
	private ImageView mImageView;
	private LinearLayout timeLinearLayout;
	private LinearLayout historyLinearLayout;
	private Button timebuButton;
	private Button historybButton;
	private H5PlusWebView webView;
	private String pluginName = "16Akey.zip";

	public WL_72_EML(Context context, String type) {
		super(context, type);
	}

	// 刷新设备状态
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}

	private void disassembleCompoundCmd(String epData) {
		if (isNull(epData))
			return;
		if (epData.length() < 4)
			return;

		if (isSameAs(epData, this.mLastEpData))
			return;
		if (!epData.startsWith(DATA_CTRL_PREFIX_3001)
				&& !epData.startsWith(DATA_CTRL_PREFIX_01)
				&& !epData.startsWith(DATA_CTRL_PREFIX_02))
			return;

		if (epData.startsWith(DATA_CTRL_PREFIX_3001)) {
			String mode = this.substring(epData, 4, 6);
			String w = this.substring(epData, 10, 14);
			String a = this.substring(epData, 6, 10);
			String kwh = this.substring(epData, 14, 20);

			this.mControlMode = mode;
			this.mW = StringUtil.toInteger(w, 16);
			this.mA = (StringUtil.toInteger(a, 16));
			java.text.DecimalFormat   df=new   java.text.DecimalFormat("######0.00"); 
			this.mKWH =df.format(Math.round(StringUtil.toInteger(kwh, 16) + 0.5D) / 1000D);
			
		}
	}

	// 获取状态图标
	@Override
	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	@Override
	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}

	@Override
	public int getOpenBigPic() {
		return BIG_OPEN_D;
	}

	@Override
	public int getCloseBigPic() {
		return BIG_CLOSE_D;
	}

	@Override
	public boolean isOpened() {
		if (isNull(epData))
			return false;

		return epData.startsWith(DATA_PROTOCOL_OPEN);
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData))
			return true;
		return epData.startsWith(DATA_PROTOCOL_CLOSE);
	}

	/**
	 * 初始化界面
	 */
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = inflater.inflate(R.layout.device_measure_switch_one,
				container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		mImageView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		now_power = (TextView) view
				.findViewById(R.id.device_measure_switch_now_power);
		all_electic = (TextView) view
				.findViewById(R.id.device_measure_switch_all_electic);
		timeLinearLayout = (LinearLayout) view
				.findViewById(R.id.device_measure_switch_now_time);
		historyLinearLayout = (LinearLayout) view
				.findViewById(R.id.device_measure_switch_history);
		timebuButton = (Button) view
				.findViewById(R.id.device_measure_switch_now_button);
		historybButton = (Button) view
				.findViewById(R.id.device_measure_switch_history_button);
		webView = (H5PlusWebView) view
				.findViewById(R.id.measure_switch_webview);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID,
				this.gwID);
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID,
				this.devID);
		Engine.bindWebviewToContainer(
				(H5PlusWebViewContainer) this.getCurrentFragment(), webView);
//		if (!Preference.getPreferences().getPMHtmlUri().equals("noUri")) {
//			webView.loadUrl(Preference.getPreferences().getPMHtmlUri());
//		}else{
//			getPlugin();
//		}
		getPlugin();
		mImageView.setImageDrawable(getStateBigPictureArray()[0]);
		timebuButton.setOnClickListener(mOnClickListener);
		historybButton.setOnClickListener(mOnClickListener);
		mImageView.setOnClickListener(mOnClickListener);
		mViewCreated = true;
	}

	private OnClickListener mOnClickListener = new OnClickListener() {
		public void onClick(View v) {
			if (v == timebuButton) {
				historybButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				timebuButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				timeLinearLayout.setVisibility(View.VISIBLE);
				historyLinearLayout.setVisibility(View.GONE);
			} else if (v == historybButton) {
				timebuButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				historybButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				historyLinearLayout.setVisibility(View.VISIBLE);
				timeLinearLayout.setVisibility(View.GONE);
				webView.setVisibility(View.VISIBLE);

			} else if (v == mImageView) {
				if ("00".equals(epData.substring(4, 6))) {
					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,
							"11", true);

				} else if ("01".equals(epData.substring(4, 6))) {
					now_power.setText(0 + "");
					now_power
							.setBackgroundResource(R.drawable.device_measure_switch_green);
					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,
							"10", true);
				}
			}
		};
	};

	@Override
	public void initViewStatus() {
		mImageView.setImageDrawable(getStateBigPictureArray()[0]);
		if (mA >= DATA_WL_72_ELECTRIC_MIN && mA < DATA_WL_72_ELECTRIC_MIDDLE) {
			now_power.setText(mW + "");
			all_electic.setText(mKWH + "");
		}
		if (mA >= DATA_WL_72_ELECTRIC_MIDDLE && mA <= DATA_WL_72_ELECTRIC_BIG) {
			now_power
					.setBackgroundResource(R.drawable.device_measure_switch_orange);
			now_power.setText(mW + "");
			all_electic.setText(mKWH + "");
		}
		if (mA > DATA_WL_72_ELECTRIC_BIG) {
			now_power
					.setBackgroundResource(R.drawable.device_measure_switch_red);

			WLDialog.Builder builder = new WLDialog.Builder(mContext);
			builder.setTitle(mContext.getResources().getString(
					R.string.device_songname_refresh_title));
			builder.setMessage(getResources().getString(R.string.device_72_now_power_disconnect));
			builder.setPositiveButton(android.R.string.ok);
			dialog = builder.create();
			dialog.show();
		}
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_SEND_CMD_OPEN_11;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_SEND_CMD_CLOSE_10;
	}

	private void getPlugin() {
		new Thread(new Runnable() {
			@Override
			public void run() {

				((Activity) mContext).runOnUiThread(new Runnable() {

					@Override
					public void run() {

					}
				});

				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext, pluginName,
						new PluginsManagerCallback() {

							@Override
							public void onGetPluginSuccess(PluginModel model) {
								File file = new File(model.getFolder(),
										"Switch.html");
								String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
								if (file.exists()) {
									uri = "file:///" + file.getAbsolutePath();
								} else if (LanguageUtil.isChina()) {
									uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
								}
								final String uriString = uri;
								Preference.getPreferences().savePMHtmlUri(uri);
								Handler handler = new Handler(Looper
										.getMainLooper());
								handler.post(new Runnable() {
									@Override
									public void run() {
										webView.loadUrl(uriString);
									}
								});
							}

							@Override
							public void onGetPluginFailed(final String hint) {
								if (hint != null && hint.length() > 0) {
									Handler handler = new Handler(Looper
											.getMainLooper());
									handler.post(new Runnable() {
										@Override
										public void run() {
											Toast.makeText(mContext, hint,
													Toast.LENGTH_SHORT).show();
										}
									});
								}
							}
						});
			}
		}).start();
	}
}
