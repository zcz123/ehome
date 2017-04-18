package cc.wulian.app.model.device.impls.configureable.ir;

import android.os.Bundle;
import android.view.View;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.configureable.ir.IRViewBuilder.IRAirAdapter;
import cc.wulian.app.model.device.impls.configureable.ir.IRViewBuilder.IRGeneralAdapter;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class IRStudyFragment extends WulianFragment {
	public static final String GW_ID = "extra_dev_gwID";
	public static final String DEV_ID = "extra_dev_ID";
	public static final String IR_TYPE = "extra_ir_type";
	public static final  String CURRENT_SHOW_FRAGMENT_IR_STUDY = "3";
	public static final String KEY_PROCESS_DIALOG_IR_STUDY = "KEY_PROCESS_DIALOG_IR_STUDY";
	private WulianDevice irDevice;
	private DeviceCache deviceCache;
	private IRGroupManager iRGroupManager;
	private IRGroup irGroup;
	private IRViewBuilder builder;
	public static final String EXTRA_DEV_IR_IRTYPE = "extra_dev_ir_irtype";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Logger.debug("add:" + "study on creat");
		deviceCache = DeviceCache.getInstance(mActivity);
		Bundle bundle = getArguments();
		String gwID = bundle.getString(GW_ID);
		String devID = bundle.getString(DEV_ID);
		String irType = bundle.getString(IR_TYPE);
		irDevice = deviceCache.getDeviceByID(mActivity, gwID, devID);
		iRGroupManager = IRManager.getInstance().getIrGroupManager(gwID, devID);
		irGroup = iRGroupManager.getGroup(irType);
		initBar();
		builder = new IRViewBuilder(mActivity, iRGroupManager);
		createView(IRAirAdapter.MODE_STUDY);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.device_ir_study);
		getSupportActionBar().setIconText(R.string.device_ir_back);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setRightIconText(R.string.device_ir_save);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				showIRStudyBackDialog();
			}
		});
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = WL_22_IR_Control.CURRENT_SHOW_FRAGMENT_DEVICE;
						Logger.debug("add:" + "study on onClick");
						JsonTool.saveIrInfoBath(mActivity,
								iRGroupManager.getGwID(),
								iRGroupManager.getDevID(), WulianDevice.EP_14,
								iRGroupManager.getAllIRInfos(),
								irGroup.getGroupType());
						mActivity.finish();
					}
				});
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.debug("add:" + "study on onResume");
		if(!StringUtil.equals(WL_22_IR_Control.CURRENT_SHOW_FRAGMENT, CURRENT_SHOW_FRAGMENT_IR_STUDY)){
			mActivity.finish();
		}
	}
	private void showIRStudyBackDialog(){
		View ContentView = View.inflate(mActivity, R.layout.device_ir_study_back_dialog, null);
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mActivity.getString(R.string.device_songname_refresh_title))
		.setContentView(ContentView)
		 .setPositiveButton(android.R.string.ok)
		 .setNegativeButton(android.R.string.cancel)
		 .setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = IRSettingFragment.CURRENT_SHOW_FRAGMENT_IR_SETTING;
				Logger.debug("add:" + "study on Dialog");
				mActivity.finish();
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
				
			}
		});
		WLDialog dialog = builder.create();
		dialog.show();
	}
	
	public void createView(int mode) {
		View view = null;
		if (IRGroupManager.TYPE_STB.equals(irGroup.getGroupType())) {
			view = builder.createStudySTBView();
		} else if (IRGroupManager.TYPE_AIR_CONDITION.equals(irGroup.getGroupType())) {
			//接口回调函数，创建多个圆形按钮
			view = builder.createStudyAirView(mode);
		} else if (IRGroupManager.TYPE_GENERAL.equals(irGroup.getGroupType()) || StringUtil.equals("-1", irGroup.getGroupType())) {
			view = builder.createStudyGeneralView(mode);
		}
		if (view != null) {
			mActivity.setContentView(view);
		}
	}

	public void onEventMainThread(EditDeviceIRInfoEvent event){
		createView(event.mode);
	}
	public static class EditDeviceIRInfoEvent{
		public int mode;
		public EditDeviceIRInfoEvent(){
			
		}
		public EditDeviceIRInfoEvent(int mode) {
			this.mode = mode;
		}
		
		
	}
	public void onEventMainThread(DeviceEvent event) {
		ProgressDialogManager.getDialogManager().dimissDialog(
				IRStudyFragment.KEY_PROCESS_DIALOG_IR_STUDY, 0);
		DeviceInfo info = event.deviceInfo;
		if (info != null && info.getGwID().equals(iRGroupManager.getGwID())
				&& iRGroupManager.getDevID().equals(info.getDevID())) {
			String epData = info.getDevEPInfo().getEpData();
			notifyStudyed(epData);
		}
	}

	private void notifyStudyed(String epData) {
		if (!StringUtil.isNullOrEmpty(epData)
				&& epData.startsWith(CmdUtil.IR_MODE_STUDY)) {
			String keySet = StringUtil.appendLeft(epData.substring(1),3,'0');
			Logger.debug("ir study receive keyset:"+keySet);
			DeviceIRInfo info = iRGroupManager.getDeviceIRInfo(
					irGroup.getGroupType(), keySet);
			
			if (info != null) {
				info.setStatus(DeviceIRInfo.STATUS_STUDYED);
				createView(IRAirAdapter.MODE_STUDY);
				if(info.getIRType().equals(IRGroupManager.TYPE_AIR_CONDITION)){
					builder.showEditDeviceIrAirInfoDialog(info,IRAirAdapter.MODE_STUDY);
				}else if(info.getIRType().equals(IRGroupManager.TYPE_GENERAL)){
					builder.showEditDeviceIrGeneralInfoDialog(info,IRGeneralAdapter.MODE_STUDY);
				}
				
			}
		}
	}
	
}
