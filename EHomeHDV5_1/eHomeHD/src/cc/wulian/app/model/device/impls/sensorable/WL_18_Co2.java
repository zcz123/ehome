package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * int(单位PPM) <br/>
 * 350～450ppm：同一般室外环境 <br/>
 * 350～1000ppm：空气清新，呼吸顺畅。 <br/>
 * 1000～2000ppm：感觉空气浑浊，并开始觉得昏昏欲睡。 <br/>
 * 2000～5000ppm：感觉头痛、嗜睡、呆滞、注意力无法集中、心跳加速、轻度恶心。 <br/>
 * 大于5000ppm：可能导致严重缺氧，造成永久性脑损伤、昏迷、甚至死亡。 <br/>
 */
@Deprecated
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_CO2}, 
		category = Category.C_ENVIRONMENT)
public class WL_18_Co2 extends AbstractScanAnimSensorDevice
{
	private static final String UNIT_PPM 								= " PPM";
	
	private static final float PPM_CO2_1000 						= 1000;
	private static final float PPM_CO2_2000 						= 2000;
	
	private static final int 	 SMALL_NORMAL_D 					= R.drawable.device_co2_normal;
	private static final int 	 SMALL_ALARM_D 						= R.drawable.device_co2_alarm;
	private static final int 	 SMALL_MID_D 							= R.drawable.device_co2_mid;
	
	private static final int 	 BIG_NORMAL_D 						= R.drawable.device_co2_normal_big;
	private static final int 	 BIG_ALARM_D 							= R.drawable.device_co2_alarm_big;
	private static final int 	 BIG_MEDIUM_D 						= R.drawable.device_co2_mid_big;
	private final String pluginName="Indepent_environmental.zip";
	
	public WL_18_Co2( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public Drawable getSensorStateSmallIcon(){
		String flag = checkDataRatioFlag();
		int res = SMALL_NORMAL_D;
		if (FLAG_RATIO_NORMAL == flag){
			res = SMALL_NORMAL_D;
		}
		else if (FLAG_RATIO_MID == flag){
			res = SMALL_MID_D;
		}
		else if (FLAG_RATIO_ALARM == flag){
			res = SMALL_ALARM_D;
		}
		return getDrawable(res);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		SpannableStringBuilder sb = new SpannableStringBuilder();
		String flag = checkDataRatioFlag();
		int color = COLOR_CONTROL_GREEN;
		if (FLAG_RATIO_NORMAL == flag){
			color = COLOR_CONTROL_GREEN;
		}
		else if (FLAG_RATIO_MID == flag){
			color = COLOR_NORMAL_ORANGE;
		}
		else if (FLAG_RATIO_ALARM == flag){
			color = COLOR_ALARM_RED;
		}
		sb.append(epData);
		// sb.append(SpannableUtil.makeSpannable(epData, new ForegroundColorSpan(getColor(color))));
		sb.append(UNIT_PPM);
		return sb;
	}

	@Override
	public String checkDataRatioFlag(){
		String flag = FLAG_RATIO_NORMAL;
		int ratio = StringUtil.toInteger(epData);

		// nice
		if (ratio <= PPM_CO2_1000){
			flag = FLAG_RATIO_MID;
		}
		// bad
		else if (ratio > PPM_CO2_1000 && ratio <= PPM_CO2_2000){
			flag = FLAG_RATIO_ALARM;
		}
		// very bad
		else if (ratio > PPM_CO2_2000){
			flag = FLAG_RATIO_ALARM;
		}
		return flag;
	}

	@Override
	protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		final List<MoreMenuPopupWindow.MenuItem> items = super.getDeviceMenuItems(manager);
		final MoreMenuPopupWindow.MenuItem settingItem = new MoreMenuPopupWindow.MenuItem(mContext) {

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
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManager.PluginsManagerCallback() {

					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),"device_18.html");
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
	@Override
	public int getScanStateNormalRes(){
		return BIG_NORMAL_D;
	}

	@Override
	public int getScanStateAlarmRes(){
		return BIG_ALARM_D;
	}

	@Override
	public int getScanStateMediumRes(){
		return BIG_MEDIUM_D;
	}

	@Override
	public void onInitViewState( TextView topView, TextView midView, TextView bottomView ){
		topView.setText(this.parseDataWithProtocol(epData));
	}

	@Override
	public String unit(String ep,String epType) {
		return "PPM";
	}

	@Override
	public String unitName() {
		return "CO2";
	}
	
}