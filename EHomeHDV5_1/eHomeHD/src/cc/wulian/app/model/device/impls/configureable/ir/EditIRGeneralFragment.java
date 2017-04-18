package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DeviceIREvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class EditIRGeneralFragment extends WulianFragment {

	public static final String EXTRA_DEVICE_IR_GENERAL = "EXTRA_DEVICE_IR_GENERAL";
	private ListView editListView;
	private ImageView addImageView;
	private IRKeyAdapter irAdapter;
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;

	private WulianDevice irDevice;
	private String devEpType = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		deviceIRInfo = (DeviceIRInfo) bundle
				.getSerializable(EXTRA_DEVICE_IR_GENERAL);
		if(deviceIRInfo == null)
			mActivity.finish();
		irDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, deviceIRInfo.getGwID(), deviceIRInfo.getDeviceID());
		devEpType = irDevice.getDeviceInfo().getDevEPInfo().getEpType();
		irAdapter = new IRKeyAdapter(mActivity, getIREntites());
		initBar();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_ir_setting));
		getSupportActionBar().setRightIconText(
				getResources().getString(R.string.device_ir_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						saveIRKeys();
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return inflater.inflate(R.layout.device_ir_genenral_setting_content,
				null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		editListView = (ListView) view.findViewById(R.id.device_ir_general_lv);
		addImageView = (ImageView) view
				.findViewById(R.id.device_ir_general_setting_add_iv);
		editListView.setAdapter(irAdapter);
		addImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				irAdapter.newDeviceIR();
				editListView.setSelection(irAdapter.getCount());
			}
		});
	}

	private void saveIRKeys() {
		JsonTool.saveIrInfoBath(mActivity, deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(),
				irAdapter.getData(), deviceIRInfo.getIRType());
	}

	private List<DeviceIRInfo> getIREntites() {
		return irDao.findListAll(deviceIRInfo);
	}

	public void onEventMainThread(DeviceEvent event) {
		DeviceInfo info = event.deviceInfo;

		if (info != null && info.getGwID().equals(deviceIRInfo.getGwID())
				&& info.getDevID().equals(deviceIRInfo.getDeviceID())
				&& info.getDevEPInfo().getEp().equals(deviceIRInfo.getEp())) {
			String epData = info.getDevEPInfo().getEpData();
			String type = info.getType();
			irAdapter.makeKeyStudyed(type, epData);
		}
	}
	public void onEventMainThread(DeviceIREvent event) {
		if(getIREntites().size() == irAdapter.getCount()){
			irAdapter.swapData(getIREntites());
			mActivity.finish();
		}
	}

	public class IRKeyAdapter extends WLBaseAdapter<DeviceIRInfo> {
		private int index = -1;
		public IRKeyAdapter(Context context, List<DeviceIRInfo> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.device_ir_genenral_key_setting_item, null);
		}

		@Override
		protected void bindView(Context context, View view, final int pos,
				final DeviceIRInfo item) {
			TextView keyCodeTextView = (TextView) view
					.findViewById(R.id.device_ir_setting_code_tv);
			keyCodeTextView.setText(item.getCode());
			final EditText editText = (EditText) view
					.findViewById(R.id.device_ir_key_name_et);
			editText.setText(item.getName());
			editText.setOnFocusChangeListener(new OnFocusChangeListener() {

				@Override
				public void onFocusChange(View v, boolean hasFocus) {
					if (!hasFocus) {
						item.setName(editText.getText().toString());
					}
				}
			});
			editText.setOnTouchListener(new View.OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = pos;
					}
					return false;
				}
			});

			if (DeviceIRInfo.STATUS_STUDYED.equals(item.getStatus())) {
				editText.setBackgroundResource(R.drawable.device_ir_setting_check_bg);
			} else {
				editText.setBackgroundResource(R.color.white);
			}
			editText.clearFocus();
			if (index!=-1 && index== pos){
				editText.requestFocus();
			}
			editText.setSelection(editText.getText().length());
			ImageButton editBtn = (ImageButton) view
					.findViewById(R.id.device_ir_key_edit_ib);
			editBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					study(pos);
				}
			});
			ImageButton deleteBtn = (ImageButton) view
					.findViewById(R.id.device_ir_key_delete_ib);
			deleteBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					deleteDeviceIR(pos);
				}
			});
		}

		public void study(int position) {
			DeviceIRInfo info = getData().get(position).clone();
			StringBuilder sb = new StringBuilder();
			sb.append(CmdUtil.IR_MODE_STUDY);
			if(AbstractIRView.TYPE_GENERAL.equals(info.getIRType())){
				sb.append(info.getIRType()+"000"+info.getCode());
			}else{
				sb.append(info.getCode());
			}
			String sendData = sb.toString();
			info.setStatus(DeviceIRInfo.STATUS_STUDYED);
			SendMessage.sendControlDevMsg(info.getGwID(), info.getDeviceID(),
					info.getEp(), devEpType, sendData);
		}

		public void deleteDeviceIR(int position) {
			getData().remove(position);
			notifyDataSetChanged();
		}


		public void newDeviceIR() {
			DeviceIRInfo newInfo = null;
			for (int code = 511; code < 610; code++) {
				int i = 0;
				while (i < getCount()) {
					DeviceIRInfo info = getItem(i);
					if (info.getCode().equals(code + "")) {
						break;
					}
					i++;
				}
				if (i >= getCount()) {
					newInfo = new DeviceIRInfo();
					newInfo.setCode(code + "");
					newInfo.setDeviceID(deviceIRInfo.getDeviceID());
					newInfo.setGwID(deviceIRInfo.getGwID());
					newInfo.setIRType(deviceIRInfo.getIRType());
					newInfo.setEp(deviceIRInfo.getEp());
					if (!StringUtil.isNullOrEmpty(newInfo.getIRType())
							&& !newInfo.getIRType().equals("-1")) {
						newInfo.setKeyset(newInfo.getIRType()
								+ "000"+newInfo.getCode());
					} else {
						newInfo.setKeyset(newInfo.getCode());
					}
					newInfo.setStatus(DeviceIRInfo.STATUS_NO_STUDY);
					newInfo.setName(newInfo.getCode());
					break;
				}
			}
			if (newInfo != null) {
				this.getData().add(newInfo);
				notifyDataSetChanged();
			}
		}

		public void makeKeyStudyed(String type, String epData) {
			if (ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL.equals(type)) {
				if (!StringUtil.isNullOrEmpty(epData)
						&& epData.startsWith(CmdUtil.IR_MODE_STUDY)) {
					notifyStudyed(epData.substring(1));
				}
			} else if (ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL.equals(type)) {
				if (!StringUtil.isNullOrEmpty(epData)
						&& epData.startsWith("0" + CmdUtil.IR_MODE_STUDY)) {
					String irType = epData.substring(2, 4);
					int code = StringUtil.toInteger(epData.substring(8), 16);
					notifyStudyed(irType+"000"+ StringUtil.appendLeft(code + "", 4, '0'));
				}
			}

		}

		private void notifyStudyed(String keySet) {
			for (int i = 0; i < irAdapter.getCount(); i++) {
				DeviceIRInfo irInfo = irAdapter.getItem(i);
				if (keySet.endsWith(irInfo.getKeyset())) {
					irInfo.setStatus(CmdUtil.IR_STATUS_STUDY);
					notifyDataSetChanged();
					break;
				}
			}
		}
	}
}
