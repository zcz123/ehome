package cc.wulian.smarthomev5.fragment.more.nfc;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.NFCConfigureAdapter;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.NFCEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.yuantuo.customview.ui.CustomProgressDialog;
import com.yuantuo.customview.ui.CustomProgressDialog.OnDialogDismissListener;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLListViewBuilder;

public class NFCAddDeviceFragment extends WulianFragment {
	
	boolean isAddDoorLockDevice = false;// 是否增添了门锁的安防设备为NFC标签内容
	private String NFC_ADD_DEVICE_KEY = "nfc_add_device_key";
	private WLDialog mMessageDialog;
	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo gatewayInfo = accountManager.getmCurrentInfo();
	private List<NFCControlItem> items;
	private List<NFCEntity> doorLockItems;
	private NFCEntity currentDataItem;
	private boolean isClickAdd = false;// 判断是否点击过添加按钮
	public static final String WINDOWS_PWD = "1111";
	public static final String WINDOWS_PWD_MD5 = MD5Util.encrypt(WINDOWS_PWD);

	private NFCConfigureAdapter mNFCConfigureAdapter;
	private LinearLayout nfcDevicesContentLineLayout;
	private WLListViewBuilder listViewBuilder;
	private RelativeLayout mDeviceLayout;
	private NFCManager nfcManager = NFCManager.getInstance();
	public OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.more_add_device_ll:
				isClickAdd=true;
				FragmentManager fm = NFCAddDeviceFragment.this.getActivity()
						.getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				AddDeviceToNFCFragmentDialog.showDeviceDialog(fm, ft);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNFCConfigureAdapter = new NFCConfigureAdapter(mActivity, getNFCItems());
		listViewBuilder = new WLListViewBuilder(mActivity);
		listViewBuilder.setAdapter(mNFCConfigureAdapter);
		doorLockItems=new ArrayList<NFCEntity>();
	}

	private List<NFCControlItem> getNFCItems() {
		if(doorLockItems!=null){
			doorLockItems.clear();			
		}
		if (items == null) {
			items = new ArrayList<NFCControlItem>();
		} else {
			items.clear();
		}
		List<NFCEntity> infos = nfcManager.getDeviceNFCEntitys();
		for (int i = 0; i < infos.size(); i++) {
			if (NFCEntity.TYPE_DEVICE.equals(infos.get(i).getType())) {
				if (isDoorLockDevice(infos.get(i))) {
					System.out.println("-------------" + "isdoor");
					if (!isClickAdd) {
						items.add(new NFCControlItem(mActivity, infos.get(i)));
					}else{
						checkPassword(infos.get(i));
					}
				} else {
					items.add(new NFCControlItem(mActivity, infos.get(i)));
				}
			}
		}
		return items;
	}
	private void checkPassword(NFCEntity nfcEntity) {
			showTipsDialog(nfcEntity);
	}

	private void showTipsDialog(final NFCEntity currentData) {
		WLDialog.Builder builder = new Builder(mActivity);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.nfc_add_device_door_lock_dialog,
				null);
		final EditText editText = (EditText) view
				.findViewById(R.id.nfc_add_device_door_lock_dialog_edittext);
		DeviceCache mDeviceCache = DeviceCache.getInstance(mActivity);
		WulianDevice mDevice = mDeviceCache.getDeviceByID(mActivity, gatewayInfo.getGwID(), currentData.getID());
		String deviceName=DeviceTool.getDeviceShowName(mDevice);
		System.out.println("--------------------"+DeviceTool.getDeviceShowName(mDevice));
		builder.setContentView(view).setTitle(getResources().getString(R.string.more_nfc_check_door_lock_password_head_dialog)+deviceName+getResources().getString(R.string.more_nfc_check_door_lock_password_end_dialog))
				.setPositiveButton(getResources().getString(R.string.html_disk_format_confirm)).setNegativeButton(null)
				.setCancelOnTouchOutSide(false)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						String confirmPwd = editText.getText().toString();
						if (currentData.getEpType().equals("70")) {
							currentDataItem=currentData;
							String passwordString = "9" + confirmPwd.length()
									+ confirmPwd;
							mDialogManager.showDialog(NFC_ADD_DEVICE_KEY,
									mActivity, null, new OnDialogDismissListener(){

										@Override
										public void onDismiss(
												CustomProgressDialog progressDialog,
												int result) {
											if(result==-1){
												showErrorDialog();
												nfcManager.removeDeviceInfo(currentDataItem);
												mNFCConfigureAdapter.swapData(items);
											}
										}
								
							});
							SendMessage.sendControlDevMsg(gatewayInfo.getGwID(),
									currentData.getID(), currentData.getEp(),
									currentData.getEpType(), passwordString);
						} else {
							String confirmdoorpwd = MD5Util.encrypt(confirmPwd);
							String savedMD5Pwd = Preference
									.getPreferences()
									.getString(
											IPreferenceKey.P_KEY_DEVICE_DOOR_LOCK_PWD,
											WINDOWS_PWD_MD5);
							if (confirmdoorpwd.equals(savedMD5Pwd)) {
								items.add(new NFCControlItem(mActivity,
										currentData));
								mNFCConfigureAdapter.swapData(items);
							} else {
								showErroeTipsDialog(currentData);
							}
						}

					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						// TODO Auto-generated method stub
					}
				});
		mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	private void showErrorDialog() {
		Builder builder = new Builder(mActivity);
		builder.setNegativeButton(null);
		builder.setPositiveButton(R.string.switch_off);
		builder.setContentView(R.layout.dialog_error_content);
		WLDialog  dialog = builder.create();
		dialog.show();
	}
	
	private void showErroeTipsDialog(final NFCEntity currentData2) {
		WLDialog.Builder builder = new Builder(mActivity);
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(R.layout.nfc_add_device_door_lock_dialog,
				null);
		final EditText editText = (EditText) view
				.findViewById(R.id.nfc_add_device_door_lock_dialog_edittext);
		TextView textView = (TextView) view
				.findViewById(R.id.nfc_add_device_door_lock_dialog_tv);
		editText.setVisibility(View.GONE);
		textView.setText(getResources().getString(R.string.more_nfc_check_error_dialog));
		builder.setContentView(view).setTitle(getResources().getString(R.string.gateway_router_setting_dialog_toast))
				.setPositiveButton(getResources().getString(R.string.more_nfc_error_put_again_dialog)).setNegativeButton(getResources().getString(R.string.cancel))
				.setCancelOnTouchOutSide(false)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						showTipsDialog(currentData2);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						// TODO Auto-generated method stub
						nfcManager.removeDeviceInfo(currentData2);
						mNFCConfigureAdapter.swapData(items);
						mMessageDialog.dismiss();
					}
				});
		mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	private boolean isDoorLockDevice(NFCEntity infos) {
		// TODO Auto-generated method stub
		return infos.getEpType().equals("70") || infos.getEpType().equals("69");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.more_add_device, container,
				false);
		ViewUtils.inject(this, rootView);
		initBar();
		return rootView;
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.more_nfc_function));
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(R.string.nav_device_title));
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mDeviceLayout = (RelativeLayout) view
				.findViewById(R.id.more_add_device_ll);
		nfcDevicesContentLineLayout = (LinearLayout) view
				.findViewById(R.id.device_content);
		mDeviceLayout.setOnClickListener(listener);
		nfcDevicesContentLineLayout.addView(listViewBuilder.create());

	}
	
	public void onEventMainThread(NFCEvent event) {

		if ((event.action + "").equals(CmdUtil.MODE_ADD)) {
			mNFCConfigureAdapter.swapData(getNFCItems());
		} else if ((event.action + "").equals(CmdUtil.MODE_DEL)) {
			nfcManager.removeDeviceInfo(event.mifareSectorInfo);
			mNFCConfigureAdapter.swapData(getNFCItems());
		}
	}
	public void onEventMainThread(DeviceEvent event) {
		System.out.println("-----------"
				+ event.deviceInfo.getDevEPInfo().getEpData());
		mDialogManager.dimissDialog(NFC_ADD_DEVICE_KEY, 0);
		if (event.deviceInfo.getDevEPInfo().getEpData().equals("144")) {
			items.add(new NFCControlItem(mActivity, currentDataItem));
			mNFCConfigureAdapter.swapData(items);
		} else if (event.deviceInfo.getDevEPInfo().getEpData().equals("145")) {
			showErroeTipsDialog(currentDataItem);
		}
	}
}
