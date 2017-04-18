package com.wulian.iot.server.service;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import com.wulian.iot.Config;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.utils.DownLoadUtils;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.MeshUtil;
import com.wulian.iot.view.manage.FirmwareUpManage;

import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

/**
 * Created by syf on 2016/10/11.
 */

public class CameraService extends Service{
    private static final String TAG = "CameraService";
    private DownLoadUtils downLoadUtils = null;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG,"onBind");
        return cameraBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG,"onCreate");
        initBinder();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        setCameraBinder(null);
        if(downLoadUtils!=null){
            downLoadUtils.destroy();
        }
        Log.i(TAG,"onDestroy");
    }
    private void initBinder(){
        Log.i(TAG,"initBinder");
        cameraBinder = new CameraBinder();
    }
    private CameraBinder cameraBinder = null;

    public void setCameraBinder(CameraBinder cameraBinder) {
        this.cameraBinder = cameraBinder;
    }

    public CameraBinder getCameraBinder() {
        return cameraBinder;
    }

    public class CameraBinder extends Binder{
        public FirmwareUpManage.CameraUpPojoFromIOT parseCameConfigInfo(final byte[]datas){
            Log.i(TAG,"parseCameConfigInfo");
            FirmwareUpManage.CameraUpPojoFromIOT cameraUpPojoFromIOT = new FirmwareUpManage.CameraUpPojoFromIOT();
            if (datas.length < 0) {
                return cameraUpPojoFromIOT;
            }
            //修改char为16位，防止ip地址获取位数不够
            char[] ip = new char[15];
            // 设备IP 高版本地址对齐多出4个字节
            int port = -1; // 设备监听端口
            int build = -1; // 表示编译的时间先后 ，值越在版本越新
            char[] version = new char[23]; // char version[64]; // 固件版本号
            // 高版本地址对齐多出5个字节
            // 获取设备ip
            byte[] ipDevice = new byte[15];
            System.arraycopy(datas, 0, ipDevice, 0, 15);

            ip = DateUtil.getChars(ipDevice);
            StringBuilder sb1 = new StringBuilder();
            for (int i = 0; i < ip.length; i++) {
                if(String.valueOf(ip[i]).matches("[\\w\\d_\\+\\.]*$")){
                    sb1.append(String.valueOf(ip[i]));
                }
            }
            if (sb1.toString() != null) {
                Log.i(TAG, "" + sb1.toString().length());
                cameraUpPojoFromIOT.setDeviceIp(sb1.toString());
            }
            // 获取固件版本号。
            int version_length = 0;
            for (int i = 24; i < datas.length; i++) {
                if (datas[i] != 0x00) {
                    version_length++;
                } else {
                    break;
                }
            }
            byte[] versionDevice = new byte[version_length];
            System.arraycopy(datas, 24, versionDevice, 0, version_length);
            version = DateUtil.getChars(versionDevice);
            StringBuilder sb2 = new StringBuilder();
            for (int i = 0; i < version.length; i++) {
                sb2.append(String.valueOf(version[i]));
            }
            if (sb2.toString() != null) {
                cameraUpPojoFromIOT.setDeviceVersion(sb2.toString());
            }
            port = DateUtil.bytesToInt(datas, 16);//端口
            build = DateUtil.bytesToInt(datas, 20);//版本号
            if (port != -1) {
                cameraUpPojoFromIOT.setPort(port);
            }
            if (build != -1) {
                cameraUpPojoFromIOT.setBuild(build);
            }
            cameraUpPojoFromIOT.setSavePath(IotUtil.getVersionPath());
            Log.i(TAG, "DeviceIp(" + cameraUpPojoFromIOT.getDeviceIp() + ")");
            Log.i(TAG, "DeviceVersion(" + cameraUpPojoFromIOT.getDeviceVersion() + ")");
            Log.i(TAG, "Build(" + cameraUpPojoFromIOT.getBuild() + ")");
            Log.i(TAG, "Port(" + cameraUpPojoFromIOT.getPort() + ")");
            return cameraUpPojoFromIOT;
        }
        public void connectServerObtainCamVersion(final int type,final String url,final MeshCallback meshCallback){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    URL httpUrl = null;
                    HttpURLConnection httpURLConnection = null;
                    String res = null;
                    try {
                        if(type==-1){
                            res = "type is null";
                            meshCallback.error(res);
                            return;
                        }
                        if(url==null||url.trim().equals("")){
                            res = "url is null";
                            meshCallback.error(res);
                            return;
                        }
                        httpUrl = new URL(url);
                        httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                        if(httpURLConnection.getResponseCode() == 200){
                            res = MeshUtil.isToString(httpURLConnection.getInputStream());
                            if(res!=null){
                                switch (type){
                                    case Config.parse_xml:
                                        meshCallback.success(res);
                                        break;
                                }
                            }
                        }
                    }catch (Exception ex){
                        String errorMsg = null;
                         if((errorMsg=ex.getLocalizedMessage())==null){
                             errorMsg = "http connection exception";
                         }
                        meshCallback.error(errorMsg);
                    } finally {
                        httpUrl = null;
                        httpURLConnection.disconnect();
                        httpURLConnection = null;
                    }
                }
            }).start();
        }
        public void startDownLoadUtil(DownLoadUtils.DownLoadPojo downLoadPojo){
            downLoadUtils = new DownLoadUtils();
            downLoadUtils.setDownLoadPojo(downLoadPojo);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    downLoadUtils.startDonwLoadFile();
                }
            }).start();
        }
        public void writeDataToDeviceBySocket(final String filePath,final String ip,final int port,final FirmwareUpManage.CameraUpdateCallback cameraUpdateCallback){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Socket mSocket = new Socket();
                    FileInputStream reader = null;
                    DataOutputStream out = null;
                    byte[] buf = null;
                    if (filePath != null) {
                        Log.i(TAG,filePath);
                        try {
                            mSocket.connect(new InetSocketAddress(ip, port), 5000);
                            reader = new FileInputStream(filePath);
                            out = new DataOutputStream(mSocket.getOutputStream());
                            buf = new byte[204800];
                            int read = 0;
                            int count = 0;
                            while ((read = reader.read(buf, 0, buf.length)) != -1) {
                                out.write(buf, 0, read);
                                Log.i(TAG, "" + count++);
                            }
                            Log.i(TAG, "" + read);
                            cameraUpdateCallback.upGradeSuccess();
                        } catch (Exception e) {
                            e.printStackTrace();
                            String res = null;
                            if (e.getLocalizedMessage() != null) {
                                res = e.getLocalizedMessage();
                            } else {
                                res = "camera update error";
                            }
                            Log.e(TAG, res);
                            cameraUpdateCallback.upGradeError(res);
                        } finally {
                            try {
                                buf = null;
                                out.close();
                                reader.close();
                                mSocket.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                                String res = null;
                                if (e.getLocalizedMessage() != null) {
                                    res = e.getLocalizedMessage();
                                } else {
                                    res = "camera update error";
                                }
                                Log.e(TAG, res);
                                cameraUpdateCallback.upGradeError(res);
                            }
                        }
                    }
                }
            }).start();
        }
    }
    public static interface MeshCallback{
        void success(String msg);
        void error(String msg);
    }
}
