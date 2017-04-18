package cc.wulian.app.model.device.impls.configureable.ir;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.uei.control.ACEService;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.configureable.ConfigureableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView.DeviceCategoryEntity;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.activity.testapi.TestApi_406Activity;
import cc.wulian.smarthomev5.activity.uei.RemooteControlActivity;
import cc.wulian.smarthomev5.activity.uei.SettingActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UEIEntityManager;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.fragment.uei.UeiCommonEpdata;
import cc.wulian.smarthomev5.fragment.uei.UeiOnlineUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.YkanSDKManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;


@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_23}, category = Category.C_CONTROL)
public class WL_23_IR_Control extends ConfigureableDeviceImpl implements ICommand406_Result {
    private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil.getIRCategoryDrawable();
    private SwipeMenuListView deviceList;//可滑动列表
    private BrandAdapter brandAdapter;//遥控器品牌适配器
    private Button addRemoteControlBtn;//添加遥控器按钮
    private List<UEIEntity> ueiEntitys = new ArrayList<UEIEntity>();
    private String appID = AccountManager.getAccountManger().getmCurrentInfo().getAppID();
    private Command406_DeviceConfigMsg command406 = null;
    public static String curDeleteFlag = "";//删除标志
    public static String flag_Clear = "CLEAR";//清空标志
    //	private String flag_delete="DELETE";//删除标志
    private LinearLayout cleartest_layout = null;//测试
    public static String pluginName = "UEI_23_Beta1.zip";
    ProgressDialogManager mDialogManager = ProgressDialogManager.getDialogManager();
    private List<String> shieldKeys = new ArrayList<String>();//需要屏蔽的键值，这些做特殊作用，不用在遥控列表中显示出来
    public static boolean isUsePlugin=true;//默认是true，调试时可以改成false

    public WL_23_IR_Control(Context context, String type) {
        super(context, type);
        shieldKeys.clear();
        shieldKeys.add("currentIndex");//空调快捷键最大值
        shieldKeys.add("LearnIndex");//学习码最大码值
        shieldKeys.add("testIndex");//用于测试的
        YkanSDKManager.init(this.getContext(), "", "");
        YkanSDKManager.getInstance().setLogger(true);
    }


    @Override
    public void onDeviceUp(DeviceInfo devInfo) {
        super.onDeviceUp(devInfo);
    }

    @Override
    public Drawable getStateSmallIcon() {
        Drawable icon = null;
        if (this.isDeviceOnLine()) {
            String categoyID = getDeviceInfo().getCategory();
            icon = getDefaultStateSmallIcon();
            if (categoryIcons.containsKey(categoyID)) {
                icon = mResources.getDrawable(categoryIcons.get(categoyID).get(0));
            }
        } else {
            icon = getResources().getDrawable(cc.wulian.app.model.device.R.drawable.uei_offline);
        }
        return icon;
    }

    @Override
    public void onAttachView(Context context) {
        super.onAttachView(context);
    }

    @Override
    public void onDetachView() {
        super.onDetachView();
        Logger.debug("add:" + "study on onDetachView");
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        StringBuilder sb = new StringBuilder();
        sb.append(getResources().getString(R.string.device_type_22));
        return sb.toString();
    }

    /**
     * 创建左划删除item样式
     */
    private SwipeMenuCreator creatLeftDeleteItem() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {
            @Override
            public void create(SwipeMenu menu, int position) {

				/*SwipeMenuItem deleteItem = new SwipeMenuItem(DeviceDetailsActivity.instance);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(DeviceDetailsActivity.instance, 90));
				deleteItem.setIcon(cc.wulian.smarthomev5.R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);*/

                SwipeMenuItem settingItem = new SwipeMenuItem(DeviceDetailsActivity.instance);
                settingItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                settingItem.setWidth(DisplayUtil.dip2Pix(DeviceDetailsActivity.instance, 90));
                settingItem.setTitle(cc.wulian.smarthomev5.R.string.device_ir_setting);
                settingItem.setTitleSize(DisplayUtil.dip2Sp(DeviceDetailsActivity.instance, 5));
                settingItem.setTitleColor(mContext.getResources().getColor(cc.wulian.smarthomev5.R.color.white));
                menu.addMenuItem(settingItem);
            }
        };
        return creator;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View rootView = inflater.inflate(R.layout.device_23_ir_layout, null);
        Preference.getPreferences().putString("uei_devID", devID);
        String acEncryptUserId = ACEService.ACEncryptUserId(getDeviceID());
        if(!StringUtil.isNullOrEmpty(acEncryptUserId)){
            SmarthomeFeatureImpl.setData("UeiUserID",acEncryptUserId);
            if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
                Log.d("UEI_23", "acEncryptUserId:"+acEncryptUserId);
            }
        }
        else{
            SmarthomeFeatureImpl.setData("UeiUserID","");
            if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
                Log.d("UEI_23", "acEncryptUserId is null");
            }
        }
        initChildView(rootView, 0);
        return rootView;
    }

    //添加遥控
    View.OnClickListener addRemoteControlBtn_onclick = new View.OnClickListener() {


        @Override
        public void onClick(View arg0) {
            if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)){
                return;
            }
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, ep);
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPTYPE, epType);
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
            String strUri="";
            if(isUsePlugin){
                getPlugin(true);
            }else {
                strUri="file:///android_asset/uei/type_select.html";
                Intent intent = new Intent(DeviceDetailsActivity.instance, Html5PlusWebViewActvity.class);
                intent.putExtra(Html5PlusWebViewActvity.KEY_URL, strUri);
                DeviceDetailsActivity.instance.startActivity(intent);
            }
        }
    };
    private String LOCK_KEY_CLEARITEMS = "LOCK_KEY_CLEARITEMS";

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
    }

    @Override
    public void onResume() {
        super.onResume();
        SmarthomeFeatureImpl.setData("relearnLearnDic", "");
        SmarthomeFeatureImpl.setData("relearnKcsArr", "");
    }

    @Override
    public void initViewStatus() {
        Logger.debug("add:" + "device on createControlView");
    }

    EventBusActivity parentActivity = null;

    private void loadData(final List<Command406Result> results) {

        if (parentActivity == null) {
            Log.d("WL_23", "parentActivity==null");
        } else {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    List<UEIEntity> newEntities = new ArrayList<>();
                    for (Command406Result result : results) {
                        UEIEntity entity = ConvertToUEIEntity(result);
                        if (entity != null) {
                            newEntities.add(entity);
                        }
                    }
                    if (newEntities != null && newEntities.size() > 0) {
                        for (UEIEntity item : newEntities) {
                            if (item != null) {
                                UEIEntity result = findUEI(ueiEntitys, item);
                                if (result == null) {
                                    ueiEntitys.add(item);
                                } else {
                                    result.setValue(item.getValue());
                                    result.setTime(item.getTime());
                                }
                            }
                        }
                    }
                    brandAdapter.swapData(ueiEntitys);
                }
            });
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = IRSettingFragment.CURRENT_SHOW_FRAGMENT_IR_SETTING;
        Logger.debug("add:" + "study on device onPause");
    }

    @Override
    protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MenuItem> items = super.getDeviceMenuItems(manager);
        //设置在当前页面不需要
		MenuItem settingItem = new MenuItem(DeviceDetailsActivity.instance) {

			@Override
			public void initSystemState() {
				titleTextView.setText(DeviceDetailsActivity.instance
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
                Bundle args = new Bundle();
                args.putString("settingType", "ueiSetting");
                args.putString("devID", devID);
                args.putString("gwID", gwID);
                Intent intent = new Intent(parentActivity, SettingActivity.class);
                intent.putExtra("args", args);
                if (WL_23_IR_Control.this.viewMode == 0) {
//									parentActivity.startActivity(intent);
                    parentActivity.startActivityForResult(intent, 3);
                    manager.dismiss();
                }

            }
		};
		if(isDeviceOnLine())
			items.add(settingItem);
        return items;
    }

    @Override
    public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
        EditDeviceInfoView view = super.onCreateEditDeviceInfoView(inflater);
        ArrayList<DeviceCategoryEntity> entities = new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
        for (String key : categoryIcons.keySet()) {
            DeviceCategoryEntity entity = new DeviceCategoryEntity();
            entity.setCategory(key);
            entity.setResources(categoryIcons.get(key));
            entities.add(entity);
        }
        view.setDeviceIcons(entities);
        return view;
    }

    @Override
    public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater, String ep,
                                                 String epData) {
        linkTaskControlEPData = new StringBuffer(epData);
        IRViewBuilder builder = new IRViewBuilder(inflater.getContext(), IRManager.getInstance().getIrGroupManager(getDeviceGwID(), getDeviceID()));
        builder.initControlView();
        String irType = null;
        if (!builder.isHasGrouptype()) {
            irType = IRGroupManager.NO_TYPE;
        } else {
            if (epData != null && epData.length() >= 4) {
                int data = StringUtil.toInteger(epData.substring(1));
                if (data >= 0 && data <= 255 && builder.isSelectAriGrouptype()) {
                    irType = IRGroupManager.TYPE_AIR_CONDITION;
                } else if (data >= 256 && data <= 510 && builder.isSelectSTBGrouptype()) {
                    irType = IRGroupManager.TYPE_STB;
                } else if (data >= 511 && data <= 610 && builder.isSelectGeneralGrouptype()) {
                    irType = IRGroupManager.TYPE_GENERAL;
                } else {
                    if (builder.isSelectGeneralGrouptype()) {
                        irType = IRGroupManager.TYPE_GENERAL;
                    } else if (builder.isSelectAriGrouptype()) {
                        irType = IRGroupManager.TYPE_AIR_CONDITION;
                    } else if (builder.isSelectSTBGrouptype()) {
                        irType = IRGroupManager.TYPE_STB;
                    }
                }
            } else {
                if (builder.isSelectGeneralGrouptype()) {
                    irType = IRGroupManager.TYPE_GENERAL;
                } else if (builder.isSelectAriGrouptype()) {
                    irType = IRGroupManager.TYPE_AIR_CONDITION;
                } else if (builder.isSelectSTBGrouptype()) {
                    irType = IRGroupManager.TYPE_STB;
                }
            }
        }
        View view = builder.createLinkView(irType, linkTaskControlEPData, false);
        return createControlDataDialog(inflater.getContext(), view);
    }

    private Button hidebtn_getepdata;

    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
            LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        String epdata=autoActionInfo.getEpData();
        Log.d("UEI23_Keeper", ""+epdata);
        View rootView = LayoutInflater.from(mContext).inflate(R.layout.device_23_ir_layout, null);
        holder.setShowDialog(false);
        holder.setContentView(rootView);
        holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
        initChildView(rootView, 1);
        hidebtn_getepdata.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                String epdata = "";
                String desc = "";
                if (view.getTag() != null) {
                    String[] arrinfo = view.getTag().toString().split(",", -1);
                    if (arrinfo != null && arrinfo.length == 2) {
                        epdata = arrinfo[0];
                        desc = arrinfo[1];
                    }
                }
                autoActionInfo.setEpData(epdata);
                autoActionInfo.setDescription(desc);
            }
        });
        return holder;
    }
	
	/*@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if (item == null) {
			item = new ShortCutUeiSelectDataItem(
					inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	private static TextView shortCutTextView;
	public static class ShortCutUeiSelectDataItem extends DeviceShortCutSelectDataItem{
		public ShortCutUeiSelectDataItem(Context context) {
			super(context);
			shortCutTextView =new TextView(context);
			controlLineLayout.addView(shortCutTextView);
		}
		@Override
		public void setWulianDeviceAndSelectData(final WulianDevice device,
				final AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			shortCutTextView.setText(autoActionInfo.getDescription());
		}
	}*/

    public class BrandAdapter extends SwipeMenuAdapter<UEIEntity> {
        private Map<UEIEntity, BrandBeanListItem> brandItemMap = new HashMap<UEIEntity, BrandBeanListItem>();

        public BrandAdapter(Context context, List<UEIEntity> data) {
            super(context, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final UEIEntity info = getItem(position);
            BrandBeanListItem item = new BrandBeanListItem(mContext, info);
            brandItemMap.put(info, item);
            convertView = item.getView(info);
            SwipeMenuLayout layout = null;
            if (convertView != null)
                layout = createMenuView(position, parent, convertView);
            return layout;
        }
    }

    public class BrandBeanListItem {
        ImageView itemIcon;
        TextView item_brandName_tv;
        TextView item_brandtype_tv;
        TextView itemInfoTv;
        ToggleButton itemSwitch;
        ImageView itemInfoIv;
        private LayoutInflater inflater;
        private LinearLayout lineLayout;

        public BrandBeanListItem(final Context context, UEIEntity bean) {
            inflater = LayoutInflater.from(context);
            lineLayout = (LinearLayout) inflater.inflate(cc.wulian.smarthomev5.R.layout.device_23_brand_item, null);
            itemIcon = (ImageView) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_icon_iv);
            item_brandName_tv = (TextView) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_brandName_tv);
            item_brandtype_tv = (TextView) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_brandtype_tv);
            itemInfoTv = (TextView) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_info_tv);
            itemSwitch = (ToggleButton) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_switch);
            itemInfoIv = (ImageView) lineLayout.findViewById(cc.wulian.smarthomev5.R.id.item_info_iv);
            refresh(bean);
        }

        public View getView(UEIEntity bean) {
            refresh(bean);
            return lineLayout;
        }

        public void refresh(final UEIEntity bean) {
            if (viewMode == 1) {
                itemInfoIv.setVisibility(View.GONE);
            }
            if (bean != null) {
                item_brandName_tv.setText(bean.getBrandName() + " " + bean.getBrandTypeName());
                String displayName = bean.getDisplayName();
                if (!StringUtil.isNullOrEmpty(displayName)) {
                    item_brandtype_tv.setVisibility(View.VISIBLE);
                    item_brandtype_tv.setText(displayName);
                } else {
                    item_brandtype_tv.setVisibility(View.GONE);
                }
                itemIcon.setImageDrawable(getDrawable(bean.getSmallIcon()));
            }
        }
    }


    private void initBrandItemList_OnMenuItemClick() {
        this.brandAdapter
                .setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public void onMenuItemClick(int position, SwipeMenu menu,
                                                int index) {
                        if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
                            return;
                        }
                        final UEIEntity item = brandAdapter.getItem(position);
                        switch (index) {
                            case 0: {
                                if (item != null) {
                                    //进入UEI设置界面
                                    Bundle args = new Bundle();
                                    args.putString("settingType", "ueiEdit");
                                    args.putString("brandName", item.getDisplayName());
                                    args.putString("deviceType", item.getDeviceType());
                                    args.putString("deviceKey", item.getKey());
                                    args.putString("devID", devID);
                                    args.putString("gwID", gwID);
                                    args.putString("ep", ep);
                                    args.putString("proCode", item.getProCode());
                                    args.putString("proName", item.getProName());
                                    SmarthomeFeatureImpl.setData("UEIEPGVALUE", item.getValue());
                                    SmarthomeFeatureImpl.setData("UEIEPGK", item.getKey());
                                    Intent intent = new Intent(parentActivity, SettingActivity.class);
                                    intent.putExtra("args", args);
                                    if (WL_23_IR_Control.this.viewMode == 0) {
//									parentActivity.startActivity(intent);
                                        parentActivity.startActivityForResult(intent, 2);
                                    }
                                }
                            }
                            break;
                        }
                    }

                });
    }

    private void initBrandItemList_OnOpenOrClost() {
        // 解决左划删除与右划菜单栏冲突
        deviceList.setOnOpenOrCloseListener(new OpenOrCloseListener() {
            @Override
            public void isOpen(boolean isOpen) {
            }
        });
    }

    private Intent topBoxIntent = null;

    private void initBrandItemList_OnItemClickListener() {
        deviceList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                UEIEntity brandBean = brandAdapter.getItem(position);
                if (brandBean != null) {
                    UeiUiArgs args_value = new UeiUiArgs();
                    args_value.setGwID(getDeviceGwID());
                    args_value.setDevID(getDeviceID());
                    args_value.setAppID(brandBean.getAppID());
                    args_value.setKey(brandBean.getKey());
                    args_value.setTime(brandBean.getTime());
                    args_value.setValue(brandBean.getValue());
                    args_value.setViewMode(WL_23_IR_Control.this.viewMode);
                    args_value.setEp(ep);
                    args_value.setEpType(epType);
                    if (brandBean.getDeviceType().equals(WL_23_IR_Resource.Model_C)) {
                        SmarthomeFeatureImpl.setData("UEIEPGK", brandBean.getKey());
                        UeiCommonEpdata uce = new UeiCommonEpdata();
                        String epgModel = uce.getParseDevideCode(brandBean.getDeviceCode());
                        if (TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
                            Log.d("WL_23_IR_Control", "epgModel=" + epgModel);
                        }
                        SmarthomeFeatureImpl.setData("EPGmodel", epgModel);
                    }
                    Intent intent = new Intent(parentActivity, RemooteControlActivity.class);
                    intent.putExtra("deviceType", brandBean.getDeviceType());
                    Bundle args = new Bundle();
                    args.putParcelable("args", args_value);
                    intent.putExtra("args", args);
                    if (WL_23_IR_Control.this.viewMode == 0) {
                        if(isUsePlugin){
                            //只有机顶盒是需要判断网页
                            boolean isStartActivity = true;
                            if (WL_23_IR_Resource.Model_C.equals(brandBean.getDeviceType())) {
                                String strUri = Preference.getPreferences().getUeiUri();
//                                File file = new File(strUri);
                                if (!strUri.equals("noUri")) {
                                    topBoxIntent = intent;
                                    isStartActivity = false;
                                    getPlugin(false);
                                }
                            }
                            if (isStartActivity) {
                                parentActivity.startActivity(intent);
                            }
                        }else {
                            parentActivity.startActivity(intent);
                        }

                    } else {
                        parentActivity.startActivityForResult(intent, 1);
                    }
                }
            }
        });
    }


    @Override
    public void Reply406Result(Command406Result result) {
        List<Command406Result> results = new ArrayList<>();
        if (result.getMode().equals(Command406_DeviceConfigMsg.mode_delete)) {
			/*if(!StringUtil.isNullOrEmpty(curDeleteFlag)){
				command406.SendCommand_Get();

				mDialogManager.dimissDialog(LOCK_KEY_CLEARITEMS, 0);
				String msg="";
			   if(curDeleteFlag.equals(flag_Clear)){
					msg="清空完成！";
					ueiEntitys.clear();
				}
				else if(curDeleteFlag.equals("test")){

				}
				if(!StringUtil.isNullOrEmpty(msg)){
					Toast.makeText(WL_23_IR_Control.this.mContext,msg, Toast.LENGTH_SHORT).show();
				}
				curDeleteFlag="";
			}*/
        } else if (result.getMode().equals(Command406_DeviceConfigMsg.mode_clear)) {
//            if (curDeleteFlag.equals(flag_Clear)) {
//                UeiCommonEpdata ueiCommand = new UeiCommonEpdata(gwID, devID, ep);
//                ueiCommand.sendCommand12("0A00020903");
//                mDialogManager.dimissDialog(LOCK_KEY_CLEARITEMS, 0);
////				String msg="清空完成！";
//                String msg = getString(cc.wulian.smarthomev5.R.string.main_process_success);
//                if (!StringUtil.isNullOrEmpty(msg)) {
//                    Toast.makeText(WL_23_IR_Control.this.mContext, msg, Toast.LENGTH_SHORT).show();
//                }
//                curDeleteFlag = "";
//            }
            ueiEntitys.clear();
            command406.ClearDbCache();
            command406.setDevID(this.getDeviceID());
            command406.SendCommand_Get();
        } else {
            results.add(result);
        }
        loadData(results);
    }


    @Override
    public void Reply406Result(List<Command406Result> results) {
        loadData(results);
    }

    private UEIEntity findUEI(List<UEIEntity> items, UEIEntity entity) {
        UEIEntity result = null;
        boolean isExsit = false;
        for (UEIEntity item : items) {
            isExsit = item.getGwID().equals(entity.getGwID())
                    && item.getDevID().equals(entity.getDevID())
                    && item.getAppID().equals(entity.getAppID())
                    && item.getKey().equals(entity.getKey());
            if (isExsit) {
                result = item;
                break;
            }
        }
        return result;
    }

    private UEIEntity ConvertToUEIEntity(Command406Result result) {
        UEIEntity entity = null;
        boolean isRight = result != null && result.getKey() != null
                &&
                (
                        !shieldKeys.contains(result.getKey())//特殊key，需要屏蔽掉
                        || !result.getMode().equals(Command406_DeviceConfigMsg.mode_delete)//删除状态的设备不予以显示
                        )
                && !StringUtil.isNullOrEmpty(result.getData())
                &&result.getData().contains(":")
                &&result.getDevID().equals(this.devID);
        if (isRight) {
            entity = UEIEntityManager.ConvertToUEIEntity(getContext(), appID, result);
        }
        return entity;
    }
	/*@Override
	public boolean isDeviceOnLine() {
		return true;
	}*/
    /**
     * 0 设备模块；1 管家模块；
     */
    private int viewMode = -1;

    /**
     * 初始化页面内的子控件
     *
     * @param mode 0 设备模块；1 管家模块；
     */
    private void initChildView(View rootView, int mode) {
        viewMode = mode;
        this.brandAdapter = new BrandAdapter(this.mContext, null);
        command406 = new Command406_DeviceConfigMsg(this.mContext);
        command406.setConfigMsg(this);
        command406.setDevID(devID);
        command406.setGwID(gwID);
        this.ueiEntitys.clear();
        cleartest_layout = (LinearLayout) rootView.findViewById(R.id.cleartest_layout);
        deviceList = (SwipeMenuListView) rootView.findViewById(R.id.device_lv);
        addRemoteControlBtn = (Button) rootView.findViewById(R.id.add_remote_control_btn);
        initBrandItemList_OnItemClickListener();
        if (viewMode == 0) {
            parentActivity = (EventBusActivity) DeviceDetailsActivity.instance;
            addRemoteControlBtn.setOnClickListener(addRemoteControlBtn_onclick);
            this.brandAdapter.setMenuCreator(creatLeftDeleteItem());
            initBrandItemList_OnMenuItemClick();
            initBrandItemList_OnOpenOrClost();
        } else if (viewMode == 1) {
            hidebtn_getepdata = (Button) rootView.findViewById(R.id.hidebtn_getepdata);
            addRemoteControlBtn.setVisibility(View.GONE);
            parentActivity = (EventBusActivity) DeviceSettingActivity.instance;
        }
        deviceList.setAdapter(brandAdapter);
        command406.setDevID(this.getDeviceID());
        command406.SendCommand_Get();
        cleartest_layout.setOnClickListener(testbtn_Onclick);
    }

    //和其它页面的交互回调
    @Override
    public void OnRefreshResultData(Intent data) {
        if (data != null) {
            int requestCode = data.getIntExtra("requestCode", -1);
//			int resultCode=data.getIntExtra("resultCode", -1);
            if (requestCode == 1) {
                String epdata = data.getStringExtra("epdata");
                String desc = data.getStringExtra("desc");
                if (StringUtil.isNullOrEmpty(desc)) {
                    desc = "";
                }
                hidebtn_getepdata.setTag(epdata + "," + desc);
                hidebtn_getepdata.performClick();
            } else if (requestCode == 2) {
                //表示设置界面是否进行了删除操作，若进行了删除操作，则需重写获取数据
                boolean isDelete = data.getBooleanExtra("isDelete", false);
                if (isDelete) {
                    ueiEntitys.clear();
                    command406.setDevID(this.devID);
                    command406.SendCommand_Get();//重写获取数据
                }
            }else if(requestCode == 3){
                ueiEntitys.clear();
                command406.ClearDbCache();
//                command406.SendCommand_Get();
            }
        }
    }

    private String toHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = Integer.parseInt(s.charAt(i) + "");
            String s4 = Integer.toHexString(ch);
            if (s4.length() < 2) {
                s4 = "0" + s4;
            }
            str = str + s4;
        }
        return str.toUpperCase();
    }

    private void getPlugin(final boolean isOpenWebview) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm = PluginsManager.getInstance();
                pm.getHtmlPlugin(mContext, pluginName, new PluginsManager.PluginsManagerCallback() {

                    @Override
                    public void onGetPluginSuccess(PluginModel model) {
                        File file = new File(model.getFolder(), model.getEntry());
                        String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                        if (file.exists()) {
                            uri = "file:///" + file.getAbsolutePath();
                        } else if (LanguageUtil.isChina()) {
                            uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                        }
                        Preference.getPreferences().saveUeiUrl(uri);
                        Preference.getPreferences().saveUeiTopBox_Channel("file:///" + model.getFolder() + "/channelList.html");
                        Preference.getPreferences().saveUeiTopBox_Collection("file:///" + model.getFolder() + "/ConnectionList.html");
                        Preference.getPreferences().saveUeiTopBox_Program("file:///" + model.getFolder() + "/contentList.html");
                        Preference.getPreferences().saveUeiTopBox_Operators("file:///" + model.getFolder() + "/setstation.html");
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, ep);
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPTYPE, epType);
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
                        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
                        if (isOpenWebview) {
                            Intent intent = new Intent();
                            intent.setClass(mContext, Html5PlusWebViewActvity.class);
                            intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
                            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
                            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
                            mContext.startActivity(intent);
                        } else {
                            if (topBoxIntent != null) {
                                parentActivity.startActivity(topBoxIntent);
                            }
                        }
                    }

                    @Override
                    public void onGetPluginFailed(final String hint) {
                        if (hint != null && hint.length() > 0) {
                            Handler handler = new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }


    View.OnClickListener testbtn_Onclick = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent();
            Bundle args = new Bundle();
            args.putString("devID", devID);
            args.putString("gwID", gwID);
            intent.putExtra("args", args);
            intent.setClass(WL_23_IR_Control.this.mContext, TestApi_406Activity.class);
            WL_23_IR_Control.this.mContext.startActivity(intent);
        }
    };
}
