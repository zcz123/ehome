package cc.wulian.smarthomev5.tools;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.alibaba.fastjson.JSONObject;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.cdm.UpdateCameraSet;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.core.http.HttpProvider;
import cc.wulian.ihome.wan.core.http.Result;
import cc.wulian.ihome.wan.sdk.user.entity.BindUser;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.activity.iotc.share.EagleShareActivity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

public class UpdateCameraInfo implements UpdateCameraSet {
    @Override
    public void deviceUpdate(final String deviceId, final String common, final Handler mHandler) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                Result result = new Result();
                JSONObject params = new JSONObject();
                params.put("deviceId", deviceId);
                params.put("deviceName", common);
                String url = "https://v2.wuliancloud.com:52182/AMS/user/device";
                Map<String, String> headerMap = new HashMap<String, String>();
                headerMap.put("token", SmarthomeFeatureImpl.getData("token"));
                headerMap.put("cmd", "deviceUpdate");
                byte[] body = params.toString().getBytes();
                HttpProvider httpProvider = HttpManager.getDefaultProvider();
                JSONObject json = httpProvider.post(url, headerMap, body);
                result.status = statusFromJsonObject(json);

                Message ms = mHandler.obtainMessage();
                ms.arg1 = result.status;
                ms.what = Config.UPDATE_IS_OK;
                mHandler.sendMessage(ms);
            }
        });
    }
    private int statusFromJsonObject(JSONObject result) {
        int status = -1;
        if (result != null) {
            JSONObject head = result.getJSONObject("header");
            if (head != null && head.containsKey("status")) {
                try {
                    status = Integer.parseInt(head.getString("status"));
                } catch (Exception e) {
                    Logger.error(e);
                }
            }
        }
        return status;
    }

    @Override
    public void deleteEageleCamera(final String deviceId, final boolean isAdmin,final Handler mHandler) {
//         DevicesUserManage.queryUserByDevice(amsDeviceInfo.getDeviceId(), onRunUIThread, HandlerConstant.SUCCESS);
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                if (isAdmin){
                    List<BindUser> bindUsers=  WLUserManager.getInstance().getStub().getUserByDevice(deviceId);
                    List<String> listID=new ArrayList<String>();
                    listID.add(deviceId);
                    if (bindUsers!=null){
                        for (BindUser obj : bindUsers) {
//                            DevicesUserManage.authUser(getAuthUser(obj), listID, false, null);
                            int status= WLUserManager.getInstance().getStub().unbindUser(getAuthUser(obj), listID).status;
                            if(status==0){
                                mHandler.sendEmptyMessage(HandlerConstant.SUCCESS);
                            }else{
                                mHandler.sendEmptyMessage(HandlerConstant.ERROR);
                            }
                        }
                    }
                }else {
                    int status=  DevicesUserManage.unBindShareEagle(deviceId);//分享用户的删除
                    if(status==0){
                        mHandler.sendEmptyMessage(HandlerConstant.SUCCESS);
                    }else{
                        mHandler.sendEmptyMessage(HandlerConstant.ERROR);
                    }
                }
                unreTutkMapping(deviceId,mHandler);
            }
        });

    }

    @Override
    public void startShareActivity(String deviceID, Activity activity) {
        activity.startActivity(new Intent(activity, EagleShareActivity.class).putExtra("SHARE_MODEL",deviceID));

    }


    private void unreTutkMapping(final String deviceId, final Handler mHandler) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BaiduPushManager.getInstance().registerTutkServer(null, deviceId, BaiduPushManager.unreg_mapping, mHandler);
            }
        }).start();
    }
    public String getAuthUser(BindUser bindUser) {
        String user = null;
        if (bindUser.getUserName() != null) {
            user = bindUser.getUserName();
        } else if (bindUser.getUserId() != -1) {
            user = String.valueOf(bindUser.getUserId());
        }
        return user;
    }
}
