package com.tutk.IOTC;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Build.VERSION;
import android.util.Log;

import com.decoder.util.AacEncoder;
import com.decoder.util.DecG726;
import com.decoder.util.DecH264;
import com.listener.GetVersionFromCameraListener;
import com.muxer.util.MuxerMp4;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlAVStream;
import com.tutk.SLC.AcousticEchoCanceler;
import com.tutk.webrtc.NS;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

public class Camera implements Serializable {
    private final static String TAG = "IOTCamera";
    private static volatile int mCameraCount = 0;
    private static int mDefaultMaxCameraLimit = 4;
    public static final int DEFAULT_AV_CHANNEL = 0;
    public static final int CONNECTION_STATE_NONE = 0;
    public static final int CONNECTION_STATE_CONNECTING = 1;
    public static final int CONNECTION_STATE_CONNECTED = 2;
    public static final int CONNECTION_STATE_DISCONNECTED = 3;
    public static final int CONNECTION_STATE_UNKNOWN_DEVICE = 4;
    public static final int CONNECTION_STATE_WRONG_PASSWORD = 5;
    public static final int CONNECTION_STATE_TIMEOUT = 6;
    public static final int CONNECTION_STATE_UNSUPPORTED = 7;
    public static final int CONNECTION_STATE_CONNECT_FAILED = 8;
    public static final int CONNECTION_STATE_IOTC_MODULE_NOT_INITIAL = 9;
    public static final int CONNECTION_STATE_IOTC_INVALID_SID = 10;
    public static final int CONNECTION_STATE_IOTC_SESSION_CLOSE_BY_REMOTE = 11;
    public static final int CONNECTION_STATE_IOTC_CLIENT_MAX = 12;
    public static final int CONNECTION_STATE_IOTC_NETWORK_IS_POOR = 13;
    public static final int CONNECTION_STATE_IOTC_WAKE_UP = 14;// add syf 唤醒状态
    public static final int CONNECTION_ER_DEVICE_OFFLINE = 15;//add syf 设备不在线
    public static final int CONNECTION_EXCEED_MAX_SESSION = 16;
    public static final int CONNECTION_NOT_INITIALIZED = 17;
    public static final int CONNECTION_DEVICE_NOT_LISTENING = 18;
    public static final int DECODE_WRONG = 1001;//解码异常
    public static final int HARD_DECODE = 0;//硬解码
    public static final int SOFT_DECODE = 1;//软解码
    private final Object mWaitObjectForConnected = new Object();
    private ThreadConnectDev mThreadConnectDev = null;
    private ThreadCheckDevStatus mThreadChkDevStatus = null;
    private ThreadSendAudio mThreadSendAudio = null;
    private AcousticEchoCanceler mAec;
    private volatile int mSID = -1;
    private volatile int mSessionMode = -1;
    private GetVersionFromCameraListener versionlistener;
    private boolean mInitAudio = false;
    private AudioTrack mAudioTrack = null;
    private int mCamIndex = 0;
    protected String dvcTag;//设备标记
    private int DECOD_MODE = 1;
    //变量开始录像   add by guofeng
    private boolean mIsRecording = false;
    private boolean runAudioRecord = false;
    private boolean runAudioTrack = false;
    private volatile int[] bResendHistory = new int[1];
    /* camera info */
    private String mDevUID;
    private String mDevPwd;
    private AVChannel avChannel = null;
    private int avIndex = -1;
    public static int IOTC_Get_SessionID = 0;
    public static int IOTC_Connect_ByUID = 1;

    // add syf
    public AVChannel getAvChannel() {
        return avChannel;
    }

    public void camIndex(int mCamIndex) {
        this.mCamIndex = mCamIndex;
    }

    public int getCamIndex() {
        return mCamIndex;
    }

    private volatile int[] bResend = new int[1];
    private List<IRegisterIOTCListener> mIOTCListeners = Collections.synchronizedList(new Vector<IRegisterIOTCListener>());
    private final List<AVChannel> mAVChannels = Collections.synchronizedList(new Vector<AVChannel>());

    public Camera() {
        mDevUID = "";
        mDevPwd = "";
    }

    public int getSessionMode() {
        return mSessionMode;
    }

    public long getChannelServiceType(int avChannel) {
        long ret = 0;
        synchronized (mAVChannels) {
            for (AVChannel ch : mAVChannels) {
                if (ch.getChannel() == avChannel) {
                    ret = ch.getServiceType();
                    break;
                }
            }
        }
        return ret;
    }

    // add syf
    public void setDvcTag(String dvcTag) {
        this.dvcTag = dvcTag;
    }

    public String getDvcTag() {
        return dvcTag;
    }

    /**
     * add syf
     */
    public int getIOTCListeners() {
        return mIOTCListeners.size();
    }

    public boolean registerIOTCListener(IRegisterIOTCListener listener) {
        boolean result = false;
        if (!mIOTCListeners.contains(listener)) {
            Log.i(TAG, "register IOTC listener");
            mIOTCListeners.add(listener);
            result = true;
        }
        return result;
    }

    public boolean unregisterIOTCListener(IRegisterIOTCListener listener) {
        boolean result = false;
        if (mIOTCListeners.contains(listener)) {
            Log.i(TAG, "unregister IOTC listener");
            mIOTCListeners.remove(listener);
            result = true;
        }
        return result;
    }

    public synchronized static st_SearchDeviceInfo[] SearchLAN() {
        int num[] = new int[1];
        st_SearchDeviceInfo[] result = null;
        result = IOTCAPIs.IOTC_Search_Device_Result(num, 0xFD86AA1C);
        return result;
    }

    public static void setMaxCameraLimit(int limit) {
        mDefaultMaxCameraLimit = limit;
    }

    /**
     * 初始化
     */
    public synchronized static int init() {
        synchronized (Camera.class) {
            int nRet = 0;
            if (mCameraCount == 0) {
                int port = (int) (10000 + (System.currentTimeMillis() % 10000));
                nRet = IOTCAPIs.IOTC_Initialize2(port);
                Log.i(TAG, "IOTC_Initialize2() returns " + nRet);
                if (nRet != IOTCAPIs.IOTC_ER_NoERROR) {
                    Log.i("IOTCamera", "-----------IOTCAPIs_Device exit...!!\n" + nRet);
                    return nRet;
                }
                if (nRet < 0) {
                    return nRet;
                }
                nRet = AVAPIs.avInitialize(16 * mDefaultMaxCameraLimit);
                Log.i(TAG, "avInitialize() = " + nRet);
                if (nRet < 0) {
                    Log.i(TAG, "-----------avInitialize exit...!!\n" + nRet);
                    return nRet;
                }
            }
            mCameraCount++;
            return nRet;
        }
    }

    /**
     * 关闭初始化
     */
    public synchronized static int uninit() {
        synchronized (Camera.class) {
            int nRet = 0;
            if (mCameraCount > 0) {
                mCameraCount--;
                if (mCameraCount == 0) {
                    nRet = AVAPIs.avDeInitialize();
                    Log.i(TAG, "avDeInitialize() returns " + nRet);
                    nRet = IOTCAPIs.IOTC_DeInitialize();
                    Log.i(TAG, "IOTC_DeInitialize() returns " + nRet);
                }
            }
            return nRet;
        }

    }

    /****************
     * modfi syf 2016.10.18
     ********************/
    public void setmSID(int mSID) {
        this.mSID = mSID;
    }

    public int getmSID() {
        return mSID;
    }

    /****************
     * modfi syf 2016.10.18
     ********************/
    public int getDECOD_MODE() {
        return DECOD_MODE;
    }

    public void setDECOD_MODE(int DECOD_MODE) {//add syf
        this.DECOD_MODE = DECOD_MODE;
    }

    public boolean isSessionConnected() {
        Log.i(TAG, "===isSessionConnected  (" + mSID + ")===");
        return mSID >= 0;
    }

    public boolean isWakeUp() {
        return mSID == IOTCAPIs.IOTC_ER_DEVICE_IS_SLEEP;
    }

    public int returnSession() {
        return mSID;
    }

    public boolean isChannelConnected(int avChannel) {
        boolean result = false;
        synchronized (mAVChannels) {
            for (AVChannel ch : mAVChannels) {
                if (avChannel == ch.getChannel()) {
                    result = mSID >= 0 && ch.getAVIndex() >= 0;
                    break;
                }
            }
        }
        return result;
    }

    /**
     * 发送命令队列
     */
    public void sendIOCtrl(int avChannel, int type, byte[] data) {
        synchronized (mAVChannels) {
            Log.e(TAG, "sendIOCtrl");
            for (AVChannel ch : mAVChannels) {
                if (avChannel == ch.getChannel()) {
                    ch.IOCtrlQueue.Enqueue(type, data);
                }
            }
        }
    }

    /**
     * 绑定连接
     */
    // modifi syf
    public void connect(String uid, int connectMode) {
        mDevUID = uid;
        if (mThreadConnectDev == null) {
            mThreadConnectDev = new ThreadConnectDev(connectMode);
            mThreadConnectDev.start();
        }
        if (mThreadChkDevStatus == null) {
            mThreadChkDevStatus = new ThreadCheckDevStatus();
            mThreadChkDevStatus.start();
        }
    }

    /**
     * 绑定连接
     */
    public void connect(String uid, String pwd) {
        mDevUID = uid;
        mDevPwd = pwd;
        if (mThreadConnectDev == null) {
            mThreadConnectDev = new ThreadConnectDev(1);
            mThreadConnectDev.start();
        }
        if (mThreadChkDevStatus == null) {
            mThreadChkDevStatus = new ThreadCheckDevStatus();
            mThreadChkDevStatus.start();
        }
    }

    public void disconnect() {
        synchronized (mAVChannels) {
            for (AVChannel ch : mAVChannels) {
                stopSpeaking(ch.getChannel());
                if (ch.threadStartDev != null)
                    ch.threadStartDev.stopThread();
                if (ch.threadDecAudio != null)
                    ch.threadDecAudio.stopThread();
                if (ch.threadRecvAudio != null)
                    ch.threadRecvAudio.stopThread();
                if (ch.threadRecvIOCtrl != null)
                    ch.threadRecvIOCtrl.stopThread();
                if (ch.threadSendIOCtrl != null)
                    ch.threadSendIOCtrl.stopThread();
                if (ch.threadRecvAudio != null) {
                    try {
                        ch.threadRecvAudio.interrupt();
                        ch.threadRecvAudio.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ch.threadRecvAudio = null;
                }
                if (ch.threadDecAudio != null) {
                    try {
                        ch.threadDecAudio.interrupt();
                        ch.threadDecAudio.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ch.threadDecAudio = null;
                }
                if (ch.threadRecvIOCtrl != null) {
                    try {
                        ch.threadRecvIOCtrl.interrupt();
                        ch.threadRecvIOCtrl.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ch.threadRecvIOCtrl = null;
                }
                if (ch.threadSendIOCtrl != null) {
                    try {
                        ch.threadSendIOCtrl.interrupt();
                        ch.threadSendIOCtrl.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    ch.threadSendIOCtrl = null;
                }
                if (ch.threadStartDev != null && ch.threadStartDev.isAlive()) {
                    try {
                        ch.threadStartDev.interrupt();
                        ch.threadStartDev.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                ch.threadStartDev = null;
                ch.AudioFrameQueue.removeAll();
                ch.AudioFrameQueue = null;
                ch.VideoFrameQueue.removeAll();
                ch.VideoFrameQueue = null;
                ch.IOCtrlQueue.removeAll();
                ch.IOCtrlQueue = null;
                if (ch.getAVIndex() >= 0) {
                    AVAPIs.avClientStop(ch.getAVIndex());
                    Log.i(TAG, "avClientStop(avIndex = " + ch.getAVIndex() + ")");
                }
            }
        }
        mAVChannels.clear();
        synchronized (mWaitObjectForConnected) {
            mWaitObjectForConnected.notify();
        }
        if (mThreadChkDevStatus != null) {
            mThreadChkDevStatus.stopThread();
        }
        if (mThreadConnectDev != null)
            mThreadConnectDev.stopThread();
        if (mThreadChkDevStatus != null) {
            try {
                mThreadChkDevStatus.interrupt();
                mThreadChkDevStatus.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            mThreadChkDevStatus = null;
        }
        if (mThreadConnectDev != null && mThreadConnectDev.isAlive()) {
            try {
                mThreadConnectDev.interrupt();
                mThreadConnectDev.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mThreadConnectDev = null;
        Log.i(TAG, "------------------主动断开链接1");
        if (mSID >= 0) {
            IOTCAPIs.IOTC_Session_Close(mSID);
            Log.i(TAG, "IOTC_Session_Close(nSID = " + mSID + ")");
            Log.i(TAG, "------------------主动断开链接2");
            mSID = -1;
            Log.e(TAG, "IOTC_Session_Close(nSID = " + mSID + ")");
        }
        mSessionMode = -1;
    }

    public void setAEC(boolean paramBoolean) {
        if (paramBoolean) {
            if (mAec == null) {
                mAec = new AcousticEchoCanceler();
                mAec.Open();
            }
        } else {
            if (mAec != null) {
                mAec.close();
                mAec = null;
            }
        }
    }

    /**
     * @param avChannel
     * @param viewAccount
     * @param viewPasswd
     * @author syf
     */
    public void start(int avChannel, String viewAccount, String viewPasswd) {
        Log.e(TAG, "===start   function===");
        AVChannel session = null;
        synchronized (mAVChannels) {
            for (AVChannel ch : mAVChannels) {
                if (ch.getChannel() == avChannel) {
                    session = ch;
                    break;
                }
            }
        }
        if (session == null) {
            AVChannel ch = new AVChannel(avChannel, viewAccount, viewPasswd);
            mAVChannels.add(ch);
            ch.threadStartDev = new ThreadStartDev(ch);
            ch.threadStartDev.start();
            ch.threadRecvIOCtrl = new ThreadRecvIOCtrl(ch);
            ch.threadRecvIOCtrl.start();
            ch.threadSendIOCtrl = new ThreadSendIOCtrl(ch);
            ch.threadSendIOCtrl.start();
        } else {
            if (session.threadStartDev == null) {
                session.threadStartDev = new ThreadStartDev(session);
                session.threadStartDev.start();
            }
            if (session.threadRecvIOCtrl == null) {
                session.threadRecvIOCtrl = new ThreadRecvIOCtrl(session);
                session.threadRecvIOCtrl.start();
            }
            if (session.threadSendIOCtrl == null) {
                session.threadSendIOCtrl = new ThreadSendIOCtrl(session);
                session.threadSendIOCtrl.start();
            }
        }
    }

    /**
     * @param avChannel
     * @author syf
     */
    public void stop(int avChannel) {
        synchronized (mAVChannels) {
            int idx = -1;
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (ch.getChannel() == avChannel) {
                    idx = i;
                    stopSpeaking(ch.getChannel());
                    if (ch.threadStartDev != null)
                        ch.threadStartDev.stopThread();
                    if (ch.threadDecAudio != null)
                        ch.threadDecAudio.stopThread();
                    if (ch.threadRecvAudio != null)
                        ch.threadRecvAudio.stopThread();
                    if (ch.threadRecvIOCtrl != null)
                        ch.threadRecvIOCtrl.stopThread();
                    if (ch.threadSendIOCtrl != null)
                        ch.threadSendIOCtrl.stopThread();
                    if (ch.threadRecvAudio != null) {
                        try {
                            ch.threadRecvAudio.interrupt();
                            ch.threadRecvAudio.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadRecvAudio = null;
                    }
                    if (ch.threadDecAudio != null) {
                        try {
                            ch.threadDecAudio.interrupt();
                            ch.threadDecAudio.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadDecAudio = null;
                    }
                    if (ch.threadRecvIOCtrl != null) {
                        try {
                            ch.threadRecvIOCtrl.interrupt();
                            ch.threadRecvIOCtrl.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadRecvIOCtrl = null;
                    }

                    if (ch.threadSendIOCtrl != null) {
                        try {
                            ch.threadSendIOCtrl.interrupt();
                            ch.threadSendIOCtrl.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadSendIOCtrl = null;
                    }

                    if (ch.threadStartDev != null && ch.threadStartDev.isAlive()) {
                        try {
                            ch.threadStartDev.interrupt();
                            ch.threadStartDev.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    ch.threadStartDev = null;
                    ch.AudioFrameQueue.removeAll();
                    ch.AudioFrameQueue = null;
                    ch.VideoFrameQueue.removeAll();
                    ch.VideoFrameQueue = null;
                    ch.IOCtrlQueue.removeAll();
                    ch.IOCtrlQueue = null;
                    if (ch.getAVIndex() >= 0) {
                        AVAPIs.avClientStop(ch.getAVIndex());
                        Log.i(TAG, "avClientStop(avIndex = " + ch.getAVIndex() + ")");
                    }
                    break;
                }
            }
            if (idx >= 0) {
                mAVChannels.remove(idx);
            }
        }
    }

    /**
     * @param avChannel
     * @param fileName
     * @param flag
     * @author syf
     */
    public void startRecording(int avChannel, String fileName, boolean flag) {
        Log.e("startRecording", "startRecording");
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                try {
                    if (i >= this.mAVChannels.size())
                        return;
                    AVChannel localAVChannel = (AVChannel) this.mAVChannels.get(i);
                    if (avChannel == localAVChannel.getChannel()) {
                        Log.e("startRecording", "startRecording" + avChannel);
                        localAVChannel.AudioRecordFrameQueue.removeAll();   // 清理音频通道。
                        localAVChannel.VideoFrameQueue.removeAll();
                        if (localAVChannel.mThreadRecordingMovie == null) {
                            localAVChannel.mThreadRecordingMovie = new ThreadRecordingMovie(localAVChannel, fileName, flag);
                            localAVChannel.mThreadRecordingMovie.start();
                        }
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param avChannel
     * @author syf
     */
    public void stopRecording(int avChannel) {
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (avChannel == ch.getChannel()) {
                    if (ch.mThreadRecordingMovie != null) {
                        ch.mThreadRecordingMovie.stopThread();
                        try {
                            ch.mThreadRecordingMovie.interrupt();
                            ch.mThreadRecordingMovie.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.mThreadRecordingMovie = null;
                    }
                    ch.AudioRecordFrameQueue.removeAll();
                    ch.VideoFrameQueue.removeAll();
                    break;
                }
            }
        }
    }

    /**
     * @param avChannel
     * @param avNoClearBuf
     * @param runSoftwareDecode
     * @author syf
     */
    public void startShow(int avChannel, boolean avNoClearBuf, boolean runSoftwareDecode, int decodMethod) {
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (ch.getChannel() == avChannel) {
                    ch.VideoFrameQueue.removeAll();
                    if (android.os.Build.VERSION.SDK_INT >= 16 && !runSoftwareDecode) {
                        Camera.this.setDECOD_MODE(decodMethod);
                        switch (Camera.this.getDECOD_MODE()) {
                            case HARD_DECODE:
                                Log.i(TAG, "===Hard  Decode ===");
                                if (ch.threadMediaCodecRecvVideo == null) {//硬解码
                                    ch.threadMediaCodecRecvVideo = new ThreadMediaCodecRecvVideo(ch);
                                    ch.threadMediaCodecRecvVideo.start();
                                }
                                break;
                            case SOFT_DECODE:
                                Log.i(TAG, "===Soft  Decode ===");
                                if (ch.mThreadRecvVideo == null) {//软解码
                                    ch.mThreadRecvVideo = new ThreadRecvVideo(ch);
                                    ch.mThreadRecvVideo.start();
                                }
                                if (ch.mThreadDecodeVideo == null) {
                                    ch.mThreadDecodeVideo = new ThreadDecodeVideo(ch);
                                    ch.mThreadDecodeVideo.start();
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    /**
     * @param avChannel
     * @author syf
     */
    public void stopShow(int avChannel) {
        if (mAVChannels.size() > 0) {
            synchronized (mAVChannels) {
                for (int i = 0; i < mAVChannels.size(); i++) {
                    AVChannel ch = mAVChannels.get(i);
                    if (ch.getChannel() == avChannel) {
                        if (android.os.Build.VERSION.SDK_INT >= 16) {
                            //硬解码关闭
                            if (ch.threadMediaCodecRecvVideo != null) {
                                ch.threadMediaCodecRecvVideo.stopThread();
                                try {
                                    ch.threadMediaCodecRecvVideo.interrupt();
                                    ch.threadMediaCodecRecvVideo.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ch.threadMediaCodecRecvVideo = null;
                            }
                            // 软解码关闭
                            if (ch.mThreadRecvVideo != null) {
                                ch.mThreadRecvVideo.stopThread();
                                try {
                                    ch.mThreadRecvVideo.interrupt();
                                    ch.mThreadRecvVideo.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ch.mThreadRecvVideo = null;
                            }
                            if (ch.mThreadDecodeVideo != null) {
                                ch.mThreadDecodeVideo.stopThread();
                                try {
                                    ch.mThreadDecodeVideo.interrupt();
                                    ch.mThreadDecodeVideo.join();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                ch.mThreadDecodeVideo = null;
                            }
                        }
                        ch.VideoFrameQueue.removeAll();
                        break;
                    }
                }
            }
        }
    }

    public void startSpeaking(int avChannel) {
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (ch.getChannel() == avChannel) {
                    ch.AudioFrameQueue.removeAll();
                    if (mThreadSendAudio == null) {
                        mThreadSendAudio = new ThreadSendAudio(ch);
                        mThreadSendAudio.start();
                    }
                    break;
                }
            }
        }
    }

    public void stopSpeaking(int avChannel) {

        if (mThreadSendAudio != null) {
            mThreadSendAudio.stopThread();

            try {
                mThreadSendAudio.interrupt();
                mThreadSendAudio.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            mThreadSendAudio = null;
        }
    }


    public void stopListening(int avChannel) {
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (avChannel == ch.getChannel()) {
                    if (ch.threadRecvAudio != null) {
                        ch.threadRecvAudio.stopThread();
                        try {
                            ch.threadRecvAudio.interrupt();
                            ch.threadRecvAudio.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadRecvAudio = null;
                    }
                    if (ch.threadDecAudio != null) {
                        ch.threadDecAudio.stopThread();
                        try {
                            ch.threadDecAudio.interrupt();
                            ch.threadDecAudio.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ch.threadDecAudio = null;
                    }
                    ch.AudioFrameQueue.removeAll();
                    break;
                }
            }
        }
    }

    public void startListening(int avChannel, boolean paramBoolean) {
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                try {
                    Log.e(TAG, "-----------------mAVChannels.size()" + mAVChannels.size());
                    if (i >= this.mAVChannels.size())
                        return;
                    AVChannel localAVChannel = (AVChannel) this.mAVChannels.get(i);
                    if (avChannel == localAVChannel.getChannel()) {
                        localAVChannel.AudioFrameQueue.removeAll();
                        if (localAVChannel.threadRecvAudio == null) {
                            Log.e(TAG, "-----------------new ThreadRecvAudio");
                            localAVChannel.threadRecvAudio = new ThreadRecvAudio(localAVChannel, paramBoolean);
                            localAVChannel.threadRecvAudio.start();
                        }

                        if (localAVChannel.threadDecAudio == null) {
                            Log.e(TAG, "-----------------new threadDecAudio");
                            localAVChannel.threadDecAudio = new ThreadDecodeAudio(localAVChannel);
                            localAVChannel.threadDecAudio.start();
                        }
                        return;
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    public Bitmap Snapshot(int avChannel) {

        Bitmap result = null;
        Log.i(TAG, "-------avChannel=" + avChannel);
        synchronized (mAVChannels) {
            for (int i = 0; i < mAVChannels.size(); i++) {
                AVChannel ch = mAVChannels.get(i);
                if (avChannel == ch.getChannel()) {
                    result = ch.LastFrame;
                    break;
                }
            }
        }
        return result;
    }

    private synchronized boolean audioDev_init(int sampleRateInHz, int channel, int dataBit, int codec_id) {
        if (!mInitAudio) {
            int channelConfig = 2;
            int audioFormat = 2;
            int mMinBufSize = 0;
            channelConfig = (channel == AVFrame.AUDIO_CHANNEL_STERO) ? AudioFormat.CHANNEL_CONFIGURATION_STEREO : AudioFormat.CHANNEL_CONFIGURATION_MONO;
            audioFormat = (dataBit == AVFrame.AUDIO_DATABITS_16) ? AudioFormat.ENCODING_PCM_16BIT : AudioFormat.ENCODING_PCM_8BIT;
            mMinBufSize = AudioTrack.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
            if (mMinBufSize == AudioTrack.ERROR_BAD_VALUE || mMinBufSize == AudioTrack.ERROR)
                return false;
            try {
                mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRateInHz, channelConfig, audioFormat, mMinBufSize, AudioTrack.MODE_STREAM);
                Log.i(TAG, "init AudioTrack with SampleRate:" + sampleRateInHz + " " + ((dataBit == AVFrame.AUDIO_DATABITS_16) ? String.valueOf(16) : String.valueOf(8)) + "bit " + (channel == AVFrame.AUDIO_CHANNEL_STERO ? "Stereo" : "Mono"));
                ;
            } catch (IllegalArgumentException iae) {
                iae.printStackTrace();
                return false; // return----------------------------------------
            }
            mAudioTrack.setStereoVolume(1.0f, 1.0f);
            mAudioTrack.play();
            mInitAudio = true;
            return true;
        } else
            return false;
    }

    private synchronized void audioDev_stop(int codec_id) {
        if (mInitAudio) {
            if (mAudioTrack != null) {
                mAudioTrack.stop();
                mAudioTrack.release();
                mAudioTrack = null;
            }
            mInitAudio = false;
        }
    }

    private class ThreadDecodeVideo extends Thread {
        private final static String TAG = "ThreadDecodeVideo";
        static final int MAX_FRAMEBUF = 1920 * 1080 * 2;
        private boolean m_bIsRunning = false;
        private AVChannel mAVChannel;

        public ThreadDecodeVideo(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            m_bIsRunning = false;
        }

        @Override
        public void run() {
            System.gc();
            int avFrameSize = 0;
            AVFrame avFrame = null;
            int videoWidth = 0;
            int videoHeight = 0;
            long firstTimeStampFromDevice = 0;
            long firstTimeStampFromLocal = 0;
            long sleepTime = 0;
            long t1 = 0, t2 = 0;
            long lastFrameTimeStamp = 0;
            long delayTime = 0;
            int[] framePara = new int[4];
            byte[] bufOut = new byte[MAX_FRAMEBUF];
            byte[] bmpBuff = null;
            ByteBuffer bytBuffer = null;
            Bitmap bmp = null;
            int[] out_width = new int[1];
            int[] out_height = new int[1];
            int[] out_size = new int[1];
            boolean bInitH264 = false;
            boolean bInitMpeg4 = false;
            mAVChannel.VideoFPS = 0;
            m_bIsRunning = true;
            System.gc();
            while (m_bIsRunning) {
                if (mAVChannel.VideoFrameQueue.getCount() > 0) {
                    avFrame = mAVChannel.VideoFrameQueue.removeHead();
                    if (avFrame == null)
                        continue;
                    avFrameSize = avFrame.getFrmSize();
                } else {
                    continue;
                }
                while (mAVChannel.VideoFrameQueue.getCount() > 0 && delayTime > 1000) {
                    int skipTime = 0;
                    AVFrame tmp = mAVChannel.VideoFrameQueue.removeHead();
                    if (tmp == null) continue;
                    skipTime += (tmp.getTimeStamp() - lastFrameTimeStamp);
                    lastFrameTimeStamp = tmp.getTimeStamp();
                    while (true) {
                        if (!mAVChannel.VideoFrameQueue.isFirstIFrame()) {
                            tmp = mAVChannel.VideoFrameQueue.removeHead();
                            if (tmp == null) break;
                            skipTime += (tmp.getTimeStamp() - lastFrameTimeStamp);
                            Log.i(TAG, "low decode performance, drop " + (tmp.isIFrame() ? "I" : "P") + " frame, skip time: " + (tmp.getTimeStamp() - lastFrameTimeStamp) + ", total skip: " + skipTime);
                            lastFrameTimeStamp = tmp.getTimeStamp();
                        } else break;
                    }
                    delayTime -= skipTime;
                    Log.i(TAG, "delayTime: " + delayTime);
                }
                if (avFrameSize > 0) {
                    out_size[0] = 0;
                    out_width[0] = 0;
                    out_height[0] = 0;
                    if (avFrame.getCodecId() == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                        t1 = System.currentTimeMillis();
                        if (!bInitH264) {
                            DecH264.InitDecoder();
                            bInitH264 = true;
                        }
                        DecH264.DecoderNal(avFrame.frmData, avFrameSize, framePara, bufOut, false);
                        Log.e(TAG, "DecH264.DecoderNal");
                    }
                    if (avFrame.getCodecId() == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                        out_width[0] = framePara[2];
                        out_height[0] = framePara[3];
                        out_size[0] = out_width[0] * out_height[0] * 2;
                    }
                    if (out_size[0] > 0 && out_width[0] > 0 && out_height[0] > 0) {
                        if (videoWidth != out_width[0] || videoHeight != out_height[0]) {
                            videoWidth = out_width[0];
                            videoHeight = out_height[0];
                            bmpBuff = new byte[out_size[0]];
                            bmp = Bitmap.createBitmap(videoWidth, videoHeight, android.graphics.Bitmap.Config.RGB_565);
                            Log.i(TAG, "------------createBitmap");
                        }
                        if (bmpBuff != null) {
                            System.arraycopy(bufOut, 0, bmpBuff, 0, videoWidth * videoHeight * 2);
                            bytBuffer = ByteBuffer.wrap(bufOut); // for Android 4.2
                            bmp.copyPixelsFromBuffer(bytBuffer);
                        }
                        if (avFrame != null && firstTimeStampFromDevice != 0 && firstTimeStampFromLocal != 0) {
                            long t = System.currentTimeMillis();
                            t2 = t - t1;
                            sleepTime = (firstTimeStampFromLocal + (avFrame.getTimeStamp() - firstTimeStampFromDevice)) - t;
                            delayTime = sleepTime * -1;
                            if (sleepTime >= 0) {
                                if ((avFrame.getTimeStamp() - lastFrameTimeStamp) > 1000) {
                                    firstTimeStampFromDevice = avFrame.getTimeStamp();
                                    firstTimeStampFromLocal = t;
                                    Log.i("IOTCamera", "RESET base timestamp");
                                    if (sleepTime > 1000) sleepTime = 33;
                                }
                                if (sleepTime > 1000) sleepTime = 1000;
                                try {
                                    Thread.sleep(sleepTime);
                                } catch (Exception e) {
                                }
                            }
                            lastFrameTimeStamp = avFrame.getTimeStamp();
                        }
                        if (firstTimeStampFromDevice == 0 || firstTimeStampFromLocal == 0) {
                            firstTimeStampFromDevice = lastFrameTimeStamp = avFrame.getTimeStamp();
                            firstTimeStampFromLocal = System.currentTimeMillis();
                        }
                        // -- end calculate sleep time --
                        mAVChannel.VideoFPS++;
                        synchronized (mIOTCListeners) {
                            for (int i = 0; i < mIOTCListeners.size(); i++) {
                                IRegisterIOTCListener listener = mIOTCListeners.get(i);
                                listener.receiveFrameData(Camera.this, mAVChannel.getChannel(), bmp);
                                Log.e(TAG, "ThreadDecodeVideo2");
                            }
                        }
                        mAVChannel.LastFrame = bmp;
                    }
                }
                if (avFrame != null) {
                    avFrame.frmData = null;
                    avFrame = null;
                }
            }
            // while end
            if (bInitH264) {
                DecH264.UninitDecoder();
            }
            bufOut = null;
            bmpBuff = null;
            if (bmp != null) {
                bmp.recycle();
                bmp = null;
            }
            System.gc();
            Log.i(TAG, "===ThreadDecodeVideo exit===");
        }
    }

    /**
     * 软解码获取数据
     *
     * @author syf
     */
    private class ThreadRecvVideo extends Thread {
        private final static String TAG = "ThreadRecvVideo";
        private static final int MAX_BUF_SIZE = 1048576;//1280 * 720 * 3  1048576
        private boolean bIsRunning = false;
        private AVChannel mAVChannel;

        public ThreadRecvVideo(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            bIsRunning = false;
        }

        @Override
        public void run() {
            System.gc();
            bIsRunning = true;
            while (bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0)) {
                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mAVChannel.VideoBPS = 0;
            byte[] buf = new byte[MAX_BUF_SIZE];
            byte[] pFrmInfoBuf = new byte[AVFrame.FRAMEINFO_SIZE];
            int[] pFrmNo = new int[1];
            int nCodecId = 0;
            int nReadSize = 0;
            int nFrmCount = 0;
            int nIncompleteFrmCount = 0;
            int nOnlineNumber = 0;
            long nPrevFrmNo = 0x0FFFFFFF;
            long lastTimeStamp = System.currentTimeMillis();
            int[] outBufSize = new int[1];
            int[] outFrmSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            if (bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, Packet.intToByteArray_Little(mCamIndex));
            }
            mAVChannel.AudioFrameQueue.removeAll();
            if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                AVAPIs.avClientCleanBuf(mAVChannel.getAVIndex());
            }
            while (bIsRunning) {
                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                    if (System.currentTimeMillis() - lastTimeStamp > 1000) {
                        lastTimeStamp = System.currentTimeMillis();
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveFrameInfo(Camera.this, mAVChannel.getChannel(), (mAVChannel.AudioBPS + mAVChannel.VideoBPS) * 8 / 1024, mAVChannel.VideoFPS, nOnlineNumber, nFrmCount, nIncompleteFrmCount);
                        }
                        mAVChannel.VideoFPS = mAVChannel.VideoBPS = mAVChannel.AudioBPS = 0;
                    }
                    nReadSize = AVAPIs.avRecvFrameData2(mAVChannel.getAVIndex(), buf, buf.length, outBufSize, outFrmSize, pFrmInfoBuf, pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);
                    if (nReadSize >= 0) {
                        mAVChannel.VideoBPS += outBufSize[0];
                        nFrmCount++;
                        byte[] frameData = new byte[nReadSize];
                        System.arraycopy(buf, 0, frameData, 0, nReadSize);
                        AVFrame frame = new AVFrame(pFrmNo[0], AVFrame.FRM_STATE_COMPLETE, pFrmInfoBuf, frameData, nReadSize);
                        nCodecId = (int) frame.getCodecId();
                        nOnlineNumber = (int) frame.getOnlineNum();//在线人数
                        Log.d(TAG, "===CodecId(" + nCodecId + ")===");
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_H264) {//数据类型  H264
                            if (frame.isIFrame() || pFrmNo[0] == (nPrevFrmNo + 1)) {
                                nPrevFrmNo = pFrmNo[0];
                                mAVChannel.VideoFrameQueue.addLast(frame);
                                Log.d(TAG, "===Cache Data===");
                            }
                        }
                    } else if (nReadSize == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_DATA_NOREADY) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_BUFPARA_MAXSIZE_INSUFF) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_MEM_INSUFF) {
                        nFrmCount++;
                        nIncompleteFrmCount++;
                        Log.i(TAG, "AV_ER_MEM_INSUFF");
                    } else if (nReadSize == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                        Log.i(TAG, "AV_ER_LOSED_THIS_FRAME");
                        nFrmCount++;
                        nIncompleteFrmCount++;
                    } else if (nReadSize == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                        nFrmCount++;
                        mAVChannel.VideoBPS += outBufSize[0];
                        if (outFrmInfoBufSize[0] == 0 || (outFrmSize[0] * 0.9) > outBufSize[0] || (int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME) {
                            nIncompleteFrmCount++;
                            Log.i(TAG, ((int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME ? "P" : "I") + " frame, outFrmSize(" + outFrmSize[0] + ") * 0.9 = " + ((outFrmSize[0] * 0.9)) + " > outBufSize(" + outBufSize[0] + ")");
                            continue;
                        }
                        byte[] frameData = new byte[outFrmSize[0]];
                        System.arraycopy(buf, 0, frameData, 0, outFrmSize[0]);
                        nCodecId = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MJPEG) {
                            nIncompleteFrmCount++;
                            continue;
                        }
                    }
                }
            }// while end
            mAVChannel.VideoFrameQueue.removeAll();
            if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STOP, Packet.intToByteArray_Little(mCamIndex));
                AVAPIs.avClientCleanBuf(mAVChannel.getAVIndex());
            }
            buf = null;
            Log.i(TAG, "===ThreadRecvVideo exit===");
        }
    }

    private class ThreadConnectDev extends Thread {
        private int ret = 0;
        private int mConnType = -1;
        private boolean mIsRunning = false;
        private Object m_waitForStopConnectThread = new Object();

        public ThreadConnectDev(int connType) {
            mConnType = connType;
            Log.d(TAG, "===启动连接函数===");
        }

        public void stopThread() {
            mIsRunning = false;
            if (mSID < 0)
                IOTCAPIs.IOTC_Connect_Stop();
            synchronized (m_waitForStopConnectThread) {
                m_waitForStopConnectThread.notify();
            }
        }

        public void run() {
            int nRetryForIOTC_Conn = 0;
            mIsRunning = true;
            while (mIsRunning && mSID < 0) {
                for (int i = 0; i < mIOTCListeners.size(); i++) {
                    IRegisterIOTCListener listener = mIOTCListeners.get(i);
                    listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_CONNECTING);
                }
                if (mConnType == IOTC_Get_SessionID) {
                    mSID = IOTCAPIs.IOTC_Get_SessionID();
                    Log.i(TAG, "IOTC_Get_SessionID(" + mDevUID + ") returns " + mSID);
                } else if (mConnType == IOTC_Connect_ByUID) {
                    mSID = IOTCAPIs.IOTC_Connect_ByUID(mDevUID);
                    Log.i(TAG, "IOTC_Connect_ByUID(" + mDevUID + ") returns " + mSID);
                }
                if (mSID >= 0) {
                    Log.i(TAG, "===申请链接====");
                    Log.i(TAG, "===mSID(" + mSID + ")===");
                    ret = IOTCAPIs.IOTC_Connect_ByUID_Parallel(mDevUID, mSID);
                    St_SInfo stSInfo = new St_SInfo();
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_CONNECTED);
                    }
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.notify();
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_CONNECT_IS_CALLING) {
                    Log.e(TAG, "===IOTC_ER_CONNECT_IS_CALLING===");
                    try {
                        synchronized (m_waitForStopConnectThread) {
                            m_waitForStopConnectThread.wait(1000);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_FAIL_CONNECT_SEARCH) {
                    Log.e(TAG, "===IOTC_ER_DEVICE_NOT_LISTENING===");
                } else if (mSID == IOTCAPIs.IOTC_ER_DEVICE_NOT_LISTENING) {
                    Log.e(TAG, "===IOTC_ER_DEVICE_NOT_LISTENING===");
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_DEVICE_NOT_LISTENING);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_NOT_INITIALIZED) {
                    Log.e(TAG, "===IOTC_ER_NOT_INITIALIZED===");
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_NOT_INITIALIZED);
                    }
                    break;
                } else if (mSID == IOTCAPIs.IOTC_ER_DEVICE_IS_SLEEP) {// add syf 用于唤醒
                    Log.e(TAG, "===WAKE UP===");
                    IOTCAPIs.IOTC_WakeUp_WakeDevice(mDevUID);
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_IOTC_WAKE_UP);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_DEVICE_EXCEED_MAX_SESSION) {// add syf 设备超过最大回话
                    Log.e(TAG, "===EXCEED_MAX_SESSION===");
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_EXCEED_MAX_SESSION);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_DEVICE_OFFLINE) {//add syf 设备不在线
                    Log.e(TAG, "===DEVICE_OFFLINE===");
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_ER_DEVICE_OFFLINE);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_NOT_INITIALIZED) {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_IOTC_MODULE_NOT_INITIAL);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_EXCEED_MAX_SESSION) {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_IOTC_CLIENT_MAX);
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_UNKNOWN_DEVICE || mSID == IOTCAPIs.IOTC_ER_UNLICENSE || mSID == IOTCAPIs.IOTC_ER_CAN_NOT_FIND_DEVICE || mSID == IOTCAPIs.IOTC_ER_TIMEOUT) {
                    if (mSID != IOTCAPIs.IOTC_ER_TIMEOUT) {
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_UNKNOWN_DEVICE);
                        }
                    }
                    nRetryForIOTC_Conn++;

                    try {
                        long sleepTime = nRetryForIOTC_Conn > 60 ? 60000 : nRetryForIOTC_Conn * 1000;
                        synchronized (m_waitForStopConnectThread) {
                            m_waitForStopConnectThread.wait(sleepTime);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else if (mSID == IOTCAPIs.IOTC_ER_DEVICE_NOT_SECURE_MODE ||
                        mSID == IOTCAPIs.IOTC_ER_DEVICE_SECURE_MODE) {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {

                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_UNSUPPORTED);
                    }
                    break;
                } else {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_CONNECT_FAILED);
                    }
                    break;
                }
            }
            Log.i(TAG, "===ThreadConnectDev exit===");
        }
    }

    public int getAVIndex() {
        return avIndex;
    }

    public List<AVChannel> getmAVChannels() {
        return mAVChannels;
    }

    private class ThreadStartDev extends Thread {

        private boolean mIsRunning = false;
        private AVChannel mAVChannel;
        private Object mWaitObject = new Object();

        public ThreadStartDev(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            mIsRunning = false;
            if (mSID >= 0) {
                Log.i(TAG, "avClientExit(" + mSID + ", " + mAVChannel.getChannel() + ")");
                AVAPIs.avClientExit(mSID, mAVChannel.getChannel());
            }
            synchronized (mWaitObject) {
                mWaitObject.notify();
            }
        }

        @Override
        public void run() {
            mIsRunning = true;
            int avIndex = -1;
            while (mIsRunning) {
                if (mSID < 0) {
                    try {
                        synchronized (mWaitObjectForConnected) {
                            mWaitObjectForConnected.wait(100);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    continue;
                }
                for (int i = 0; i < mIOTCListeners.size(); i++) {
                    IRegisterIOTCListener listener = mIOTCListeners.get(i);
                    listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), CONNECTION_STATE_CONNECTING);
                }
                int[] nServType = new int[1];
                nServType[0] = -1;
                avIndex = AVAPIs.avClientStart2(mSID, mAVChannel.getViewAcc(), mAVChannel.getViewPwd(), 30, nServType, mAVChannel.getChannel(), bResend);
                Log.i(TAG, "avClientStart(" + mAVChannel.getChannel() + ", " + mAVChannel.getViewAcc() + ", " + mAVChannel.getViewPwd() + ") in Session(" + mSID + ") returns " + avIndex);
                long servType = nServType[0];
                if (avIndex >= 0) {
                    mAVChannel.setAVIndex(avIndex);
                    mAVChannel.setServiceType(servType);
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), CONNECTION_STATE_CONNECTED);
                    }
                    break;
                } else if (avIndex == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT || avIndex == AVAPIs.AV_ER_TIMEOUT || avIndex == -20009) {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), CONNECTION_STATE_TIMEOUT);
                    }
                } else if (avIndex == AVAPIs.AV_ER_WRONG_VIEWACCorPWD) {
                    for (int i = 0; i < mIOTCListeners.size(); i++) {
                        IRegisterIOTCListener listener = mIOTCListeners.get(i);
                        listener.receiveChannelInfo(Camera.this, mAVChannel.getChannel(), CONNECTION_STATE_WRONG_PASSWORD);
                    }
                    break;
                } else {
                    try {
                        synchronized (mWaitObject) {
                            mWaitObject.wait(1000);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("IOTCamera", "===ThreadStartDev exit===");
        }
    }

    private class ThreadCheckDevStatus extends Thread {

        private boolean m_bIsRunning = false;
        private Object m_waitObjForCheckDevStatus = new Object();

        public void stopThread() {

            m_bIsRunning = false;

            synchronized (m_waitObjForCheckDevStatus) {
                m_waitObjForCheckDevStatus.notify();
            }
        }

        @Override
        public void run() {
            super.run();

            m_bIsRunning = true;
            St_SInfo stSInfo = new St_SInfo();
            int ret = 0;

            while (m_bIsRunning && mSID < 0) {

                try {

                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            while (m_bIsRunning) {
                if (mSID >= 0) {
                    ret = IOTCAPIs.IOTC_Session_Check(mSID, stSInfo);
                    if (ret >= 0) {
                        //在这个值为1的时候 会出现连接非常慢的情况。   需要报给用户知道。对于 连接转台出现的各种异常，捕捉到后要及时做处理，就是断开链接，执行disconnect()函数暂时没做。
                        //Log.i("IOTCamera", "------------"+mSessionMode);
                        if (mSessionMode != stSInfo.Mode) {
                            mSessionMode = stSInfo.Mode;

                        }
                    } else if (ret == IOTCAPIs.IOTC_ER_REMOTE_TIMEOUT_DISCONNECT || ret == IOTCAPIs.IOTC_ER_TIMEOUT) {

                        Log.i("IOTCamera", "IOTC_Session_Check(" + mSID + ") timeout");
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_TIMEOUT);
                        }
                    } else if (ret == IOTCAPIs.IOTC_ER_INVALID_SID) {

                        Log.i("IOTCamera", "IOTC_Session_Check(" + mSID + ")");
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_IOTC_INVALID_SID);
                        }
                    } else if (ret == IOTCAPIs.IOTC_ER_SESSION_CLOSE_BY_REMOTE) {

                        Log.i("IOTCamera", "IOTC_Session_Check(" + mSID + ") timeout");
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_IOTC_SESSION_CLOSE_BY_REMOTE);
                        }
                    } else {

                        Log.i("IOTCamera", "IOTC_Session_Check(" + mSID + ") Failed return " + ret);

                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveSessionInfo(Camera.this, CONNECTION_STATE_CONNECT_FAILED);
                        }
                    }
                }
                synchronized (m_waitObjForCheckDevStatus) {
                    try {
                        m_waitObjForCheckDevStatus.wait(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            Log.i("IOTCamera", "===ThreadCheckDevStatus exit===");
        }
    }

    class ThreadMediaCodecRecvVideo extends Thread {
        private static final String TAG = "ThreadMediaCodecRecvVideo";
        private static final int MAX_BUF_SIZE = 1048576;//1280 * 720 * 3
        static final int MAX_FRAMEBUF = 1920 * 1080 * 2;
        private boolean bIsRunning = false;
        private AVChannel mAVChannel;
        AVFrame avFrame = null;

        public ThreadMediaCodecRecvVideo(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            bIsRunning = false;
        }

        @Override
        public void run() {
            System.gc();
            bIsRunning = true;
            while (bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0)) {
                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mAVChannel.VideoBPS = 0;
            byte[] buf = new byte[MAX_BUF_SIZE];
            byte[] pFrmInfoBuf = new byte[AVFrame.FRAMEINFO_SIZE];
            int[] pFrmNo = new int[1];
            int nCodecId = 0;
            int nReadSize = 0;
            int nFrmCount = 0;
            int nIncompleteFrmCount = 0;
            int nOnlineNumber = 0;
            long nPrevFrmNo = 0x0FFFFFFF;
            long lastTimeStamp = System.currentTimeMillis();
            long lastReceiveFrame = System.currentTimeMillis();
            int[] outBufSize = new int[1];
            int[] outFrmSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            byte nNetPoorNumber = 5; //add by guofeng  用来记录已经发送网速差的次数。
            // create by guofeng    不希望定义两个全部变量 占用内存，要注意内存不能泄漏 特别是大规模使用的时候。
            ByteBuffer mRecordFrame;
            boolean mStartRecording = false;
            //不知怎么用，或者他的作用是什么就先屏蔽了的。
            //int avFrameSize = 0;
            mAVChannel.VideoFPS = 0;
            Options options;
            options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = Camera.computeSampleSize(options, -1, 0xc8000);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inDither = false;
            options.inPurgeable = true;
            options.inTempStorage = new byte[16384];
            if (bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, Packet.intToByteArray_Little(mCamIndex));
            }
            mAVChannel.AudioFrameQueue.removeAll();
            AVAPIs.avClientSetMaxBufSize(2048);
            while (bIsRunning) {
                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                    if (System.currentTimeMillis() - lastTimeStamp > 1000) {
                        lastTimeStamp = System.currentTimeMillis();
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveFrameInfo(Camera.this, mAVChannel.getChannel(), (mAVChannel.AudioBPS + mAVChannel.VideoBPS) * 8 / 1024, mAVChannel.VideoFPS, nOnlineNumber, nFrmCount, nIncompleteFrmCount);
                        }
                        mAVChannel.VideoFPS = mAVChannel.VideoBPS = mAVChannel.AudioBPS = 0;
                    }
                    nReadSize = AVAPIs.avRecvFrameData2(mAVChannel.getAVIndex(), buf, buf.length, outBufSize, outFrmSize, pFrmInfoBuf, pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);
                    if (nReadSize >= 0) {
                        mAVChannel.VideoBPS += outBufSize[0];
                        nFrmCount++;
                        lastReceiveFrame = System.currentTimeMillis();
                        nCodecId = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            for (int i = 0; i < mIOTCListeners.size(); i++) {
                                IRegisterIOTCListener listener = mIOTCListeners.get(i);
                                listener.receiveFrameDataForMediaCodec(Camera.this, mAVChannel.getChannel(), buf, nReadSize, pFrmNo[0], pFrmInfoBuf, AVFrame.parseIfIFrame(pFrmInfoBuf), AVFrame.parseCodecId(pFrmInfoBuf));
                            }
                            // 首先打开总开关 也就是开始录像 mIsRecording 为true， 然后保存 有可能会出现 在把数据教给VideoRecord
                            // 后 无法播放那个了。
                            if (mIsRecording) {
                                //长度有可能会出错
                                mRecordFrame = ByteBuffer.allocateDirect(nReadSize);
                                mRecordFrame.put(buf, 0x0, nReadSize);

                                AVFrame mFrameRecord = new AVFrame((long) pFrmNo[0], (byte) 0,
                                        pFrmInfoBuf, mRecordFrame.array(), nReadSize);


                                if (mFrameRecord.isIFrame()) {
                                    mStartRecording = true;
                                }

                                if (mStartRecording) {
                                    // Log.i("IOTCamera",
                                    // "------------------VideoRecordQueue.addLast(mFrame)==!"+System.currentTimeMillis());
                                    mAVChannel.VideoFrameQueue.addLast(mFrameRecord);

                                }

                            }


                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MPEG4) {
                            Log.e("IOTCamera", "------nCodecId+AVFrame.MEDIA_CODEC_VIDEO_MPEG4");
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MJPEG) {
                            Log.e("IOTCamera", "------nCodecId+AVFrame.MEDIA_CODEC_VIDEO_MJPEG");
                        }
                    } else if (nReadSize == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                        Log.i("IOTCamera", "AV_ER_SESSION_CLOSE_BY_REMOTE");
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                        Log.i("IOTCamera", "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_DATA_NOREADY) {
//                        if (System.currentTimeMillis() - lastReceiveFrame > 1000) {
//                            lastReceiveFrame = System.currentTimeMillis();
//                            if (nNetPoorNumber == 5) {
//                                for (int i = 0; i < mIOTCListeners.size(); i++) {
//                                    IRegisterIOTCListener listener = mIOTCListeners.get(i);
//                                    listener.receiveSessionInfo(Camera.this, Camera.CONNECTION_STATE_IOTC_NETWORK_IS_POOR);
//                                }
//                            }
//                            nNetPoorNumber--;
//                            if (nNetPoorNumber == 0) {
//                                nNetPoorNumber = 5;    //逻辑不应该在这里写 但好像 又是不在这里写不行。
//                            }
//                            Log.e("IOTCamera", "-----------------System.currentTimeMillis() - lastReceiveFrame > 1000");
//                        }
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_BUFPARA_MAXSIZE_INSUFF) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_MEM_INSUFF) {
                        nFrmCount++;
                        nIncompleteFrmCount++;
                        Log.i("IOTCamera", "AV_ER_MEM_INSUFF");
                    } else if (nReadSize == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                        Log.i("IOTCamera", "AV_ER_LOSED_THIS_FRAME");
                        nFrmCount++;
                        nIncompleteFrmCount++;
                    } else if (nReadSize == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                        nFrmCount++;
                        mAVChannel.VideoBPS += outBufSize[0];
                        if (outFrmInfoBufSize[0] == 0 || (outFrmSize[0] * 0.9) > outBufSize[0] || (int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME) {
                            nIncompleteFrmCount++;
                            Log.i("IOTCamera", ((int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME ? "P" : "I") + " frame, outFrmSize(" + outFrmSize[0] + ") * 0.9 = " + ((outFrmSize[0] * 0.9)) + " > outBufSize(" + outBufSize[0] + ")");
                            continue;
                        }
                        byte[] frameData = new byte[outFrmSize[0]];
                        System.arraycopy(buf, 0, frameData, 0, outFrmSize[0]);
                        nCodecId = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MJPEG) {
                            nIncompleteFrmCount++;
                            continue;
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MPEG4) {
                            AVFrame frame = new AVFrame(pFrmNo[0], AVFrame.FRM_STATE_COMPLETE, pFrmInfoBuf, frameData, outFrmSize[0]);

                            if (frame.isIFrame() || pFrmNo[0] == (nPrevFrmNo + 1)) {
                                nPrevFrmNo = pFrmNo[0];
                                mAVChannel.VideoFrameQueue.addLast(frame);
                                Log.i("IOTCamera", "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4");
                            } else {
                                nIncompleteFrmCount++;
                                Log.i("IOTCamera", "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4 - LOST");
                            }
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            for (int i = 0; i < mIOTCListeners.size(); i++) {

                                IRegisterIOTCListener listener = mIOTCListeners.get(i);
                                listener.receiveFrameDataForMediaCodec(Camera.this, mAVChannel.getChannel(), buf, nReadSize, pFrmNo[0], pFrmInfoBuf, AVFrame.parseIfIFrame(pFrmInfoBuf), AVFrame.parseCodecId(pFrmInfoBuf));

                            }
                        } else {
                            nIncompleteFrmCount++;
                        }
                    }
                }

            }// while--end
            mAVChannel.VideoFrameQueue.removeAll();
            if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_STOP, Packet.intToByteArray_Little(mCamIndex));
                //AVAPIs.avClientCleanBuf(mAVChannel.getAVIndex());
            }
            if (buf != null) {
                buf = null;
            }
            Log.e("IOTCamera", "===ThreadMediaRecvVideo exit===");
        }
    }

    public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
        if (initialSize <= 0x8) {
            int roundedSize = 0x1;
            if (roundedSize >= initialSize) {
                return roundedSize;
            }
            roundedSize = roundedSize << 0x1;
        }
        int roundedSize = ((initialSize + 0x7) / 0x8) * 0x8;
        return roundedSize;
    }

    private static int computeInitialSampleSize(Options options, int minSideLength, int maxNumOfPixels) {
        double w = (double) 1080;
        double h = (double) 1920;
        double lowerBoundDouble = Math.ceil((double) Math.sqrt(((w * h) / (double) maxNumOfPixels)));
        double upperBoundDouble = Math.min((double) Math.floor((w / (double) minSideLength)), (double) Math.floor((w / (double) minSideLength)));
        int lowerBound = (int) lowerBoundDouble;
        int upperBound = (int) upperBoundDouble;
        if (upperBound < lowerBound) {
            return lowerBound;
        }
        if ((maxNumOfPixels == -0x1) && (minSideLength == -0x1)) {
            return 0x1;
        }
        if (minSideLength != -0x1) {
            return upperBound;
        }
        return lowerBound;
    }

    private class ThreadRecvAudio extends Thread {

        private int nReadSize = 0;
        private boolean bIsRunning = false;
//        NS localNS;
        private AVChannel mAVChannel;


        private boolean isRecordListen;
//		int avIndexForReceiveAudio = -1; 
//		int chIndexForReceiveAudio = -1;

        public ThreadRecvAudio(AVChannel channel, boolean paramBoolean) {
            mAVChannel = channel;
            isRecordListen = paramBoolean;
        }

        public void stopThread() {

//			AVAPIs.avServExit(mSID, chIndexForReceiveAudio);

            bIsRunning = false;
        }

        @Override
        public void run() {

            bIsRunning = true;


            while (bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0)) {

                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            mAVChannel.AudioBPS = 0;
            byte[] recvBuf = new byte[2000];
            byte[] bytAVFrame = new byte[AVFrame.FRAMEINFO_SIZE];


            int[] pFrmNo = new int[1];


            int nFPS = 0;
/*
            //add by guofeng用来记录到时间够五次了。
            int i = 0;
            ByteBuffer byteAudioBuffer;
            byte[] audioAACDate = new byte[1024];
            int[] aac_decode_num = new int[1];
*/

            if (bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0)
                mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTART, Packet.intToByteArray_Little(mCamIndex));
            runAudioTrack = bIsRunning;


            while (bIsRunning) {

                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {


                    nReadSize = AVAPIs.avRecvAudioData(mAVChannel.getAVIndex(), recvBuf, recvBuf.length * 10, bytAVFrame, AVFrame.FRAMEINFO_SIZE, pFrmNo);


                    if (nReadSize > 0) {

                        //现在用不到这句话，当显示码流的的时候可能会用到的。
                        //mAVChannel.AudioBPS += nReadSize;


//						mLastFrame = ByteBuffer.allocateDirect(nReadSize);
//						mLastFrame.put(recvBuf, 0x0, nReadSize);

                        //Log.i("IOTCamera", "--------vavRecvAudioData"+nReadSize);

                        byte[] frameData = new byte[nReadSize];
                        System.arraycopy(recvBuf, 0, frameData, 0, nReadSize);
                        AVFrame frame = new AVFrame(pFrmNo[0], AVFrame.FRM_STATE_COMPLETE, bytAVFrame, frameData, nReadSize);
                        mAVChannel.AudioFrameQueue.addLast(frame);


                    } else if (nReadSize == AVAPIs.AV_ER_DATA_NOREADY) {
                        //Log.i("IOTCamera", "avRecvAudioData returns AV_ER_DATA_NOREADY");
                        try {
                            Thread.sleep(nFPS == 0 ? 20 : (1000 / nFPS));

                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    } else if (nReadSize == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                        Log.i("IOTCamera", "avRecvAudioData returns AV_ER_LOSED_THIS_FRAME");
                    } else {
                        try {
                            Thread.sleep(nFPS == 0 ? 20 : (1000 / nFPS));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.i("IOTCamera", "avRecvAudioDataGF returns " + nReadSize);
                    }

                }

            }


            mAVChannel.IOCtrlQueue.Enqueue(mAVChannel.getAVIndex(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_AUDIOSTOP, Packet.intToByteArray_Little(mCamIndex));

            Log.i("IOTCamera", "===ThreadRecvAudio exit===");
        }
    }

    private class ThreadDecodeAudio extends Thread {

        private boolean mStopedDecodeAudio = false;

        private AVChannel mAVChannel;

        public ThreadDecodeAudio(AVChannel channel) {

            mAVChannel = channel;

        }

        public void stopThread() {

            mStopedDecodeAudio = false;

        }

        @Override
        public void run() {

            boolean bFirst = true;
            boolean bInitAudio = false;

            boolean bIsPlay = false;
            int bIsPlayNumber = 0;

            int nCodecId = -1;
            int nSamplerate = -1;
            int nDatabits = -1;
            int nChannel = -1;

            int nFPS = 0;

            NS localNS = new NS();
            byte[] recvBuf = new byte[2000];
            // 这三个变量 应该是针对声音做的某些处理，我却是 忘记了，也不知怎么用，不好的。
            // long firstTimeStampFromDevice = 0;
            // long firstTimeStampFromLocal = 0;
            // long sleepTime = 0;

            mStopedDecodeAudio = true;

            while (mStopedDecodeAudio) {

                if (mAVChannel.AudioFrameQueue.getCount() > 60) {

                    bIsPlay = true;
                    //Log.e("IOTCamera", "------bIsPlay = true;");
                }

                if (bIsPlay && mAVChannel.AudioFrameQueue.getCount() > 0) {


                    AVFrame frame = mAVChannel.AudioFrameQueue.removeHead();
                    bIsPlayNumber++;
                    if (bIsPlayNumber == 60) {
                        bIsPlayNumber = 0;
                        bIsPlay = false;
                    }
                    nCodecId = frame.getCodecId();

                    if (bFirst) {

                        if (!mInitAudio && (nCodecId == AVFrame.MEDIA_CODEC_AUDIO_MP3 || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_SPEEX
                                || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_ADPCM || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_PCM
                                || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_G726 || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_G711A
                                || nCodecId == AVFrame.MEDIA_CODEC_AUDIO_G711U)) {

                            bFirst = false;

                            nSamplerate = AVFrame.getSamplerate(frame.getFlags());
                            nDatabits = (int) (frame.getFlags() & 0x02);
                            nDatabits = (nDatabits == 0x02) ? 1 : 0;
                            nChannel = (int) (frame.getFlags() & 0x01);
                            Log.i("IOTCamera", "--------------nCodeId" + nCodecId);
                            bInitAudio = audioDev_init(nSamplerate, nChannel, nDatabits, nCodecId);

                            if (!bInitAudio)
                                break;
                        }
                    }
                    if (nCodecId == AVFrame.MEDIA_CODEC_AUDIO_PCM) {
                        mAudioTrack.write(frame.frmData, 0, frame.getFrmSize());
                        nFPS = ((nSamplerate * (nChannel == AVFrame.AUDIO_CHANNEL_MONO ? 1 : 2)
                                * (nDatabits == AVFrame.AUDIO_DATABITS_8 ? 8 : 16)) / 8) / frame.getFrmSize();
                        //Log.i("IOTCamera", "------"+nFPS);
                    } else if (nCodecId == AVFrame.MEDIA_CODEC_AUDIO_G711U) {//137
                        // Log.i("IOTCamera", "--------------MEDIA_CODEC_AUDIO_G711UnReadSize"+nReadSize);
                        // Log.e("IOTCamera", "--------------G711需要打开这个地方下面两句话"+nReadSize);
                        //Log.i("IOTCamera", "--------vavRecvAudioData"+nReadSize);
                        int i3 = DecG726.g711_decode(recvBuf, frame.frmData, frame.getFrmSize(), 1);

                        Camera.TypeTransform localTypeTransform = new Camera.TypeTransform();
                        if (!localNS.isInit()) {
                            Log.d("Camera", "WebRtc created : " + localNS.Create(nSamplerate));
                        }

                        short[] arrayOfShort2 = localTypeTransform.byteArray2shortArray(recvBuf, i3);
                        short[] arrayOfShort3 = new short[arrayOfShort2.length];

                        localNS.run(arrayOfShort2, arrayOfShort3, 8.0F);

                        if (mAec != null) {
                            mAec.Capture(arrayOfShort3);
                        }

                        mAudioTrack.write(recvBuf, 0, i3);
                        nFPS = ((nSamplerate * (nChannel == AVFrame.AUDIO_CHANNEL_MONO ? 1 : 2)
                                * (nDatabits == AVFrame.AUDIO_DATABITS_8 ? 8 : 16)) / 8) / 640;

                    }

                    try {
                        Thread.sleep(1000 / nFPS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } else {
                    try {
                        Thread.sleep(4);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (bInitAudio) {
                audioDev_stop(nCodecId);
            }

            Log.i("IOTCamera", "===ThreadDecodeAudio exit===");
        }
    }

    private class ThreadSendAudio extends Thread {
        private boolean m_bIsRunning = false;
        private static final int SAMPLE_RATE_IN_HZ = 16000;// syf 16hz
        private int avIndexForSendAudio = -1;
        private int chIndexForSendAudio = -1;
        private AVChannel mAVChannel = null;

        public ThreadSendAudio(AVChannel ch) {
            mAVChannel = ch;
        }

        public void stopThread() {
            if (mSID >= 0 && chIndexForSendAudio >= 0) {
                AVAPIs.avServExit(mSID, chIndexForSendAudio);
                sendIOCtrl(mAVChannel.mChannel, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTOP, SMsgAVIoctrlAVStream.parseContent(chIndexForSendAudio));
            }
            m_bIsRunning = false;
        }

        @Override
        public void run() {
            super.run();
            if (mSID < 0) {
                Log.i("IOTCamera", "=== ThreadSendAudio exit because SID < 0 ===");
                return;
            }
            m_bIsRunning = true;
            boolean bInitPCM = false;
            int nMinBufSize = 0;
            int nReadBytes = 0;
            /* wait for connection */
            chIndexForSendAudio = IOTCAPIs.IOTC_Session_Get_Free_Channel(mSID);

            if (chIndexForSendAudio < 0) {
                Log.i("IOTCamera", "=== ThreadSendAudio exit becuase no more channel for connection ===");
                return;
            }
            Log.e("IOTCamera", "mAVChannel.mChannel" + mAVChannel.mChannel + "AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTART" + AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTART);

            sendIOCtrl(mAVChannel.mChannel, AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SPEAKERSTART, SMsgAVIoctrlAVStream.parseContent(chIndexForSendAudio));

            Log.e("IOTCamera", "start avServerStart(" + mSID + ", " + chIndexForSendAudio + ")");

            while (m_bIsRunning && (avIndexForSendAudio = AVAPIs.avServStart(mSID, null, null, 10, 0, chIndexForSendAudio)) < 0) {
                Log.i("IOTCamera", "avServerStart(" + mSID + ", " + chIndexForSendAudio + ") : " + avIndexForSendAudio);
            }

            Log.i("IOTCamera", "avServerStart(" + mSID + ", " + chIndexForSendAudio + ") : " + avIndexForSendAudio);
            if (m_bIsRunning && mAVChannel.getAudioCodec() == AVFrame.MEDIA_CODEC_AUDIO_PCM) {// 修改syf 配合 1.6设备 MEDIA_CODEC_AUDIO_SPEEX
                bInitPCM = true;
                nMinBufSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
            }
			/* init mic of phone */
            AudioRecord recorder = null;
            if (m_bIsRunning && bInitPCM) {
                recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_IN_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, nMinBufSize);
                recorder.startRecording();
            }
            byte[] inPCMBuf = new byte[500];
            NS speakNS = new NS();
			/* send audio data continuously */
            while (m_bIsRunning) {
                // read speaker data
                speakNS.release();
                if (mAVChannel.getAudioCodec() == AVFrame.MEDIA_CODEC_AUDIO_PCM) {// 修改syf 配合 1.6设备 MEDIA_CODEC_AUDIO_SPEEX

                    nReadBytes = recorder.read(inPCMBuf, 0, inPCMBuf.length);

                    if (nReadBytes > 0) {
                        byte flag = (AVFrame.AUDIO_SAMPLE_16K << 2) | (AVFrame.AUDIO_DATABITS_16 << 1) ;
                        byte[] frameInfo = AVIOCTRLDEFs.SFrameInfo.parseContent((short) AVFrame.MEDIA_CODEC_AUDIO_PCM, flag, (byte) 0, (byte) 0, (int) System.currentTimeMillis());

                        AVAPIs.avSendAudioData(avIndexForSendAudio, inPCMBuf, nReadBytes, frameInfo, 16);
                    }
                }
            }
 			/* uninit speaker of phone */
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
            }
 
 			/* close connection */
            if (avIndexForSendAudio >= 0) {
                AVAPIs.avServStop(avIndexForSendAudio);
            }

            if (chIndexForSendAudio >= 0) {
                IOTCAPIs.IOTC_Session_Channel_OFF(mSID, chIndexForSendAudio);
            }

            avIndexForSendAudio = -1;
            chIndexForSendAudio = -1;

            Log.i("IOTCamera", "===ThreadSendAudio exit===");
        }
    }

    private class ThreadRecordingMovie extends Thread {
        private final static String TAG = "ThreadRecordingMovie";
        private String fileName;
        private boolean m_bIsRunning = false;
        private boolean isLOLLIPOP = false;
        //是否是I帧
        private boolean isIFrame = true;
        // 是否是第一次获取声音
        //是否是 高分辨率  isHighFix = true 代表 1080P； isHighFix =false 为720P分辨率
        private boolean isHighFix = false;
        private AVChannel mAVChannel;

        public ThreadRecordingMovie(AVChannel channel, String name, boolean flag) {
            // TODO Auto-generated constructor stub
            fileName = name;
            isHighFix = flag;
            mAVChannel = channel;
        }

        public void stopThread() {
            m_bIsRunning = false;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            // 不变码流 用不到 ，哪里用到就在哪里设置。
            AVFrame avRecordFrame;
            AVFrame avAudioFrame;
            mIsRecording = true;
            m_bIsRunning = true;
            // 用于编码 PCM数据格式。这样写比较乱 ，建议专门建一个线程用于 编码。AAc编码初始化
            AacEncoder.AACEncoderOpen(16000, 1);
            while (m_bIsRunning) {
                if (mAVChannel.AudioRecordFrameQueue.getCount() > 0 && !isIFrame) {
                    //这个地方 有可能出错，两个用一个都。
                    //Log.i("IOTCamera", "--------------mp4packAudio");
                    //avAudioFrame = mAVChannel.AudioRecordFrameQueue.removeHead();
                    //MuxerMp4.mp4packAudio(avAudioFrame.frmData, avAudioFrame.frmData.length);
                }
                if (mAVChannel.VideoFrameQueue.getCount() > 0) {
                    Log.i(TAG, "--------------mp4packVideo");
                    avRecordFrame = mAVChannel.VideoFrameQueue.removeHead();
                    if (isIFrame) {
                        isIFrame = false;
                        Log.e(TAG, "-----------" + MuxerMp4.stringFromJNI());
                        MuxerMp4.SeparateIFrame_GetSpsPpsSeiLen(avRecordFrame.frmData);
                        byte[] sps = MuxerMp4.getSps();
                        byte[] pps = MuxerMp4.getPps();
                        int sps_num = MuxerMp4.getSps_number();
                        int pps_num = MuxerMp4.getPps_number();
                        if ((pps != null) && (sps != null)) {
                            Log.i(TAG, "----------------" + fileName);
                            MuxerMp4.mp4init(fileName, 0, sps, sps_num, pps, pps_num, isHighFix);
                            // 这样写不合适，不能把值写成固定的 ，16K 单通道。
                            AacEncoder.AACEncoderOpen(16000, 1);
                        }
                        if (Build.VERSION.SDK_INT >= 21) {
                            isLOLLIPOP = true;
                        }
                        Log.e(TAG, "-----------------isLOLLIPOP = " + isLOLLIPOP + "isHighFix" + isHighFix);
                    }
                    MuxerMp4.mp4packVideo(avRecordFrame.frmData, avRecordFrame.frmData.length,
                            avRecordFrame.isIFrame() ? 1 : 0, isLOLLIPOP);
                    //Log.i("IOTCamera", "-----------------isLOLLIPOP = "+avRecordFrame.isIFrame());
                }
                //Log.i("IOTCamera", "--------------end");

            }
            mIsRecording = false;
            MuxerMp4.mp4close();
            MuxerMp4.setSps_byte(null);
            MuxerMp4.setPps_byte(null);
            //add by guofeng  AAc编码初始化
            AacEncoder.AACEncoderClose();
        }
    }

    private class ThreadSendIOCtrl extends Thread {
        private boolean bIsRunning = false;
        private AVChannel mAVChannel;

        public ThreadSendIOCtrl(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {

            bIsRunning = false;

            if (mAVChannel.getAVIndex() >= 0) {
                Log.i("IOTCamera", "avSendIOCtrlExit(" + mAVChannel.getAVIndex() + ")");
                AVAPIs.avSendIOCtrlExit(mAVChannel.getAVIndex());
            }
        }

        @Override
        public void run() {

            bIsRunning = true;

            while (bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0)) {
                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                int nDelayTime_ms = 0;
                AVAPIs.avSendIOCtrl(mAVChannel.getAVIndex(), AVAPIs.IOTYPE_INNER_SND_DATA_DELAY, Packet.intToByteArray_Little(nDelayTime_ms), 4);
                Log.i("IOTCamera", "avSendIOCtrl(" + mAVChannel.getAVIndex() + ", 0x" + Integer.toHexString(AVAPIs.IOTYPE_INNER_SND_DATA_DELAY) + ", " + getHex(Packet.intToByteArray_Little(nDelayTime_ms), 4) + ")");
            }

            while (bIsRunning) {
                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0 && !mAVChannel.IOCtrlQueue.isEmpty()) {
                    IOCtrlQueue.IOCtrlSet data = mAVChannel.IOCtrlQueue.Dequeue();
                    if (bIsRunning && data != null) {
                        int ret = AVAPIs.avSendIOCtrl(mAVChannel.getAVIndex(), data.IOCtrlType, data.IOCtrlBuf, data.IOCtrlBuf.length);
                        if (ret >= 0) {
                            Log.i("IOTCamera", "avSendIOCtrl(" + mAVChannel.getAVIndex() + ", 0x" + Integer.toHexString(data.IOCtrlType) + ", " + getHex(data.IOCtrlBuf, data.IOCtrlBuf.length) + ")");
                        } else {
                            Log.i("IOTCamera", "avSendIOCtrl failed : " + ret);
                        }
//						if (data.IOCtrlType == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_PTZ_COMMAND) {
//							
//							// 只有在类型为pTZ 时启动 线程。
//							isSendRotateCorrect();
//						}
                    }
                } else {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            Log.i("IOTCamera", "===ThreadSendIOCtrl exit===");
        }
    }

    private class ThreadRecvIOCtrl extends Thread {
        private final int TIME_OUT = 0;
        private boolean bIsRunning = false;
        private AVChannel mAVChannel;

        public ThreadRecvIOCtrl(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            bIsRunning = false;
        }

        @Override
        public void run() {
            bIsRunning = true;
            while (bIsRunning && (mSID < 0 || mAVChannel.getAVIndex() < 0)) {
                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(1000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            int idx = 0;
            while (bIsRunning) {
                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                    int[] ioCtrlType = new int[1];
                    byte[] ioCtrlBuf = new byte[1024];
                    int nRet = AVAPIs.avRecvIOCtrl(mAVChannel.getAVIndex(), ioCtrlType, ioCtrlBuf, ioCtrlBuf.length, TIME_OUT);
                    if (nRet >= 0) {
                        Log.i("IOTCamera", "avRecvIOCtrl(" + mAVChannel.getAVIndex() + ", 0x" + Integer.toHexString(ioCtrlType[0]) + ", " + getHex(ioCtrlBuf, nRet) + ")");
                        if (ioCtrlType[0] == 8196) {
                            if (versionlistener != null) {
                                versionlistener.getVersion(getHex(ioCtrlBuf, nRet).trim());
                            }
                        }
                        byte[] data = new byte[nRet];
                        System.arraycopy(ioCtrlBuf, 0, data, 0, nRet);
                        if (ioCtrlType[0] == AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GETAUDIOOUTFORMAT_RESP) {
                            int channel = Packet.byteArrayToInt_Little(data, 0);
                            int format = Packet.byteArrayToInt_Little(data, 4);
                            for (AVChannel ch : mAVChannels) {
                                if (ch.getChannel() == channel) {
                                    ch.setAudioCodec(format);
                                    break;
                                }
                            }
                        }
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveIOCtrlData(Camera.this, mAVChannel.getChannel(), ioCtrlType[0], data);
                        }
                    } else {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Log.i("IOTCamera", "===ThreadRecvIOCtrl exit===");
        }
    }

    public class AVChannel {
        private volatile int mChannel = -1;
        private volatile int mAVIndex = -1;
        private long mServiceType = 0xFFFFFFFF;
        private String mViewAcc;
        private String mViewPwd;
        private int mAudioCodec;

        public IOCtrlQueue IOCtrlQueue;
        public AVFrameQueue VideoFrameQueue;
        public AVFrameQueue AudioFrameQueue;
        public AVFrameQueue AudioRecordFrameQueue;

        public Bitmap LastFrame;

        public int VideoFPS;
        public int VideoBPS;
        public int AudioBPS;
        public Camera.ThreadMediaCodecRecvHistoryVideo threadMediaCodecPlayBackRecvVideo = null;

        public AVChannel() {
        }

        public AVChannel(int channel, String view_acc, String view_pwd) {
            mChannel = channel;
            mViewAcc = view_acc;
            mViewPwd = view_pwd;
            mServiceType = 0xFFFFFFFF;

            VideoFPS = VideoBPS = AudioBPS = 0;

            LastFrame = null;

            IOCtrlQueue = new IOCtrlQueue();
            VideoFrameQueue = new AVFrameQueue();
            AudioFrameQueue = new AVFrameQueue();
            AudioRecordFrameQueue = new AVFrameQueue();

        }

        public void setmChannel(int mChannel) {
            this.mChannel = mChannel;
        }

        public int getChannel() {
            return mChannel;
        }

        public synchronized int getAVIndex() {
            return mAVIndex;
        }

        public synchronized void setAVIndex(int idx) {
            mAVIndex = idx;
        }

        public synchronized long getServiceType() {
            return mServiceType;
        }

        public synchronized int getAudioCodec() {
            return mAudioCodec;
        }

        public synchronized void setAudioCodec(int codec) {
            mAudioCodec = codec;
        }

        public synchronized void setServiceType(long serviceType) {
            mServiceType = serviceType;
            mAudioCodec = (serviceType & 4096) == 0 ? AVFrame.MEDIA_CODEC_AUDIO_SPEEX : AVFrame.MEDIA_CODEC_AUDIO_ADPCM;
        }

        public String getViewAcc() {
            return mViewAcc;
        }

        public String getViewPwd() {
            return mViewPwd;
        }

        public ThreadStartDev threadStartDev = null;
        public ThreadRecvIOCtrl threadRecvIOCtrl = null;
        public ThreadSendIOCtrl threadSendIOCtrl = null;
        public ThreadRecvAudio threadRecvAudio = null;
        public ThreadDecodeAudio threadDecAudio = null;
        public ThreadMediaCodecRecvVideo threadMediaCodecRecvVideo = null;
        private ThreadRecordingMovie mThreadRecordingMovie = null;
        private ThreadDecodeVideo mThreadDecodeVideo = null;
        private ThreadRecvVideo mThreadRecvVideo = null;
    }

    private class IOCtrlQueue {

        public class IOCtrlSet {

            public int IOCtrlType;
            public byte[] IOCtrlBuf;

            public IOCtrlSet(int avIndex, int type, byte[] buf) {
                IOCtrlType = type;
                IOCtrlBuf = buf;
            }

            public IOCtrlSet(int type, byte[] buf) {
                IOCtrlType = type;
                IOCtrlBuf = buf;
            }
        }

        LinkedList<IOCtrlSet> listData = new LinkedList<IOCtrlSet>();

        public synchronized boolean isEmpty() {
            return listData.isEmpty();
        }

        public synchronized void Enqueue(int type, byte[] data) {
            listData.addLast(new IOCtrlSet(type, data));
        }

        public synchronized void Enqueue(int avIndex, int type, byte[] data) {
            listData.addLast(new IOCtrlSet(avIndex, type, data));
        }

        public synchronized IOCtrlSet Dequeue() {

            return listData.isEmpty() ? null : listData.removeFirst();
        }

        public synchronized void removeAll() {
            if (!listData.isEmpty())
                listData.clear();
        }
    }

    private static final String HEXES = "0123456789ABCDEF";

    static String getHex(byte[] raw, int size) {

        if (raw == null) {
            return null;
        }

        final StringBuilder hex = new StringBuilder(2 * raw.length);

        int len = 0;

        for (final byte b : raw) {
            hex.append(HEXES.charAt((b & 0xF0) >> 4)).append(HEXES.charAt((b & 0x0F))).append(" ");

            if (++len >= size)
                break;
        }

        return hex.toString();
    }

    class TypeTransform {
        TypeTransform() {
        }

        public final short[] byteArray2shortArray(byte[] paramArrayOfByte, int paramInt) {
            short[] arrayOfShort = new short[paramInt / 2];
            for (int i = 0; ; ++i) {
                if (i >= paramInt / 2)
                    return arrayOfShort;
                arrayOfShort[i] = (short) (0xFF & paramArrayOfByte[(i * 2)] | (0xFF & paramArrayOfByte[(1 + i * 2)]) << 8);
            }
        }

        public byte[] shortArray2byteArray(short[] paramArrayOfShort, int paramInt) {
            byte[] arrayOfByte1 = new byte[paramInt * 2];
            for (int i = 0; ; ++i) {
                if (i >= paramInt)
                    return arrayOfByte1;
                byte[] arrayOfByte2 = new byte[2];
                arrayOfByte2[0] = (byte) paramArrayOfShort[i];
                arrayOfByte2[1] = (byte) (paramArrayOfShort[i] >>> 8);
                arrayOfByte1[(0 + i * 2)] = arrayOfByte2[0];
                arrayOfByte1[(1 + i * 2)] = arrayOfByte2[1];
            }
        }
    }

    public void addVersionListener(GetVersionFromCameraListener listener) {
        this.versionlistener = listener;
    }

    /**
     * 历史录像
     */
    public void startPlayBack(int avChannel, String viewAccount, String viewPasswd) {
//	        Camera.AVChannel sessionHistory = null;
//	        List ch = this.mAVChannels;
//	        synchronized(this.mAVChannels) {
//	            Iterator var7 = this.mAVChannels.iterator();
//
//	            while(var7.hasNext()) {
//	                Camera.AVChannel ch1 = (Camera.AVChannel)var7.next();
//	                if(ch1.getChannel() == avChannel) {
//	                    sessionHistory = ch1;
//	                    break;
//	                }
//	            }
//	        }
        AVChannel sessionHistory = null;

        synchronized (mAVChannels) {
            for (AVChannel ch2 : mAVChannels) {
                if (ch2.getChannel() == avChannel) {
                    sessionHistory = ch2;
                    break;
                }
            }
        }


        if (sessionHistory == null) {
            Camera.AVChannel ch2 = new Camera.AVChannel(avChannel, viewAccount, viewPasswd);
            this.mAVChannels.add(ch2);
            ch2.threadRecvIOCtrl = new ThreadRecvIOCtrl(ch2);
            ch2.threadRecvIOCtrl.start();

            ch2.threadSendIOCtrl = new ThreadSendIOCtrl(ch2);
            ch2.threadSendIOCtrl.start();
        }

    }

    public void startPlayBackShow(int avChannel, boolean avNoClearBuf, boolean runSoftwareDecode) {
//	        List var4 = this.mAVChannels;
        synchronized (this.mAVChannels) {
//	            for(int i = 0; i < this.mAVChannels.size(); i++) {
//	                Camera.AVChannel ch = (Camera.AVChannel)this.mAVChannels.get(i);
//	                if(ch.getChannel() == avChannel) {
//	                    ch.VideoFrameQueue.removeAll();
//	                    if(VERSION.SDK_INT < 16 || runSoftwareDecode) {
//	                        break;
//	                    }
//
//	                    if(ch.threadMediaCodecPlayBackRecvVideo == null) {
//	                        ch.threadMediaCodecPlayBackRecvVideo = new Camera.ThreadMediaCodecRecvHistoryVideo(ch);
//	                        ch.threadMediaCodecPlayBackRecvVideo.start();
//	                    }
//	                }
//	            }
            for (int i = 0; i < mAVChannels.size(); i++) {

                Log.i("IOTCamera", "----------mAVChannels.size()" + mAVChannels.size());
                AVChannel ch = mAVChannels.get(i);


                Log.i("IOTCamera", "----------ch.getChannel()" + ch.getChannel());
                if (ch.getChannel() == avChannel) {

                    ch.VideoFrameQueue.removeAll();

                    if (android.os.Build.VERSION.SDK_INT >= 16 && !runSoftwareDecode) {
                        if (ch.threadMediaCodecPlayBackRecvVideo == null) {
                            ch.threadMediaCodecPlayBackRecvVideo = new ThreadMediaCodecRecvHistoryVideo(ch);
                            ch.threadMediaCodecPlayBackRecvVideo.start();
                        }

                        continue; /* Loop/switch isn't completed */
                    }

                    break;
                }
            }


        }
    }

    private class ThreadMediaCodecRecvHistoryVideo extends Thread {
        private final static String TAG = "CodecRecv";
        private static final int MAX_BUF_SIZE = 1048576;// 1280 * 720 * 3
        static final int MAX_FRAMEBUF = 1920 * 1080 * 2;
        private boolean bIsRunning = false;
        private AVChannel mAVChannel;
        AVFrame avFrame = null;

        public ThreadMediaCodecRecvHistoryVideo(AVChannel channel) {
            mAVChannel = channel;
        }

        public void stopThread() {
            bIsRunning = false;
        }

        @Override
        public void run() {
            System.gc();
            bIsRunning = true;
            int[] nServType = new int[1];
            nServType[0] = -1;
            Log.e(TAG, "channel number(" + mAVChannel.getChannel() + ")");
            int avIndex = -1;
            int count = 100;
            if (mSID >= 0) {
                IOTCAPIs.IOTC_Session_Channel_ON(mSID, mAVChannel.getChannel());
            }
            while (mSID < 0 || avIndex < 0) {
                avIndex = AVAPIs.avClientStart2(mSID, mAVChannel.getViewAcc(), mAVChannel.getViewPwd(), 30, nServType, mAVChannel.getChannel(), bResendHistory);
                if (count == 0) {
                    Log.d(TAG, "===History Video time out===");
                    break;
                }
                count--;
                try {
                    synchronized (mWaitObjectForConnected) {
                        mWaitObjectForConnected.wait(100);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e(TAG, "AVAPIs.avClientStart2(" + avIndex + ")");
            }
            mAVChannel.setAVIndex(avIndex);
            mAVChannel.VideoBPS = 0;
            byte[] buf = new byte[MAX_BUF_SIZE];
            byte[] pFrmInfoBuf = new byte[AVFrame.FRAMEINFO_SIZE];
            int[] pFrmNo = new int[1];
            int nCodecId = 0;
            int nReadSize = 0;
            int nFrmCount = 0;
            int nIncompleteFrmCount = 0;
            int nOnlineNumber = 0;
            long nPrevFrmNo = 0x0FFFFFFF;
            long lastTimeStamp = System.currentTimeMillis();
            long lastReceiveFrame = System.currentTimeMillis();
            int[] outBufSize = new int[1];
            int[] outFrmSize = new int[1];
            int[] outFrmInfoBufSize = new int[1];
            // 不知怎么用，或者他的作用是什么就先屏蔽了的。
            // int avFrameSize = 0;
            mAVChannel.VideoFPS = 0;
            Options options;
            options = new android.graphics.BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inSampleSize = Camera.computeSampleSize(options, -1, 0xc8000);
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inInputShareable = true;
            options.inDither = false;
            options.inPurgeable = true;
            options.inTempStorage = new byte[16384];
            mAVChannel.AudioFrameQueue.removeAll();
            if (bIsRunning && mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                sendIOCtrl(mAVChannel.getChannel(), AVIOCTRLDEFs.IOTYPE_USER_IPCAM_START, Packet.intToByteArray_Little(mAVChannel.getAVIndex()));
            }
            while (bIsRunning) {
                if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                    if (System.currentTimeMillis() - lastTimeStamp > 1000) {
                        lastTimeStamp = System.currentTimeMillis();
                        for (int i = 0; i < mIOTCListeners.size(); i++) {
                            IRegisterIOTCListener listener = mIOTCListeners.get(i);
                            listener.receiveFrameInfo(Camera.this, mAVChannel.getChannel(),
                                    (mAVChannel.AudioBPS + mAVChannel.VideoBPS) * 8 / 1024, mAVChannel.VideoFPS,
                                    nOnlineNumber, nFrmCount, nIncompleteFrmCount);
                        }
                        mAVChannel.VideoFPS = mAVChannel.VideoBPS = mAVChannel.AudioBPS = 0;
                    }
                    nReadSize = AVAPIs.avRecvFrameData2(1, buf, buf.length, outBufSize, outFrmSize, pFrmInfoBuf, pFrmInfoBuf.length, outFrmInfoBufSize, pFrmNo);
                    if (nReadSize >= 0) {
                        mAVChannel.VideoBPS += outBufSize[0];
                        nFrmCount++;
                        lastReceiveFrame = System.currentTimeMillis();
                        nCodecId = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            Log.i(TAG, "===MEDIA_CODEC_VIDEO_H264===");
                            for (int i = 0; i < mIOTCListeners.size(); i++) {
                                IRegisterIOTCListener listener = mIOTCListeners.get(i);
                                listener.receiveFrameDataForMediaCodec(Camera.this, mAVChannel.getChannel(), buf,
                                        nReadSize, pFrmNo[0], pFrmInfoBuf, AVFrame.parseIfIFrame(pFrmInfoBuf),
                                        AVFrame.parseCodecId(pFrmInfoBuf));
                            }
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MPEG4) {
                            Log.e(TAG, "------nCodecId+AVFrame.MEDIA_CODEC_VIDEO_MPEG4");
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MJPEG) {
                            Log.e(TAG, "------nCodecId+AVFrame.MEDIA_CODEC_VIDEO_MJPEG");
                        }
                    } else if (nReadSize == AVAPIs.AV_ER_SESSION_CLOSE_BY_REMOTE) {
                        Log.i(TAG, "AV_ER_SESSION_CLOSE_BY_REMOTE");
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_REMOTE_TIMEOUT_DISCONNECT) {
                        Log.i(TAG, "AV_ER_REMOTE_TIMEOUT_DISCONNECT");
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_DATA_NOREADY) {
                        if (System.currentTimeMillis() - lastReceiveFrame > 1000) {
                            lastReceiveFrame = System.currentTimeMillis();
                            Log.e(TAG, "-----------------System.currentTimeMillis() - lastReceiveFrame > 1000");
                        }
                        try {
                            Thread.sleep(32);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_BUFPARA_MAXSIZE_INSUFF) {
                        continue;
                    } else if (nReadSize == AVAPIs.AV_ER_MEM_INSUFF) {
                        nFrmCount++;
                        nIncompleteFrmCount++;
                        Log.i(TAG, "AV_ER_MEM_INSUFF");
                    } else if (nReadSize == AVAPIs.AV_ER_LOSED_THIS_FRAME) {
                        Log.i(TAG, "AV_ER_LOSED_THIS_FRAME");
                        nFrmCount++;
                        nIncompleteFrmCount++;
                    } else if (nReadSize == AVAPIs.AV_ER_INCOMPLETE_FRAME) {
                        nFrmCount++;
                        mAVChannel.VideoBPS += outBufSize[0];
                        if (outFrmInfoBufSize[0] == 0 || (outFrmSize[0] * 0.9) > outBufSize[0]
                                || (int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME) {
                            nIncompleteFrmCount++;
                            Log.i(TAG,
                                    "guo" + ((int) pFrmInfoBuf[2] == AVFrame.IPC_FRAME_FLAG_PBFRAME ? "P" : "I")
                                            + "---frame, outFrmSize(" + outFrmSize[0] + ") * 0.9 = "
                                            + ((outFrmSize[0] * 0.9)) + " > outBufSize(" + outBufSize[0] + ")");
                            continue;
                        }
                        byte[] frameData = new byte[outFrmSize[0]];
                        System.arraycopy(buf, 0, frameData, 0, outFrmSize[0]);
                        nCodecId = Packet.byteArrayToShort_Little(pFrmInfoBuf, 0);
                        if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MJPEG) {
                            nIncompleteFrmCount++;
                            continue;
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_MPEG4) {
                            AVFrame frame = new AVFrame(pFrmNo[0], AVFrame.FRM_STATE_COMPLETE, pFrmInfoBuf, frameData,
                                    outFrmSize[0]);
                            if (frame.isIFrame() || pFrmNo[0] == (nPrevFrmNo + 1)) {
                                nPrevFrmNo = pFrmNo[0];
                                mAVChannel.VideoFrameQueue.addLast(frame);
                                Log.i(TAG, "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4");
                            } else {
                                nIncompleteFrmCount++;
                                Log.i(TAG, "AV_ER_INCOMPLETE_FRAME - H264 or MPEG4 - LOST");
                            }
                        } else if (nCodecId == AVFrame.MEDIA_CODEC_VIDEO_H264) {
                            for (int i = 0; i < mIOTCListeners.size(); i++) {

                                IRegisterIOTCListener listener = mIOTCListeners.get(i);
                                listener.receiveFrameDataForMediaCodec(Camera.this, mAVChannel.getChannel(), buf,
                                        nReadSize, pFrmNo[0], pFrmInfoBuf, AVFrame.parseIfIFrame(pFrmInfoBuf),
                                        AVFrame.parseCodecId(pFrmInfoBuf));
                            }
                        } else {
                            nIncompleteFrmCount++;
                        }
                    }
                }
            } // while--end
            mAVChannel.VideoFrameQueue.removeAll();
            if (mSID >= 0 && mAVChannel.getAVIndex() >= 0) {
                //AVAPIs.avClientCleanBuf(mAVChannel.getAVIndex());
            }
            if (mSID >= 0) {
                int ret = IOTCAPIs.IOTC_Session_Channel_OFF(mSID, mAVChannel.getChannel());
                Log.e(TAG, "---------IOTC_Session_Channel_ON" + ret);
            }
            if (mAVChannel.getAVIndex() >= 0) {

                AVAPIs.avClientStop(mAVChannel.getAVIndex());
                AVAPIs.avClientExit(mSID, mAVChannel.getAVIndex());
                Log.e(TAG, "avClientStop(avIndex = " + mAVChannel.getAVIndex() + ")");
            }
            if (buf != null) {
                buf = null;
            }

            Log.e(TAG, "===ThreadMediaRecvVideo exit===");
        }
    }

    public void stopPlayBackShow(int avChannel) {
        List var2 = this.mAVChannels;
        synchronized (this.mAVChannels) {
            for (int i = 0; i < this.mAVChannels.size(); ++i) {
                Camera.AVChannel ch = (Camera.AVChannel) this.mAVChannels.get(i);
                if (ch.getChannel() == avChannel) {
                    if (VERSION.SDK_INT >= 16 && ch.threadMediaCodecPlayBackRecvVideo != null) {
                        ch.threadMediaCodecPlayBackRecvVideo.stopThread();

                        try {
                            ch.threadMediaCodecPlayBackRecvVideo.interrupt();
                            ch.threadMediaCodecPlayBackRecvVideo.join();
                        } catch (InterruptedException var6) {
                            var6.printStackTrace();
                        }

                        ch.threadMediaCodecPlayBackRecvVideo = null;
                    }

                    ch.VideoFrameQueue.removeAll();
                    break;
                }
            }

        }
    }

    public void stopPlayBack(int avChannel) {
        List var2 = this.mAVChannels;
        synchronized (this.mAVChannels) {
            int idx = -1;

            for (int i = 0; i < this.mAVChannels.size(); ++i) {
                Camera.AVChannel ch = (Camera.AVChannel) this.mAVChannels.get(i);
                if (ch.getChannel() == avChannel) {
                    idx = i;
                    ch.AudioFrameQueue.removeAll();
                    ch.AudioFrameQueue = null;
                    ch.VideoFrameQueue.removeAll();
                    ch.VideoFrameQueue = null;
                    if (ch.getAVIndex() >= 0) {
                        AVAPIs.avClientStop(ch.getAVIndex());
                        Log.i("IOTCamera", "avClientStop(avIndex = " + ch.getAVIndex() + ")");
                    }
                    break;
                }
            }
            if (idx >= 0) {
                this.mAVChannels.remove(idx);
            }
        }
    }
}
