package cc.wulian.app.model.device.impls.controlable.newthermostat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yuantuo.customview.ui.WLDialog;

import org.w3c.dom.Text;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.CurAutoTempListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.CurFanModeListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.CurModeListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.CurStateListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.CurTempListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.ProgramBtnListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.SettingBtnListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider.ToSetBtnListener;
import cc.wulian.app.model.device.impls.controlable.newthermostat.program.ThermostatProgramActivity;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingActivity;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingFragment;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment.EquipmentSettingActivity;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.event.MessageEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.Logger;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import de.greenrobot.event.EventBus;

/**
 * 美标温控器
 *
 * @author hanx
 */

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_82}, category = Category.C_CONTROL)
public class WL_82_Thermostat extends ControlableDeviceImpl implements Alarmable {
    private static final String TAG = "WL_82_Thermostat:";

    private Thermostat82ViewBulider mViewBulider;

    private String mAlarmEpData;

    /**
     * 返回标志位（02开关设置03模式设置04分机设置05温标设置06设备工作模式设置07时间设置08温度相关09声音设置0A紧急制热0B用户编程热冷0C用户编程自动）
     */
    private String mReturnId;

    /**
     * 开关 off:0  on:1
     */
    private String mOnOff;

    /**
     * 风机 1=automatic 2=always on
     */
    private String mFanMode;
    /**
     * 温度单位：0摄氏度，1华氏度
     */
    private String mTemperatureUnit;
    /**
     * 供热方式
     */
    private String mTemperatureType;
    /**
     * 系统类型(如 一级制冷)
     */
    private String mSystemType;
    /**
     * 模式 1=heat 2=cool 3=auto
     */
    private String mMode;
    /**
     * 制热温度按照10进制(扩大一百倍)
     */
    private String mHeatTemperature;
    /**
     * 制冷温度按照10进制(扩大一百倍)
     */
    private String mCoolTemperature;
    /**
     * 当前温度 16进制－》10进制－》除10
     */
    private String mCurrentTemperature;
    /**
     * 当前环境湿度
     */
    private String mCurrentHumidity;
    /**
     * 自动制热温度按照10进制(扩大一百倍)
     */
    private String mAutoHeatTemperature;
    /**
     * 自动制冷温度按照10进制(扩大一百倍)
     */
    private String mAutoCoolTemperature;
    /**
     * 紧急制热数据
     */
    private String mEmergencyHeat;
    /**
     * 设置锁定
     */
    private String mLocked;

    private static final String HOUSE_KEEPER_MESSAGE ="Thermostat should be turned on while you performing the task!";

    //状态 开关 指令
    private static final String STATE_DATA_TAG = "2";
    private static final String STATE_DATA_ON = "1";
    private static final String STATE_DATA_OFF = "0";
    // 开关  发送指令
    private static final String STATE_CMD_ON = createCompoundCmd(STATE_DATA_TAG, STATE_DATA_ON);
    private static final String STATE_CMD_OFF = createCompoundCmd(STATE_DATA_TAG, STATE_DATA_OFF);

    //查询当前状态指令
    private static final String CURRENT_QUERY_TAG = "1";
    private static final String CURRENT_QUERY_DATA = "1";  //查询基本数据
    private static final String CURRENT_QUERY_MODE = "2";  //查询模式数据
    //查询  发送指令
    private static final String CURRENT_QUERY_CMD_DATA = createCompoundCmd(CURRENT_QUERY_TAG, CURRENT_QUERY_DATA);
    private static final String CURRENT_QUERY_CMD_MODE = createCompoundCmd(CURRENT_QUERY_TAG, CURRENT_QUERY_MODE);
    //风机 模式  指令
    private static final String FNMODE_DATA_TAG = "4";
    private static final String FNMODE_DATA_AUTO = "1";
    private static final String FNMODE_DATA_ON = "2";
    //风机 模式  发送指令
    private static final String FNMODE_CMD_AUTO = createCompoundCmd(FNMODE_DATA_TAG, FNMODE_DATA_AUTO);
    private static final String FNMODE_CMD_ON = createCompoundCmd(FNMODE_DATA_TAG, FNMODE_DATA_ON);

    //工作 模式 指令
    private static final String MODE_DATA_TAG = "3";
    private static final String MODE_DATA_HEAT = "1";
    private static final String MODE_DATA_COOL = "2";
    private static final String MODE_DATA_AUTO = "3";
    //工作 模式  发送指令
    private static final String MODE_CMD_HEAT = createCompoundCmd(MODE_DATA_TAG, MODE_DATA_HEAT);
    private static final String MODE_CMD_COOL = createCompoundCmd(MODE_DATA_TAG, MODE_DATA_COOL);
    private static final String MODE_CMD_AUTO = createCompoundCmd(MODE_DATA_TAG, MODE_DATA_AUTO);
    // 模式切换标志     0：改变模式    1：改变温度
    private static final String MODE_CHANGE_TAG = "0";
    private static final String TEMP_CHANGE_TAG = "1";
    //温度改变 指令
    private static final String TEMP_CMD_HEAT = createCompoundCmd(MODE_CMD_HEAT, TEMP_CHANGE_TAG);
    private static final String TEMP_CMD_COOL = createCompoundCmd(MODE_CMD_COOL, TEMP_CHANGE_TAG);
    private static final String TEMP_CMD_AUTO = createCompoundCmd(MODE_CMD_AUTO, TEMP_CHANGE_TAG);


    public WL_82_Thermostat(Context context, String type) {
        super(context, type);
    }

    @Override
    public MoreMenuPopupWindow getDeviceMenu() {
        return super.getDeviceMenu();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        mViewBulider = new Thermostat82ViewBulider(inflater.getContext());
        return mViewBulider.getContentView();
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        mViewBulider.initThermostat();
        fireWulianDeviceRequestControlSelf();
        controlDevice(ep, epType, CURRENT_QUERY_CMD_DATA);
        controlDevice(ep, epType, CURRENT_QUERY_CMD_MODE);
        //开关状态监听
        mViewBulider.setCurStateListener(new CurStateListener() {

            @Override
            public void onStateChanged(boolean isOpen) {
                if (isOpen) {
                    controlDevice(ep, epType, STATE_CMD_ON);
                } else {
                    controlDevice(ep, epType, STATE_CMD_OFF);
                }

            }
        });

        // 风机开关监听
        mViewBulider.setCurFanModeListener(new CurFanModeListener() {

            @Override
            public void onFanModeChanged(String fnMode) {
                if (isSameAs(fnMode, mViewBulider.FANMODE_AUTO)) {
                    controlDevice(ep, epType, FNMODE_CMD_AUTO);
                } else {
                    controlDevice(ep, epType, FNMODE_CMD_ON);
                }

            }
        });

        //模式改变监听
        mViewBulider.setCurModeListener(new CurModeListener() {

            @Override
            public void onModelChanged(String model) {
                if (isSameAs(model, mViewBulider.MODE_HEAT)) {
                    controlDevice(ep, epType, MODE_CMD_HEAT);
                } else if (isSameAs(model, mViewBulider.MODE_COOL)) {
                    controlDevice(ep, epType, MODE_CMD_COOL);
                } else {
                    controlDevice(ep, epType, MODE_CMD_AUTO);
                }
                controlDevice(ep, epType, CURRENT_QUERY_CMD_MODE);

            }
        });

        //当前温度监听 制热和制冷温度
        mViewBulider.setCurTempListener(new CurTempListener() {

            @Override
            public void onTempChanged(String temp) {
                String mHeatTempCmd;
                String mCoolTempCmd;
                if (isSameAs(mMode, mViewBulider.MODE_HEAT)) {
                    mCoolTempCmd = "0000";
                    mHeatTempCmd = setTempCmd(temp);
                    String tempChangeCmd = createCompoundCmd(mHeatTempCmd, mCoolTempCmd);
                    String tempCmd = createCompoundCmd(TEMP_CMD_HEAT, tempChangeCmd);
                    SendMessage.sendControlDevMsg(gwID, devID, ep, epType, tempCmd);
                } else if (isSameAs(mMode, mViewBulider.MODE_COOL)) {
                    mHeatTempCmd = "0000";
                    mCoolTempCmd = setTempCmd(temp);
                    String tempChangeCmd = createCompoundCmd(mHeatTempCmd, mCoolTempCmd);
                    String tempCmd = createCompoundCmd(TEMP_CMD_COOL, tempChangeCmd);
                    SendMessage.sendControlDevMsg(gwID, devID, ep, epType, tempCmd);
                } else {
                }

            }
        });

        //Auto模式下 制热 制冷温度改变监听
        mViewBulider.setCurAutoTempListener(new CurAutoTempListener() {

            @Override
            public void onTempChanged(String temp1, String temp2) {
                String mHeatTempCmd = setTempCmd(temp1);
                String mCoolTempCmd = setTempCmd(temp2);
                String tempChangeCmd = createCompoundCmd(mHeatTempCmd, mCoolTempCmd);
                String tempCmd = createCompoundCmd(TEMP_CMD_AUTO, tempChangeCmd);
                SendMessage.sendControlDevMsg(gwID, devID, ep, epType, tempCmd);
            }
        });

        mViewBulider.setProgramBtnListener(new ProgramBtnListener() {

            @Override
            public void onProgramBtnClick() {

                if (isStateOff()) {
                    return;
                }
                jumpToProgramActivity();
            }
        });

        mViewBulider.setSettingBtnListener(new SettingBtnListener() {

            @Override
            public void onSettingBtnClick() {

                Intent settingIntent = getSettingIntent();

                mContext.startActivity(settingIntent);
            }
        });

        mViewBulider.setToSetBtnListener(new ToSetBtnListener() {

            @Override
            public void onToSetBtnClick() {

                jumpToEquipmentSettingActivity();
            }
        });
    }

    private String setTempCmd(String temp) {
        String tempCmd = "";
        if (isTempFormatC()) {
            double tempdou = Double.parseDouble(temp) * 100;
            String tempstr = String.valueOf((int)tempdou);
            tempCmd = StringUtil.appendLeft(tempstr, 4, '0');
        } else {
            double tempdou = Double.parseDouble((int) (Math.round((Float.parseFloat(temp) - 32) / (1.8))) + "") * 100;
            String tempstr = String.valueOf((int)tempdou );
            tempCmd = StringUtil.appendLeft(tempstr, 4, '0');
        }

        return tempCmd;
    }

    private void jumpToEquipmentSettingActivity() {
        Intent intent = new Intent(mContext, EquipmentSettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThermostatSettingFragment.GWID, gwID);
        bundle.putString(ThermostatSettingFragment.DEVID, devID);
        bundle.putString(ThermostatSettingFragment.EP, ep);
        bundle.putString(ThermostatSettingFragment.EPTYPE, epType);
        intent.putExtra("EquipmentSettingInfo", bundle);
        mContext.startActivity(intent);

    }

    private void jumpToProgramActivity() {
        Intent intent = new Intent(mContext, ThermostatProgramActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThermostatSettingFragment.GWID, gwID);
        bundle.putString(ThermostatSettingFragment.DEVID, devID);
        bundle.putString(ThermostatSettingFragment.EP, ep);
        bundle.putString(ThermostatSettingFragment.EPTYPE, epType);
        bundle.putString("mode", mMode);
        bundle.putString("tempUnit", mTemperatureUnit);
        intent.putExtra("ThermostatProgramInfo", bundle);
        mContext.startActivity(intent);
    }

    private Intent getSettingIntent() {
        Intent intent = new Intent(mContext, ThermostatSettingActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(ThermostatSettingFragment.GWID, gwID);
        bundle.putString(ThermostatSettingFragment.DEVID, devID);
        bundle.putString(ThermostatSettingFragment.EP, ep);
        bundle.putString(ThermostatSettingFragment.EPTYPE, epType);
        bundle.putString("mode", mMode);
        bundle.putString("tempType", mTemperatureType);
        bundle.putString("tempUnit", mTemperatureUnit);
        intent.putExtra("ThermostatSettingInfo", bundle);
        return intent;
    }

    //接收数据
    @Override
    public void refreshDevice() {
        super.refreshDevice();
        disassembleCompoundCmd(epData);
    }

    private void disassembleCompoundCmd(String epData) {
        if (!isNull(epData)) {
            Logger.i(TAG + "-epData:", epData + "-"+ epData.length());
            if (epData.length() == 38) {
                mReturnId = epData.substring(0, 2);
                Logger.i(TAG + "mReturnId", mReturnId);
                mOnOff = epData.substring(2, 4);
                Logger.i(TAG + "mOnOff", mOnOff);
                mFanMode = epData.substring(4, 6);
                Logger.i(TAG + "mFanMode", mFanMode);
                mTemperatureUnit = epData.substring(6, 8);
                Logger.i(TAG + "mTemperatureUnit", mTemperatureUnit);
                mTemperatureType = epData.substring(8, 10);
                Logger.i(TAG + "mTemperatureType", mTemperatureType);
                mSystemType = epData.substring(10, 12);
                Logger.i(TAG + "mSystemType", mSystemType);
                mMode = epData.substring(12, 14);
                Logger.i(TAG + "mMode", mMode);
                mHeatTemperature = epData.substring(14, 18);
                Logger.i(TAG + "mHeatTemperature", mHeatTemperature);

                mCoolTemperature = epData.substring(18, 22);
                Logger.i(TAG + "mCoolTemperature", mCoolTemperature);
                mCurrentTemperature = epData.substring(22, 26);
                Logger.i(TAG + "mCurrentTemperature", mCurrentTemperature);
                mCurrentHumidity = epData.substring(26, 28);
                Logger.i(TAG + "mCurrentHumidity", mCurrentHumidity);
                mAutoHeatTemperature = epData.substring(28, 32);
                Logger.i(TAG + "mAutoHeatTemperature", mAutoHeatTemperature);
                mAutoCoolTemperature = epData.substring(32, 36);
                Logger.i(TAG + "mAutoCoolTemperature", mAutoCoolTemperature);
                mLocked = epData.substring(36, 38);
                Logger.i(TAG + "mLocked", mLocked);
            }
            if(epData.length() == 28){
                mReturnId = epData.substring(0, 2);
                mEmergencyHeat = epData.substring(12, 14);
            }
        }

    }

    @Override
    public void initViewStatus() {
        super.initViewStatus();
        initThermostatView();
    }

    private void initThermostatView() {
        initTempratureUnit();
        initModeView();
        initModeTemperature();
        initCurrentTemperature();
        initCurrentProgress();
    }

    /**
     * 根据模式 设置界面
     */
    private void initModeView() {
        mViewBulider.setMode(mMode, mFanMode, mOnOff);
        mViewBulider.setIdAndSystemType(mReturnId, mSystemType);
        mViewBulider.setEmergencyHeat(mEmergencyHeat);
        mViewBulider.initModeView();

    }

    /**
     * 设置各种模式下  温度
     */
    private void initModeTemperature() {
        mViewBulider.setHeatTemp(FloorWarmUtil.hexStr2Str100(mHeatTemperature));
        mViewBulider.setCoolTemp(FloorWarmUtil.hexStr2Str100(mCoolTemperature));
        mViewBulider.setAutoHeatTemp(FloorWarmUtil.hexStr2Str100(mAutoHeatTemperature));
        mViewBulider.setAutoCoolTemp(FloorWarmUtil.hexStr2Str100(mAutoCoolTemperature));

    }

    /**
     * 设置拖动条  温度显示
     */
    private void initCurrentProgress() {
        mViewBulider.setCurProgress();
    }

    /**
     * 设置温度单位
     */
    private void initTempratureUnit() {
        mViewBulider.setTempUnit(mTemperatureUnit);
    }

    /**
     * 设置温度显示
     */
    private void initCurrentTemperature() {
        mViewBulider.setTemperatureView(FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str10(mCurrentTemperature)));

    }

    @Override
    public String getOpenSendCmd() {
        return STATE_CMD_ON;
    }

    @Override
    public String getCloseSendCmd() {
        return STATE_CMD_OFF;
    }

    @Override
    public boolean isStoped() {
        return super.isStoped();
    }

    @Override
    public String getStopSendCmd() {
        return super.getStopSendCmd();
    }

    @Override
    public String getStopProtocol() {
        return super.getStopProtocol();
    }

    @Override
    public String getOpenProtocol() {
        return super.getOpenProtocol();
    }

    @Override
    public String getCloseProtocol() {
        return super.getCloseProtocol();
    }

    @Override
    public boolean isOpened() {
        return !isClosed();
    }

    @Override
    public boolean isClosed() {
        return isStateOff();
    }

    public boolean isStateOff() {
        return isSameAs(mOnOff, Thermostat82ViewBulider.STATE_OFF);
    }

    public boolean isTempFormatC() {
        return isSameAs(mTemperatureUnit, Thermostat82ViewBulider.TEMP_UNIT_C);
    }

    @Override
    public boolean isNormal() {
        return false;
    }

    @Override
    public String getCancleAlarmProtocol() {
        return null;
    }

    @Override
    public String getAlarmProtocol() {
        return null;
    }

    @Override
    public String getNormalProtocol() {
        return null;
    }

    @Override
    public String getAlarmString() {
        return null;
    }

    @Override
    public String getNormalString() {
        return null;
    }

    @Override
    public boolean isDestory() {
        return false;
    }

    @Override
    public boolean isAlarming() {
        return false;
    }

    @Override
    public boolean isLowPower() {
        return false;
    }

    @Override
    public CharSequence parseAlarmProtocol(String data) {

        return null;
    }

    //温控器报警
    @Override
    public CharSequence parseDestoryProtocol(String data) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceTool.getDeviceAlarmAreaName(this));
        sb.append(DeviceTool.getDeviceShowName(this));
        if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
            sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
        } else {
            sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect)+ " ");
        }
        if (isSameAs(data, "0101")) {   //欠压报警
            sb.append(mContext.getString(R.string.rd_69_28));
        }
        else if (isSameAs(data, "0301")) {  //断电报警
            sb.append(mContext.getString(R.string.floor_feating_power_off));
        }
        return sb.toString();
    }

    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new ControlableThermostatShortCutControlItem(
                    inflater.getContext());
        }
        item.setWulianDevice(this);
        return item;
    }

    //不显示设备列表中控制按钮
    protected class ControlableThermostatShortCutControlItem extends
            DeviceShortCutControlItem {

        protected ControlableThermostatShortCutControlItem(Context context) {
            super(context);
            View view = new View(mContext);
            controlLineLayout.addView(view);
        }

    }

    /**
     * 美标温控器管家场景列表显示
     */
    @Override
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item,
                                                                       LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        if(item == null){
            item = new ShortCutSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }

    /**
     * 美标温控器管家场景列表显示条目
     */
    private class ShortCutSelectDataItem extends DeviceShortCutSelectDataItem{

        private LinearLayout controlableLineLayout;
        private TextView controlableTV;
        private ImageView controlableImage;

        public ShortCutSelectDataItem(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_thermostat82_cut_control_controlable, null);
            controlableTV = (TextView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_tv);
            controlableImage = (ImageView) controlableLineLayout.findViewById(R.id.thermostat_cut_control_iv);
            controlLineLayout.addView(controlableLineLayout);
        }

        @Override
        public void setWulianDeviceAndSelectData(WulianDevice device, AutoActionInfo autoActionInfo) {
            super.setWulianDeviceAndSelectData(device, autoActionInfo);
            String epData = autoActionInfo.getEpData();
            if(!StringUtil.isNullOrEmpty(epData)){
                if(StringUtil.equals(epData , STATE_CMD_OFF)){
                    controlableTV.setVisibility(View.INVISIBLE);
                    controlableImage.setVisibility(View.VISIBLE);
                    controlableImage.setImageResource(R.drawable.thermost_housekeeper_off);
                }else if(StringUtil.equals(epData , STATE_CMD_ON)){
                    controlableTV.setVisibility(View.INVISIBLE);
                    controlableImage.setVisibility(View.VISIBLE);
                    controlableImage.setImageResource(R.drawable.thermost_housekeeper_on);
                }else if(StringUtil.equals(epData , FNMODE_CMD_ON)){
                    controlableTV.setVisibility(View.INVISIBLE);
                    controlableImage.setVisibility(View.VISIBLE);
                    controlableImage.setImageResource(R.drawable.thermost_housekeeper_wind);
                }else if((epData.length() >3) && StringUtil.equals(epData.substring(0,3) , TEMP_CMD_HEAT)){
                    String tempHeat = Float.parseFloat(epData.substring(3 , 7))/100 + "";
                    controlableTV.setText(tempHeat +"℃");
                    controlableTV.setVisibility(View.VISIBLE);
                    controlableImage.setVisibility(View.VISIBLE);
                    controlableImage.setImageResource(R.drawable.thermost_housekeeper_heat);
                }else if((epData.length() >3) && StringUtil.equals(epData.substring(0,3) , TEMP_CMD_COOL)){
                    String tempCool = Float.parseFloat(epData.substring(7 , 11))/100 + "";
                    controlableTV.setText(tempCool +"℃");
                    controlableTV.setVisibility(View.VISIBLE);
                    controlableImage.setVisibility(View.VISIBLE);
                    controlableImage.setImageResource(R.drawable.thermost_housekeeper_cool);
                }
            }


        }

    }

    /**
     * 美标温控器管家场景设置
     */
    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,
                                                                                 final AutoActionInfo autoActionInfo) {
        final boolean[] isWarningDialogShow = {false , false};
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
        holder.setShowDialog(false);
        String epData = autoActionInfo.getEpData();
        final View contentView =  inflater.inflate(R.layout.device_thermostat82_house_keeper_setting, null);
        final ImageButton btnStateOff = (ImageButton) contentView.findViewById(R.id.thermostat_house_off_btn);
        final ImageButton btnStateOn = (ImageButton) contentView.findViewById(R.id.thermostat_house_on_btn);
        final ImageButton btnStateFan = (ImageButton) contentView.findViewById(R.id.thermostat_house_fan_btn);
        final TextView tvHeatTemp = (TextView) contentView.findViewById(R.id.thermostat_house_seekbar_heat_progress);
        final SeekBar seekBarHeat = (SeekBar) contentView.findViewById(R.id.thermost_house_seekbar_heat);
        final TextView tvCoolTemp = (TextView) contentView.findViewById(R.id.thermostat_house_seekbar_cool_progress);
        final SeekBar seekBarCool = (SeekBar) contentView.findViewById(R.id.thermost_house_seekbar_cool);

        if(!StringUtil.isNullOrEmpty(epData)){
            if(StringUtil.equals(epData , STATE_CMD_OFF)){
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_pre);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
            }else if(StringUtil.equals(epData , STATE_CMD_ON)){
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_pre);
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));

            }else if(StringUtil.equals(epData , FNMODE_CMD_ON)){
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_pre);
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));

            }else if((epData.length() >3) && StringUtil.equals(epData.substring(0,3) , TEMP_CMD_HEAT)){
                String tempHeat = Float.parseFloat(epData.substring(3 , 7))/100 + "";
                tvHeatTemp.setText(tempHeat +"℃");
                tvHeatTemp.setTextColor(Color.parseColor("#709E17"));
                int progress = (int)((Float.parseFloat(tempHeat) - 10) *2);
                DisplayMetrics metric = new DisplayMetrics();
                WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;
                int left = (int)(((float)(progress)/seekBarHeat.getMax()) * (width/4*3-120))  +10;
                tvHeatTemp.setPadding(left ,0 , 10 ,0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
                seekBarHeat.setProgress(progress);
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));

            }else if((epData.length() >3) && StringUtil.equals(epData.substring(0,3) , TEMP_CMD_COOL)){
                String tempCool = Float.parseFloat(epData.substring(7 , 11))/100 + "";
                tvCoolTemp.setText(tempCool + "℃");
                tvCoolTemp.setTextColor(Color.parseColor("#709E17"));
                int progress = (int)((Float.parseFloat(tempCool) - 10) *2);
                DisplayMetrics metric = new DisplayMetrics();
                WindowManager wm = (WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE);
                wm.getDefaultDisplay().getMetrics(metric);
                int width = metric.widthPixels;
                int left = (int)(((float)(progress)/seekBarCool.getMax()) * (width/4*3-120))  +10;
                tvCoolTemp.setPadding(left ,0 , 10 ,0);
                seekBarCool.setProgress(progress);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
            }
        }

        //提示窗
        final AreaGroupMenuPopupWindow warningPopWindow= new AreaGroupMenuPopupWindow(mContext);
        final View warningContent = LayoutInflater.from(mContext).
                inflate(R.layout.device_fancoil_house_keeper_setting_warning_dialog, null);
        warningPopWindow.setContentView(warningContent);
        TextView warningMessage = (TextView) warningContent.findViewById(R.id.fancoil_house_keeper_message);
        TextView warningBtnOk = (TextView) warningContent.findViewById(R.id.fancoil_house_keeper_prompt);
        warningMessage.setText(HOUSE_KEEPER_MESSAGE);
        warningBtnOk.setText("Ok");
        warningBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                warningPopWindow.dismiss();
            }
        });

        btnStateOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_pre);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                autoActionInfo.setEpData(STATE_CMD_OFF);
            }
        });

        btnStateOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_pre);
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                autoActionInfo.setEpData(STATE_CMD_ON);
            }
        });

        btnStateFan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_pre);
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                autoActionInfo.setEpData(FNMODE_CMD_ON);
            }
        });

        seekBarHeat.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!isWarningDialogShow[0]){
                    warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
                }
                tvHeatTemp.setText(((float)progress)/2 + 10 +"℃");
                tvHeatTemp.setTextColor(Color.parseColor("#709E17"));
                int left = (int)(((float)(progress)/seekBar.getMax()) * (seekBar.getWidth()-120))  +10;
                tvHeatTemp.setPadding(left ,0 , 10 ,0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvCoolTemp.setText("10℃");
                tvCoolTemp.setTextColor(Color.GRAY);
                seekBarCool.setProgress(0);
                seekBarCool.setThumbOffset(0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                isWarningDialogShow[0] = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float tempHeat = ((float)seekBar.getProgress())/2 + 10;
                String tempHeatCmd = StringUtil.appendLeft((int)(tempHeat * 100)+"" ,4 ,'0');
                String tempChangeCmd = createCompoundCmd(tempHeatCmd, "0000");
                String heatEpData= createCompoundCmd(TEMP_CMD_HEAT, tempChangeCmd);
                autoActionInfo.setEpData(heatEpData);
            }
        });

        seekBarCool.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(!isWarningDialogShow[1]){
                    warningPopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
                }
                tvCoolTemp.setText(((float)progress)/2 + 10 + "℃");
                tvCoolTemp.setTextColor(Color.parseColor("#709E17"));
                int left = (int)(((float)(progress)/seekBar.getMax()) * (seekBar.getWidth()-120))  +10;
                tvCoolTemp.setPadding(left ,0 , 10 ,0);
                seekBarCool.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont));
                btnStateOff.setBackgroundResource(R.drawable.thermost_housekeeper_off_nor);
                btnStateOn.setBackgroundResource(R.drawable.thermost_housekeeper_on_nor);
                btnStateFan.setBackgroundResource(R.drawable.thermost_housekeeper_wind_nor);
                tvHeatTemp.setText("10℃");
                tvHeatTemp.setTextColor(Color.GRAY);
                seekBarHeat.setProgress(0);
                seekBarHeat.setThumbOffset(0);
                seekBarHeat.setThumb(mContext.getResources().getDrawable(R.drawable.thermost_housekeeper_piont_nor));
                isWarningDialogShow[1] = true;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                float tempCool = ((float)seekBar.getProgress())/2 + 10;
                String tempCoolCmd = StringUtil.appendLeft(String.valueOf((int)(tempCool * 100)),4 ,'0');
                String tempChangeCmd = createCompoundCmd("0000" , tempCoolCmd);
                String coolEpData = createCompoundCmd(TEMP_CMD_COOL, tempChangeCmd);
                autoActionInfo.setEpData(coolEpData);
            }
        });

        holder.setContentView(contentView);
        return holder;
    }

    private WLDialog noticeDialog;

    private void showNoticeDialog(){
        WLDialog.Builder builder = new WLDialog.Builder(mContext);

        builder.setTitle("Prompt")
                .setSubTitleText(null)
                .setMessage(HOUSE_KEEPER_MESSAGE)
                .setNegativeButton("Ok")
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {

                    }

                    @Override
                    public void onClickNegative(View view) {

                    }
                });
        noticeDialog = builder.create();
        noticeDialog.show();
    }

}
