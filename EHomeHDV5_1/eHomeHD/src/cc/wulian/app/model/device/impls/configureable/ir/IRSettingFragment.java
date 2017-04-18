package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.event.DeviceIREvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class IRSettingFragment extends WulianFragment{
	public static final String GW_ID = "extra_dev_gwID";
	public static final String DEV_ID = "extra_dev_ID";
	public static final String CURRENT_SHOW_FRAGMENT_IR_SETTING = "2";
	private WulianDevice irDevice;
	private DeviceCache deviceCache ;
	private View rootVliew;
	private LinearLayout contentView;
	private IRGroupManager iRGroupManager ;
	private ImageView addImageView;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		Bundle bundle = getArguments();
		String gwID = bundle.getString(GW_ID);
		String devID = bundle.getString(DEV_ID);
//		String irType = bundle.getString(EXTRA_DEV_IR_IRTYPE);
//		if(!StringUtil.isNullOrEmpty(irType)){
//			mActivity.finish();
//		}
		irDevice = deviceCache.getDeviceByID(mActivity, gwID, devID);
		iRGroupManager = IRManager.getInstance().getIrGroupManager(gwID, devID);
		initBar();
	}
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setTitle(R.string.device_ir_setting);
		getSupportActionBar().setIconText(R.string.device_ir_back);
		getSupportActionBar().setLeftIconClickListener(new OnLeftIconClickListener() {
			
			@Override
			public void onClick(View v) {
				if(iRGroupManager.getAllIRInfos().size() == 0){
					WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = WL_22_IR_Control.CURRENT_SHOW_DEVICE_LIST;
					mActivity.finish();
				}else{
					WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = WL_22_IR_Control.CURRENT_SHOW_FRAGMENT_DEVICE;
					mActivity.finish();
				}
			}
		});
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		rootVliew =  (LinearLayout)inflater.inflate(R.layout.device_ir_setting_content, null);
		return rootVliew;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		contentView = (LinearLayout)view.findViewById(R.id.devcie_setting_content_ll);
		addImageView = (ImageView)view.findViewById(R.id.devcie_setting_add_iv);
		addImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				new IRViewBuilder(mActivity,iRGroupManager).showAddIrGroupViewPopupWindow(addImageView);
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		Logger.debug("add:" + "onResume");
		if(!StringUtil.equals(WL_22_IR_Control.CURRENT_SHOW_FRAGMENT, CURRENT_SHOW_FRAGMENT_IR_SETTING)){
			Logger.debug("add:" + "onResume close setting");
			mActivity.finish();
		}else{
			createView();
		}
	}
	
	private void createView(){
		Logger.debug("add:" + "createView");
		contentView.removeAllViews();
		for(final IRGroup group : iRGroupManager.getIRGroups()){
			int size = group.size();
			if(IRGroupManager.TYPE_GENERAL.equals(group.getGroupType())){
				size = iRGroupManager.getGeneralGroupSize();
			}
			if(size <= 0)
				continue;
			FrameLayout view = (FrameLayout)inflater.inflate(R.layout.device_ir_setting_item, null);
			LinearLayout backgroupView = (LinearLayout)view.findViewById(R.id.device_ir_setting_bg_ll);
			Button studyBtn = (Button)backgroupView.findViewById(R.id.device_ir_setting_study);
			Button deleteBtn = (Button)backgroupView.findViewById(R.id.device_ir_setting_delete);
			studyBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					jumpIRStudyFragment(group);
				}
			});
			deleteBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					List<DeviceIRInfo> deleteDeviceInfos = null;
					if(IRGroupManager.TYPE_GENERAL.equals(group.getGroupType())){
						deleteDeviceInfos = iRGroupManager.getGeneralGroupDeviceIrInfos();
					}else
						deleteDeviceInfos = group.getAllKeys();
					JsonTool.deleteIrInfo(mActivity, iRGroupManager.getGwID(), iRGroupManager.getDevID(), WulianDevice.EP_14,deleteDeviceInfos, null);
				}
			});
			LinearLayout frontView = (LinearLayout)view.findViewById(R.id.device_ir_setting_font_ll);
			TextView titleTextView = (TextView)view.findViewById(R.id.device_ir_setting_title);
			ImageView clickImageView = (ImageView)view.findViewById(R.id.device_ir_setting_font_imageview);
			titleTextView.setText(group.getGroupName());
			frontView.setOnTouchListener(new SwipeTouchViewListener(frontView,backgroupView));
			clickImageView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					jumpIRStudyFragment(group);
				}
			});
			contentView.addView(view);
		}
		
	}
	
	private void jumpIRStudyFragment(IRGroup group){
		Intent intent = new Intent(mActivity,DeviceSettingActivity.class);
		intent.putExtra(IRStudyFragment.DEV_ID, iRGroupManager.getDevID());
		intent.putExtra(IRStudyFragment.GW_ID, iRGroupManager.getGwID());
		intent.putExtra(IRStudyFragment.IR_TYPE,group.getGroupType());
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, IRStudyFragment.class.getName());
		mActivity.startActivity(intent);
		Logger.debug("add:" + "device_jump");
		WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = IRStudyFragment.CURRENT_SHOW_FRAGMENT_IR_STUDY;
	}
	public void onEventMainThread(DeviceIREvent event){
//		createView();
		if(StringUtil.equals(event.action, CmdUtil.MODE_ADD)){
			Logger.debug("add:" + "device_irtype");
			jumpIRStudyFragment(iRGroupManager.getGroup(event.irType));
		}else{
			Logger.debug("add:" + "refresh");
			createView();	
		}
	}
	
}
