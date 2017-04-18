package cc.wulian.smarthomev5.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wulian.iot.Config;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.core.http.HttpProvider;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.event.DeviceActivation;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.monitor.EditMonitorInfoFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.HttpURLConnUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2016/12/22 0022.
 */

public class BackMusicActivationActivity extends Activity implements View.OnClickListener, Handler.Callback {

    private String deviceID;
    private String deviceType;
    private String gwID;
    private String userPhone;
    private Handler mHandler = new Handler(this);
    private ImageView mTitlebarBack;
    private TextView mTitlebarTitle;
    private Button mBtnScan;
    private boolean flag=true;//用于eventbus接收时用

    private final String dialogKey="backmusic";

    private final String url = "https://acs.wuliancloud.com:33443/product/oauth.do";//测试的url http://222.190.121.158:7009/acs/product/oauth.do

    private final String SUCCESS="SUCCESS";//认证成功
    private final String ERROR_PARAMS_EMPTY="ERROR_PARAMS_EMPTY";//请求参数存在空值
    private final String ERROR_PRODUCT_ALREADY_ACTIVATED="ERROR_PRODUCT_ALREADY_ACTIVATED";//该设备已激活
    private final String ERROR_CODE_NOT_EXIST="ERROR_CODE_NOT_EXIST";//该授权码无效
    private final String ERROR_CODE_ALREADY_BOUND="ERROR_CODE_ALREADY_BOUND";//该授权码已被其他设备绑定
    private final String ERROR_CODE_NOT_FOR_TYPE="ERROR_CODE_NOT_FOR_TYPE";//该授权码不支持此设备类型
    private final String ERROR_TOKEN_EXPIRED="ERROR_TOKEN_EXPIRED";//token失效
    private final String ERROR_CCP_INTERNAL="ERROR_CCP_INTERNAL";//系统内部异常抛出错误码

    private boolean account=false;
    private ProgressDialogManager progressDialogManager=ProgressDialogManager.getDialogManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bg_music_activation);
        initData();
        initView();
    }


    private void initView() {
        mTitlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        mTitlebarBack.setOnClickListener(this);
        mTitlebarTitle = (TextView) findViewById(R.id.titlebar_title);
        mTitlebarTitle.setText(getResources().getString(R.string.device_music_activate_the_device));
        mBtnScan = (Button) findViewById(R.id.btn_scan);
        mBtnScan.setOnClickListener(this);
        EventBus.getDefault().register(this);
    }

    private void initData() {
        deviceID = getIntent().getStringExtra(Config.DEVICE_ID);
        deviceType = getIntent().getStringExtra(Config.DEVICE_TYPE);
        gwID=getIntent().getStringExtra(Config.GW_ID);

        String json=SmarthomeFeatureImpl.getData("profile_json");
        if (!StringUtil.isNullOrEmpty(json)){
            JSONObject jsonObject=JSONObject.parseObject(json);
            userPhone=jsonObject.getString("phone");
        }
        account= Preference.getPreferences().getUserEnterType().equals("account");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_scan:
                if (account){
                    Intent it = new Intent(this, QRScanActivity.class);
                    it.putExtra("wulianScan", "bgmusicScan");
                    startActivityForResult(it, 0);
                }else {
                    showResultDialog(getResources().getString(R.string.device_music_unable_to_scan_hint));
                }
                break;
            case R.id.titlebar_back:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data==null){
            return;
        }
        if (requestCode == 0) {
            //拿到扫码后 去验证
            String oauthCode = data.getStringExtra(EditMonitorInfoFragment.RESULT_UID);
            progressDialogManager.showDialog(dialogKey,this,"",null,5000*12);
            String token=SmarthomeFeatureImpl.getData("token");
            sendVerification(oauthCode, deviceID, deviceType, userPhone,token);
        }
    }
    private void sendVerification( String oauthCode,  String productId,  String productType, String oauthUser,String token) {
        if (StringUtil.isNullOrEmpty(token)){
            mHandler.sendEmptyMessage(-1); //token为空
        }
        if (null==oauthCode||null==productId||productId==null||oauthUser==null){
            mHandler.sendEmptyMessage(0);
        }
        String data="productId="+productId+"&oauthCode="+oauthCode+"&productType="+productType+"&oauthUser="+oauthUser;
        HttpURLConnUtil.post(url,data,0,mHandler,token);
    }

    @Override
    public boolean handleMessage(Message msg) {
        switch(msg.what){
            case 0:
                progressDialogManager.dimissDialog(dialogKey,0);
                result((String) msg.obj);
                break;
            case 1:
                showResultDialog(getResources().getString(R.string.device_music_activation_successful));
                break;
            case 2:
                showResultDialog(getResources().getString(R.string.device_music_activation_failed));
                break;
            case -1:
                showResultDialog(getResources().getString(R.string.device_music_unable_to_scan_hint));
                break;

            default:
                Log.i("","this is default");
                break;
        }
        return false;
    }
    private void result(String code){
        if (StringUtil.isNullOrEmpty(code)){
            return;
        }
        switch (code){
            case SUCCESS:
                NetSDK.sendSetDevMsg(gwID,"8",deviceID,null,null,null,null,null,null,null,null,null,null,null);
                flag=false;
                progressDialogManager.showDialog(dialogKey,this,"",null,5000*12);
                break;
            case ERROR_PRODUCT_ALREADY_ACTIVATED:
                showResultDialog(getResources().getString(R.string.device_music_has_been_activated));
                NetSDK.sendSetDevMsg(gwID,"8",deviceID,null,null,null,null,null,null,null,null,null,null,null);
                flag=false;
                break;
            case ERROR_TOKEN_EXPIRED://token失效
                showResultDialog(getResources().getString(R.string.eagle_bind_token_failure));
                break;
            case ERROR_CODE_NOT_EXIST://无效
                showResultDialog(getResources().getString(R.string.device_music_active_invalid));
                break;
            default://激活失败
                showResultDialog(getResources().getString(R.string.device_music_activation_failed));
                break;
        }

    }
    public void onEventMainThread(DeviceActivation deviceEvent){
        if (flag){
            return;
        }
        flag=true;
        if (deviceEvent==null){
            mHandler.sendEmptyMessage(2);//激活失败
            return;
        }
        if (StringUtil.isNullOrEmpty(deviceEvent.deviceID)){
            return;
        }else {
            if (!deviceID.equals(deviceEvent.deviceID)){
                mHandler.sendEmptyMessage(2);//激活失败
                return;
            }
        }
        DeviceInfo info=deviceEvent.deviceInfo;
        if (info==null){
            mHandler.sendEmptyMessage(2);//激活失败
            return;
        }
        String isValidate=info.getIsvalidate();
        if (!StringUtil.isNullOrEmpty(isValidate)){
            if ("1".equals(isValidate)){
                mHandler.sendEmptyMessage(1);//成功
            }else if ("2".equals(isValidate)){
                mHandler.sendEmptyMessage(2);//失败
            }
        }
        progressDialogManager.dimissDialog(dialogKey,0);
    }

    private void  showResultDialog(String message){
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle("");
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(
                R.layout.device_door_lock_setting_account_dynamic, null);
        TextView textView = (TextView) view
                .findViewById(R.id.device_new_door_lock_account_dynamic_textview);
        textView.setText(message);

        builder.setContentView(view);
        builder.setPositiveButton(getResources().getString(R.string.common_ok));
        builder.setNegativeButton(null);
        final WLDialog mMessageDialog = builder.create();
        mMessageDialog.show();
        builder.setListener(new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View view) {
                mMessageDialog.dismiss();
                finish();
            }

            @Override
            public void onClickNegative(View view) {

            }
        });
        mMessageDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                finish();
            }
        });
        if (!mMessageDialog.isShowing()){
            mMessageDialog.show();
        }
    }
}
