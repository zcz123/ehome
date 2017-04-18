package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import cc.wulian.smarthomev5.R;


/**
 * Created by Administrator on 2017/3/9.
 */

public class EyecatScanQRcodeActivity extends Activity implements View.OnClickListener {
    private Button eyecat_ready_scan;
    private String password;
    private LinearLayout eyecat_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_sacn_qrcode);
        initView();
        initDate();
    }
    private void initView(){
        eyecat_return = (LinearLayout) findViewById(R.id.eyecat_return);
        eyecat_ready_scan = (Button) findViewById(R.id.eyecat_ready_scan);
        eyecat_return.setOnClickListener(this);
        eyecat_ready_scan.setOnClickListener(this);
    }
    private void initDate(){
        Intent intent = getIntent();
        password = intent.getStringExtra("pwd");
    }
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
            case R.id.eyecat_ready_scan:
                Intent intent = new Intent(EyecatScanQRcodeActivity.this,EyecatQRcodeActivity.class);
                intent.putExtra("pwd",password);
                startActivity(intent);
                finish();
                break;
        }
    }

}
