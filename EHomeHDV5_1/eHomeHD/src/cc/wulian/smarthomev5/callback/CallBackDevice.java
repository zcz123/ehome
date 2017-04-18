package cc.wulian.smarthomev5.callback;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;

import java.util.Map;
import java.util.Set;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.configureable.ir.IRGroupManager;
import cc.wulian.app.model.device.impls.configureable.ir.IRManager;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.dao.MessageDao;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.event.AlarmEvent;
import cc.wulian.smarthomev5.event.DeviceActivation;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DeviceIREvent;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;
import cc.wulian.smarthomev5.event.JoinGatewayEvent;
import cc.wulian.smarthomev5.event.MessageEvent;
import cc.wulian.smarthomev5.event.RssiEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceJoinGWManager;
import cc.wulian.smarthomev5.fragment.home.HomeManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.CmdControlFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MotionDetectionManger;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.LogUtil;
import de.greenrobot.event.EventBus;

public class CallBackDevice {
    private final MainApplication mApp = MainApplication.getApplication();
    private final Context mContext;
    private IRDao irDao = IRDao.getInstance();
    private DeviceDao deviceDao = DeviceDao.getInstance();
    private MessageDao messageDao = MessageDao.getInstance();
    private DeviceCache mDeviceCache;

    public CallBackDevice(Context context, ServiceCallback callback) {
        mContext = context;
        mDeviceCache = DeviceCache.getInstance(mContext);

    }

    public void DeviceUp(DeviceInfo devInfo, Set<DeviceEPInfo> devEPInfoSet,
                         boolean isFirst) {
        String gwID = devInfo.getGwID();
        String devID = devInfo.getDevID();

        if (!UserRightUtil.getInstance().canSeeDevice(devID)) {
            //没有权限
            return;
        }

        String devType = devInfo.getType();
        devType = DeviceTool.createDeviceTypeCompat(devType);
        devInfo.setType(devType);

        // push in device cache
        WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID, devID);
        if (device == null)
            device = mDeviceCache
                    .startUpDevice(mContext, devInfo, devEPInfoSet);
        else {
            for (DeviceEPInfo epInfo : devEPInfoSet) {
                mDeviceCache.updateDevice(mContext, devInfo, epInfo);
            }
        }
        device.setDeviceOnLineState(true);

        for (DeviceEPInfo epInfo : devEPInfoSet) {
            deviceDao.insertOrUpdate(devInfo, epInfo);
        }
        EventBus.getDefault().post(
                new DeviceEvent(DeviceEvent.QUICK_EDIT, devInfo, false));
        EventBus.getDefault().post(
                new DeviceEvent(DeviceEvent.REFRESH, devInfo, false));
        if (isFirst) {
            MessageEventEntity entity = new MessageEventEntity();
            entity.setGwID(gwID);
            entity.setDevID(devID);
            entity.setPriority(Messages.PRIORITY_DEFAULT);
            entity.setTime(System.currentTimeMillis() + "");
            entity.setSmile(Messages.SMILE_DEFAULT);
            entity.setType(Messages.TYPE_DEV_ONLINE);
            messageDao.deleteAndInsert(entity);
            DeviceJoinGWManager.getInstance().add(device);
            EventBus.getDefault().post(new JoinDeviceEvent(gwID, devID));
            EventBus.getDefault().post(
                    new MessageEvent(Messages.TYPE_DEV_ONLINE));
        }
    }

    HomeManager alarmMessageManager = HomeManager.getInstance();

    public void DeviceDown(String gwID, String devID, String status) {
        if (!UserRightUtil.getInstance().canSeeDevice(devID)) {
            //没有权限
            return;
        }
        if (status != null && status.equals("1")) {
            WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID,
                    devID);
            if (device != null) {
                device.onDeviceDestory(gwID, devID);
                mDeviceCache.removeDevice(gwID, devID);
            }
            mApp.mDataBaseHelper.deleteFromDeviceIR(gwID, devID, null);
            mApp.mDataBaseHelper.deleteFromShake(gwID, devID);
            EventBus.getDefault().post(
                    new DeviceEvent(DeviceEvent.REFRESH, null, false));
        } else {
            MessageEventEntity offineMessageEntity = new MessageEventEntity(null,
                    null, null, gwID, devID, "", "", null,
                    mContext.getString(R.string.device_offline),
                    System.currentTimeMillis() + "", Messages.PRIORITY_DEFAULT,
                    Messages.TYPE_DEV_OFFLINE, Messages.SMILE_DEFAULT, null);
            alarmMessageManager.addSingleAlarmMessageEntity(offineMessageEntity);
            WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID, devID);
            if (device != null) {
                device.setDeviceOnLineState(false);
                EventBus.getDefault().post(
                        new AlarmEvent(Messages.TYPE_DEV_OFFLINE, DeviceTool
                                .getDeviceAlarmAreaName(device)
                                + DeviceTool.getDeviceShowName(device)
                                + mContext.getString(R.string.device_offline)));
                EventBus.getDefault().post(
                        new DeviceEvent(DeviceEvent.REFRESH, null, false));
            }
        }
    }

    public void DeviceData(String gwID, String devID, String type, DeviceEPInfo devEPInfo) {
        if (!UserRightUtil.getInstance().canSeeDevice(devID)) {
            //没有权限
            return;
        }
        String ep = devEPInfo.getEp();
        String epType = DeviceTool.createDeviceTypeCompat(devEPInfo.getEpType());
        devEPInfo.setEpType(epType);
        String epMsg = devEPInfo.getEpMsg();
        String epData = devEPInfo.getEpData();
        long time = StringUtil.toLong(devEPInfo.getTime());
        if (epType.equals("DC")) {//
            MotionDetectionManger motionDetectionManger = MotionDetectionManger.getInstance(mContext);
            String alarmMsg = motionDetectionManger.chooseCameraAlarmMsg(epData);
            EventBus.getDefault().post(new AlarmEvent(Messages.TYPE_DEV_ALARM, alarmMsg));
//            return;
        }
        WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID, devID);
        if (device == null) return;
        DeviceInfo deviceInfo = device.getDeviceInfo();
        if (device.getChildDevices() != null) {
            WulianDevice childDevice = device.getChildDevice(devEPInfo.getEp());
            if (childDevice != null) {
                devEPInfo.setEpName(childDevice.getDeviceInfo().getDevEPInfo().getEpName());
            }
        }
        if (deviceInfo == null) return;
        try {
            device = mDeviceCache.updateDevice(mContext, deviceInfo, devEPInfo);
            if (epMsg != null && epMsg.length() > 1) {
                String msgType = epMsg.substring(0, 1);
                String deviceName = device.getDeviceName();
                if (deviceName == null || deviceName.equals("")) {
                    deviceName = device.getDefaultDeviceName();
                }
                String area="";
                if(!device.getDeviceRoomID().equals(Area.AREA_DEFAULT)){
                    DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
                            .getDeviceAreaEntity(device.getDeviceGwID(),
                                    device.getDeviceRoomID());
                    area= areaEntity.getName();
                }
                String parseData =area+deviceName+mContext.getResources().getString(
                        R.string.house_rule_detect)+epMsg.substring(1);
                if ("W".equals(msgType)) {
                    String notifyType = Messages.TYPE_DEV_ALARM;
                    MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                            null, null, null, gwID, devID, ep, epType, null,
                            parseData, time + "", Messages.PRIORITY_DEFAULT,
                            notifyType, Messages.SMILE_DEFAULT, epMsg);
                    alarmMessageManager.addSingleAlarmMessageEntity(alarmMessageEntity);
                    AlarmEvent alarmEvent = new AlarmEvent(notifyType, parseData);
                    EventBus.getDefault().post(alarmEvent);
                } else if ("M".equals(msgType)) {
//					String smile = sensorable.checkDataRatioFlag();
                    String smile = Messages.SMILE_DEFAULT;
                    String messageType = Messages.TYPE_DEV_SENSOR_DATA;
                    MessageEventEntity entity = new MessageEventEntity();
                    entity.setGwID(gwID);
                    entity.setDevID(devID);
                    entity.setEp(ep);
                    entity.setEp(ep);
                    entity.setPriority(Messages.PRIORITY_DEFAULT);
                    entity.setEpType(epType);
                    entity.setEpData(parseData);
                    entity.setTime(time + "");
                    entity.setSmile(smile);
                    entity.setType(messageType);
                    messageDao.deleteAndInsert(entity);
                    EventBus.getDefault().post(new MessageEvent(messageType));
                }
            } else if (epMsg != null && epMsg.equals("N")) {
                //不进行报警操作
                return;
            } else {
                boolean isDoAlarmAndSensor = checkDeviceAlarmAndSensorState(device, gwID, devID, ep, epType, epData, time, epMsg);
                if (!isDoAlarmAndSensor) {
                    boolean isDoAlarm = checkDeviceAlarmState(device, gwID, devID, ep, epType, epData, time, epMsg);
                    if (!isDoAlarm) {
                        checkSensorData(device, gwID, devID, ep, epType, epData, time);
                    }
                }
            }

        } catch (Exception e) {
            LogUtil.logException("update device data Failed:", e);
        } finally {
            // feed back to control action
            String key = gwID + devID;
            EventBus.getDefault().post(new DialogEvent(key, ResultUtil.RESULT_SUCCESS));
            EventBus.getDefault().post(new DeviceEvent(DeviceEvent.REFRESH, deviceInfo, false));
        }
    }

    private void checkSensorData(WulianDevice device, String gwID,
                                 String devID, String ep, String epType, String epData, long time) {
        if (device instanceof Sensorable) {
            Sensorable sensorable = (Sensorable) device;
            String parseData = device.parseDataWithProtocol(epData).toString();
            if (StringUtil.isNullOrEmpty(parseData))
                return;
            String smile = sensorable.checkDataRatioFlag();
            String messageType = Messages.TYPE_DEV_SENSOR_DATA;
            MessageEventEntity entity = new MessageEventEntity();
            entity.setGwID(gwID);
            entity.setDevID(devID);
            entity.setEp(ep);
            entity.setPriority(Messages.PRIORITY_DEFAULT);
            entity.setEpType(epType);
            entity.setEpData(parseData);
            entity.setTime(time + "");
            entity.setSmile(smile);
            entity.setType(messageType);
            messageDao.deleteAndInsert(entity);
            EventBus.getDefault().post(new MessageEvent(messageType));
        }
    }

    private boolean checkDeviceAlarmState(WulianDevice device, String gwID,
                                          String devID, String ep, String epType, String epData, long time, String epMsg) {
        if (!DeviceUtil.isDeviceAlarmable(device))
            return false;
        Alarmable alarm = ((Alarmable) device);
        if (alarm.isAlarming()) {
            String parseData = device.parseDataWithProtocol(epData).toString();
            String notifyType = Messages.TYPE_DEV_ALARM;
            MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                    null, null, null, gwID, devID, ep, epType, null, parseData,
                    time + "", Messages.PRIORITY_DEFAULT, notifyType,
                    Messages.SMILE_DEFAULT, epMsg);
            alarmMessageManager.addSingleAlarmMessageEntity(alarmMessageEntity);
            if (!(epMsg != null && epMsg.equals("N"))) {
                AlarmEvent alarmEvent = new AlarmEvent(notifyType, alarm
                        .parseAlarmProtocol(epData).toString());
                EventBus.getDefault().post(alarmEvent);
            }
            return true;
        }
        return false;
    }

    private boolean checkDeviceAlarmAndSensorState(WulianDevice device,
                                                   String gwID, String devID, String ep, String epType, String epData,
                                                   long time, String epMsg) {
        if (DeviceUtil.isDeviceAlarmable(device)
                && DeviceUtil.isDeviceSensorable(device)) {
            Alarmable alarm = ((Alarmable) device);
            if (alarm.isAlarming()
                    && StringUtil.equals(epData, alarm.getAlarmProtocol())) {
                String parseData = device.parseDataWithProtocol(epData)
                        .toString();
                String notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, ep, epType, null,
                        parseData, time + "", Messages.PRIORITY_DEFAULT,
                        notifyType, Messages.SMILE_DEFAULT, epMsg);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);
                if (!(epMsg != null && epMsg.equals("N"))) {
                    AlarmEvent alarmEvent = new AlarmEvent(notifyType, alarm
                            .parseAlarmProtocol(epData).toString());
                    EventBus.getDefault().post(alarmEvent);
                }
                return true;
            } else {
                Sensorable sensorable = (Sensorable) device;
                String parseData = device.parseDataWithProtocol(epData)
                        .toString();
                if (StringUtil.isNullOrEmpty(parseData))
                    return false;
                String smile = sensorable.checkDataRatioFlag();
                String messageType = Messages.TYPE_DEV_SENSOR_DATA;
                MessageEventEntity entity = new MessageEventEntity();
                entity.setGwID(gwID);
                entity.setDevID(devID);
                entity.setEp(ep);
                entity.setPriority(Messages.PRIORITY_DEFAULT);
                entity.setEpType(epType);
                entity.setEpData(parseData);
                entity.setTime(time + "");
                entity.setSmile(smile);
                entity.setType(messageType);
                messageDao.deleteAndInsert(entity);
                EventBus.getDefault().post(new MessageEvent(messageType));
            }
        }
        return false;
    }

    public void SetDeviceInfo(String mode, DeviceInfo deviceInfo,
                              DeviceEPInfo epInfo) {
        String gwID = deviceInfo.getGwID();
        String devID = deviceInfo.getDevID();
        String actionKey = gwID + devID;
        deviceInfo.getIsOnline();
        try {
            WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID,
                    devID);
            // delete
            if (TextUtils.equals(CmdUtil.MODE_DEL, mode)) {

                if (device != null) {
                    device.onDeviceDestory(gwID, devID);
                    // Mark: modify cache back some data
                    // operateSuccess |= mDeviceLocal.get().removeDevice(gwID,
                    // devID);
                    mDeviceCache.removeDevice(gwID, devID);
                }
                mApp.mDataBaseHelper.deleteFromDeviceIR(gwID, devID, null);
                mApp.mDataBaseHelper.deleteFromShake(gwID, devID);
            } else {
                if (device != null)
                    device.onDeviceSet(mode, deviceInfo, epInfo, ConstUtil.CMD_SET_DEV);
            }

        } catch (Exception e) {
            LogUtil.logException("update device info Failed:", e);
        } finally {
            if (CmdUtil.MODE_DEL.equals(mode)) {
                EventBus.getDefault().post(
                        new DeviceEvent(DeviceEvent.REMOVE, deviceInfo, false));
            } else {
                EventBus.getDefault()
                        .post(new DeviceEvent(DeviceEvent.REFRESH, deviceInfo,
                                false));
            }
            EventBus.getDefault().post(
                    new DialogEvent(actionKey, ResultUtil.RESULT_SUCCESS));
            EventBus.getDefault().post(new DeviceActivation(devID,deviceInfo));
        }
    }

    public void SetDeviceIRInfo(String gwID, String devID, String ep,
                                String mode, String irType, Set<DeviceIRInfo> devIRInfoSet) {
        IRGroupManager irGroupManager = IRManager.getInstance()
                .getIrGroupManager(gwID, devID);
        String actionKey = SendMessage.ACTION_SET_DEVICE_IR + gwID + devID;
        int runResult = ResultUtil.RESULT_FAILED;
        try {
            if (StringUtil.equals(CmdUtil.MODE_ADD, mode)) {
                // clear local data when mode[1]
                DeviceIRInfo obj = new DeviceIRInfo();
                obj.setDeviceID(devID);
                obj.setGwID(gwID);
                obj.setIRType(irType);
                irDao.delete(obj);
                for (DeviceIRInfo info : devIRInfoSet) {
                    try {
                        irDao.insertOrUpdate(info);
                        irGroupManager.addIrInfo(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // then save all server data
                // saveDeviceIRInfo(true, gwID, devID, ep, devIRInfoSet);
            } else if (StringUtil.equals(CmdUtil.MODE_UPD, mode)) {
                for (DeviceIRInfo info : devIRInfoSet) {
                    try {
                        irDao.insertOrUpdate(info);
                        irGroupManager.addIrInfo(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // saveDeviceIRInfo(true, gwID, devID, ep, devIRInfoSet);
            } else if (StringUtil.equals(CmdUtil.MODE_DEL, mode)) {
                DeviceIRInfo obj = new DeviceIRInfo();
                obj.setDeviceID(devID);
                obj.setGwID(gwID);
                obj.setIRType(irType);
                try {
                    irDao.delete(obj);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // mApp.mDataBaseHelper.deleteFromDeviceIR(gwID, devID, irType);
                for (DeviceIRInfo info : devIRInfoSet) {
                    irGroupManager.removeIRInfo(info);
                }

            } else if (StringUtil.equals(CmdUtil.MODE_BATCH_ADD, mode)) {
                for (DeviceIRInfo info : devIRInfoSet) {
                    try {
                        irDao.insertOrUpdate(info);
                        irGroupManager.addIrInfo(info);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                // when mode[4], just save this data, not clear anything
                // saveDeviceIRInfo(true, gwID, devID, ep, devIRInfoSet);
            }
            runResult = ResultUtil.RESULT_SUCCESS;
        } catch (Exception e) {
            runResult = ResultUtil.RESULT_FAILED;
            LogUtil.logException("update deviceIRInfo info Failed ", e);
        }
        EventBus.getDefault().post(
                new DeviceIREvent(mode, gwID, devID, irType, false));
        EventBus.getDefault().post(new DialogEvent(actionKey, runResult));
    }

    public void GetDeviceIRInfo(String gwID, String devID, String ep,
                                String mode, Set<DeviceIRInfo> devIRInfoSet) {

        if (devIRInfoSet == null)
            return;
        IRGroupManager irGroupManager = IRManager.getInstance()
                .getIrGroupManager(gwID, devID);
        if (CmdUtil.MODE_DEL.equals(mode))
            irGroupManager.clear();
        else {
            for (DeviceIRInfo info : devIRInfoSet) {
                try {
                    irDao.insertOrUpdate(info);
                    irGroupManager.addIrInfo(info);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void SetBindDevInfo(String gwID, String mode, String devID,
                               JSONArray data) {
        // saveBindDeviceInfo(gwID, mode, devID, data, true);
    }

    public void GetBindDevInfo(String gwID, String devID, JSONArray data) {
        // saveBindDeviceInfo(gwID, null, devID, data, false);
    }

    public void QueryDevRssiInfo(String gwID, String devID, String data, String uplink) {
        final Map<String, Integer> rssiMap = mApp.queryRssiInfoMap;

        int rssi = StringUtil.toInteger(data);
        rssiMap.put(gwID + devID, (100 - rssi));
        EventBus.getDefault().post(new RssiEvent(gwID, devID, data, uplink));
        EventBus.getDefault().post(new DeviceEvent(DeviceEvent.REFRESH));
    }

    public void QueryDevRelaInfo(String gwID, String devID, String data) {
    }

    public void DeviceHardData(String gwID, String devID, String devType,
                               String data) {
        try {
            Logger.debug("alarm :" + devID + ";" + devType + ":" + data);
            WulianDevice device = mDeviceCache.getDeviceByID(mContext, gwID,
                    devID);
            if (device == null)
                return;
            Alarmable alarm = ((Alarmable) device);
            String notifyType = "";
            if ("0201".equals(data)) {
                notifyType = Messages.TYPE_DEV_DESTORY;
                MessageEventEntity alarmDestoryMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        data, System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmDestoryMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);
            } else if ("0101".equals(data)) {   //美标温控器欠压报警
                notifyType = Messages.TYPE_DEV_LOW_POWER;
                MessageEventEntity entity = new MessageEventEntity();
                entity.setGwID(gwID);
                entity.setDevID(devID);
                entity.setEpType(devType);
                entity.setEpData(data);
                entity.setPriority(Messages.PRIORITY_DEFAULT);
                entity.setTime(System.currentTimeMillis() + "");
                entity.setSmile(Messages.SMILE_DEFAULT);
                entity.setType(notifyType);
                messageDao.deleteAndInsert(entity);
                EventBus.getDefault().post(new MessageEvent(notifyType));

                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        mContext.getResources().getString(R.string.set_sound_notification_bell_prompt_undervoltage),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);

            } else if ("0301".equals(data)) {   //美标温控器断电报警

                notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        mContext.getResources().getString(R.string.floor_feating_power_off),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);

            } else if ("0401".equals(data)) {        //地暖外部传感器异常

                notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        mContext.getResources().getString(R.string.AP_outside_wrong),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);

            } else if ("0410".equals(data)) {      //地暖内部传感器异常

                notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        mContext.getResources().getString(R.string.AP_inside_wrong),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);

            } else if ("0411".equals(data)) {        //地暖内外部传感器均异常
                notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                        mContext.getResources().getString(R.string.AP_both_wrong),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);
            } else if ("0422".equals(data)) {        //地暖传感器异常恢复
                notifyType = Messages.TYPE_DEV_ALARM;
                MessageEventEntity alarmMessageEntity = new MessageEventEntity(
                        null, null, null, gwID, devID, null, devType, null,
                       mContext.getResources().getString(R.string.device_state_normal),
                        System.currentTimeMillis() + "",
                        Messages.PRIORITY_DEFAULT, notifyType,
                        Messages.SMILE_DEFAULT, null);

                alarmMessageManager
                        .addSingleAlarmMessageEntity(alarmMessageEntity);

                AlarmEvent alarmEvent = new AlarmEvent(notifyType, device
                        .parseDestoryProtocol(data).toString());
                EventBus.getDefault().post(alarmEvent);
            }
        } catch (Exception e) {
            LogUtil.logException("update device data Failed:", e);
        }
    }

    public void PermitDevJoin(String gwID, String devID, String data) {
        EventBus.getDefault().post(new JoinGatewayEvent(gwID, devID, data));
    }

    public void GetDevAlarmNum(String gwID, String userID, String devID,
                               String data) {
    }

    public void GetDevRecordInfo(String gwID, String mode, String count,
                                 JSONArray data) {
    }

    public void reqeustOrSetTwoStateConfigration(String mode, String gwID,
                                                 String devID, String ep, JSONArray data) {

    }

    public void sendControlGroupDevices(String gwID, String group, String mode,
                                        String data) {

    }
    public void WifiJionNetwork(String gwID, String appID,String devType,String typeID, String opt, String mode,String data){
        JsUtil.getInstance().execCallback(CmdControlFeatureImpl.mWebview,CmdControlFeatureImpl.mCallBackId,data,JsUtil.OK,true);
    }
}