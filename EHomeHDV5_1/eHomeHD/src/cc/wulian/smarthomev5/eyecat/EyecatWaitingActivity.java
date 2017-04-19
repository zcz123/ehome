package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eques.icvss.utils.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.ihome.wan.core.http.Result;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.bean.Session;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;


/**
 * Created by Administrator on 2017/3/10.
 */

public class EyecatWaitingActivity extends Activity implements View.OnClickListener{
    private LinearLayout eyecat_return;
    private ProgressDialog progressDialog;
    private String bid;
    private String reqId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_waiting);
        initView();
        Intent intent = getIntent();
        reqId = intent.getStringExtra("reqId");

    }
    private void initView(){
        eyecat_return = (LinearLayout) findViewById(R.id.eyecat_return);
        eyecat_return.setOnClickListener(this);
        progressDialog = new ProgressDialog(this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setMessage("正在绑定。。。。。");
        progressDialog.show();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        EyecatManager.getInstance().addPacketListener(addDeviceResultListener);
        EyecatManager.getInstance().addPacketListener(devstResultListener);
        if(!StringUtil.isNullOrEmpty(reqId)) {
            EyecatManager.getInstance().getICVSSUserInstance().equesAckAddResponse(reqId, 1);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        EyecatManager.getInstance().removePacketListener(addDeviceResultListener);
        EyecatManager.getInstance().removePacketListener(devstResultListener);
    }
    private EyecatManager.PacketListener addDeviceResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_ONADDBDY_RESULT;
        }

        @Override
        public void processPacket(JSONObject object) {

            String code = object.optString(Method.ATTR_ERROR_CODE);
            if("4407".equals(code)){
                Log.i("eyecat:","设备已绑定");
            }
            else if("4000".equals(code)) {
                JSONObject added_bdy = object.optJSONObject(Method.ATTR_ADDED_BDY);
                if (added_bdy != null) {
                    bid = added_bdy.optString(Method.ATTR_BUDDY_BID);
                }
                JSONArray array = object.optJSONArray(Method.ATTR_ONLINES);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.optJSONObject(i);
                    EyecatManager.EyecatDevice device = new EyecatManager.EyecatDevice();
                    device.setBid(obj.optString(Method.ATTR_BUDDY_BID));
                    device.setUid(obj.optString(Method.ATTR_BUDDY_UID));
                    device.setStatus(obj.optInt(Method.ATTR_BUDDY_STATUS));
                    EyecatManager.getInstance().putDevice(device);
                    if (StringUtil.equals(bid, obj.optString(Method.ATTR_BUDDY_BID))) {
                        Session session = new Session();
                        session.setBid(bid);
                        session.setUid(EyecatManager.getInstance().getDevice(bid).getUid());
                        Cookies.saveSession(getApplicationContext(), session);
                    }
                }
                TaskExecutor.getInstance().execute(new Runnable() {
                    @Override
                    public void run() {
                        final Result result = WLUserManager.getInstance().getStub().bindDevice(bid,bid,"CAMERA","CMMY02");
                        if(0==result.status){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EyecatWaitingActivity.this, "初次绑定成功", Toast.LENGTH_SHORT).show();
                                    Intent i = new Intent(EyecatWaitingActivity.this, EyecatBindActivity.class);
                                    i.putExtra("flag", true);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }else{
                            EyecatManager.getInstance().getICVSSUserInstance().equesDelDevice(bid);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(EyecatWaitingActivity.this, "初次绑定失败:"+result.status, Toast.LENGTH_LONG).show();
                                    Intent i = new Intent(EyecatWaitingActivity.this, EyecatBindActivity.class);
                                    i.putExtra("flag", false);
                                    startActivity(i);
                                    finish();
                                }
                            });
                        }
                    }
                });
            }
        }
    };
    private EyecatManager.PacketListener devstResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_DEVST;
        }

        @Override
        public void processPacket(JSONObject object) {
            bid = object.optString(Method.ATTR_BUDDY_BID);
            EyecatManager.EyecatDevice device = new EyecatManager.EyecatDevice();
            device.setBid(object.optString(Method.ATTR_BUDDY_BID));
            device.setUid(object.optString(Method.ATTR_BUDDY_UID));
            device.setStatus(object.optInt(Method.ATTR_BUDDY_STATUS));
            EyecatManager.getInstance().putDevice(device);
            Session session = new Session();
            session.setBid(object.optString(Method.ATTR_BUDDY_BID));
            session.setUid(object.optString(Method.ATTR_BUDDY_UID));
            Cookies.saveSession(getApplicationContext(),session);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EyecatWaitingActivity.this,"设备已绑定过",Toast.LENGTH_SHORT).show();
                }
            });
            if(StringUtil.isNullOrEmpty(bid)){
                Intent i = new Intent(EyecatWaitingActivity.this, EyecatBindActivity.class);
                i.putExtra("flag", false);
                startActivity(i);
                finish();
            }else {
                JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.mWebview, SmarthomeFeatureImpl.mCallBackId,"2", JsUtil.OK, true);
                finish();
            }
        }
    };
}
