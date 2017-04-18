package cc.wulian.smarthomev5.fragment.uei;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import java.util.List;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UEIEntityManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.TargetConfigure;

import static cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control.curDeleteFlag;
import static cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control.flag_Clear;

public class SettingFragment extends WulianFragment implements OnClickListener, ICommand406_Result {

	private  String tag="SettingFragment";
	private  String settintType="";
	private View parentView;
	private String brandName="";
	private String deviceType="";
	private String deviceKey="";
	private String devID;
	private String gwID;
	private String ep;
	private String proName;
	private String proCode;
	private Button tvOperators_btn;
	private ImageView tvOperators_iv;
	private Button delete_btn;
	private Button brandName_btn;
	private LinearLayout tvOperatorslayout;
	private LinearLayout ueiEditLayout;
	private Button ueiDeleteButton;

	private WLDialog dialog_update;
	private String LOCK_KEY_CLEARITEMS="LOCK_KEY_CLEARITEMS";
	private Command406_DeviceConfigMsg command406=null;
	UEIEntity currUEI=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args=getArguments();
		if(args!=null){
			settintType = args.getString("settingType");
			devID=args.getString("devID");
			gwID=args.getString("gwID");
			if ("ueiEdit".equals(settintType)){
				brandName=args.getString("brandName");
				deviceType=args.getString("deviceType");
				deviceKey=args.getString("deviceKey");
				ep=args.getString("ep");
				proCode=args.getString("proCode");
				proName=args.getString("proName");
			}
		}
		command406=new Command406_DeviceConfigMsg(this.mActivity);
		command406.setConfigMsg(this);
		command406.setDevID(devID);
		command406.setGwID(gwID);
		initBar();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.activity_ueisettings, container, false);
		return parentView;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView(view);
	}

	private void initBar(){
		this.mActivity.resetActionMenu();
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    getSupportActionBar().setDisplayIconEnabled(true);
	    getSupportActionBar().setDisplayIconTextEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
	    getSupportActionBar().setDisplayShowMenuEnabled(false);
	    getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		if ("ueiEdit".equals(settintType)){
			getSupportActionBar().setTitle(getString(R.string.device_edit));
		}else {
			getSupportActionBar().setTitle(getString(R.string.common_setting));
		}
	    getSupportActionBar().setIconText(getString(R.string.about_back));
	    getSupportActionBar().setDisplayShowMenuTextEnabled(false);
	}
	private void initView(View view){
		ueiEditLayout=(LinearLayout) view.findViewById(R.id.uei_remote_control_edit);
		ueiDeleteButton=(Button) view.findViewById(R.id.uei_remote_control_delete);
		if ("ueiEdit".equals(settintType)){
			ueiEditLayout.setVisibility(View.VISIBLE);
			ueiDeleteButton.setVisibility(View.GONE);

			tvOperators_btn=(Button) view.findViewById(R.id.tvOperators_btn);
			tvOperators_iv=(ImageView) view.findViewById(R.id.tvOperators_iv);
			delete_btn=(Button) view.findViewById(R.id.delete_btn);
			brandName_btn=(Button) view.findViewById(R.id.brandName_btn);

			tvOperatorslayout=(LinearLayout) view.findViewById(R.id.tvOperatorslayout);
			tvOperators_btn.setOnClickListener(this);
			tvOperators_iv.setOnClickListener(this);
			delete_btn.setOnClickListener(this);
			brandName_btn.setOnClickListener(this);

			if(deviceType.equals(WL_23_IR_Resource.Model_C)){
				tvOperatorslayout.setVisibility(View.VISIBLE);
			}else{
				tvOperatorslayout.setVisibility(View.GONE);
			}
			brandName_btn.setText(brandName);
			tvOperators_btn.setText(proName);
		}else {
			ueiEditLayout.setVisibility(View.GONE);
			ueiDeleteButton.setVisibility(View.VISIBLE);

			ueiDeleteButton.setOnClickListener(this);
		}

	}

	@Override
	public void onClick(View v) {
		if ("ueiSetting".equals(settintType)){
			if (v.getId() == R.id.uei_remote_control_delete) {
				deleteStore();
			}
			return;
		}else if(currUEI==null){
//			Toast.makeText(this.mActivity, "未查找到该UEI信息！", Toast.LENGTH_SHORT).show();
			return;
		}
		switch (v.getId()) {
		case R.id.tvOperators_btn:
		case R.id.tvOperators_iv: {
			String url="";
			if(WL_23_IR_Control.isUsePlugin){
				url= Preference.getPreferences().getUeiTopBox_Operators();
			}else {
				url="file:///android_asset/uei/setstation.html";
			}
			Intent intent=new Intent(DeviceDetailsActivity.instance,Html5PlusWebViewActvity.class);
			intent.putExtra(Html5PlusWebViewActvity.KEY_URL, url);
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, ep);
//			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPTYPE, epType);
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
			SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
			DeviceDetailsActivity.instance.startActivity(intent);
		}
			break;
		case R.id.delete_btn:
			delete();
			break;
		case R.id.brandName_btn:
			updateBrandName();
			break;
		default:
			break;
		}
	}
	/**
	 * 重新命名
	 */
	private void updateBrandName(){
		WLDialog.Builder builder = new Builder(this.mActivity);
		String title=getString(cc.wulian.app.model.device.R.string.uei_remote_control_name_settings);
		builder.setContentView(R.layout.aboutus_feedback_send_comments_dialog)
				.setTitle(title)
				.setPositiveButton(
						R.string.common_ok)
				.setNegativeButton(
						R.string.cancel)
				.setDismissAfterDone(false).setListener(new MessageListener() {

					@SuppressLint("CutPasteId")
					@Override
					public void onClickPositive(View contentViewLayout) {
						EditText editText = (EditText) contentViewLayout
								.findViewById(R.id.aboutus_feedback_send_comments_et);
//						TextView msgtv=(TextView) contentViewLayout
//								.findViewById(R.id.msgtv);
//						msgtv.setVisibility(View.GONE);
						String newName=editText.getText().toString().trim();
						if(!StringUtil.isNullOrEmpty(newName)){
							String old_Data=currUEI.getValue();
							Log.d("SettingFragment", " 更改之前："+old_Data);
							JSONObject jsonValue =JSONObject.parseObject(old_Data);
							jsonValue.put("nm", newName);
							String new_data=jsonValue.toJSONString();
							command406.SendCommand_Update(deviceKey, new_data);
							dialog_update.dismiss();
						}
						else{
//							msgtv.setVisibility(View.VISIBLE);
//							String errormsg=getString(R.string.home_monitor_cloud_1_not_null);
//							msgtv.setText(errormsg);
						}
					}
					public void onClickNegative(View contentViewLayout) {
						dialog_update.dismiss();
					}

				});
		dialog_update = builder.create();
		dialog_update.show();
	}
	/**
	 * 删除操作
	 */
	private void delete(){
		WLDialog.Builder builder = new WLDialog.Builder(this.mActivity);
		builder.setTitle(cc.wulian.smarthomev5.R.string.device_ir_delete);
		LinearLayout dialog_delete=(LinearLayout) LayoutInflater.from(this.mActivity).inflate(cc.wulian.smarthomev5.R.layout.common_dialog_delete, null);
		TextView textView=(TextView) dialog_delete.findViewById(cc.wulian.smarthomev5.R.id.delete_msg_tv);								
		textView.setText(getString(cc.wulian.app.model.device.R.string.uei_delete_remote_control_hint));
		builder.setContentView(dialog_delete);
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				String strDeling=getString(cc.wulian.app.model.device.R.string.uei_delete_remote_control_doing);
				mDialogManager.showDialog(LOCK_KEY_CLEARITEMS, SettingFragment.this.mActivity, strDeling, null);
				//当前删除只是非学习、非空调的码库
				if(currUEI.getMode().equals("1")&&!currUEI.getDeviceType().equals("Z")){
					String codesNum=currUEI.getDeviceCode().replace(currUEI.getDeviceType(), "");
					String deviceTypeString=UeiCommonEpdata.matchModelToString(currUEI.getDeviceType());
					String codesNumString=toHexString(codesNum);
					String epdata="OA00040C"+deviceTypeString+codesNumString;
					UeiCommonEpdata ueiCommand=new UeiCommonEpdata(gwID,devID,ep);
					ueiCommand.sendCommand12(getContext(),epdata);
				}else{
					//不做处理
				}
				command406.SendCommand_Delete(currUEI.getKey(), currUEI.getValue());
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		WLDialog mdeleteBrandItemDialog = builder.create();
		mdeleteBrandItemDialog.show();
	}

	private void deleteStore(){
		WLDialog.Builder builder = new WLDialog.Builder(this.mActivity);
		builder.setTitle(cc.wulian.smarthomev5.R.string.device_songname_refresh_title);
		LinearLayout dialog_delete = (LinearLayout) LayoutInflater.from(DeviceDetailsActivity.instance).inflate(cc.wulian.smarthomev5.R.layout.common_dialog_delete, null);
		TextView textView = (TextView) dialog_delete.findViewById(cc.wulian.smarthomev5.R.id.delete_msg_tv);

		textView.setText(getString(cc.wulian.app.model.device.R.string.uei_mainlist_clear_code));
		builder.setContentView(dialog_delete);
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				mDialogManager.showDialog(LOCK_KEY_CLEARITEMS, SettingFragment.this.getContext(), getString(cc.wulian.app.model.device.R.string.uei_mainlist_clear_codeing), null);

				command406.SendCommand_Update("currentIndex", "0000");
				command406.SendCommand_ClearV2("");
				curDeleteFlag = flag_Clear;
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		WLDialog mdeleteBrandItemDialog = builder.create();
		mdeleteBrandItemDialog.show();
	}
	@Override
	public void onStart() {
		super.onStart();
		if(command406!=null&&!StringUtil.isNullOrEmpty(deviceKey)){
			command406.SendCommand_Get(deviceKey);
		}
	}
	
	@Override
	public void Reply406Result(Command406Result result) {
		if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
			Log.d(tag, "result.mode="+result.getMode()+" result.data="+result.getData());
		}

		if(result!=null&&result.getKey().equals(deviceKey)){
			if(result.getMode().equals(Command406_DeviceConfigMsg.mode_delete)){
				mDialogManager.dimissDialog(LOCK_KEY_CLEARITEMS, 0);
				Intent data=new Intent();
				data.putExtra("isDelete", true);
				this.mActivity.setResult(1, data);
				this.mActivity.finish();
			}else if(result.getMode().equals(Command406_DeviceConfigMsg.mode_get)){
				currUEI=UEIEntityManager.ConvertToUEIEntity(this.mActivity, result.getAppID(), result);
			}else if(result.getMode().equals(Command406_DeviceConfigMsg.mode_update)){
				currUEI=UEIEntityManager.ConvertToUEIEntity(this.mActivity, result.getAppID(), result);

			}else if (result.getMode().equals(Command406_DeviceConfigMsg.mode_clear)){
				if (curDeleteFlag.equals(flag_Clear)) {
					UeiCommonEpdata ueiCommand = new UeiCommonEpdata(gwID, devID, ep);
					ueiCommand.sendCommand12(getContext(),"0A00020903");
					mDialogManager.dimissDialog(LOCK_KEY_CLEARITEMS, 0);
//				String msg="清空完成！";
					String msg = getString(cc.wulian.smarthomev5.R.string.main_process_success);
					if (!StringUtil.isNullOrEmpty(msg)) {
						Toast.makeText(mActivity, msg, Toast.LENGTH_SHORT).show();
					}
					curDeleteFlag = "";
				}
				this.mActivity.setResult(3);
				this.mActivity.finish();
			}
			if(currUEI!=null){
				brandName_btn.setText(currUEI.getDisplayName());
				tvOperators_btn.setText(currUEI.getProName());
			}
		}
	}

	@Override
	public void Reply406Result(List<Command406Result> results) {
		if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
			Log.d(tag, "results.lenth="+results.size());
		}
//		if(results!=null&&results.size()>0){
//			Command406Result result=results.get(0);
//			if(result.getKey().equals(deviceKey)){
//				currUEI=UEIEntityManager.ConvertToUEIEntity(this.mActivity, result.getAppID(), result);
//			}
//		}
	}
	private String toHexString(String s) 
	{ 
		String str = "";
		for (int i = 0; i < s.length(); i++) {
			int ch = Integer.parseInt(s.charAt(i)+"");
			String s4 = Integer.toHexString(ch);
			if(s4.length()<2){
				s4="0"+s4;
			}
			str = str + s4;
		}
		return str.toUpperCase();
	}
}
