package cc.wulian.app.model.device;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.server.controller.CamPresetting;
import com.wulian.iot.server.controller.logic.CamPresettingLogicImpl;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.manage.PresettingManager;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.ControlEPDataListener;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceRequestListener;
import cc.wulian.app.model.device.interfaces.OnWulianDeviceStateChangeListener;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.WLPresettingDataManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.R;

public class DesktopCameraDevice extends AbstractDevice {

    private Resources mResources;

    private WLDialog mPresetDialogCheck;
    private WLDialog mShortVideoDialogCheck;

    private DesktopCameraTimeView desktopCameraTimeView;

    @ViewInject(R.id.desktop_preset_show_textview)
    private TextView presetShowTextView;
    @ViewInject(R.id.desktop_preset_click_imgview)
    private ImageView presetClickImagView;

    @ViewInject(R.id.desktop_motion_detection_switch)
    private ToggleButton motionDetextionSwitch;
    @ViewInject(R.id.desktop_motion_detection_click_imgview)
    private ImageView motionDetextionClickImagView;

    @ViewInject(R.id.desktop_continue_video_switch)
    private ToggleButton contimueVideoSwitch;
    @ViewInject(R.id.desktop_continue_video_click_imgview)
    private ImageView contimueVideoClickImagView;

    @ViewInject(R.id.desktop_short_video_show_textview)
    private TextView shortVideoShowTextView;
    @ViewInject(R.id.desktop_short_video_click_imgview)
    private ImageView shortVideoClickImagView;

    @ViewInject(R.id.preset_choose_big_one)
    private ImageView presetChooseBigOne;

    @ViewInject(R.id.preset_choose_big_two)
    private ImageView presetChooseBigTwo;

    @ViewInject(R.id.preset_choose_big_three)
    private ImageView presetChooseBigThree;

    @ViewInject(R.id.preset_choose_big_four)
    private ImageView presetChooseBigFour;

    @ViewInject(R.id.preset_choose_framelayout_three)
    private LinearLayout presetChooseFrameLayoutThree;

    @ViewInject(R.id.preset_choose_name_one)
    private TextView presetChooseNameOne;
    @ViewInject(R.id.preset_choose_name_two)
    private TextView presetChooseNameTwo;
    @ViewInject(R.id.preset_choose_name_three)
    private TextView presetChooseNameThree;
    @ViewInject(R.id.preset_choose_name_four)
    private TextView presetChooseNameFour;

    @ViewInject(R.id.preset_choose_linearlayout_one)
    private LinearLayout presetChooseLinearLayoutOne;
    @ViewInject(R.id.preset_choose_linearlayout_two)
    private LinearLayout presetChooseLinearLayoutTwo;

    // TODO 最后修改此处
    private static TextView shortCutTextView;
    // 预置位 add syf
    private static CamPresetting camPresettingImpl = null;
    private AccountManager accountManager = AccountManager.getAccountManger();
    private GatewayInfo info = accountManager.getmCurrentInfo();
    private static List<PresettingModel> presettingModels = null;
    private static WLPresettingDataManager wlPresettingDataManager;

    public DesktopCameraDevice(Context context, String type) {
        super(context, type);
        mContext = context;
        mResources = context.getResources();
        camPresettingImpl = new CamPresettingLogicImpl(mContext);
    }

    @Override
    public Drawable getDefaultStateSmallIcon() {
        return mResources
                .getDrawable(R.drawable.desktop_camera_manager_show_icon);
    }

    @Override
    public Drawable getStateSmallIcon() {
        // TODO Auto-generated method stub
        return mResources
                .getDrawable(R.drawable.desktop_camera_manager_show_icon);
    }

    @Override
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
            DeviceShortCutSelectDataItem item, LayoutInflater inflater,
            AutoActionInfo autoActionInfo) {
        if (item == null) {
            item = new ShortCutDesktopCameraSelectDataItem(
                    inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }

    public static class ShortCutDesktopCameraSelectDataItem extends
            DeviceShortCutSelectDataItem {

        protected LinearLayout defaultLineLayout;

        public ShortCutDesktopCameraSelectDataItem(Context context) {
            super(context);
            defaultLineLayout = (LinearLayout) inflater.inflate(
                    R.layout.desktop_short_cut_view, null);
            shortCutTextView = (TextView) defaultLineLayout
                    .findViewById(R.id.desktop_short_cut_textview);
            controlLineLayout.addView(defaultLineLayout);
        }

        @Override
        public void setWulianDeviceAndSelectData(final WulianDevice device,
                                                 final AutoActionInfo autoActionInfo) {
            super.setWulianDeviceAndSelectData(device, autoActionInfo);
            AccountManager accountManager = AccountManager.getAccountManger();
            GatewayInfo info = accountManager.getmCurrentInfo();
//            getPosition(info.getGwID(), new GetPresetPosiTionListener() {
//                @Override
//                public void doSomeThing() {
//                    if (!(autoActionInfo.getEpData() == null || autoActionInfo
//                            .getEpData().equals(""))) {
//
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
//                                ((Activity) mContext).runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        switch (autoActionInfo.getEpData().charAt(0) + "") {
//                                            case "F":
//                                                switch (autoActionInfo.getEpData()) {
//                                                    case "F1":
//                                                        setPresetShortCutTextView(0);
//                                                        break;
//                                                    case "F2":
//                                                        setPresetShortCutTextView(1);
//                                                        break;
//                                                    case "F3":
//                                                        setPresetShortCutTextView(2);
//                                                        break;
//                                                    case "F4":
//                                                        setPresetShortCutTextView(3);
//                                                        break;
//                                                }
//                                                break;
//                                        }
//                                    }
//                                });
//                            }
//                        }).start();
//                    }
//                }
//            });
            if (!(autoActionInfo.getEpData() == null || autoActionInfo
                    .getEpData().equals(""))) {
                switch (autoActionInfo.getEpData().charAt(0) + "") {
                    case "F":
                        switch (autoActionInfo.getEpData()) {
                            case "F1":
                                setPresetShortCutTextView(1);
                                break;
                            case "F2":
                                setPresetShortCutTextView(2);
                                break;
                            case "F3":
                                setPresetShortCutTextView(3);
                                break;
                            case "F4":
                                setPresetShortCutTextView(4);
                                break;
                        }
                        break;
                    case "A":
                        switch (autoActionInfo.getEpData()) {
                            case "A1":
                                shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_motion_detection_textview) + ":" + mContext.getResources().getString(R.string.desktop_camera_manager_preset_open));
                                break;
                            case "A0":
                                shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_motion_detection_textview) + ":" + mContext.getResources().getString(R.string.house_rule_condition_device_status_close));
                                break;
                        }
                        break;
                    case "D":
                        shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_continue_video_textview) + ":" + mContext.getResources().getString(R.string.desktop_camera_manager_preset_open));
                        break;
                    case "E":
                        shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_continue_video_textview) + ":" + mContext.getResources().getString(R.string.house_rule_condition_device_status_close));
                        break;
                    case "C":
                        shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_short_video_textview) + ":" + Integer.parseInt(autoActionInfo.getEpData().substring(1)) + "s");
                        break;
                    default:
                        shortCutTextView.setText(mContext.getResources().getString(R.string.html_user_hint_not_set));
                        break;
                }
            }

        }

        private void setPresetShortCutTextView(int position) {
            shortCutTextView.setText(mContext.getResources().getString(R.string.desktop_preset_textview) + ":" +position);
        }

    }

    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
            LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        holder.setShowDialog(false);
        View contentView = inflater.inflate(R.layout.desktop_camera_edit_task,
                null);
        ViewUtils.inject(this, contentView);
        holder.setContentView(contentView);
        getPosition(info.getGwID(), new GetPresetPosiTionListener() {
            @Override
            public void doSomeThing() {

            }
        });

        if (!(autoActionInfo.getEpData() == null || autoActionInfo
                .getEpData().equals(""))) {
            switch (autoActionInfo.getEpData().charAt(0) + "") {
                case "F":
                    initItemStatus();
                    presetClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    break;
                case "A":
                    initItemStatus();
                    motionDetextionClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    break;
                case "D":
                    initItemStatus();
                    contimueVideoClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    break;
                case "E":
                    initItemStatus();
                    contimueVideoClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    break;
                case "C":
                    initItemStatus();
                    shortVideoClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    break;
                default:
                    shortCutTextView.setText(mContext.getResources().getString(R.string.html_user_hint_not_set));
                    break;
            }
        }

        motionDetextionSwitch
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        // TODO Auto-generated method stub
                        if (arg1) {
                            autoActionInfo.setEpData("A1");
                        } else {
                            autoActionInfo.setEpData("A0");
                        }
                    }
                });
        contimueVideoSwitch
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton arg0,
                                                 boolean arg1) {
                        // TODO Auto-generated method stub
                        if (arg1) {
                            autoActionInfo.setEpData("D");
                        } else {
                            autoActionInfo.setEpData("E");
                        }
                    }
                });
        presetClickImagView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initItemStatus();
                if (presettingModels == null) {
                    WLToast.showToast(mContext, mContext.getResources().getString(R.string.data_recving), WLToast.TOAST_SHORT);
                } else {
                    presetClickImagView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.device_security_switch_on_0));
                    presetShowTextView.setVisibility(View.VISIBLE);
                    showPresetDialog(autoActionInfo);
                }
            }
        });
        motionDetextionClickImagView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initItemStatus();
                motionDetextionClickImagView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.device_security_switch_on_0));
                motionDetextionSwitch.setVisibility(View.VISIBLE);
                autoActionInfo.setEpData("A0");
            }
        });
        contimueVideoClickImagView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initItemStatus();
                contimueVideoClickImagView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.device_security_switch_on_0));
                contimueVideoSwitch.setVisibility(View.VISIBLE);
                autoActionInfo.setEpData("E");
            }
        });
        shortVideoClickImagView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                initItemStatus();
                showChooseTimeDialog(autoActionInfo);
                shortVideoClickImagView.setImageDrawable(getResources()
                        .getDrawable(R.drawable.device_security_switch_on_0));
                shortVideoShowTextView.setVisibility(View.VISIBLE);
            }
        });
        holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
        return holder;
    }

    private void showChooseTimeDialog(final AutoActionInfo autoActionInfo) {
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setTitle(R.string.gateway_dream_flower_time_show_select_time);
        builder.setContentView(createViewTime());
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(android.R.string.cancel);
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {
                mShortVideoDialogCheck.dismiss();
                autoActionInfo.setEpData("C00"
                        + desktopCameraTimeView.getSettingMinuesTime());
                shortVideoShowTextView.setText(Integer.parseInt(desktopCameraTimeView.getSettingMinuesTime()) + "s");
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                mShortVideoDialogCheck.dismiss();
                autoActionInfo.setEpData("C00"
                        + desktopCameraTimeView.getSettingMinuesTime());
            }
        });
        mShortVideoDialogCheck = builder.create();
        mShortVideoDialogCheck.show();
    }

    private View createViewTime() {
        desktopCameraTimeView = new DesktopCameraTimeView(mContext);
        return desktopCameraTimeView;
    }

    private void initItemStatus() {
        presetShowTextView.setVisibility(View.INVISIBLE);
        motionDetextionSwitch.setVisibility(View.INVISIBLE);
        contimueVideoSwitch.setVisibility(View.INVISIBLE);
        shortVideoShowTextView.setVisibility(View.INVISIBLE);
        presetClickImagView.setImageDrawable(getResources().getDrawable(
                R.drawable.device_security_switch_on_1));
        motionDetextionClickImagView.setImageDrawable(getResources()
                .getDrawable(R.drawable.device_security_switch_on_1));
        contimueVideoClickImagView.setImageDrawable(getResources().getDrawable(
                R.drawable.device_security_switch_on_1));
        shortVideoClickImagView.setImageDrawable(getResources().getDrawable(
                R.drawable.device_security_switch_on_1));
    }

    // 获得预置位信息
    public static void getPosition(String gwID, final GetPresetPosiTionListener getPresetPosiTionListener) {
        wlPresettingDataManager = new WLPresettingDataManager();
        wlPresettingDataManager.sendCommonDeviceConfigMsg();
        wlPresettingDataManager.setSmit406MsgCallback(new WLPresettingDataManager.Smit406MsgCallback() {
            @Override
            public void retrunData(PresettingManager.Return406 return406) {
                presettingModels = camPresettingImpl.findPresettingListAll(return406.getGwId(), return406.getSmit406_Pojo_Items());
                getPresetPosiTionListener.doSomeThing();
            }
        });
//		if(presettingModels.size()==0){
//			return presettingModels = camPresettingImpl
//					.getDefaultPresettingList();
//		}else{
//			return presettingModels = camPresettingImpl
//					.groupPresettingList(presettingModels);
//		}
    }

    interface GetPresetPosiTionListener {
        void doSomeThing();
    }

    // 弹出预置位的选择对话框
    private void showPresetDialog(final AutoActionInfo autoActionInfo) {

        WLDialog.Builder mBuilder = new WLDialog.Builder(mContext);
        // LayoutInflater inflater = (LayoutInflater) mContext
        // .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.desktop_preset_choose, null);
        ViewUtils.inject(this, view);
        // 对返回的预置位信息进行处理
        presetChooseNameFour.setText(presettingModels.get(3).getpName().equals("") ? getString(R.string.html_user_hint_not_set) : presettingModels.get(3).getpName());
        presetChooseNameThree.setText(presettingModels.get(2).getpName().equals("") ? getString(R.string.html_user_hint_not_set) : presettingModels.get(2).getpName());
        presetChooseNameTwo.setText(presettingModels.get(1).getpName().equals("") ? getString(R.string.html_user_hint_not_set) : presettingModels.get(1).getpName());
        presetChooseNameOne.setText(presettingModels.get(0).getpName().equals("") ? getString(R.string.html_user_hint_not_set) : presettingModels.get(0).getpName());

        presetChooseBigOne.setImageDrawable(presettingModels.get(0)
                .getpImg());
        presetChooseBigTwo.setImageDrawable(presettingModels.get(1)
                .getpImg());
        presetChooseBigThree.setImageDrawable(presettingModels.get(2)
                .getpImg());
        presetChooseBigFour.setImageDrawable(presettingModels.get(3)
                .getpImg());
        presetChooseBigOne.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (!presettingModels.get(0).getpName().equals("")) {
                    autoActionInfo.setEpData("F1");
                    presetShowTextView.setText(presettingModels.get(0).getpName());
                    mPresetDialogCheck.dismiss();
                }
            }
        });
        presetChooseBigTwo.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!presettingModels.get(1).getpName().equals("")) {
                    autoActionInfo.setEpData("F2");
                    presetShowTextView.setText(presettingModels.get(1).getpName());
                    mPresetDialogCheck.dismiss();

                }
            }
        });
        presetChooseBigThree.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!presettingModels.get(2).getpName().equals("")) {
                    autoActionInfo.setEpData("F3");
                    presetShowTextView.setText(presettingModels.get(2).getpName());
                    mPresetDialogCheck.dismiss();
                }
            }
        });
        presetChooseBigFour.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (!presettingModels.get(3).getpName().equals("")) {
                    autoActionInfo.setEpData("F4");
                    presetShowTextView.setText(presettingModels.get(3).getpName());
                    mPresetDialogCheck.dismiss();
                }
            }
        });
        mBuilder.setContentView(view)
                .setPositiveButton(null)
                .setNegativeButton(null)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View contentViewLayout) {

                        mPresetDialogCheck.dismiss();

                    }

                    @Override
                    public void onClickNegative(View contentViewLayout) {
                        mPresetDialogCheck.dismiss();
                    }
                });
        mPresetDialogCheck = mBuilder.create();
        mPresetDialogCheck.show();
    }

    @Override
    public String getDefaultDeviceName() {
        // TODO Auto-generated method stub
        return getString(R.string.WL_DESKTOP_CAMERA);
    }

    @Override
    public Drawable[] getStateBigPictureArray() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAttachView(Context context) {
        // TODO Auto-generated method stub

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveState) {
        // TODO Auto-generated method stub
        return new TextView(mContext);
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub

    }

    @Override
    public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DeviceShortCutControlItem onCreateShortCutView(
            DeviceShortCutControlItem item, LayoutInflater inflater) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isAutoControl(boolean isNormal) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setControlEPDataListener(ControlEPDataListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
                                                 String ep, String epData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
                                                     TaskInfo taskInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
            LayoutInflater inflater, AutoConditionInfo autoConditionInfo,
            boolean isTriggerCondition) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void initViewStatus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDetachView() {
        // TODO Auto-generated method stub

    }

    @Override
    public MoreMenuPopupWindow getDeviceMenu() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int compareTo(WulianDevice arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void onDeviceUp(DeviceInfo devInfo) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo,String cmd,String mode) {
        // TODO Auto-generated method stub

    }

//    @Override
//    public void onDeviceSet(DeviceInfo devInfo, DeviceEPInfo devEPInfo) {
//        // TODO Auto-generated method stub
//
//    }

    @Override
    public void onDeviceDestory(String gwID, String devID) {
        // TODO Auto-generated method stub

    }

    @Override
    public void refreshDevice() {
        // TODO Auto-generated method stub

    }

    @Override
    public String getDefaultEndPoint() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDeviceGwID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDeviceID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDeviceType() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDeviceName() {
        // TODO Auto-generated method stub
        return getString(R.string.WL_DESKTOP_CAMERA);
    }

    @Override
    public String getDeviceRoomID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isDeviceOnLine() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void setDeviceOnLineState(boolean isOnLine) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDeviceUseable() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void setDeviceParent(WulianDevice parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public WulianDevice getDeviceParent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void registerStateChangeListener(
            OnWulianDeviceStateChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterStateChangeListener(
            OnWulianDeviceStateChangeListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void registerControlRequestListener(
            OnWulianDeviceRequestListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public void unregisterControlRequestListener(
            OnWulianDeviceRequestListener listener) {
        // TODO Auto-generated method stub

    }

    @Override
    public Map<String, WulianDevice> getChildDevices() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDeviceCategory() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void controlDevice(String ep, String epType, String epData) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isLinkControl() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public CharSequence parseDestoryProtocol(String epData) {
        // TODO Auto-generated method stub
        return null;
    }
}
