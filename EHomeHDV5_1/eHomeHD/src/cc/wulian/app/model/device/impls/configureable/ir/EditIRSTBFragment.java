package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportSTB;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse.Box;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.STBParse.STB;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditIRSTBFragment extends WulianFragment {

	public static final String EXTRA_DEVICE_IR_STB = "EXTRA_DEVICE_IR_STB";
	@ViewInject(R.id.device_ir_setting_brand_spinner)
	private Spinner brandSpinner;
	@ViewInject(R.id.device_ir_setting_check_left_btn)
	private ImageButton leftBtn;
	@ViewInject(R.id.device_ir_setting_check_tv)
	private TextView checkTextView;
	@ViewInject(R.id.device_ir_setting_check_right_btn)
	private ImageButton rightBtn;
	@ViewInject(R.id.device_ir_setting_power_btn)
	private ImageButton powerBtn;
	@ViewInject(R.id.device_ir_setting_add_btn)
	private ImageButton addBtn;
	@ViewInject(R.id.device_ir_setting_minus_btn)
	private ImageButton minusBtn;
	private IRSupportSTB stbSupport;
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;
	private WulianDevice irDevice;
	private IRSTBAdapter stbAdapter;
	private List<String> codes;
	private Preference preference = Preference.getPreferences();
	private OnClickListener clickListner = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == leftBtn) {
				if (codes == null)
					return;
				int index = StringUtil.toInteger(checkTextView.getTag());
				index = (index + codes.size() - 1) % codes.size();
				checkTextView.setTag(index);
				String brandCode = codes.get(index);
				checkTextView.setText(brandCode);
				keySearch(brandCode);
			} else if (v == rightBtn) {
				if (codes == null)
					return;
				int index = StringUtil.toInteger(checkTextView.getTag());
				index = (index + 1) % codes.size();
				checkTextView.setTag(index);
				String brandCode = codes.get(index);
				checkTextView.setText(brandCode);
				keySearch(brandCode);
			} else if (v == powerBtn) {
				keyTest(CmdUtil.IR_GENERAL_KEY_611);
			} else if (v == addBtn) {
				keyTest("014");
			} else if (v == minusBtn) {
				keyTest("015");
			}
		}
	};

	private void keyTest(String keyCode) {
		StringBuilder sb = new StringBuilder();
		sb.append(CmdUtil.IR_MODE_CTRL);
		String code = checkTextView.getText().toString();
		String keySet = deviceIRInfo.getIRType() + code;
		sb.append(keySet);
		sb.append(keyCode);
		String sendData = sb.toString();
		SendMessage.sendControlDevMsg(deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(), ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL, sendData);
	}
	private void keySearch(String brandCode){
		SendMessage.sendControlDevMsg(deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(), ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL, 3+deviceIRInfo.getIRType()+brandCode);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		deviceIRInfo = (DeviceIRInfo) bundle
				.getSerializable(EXTRA_DEVICE_IR_STB);
		if(deviceIRInfo == null)
			mActivity.finish();
		irDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, deviceIRInfo.getGwID(), deviceIRInfo.getDeviceID());
		initBar();
		stbSupport = IRSupportSTB.getInstance(mActivity);
		STB stb = stbSupport
				.getSupportSTBsInArea(preference.getSTBLocation().getProvinceCode());
		if (stb == null) {
			stb = stbSupport.getSupportSTBsInArea("10");
		}
		stbAdapter = new IRSTBAdapter(mActivity, stb.getBoxs());
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
						saveIRSTB();
						mActivity.finish();
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.device_ir_air_setting_content,
				null);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		brandSpinner.setAdapter(stbAdapter);
		initListeners();
		initValues();
	}

	private void initValues() {
		brandSpinner.setSelection(0);
	}

	private void initListeners() {
		leftBtn.setOnClickListener(clickListner);
		rightBtn.setOnClickListener(clickListner);
		powerBtn.setOnClickListener(clickListner);
		addBtn.setOnClickListener(clickListner);
		minusBtn.setOnClickListener(clickListner);
		brandSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int position, long id) {
				codes = stbAdapter.getItem(position).getCodes();
				if (codes.size() > 0) {
					checkTextView.setText(codes.get(0));
					checkTextView.setTag(0);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}

		});
	}

	private void saveIRSTB() {
		Box box = stbAdapter.getItem(brandSpinner.getSelectedItemPosition());
		String name = box.getName();
		String code = checkTextView.getText().toString();
		deviceIRInfo.setName(name);
		deviceIRInfo.setCode(code);
		deviceIRInfo.setKeyset(deviceIRInfo.getIRType()
				+ deviceIRInfo.getCode());
		deviceIRInfo.setStatus(DeviceIRInfo.STATUS_STUDYED);
		ArrayList<DeviceIRInfo> irInfos = new ArrayList<DeviceIRInfo>();
		irInfos.add(deviceIRInfo);
		JsonTool.saveIrInfoBath(mActivity, deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(), irInfos,
				deviceIRInfo.getIRType());
	}

	public class IRSTBAdapter extends WLBaseAdapter<Box> {

		public IRSTBAdapter(Context context, List<Box> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_air_setting_brand_item,
					null);
		}

		@Override
		protected void bindView(Context context, View view, int pos, Box item) {
			TextView name = (TextView) view
					.findViewById(R.id.device_ir_setting_brand_tv);
			name.setText(item.getName());
		}

	}
}
