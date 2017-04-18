package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.configureable.ConfigureableDeviceImpl;
import cc.wulian.app.model.device.impls.configureable.ir.AbstractEpDataView.ScrollListener;
import cc.wulian.app.model.device.impls.configureable.ir.AbstractEpDataView.SelectEpDataListener;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportACBrand;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportKeyCode;
import cc.wulian.app.model.device.impls.configureable.ir.xml.IRSupportSTB;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView.DeviceCategoryEntity;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.CmdUtil;

/**
 * 发送： <br/>
 * 位1:模式(1:学习,2:控制,3:搜索匹配) <br/>
 * 位2～3:红外类型(00:通用,01:空调,02:机顶盒) <br/>
 * 位4～6:品牌码(3位十进制字符,不足左补零) <br/>
 * 位7～9:控制码(3位十进制字符,不足左补零;模式为03时为空) <br/>
 * 
 * 接受： <br/>
 * (十六进制) 位1～2:模式(01:学习,02:控制,03:搜索匹配) <br/>
 * 位3～4:红外类型(00:通用,01:空调,02:机顶盒) <br/>
 * 位5～8:品牌码(需转换成3位十进制字符,不足左补零) <br/>
 * 位9～12:控制码(需转换成3位十进制字符,不足左补零;模式为03时为空) <br/>
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL}, 
		category = Category.C_CONTROL)
public class WL_24_AR_IR_Control extends ConfigureableDeviceImpl
{
	private Map<String,Map<Integer,Integer>> categoryIcons = DeviceUtil.getIRCategoryDrawable();
	private Preference preference = Preference.getPreferences();
	private TextView stbTextView;
	private TextView airTextView;
	private TextView generalTextView;
	private LinearLayout contentLinearLayout;
	private Map<String,AbstractIRView> irViewsMap = new HashMap<String, AbstractIRView>();
	private IRGeneralView generalView ;
	private IRAirView airView;
	private IRSTBView stbView;
	private Intent intent;
	public WL_24_AR_IR_Control( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		NetSDK.sendGetDevIRMsg(gwID, devID, ep, CmdUtil.MODE_BATCH_ADD);
		generalView = new IRGeneralView(mContext,getDeviceInfo());
		airView = new IRAirView(mContext,getDeviceInfo());
		stbView = new IRSTBView(mContext,getDeviceInfo());
		IRSupportACBrand.getInstance(mContext);
		IRSupportSTB.getInstance(mContext);
		IRSupportKeyCode.getInstance(mContext);
	}

	public Intent getSettingIntent() {
		String currentPage = preference.getString(IPreferenceKey.P_KEY_IR_CURRENT_PAGE, "");
		if(currentPage.equals(generalView.getType())){
			intent = generalView.getSettingIntent();
		}else if(currentPage.equals(airView.getType())){
			intent = airView.getSettingIntent();
		}else if(currentPage.equals(stbView.getType())){
			intent = stbView.getSettingIntent();
		}else{
			intent = null;
		}
		return intent;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}

				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	@Override
	public Drawable getStateSmallIcon() {
		String categoyID = getDeviceInfo().getCategory();
		Drawable icon = getDefaultStateSmallIcon();
		if(categoryIcons.containsKey(categoyID)){
			icon = mResources.getDrawable(categoryIcons.get(categoyID).get(0));
		}
		return icon;
	}
	@Override
	public Drawable[] getStateBigPictureArray(){
		Drawable[] drawables = new Drawable[]{getResources().getDrawable(R.drawable.device_ir_control_normal)};
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_24));
		return sb.toString();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		generalView.attachContext(inflater.getContext());
		generalView.setDeviceInfo(getDeviceInfo());
		airView.attachContext(inflater.getContext());
		airView.setDeviceInfo(getDeviceInfo());
		stbView.attachContext(inflater.getContext());
		stbView.setDeviceInfo(getDeviceInfo());
		return inflater.inflate(R.layout.device_ir_ar_content, null);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		stbTextView = (TextView)view.findViewById(R.id.device_ar_stb_tv);
		airTextView = (TextView)view.findViewById(R.id.device_ar_air_tv);
		generalTextView = (TextView)view.findViewById(R.id.device_ar_general_tv);
		contentLinearLayout = (LinearLayout)view.findViewById(R.id.device_ar_contnet_ll);
		//通用
		generalView.setHeadView(generalTextView);
		generalView.setContentView(contentLinearLayout);
		generalView.setViewMap(irViewsMap);
		
		//空调
		airView.setHeadView(airTextView);
		airView.setContentView(contentLinearLayout);
		airView.setViewMap(irViewsMap);
		
		//机顶盒
		
		stbView.setHeadView(stbTextView);
		stbView.setContentView(contentLinearLayout);
		stbView.setViewMap(irViewsMap);
		
		mViewCreated = true;
	}

	private void initClick() {
		String currentPage = preference.getString(IPreferenceKey.P_KEY_IR_CURRENT_PAGE, "");
		if(currentPage.equals(generalView.getType())){
			generalView.headClick(generalTextView);
		}else if(currentPage.equals(airView.getType())){
			airView.headClick(airTextView);
		}else{
			stbView.headClick(stbTextView);
		}
	}

	@Override
	public void initViewStatus() {
		initClick();
	}
	@Override
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
		EditDeviceInfoView view  = super.onCreateEditDeviceInfoView(inflater);
		ArrayList<DeviceCategoryEntity> entities= new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
		for(String key : categoryIcons.keySet()){
			DeviceCategoryEntity entity = new DeviceCategoryEntity();
			entity.setCategory(key);
			entity.setResources(categoryIcons.get(key));
			entities.add(entity);
		}
		view.setDeviceIcons(entities);
		return view;
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,
			String epData) {
		String type = (epData != null && epData.length()>=9)?epData.substring(1,3):AbstractEpDataView.TYPE_GENERAL;
		final LinearLayout contentLinearLayout = (LinearLayout)inflater.inflate(R.layout.device_ir_ar_epdata_content, null);
		final EpDataGeneralView generalView = new EpDataGeneralView(inflater.getContext(), getDeviceInfo(),epData);
		generalView.onViewCreated(generalView.onCreateView());
		generalView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				linkTaskControlEPData = new StringBuffer(epData);
				
			}
		});
		final EpDataAirView airView = new EpDataAirView(inflater.getContext(), getDeviceInfo(), epData);
		airView.onViewCreated(airView.onCreateView());
		airView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				linkTaskControlEPData = new StringBuffer(epData);
				
			}
		});
		final EpDataSTBView stbView = new EpDataSTBView(inflater.getContext(), getDeviceInfo(), epData);
		stbView.onViewCreated(stbView.onCreateView());
		stbView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				linkTaskControlEPData = new StringBuffer(epData);
				
			}
		});
		generalView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(stbView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(airView.getView());
			}
		});
		airView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int  dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(generalView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(stbView.getView());
			}
		});
		stbView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int  dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(airView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(generalView.getView());
			}
		});
		View view = null;
		if(type.equals(airView.getType())){
			view = airView.getView();
		}else if(type.equals(generalView.getType())){
			view = stbView.getView();
		}else{
			view = stbView.getView();
		}
		contentLinearLayout.addView(view);
		return createControlDataDialog(inflater.getContext(),contentLinearLayout);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		String type = (epData != null && epData.length()>=9)?epData.substring(1,3):AbstractEpDataView.TYPE_GENERAL;
		final LinearLayout contentLinearLayout = (LinearLayout)inflater.inflate(R.layout.device_ir_ar_epdata_content, null);
		final EpDataGeneralView generalView = new EpDataGeneralView(inflater.getContext(), getDeviceInfo(),epData);
		generalView.onViewCreated(generalView.onCreateView());
		generalView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				autoActionInfo.setEpData(epData);
				autoActionInfo.setDescription("");
			}
		});
		final EpDataAirView airView = new EpDataAirView(inflater.getContext(), getDeviceInfo(), epData);
		airView.onViewCreated(airView.onCreateView());
		airView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				autoActionInfo.setEpData(epData);
				autoActionInfo.setDescription("");
			}
		});
		final EpDataSTBView stbView = new EpDataSTBView(inflater.getContext(), getDeviceInfo(), epData);
		stbView.onViewCreated(stbView.onCreateView());
		stbView.setSelectEpDataListener(new SelectEpDataListener() {
			
			@Override
			public void onSelectEpData(String epData) {
				autoActionInfo.setEpData(epData);
				autoActionInfo.setDescription("");
			}
		});
		generalView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(stbView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(airView.getView());
			}
		});
		airView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int  dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(generalView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(stbView.getView());
			}
		});
		stbView.setScrollListener(new ScrollListener() {
			
			@Override
			public void processScroll(int  dir) {
				contentLinearLayout.removeAllViews();
				if(dir == AbstractEpDataView.DIR_RIGHT)
					contentLinearLayout.addView(airView.getView());
				else if(dir == AbstractEpDataView.DIR_LEFT)
					contentLinearLayout.addView(generalView.getView());
			}
		});
		View view = null;
		if(type.equals(airView.getType())){
			view = airView.getView();
		}else if(type.equals(generalView.getType())){
			view = stbView.getView();
		}else{
			view = stbView.getView();
		}
		contentLinearLayout.addView(view);
		holder.setContentView(contentLinearLayout);
		return holder;
		
	}
}