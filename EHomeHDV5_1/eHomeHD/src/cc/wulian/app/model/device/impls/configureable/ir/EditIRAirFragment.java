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
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Power;
import cc.wulian.app.model.device.impls.configureable.ir.xml.ACCommand.Temp;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportACBrand;
import cc.wulian.app.model.device.impls.configureable.ir.xml.parse.BrandParse.ACBrand;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditIRAirFragment extends WulianFragment {

	
	public static final String EXTRA_DEVICE_IR_AIR = "EXTRA_DEVICE_IR_AIR";
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
	private IRSupportACBrand brandSupport;
	private DeviceIRInfo deviceIRInfo;
	private IRBrandAdapter brandAdapter;
	private List<String> codes;
	private int mCurrentTempIndex = -1;
	private int mPowerIndex = -1;
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
				boolean over = Power.checkOverRange(mPowerIndex);
				if (over) {
					mPowerIndex = 0;
				}
				String cmd = Power.getCommand(mPowerIndex).getCmd();
				keyTest(cmd);
				mPowerIndex++;
			} else if (v == addBtn) {
				mCurrentTempIndex++;
				boolean over = Temp.checkOverRange(mCurrentTempIndex);
				if (over) {
					mCurrentTempIndex = 0;
				}
				String cmd = Temp.getCommand(mCurrentTempIndex).getCmd();
				keyTest(cmd);
			} else if (v == minusBtn) {
				mCurrentTempIndex--;
				boolean over = Temp.checkOverRange(mCurrentTempIndex);
				if (over) {
					mCurrentTempIndex = 0;
				}
				String cmd = Temp.getCommand(mCurrentTempIndex).getCmd();
				keyTest(cmd);
			}
		}

	};

	private void keySearch(String brandCode){
		SendMessage.sendControlDevMsg(deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(), ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL, 3+deviceIRInfo.getIRType()+brandCode);
	}
	private void keyTest(String cmd) {

		StringBuilder sb = new StringBuilder();
		sb.append(CmdUtil.IR_MODE_CTRL);
		String code = checkTextView.getText().toString();
		String keySet = deviceIRInfo.getIRType() + code;
		sb.append(keySet);
		sb.append(cmd);
		String sendData = sb.toString();
		SendMessage.sendControlDevMsg(deviceIRInfo.getGwID(),
				deviceIRInfo.getDeviceID(), deviceIRInfo.getEp(), "", sendData);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		deviceIRInfo = (DeviceIRInfo) bundle
				.getSerializable(EXTRA_DEVICE_IR_AIR);
		if(deviceIRInfo == null)
			mActivity.finish();
		initBar();
		brandSupport = IRSupportACBrand.getInstance(mActivity);
		brandAdapter = new IRBrandAdapter(mActivity,
				brandSupport.getSupportBrandList());
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
						saveIRAir();
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
		brandSpinner.setAdapter(brandAdapter);
		initListeners();
		initValues();
	}

	private void initValues() {
		if(deviceIRInfo != null && !StringUtil.isNullOrEmpty(deviceIRInfo.getCode())){
			brandSpinner.setSelection(brandAdapter.getSelectedPosition(deviceIRInfo
					.getCode()));
		}
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
				codes = brandAdapter.getItem(position).getCodes();
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

	private void saveIRAir() {
		ACBrand brand = brandAdapter.getItem(brandSpinner
				.getSelectedItemPosition());
		String name = brand.getName();
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

	public class IRBrandAdapter extends WLBaseAdapter<ACBrand> {

		public IRBrandAdapter(Context context, List<ACBrand> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_air_setting_brand_item,
					null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,
				ACBrand item) {
			TextView name = (TextView) view
					.findViewById(R.id.device_ir_setting_brand_tv);
			name.setText(item.getName());
		}

		public int getSelectedPosition(String brandID) {
			for (int i = 0; i < getCount(); i++) {
				ACBrand b = getItem(i);
				if (b.getID().equals(brandID)) {
					return i;
				}
			}
			return 0;
		}
	}
}
