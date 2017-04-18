package cc.wulian.smarthomev5.tools;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;
import cc.wulian.smarthomev5.utils.FileUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wulian.icam.utils.DeviceType;
import com.yuantuo.customview.ui.CustomToast;

public final class CameraUtil {
    public static boolean isCameraRunning;

    public static void saveSnapshot(Context context, Bitmap bitmap) {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            CustomToast.showToast(context, context.getString(R.string.home_monitor_no_sdcard_hint),
                    CustomToast.LENGTH_LONG, true);
        } else {
            String folderPath = FileUtil.getSnapshotPath();
            boolean result = FileUtil.saveBitmapToJpeg(bitmap, folderPath);
            if (result) {
                CustomToast.showToast(context,
                        context.getString(R.string.play_take_picture_ok),
                        CustomToast.LENGTH_SHORT, true);
                try {
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + folderPath)));
                } catch (Exception e) {
                }
            } else {
                CustomToast.showToast(context, context.getString(R.string.play_take_picture_exception),
                        CustomToast.LENGTH_SHORT, true);
            }
        }
    }

    public static String type2String(int type) {
        String str = null;
        if (CameraInfo.CAMERA_TYPE_IP == type) {
            str = "IP";
        } else if (CameraInfo.CAMERA_TYPE_DVR_4 == type || CameraInfo.CAMERA_TYPE_DVR_8 == type) {
            str = "DVR";
        } else if (CameraInfo.CAMERA_TYPE_CLOUD_1 == type || CameraInfo.CAMERA_TYPE_CLOUD_2 == type) {
            str = "Cloud";
        }
        return str;
    }

    /**
     * 初始化摄像头的SDK
     */
    // public static void initSDK() {
    // new AsyncTask<Void, Void, Void>() {
    // @Override
    // protected Void doInBackground(Void... params) {
    // // FIXME to del
    // // // DVR-MonitorHCActivity
    // // HCCamHelper.initSdk();
    // // // IPCamera-MonitorTTActivity
    // // JNI.init();
    // // Cloud1-MonitorTKActivity, init may cause JNIErr
    // TKCamHelper.init();
    // // Cloud2-MonitorVSActivity, must call in MonitorVSActivity
    // // NativeCaller.Init();
    // Intent intent = new Intent();
    // intent.setClass(MainApplication.getApplication(),
    // BridgeService.class);
    // MainApplication.getApplication().startService(intent);
    // return null;
    // }
    // }.execute();
    // }

    /**
     * 物联摄像机列表json转换为list
     */
    public static List<MonitorWLCloudEntity> monitorWLjsonArrayToList(String json) {
        List<MonitorWLCloudEntity> list = new ArrayList<MonitorWLCloudEntity>();
        try {

            JSONObject jsonObject = JSONObject.parseObject(json);
            JSONArray bindingData = jsonObject.getJSONArray("owned");
            if (bindingData != null) {
                for (int i = 0; i < bindingData.size(); i++) {
                    JSONObject jsonArraybinding = (JSONObject) bindingData.getJSONObject(i);
                    MonitorWLCloudEntity wlCameraEntiry = getCameraEntityFromJSONObject(jsonArraybinding);
                    wlCameraEntiry.setMonitorIsBindDevice(true);
                        list.add(wlCameraEntiry);
                }
            }

            JSONArray sharedData = jsonObject.getJSONArray("shared");
            if (sharedData != null) {
                for (int i = 0; i < sharedData.size(); i++) {
                    JSONObject jsonArraybinding = (JSONObject) sharedData.getJSONObject(i);
                    MonitorWLCloudEntity wlCameraEntiry = getCameraEntityFromJSONObject(jsonArraybinding);
                    wlCameraEntiry.setMonitorIsBindDevice(false);
                    list.add(wlCameraEntiry);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return list;
    }

    private static MonitorWLCloudEntity getCameraEntityFromJSONObject(JSONObject jsonArraybinding) {
        String bindingSipDomain = "sh.gg";
        MonitorWLCloudEntity mMonitorWLCloudEntity = new MonitorWLCloudEntity();
        String bindingIsOnline = jsonArraybinding.getString("online");
        String bindingDeviceDesc = jsonArraybinding.getString("description");
        boolean hasDomain = jsonArraybinding.containsKey("sdomain");
        if (hasDomain) {
            bindingSipDomain = jsonArraybinding.getString("sdomain");
        }
        String bindingDeviceId = jsonArraybinding.getString("did");
        String bindingUpDateAt = jsonArraybinding.getString("updated");
        String bindingDeviceNick = jsonArraybinding.getString("nick");

        mMonitorWLCloudEntity.setMonitorIsOnline(bindingIsOnline);
        mMonitorWLCloudEntity.setMonitorDeviceDesc(bindingDeviceDesc);
        mMonitorWLCloudEntity.setMonitorSipDoMain(bindingSipDomain);
        mMonitorWLCloudEntity.setMonitorSipUserName(bindingDeviceId);
        mMonitorWLCloudEntity.setMonitorDeviceId(bindingDeviceId);
        mMonitorWLCloudEntity.setMonitorUpDatedAt(bindingUpDateAt);
        mMonitorWLCloudEntity.setMonitorDeviceNick(bindingDeviceNick);
        return mMonitorWLCloudEntity;
    }

    // add syf
    public static String judgeWLcameraIsPGType(Context context, String type) {
        DeviceType device = DeviceType.getDevivceTypeByDeviceID(type);
        Log.e("judgeWLcameraIsPGType", device.getDeviceType());
        switch (device) {
            case INDOOR:
            case OUTDOOR:
            case INDOOR2:
                return context.getResources().getString(R.string.monitor_cloud_video_camera_wlpg);
            // 随便看
            case SIMPLE_N:
            case SIMPLE:
                return context.getResources().getString(R.string.monitor_cloud_video_camera_wlcl);
            // 桌面摄像机
            case DESKTOP_C:
                return "桌面摄像机";
        }
        return null;
    }

    public static void saveEagleUidList(List<AMSDeviceInfo> cameraList) {

        if (cameraList == null) {
            return;
        }
        if (cameraList.isEmpty()) {
            return;
        }

        String eagleUid = "";
//		String cameradevieID="";

        for (AMSDeviceInfo camera : cameraList) {
            if (camera.getDeviceId() == null || camera.getDeviceId().isEmpty()) {
                return;
            }
            eagleUid += camera.getDeviceId() + "@";
        }

        if (eagleUid != null) {
            File file = new File(Environment.getExternalStorageDirectory() + "/wulian/eagle/info/uid");
            file.getParentFile().mkdirs();
            try {
                Writer writer = new FileWriter(file);
                BufferedWriter bw = new BufferedWriter(writer);
                bw.write(eagleUid);
                bw.flush();
                bw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<String> getEagleUidList() {
        File file = new File(Environment.getExternalStorageDirectory() + "/wulian/eagle/info/uid");
        if (!file.exists()) {
            return new ArrayList<>();
        }
        file.getParentFile().mkdirs();
        String[] uidString = null;
        List<String> uidlist = new ArrayList<>();
        try {
            Reader reader = new FileReader(file);
            BufferedReader br = new BufferedReader(reader);
            String uid = br.readLine();
            if (uid != null && uid != "null@" && uid != "") {
                if (uid.length() < 22) {
                    uidString = new String[1];
                    uidString[0] = uid.substring(0, 20);
                } else {
                    uidString = uid.split("@");
                }
            }
//			file.delete();
            br.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (uidString != null) {
            for (int i = 0; i < uidString.length; i++) {
                String oneUid = uidString[i];
                if (oneUid != null && oneUid != "") {
                    uidlist.add(oneUid);
                }
            }
        }
        return uidlist;
    }

    /**
     * 检查存在的UID和新的UID列表中有区别
     *
     * @param cameraList
     * @return
     */
    public static List<String> isUnBind(List<AMSDeviceInfo> cameraList) {
        List<String> noUid = new ArrayList<>();
        List<String> listUid = getEagleUidList();
        List<String> cameraDeviceID = new ArrayList<>();
        if (cameraList.size() <= 0) {
            return getEagleUidList();
        }
        for (AMSDeviceInfo camera : cameraList) {
            cameraDeviceID.add(camera.getDeviceId());
        }
        if (listUid != null) {
            for (String uid : listUid) {
                if (!cameraDeviceID.contains(uid)) {
                    //新的用户列表中没有存在的
                    noUid.add(uid);
                }
            }
        }
        return noUid;
    }
}
