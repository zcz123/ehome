package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cc.wulian.smarthomev5.R;


/**
 * Created by Administrator on 2017/3/10.
 */

public class EyecatWaitingActivity extends Activity implements View.OnClickListener{
    private LinearLayout eyecat_return;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_waiting);
        initView();
        Intent intent = getIntent();
        final Boolean flag = intent.getBooleanExtra("flag",false);
        new Handler().postDelayed(new Runnable(){
            public void run() {
                progressDialog.dismiss();
                Intent i = new Intent(EyecatWaitingActivity.this,EyecatBindActivity.class);
                i.putExtra("flag",flag);
                startActivity(i);
            }
        }, 3000);

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
}
