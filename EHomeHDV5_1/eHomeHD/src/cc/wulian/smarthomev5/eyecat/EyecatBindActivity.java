package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.smarthomev5.R;


/**
 * Created by Administrator on 2017/3/10.
 */

public class EyecatBindActivity extends Activity implements View.OnClickListener
{
    private LinearLayout eyecat_return;
    private Button eyecat_next;
    private ImageView eyecat_bind;
    private TextView eyecat_status,eyecat_still_problem;
    private LinearLayout eyecat_status_fail;
    private Boolean flag;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_bind);


        initView();
    }
    private void initView(){
        Intent i = getIntent();
        flag = i.getBooleanExtra("flag",false);
        eyecat_bind = (ImageView) findViewById(R.id.eyecat_bind);
        eyecat_status = (TextView) findViewById(R.id.eyecat_status);
        eyecat_status_fail = (LinearLayout) findViewById(R.id.eyecat_status_fail);
        eyecat_return = (LinearLayout) findViewById(R.id.eyecat_return);
        eyecat_return.setOnClickListener(this);
        eyecat_next = (Button) findViewById(R.id.eyecat_next);
        eyecat_next.setOnClickListener(this);
        eyecat_still_problem = (TextView) findViewById(R.id.eyecat_still_problem);
        if(flag){
            eyecat_bind.setImageResource(R.drawable.eyecat_icon_succeed);
            eyecat_status_fail.setVisibility(View.INVISIBLE);
            eyecat_status.setText("绑定成功");
            eyecat_next.setText("查看我的设备");
            eyecat_still_problem.setVisibility(View.GONE);
        }else{
            eyecat_bind.setImageResource(R.drawable.eyecat_icon_cometonothing);
            eyecat_status.setText("绑定失败");
            eyecat_status_fail.setVisibility(View.VISIBLE);
            eyecat_next.setText("重新绑定");
            eyecat_still_problem.setVisibility(View.VISIBLE);
        }

    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
            case R.id.eyecat_next:
                Intent intent = null;
                if(flag){
                   intent = new Intent(EyecatBindActivity.this,EyecatMonitoringActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    intent = new Intent(EyecatBindActivity.this,EyecatQRcodeActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }
}
