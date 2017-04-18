package cc.wulian.smarthomev5.fragment.uei;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UEIEntity_Air;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.entity.uei.UeiVirtualBtn;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl.Constants;
import cc.wulian.smarthomev5.tools.DownUpMenuList;
import cc.wulian.smarthomev5.tools.DownUpMenuList.DownUpMenuItem;

/**
 * 为虚拟机键盘提供方法</br>
 * 1.注册事件，发送NetSDK.sendDevMsg</br>
 * 2.设置按钮是否可用；</br>
 * 3.按键震动</br>
 * 4.找出拓展按钮；---该项尚未开发
 * @author yuxiaoxuan
 * @date 2016年7月12日10:50:57
 */
public class VirtualKeyButton {	
	private Vibrator mVibrator01;  //声明一个振动器对象 
	private UeiCommonEpdata commnEpdata;
	private List<UeiVirtualBtn> virKeyList=null;
	private UEIEntity uei=null;
	private Context mcontext;
	private Activity mActivity;
	public VirtualKeyButton(Activity mActivity){
		this.mActivity=mActivity;
		this.mcontext=mActivity;
		mVibrator01 = ( Vibrator ) MainApplication.getApplication().getSystemService(Service.VIBRATOR_SERVICE);  
		commnEpdata=new UeiCommonEpdata();
	}
	
	private String keyFlag;
    public String getKeyFlag() {
		return keyFlag;
	}
	public void setKeyFlag(String keyFlag) {
		this.keyFlag = keyFlag;
	}
	private UeiUiArgs args=null;
	
	public UeiUiArgs getArgs() {
		return args;
	}
	public void setArgs(UeiUiArgs args) {
		this.args = args;
		commnEpdata.setDevID(this.args.getDevID());
		commnEpdata.setEp(this.args.getEp());
		commnEpdata.setGwID(this.args.getGwID());
		
	}
	public ImageView TVLight=null;
	
	public ImageView getTVLight() {
		return TVLight;
	}
	public void setTVLight(ImageView tVLight) {
		TVLight = tVLight;
	}
	private String currEpData="";
	public String getCurrEpData(){
		return currEpData;
	}
	public void setCurrEpData(String epData){
		this.currEpData=epData;
	}
	
	public void SendUeiKey(String keyCode){
		String epData=getEpdata(keyCode);
		commnEpdata.sendCommand12(mcontext,epData);
	}
	
    public View.OnClickListener virtualKey_OnClick_fordevice=new View.OnClickListener() {
		@Override
		public void onClick(View virtualKey) {
			if(virtualKey.getTag()!=null){
				virtualKey.setSelected(false);
				String keyCode=virtualKey.getTag().toString();
				vibratorLinght();
				SendUeiKey(keyCode);
			}
		}
	};
	public void vibratorLinght(){
		mVibrator01.vibrate(300);
		showLight();
	}
	public View.OnLongClickListener virtualKey_OnLongClick_fordevice=new View.OnLongClickListener() {

		@Override
		public boolean onLongClick(View virtualKey) {
			UeiStudyAgain ueiStudyAgain=new UeiStudyAgain(VirtualKeyButton.this.mcontext,VirtualKeyButton.this.args);
			ueiStudyAgain.SetMyActivity(VirtualKeyButton.this.mActivity);
			String keyCode=virtualKey.getTag().toString();
			String studyCode=getLeanerId(keyCode);//通过学习得到的键
			ueiStudyAgain.setStudyCode(studyCode);
			ueiStudyAgain.BeginStudy(virtualKey);
			return true;
		}
	};
	public View curSelectedView=null;
	public View.OnClickListener virtualKey_OnClick_forhouse=new View.OnClickListener() {
		@Override
		public void onClick(View virtualKey) {
			if(virtualKey.getTag()!=null){
				if(curSelectedView!=null){
					curSelectedView.setSelected(false);
				}
				virtualKey.setSelected(true);
				curSelectedView=virtualKey;
				String keyCode=virtualKey.getTag().toString();
				currEpData=getEpdata(keyCode);
				if(StringUtil.isNullOrEmpty(currEpData)){
					virtualKey.setSelected(false);
					Toast.makeText(VirtualKeyButton.this.mcontext, "未学习按键", Toast.LENGTH_SHORT).show();
				}
			}
		}
	};
	/**
	 * 获取EpData
	 * @param keyCode 标准按键码
	 * @return
	 */
	public String getEpdata(String keyCode){
		String epData="";
		String devicemode= uei.getMode();
		if(devicemode.equals("1")){
			epData=commnEpdata.getEpData(args.ConvertToEntity().getDeviceCode(), keyCode, keyFlag);
		}else if(devicemode.equals("2")){//根据模板学习的设备
			String lc=getLeanerId(keyCode);//通过学习得到的键
			epData=commnEpdata.getEpDataForStudy(lc);
		}else{
			Log.d("VirtualKeyButton", "设备类型不正确 deviceType="+devicemode);
		}
		return epData;
	}
	
	
	private String getLeanerId(String keyCode){
		String lc="";
		int intKeyid=-1;
		int intKeyCode=Integer.parseInt(keyCode);
		for(UeiVirtualBtn virBtn:virKeyList){
			intKeyid=Integer.parseInt(virBtn.getKeyid());
			if(intKeyid==intKeyCode){
				lc=virBtn.getLc();
				break;
			}
		}
		return lc;
	}
	public void RegiestVirtualKeyEvent(ViewGroup rootvg){
		virtualKeyEvent(rootvg);
	}
	private void virtualKeyEvent(View view){
		if(view!=null){
			uei=this.args.ConvertToEntity();
			virKeyList=uei.GetVirKeyList();
			boolean isAva=isAvailableKey(view);
			if(view.getTag()!=null){
				if(isAva){
					if(this.args.getViewMode()==0){//设备功能
						view.setOnClickListener(virtualKey_OnClick_fordevice);
						if(uei.getUeiType()==2||uei.getUeiType()==4){
							view.setOnLongClickListener(virtualKey_OnLongClick_fordevice);
						}
					}else if(this.args.getViewMode()==1){//管家功能
						view.setOnClickListener(virtualKey_OnClick_forhouse);
					}
				}else{
					view.setEnabled(false);
				}
			}			
			if(view instanceof ViewGroup){
				ViewGroup vg=(ViewGroup)view;
				if(vg.getChildCount()>0){
					for(int i=0;i<vg.getChildCount();i++){
						virtualKeyEvent(vg.getChildAt(i));
					}
				}
			}
		}
	}
	private boolean isStart = false;
	private int lightCount = 0;
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.what == 1) {
				lightCount ++;
				if(lightCount>6){
					lightCount=0;
					isStart=false;
				}else{
					if(lightCount%2==1){
						TVLight.setImageResource(R.drawable.remote_control_light_2);
					}else{
						TVLight.setImageResource(R.drawable.remote_control_light_1);
					}
				}
			}
		}

	};
	
	private void showLight(){
		if(TVLight==null){
			return;
		}
		isStart = true;
		new Thread() {
			@Override
			public void run() {

				super.run();
				while (isStart) {
					try {
						Message msg = Message.obtain();
						msg.what = 1;
						handler.sendMessage(msg);
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}

		}.start();
	}

	@SuppressWarnings("unused")
	private boolean isAvailableKey(View view){
		boolean isAvai=false;
		if(this.args!=null){
			if(uei.getUeiType()!=3){
				if(virKeyList!=null&&view.getTag()!=null){
					for(UeiVirtualBtn virkey:virKeyList){
						int intKeyid=Integer.parseInt(virkey.getKeyid());
						int intTag=Integer.parseInt(view.getTag().toString());
						if(intKeyid==intTag){
							isAvai=true;
							break;
						}
					}
				}
			}
		}
		return isAvai;
	}
	
	
}
