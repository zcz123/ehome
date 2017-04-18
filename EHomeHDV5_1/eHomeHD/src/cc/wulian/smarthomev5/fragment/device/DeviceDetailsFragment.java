package cc.wulian.smarthomev5.fragment.device;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceStateChangeListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.FavorityDao;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;

public class DeviceDetailsFragment extends WulianFragment implements
		OnWulianDeviceStateChangeListener, OnWulianDeviceRequestListener, H5PlusWebViewContainer {
	public static final String TAG = DeviceDetailsFragment.class
			.getSimpleName();
	public static boolean isJionNetwork=false;//为配网时wifi切换，网关掉线，开锁界面不可点击用  add mabo

	private static final String DEVICE_DETAIL = "device_detail_view";
	public static final String EXTRA_DEV_GW_ID = "extra_dev_gwID";
	public static final String EXTRA_DEV_ID = "extra_dev_ID";

	private WulianDevice mDevice;
	private DeviceCache deviceCache;
	private FavorityDao mFavorityDao = FavorityDao.getInstance();
	public WulianDevice getCurDevice(){
		return mDevice;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		String gwID = getArguments().getString(EXTRA_DEV_GW_ID);
		String devID = getArguments().getString(EXTRA_DEV_ID);
		mDevice = deviceCache.getDeviceByID(mActivity, gwID, devID);
		if(mDevice == null){
			return ;
		}
		mDevice.onAttachView(mActivity);
	}

	@Override
	public void onDeviceOnLineStateChange(boolean onLine) {

	}
	@Override
	public void onDeviceRequestControlSelf(WulianDevice device) {
		//有权限方可弹窗
		if (UserRightUtil.getInstance().canControlDevice(device.getDeviceID())) {
			StringBuilder sb = new StringBuilder();
			sb.append(device.getDeviceGwID());
			sb.append(device.getDeviceID());
			String key = sb.toString();
			getDialogManager().showDialog(key, getActivity(), null, null);
		}
	}

	@Override
	public void onDeviceRequestControlData(WulianDevice device) {
		FavorityEntity mFavorityEntity = new FavorityEntity();
		mFavorityEntity.setTime(System.currentTimeMillis()+"");
		mFavorityEntity.setGwID(device.getDeviceGwID());
		mFavorityEntity.setOperationID(device.getDeviceID());
		mFavorityEntity.setType(Favority.TYPE_DEVICE);
		mFavorityEntity.setOrder(Favority.OPERATION_AUTO);
		mFavorityDao.operateFavorityDao(mFavorityEntity);

	}
	private View deviceDetailView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(mDevice == null){
			return null;
		}
		LinearLayout view = (LinearLayout) inflater.inflate(
				R.layout.fragment_device_details_and_menu, container, false);
		FrameLayout flayout = (FrameLayout) view
				.findViewById(R.id.device_detail_continer_fl);
		// TextView deviceAreaTextView = (TextView) view
		// .findViewById(R.id.device_area_textview);
		// deviceAreaTextView.setText(mDevice.getDeviceRoomID());
		flayout.removeAllViews();
		deviceDetailView= mDevice.onCreateView(inflater, view,
				savedInstanceState);
		deviceDetailView.setTag(DEVICE_DETAIL);
		flayout.addView(deviceDetailView, new FrameLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT,
				Gravity.CENTER));
		View screenView = (View) view.findViewById(R.id.device_detail_screen_view);
		if(!mDevice.isDeviceOnLine()){
			screenView.setVisibility(View.VISIBLE);
			screenView.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					return true;
				}
			});
		}else{
			screenView.setVisibility(View.GONE);
		}
		mDevice.setCurrentActionBar(getSupportActionBar());
		mDevice.setCurrentFragment(this);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if(mDevice == null){
			return ;
		}
		super.onViewCreated(view, savedInstanceState);
		initBar();
		mDevice.onViewCreated(view.findViewWithTag(DEVICE_DETAIL),
				savedInstanceState);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setIconText(
				mApplication.getResources().getString(R.string.nav_device_title));
		getSupportActionBar().setTitle(DeviceTool.getDeviceShowName(mDevice));
		getSupportActionBar().setRightIcon(
				R.drawable.device_setting_more_actionbar_button);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						LinearLayout mLinearLayout = (LinearLayout) getSupportActionBar()
								.getCustomView()
								.findViewById(
										R.id.common_action_bar_right_icon_and_text);
						// DeviceSettingMorePopupWindowManager manager = new
						// DeviceSettingMorePopupWindowManager(
						// mActivity, mDevice);
						MoreMenuPopupWindow manager = mDevice.getDeviceMenu();
						manager.show(mLinearLayout);
					}
				});
	}
	@Override
	public void onResume() {
		super.onResume();
		if (isJionNetwork){
			isJionNetwork=false;
			mActivity.finish();
			return ;
		}
		if(mDevice == null){
			mActivity.finish();
			return ;
		}
		mDevice.registerStateChangeListener(this);
		mDevice.registerControlRequestListener(this);
		mDevice.initViewStatus();
		mDevice.onResume();
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		if(mDevice == null){
			return ;
		}
		mDevice.onDetachView();
	}
         
	@Override
	public void onPause() {
		super.onPause();
		if(mDevice == null){
			return ;
		}
		mDevice.onPause();
		mDevice.unregisterStateChangeListener(this);
		mDevice.unregisterControlRequestListener(this);
	}
	
	public void onEventMainThread(DeviceEvent event) {
		if(event.deviceInfo == null)
			return ;
		try{
			if(mDevice.getDeviceID().equals(event.deviceInfo.getDevID())){
				if(DeviceEvent.REFRESH.equals(event.action)){
					getSupportActionBar().setTitle(DeviceTool.getDeviceShowName(mDevice));
				}else if(DeviceEvent.REMOVE.equals(event.action)){
					mActivity.finish();
				}
			}
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void onDestroy() {
//		mDevice.onDeviceDetailsFragmentDestroy();
		Engine.destroyPager(this);
		super.onDestroy();
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
		return (ViewGroup)deviceDetailView;
	}

}