package cc.wulian.app.model.device.impls.controlable.toc;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class TwoOutputDefaultSetFragment extends DialogFragment {
	private DeviceTwoOutputFragment mdtoFragment;
	private static String gwID;
	private static String devID;
	private WulianDevice device;
	private static final String TAG = TwoOutputDefaultSetFragment.class
			.getSimpleName();

	public static void showDefaultSetFragment(FragmentManager fm,
			FragmentTransaction ft, DeviceTwoOutputFragment dtoFragment,
			WulianDevice deviceInfo) {
		DialogFragment df = (DialogFragment) fm.findFragmentByTag(TAG);
		if (df != null) {
			if (!df.getDialog().isShowing()) {
				ft.remove(df);
			} else {
				return;
			}
		}

		gwID = deviceInfo.getDeviceGwID();
		devID = deviceInfo.getDeviceID();
		TwoOutputDefaultSetFragment fragment = new TwoOutputDefaultSetFragment();
		fragment.mdtoFragment = dtoFragment;
		fragment.setCancelable(false);
		fragment.show(ft.addToBackStack(TAG), TAG);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		device = DeviceCache.getInstance(getActivity()).getDeviceByID(
				getActivity(), gwID, devID);
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		return createDialog();
	}

	private View torDialog;
	private WLDialog dialog;
	private FrameLayout open1;
	private FrameLayout close1;
	private FrameLayout open2;
	private FrameLayout close2;
	private boolean isOpen1 = true;
	private boolean isOpen2 = true;

	private Dialog createDialog() {
		// 创建时获取当前状态
		SendMessage.sendControlDevMsg(gwID, devID, "14", device.getDeviceType(),
				"21");
		WLDialog.Builder builder = new WLDialog.Builder(getActivity());
		builder.setTitle(getResources().getString(R.string.device_modify_name));
		builder.setContentView(createCustomView());
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				String M = "0";
				String N = "0";
				if (isOpen1) {
					M = "0";
				} else {
					M = "1";
				}
				if (isOpen2) {
					N = "0";
				} else {
					N = "1";
				}
				String sendData = "1" + M + N;
				/**
				 * sharepreference存储
				 */
				Preference.getPreferences().saveTwoOutputSettingData(sendData);
				SendMessage.sendControlDevMsg(gwID, devID, "14", "A2", sendData);
				dialog.dismiss();

			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				dialog.dismiss();
			}

		});

		dialog = builder.create();
		return dialog;
	}

	private View createCustomView() {
		torDialog = View
				.inflate(
						getActivity(),
						R.layout.device_two_output_converter_default_setting_edit,
						null);
		open1 = (FrameLayout) torDialog
				.findViewById(R.id.device_two_output_default_setting_switch_open1);
		close1 = (FrameLayout) torDialog
				.findViewById(R.id.device_two_output_default_setting_switch_close1);
		open2 = (FrameLayout) torDialog
				.findViewById(R.id.device_two_output_default_setting_switch_open2);
		close2 = (FrameLayout) torDialog
				.findViewById(R.id.device_two_output_default_setting_switch_close2);
		open1.setOnClickListener(mClickListener);
		close1.setOnClickListener(mClickListener);
		open2.setOnClickListener(mClickListener);
		close2.setOnClickListener(mClickListener);

		getCurrentEpData();
		return torDialog;

	}

	private OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == open1) {
				isOpen1 = false;
				open1.setVisibility(View.GONE);
				close1.setVisibility(View.VISIBLE);

			} else if (arg0 == close1) {
				isOpen1 = true;
				open1.setVisibility(View.VISIBLE);
				close1.setVisibility(View.GONE);

			} else if (arg0 == open2) {
				isOpen2 = false;
				open2.setVisibility(View.GONE);
				close2.setVisibility(View.VISIBLE);

			} else if (arg0 == close2) {
				isOpen2 = true;
				open2.setVisibility(View.VISIBLE);
				close2.setVisibility(View.GONE);

			}

		}
	};

	public void getCurrentEpData() {
		/**
		 * sharepreference
		 */
		String getEpData = Preference.getPreferences()
				.getTwoOutputSettingData();
		if (getEpData != null && getEpData.length() == 3) {

			if (getEpData.substring(1, 2).equals("0")) {
				isOpen1 = true;
				open1.setVisibility(View.VISIBLE);
				close1.setVisibility(View.GONE);
			} else {
				isOpen1 = false;
				open1.setVisibility(View.GONE);
				close1.setVisibility(View.VISIBLE);
			}
			if (getEpData.substring(2, 3).equals("0")) {
				isOpen2 = true;
				open2.setVisibility(View.VISIBLE);
				close2.setVisibility(View.GONE);
			} else {
				isOpen2 = false;
				open2.setVisibility(View.GONE);
				close2.setVisibility(View.VISIBLE);
			}
		}
		// 获取相应数据
		WL_A2_Two_Output_Converter curDevice = (WL_A2_Two_Output_Converter) device;
		String curEpData = curDevice.getCurDefaultSettingData();
		if (StringUtil.isNullOrEmpty(curEpData)) {
			return;
		} else if (curEpData.startsWith("01")) {
			String setOneData = curEpData.substring(3, 4);
			String setTwoData = curEpData.substring(5, 6);
			if (setOneData.equals("0")) {
				isOpen1 = true;
				open1.setVisibility(View.VISIBLE);
				close1.setVisibility(View.GONE);
			} else if (setOneData.equals("1")) {
				isOpen1 = false;
				open1.setVisibility(View.GONE);
				close1.setVisibility(View.VISIBLE);
			}
			if (setTwoData.equals("0")) {
				isOpen2 = true;
				open2.setVisibility(View.VISIBLE);
				close2.setVisibility(View.GONE);
			} else if (setTwoData.equals("1")) {
				isOpen2 = false;
				open2.setVisibility(View.GONE);
				close2.setVisibility(View.VISIBLE);
			}
			torDialog.invalidate();

		} else if (curEpData.startsWith("02")) {
			String readOneData = curEpData.substring(3, 4);
			String readTwoData = curEpData.substring(5, 6);
			if (readOneData.equals("0")) {
				isOpen1 = true;
				open1.setVisibility(View.VISIBLE);
				close1.setVisibility(View.GONE);
			} else if (readOneData.equals("1")) {
				isOpen1 = false;
				open1.setVisibility(View.GONE);
				close1.setVisibility(View.VISIBLE);
			}
			if (readTwoData.equals("0")) {
				isOpen2 = true;
				open2.setVisibility(View.VISIBLE);
				close2.setVisibility(View.GONE);
			} else if (readTwoData.equals("1")) {
				isOpen2 = false;
				open2.setVisibility(View.GONE);
				close2.setVisibility(View.VISIBLE);
			}
			torDialog.invalidate();
		}
	}

}
