package com.wulian.iot.server;

import android.util.Log;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlListEventReq;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlPlayRecord;
import com.tutk.IOTC.IOTCAPIs;
import com.tutk.IOTC.st_SearchDeviceInfo;
import com.wulian.iot.view.device.setting.SetProtectActivity;
import com.wulian.iot.view.ui.DeskMoveDetectionActivity;
import com.yuantuo.netsdk.TKCamHelper;

import java.io.UnsupportedEncodingException;

/***
 * TUTK 命名
 *
 * @author syf
 */
public class IotSendOrder {
    private final static String TAG = "IotSendOrder";

    public static void connect(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETSUPPORTSTREAM_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetSupportStreamReq.parseContent());
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlDeviceInfoReq.parseContent());
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetAudioOutFormatReq.parseContent());
    }

    public static void settingsResolution(TKCamHelper mCamera, int resolution) {
        Log.i(TAG, resolution == 0 ? "超清" : "标清");
        mCamera.camIndex(resolution);
    }

    /**
     * 格式化sd卡
     */
    public static void sendSdFormat(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SDCARD_FORMAT_REQ, new byte[0]);
    }

    /**
     * 查询sd卡状态
     */
    public static void findSdCodeStatus(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_SDCARD_STATUS_REQ, new byte[0]);
    }

    public static void findDeskCameraVerByIoc(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_FW_UPDATA_REQ, new byte[]{0});
    }

    public static void USER_IPCAM_SETWIFI_REQ(TKCamHelper mCamera, String ssid, String pwd, byte mode, byte enctype) {
        mCamera.sendIOCtrl(
                Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(
                        ssid.getBytes(), pwd.getBytes(), mode, enctype));
    }

    /**
     * 设置pi报警 参数 1-15   15为关闭报警
     */
    public static void USER_IPCAM_SETMOTIONDETECT(TKCamHelper mCamera, int sensitivity) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetMotionPir.parseContent(0, sensitivity));
    }

    /**
     * 鹰眼获取wifi列表
     */
    public static void USER_FINDWIFILIST(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTWIFIAP_REQ, new byte[]{0});
    }

    /**
     * 获取灵敏度
     */
    public static void sendGetEagleSensitivity(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ, new byte[0]);
    }

    /**
     * 设置灵敏度
     */
    public static void sendSetEagleSensitivity(TKCamHelper mCamera, byte a) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ, new byte[]{0, 0, 0, 0, a, 0, 0, 0});
    }

    /**
     * 获取猫眼的电池电量信息
     */
    public static void sendGetVoltageInfo(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_BATTERY_REQ, new byte[0]);
    }

    /**
     * 猫眼固件升级  获取固件信息
     *
     * @param mCamera
     */
    public static void findEagleVerByIoc(TKCamHelper mCamera) {
        byte[] reserved = new byte[4];
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_REQ, reserved);
    }

    /**
     * 猫眼固件升级  发送固件升级命令
     *
     * @param mCamera
     */
    public static void sendEagleUpdata(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(0, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STARTFWUPGRADE_REQ, new byte[]{0});
    }

    public static void IOTYPE_USER_IPCAM_LISTEVENT_REQ(TKCamHelper mCamera, byte[] startTime, byte[] endTime, short year, byte event, byte status) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ,
                SMsgAVIoctrlListEventReq.parseConent(Camera.DEFAULT_AV_CHANNEL, year, startTime, endTime, event, status));
    }

    public static void IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL(TKCamHelper mCamera, short year, byte[] startTimeAck) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_RECORD_PLAYCONTROL,
                SMsgAVIoctrlPlayRecord.parseContent(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.AVIOCTRL_RECORD_PLAY_START, year, startTimeAck));
    }

    //设置语言
    public static void setLanguage(TKCamHelper mCamera, int type) {
        byte[] language = null;
        if (type == 2) {
            language = new byte[]{2, 0, 0, 0};
        } else if (type == 1) {
            language = new byte[]{1, 0, 0, 0};
        }
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_LANGUAGE_REQ, language);
    }

    //查询语言
    public static void findhLanguageByIoc(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_LANGUAGE_REQ, new byte[]{0});
    }

    //设置音量大小
    public static void setCameraVolume(TKCamHelper mCamera, int type) {
        byte[] volume = null;
        if (type == 10) {//mute
            volume = new byte[]{0xa};
        }
        if (type == -68) {//low
            volume = new byte[]{-68};
        }
        if (type == -36) {//mid
            volume = new byte[]{-36};
        }
        if (type == -24) {//high
            volume = new byte[]{-24};
        }
        if (type == -2) {//veryhigh
            volume = new byte[]{-2};
        }
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_VOLUME_REQ, volume);
    }

    //音量查询
    public static void findVolumeByIoc(TKCamHelper mCamera){
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_VOLUME_REQ, new byte[]{0});
    }

    //设置红外夜视
    public static void setIRSeries(TKCamHelper mCamera, int type) {
        byte[] IRSeries = null;
        if (type == 0) {//CLOSE
            IRSeries = new byte[]{0, 0, 0, 0};
        }
        if (type == 1) {//OPEN
            IRSeries = new byte[]{1, 0, 0, 0};
        }
        if (type == 2) {//AUTO
            IRSeries = new byte[]{2, 0, 0, 0};
        }
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SET_IRCARD_REQ, IRSeries);
    }

    //查询红外夜视
    public static void findIRByIoc(TKCamHelper mCamera) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_IRCARD_REQ, new byte[]{0});
    }


    /**
     * 查询一天的pir告警录像
     */
    public static void findEagleWifiByEvent(TKCamHelper mCamera, byte[] startTime, byte[] endTime, short year, byte event, byte status) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_LISTEVENT_REQ, SMsgAVIoctrlListEventReq.parseConent(Camera.DEFAULT_AV_CHANNEL, year, startTime, endTime, event, status));
    }

    public static String findTutkUidByWifi() {
        int[] nArray = new int[1];
        IOTCAPIs.IOTC_Search_Device_Start(3000, 100);
        String res = null;
        while (true) {
            st_SearchDeviceInfo[] ab_LanSearchInfo = IOTCAPIs.IOTC_Search_Device_Result(nArray, 0);
            if (nArray[0] < 0) {
                Log.e(TAG, "===nArray(" + nArray[0] + ")===");
                break;
            }
            for (int i = 0; i < nArray[0]; i++) {
                try {
                    res = new String(ab_LanSearchInfo[i].UID, 0, ab_LanSearchInfo[i].UID.length, "utf-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                    Log.e(TAG, "===findDeviceIdByWifi exception ===");
                }
            }
        }
        return res;
    }

    public static void sendDevConfig(TKCamHelper mCamera, String ssid, String pwd, byte mode, byte enctype) {
        mCamera.sendIOCtrl(
                Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETWIFI_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetWifiReq.parseContent(
                        ssid.getBytes(), pwd.getBytes(), mode, enctype));
    }

    //add syf
    //获取桌面摄像机历史录像
    public static void sendIoctrlGetPlayBackFile(TKCamHelper mCamera, byte[] start, byte[] end) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlGetPlayBackFileReq.parseContent(start, end));
    }

    //获取历史录像
    public static void sendIoctrlSetPlayBackFileNowReq(TKCamHelper mCamera, byte[] start,
                                                       String fileName) {
        mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_STREAM_REQ,
                AVIOCTRLDEFs.SMsgAVIoctrlSetPlayBackFileNowReq.parseContent(start,
                        fileName.getBytes()));
    }

    public static void findMoveDataByIoc(TKCamHelper mCamera) {
        if (mCamera != null) {
            mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETMOTIONDETECT_REQ, new byte[]{0});
        }
    }

    public static void sendMotionDetection(TKCamHelper mCamera, DeskMoveDetectionActivity.MotionDetectionPojo motionDetectionPojo) {
        if (mCamera != null) {
            mCamera.sendIOCtrl(Camera.DEFAULT_AV_CHANNEL, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SETMOTIONDETECT_REQ,
                    AVIOCTRLDEFs.SMsgAVIoctrlSetMotionDetectReq.parseContent(motionDetectionPojo.getSwitching(), motionDetectionPojo.getSensitivity(), motionDetectionPojo.getArea(), motionDetectionPojo.getDefenceused(), motionDetectionPojo.getWeek(),
                            motionDetectionPojo.getMoveTime()));
        }
    }
}
