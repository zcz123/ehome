package com.wulian.iot.view.manage;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.wulian.iot.Config;
import com.wulian.iot.bean.BaseCameraInfo;
import com.wulian.iot.server.service.CameraService;
import com.wulian.iot.utils.DownLoadUtils;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.MeshUtil;
/**
 * Created by syf on 2016/10/9.
 */

public class FirmwareUpManage {
    private Context appContext = null;
    private ServiceConnection serviceConnection = null;
    private CameraService.CameraBinder cameraBinder = null;
    private Intent serviceIntent = null;
    private FirmwareUpManage instance = null;
    private final static String TAG = "FirmwareUpManage";
    public static final int CHECKOUT_VERSION_STATE_UP = 0;
    public static final int CHECKOUT_VERSION_STATE_SAME = 1;
    public static final int CHECKOUT_VERSION_STATE_FORCE = 2;
    public FirmwareUpManage(Context context) {
        instance = this;
        instance.appContext = context;
        instance.initServerConnection();
        instance.binderService();
    }
    private void binderService(){
        serviceIntent = new Intent(appContext, CameraService.class);
        instance.appContext.bindService(serviceIntent,serviceConnection,Context.BIND_AUTO_CREATE);
    }
    private void initServerConnection(){
        serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.i(TAG,"bind success");
                setCameraBinder((CameraService.CameraBinder)service);
            }
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.i(TAG,"onServiceDisconnected");
            }
        };
    }

    public void setCameraBinder(CameraService.CameraBinder cameraBinder) {
        this.cameraBinder = cameraBinder;
    }

    public CameraService.CameraBinder getCameraBinder() {
        return cameraBinder;
    }
    public CameraUpPojoFromIOT parseCameConfigInfo(byte[] datas){
        if(cameraBinder!=null){
            setCameraUpPojoFromIOT(cameraBinder.parseCameConfigInfo(datas));
        }
        return getCameraUpPojoFromIOT();
    }
    public void connectServerObtainCamVersion(){
        if(cameraBinder!=null){
            cameraBinder.connectServerObtainCamVersion(Config.parse_xml, Config.XMLUrl, new CameraService.MeshCallback() {
                @Override
                public void success(String msg) {
                    setCameraUpPojoFromServer(MeshUtil.xmlParseString(msg));
                    cameraUpPojoFromIOT.setFileName(cameraUpPojoFromServer.getVersionCode() + Config.firSuffix);
                    cameraUpPojoFromIOT.setAbsolutePath(cameraUpPojoFromIOT.getSavePath() + cameraUpPojoFromIOT.getFileName());
                    serverLogger();
                    if (cameraUpFromServerCallback != null) {
                        cameraUpFromServerCallback.success();
                    }
                }
                @Override
                public void error(String msg) {
                    if(cameraUpFromServerCallback!=null){
                        cameraUpFromServerCallback.error(msg);
                    }
                }
            });
        }
    }
    private void serverLogger(){
        Log.e(TAG, "VersionCode(" + getCameraUpPojoFromServer().getVersionCode() + ")");
        Log.e(TAG, "URL(" + getCameraUpPojoFromServer().getUrl() + ")");
        Log.e(TAG, "VersionName(" + getCameraUpPojoFromServer().getVersionName() + ")");
        Log.e(TAG, "VersionTxts(" + getCameraUpPojoFromServer().getVersionTxts() + ")");
        Log.e(TAG, "RemindTimes(" + getCameraUpPojoFromServer().getRemindTimes() + ")");
    }
    public void downloadCameraUpFile() {
        if (IotUtil.isFirmware(cameraUpPojoFromIOT.getSavePath(), cameraUpPojoFromIOT.getFileName())) {
            IotUtil.delFilePassWay(cameraUpPojoFromIOT.getSavePath(), cameraUpPojoFromIOT.getFileName());
            Log.i(TAG, "delete file");
        }
        if(cameraBinder!=null){
            cameraBinder.startDownLoadUtil(new DownLoadUtils.DownLoadPojo(cameraUpPojoFromServer.getUrl(), cameraUpPojoFromIOT.getAbsolutePath(), downloadListener));
        }
    }

    private DownLoadUtils.DownloadListener downloadListener = new DownLoadUtils.DownloadListener() {
        @Override
        public void processing(long totalSize, long hasDownloadSize) {
            int present = (int) (hasDownloadSize * 100.0 / totalSize);
            if (present >= 100) {
                Log.i(TAG, "download finish");
                if (downloadCameraUpFileCallback != null) {
                    downloadCameraUpFileCallback.downFinish();
                }
            } else {
                if (downloadCameraUpFileCallback != null) {
                    downloadCameraUpFileCallback.updateUi(present);
                }
            }
        }
        @Override
        public void processError(Exception e) {
            Log.e(TAG, "download unfinish");
            if (downloadCameraUpFileCallback != null) {
                downloadCameraUpFileCallback.dismissUi();
            }
        }
    };
    public void updateCamera(Context mContext) {
        //判断是否在同一局域网
        if (IotUtil.isSameIpAddress(mContext, cameraUpPojoFromIOT.getDeviceIp())) {
            if(cameraBinder!=null){
                cameraBinder.writeDataToDeviceBySocket(cameraUpPojoFromIOT.getAbsolutePath(), cameraUpPojoFromIOT.getDeviceIp(), cameraUpPojoFromIOT.getPort(), new CameraUpdateCallback() {
                    @Override
                    public void upGradeSuccess() {
                        IotUtil.delFilePassWay(cameraUpPojoFromIOT.getSavePath(), cameraUpPojoFromIOT.getFileName());
                    }
                    @Override
                    public void upGradeError(String msg) {
                        Log.e(TAG, msg);
                    }
                });
            }
            return;
        }
        Log.d(TAG, " Not in the local area network ");
    }
    public int checkoutVersion() {
        if (getCameraUpPojoFromServer().getVersionCode() > getCameraUpPojoFromIOT().getBuild()) {//需要升级
            return CHECKOUT_VERSION_STATE_UP;
        } else if (getCameraUpPojoFromServer().getVersionCode() == getCameraUpPojoFromIOT().getBuild()) {//最新版本
            return CHECKOUT_VERSION_STATE_SAME;
        } else if (getCameraUpPojoFromIOT().getBuild() <= 17) {//强制升级
            return CHECKOUT_VERSION_STATE_FORCE;
        }
        return -1;
    }
    public void destroy() {
        setCameraUpPojoFromIOT(null);
        setCameraUpPojoFromServer(null);
        appContext.unbindService(serviceConnection);
        appContext.stopService(serviceIntent);
        instance = null;
    }

    public FirmwareUpManage getInstance() {
        return instance;
    }

    public CameraUpPojoFromIOT cameraUpPojoFromIOT = null;
    public CameraUpPojoFromServer cameraUpPojoFromServer = null;
    public CameraUpFromServerCallback cameraUpFromServerCallback = null;
    public DownloadCameraUpFileCallback downloadCameraUpFileCallback = null;

    public void setDownloadCameraUpFileCallback(DownloadCameraUpFileCallback downloadCameraUpFileCallback) {
        this.downloadCameraUpFileCallback = downloadCameraUpFileCallback;
    }

    public void setCameraUpPojoFromIOT(CameraUpPojoFromIOT cameraUpPojoFromIOT) {
        this.cameraUpPojoFromIOT = cameraUpPojoFromIOT;
    }

    public CameraUpPojoFromIOT getCameraUpPojoFromIOT() {
        return cameraUpPojoFromIOT;
    }

    public void setCameraUpPojoFromServer(CameraUpPojoFromServer cameraUpPojoFromServer) {
        this.cameraUpPojoFromServer = cameraUpPojoFromServer;
    }

    public CameraUpPojoFromServer getCameraUpPojoFromServer() {
        return cameraUpPojoFromServer;
    }

    public void setCameraUpFromServerCallback(CameraUpFromServerCallback cameraUpFromServerCallback) {
        this.cameraUpFromServerCallback = cameraUpFromServerCallback;
    }

    public CameraUpFromServerCallback getCameraUpFromServerCallback() {
        return cameraUpFromServerCallback;
    }

    public static class CameraUpPojoFromIOT extends BaseCameraInfo {
        public CameraUpPojoFromIOT() {

        }

        public CameraUpPojoFromIOT(String deviceIp, String deviceVersion, int port, int build) {
            this.deviceIp = deviceIp;
            this.deviceVersion = deviceVersion;
            this.port = port;
            this.build = build;
        }

        private String deviceIp;
        private String deviceVersion;
        private int port;
        private int build;
        private String savePath;
        private String absolutePath;//文件绝对路径
        private String fileName;

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }

        public void setAbsolutePath(String absolutePath) {
            this.absolutePath = absolutePath;
        }

        public String getAbsolutePath() {
            return absolutePath;
        }

        public void setSavePath(String savePath) {
            this.savePath = savePath;
        }

        public String getSavePath() {
            return savePath;
        }

        public String getDeviceIp() {
            return deviceIp;
        }

        public void setDeviceIp(String deviceIp) {
            this.deviceIp = deviceIp;
        }

        public String getDeviceVersion() {
            return deviceVersion;
        }

        public void setDeviceVersion(String deviceVersion) {
            this.deviceVersion = deviceVersion;
        }

        public int getPort() {
            return port;
        }

        public void setPort(int port) {
            this.port = port;
        }

        public int getBuild() {
            return build;
        }

        public void setBuild(int build) {
            this.build = build;
        }
    }

      public static class CameraUpPojoFromServer extends BaseCameraInfo {

        private int versionCode;
        private String versionName;
        private int remindTimes;
        private String versionTxts;
        private String url;


        public int getVersionCode() {
            return versionCode;
        }

        public void setVersionCode(int versionCode) {
            this.versionCode = versionCode;
        }

        public String getVersionName() {
            return versionName;
        }

        public void setVersionName(String versionName) {
            this.versionName = versionName;
        }

        public int getRemindTimes() {
            return remindTimes;
        }

        public void setRemindTimes(int remindTimes) {
            this.remindTimes = remindTimes;
        }

        public String getVersionTxts() {
            return versionTxts;
        }

        public void setVersionTxts(String versionTxts) {
            this.versionTxts = versionTxts;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }
     public static class CameraUpPojo extends BaseCameraInfo{
         public CameraUpPojo(){

         }
         public CameraUpPojo(int upMode, String host, int port, String filePath) {
             this.upMode = upMode;
             this.host = host;
             this.port = port;
             this.filePath = filePath;
         }

         private int upMode;
         private String host;
         private int port;
         private String filePath;

         public int getUpMode() {
             return upMode;
         }

         public void setUpMode(int upMode) {
             this.upMode = upMode;
         }

         public String getHost() {
             return host;
         }

         public void setHost(String host) {
             this.host = host;
         }

         public void setPort(int port) {
             this.port = port;
         }

         public int getPort() {
             return port;
         }

         public String getFilePath() {
             return filePath;
         }

         public void setFilePath(String filePath) {
             this.filePath = filePath;
         }
     }
    public interface CameraUpFromServerCallback {
        void success();

        void error(String msg);
    }

    public interface DownloadCameraUpFileCallback {
        void downFinish();

        void updateUi(int present);

        void dismissUi();
    }
    public interface CameraUpdateCallback{
        void  upGradeSuccess();
        void  upGradeError(String msg);
    }
}



