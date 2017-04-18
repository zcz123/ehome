package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class EpDataSTBView extends AbstractEpDataView{
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;
	private final View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v ) {
			String tag = String.valueOf(v.getTag());
			if (StringUtil.isNullOrEmpty(tag)) return;
			StringBuilder sb = new StringBuilder();
			sb.append(CmdUtil.IR_MODE_CTRL);
			sb.append(deviceIRInfo.getKeyset());
			sb.append(tag);
			String sendData = sb.toString();
			setEpData(sendData,true);
		}
	};
	public EpDataSTBView(Context context, DeviceInfo info,String epData) {
		super(context, info,epData);
	}

	@Override
	public View onCreateView() {
		rootView =  inflater.inflate(R.layout.common_ir_frame_stb, null);
		return rootView;
	}

	@Override
	public void onViewCreated(View view) {
		TypedArray array = resources.obtainTypedArray(R.array.ir_stb_id_array);
		int length = array.length();
		for (int i = 0; i < length; i++) {
			initView(view, array.getResourceId(i, 0));
		}
		array.recycle();
		loadData();
		view.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				 mGestureDetector.onTouchEvent(event);
				 return true;
			}
		});
		setEpData(this.epData,false);
	}
	private void initView( View view, int resId ) {
		View keyView = view.findViewById(resId);
		if (keyView == null) return;
		keyView.setOnClickListener(mOnClickListener);
	}
	public void loadData() {
		DeviceIRInfo info = new DeviceIRInfo();
		info.setGwID(deviceInfo.getGwID());
		info.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			info.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			info.setEp(WulianDevice.EP_14);
		}
		info.setIRType(getType());
		deviceIRInfo = irDao.getById(info);
		if(deviceIRInfo == null){
			deviceIRInfo = info;
			deviceIRInfo.setIRType(getType());
			deviceIRInfo.setKeyset(getType()+CmdUtil.IR_GENERAL_KEY_DEFAULT);
		}
	}

	@Override
	public String getType() {
		return TYPE_STB;
	}
	private void setEpData(String epData,boolean isFire){
		if(epData!= null && epData.length()>=9){
			clearStates();
			String cmd = epData.substring(6,9);
			TypedArray array = resources.obtainTypedArray(R.array.ir_stb_id_array);
			int length = array.length();
			for (int i = 0; i < length; i++) {
				View keyView = rootView.findViewById(array.getResourceId(i, 0));
				if(keyView != null){
					if(cmd.equals(keyView.getTag()))
						keyView.setSelected(true);
				}
			}
			array.recycle();
			if(isFire){
				fireSelectEpDataListener(epData);
			}
		}
	}
	private void clearStates(){
		TypedArray array = resources.obtainTypedArray(R.array.ir_stb_id_array);
		int length = array.length();
		for (int i = 0; i < length; i++) {
			View keyView = rootView.findViewById(array.getResourceId(i, 0));
			if(keyView != null)
				keyView.setSelected(false);
		}
		array.recycle();
	}
}
