package cc.wulian.smarthomev5.fragment.testApi;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.ui.WLDialog;

import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;


public class testApi406Fragment extends WulianFragment implements View.OnClickListener, ICommand406_Result {
    private View parentView;
    private EditText editinputKey;
    private TextView tvResult;
    private EditText editinitial;
    private  EditText editinterval;
    Command406_DeviceConfigMsg command406;
    private String devID="";
    private String gwID="";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        command406=new Command406_DeviceConfigMsg(this.mActivity);
        Bundle args=getArguments();
        devID=args.getString("devID");
        gwID=args.getString("gwID");
        command406.setDevID(devID);
        command406.setGwID(gwID);
        command406.setConfigMsg(this);
        initBar();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_test_api406, container, false);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(parentView);
    }

    private  void  initView(View paramView){
        editinitial= (EditText) paramView.findViewById(R.id.editinitial);
        editinterval= (EditText) paramView.findViewById(R.id.editinterval);
        editinputKey= (EditText) paramView.findViewById(R.id.editinputKey);
        tvResult= (TextView) paramView.findViewById(R.id.tvResult);
        paramView.findViewById(R.id.btnCreateSIN).setOnClickListener(this);
        paramView.findViewById(R.id.btnGetSIN).setOnClickListener(this);
        paramView.findViewById(R.id.btnClearData).setOnClickListener(this);
        paramView.findViewById(R.id.btnClearText).setOnClickListener(this);
    }

    private void  initBar(){
        this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setTitle("测试406命令");
        getSupportActionBar().setIconText("返回");
    }
    private boolean isInputKey(){
        boolean isInput=true;
        final String inputstr=editinputKey.getText().toString();
        if(StringUtil.isNullOrEmpty(inputstr)){
            Toast.makeText(this.mActivity,"请输入key值",Toast.LENGTH_SHORT).show();
            isInput=false;
        }
        return  isInput;
    }
    @Override
    public void onClick(View view) {

        tvResult.setText("");
        switch (view.getId()) {
            case R.id.btnCreateSIN: {
                if(isInputKey()){
                    String inputstr=editinputKey.getText().toString();
                    int initial=1,interval=1;
                    if(!StringUtil.isNullOrEmpty(editinitial.getText().toString())){
                        try{
                            initial=Integer.parseInt(editinitial.getText().toString());
                        }catch(Exception ex) {
                            initial=1;
                        };
                    }
                    if(!StringUtil.isNullOrEmpty(editinterval.getText().toString())){
                        try{
                            interval=Integer.parseInt(editinterval.getText().toString());
                        }catch(Exception ex) {
                            interval=1;
                        };
                    }
                    command406.SendCommand_CreateSIN(inputstr, initial, interval);
                }
            }
            break;
            case R.id.btnGetSIN: {
                if(isInputKey()){
                    String inputstr=editinputKey.getText().toString();
                    command406.SendCommand_GetSIN(inputstr);
                }

            }
            break;
            case R.id.btnClearData:{
                if(isInputKey()){
                    final String inputstr=editinputKey.getText().toString();
                    WLDialog.Builder builder = new WLDialog.Builder(testApi406Fragment.this.mActivity);
                    builder.setTitle(cc.wulian.smarthomev5.R.string.device_songname_refresh_title);
                    LinearLayout dialog_delete=(LinearLayout) LayoutInflater.from(testApi406Fragment.this.mActivity).inflate(cc.wulian.smarthomev5.R.layout.common_dialog_delete, null);
                    TextView textView=(TextView) dialog_delete.findViewById(cc.wulian.smarthomev5.R.id.delete_msg_tv);
                    String msg=String.format("您确定要清空设备%s下的所有数据吗？",devID);
                    textView.setText(msg);
                    builder.setContentView(dialog_delete);
                    builder.setPositiveButton(android.R.string.ok);
                    builder.setNegativeButton(android.R.string.cancel);
                    builder.setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View contentViewLayout) {
                            command406.SendCommand_ClearV2(inputstr);
                        }

                        @Override
                        public void onClickNegative(View contentViewLayout) {

                        }
                    });
                    WLDialog mdeleteBrandItemDialog = builder.create();
                    mdeleteBrandItemDialog.show();
                }

            }break;
            case R.id.btnClearText:{
                tvResult.setText("");
            }break;
            default:
                break;
        }
    }

    @Override
    public void Reply406Result(Command406Result result) {

        final StringBuilder strBui=new StringBuilder();
        strBui.append("gwID="+result.getGwID()+"\r\n");
        strBui.append("devID="+result.getDevID()+"\r\n");
        strBui.append("mode="+result.getMode()+"\r\n");
        strBui.append("time="+result.getTime()+"\r\n");
        strBui.append("key="+result.getKey()+"\r\n");
        strBui.append("data="+result.getData()+"\r\n");
        strBui.append("------------------------"+"\r\n");
        testApi406Fragment.this.mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvResult.append(strBui.toString());
            }
        });
    }

    @Override
    public void Reply406Result(List<Command406Result> results) {
        for (Command406Result result:results){
            Reply406Result(result);
        }
    }
}
