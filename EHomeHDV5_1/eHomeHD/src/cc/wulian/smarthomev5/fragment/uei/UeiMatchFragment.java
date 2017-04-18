package cc.wulian.smarthomev5.fragment.uei;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.uei.control.ACEService;
import com.uei.control.AirConFunction;
import com.uei.control.AirConSDKManager;
import com.uei.control.AirConState;
import com.uei.control.AirConWidgetStatus;
import com.uei.control.ResultCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class UeiMatchFragment extends WulianFragment implements View.OnClickListener {
    String[] libCodes = null;
    private View parentView;
    private int curLibCodeIndex = -1;
    private AirStateStandard airStatus = null;//界面使用到的标准空调状态
    private AirStatusHolderView holderView = null;//空调状态显示面板
    private Map<String,Integer> map_btnTagAirid=new HashMap<String,Integer>();
    private String log_tag="UeiMatchFragment";
    private String sendData;//执行命令成功后获取的发送码，这个信息保存的时候需要使用
    private static String _states = "";//空调状态
    com.uei.control.AirConDevice airConIRDevice;//设备信息
    private ArrayList<AirConFunction> _currentAirConFunctions = new ArrayList<AirConFunction>();
    String airData = "";//空调码库
    public static String _currentAirconStates = "";//当前空调的状态，貌似和_states有所重复，需要优化
    String argdevicecode = "";//设备编号
    private UeiCommonEpdata ueiCommEpdata=null;//用于发送和组合12命令的对象
    private String Down_AirData="Down_AirData";
    byte[] arrBytedata=null;
    boolean isStart = false;//ACEngineStart是否执行成功
    private TextView tvMatchDesc;
    private String gwID="";
    private String devID="";
    private String callBackId="";
    public static H5PlusWebView pWebview=null;

    private Button btnPreLibCode;
    private Button btnNextLibCode;
    public UeiMatchFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentView = inflater.inflate(R.layout.fragment_uei_match,
                container, false);
        return parentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(parentView);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AirConSDKManager.initialize(this.mActivity);
        initBar();
    }

    private void initBar() {
        Bundle args = getArguments();
        String strLibCode = args.getString("libCodes");
        gwID=args.getString("gwID");
        devID=args.getString("devID");
        callBackId=args.getString("callBackId");
        libCodes = strLibCode.split(",");
        if (libCodes != null && libCodes.length > 0) {
            curLibCodeIndex = 0;
        }
        this.mActivity.resetActionMenu();
        ueiCommEpdata=new UeiCommonEpdata(gwID,devID,"14");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(true);
        getSupportActionBar().setIconText(R.string.remote_control);
        String title=getString(cc.wulian.app.model.device.R.string.uei_html_match_title);
        getSupportActionBar().setTitle(title);


    }

    @Override
    public void onStart() {
        super.onStart();
        if(StringUtil.isNullOrEmpty(airData)){
            btnLibCode_onclick();
        }
    }

    private void initView(View paramView) {
        paramView.findViewById(R.id.modebtn).setOnClickListener(this);
        paramView.findViewById(R.id.fanspeedbtn).setOnClickListener(this);
        paramView.findViewById(R.id.lin_ac_add).setOnClickListener(this);
        paramView.findViewById(R.id.lin_ac_sub).setOnClickListener(this);
        paramView.findViewById(R.id.iv_controltype).setOnClickListener(this);
        paramView.findViewById(R.id.swing_up_downbtn).setOnClickListener(this);
        paramView.findViewById(R.id.swing_left_rightbtn).setOnClickListener(this);
        btnPreLibCode= (Button) paramView.findViewById(R.id.btnPreLibCode);
        btnNextLibCode= (Button) paramView.findViewById(R.id.btnNextLibCode);
        btnPreLibCode.setOnClickListener(this);
        btnNextLibCode.setOnClickListener(this);
        paramView.findViewById(R.id.btnOk).setOnClickListener(this);
        tvMatchDesc= (TextView) paramView.findViewById(R.id.tvMatchDesc);
        airStatus = new AirStateStandard(AirStateStandard.defaultState);
        airStatus.setIndex("0");
        holderView = new AirStatusHolderView(this.mActivity);
        holderView.setIsShowAll(true);
        holderView.modecommlayout = (LinearLayout) parentView.findViewById(R.id.modecommlayout);
        holderView.modecommimage = (ImageButton) parentView.findViewById(R.id.modecommimage);
        holderView.modecommName1 = (TextView) parentView.findViewById(R.id.modecommName1);
        holderView.modecommName2 = (TextView) parentView.findViewById(R.id.modecommName2);
        holderView.modecomplexlayout = (LinearLayout) parentView.findViewById(R.id.modecomplexlayout);
        holderView.windspeedImage = (ImageButton) parentView.findViewById(R.id.windspeedImage);
        holderView.airmodeimage = (ImageButton) parentView.findViewById(R.id.airmodeimage);
        holderView.temperaturetv = (TextView) parentView.findViewById(R.id.temperaturetv);
        holderView.temperatureunittv = (TextView) parentView.findViewById(R.id.temperatureunittv);
        holderView.wind_left_rightimage = (ImageButton) parentView.findViewById(R.id.wind_left_rightimage);
        holderView.wind_up_downimage = (ImageButton) parentView.findViewById(R.id.wind_up_downimage);
//        holderView.customernametv= (TextView) parentView.findViewById(R.id.customernametv);
        holderView.airspeedflagimage=(ImageButton) parentView.findViewById(R.id.airspeedflagimage);
        holderView.FillData(airStatus);
    }

    @Override
    public void onClick(View view) {
        if(view.getTag()!=null&&!StringUtil.isNullOrEmpty(view.getTag().toString())){
            int keyid=-1;
            if(map_btnTagAirid!=null&&map_btnTagAirid.size()>0){
                String tag=view.getTag().toString();
                if(map_btnTagAirid.containsKey(tag)){
                    keyid=map_btnTagAirid.get(tag);
                }
            }
            if(keyid==-1){
//                Toast.makeText(this.getActivity(), "未找到对应的键值！", Toast.LENGTH_SHORT).show();
                return;
            }
            int result= ACEService.ACProcessKey(Integer.valueOf(keyid));
            if(ResultCode.SUCCESS==result){
                Log.d(log_tag, "ACEService.ACProcessKey 执行成功！");
                getSendData();
                ACEService_ACGetKeys();
                holderView.FillData(airStatus);
                if(!StringUtil.isNullOrEmpty(sendData)){
                    String codeData=ueiCommEpdata.getEpdataForAirRun(sendData);
                    ueiCommEpdata.sendCommand12(getContext(),codeData);//执行12命令
                }
            }else{
                Log.d(log_tag, "ACEService.ACProcessKey 执行失败！");
            }
        }else {
            switch (view.getId()) {
                case R.id.btnNextLibCode:{
                    curLibCodeIndex++;
                    btnLibCode_onclick();
                }break;
                case R.id.btnPreLibCode:{
                    curLibCodeIndex--;
                    btnLibCode_onclick();
                }break;
                case R.id.btnOk:{
                    saveAirData();
                }break;
                default:{

                }break;

            }
        }
    }
    /**
     * 检索冷气设备的IR数据
     */
    private void getSendData(){
        byte[] irData = ACEService.ACGetLastKeyPatternData();
        if(irData!=null&&irData.length>0){
            sendData=bytesToHexString(irData);
            Log.d(log_tag, "ACEService.ACGetLastKeyPatternData 执行成功！\r\nsendData="+sendData);
        }else{
            Log.d(log_tag, "ACEService.ACGetLastKeyPatternData 执行失败！");
        }
    }
    /**
     * 数组转换成十六进制字符串
     * @param bArray
     * @return HexString
     */
    private final String bytesToHexString(byte[] bArray) {
        StringBuffer bytesb=new StringBuffer();
        StringBuffer sb = new StringBuffer(bArray.length);
        String sTemp;
        for (int i = 0; i < bArray.length; i++) {
            bytesb.append(bArray[i]+",");
            sTemp = Integer.toHexString(0xFF & bArray[i]);
            if (sTemp.length() < 2)
                sb.append(0);
            sb.append(sTemp);
        }
        Log.d(log_tag, "byte[]="+bytesb);
        return sb.toString();
    }
    /**
     * 执行ACEService.ACGetKeys
     */
    private void ACEService_ACGetKeys(){
        List<AirConState> states = new ArrayList<AirConState>();
        List<AirConFunction> aFunctions = new ArrayList<AirConFunction>();
        List<AirConWidgetStatus> status = new ArrayList<AirConWidgetStatus>();
        int result = ACEService.ACGetKeys(states, status, aFunctions);
        if (result == ResultCode.SUCCESS && aFunctions != null && states != null) {
            Log.d(log_tag, "ACEService.ACGetKeys 执行成功！");
            initIRDevice(aFunctions);
            AirConState[] allStates = new AirConState[states.size()];
            if(allStates.length > 0) {
                states.toArray(allStates);
            }
            if(states != null) {
                _states = states.toString();
            }
            for(AirConFunction aircon:aFunctions){
                Log.d("air_fun", "aircon.Id="+aircon.Id+" aircon.Name="+aircon.Name+" aircon.funtype="+aircon.getFunctionType());
            }
            setAirStatus(allStates);
        }else{
            Log.d(log_tag, "ACEService.ACGetKeys 执行失败！");
        }
    }
    /**
     * 初始化设备对象
     * @param aFunctions
     */
    private void initIRDevice(List<AirConFunction> aFunctions){
        if(airConIRDevice==null){
            String [] functions = new String[aFunctions.size()];
            for (int i = 0; i < aFunctions.size(); i++) {
                AirConFunction af = aFunctions.get(i);
                if (af != null) {
                    functions[i] = af.Name;
                    this._currentAirConFunctions.add(af);
                }
            }
            airConIRDevice = new com.uei.control.AirConDevice(0, "Z", argdevicecode,
                    "",
                    "",
                    this._currentAirConFunctions,
                    Base64.decode(this.airData, Base64.DEFAULT),
                    this._currentAirconStates);
            airConIRDevice.Codeset = this.airData;
            Log.d(log_tag, "airConIRDevice已初始化！");
        }
    }
    /**
     * 设置airStatus，并且刷新界面
     */
    private void setAirStatus(final AirConState[] states){
        this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    if(states != null) {
                        UeiAirStateSwap stateswap=new UeiAirStateSwap(airConIRDevice, states);
                        map_btnTagAirid=stateswap.GetBtnTagsValue();
                        AirStateStandard airstateStandard=stateswap.ConvertToLocalState();
                        airStatus.setFanspeed(airstateStandard.getFanspeed());
                        airStatus.setMode(airstateStandard.getMode());
                        airStatus.setStatus(airstateStandard.getStatus());
                        airStatus.setSwing_left_right(airstateStandard.getSwing_left_right());
                        airStatus.setSwing_up_down(airstateStandard.getSwing_up_down());
                        airStatus.setTemperature(airstateStandard.getTemperature());
                        airStatus.setTemperature_unit(airstateStandard.getTemperature_unit());
                        holderView.FillData(airStatus);
                        if(!StringUtil.isNullOrEmpty(airStatus.getCustomName())){
                            holderView.customernametv.setVisibility(View.VISIBLE);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void btnLibCode_onclick(){
        if(curLibCodeIndex<=0){
            curLibCodeIndex=0;
            btnNextLibCode.setEnabled(true);
            btnPreLibCode.setEnabled(false);
        }else if(curLibCodeIndex>=libCodes.length-1){
            curLibCodeIndex=libCodes.length-1;
            btnNextLibCode.setEnabled(false);
            btnPreLibCode.setEnabled(true);
        }else {
            btnNextLibCode.setEnabled(true);
            btnPreLibCode.setEnabled(true);
        }
        String curLibCode=libCodes[curLibCodeIndex];
        if(!StringUtil.isNullOrEmpty(curLibCode)){
            String descTxt=String.format("%s/%s %s",curLibCodeIndex+1,libCodes.length,curLibCode);
            tvMatchDesc.setText(descTxt);
            downAirCode(curLibCode);
        }
    }
    private void downAirCode(final String libCode){
        getDialogManager().showDialog(Down_AirData, getActivity(), "加载空调码库...", null);
        //下载之前清空所有的变量
        _currentAirconStates="";
        arrBytedata=null;
        isStart=false;
        sendData="";
        airConIRDevice=null;
        Thread thread = new Thread() {
            @Override
            public void run() {
                UeiAirDataUtil ueiAriData = new UeiAirDataUtil();
                ueiAriData.setIsFirstReadLocal(true);
                ueiAriData.setIsSvaeFile(false);
                ueiAriData.setdevID(devID);
                airData = ueiAriData.getAirData(libCode);
                if (StringUtil.isNullOrEmpty(airData)) {
//                    String msg="获取空调码库失败！";
                    String msg=getString(cc.wulian.app.model.device.R.string.uei_remote_control_failed_get_library);
                    Log.d(log_tag, msg);
                    Toast.makeText(UeiMatchFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(log_tag, "获取空调码库成功！");
                    //启动ACEService
                    Log.d(log_tag, "_currentAirconStates="+_currentAirconStates);
                    arrBytedata = Base64.decode(airData, Base64.DEFAULT);
                    int startflag=-1;
                    try {
                        startflag = ACEService.ACEngineStart(arrBytedata, _currentAirconStates);
                    } catch (Exception ex) {
                        Log.e(log_tag, ex.toString());
                    }
                    if(ResultCode.SUCCESS==startflag){
                        Log.d(log_tag, "ACEService.ACEngineStart启动成功!");
                        isStart=true;
                        getSendData();
                        ACEService_ACGetKeys();
                    }else{
                        isStart=false;
                        Log.d(log_tag, "ACEService.ACEngineStart启动失败! startflag=="+startflag);
                    }

                }
                getDialogManager().dimissDialog(Down_AirData,0);
            }
        };
        thread.start();
    }

    /*=============下面是保存的逻辑============*/
    boolean isSaving=false;
    private void saveAirData() {
        if (isSaving) {
            return;
        }

        if(curLibCodeIndex>=0&&curLibCodeIndex<libCodes.length&&UeiMatchFragment.pWebview!=null){
            String libcode=libCodes[curLibCodeIndex];
            JsUtil.getInstance().execCallback(pWebview, callBackId, libcode, JsUtil.OK, true);
            this.getActivity().finish();
        }
    }

}
