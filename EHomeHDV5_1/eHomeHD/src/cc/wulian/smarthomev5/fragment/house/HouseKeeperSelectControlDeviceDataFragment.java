package cc.wulian.smarthomev5.fragment.house;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.DesktopCameraDevice;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

public class HouseKeeperSelectControlDeviceDataFragment extends WulianFragment implements H5PlusWebViewContainer{
	public static final String DEV_GW_ID = "extra_dev_gwID";
	public static final String DEV_ID = "extra_dev_ID";
	public static final String LINK_LIST_ACTIONINFO_INFO = "actioninfo";
	public static SelectControlDeviceDataListener listener;
	private WulianDevice device;
	private DeviceCache deviceCache;
	private DialogOrActivityHolder holder;
	public static AutoActionInfo actionInfo;
	public static boolean isShowHouseKeeperSelectControlDeviceDataView=false;
	public WulianDevice getCurDevice(){
		return device;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		String gwID = getArguments().getString(DEV_GW_ID);
		String devID = getArguments().getString(DEV_ID);
		device = deviceCache.getDeviceByID(mActivity, gwID, devID);
		Bundle bundle = getActivity().getIntent().getExtras();
		if (bundle != null) {
			actionInfo = (AutoActionInfo) bundle
					.getSerializable(LINK_LIST_ACTIONINFO_INFO);
			Logger.debug("actioninfo fragement:"+actionInfo);
		}else{
			actionInfo = new AutoActionInfo();
		}
		//增加对桌面摄像机的判断
		if(actionInfo.getObject().equals("self")){
			device=new DesktopCameraDevice(mActivity, "camera");
		}
		device.setCurrentFragment(this);
		isShowHouseKeeperSelectControlDeviceDataView=true;
		holder = device.onCreateHouseKeeperSelectControlDeviceDataView(inflater, actionInfo);
		initBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		return holder.getContentView();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(R.string.about_back);
		getSupportActionBar().setTitle(holder.getFragementTitle());
		getSupportActionBar().setRightIconText(R.string.common_ok);
		getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
			
			@Override
			public void onClick(View v) {
				String obj = actionInfo.getObject();
				if(actionBarClickRightListener!=null&&device.isHouseKeeperSelectControlDeviceActionBarUseable()){
					actionBarClickRightListener.doSomething(actionInfo);
				}
				fireSelectDeviceDataListener(obj, actionInfo.getEpData(),actionInfo.getDescription());
				mActivity.finish();
			}
		});
	}
	public static void setSelectControlDeviceDataListener(SelectControlDeviceDataListener listener){
		HouseKeeperSelectControlDeviceDataFragment.listener = listener;
	}
	public static void fireSelectDeviceDataListener(String obj,String epData,String des){
		if(HouseKeeperSelectControlDeviceDataFragment.listener != null){
			HouseKeeperSelectControlDeviceDataFragment.listener.onSelectDeviceDataChanged(obj,epData,des);
		}
	}
	
	public interface SelectControlDeviceDataListener{
		public void onSelectDeviceDataChanged(String object,String epData,String des);
	}
	
	private static ActionBarClickRightListener actionBarClickRightListener;
	
	public interface ActionBarClickRightListener{
		void doSomething(AutoActionInfo autoActionInfo);
	}
	
	public static void setActionBarClickRightListener(ActionBarClickRightListener actionBarClickRightListener){
		HouseKeeperSelectControlDeviceDataFragment.actionBarClickRightListener=actionBarClickRightListener;
	}
	@Override
	public void addH5PlusWebView(H5PlusWebView webview) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void destroyContainer() {
		Engine.destroyPager(this);
		this.getActivity().finish();
	}
	@Override
	public ViewGroup getContainerRootView() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
