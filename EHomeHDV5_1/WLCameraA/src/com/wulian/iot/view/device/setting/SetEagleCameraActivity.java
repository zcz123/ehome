package com.wulian.iot.view.device.setting;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Camera;
import com.wulian.icam.R;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.CameraEagleUpdateInfo;
import com.wulian.iot.bean.IOTCDevChPojo;
import com.wulian.iot.cdm.UpdateCameraSet;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.EagleUtil;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.utils.MeshUtil;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.view.device.play.PlayEagleActivity;
import com.wulian.iot.view.ui.EagleDeviceInfoActivity;
import com.wulian.iot.view.ui.EagleHumanAlarmSetActivity;
import com.wulian.iot.widght.DialogRealize;

public class SetEagleCameraActivity extends SimpleFragmentActivity implements OnClickListener, Handler.Callback {
    private LinearLayout mLayout, mLinEagleInfo,mHumanAlarm;//固件升级 ,设备信息
    private TextView mEagleVersion;
    private TextView mSensitivity;
    private Button mEagleDelete;
    private CameraEagleUpdateInfo mCameraEagleInfo;
    private LinearLayout linShare;
    private final static String url = "http://otacdn.wulian.cc/yingyan_zh.xml";
    private ImageView mBcak;
    private TextView mTitle;
    private TextView eagleCameraName;
    private IOTCDevChPojo iotcDevChPojo;//iot通道连接参数
    private Handler mHandler = new Handler(this);
    private String enterModel = null;
    private boolean isAdmin=false;
    private boolean isOffLine=false;
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
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_DEVINFO_RESP:
                            DialogRealize.unInit().dismissDialog();
                            setIotcDevInfo(IotUtil.parseEagleInfo(data));
                            mEagleVersion.setText(EagleUtil.interceptionString(getIotcDevInfo().getVersion()).trim());
                            break;
                    }
                }
            });
        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {
//            final String msg = messageQueue.filter(resCode,method).sendMsg();
            if (resCode== Camera.CONNECTION_ER_DEVICE_OFFLINE&&!isOffLine){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SetEagleCameraActivity.this, getResources().getString(R.string.html_map_2107_error), Toast.LENGTH_SHORT).show();
                        DialogRealize.unInit().dismissDialog();
                        isOffLine=true;
                    }
                });
            }
        }
    };

    @Override
    public boolean handleMessage(Message msg) {
        switch (msg.what){
            case HandlerConstant.UPDATE_UI:
                Toast.makeText(this, getResources().getString(R.string.desktop_setting_has_latest), Toast.LENGTH_SHORT).show();
                break;
            case Config.UPDATE_IS_OK:
                if (msg.arg1==0){
                    eagleCameraName.setText(cameraName);
                    Toast.makeText(this, getResources().getString(R.string.device_E4_change_success), Toast.LENGTH_SHORT).show();
                    setCameraName(cameraName);
                }else {
                    Toast.makeText(this, getResources().getString(R.string.cateye_update_cameraInfo_faile), Toast.LENGTH_SHORT).show();
                }
                break;
            case HandlerConstant.SUCCESS:
            case 200: //注销tutk 推送
                Toast.makeText(this,getResources().getString(R.string.smartLock_deleting_temporary_user_success_hint),Toast.LENGTH_LONG).show();
                finish();
                break;
            case HandlerConstant.ERROR:
                Toast.makeText(this,getResources().getString(R.string.smartLock_deleting_temporary_user_fail_hint),Toast.LENGTH_LONG).show();
                break;
        }
        return false;
    }


    @Override
    protected void onResume() {
        super.onResume();
        DialogRealize.init(this).showDiglog();
        startPlaySurfaceView();
        setSensitivity();
    }

    public void setmCameraEagleInfo(CameraEagleUpdateInfo mCameraEagleInfo) {
        this.mCameraEagleInfo = mCameraEagleInfo;
    }

    public CameraEagleUpdateInfo getmCameraEagleInfo() {
        return mCameraEagleInfo;
    }

    @Override
    public void root() {
        setContentView(R.layout.activity_set_eagle_camera);
    }
    public static String WITHOUT_CAMERA_SETTING = "without";
    public static String INTERIOR_CAMERA_SETTING = "interior";
    private void startPlaySurfaceView() {
        if (enterModel.equals(WITHOUT_CAMERA_SETTING)) {
            Log.i(TAG, " Without landing");
                cameaHelper = CameraHelper.getInstance(getIotcDevChPojo());
                cameaHelper.registerstIOTCLiener();
                cameaHelper.attach(iotcDevConnCallback);
                cameaHelper.attach(observer);
                cameaHelper.register();
                return;
        }
            Log.i(TAG, " Internal landing");
            cameaHelper.attach(observer);
            sendFindEagleVerByIotc();
    }

    private CameraHelper.IOTCDevConnCallback iotcDevConnCallback = new CameraHelper.IOTCDevConnCallback() {
        @Override
        public void success() {
            Log.i(TAG, "success");
            sendFindEagleVerByIotc();
        }

        @Override
        public void session() {
            Log.i(TAG, "session");
            createSessionWaitThread = new CreateSessionWaitThread();
            createSessionWaitThread.start();
        }

        @Override
        public void avChannel() {
            Log.i(TAG, "avChannel");
            createAvChannelWaitThread = new CreateAvChannelWaitThread();
            createAvChannelWaitThread.start();
        }
    };

    private final void sendFindEagleVerByIotc() {//获取设备版本号
        IotSendOrder.findEagleVerByIoc(cameaHelper.getmCamera());
    }

    private final void uninit() {
        if (enterModel.equals(INTERIOR_CAMERA_SETTING)) {
            Log.i(TAG,"Internal");
            cameaHelper.detach(iotcDevConnCallback);
            return;
        }
        Log.i(TAG,"without");
        cameaHelper.detach(observer);
        cameaHelper.destroyCameraHelper();
        cameaHelper = null;
    }

    @Override
    protected void onPause() {
        super.onPause();
        destroyWailThread();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        uninit();
    }

    @Override
    public void initView() {
        mLayout = (LinearLayout) findViewById(R.id.ll_eagle_version_info);
        mEagleVersion = (TextView) findViewById(R.id.tv_eagle_version);
        mLinEagleInfo = (LinearLayout) findViewById(R.id.lin_eagle_device_info);
        mBcak = (ImageView) findViewById(R.id.iv_cateye_titlebar_back);
        mHumanAlarm=(LinearLayout) findViewById(R.id.lin_eagle_human_alarm);
        mSensitivity=(TextView) findViewById(R.id.tv_show_sensitivity);
        mTitle = (TextView) findViewById(R.id.tv_cateye_titlebar_title);
        mTitle.setText(R.string.device_ir_setting);
        eagleCameraName= (TextView) findViewById(R.id.tv_eagle_camera_name);
        mEagleDelete= (Button) findViewById(R.id.btn_delete_eagle);
        linShare= (LinearLayout) findViewById(R.id.lin_eagle_device_share);
    }

    private String tutkUid, tutkPwd;

    @Override
    public void initData() {
        Intent dataIntent = getIntent();
        tutkUid = dataIntent.getStringExtra(Config.tutkUid);
        tutkPwd = dataIntent.getStringExtra(Config.tutkPwd);
        enterModel = dataIntent.getStringExtra(Config.eagleSettingEnter);
        isAdmin=dataIntent.getBooleanExtra(Config.isAdmin,false);
        String deviceName=dataIntent.getStringExtra(Config.eagleName);
        if (deviceName!=null&&deviceName!="null"&&deviceName!=""){
            cameraName=deviceName;
            eagleCameraName.setText(cameraName);
            setCameraName(cameraName);
        }else {
            eagleCameraName.setText(getCameraName());
        }
//        dataIntent.getSerializableExtra("amsDeviceInfo");
//        mIntent.putExtra("amsDeviceInfo",(Serializable) amsDeviceInfo);
    }

    private IOTCDevChPojo getIotcDevChPojo() {
        iotcDevChPojo = new IOTCDevChPojo();
        iotcDevChPojo.setDevTag(Config.EAGLE);
        iotcDevChPojo.setTutkPwd(tutkPwd);
        iotcDevChPojo.setTutkUid(tutkUid);
        iotcDevChPojo.setDevConnMode(Config.EagleConnMode);
        return iotcDevChPojo;
    }

    @Override
    public void initEvents() {
        mLayout.setOnClickListener(this);
        mBcak.setOnClickListener(this);
        mLinEagleInfo.setOnClickListener(this);
        mHumanAlarm.setOnClickListener(this);
        eagleCameraName.setOnClickListener(this);
        linShare.setOnClickListener(this);
        mEagleDelete.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (!isAdmin){
            if (v!=mEagleDelete){
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.AUTHGATEWAY_FAILURE),Toast.LENGTH_LONG).show();
                return;
            }
        }
        if (v == mLayout) {
            findVersionByServer();
        } else if (v == mLinEagleInfo) {
            startActivity(new Intent(this, EagleDeviceInfoActivity.class).putExtra(Config.tutkUid, tutkUid).putExtra(Config.eagleName,cameraName));
        } else if (v == mBcak) {
            finish();
        }
        else if (v==mHumanAlarm){
            Intent mIntent=new Intent(this, EagleHumanAlarmSetActivity.class);
            startActivity(mIntent);
        }else if (v==eagleCameraName){
            //弹出dialog 输入文字
            showUpCameraName();
        }else if (v==mEagleDelete){
            //删除操作
            if (updateCameraSet!=null){
                updateCameraSet.deleteEageleCamera(tutkUid,isAdmin,mHandler);
            }
        }else if (v==linShare){
            if (updateCameraSet!=null){
                updateCameraSet.startShareActivity(tutkUid,this);
            }
        }
    }
    public void showUpdataDialog() {
        AlertDialog.Builder mAlertDialog = new AlertDialog.Builder(this);
        mAlertDialog.setTitle(R.string.set_new_version_update_hint);
        mAlertDialog.setMessage(R.string.smartLock_update_prompt);
        mAlertDialog.setPositiveButton(R.string.device_ok, new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //执行升级
                IotSendOrder.sendEagleUpdata(cameaHelper.getmCamera());
                dialog.dismiss(); //关闭dialog
            }
        });
        mAlertDialog.setNegativeButton(R.string.device_cancel, new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        mAlertDialog.create().show();
    }

    /**
     * 得到设备服务端固件信息
     *
     * @return 固件版本号
     */
    private void findVersionByServer() {
        MeshUtil.xmlParseObject(url, obtainDevVersionByServer);
    }

    private CreateSessionWaitThread createSessionWaitThread = null;

    private class CreateSessionWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper.checkSession()) {
                    cameaHelper.register();
                    mIsRunning = false;
                }
            }
        }
    }

    private CreateAvChannelWaitThread createAvChannelWaitThread = null;

    private class CreateAvChannelWaitThread extends Thread {
        private boolean mIsRunning = true;

        public void stopThread() {
            mIsRunning = false;
        }

        @Override
        public void run() {
            mIsRunning = true;
            while (mIsRunning) {
                if (cameaHelper.checkAvChannel()) {
                    cameaHelper.register();
                    mIsRunning = false;
                }
            }
        }
    }

    private void destroyWailThread() {
        if (createSessionWaitThread != null) {
            createSessionWaitThread.stopThread();
            createSessionWaitThread = null;
        }
        if (createAvChannelWaitThread != null) {
            createAvChannelWaitThread.stopThread();
            createAvChannelWaitThread = null;
        }
    }
    private void setSensitivity(){
        int sensity=sharedPreferences.getInt(Config.SENSITIVITY_EAGLE, -1);
        String [] sen={getResources().getString(R.string.cateye_sensitivity_setting_close),
                getResources().getString(R.string.cateye_sensitivity_setting_low),
                getResources().getString(R.string.cateye_sensitivity_setting_mid),
                getResources().getString(R.string.cateye_sensitivity_setting_high)};
        if (sensity!=-1) {
            mSensitivity.setText(sen[sensity]);
        }else {
            mSensitivity.setText("");
        }
    }

    private ObtainDevVersionByServer obtainDevVersionByServer = new ObtainDevVersionByServer() {
        @Override
        public void success(final CameraEagleUpdateInfo cameraEagleUpdateInfo) {
            runOnUiThread(new Runnable() {
               @Override
               public void run() {
                   setmCameraEagleInfo(cameraEagleUpdateInfo);
                   checkUpdate();
               }
           });

        }
        public void error(Throwable throwable) {//TODO 异常处理
            Log.e(TAG,throwable.getLocalizedMessage().toString());
        }
    };

    public interface ObtainDevVersionByServer {
        void success(CameraEagleUpdateInfo cameraEagleUpdateInfo);

        void error(Throwable throwable);
    }

    private void checkUpdate() {
        if ( (EagleUtil.interceptionString(getIotcDevInfo().getVersion()).compareTo (getmCameraEagleInfo().getVersionName()))<0){
            showUpdataDialog();
            return;
        }
        mHandler.sendEmptyMessage(HandlerConstant.UPDATE_UI);
    }
    private  Dialog mDialog;
    private String cameraName;
    private void showUpCameraName(){
          mDialog=DialogUtils.showCommonEditDialog(this, true, getResources().getString(R.string.device_modify_name), getResources().getString(R.string.device_metering_switch_html_ok_hint),
                  getResources().getString(R.string.device_metering_switch_html_cancel_hint), "", "", new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.et_input) {
                    EditText editText= (EditText) v;
                    cameraName= editText.getText().toString().trim();
                    if (cameraName.isEmpty()){
                        Toast.makeText(getApplicationContext(),getResources().getString(R.string.hint_not_null_edittext),Toast.LENGTH_LONG).show();
                    }else{
                        if(cameraName.length()>20){
                            Toast.makeText(getApplicationContext(),"超出字符最大20长度限制",Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (updateCameraSet!=null){
                            updateCameraSet.deviceUpdate(tutkUid,cameraName,mHandler);
                        }
                    }
                }
                mDialog.dismiss();
            }

        });
    }
    private static UpdateCameraSet updateCameraSet;
    public static void setUpdateCameraName(UpdateCameraSet updateCameraName){
        SetEagleCameraActivity.updateCameraSet=updateCameraName;
    }

    private String getCameraName(){
        return  sharedPreferences.getString(Config.CAMERANAME_EAGLE,getResources().getString(R.string.monitor_eagle_camera));
    }
    private void setCameraName(String  name){
       editor.putString(Config.CAMERANAME_EAGLE,name).commit();
    }
    public void setIotcDevInfo(IOTCDevInfo iotcDevInfo) {
        this.iotcDevInfo = iotcDevInfo;
    }

    public IOTCDevInfo getIotcDevInfo() {
        return iotcDevInfo;
    }

    public IOTCDevInfo iotcDevInfo = null;

    public static class IOTCDevInfo {
        private String model;
        private String vendor;
        private int version;
        private int channel;
        private int total;
        private int free;
        private String RESERVED;

        public void setVersion(int version) {
            this.version = version;
        }

        public int getVersion() {
            return version;
        }

        public String getModel() {
            return model;
        }

        public void setModel(String model) {
            this.model = model;
        }

        public String getVendor() {
            return vendor;
        }

        public void setVendor(String vendor) {
            this.vendor = vendor;
        }

        public void setChannel(int channel) {
            this.channel = channel;
        }

        public int getChannel() {
            return channel;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return total;
        }

        public void setFree(int free) {
            this.free = free;
        }

        public int getFree() {
            return free;
        }

        public String getRESERVED() {
            return RESERVED;
        }

        public void setRESERVED(String RESERVED) {
            this.RESERVED = RESERVED;
        }
    }
}
