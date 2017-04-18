package cc.wulian.smarthomev5.fragment.uei;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.File;

import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.uei.ExpandPopupWindow;
import cc.wulian.smarthomev5.activity.uei.KeyboardPopupWindow;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import static cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control.pluginName;

public class TVRemoteControlFragment extends WulianFragment implements OnClickListener{
	/*下面的任务尚未完成：
	 * 1.拓展按键面板（ExpandPopupWindow）功能未编写
	 * */
	private View parentView;
	private KeyboardPopupWindow keyboardPopupWindow;
	private ExpandPopupWindow expandPopupWindow;
	LinearLayout lin_TV_number=null;
	UeiUiArgs args_value=null;
	private LinearLayout virtualKeyLayout;
	private VirtualKeyButton virkeyBtn;
	UEIEntity uei=null;
	private LinearLayout lin_TV_expand;
	private boolean isShowBar=true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			args_value=bundle.getParcelable("args");
			uei=args_value.ConvertToEntity();
			isShowBar=bundle.getBoolean("isShowBar",true);
		}
		initBar();
	}
	@Override
	public void onResume() {
		super.onResume();
		String uei_edit_save=SmarthomeFeatureImpl.getData("UEI_CUSSAVE_SUC","0");
		if(uei_edit_save.equals("1")){
			TVRemoteControlFragment.this.getActivity().finish();
		}
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_tv_remotecontrol, container, false);
		return parentView;
	}
	@Override
	public void onViewCreated(View paramView, Bundle paramBundle){
	    super.onViewCreated(paramView, paramBundle); 
	    initView(paramView);
	  }

	 private void initBar() {
		 if(isShowBar){
			 this.mActivity.resetActionMenu();
			 getSupportActionBar().setDisplayHomeAsUpEnabled(true);
			 getSupportActionBar().setDisplayIconEnabled(true);
			 getSupportActionBar().setDisplayIconTextEnabled(true);
			 getSupportActionBar().setDisplayShowTitleEnabled(true);
			 getSupportActionBar().setDisplayShowMenuEnabled(false);
			 getSupportActionBar().setDisplayShowMenuTextEnabled(false);
			 getSupportActionBar().setTitle(uei.getDisplayName());
			 if(args_value.getViewMode()==1){
				 getSupportActionBar().setIconText(getString(R.string.about_back));
				 getSupportActionBar().setDisplayShowMenuTextEnabled(true);
				 getSupportActionBar().setRightIconText(R.string.common_ok);
				 getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
					 @Override
					 public void onClick(View v) {
						 String epData=TVRemoteControlFragment.this.virkeyBtn.getCurrEpData();
						 Intent intent=new Intent();
						 intent.putExtra("epdata", epData);
						 mActivity.setResult(1, intent);
						 mActivity.finish();
					 }
				 });

			 }else if(args_value.getViewMode()==0){
				 getSupportActionBar().setIconText(getString(R.string.nav_device_title));
				 if ("2".equals(uei.getMode())){
					 SmarthomeFeatureImpl.setData("UEI_CUSSAVE_SUC","0");
					 getSupportActionBar().setDisplayShowMenuTextEnabled(true);
					 getSupportActionBar().setRightIconText(R.string.device_edit);
					 getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
						 @Override
						 public void onClick(View v) {
							 if(WL_23_IR_Control.isUsePlugin){
								 getPlugin("NormalLearn.html");
							 }else {
								 String strUri = "file:///android_asset/uei/NormalLearn.html";
								 Intent intent = new Intent(DeviceDetailsActivity.instance, Html5PlusWebViewActvity.class);
								 intent.putExtra(Html5PlusWebViewActvity.KEY_URL, strUri);

								 JSONObject keysetObject = JSON.parseObject(uei.getValue());
								 JSONObject relearnLearnDicJsonObject = new JSONObject();
								 relearnLearnDicJsonObject.put("key", uei.getKey());
								 relearnLearnDicJsonObject.put("nm", uei.getBrandCusName());
								 relearnLearnDicJsonObject.put("value", keysetObject);
								 SmarthomeFeatureImpl.setData("relearnLearnDic", relearnLearnDicJsonObject.toJSONString());
								 DeviceDetailsActivity.instance.startActivity(intent);
							 }
						 }
					 });
				 }
			 }
		 }
	 }
	 
	 private void initView(View paramView){
		
		expandPopupWindow = new ExpandPopupWindow(mActivity);
		//使用VirtualKeyButton统一为按钮注册事件
		virtualKeyLayout=(LinearLayout) paramView.findViewById(R.id.virtualKeyLayout);
		paramView.findViewById(R.id.lin_TV_number).setOnClickListener(this);
		lin_TV_expand=(LinearLayout) paramView.findViewById(R.id.lin_TV_expand);
		lin_TV_expand.setOnClickListener(this);
		lin_TV_expand.setEnabled(false);
		virkeyBtn=new VirtualKeyButton(this.mActivity);
		virkeyBtn.setArgs(args_value);
		virkeyBtn.setKeyFlag("80");
		if(this.args_value.getViewMode()==0){
			virkeyBtn.setTVLight((ImageView) paramView.findViewById(R.id.iv_TV_light));
		}else{
			paramView.findViewById(R.id.iv_TV_light).setVisibility(View.GONE);
		}
		virkeyBtn.RegiestVirtualKeyEvent(virtualKeyLayout);
		keyboardPopupWindow = new KeyboardPopupWindow(mActivity,virkeyBtn);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.lin_TV_number:
			UeiCommonEpdata ueiCommon=new UeiCommonEpdata(args_value.getGwID(), args_value.getDevID(), args_value.getEp());
			ueiCommon.sendCommand12(getContext(),"00010B");
            keyboardPopupWindow.showAtLocation(parentView.findViewById(R.id.lin_rc), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.lin_TV_expand:
            expandPopupWindow.showAtLocation(parentView.findViewById(R.id.lin_rc), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		default:
			break;
		}		
	}

	// 获取插件
	private void getPlugin(final String entryPager) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm = PluginsManager.getInstance();
				pm.getHtmlPlugin(mActivity, pluginName, new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file = new File(model.getFolder(), entryPager);
						String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
						if (file.exists()) {
							uri = "file:///" + file.getAbsolutePath();
						} else if (LanguageUtil.isChina()) {
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}

						JSONObject keysetObject = JSON.parseObject(uei.getValue());
						JSONObject relearnLearnDicJsonObject = new JSONObject();
						relearnLearnDicJsonObject.put("key", uei.getKey());
						relearnLearnDicJsonObject.put("nm", uei.getBrandCusName());
						relearnLearnDicJsonObject.put("value", keysetObject);
						SmarthomeFeatureImpl.setData("relearnLearnDic", relearnLearnDicJsonObject.toJSONString());
						IntentUtil.startHtml5PlusActivity(mActivity, uri);
						DeviceDetailsActivity.instance.finish();
						mActivity.finish();
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if (hint != null && hint.length() > 0) {
							Handler handler = new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mActivity, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}
}
