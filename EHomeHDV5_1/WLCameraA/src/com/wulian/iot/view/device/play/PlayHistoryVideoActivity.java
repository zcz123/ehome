package com.wulian.iot.view.device.play;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.webrtc.videoengine.ViERenderer;

import android.util.Log;
import android.view.SurfaceHolder.Callback;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.AVIOCTRLDEFs.SMsgAVIoctrlSetHistoryStop;
import com.tutk.IOTC.Camera;
import com.tutk.IOTC.MediaCodecMonitor;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.bean.VideotapeInfo;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.DateUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.adapter.VideotapeHistoryAdapter;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.widght.DatePickerPopWindow;
import com.wulian.icam.R;
import com.wulian.icam.utils.Utils;
import com.wulian.iot.widght.DialogRealize;

public class PlayHistoryVideoActivity extends SimpleFragmentActivity implements
        OnItemClickListener, OnClickListener, OnSeekBarChangeListener,
        Callback, Handler.Callback {
    private ListView lvVideotape;
    private VideotapeHistoryAdapter videotapeHistoryAdapter = null;
    private ImageView ivCalendar;
    private LinearLayout linParent;
    private ImageView ivBack;
    private int currentPosition = 0;
    private MediaCodecMonitor mediaCodecVideoMonitor = null;
    private Boolean status = true;
    private MediaPlayer mediaPlayer;
    private SeekBar seek;
    private boolean PLAY = true;// 判断是否播放
    private int mSelectedHistoryChannel = -1;
    // data
    private List<VideotapeInfo> videos = null;
    private PlayServerVideo playServerVideo;
    private StopServerVideo stopServerVideo;
    // private RelativeLayout media_codec_monitorLayout;
    // 布局中最底部的高度
    private int textHeight;
    // 横屏载体
    private SurfaceView cameraPreview;
    // 横竖屏
    private int widthRatio = 16;
    private int heightRatio = 9;
    private int minWidth, maxWidth, beginWidth;
    // 根据类型判断
    private static final int FILLDATA_TO_GETLOCALFILE = 2;
    private static final int SUCCESS_TO_GETLOCALFILE = 3;
    private static final int FAIL_TO_GETDATA = 4;
    private TextView textViewNullView;
    private ImageView mImageView;
    // add by guofeng
    // 全屏按钮
    private ImageView videoFullScreen, startPauseButton; // videotape_history_fullimage
    private LinearLayout landSpaceHistoryLayout, verticalSpaceHistoryLayout; // landHistoryLinear;
    private String fileNameString;
    private SeekThread seekThread;
    private DatePickerPopWindow datePickerPopWindow = null;//add syf
    private IOTCameraBean iotCameraBean;
    //selectData 默认为当天时间，当日期选择后自动替换
    private String selectDate = DateUtil.getFormatIMGTime(System.currentTimeMillis()).substring(0, 10);
    //用来加载第一次查询当天的视频
    private String defaultDate = DateUtil.getFormatIMGTime(System.currentTimeMillis()).substring(0, 10);

    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
        }

        @Override
        public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (avIOCtrlMsgType) {
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_ACK:
                            Log.i(TAG, "----------IOTYPE_USER_IPCAM_SEARCH_PLAYBACK_FILE_ACK");
                            int total_num = DateUtil.bytesToInt(data, 0);
                            Log.i(TAG, "total_num(" + total_num + ")");
                            if (total_num > 0) {
                                if (lvVideotape.getVisibility() == View.GONE) {
                                    lvVideotape.setVisibility(View.VISIBLE);
                                    textViewNullView.setVisibility(View.GONE);
                                }
                                videos.addAll(findVideoDataByIoc(data));
                            } else if (total_num == -1) {//结束符添加数据
                                videotapeHistoryAdapter.swapData(videos);
                            } else if (total_num == 0) {//没有数据
                                Toast.makeText(PlayHistoryVideoActivity.this, getResources().getString(R.string.ioc_history_null), Toast.LENGTH_SHORT).show();
                                videos.clear();
                            }
                            break;
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_STREAM_ACK:// 接收视频数据
                            if (data.length > 0) {
                                release();// 如果本地视频在播放需要清空
                                if ((mSelectedHistoryChannel = data[0]) > 0) {
                                    if (PLAY) {
                                        startPaly();
                                    }
                                    mediaCodecVideoMonitor.setSurfaceReady();
                                }
                                mediaCodecVideoMonitor.cleanFrameQueue();// 清除播放缓冲队列
                            }
                            break;
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_ACK:// 停止回放
                            break;
                    }
                }
            });
        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {

        }
    };
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PlayHistoryVideoActivity.FILLDATA_TO_GETLOCALFILE:
                    dismissDialog();
                    lvVideotape.setVisibility(View.GONE);
                    textViewNullView.setVisibility(View.VISIBLE);
                    break;
                case PlayHistoryVideoActivity.SUCCESS_TO_GETLOCALFILE:
                    break;
                case PlayHistoryVideoActivity.FAIL_TO_GETDATA:
                    Toast.makeText(PlayHistoryVideoActivity.this, "请选择具体的时间", Toast.LENGTH_SHORT)
                            .show();
                    break;
            }
        }
    };

    @Override
    public void root() {

        setContentView(R.layout.activity_videotape_history);

    }

    @Override
    public void initView() {
        mediaCodecVideoMonitor = (MediaCodecMonitor) findViewById(R.id.sv);
        lvVideotape = (ListView) findViewById(R.id.lv_videotape_show_hisory);
        ivCalendar = (ImageView) findViewById(R.id.iv_videotape_calendar);
        linParent = (LinearLayout) findViewById(R.id.lin_videotape_history);
        ivBack = (ImageView) findViewById(R.id.iv_back_play);
        seek = (SeekBar) findViewById(R.id.seekBar);
        videotapeHistoryAdapter = new VideotapeHistoryAdapter(null,
                PlayHistoryVideoActivity.this);
        lvVideotape.setAdapter(videotapeHistoryAdapter);

        // add by guofeng
        videoFullScreen = (ImageView) findViewById(R.id.videotape_history_fullimage);
        videoFullScreen.setOnClickListener(this);
        videoFullScreen.setTag("vertical");

        // 上下布局各站二分之一。
        verticalSpaceHistoryLayout = (LinearLayout) findViewById(R.id.VerticaHistoryLinear);

        startPauseButton = (ImageView) findViewById(R.id.startPauseButton);
        startPauseButton.setOnClickListener(this);
        startPauseButton.setTag("play");

        textViewNullView = (TextView) findViewById(R.id.lv_videotape_show_text);
        textViewNullView.setVisibility(View.GONE);
        addSurfaseCallBack();

    }

    @Override
    public void initData() {
        videos = new ArrayList<VideotapeInfo>();
    }

    @Override
    public void initEvents() {
        lvVideotape.setOnItemClickListener(this);
        ivBack.setOnClickListener(this);
        ivCalendar.setOnClickListener(this);
        seek.setOnSeekBarChangeListener(this);
        attachVideoPreview();
    }

    @Override
    protected void onResume() {
        super.onResume();
        iotCameraBean = (IOTCameraBean) getIntent().getSerializableExtra(Config.deskBean);
        if (selectDate.equals(defaultDate)) {
            PlayHistoryVideoActivity.this.execute(iotCameraBean.getGwId());
        }
        if (cameaHelper != null) {
            cameaHelper.attach(observer);
        }
    }

    private final void execute(String gwId) {
        new NativeAsyncTask().execute(gwId);
    }

    private class NativeAsyncTask extends AsyncTask<String, Void, List<VideotapeInfo>> {
        @Override
        protected List<VideotapeInfo> doInBackground(String... params) {
            return IotUtil.getVieos(params[0]);
        }

        @Override
        protected void onPostExecute(List<VideotapeInfo> result) {//加载本地视频
            dismissDialog();
            if (result.size() > 0) {
                for (int i = 0; i < result.size(); i++) {
                    if (result.get(i).getFileName().startsWith(selectDate)) {
                        videos.add(result.get(i));
                    }
                }
                if (videotapeHistoryAdapter != null) {
                    videotapeHistoryAdapter.swapData(videos);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            initDiglog();
        }
    }

    /***************************
     * 命令
     *******************************/
    private static class SMsgAVIoctrl {
        /**
         * 停止历史录像
         */
        public static void sendIoctrlSetHistoryStop() {
            cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                    AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_OVER_REQ,
                    SMsgAVIoctrlSetHistoryStop.parseContent(0));
        }
    }

    private final List<VideotapeInfo> findVideoDataByIoc(byte[] data) {
        return IotUtil.getVieos(data, this);
    }

    /**
     * 停止播放
     */
    private final void stopPlayVideo() {
        cameaHelper.getmCamera().sendIOCtrl(Camera.DEFAULT_AV_CHANNEL,
                AVIOCTRLDEFs.IOTYPE_USER_IPCAM_GET_PLAYBACK_STOP_REQ,
                new byte[0]);
    }

    /*************************** 命令 *******************************/

    /***************************
     * 线程操作
     ******************************/
    private class PlayServerVideo extends Thread {
        @Override
        public void run() {
            if (cameaHelper.getmCamera() != null) {
                cameaHelper.destroyVideoStream();
                cameaHelper.getmCamera().startPlayBack(mSelectedHistoryChannel, "admin",
                        iotCameraBean.getPassword());
                cameaHelper.getmCamera().startPlayBackShow(mSelectedHistoryChannel, true, false);
            }
            if (mediaCodecVideoMonitor != null) {
                mediaCodecVideoMonitor.attachCamera(cameaHelper.getmCamera(), 0);
            }
            PLAY = false;
        }
    }

    private class StopServerVideo extends Thread {
        @Override
        public void run() {
            if (mSelectedHistoryChannel >= 0) {
                if (cameaHelper.getmCamera() != null) {
                    cameaHelper.getmCamera().stopPlayBack(mSelectedHistoryChannel);
                    cameaHelper.getmCamera().stopPlayBackShow(mSelectedHistoryChannel);
                }
            }
            SMsgAVIoctrl.sendIoctrlSetHistoryStop();
            mSelectedHistoryChannel = -1;
        }
    }

    /***************************
     * 线程操作
     *******************************/

    private final void startPaly() {
        playServerVideo = new PlayServerVideo();
        playServerVideo.setPriority(Thread.MAX_PRIORITY);
        playServerVideo.start();
    }

    private final void stopPlay() {
        stopServerVideo = new StopServerVideo();
        stopServerVideo.setPriority(Thread.MAX_PRIORITY);
        stopServerVideo.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
        cameaHelper.detach(observer);
        release();
        if (mediaCodecVideoMonitor != null) {
            cameaHelper.destroyVideoCarrier(mediaCodecVideoMonitor);
        }
    }

    // add syf
    private void addSurfaseCallBack() {
        mediaCodecVideoMonitor.getHolder().addCallback(this);
    }

    private final void playVideo(String filePath, final int msec) {
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            return;
        }
        Log.i("IOTCamera", "-------------playVideo");
        /** 摄像机 历史录像 和 本地历史录像播放方式不一样 这里需要判断 */
        try {
            if (mediaCodecVideoMonitor.isThreadRun()) {// 历史录像在运行中
                stopPlayVideo();// 停止播放
                Log.i("IOTCamera", "-------------stopPlayVideo");

                mediaCodecVideoMonitor.surfaceDestroyed();// 关闭线程
            }

            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();

                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDisplay(mediaCodecVideoMonitor.getHolder());
                mediaPlayer.setDataSource(file.getAbsolutePath());

                Log.i("IOTCamera", "-------------new MediaPlayer()");
            }

            Log.i("IOTCamera", "-------------new SeekThread()");

            if (seekThread == null) {

                seekThread = new SeekThread();
                Log.i("IOTCamera", "-------------new SeekThread()");
            }

            Log.i("IOTCamera", "-------------setDataSource");

            mediaPlayer.prepareAsync();

            if (seekThread == null) {
                // 线程建立 与开始必须放在一块，不然会出现异常不到的错误。
                seekThread = new SeekThread();
                seekThread.start();
            }

            mediaPlayer.setOnPreparedListener(new OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.seekTo(msec);
                    seek.setMax(mediaPlayer.getDuration());
                    seekThread.start();
                    Log.i("IOTCamera", "-------------seekThread.start();");
                }
            });
            mediaPlayer.setOnCompletionListener(new OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // 我们的录像文件好像有问题，这里面的MediaPlayer 每次读到最后 就不在读了，导致seek显示不到最后
                    // ，故设此。异步更新可能出错。
                    seek.setProgress(mediaPlayer.getDuration());
                    Log.e("IOTCamera", "-------------onCompletion");
                    release();
                }
            });
        } catch (Exception ex) {

            Log.e("IOTCamera", "-------------Exception");
            ex.printStackTrace();
        }
    }

    // 应该和 device端的 播放放在一起。
    public void stopLocalVideo() {

        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
            seekThread.stopThread();
            if (seekThread != null) {
                seekThread = null;
            }

        }

    }

    public void startLocalVideo() {
        currentPosition = mediaPlayer.getCurrentPosition();
        playVideo(getFileNameString(), currentPosition);
        Log.i("IOTCamera", "-------------startLocalVideo" + getFileNameString()
                + currentPosition);
    }

    private class SeekThread extends Thread {

        boolean isPlaying = false;

        public void stopThread() {
            isPlaying = false;
            Log.i("IOTCamera", "-------------isPlaying = false;");
        }

        @Override
        public void run() {
            isPlaying = true;

            while (isPlaying) {
                int current = mediaPlayer.getCurrentPosition();
                seek.setProgress(current > mediaPlayer.getDuration() ? mediaPlayer.getDuration() : current);
                Log.i("IOTCamera", "-------------current" + current);
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
    }

    // add syf
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        //startPauseButton.setBackgroundResource(R.drawable.play_videotape);
        VideotapeInfo bean = (VideotapeInfo) videotapeHistoryAdapter
                .getItem(position);
        if (bean != null) {
            switch (bean.getVideoType()) {
                case Config.LOCAL_VOIDE:
                    // 对于本地播放每次点击前应该释放 mediaplayer
                    release();
                    playVideo(bean.getVideoLocation(), currentPosition);
                    // add by guofeng.
//				playVideo(bean.getVideoLocation(), currentPosition);
                    // add by guofeng.
                    setFileNameString(bean.getVideoLocation());
                    break;
                case Config.SERVER_VOIDE:
                    if (cameaHelper != null) {
                        IotSendOrder.sendIoctrlSetPlayBackFileNowReq(cameaHelper.getmCamera(), IotUtil.stringToByteforPlay(bean.getFileName()), bean.getFileName());
                    }
                    break;
            }
        }
    }

    public void onClick(View v) {
        if (v == ivCalendar) {
            showDataPickerPopWindow();
        } else if (v == ivBack) {
            this.finish();
        } else if (v == videoFullScreen) {
            // switchLandPorial();
        } else if (v == videoFullScreen) {
            initRotationUIFull();
        } else if (v == startPauseButton) {
        } else if (v == mImageView) {
            // switchpressState();
            selectedListenView(status);
            status = !status;
        }
    }

    //add syf
    private void showDataPickerPopWindow() {
        if (datePickerPopWindow == null) {
            datePickerPopWindow = new DatePickerPopWindow(this) {
                @Override
                public void callBackData(String data) {
                    videos.clear();
                    videotapeHistoryAdapter.notifyDataSetChanged();
                    Log.e(TAG, "videos===" + videos.size());
                    selectDate = data;
                    if (data != null) {
                        datePickerPopWindow.dismiss();
                        if (cameaHelper != null) {
                            PlayHistoryVideoActivity.this.execute(iotCameraBean.getGwId());
                            IotSendOrder.sendIoctrlGetPlayBackFile(cameaHelper.getmCamera(), IotUtil.stringToByteforSearch(data, 0), IotUtil.stringToByteforSearch(data, 1));
                        }
                        return;
                    }
                }
            };
        }
        datePickerPopWindow.show(linParent);
    }

    // add by guofeng
    @SuppressLint("NewApi")
    private void switchPlayState() {
        // TODO Auto-generated method stub

        // 切换播放状态的前提是正在播放，这个逻辑不能少的。少了就没发玩了。

        if (mediaPlayer != null) {
            if (startPauseButton.getTag() == "play") {
                Log.i("IOTCamera", "------------switchPlayState1");
                startPauseButton.setBackground(getResources().getDrawable(
                        R.drawable.play_videotape));
                startPauseButton.setTag("pause");
                startLocalVideo();//change by hxc

            } else if (startPauseButton.getTag() == "pause") {
                Log.i("IOTCamera", "------------switchPlayState2");
                startPauseButton.setBackground(getResources().getDrawable(
                        R.drawable.pause_videotape));
                startPauseButton.setTag("play");
                stopLocalVideo();

            }
        }
    }

    private void switchLandPorial() {
        // TODO Auto-generated method stub

        if (videoFullScreen.getTag() == "vertical") {

            videoFullScreen.setTag("landspace");
            goLandscape();
            Log.e("IOTCamera", "--------------横屏");

        } else if (videoFullScreen.getTag() == "landspace") {

            videoFullScreen.setTag("vertical");
            goPortrait();
            Log.e("IOTCamera", "--------------竖屏");
        }

    }

    // add by hxc

    /**
     * 设置图片方式1
     */
    private void switchpressState() {
        // TODO Auto-generated method stub

        if (mImageView.getTag() == "start") {
            mImageView.setBackgroundResource(R.drawable.desk_cb_silence_off);
            mImageView.setTag("stop");

        } else if (mImageView.getTag() == "stop") {
            mImageView.setBackgroundResource(R.drawable.desk_cb_silence_on);
            mImageView.setTag("start");

        }

    }

    // add by hxc

    /**
     * 设置图片方式2
     */
    @SuppressLint("NewApi")
    private void selectedListenView(boolean flg) {
        int drawable = flg ? R.drawable.desk_cb_silence_on
                : R.drawable.desk_cb_silence_off;
        mImageView.setBackground(getResources().getDrawable(drawable));
    }

    /**
     * 添加横屏载体
     */
    private void attachVideoPreview() {
        if (this.cameraPreview == null) {
            this.cameraPreview = ViERenderer.CreateRenderer(this, true, false);
            int deviceHeight = Utils.getDeviceSize(this).heightPixels;
            int cameraPreviewHeight = deviceHeight * 4 / 9;// 根据布局中的上下比例
            int cameraPreviewWidth = (int) ((float) cameraPreviewHeight
                    / heightRatio * widthRatio);
            minWidth = Utils.getDeviceSize(this).widthPixels;
            maxWidth = cameraPreviewWidth;
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                    cameraPreviewWidth, cameraPreviewHeight);
            lp.addRule(RelativeLayout.CENTER_IN_PARENT);// 全尺寸时居中显示
            this.cameraPreview.setLayoutParams(lp);
        }
    }

    /**
     * 设置图片
     */
    public final void goLandscape() {
        verticalSpaceHistoryLayout.setVisibility(View.GONE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // 全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置高度
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cameraPreview
                .getLayoutParams();
        lp.width = Utils.getDeviceSize(this).widthPixels;
        lp.height = (int) ((float) lp.width / widthRatio * heightRatio);
        cameraPreview.setLayoutParams(lp);
    }

    /**
     * 竖屏
     */
    public final void goPortrait() {
        verticalSpaceHistoryLayout.setVisibility(View.VISIBLE);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 以宽度 推测 高度
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) cameraPreview
                .getLayoutParams();
        lp.width = maxWidth;
        lp.height = (int) ((float) maxWidth / widthRatio * heightRatio);
        cameraPreview.setLayoutParams(lp);
    }

    /**
     * 释放播放录像内存 防止内存泄露
     */
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;

        }

        if (seekThread != null) {
            seekThread.stopThread();
            seekThread = null;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {

        Log.i("IOTCamera", "-------------onProgressChanged" + progress);

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int progress = seekBar.getProgress();
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (mediaPlayer != null) {
            currentPosition = mediaPlayer.getCurrentPosition();
            mediaPlayer.stop();
        }
    }

    // add by guofeng

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        Log.i("IOTCamera", "--------------------onConfigurationChanged");

    }

    public String getFileNameString() {
        return fileNameString;
    }

    public void setFileNameString(String fileNameString) {
        this.fileNameString = fileNameString;
    }

    public void initRotationUIFull() {

        if (videoFullScreen.getTag() == "vertical") {

            verticalSpaceHistoryLayout.setVisibility(View.GONE);
            landSpaceHistoryLayout.setVisibility(View.VISIBLE);
            videoFullScreen.setTag("landspace");
            Log.i("IOTCamera", "--------------------landspace");
        } else if (videoFullScreen.getTag() == "landspace") {

            landSpaceHistoryLayout.setVisibility(View.GONE);
            verticalSpaceHistoryLayout.setVisibility(View.VISIBLE);
            videoFullScreen.setTag("vertical");
            Log.i("IOTCamera", "--------------------vertical");
        }

    }

    public void initRotationUI(Configuration newConfig) {
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {

        } else {

        }
    }

    @Override
    protected void removeMessages() {
        mHandler.removeMessages(HandlerConstant.FILLDATA);
    }

    @Override
    public boolean handleMessage(Message msg) {
        return false;
    }
}
