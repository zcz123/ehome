package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.eques.icvss.utils.Method;

import cc.wulian.smarthomev5.R;


/**
 * Created by Administrator on 2017/3/11.
 */

public class EyecatMonitoringActivity extends Activity implements View.OnClickListener
{
    private LinearLayout eyecat_yikang_pro,eyecat_return;
    private Button eyecat_yikang_setting;
    private String uid;
    private Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_monitoring);
        uid = Cookies.session.getUid();
        Log.d("zcz",uid);
        initView();
    }
    private void initView(){
        eyecat_yikang_pro = (LinearLayout) findViewById(R.id.eyecat_yikang_pro);
        eyecat_yikang_pro.setOnClickListener(this);
        eyecat_yikang_setting = (Button) findViewById(R.id.eyecat_yikang_setting);
        eyecat_yikang_setting.setOnClickListener(this);
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_yikang_pro:
                checkMonitorVideo(uid,true);
                break;
            case R.id.eyecat_yikang_setting:

                break;
            case R.id.eyecat_return:
                finish();
                break;
        }
    }
    public void checkMonitorVideo(String uid, boolean flag) {
        Intent intent = new Intent(this, EyecatVideoCallActivity.class);
        intent.putExtra(Method.ATTR_BUDDY_UID, uid);
        intent.putExtra(Method.ATTR_CALL_HASVIDEO, flag); //是否显示视频， true：显示  false： 不显示
        startActivity(intent);
    }
}
