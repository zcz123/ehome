package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * float(单位KG)
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_SCALE }, category = Category.C_HEALTH)
public class WL_45_Scale extends SensorableDeviceImpl {
	private static final String UNIT_KG = " KG";
	private static final int BIG_NORMAL_D = R.drawable.device_banlance_normal_big;
	private TextView mDataTextvTextView;
	private String pluginName = "electronic-scale.zip";
	
	public WL_45_Scale(Context context, String type) {
		super(context, type);
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		drawables[0] = getDrawable(BIG_NORMAL_D);
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
//		sb.append(isNull(epData) ? UNIT_NA : epData);
		sb.append(isNull(epData) ? "00" : epData);
		sb.append(UNIT_KG);
		return sb;
	}

	@SuppressWarnings("deprecation")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		LinearLayout layout = new LinearLayout(getContext());
		layout.setGravity(Gravity.CENTER);
		layout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));

		LinearLayout scaleLayout = new LinearLayout(getContext());
		scaleLayout.setGravity(Gravity.RIGHT);
		scaleLayout.setBackgroundDrawable(getStateBigPictureArray()[0]);

		TextView textView = new TextView(getContext());
		textView.setId(R.id.dev_state_textview_0);
		textView.setTextColor(Color.BLACK);
		textView.setTypeface(Typeface.DEFAULT_BOLD);
		textView.setTextSize(16f);
		scaleLayout.addView(textView);

		layout.addView(scaleLayout);
		return layout;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mDataTextvTextView = (TextView) view
				.findViewById(R.id.dev_state_textview_0);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		mDataTextvTextView.setText(parseDataWithProtocol(epData));
	}

	@Override
	public boolean isLinkControl() {
		return false;
	}

	@Override
	public String unit(String ep,String epType) {
		return UNIT_KG;
	}

	@Override
	public String unitName() {
		return mResources.getString(R.string.device_weight);
	}

	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				getPlugin();
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),model.getEntry());
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						
						Intent intent= new Intent();	
						intent.setClass(mContext, Html5PlusWebViewActvity.class);
						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
						mContext.startActivity(intent);
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if(hint!=null&&hint.length()>0){
							Handler handler=new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}
	
}