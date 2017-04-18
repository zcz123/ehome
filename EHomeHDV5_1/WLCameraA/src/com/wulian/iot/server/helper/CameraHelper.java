package com.wulian.iot.server.helper;

import android.graphics.Bitmap;
import android.util.Log;

import com.tutk.IOTC.Camera;
import com.tutk.IOTC.IRegisterIOTCListener;
import com.tutk.IOTC.MediaCodecMonitor;
import com.tutk.IOTC.MediaSoftCodecMonitor;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.yuantuo.netsdk.TKCamHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * @author syf
 */
public class CameraHelper {
    private final static String TAG = "CameraHelper";
    private TKCamHelper mCamera = null;
    private final int mSelectedChannel = 0;// 通道
    private static CameraHelper instance = null;
    private boolean initialized = false;
    private MediaCodecMonitor mediaCodecMonitor = null;
    private MediaSoftCodecMonitor mediaSoftCodecMonitor = null;
    private IOTCDevChPojo iotcDevChPojo = null;
    private List<Observer> observers = null;
    private List<IOTCDevConnCallback> iotcDevConnCallbacks = null;

    public interface IOTCDevConnCallback {
        void session();

        void avChannel();

        void success();
    }

    public interface Observer {
        void avIOCtrlOnLine();

        void avIOCtrlDataSource(byte[] data, int avIOCtrlMsgType);

        void avIOCtrlMsg(int resCode, String method);//连接类型判断这里不做处理
    }

    protected void notifySession() {
        for (IOTCDevConnCallback obj : iotcDevConnCallbacks) {
            obj.session();
        }
    }

    protected void notifyAvChannel() {
        for (IOTCDevConnCallback obj : iotcDevConnCallbacks) {
            obj.avChannel();
        }
    }

    protected void notifyAttach() {
        for (IOTCDevConnCallback obj : iotcDevConnCallbacks) {
            obj.success();
        }
    }

    public void attach(IOTCDevConnCallback iotcDevConnCallback) {
        if (iotcDevConnCallbacks != null) {
            iotcDevConnCallbacks.add(iotcDevConnCallback);
        }
    }

    public void detach(IOTCDevConnCallback iotcDevConnCallback) {
        if (iotcDevConnCallbacks != null && iotcDevConnCallbacks.size() > 0) {
            iotcDevConnCallbacks.remove(iotcDevConnCallback);
        }
    }

    public void attach(Observer observer) {
        if (observers != null) {
            observers.add(observer);
        }
    }

    public void detach(Observer observer) {
        if (observers != null && observers.size() > 0) {
            observers.remove(observer);
        }
    }

    protected void notifyObserver() {
        for (Observer obj : observers) {
            obj.avIOCtrlOnLine();
        }
    }

    protected void notifyObserver(byte[] data, int type) {
        for (Observer obj : observers) {
            obj.avIOCtrlDataSource(data, type);
        }
    }

    protected void notifyObserver(int resCode, String method) {
        for (Observer obj : observers) {
            obj.avIOCtrlMsg(resCode, method);
        }
    }

    private IRegisterIOTCListener iRegisterIOTCListener = new IRegisterIOTCListener() {
        @Override
        public void receiveSessionInfo(Camera camera, int resultCode) {
            notifyObserver(resultCode, "receiveSessionInfo");
        }

        @Override
        public void receiveIOCtrlData(Camera camera, int avChannel, int avIOCtrlMsgType, byte[] data) {
            notifyObserver(data, avIOCtrlMsgType);
        }

        @Override
        public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
                                     int frameRate, int onlineNm, int frameCount,
                                     int incompleteFrameCount) {
        }

        @Override
        public void receiveFrameDataForMediaCodec(Camera camera, int i,
                                                  byte[] abyte0, int j, int k, byte[] abyte1, boolean flag, int l) {
            notifyObserver();
        }

        @Override
        public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
            notifyObserver();
        }

        @Override
        public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
            notifyObserver(resultCode, "receiveChannelInfo");
        }
    };

    public static CameraHelper getInstance(IOTCDevChPojo iotcDevChPojo) {
        if (instance == null) {
            synchronized (CameraHelper.class) {
                Log.i(TAG, "synchronized");
                if (instance == null) {
                    instance = new CameraHelper(iotcDevChPojo);
                }
            }
        }
        if (instance.initialized == false) {
            instance.initData();
        }
        return instance;
    }

    public void setInitialized(boolean initialized) {//重新initdata
        this.initialized = initialized;
    }

    public TKCamHelper getmCamera() {
        return mCamera;
    }

    private CameraHelper(IOTCDevChPojo iotcDevChPojo) {
        setIotcDevChPojo(iotcDevChPojo);
        observers = new ArrayList<Observer>();
        iotcDevConnCallbacks = new ArrayList<IOTCDevConnCallback>();
    }

    private void initData() {
        initialized = true;
        mCamera = new TKCamHelper("test", getIotcDevChPojo().getTutkUid(), "admin", getIotcDevChPojo().getTutkPwd());
    }

    public void setIotcDevChPojo(IOTCDevChPojo iotcDevChPojo) {
        this.iotcDevChPojo = iotcDevChPojo;
    }

    public IOTCDevChPojo getIotcDevChPojo() {
        return iotcDevChPojo;
    }

    /******************
     * modfi syf 2016.10.18
     *********************/
    public boolean checkSession() {
        return mCamera.getmSID() >= 0;
    }

    public boolean checkAvChannel() {
        if (mCamera.getmAVChannels().size() > 0) {
            return mCamera.getmAVChannels().get(0).getAVIndex() >= 0;
        }
        return false;
    }

    public final void register() {
        if (!checkSession()) {
            createSession();//会话失败创建会话
            notifySession();
            return;
        }
        if (!checkAvChannel()) {
            createAvChannel();//创建av通道
            notifyAvChannel();
            return;
        }
        notifyAttach();
    }

    public final void createSession() {//创建会话
        if (mCamera != null) {
            mCamera.connect(getIotcDevChPojo().getTutkUid(), getIotcDevChPojo().getDevConnMode());
        }
    }

    public final void createAvChannel() {//创建av通道
        if (mCamera != null) {
            mCamera.start(Camera.DEFAULT_AV_CHANNEL, "admin", getIotcDevChPojo().getTutkPwd());
        }
    }

    public final void createVideoStream(int decodMethod) {//创建视频流
        if (mCamera != null) {
            mCamera.startShow(mSelectedChannel, true, false, decodMethod);
        }
    }

    public final void createVideoCarrier(MediaCodecMonitor monitor) {//创建视频载体
        if (monitor != null) {
            mediaCodecMonitor = monitor;
            mediaCodecMonitor.attachCamera(mCamera, mSelectedChannel);
        }
    }
    public final void createVideoCarrier(MediaSoftCodecMonitor monitor) {//创建视频载体
        if (monitor != null) {
            mediaSoftCodecMonitor = monitor;
            mediaSoftCodecMonitor.attachCamera(mCamera, mSelectedChannel);
        }
    }
    public final void destroyVideoStream() {//销毁视频流
        if (mCamera != null) {
            mCamera.stopSpeaking(mSelectedChannel);
            mCamera.stopListening(mSelectedChannel);
            mCamera.stopShow(mSelectedChannel);
        }
    }

    public final void destroyVideoCarrier(MediaCodecMonitor monitor) {//关闭显示
        if (monitor != null) {
            monitor.deattachCamera();
        }
    }
    public final void destroyVideoCarrier(MediaSoftCodecMonitor monitor) {//关闭显示
        if (monitor != null) {
            monitor.deattachCamera();
        }
    }
    public void unregister() {//关闭连接
        if (mCamera != null) {
            mCamera.disconnect();
        }
    }

    public void setmCamera(TKCamHelper mCamera) {
        this.mCamera = mCamera;
    }

    public final void registerstIOTCLiener() {
        if (mCamera != null) {
            mCamera.registerIOTCListener(iRegisterIOTCListener);
        }
    }

    public final void unregisterIOTCLiener() {
        if (mCamera != null) {
            mCamera.unregisterIOTCListener(iRegisterIOTCListener);
        }
    }

    public final void destroyCameraHelper() {
        if (instance != null) {
            if (mCamera != null) {
                unregisterIOTCLiener();
                unregister();
                initialized = false;
                observers = null;
                iotcDevConnCallbacks = null;
            }
            instance = null;
        }
    }
}
