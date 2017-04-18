package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.uei.*;

/**
 * 万能遥控器下的类别资源
 * @author yuxiaoxuan
 * @date 2016年7月8日14:37:12
 */
public class WL_23_IR_Resource {
	public static WL23_ResourceInfo UNKNOWN_RESOURCE = new WL23_ResourceInfo(
			R.drawable.device_unknow, R.string.device_unknow);
	/**
	 * 电视机
	 */
	public static final String Model_T="T";
	/**
	 * 机顶盒
	 */
	public static final String Model_C="C";
	/**
	 * 互联网盒子
	 */
	public static final String Model_N="N";
	
	/**
	 * 空调
	 */
	public static final String Model_Z="Z";
	/**
	 * 投影仪</br>
	 * 投影仪类型前缀其实也是T，为了和电视机做区分，在406中的key的第三个字符存的是"!"
	 */
	public static final String Model_T1="!";
	/**
	 * 功放
	 */
	public static final String Model_A="A";
	/**
	 * CD
	 */
	public static final String Model_D="D";
	/**
	 * DVD
	 */
	public static final String Model_Y="Y";
	/**
	 * VCR
	 */
	public static final String Model_V="V";
	/**
	 * 音频设备
	 */
	public static final String Model_M="M";
	/**
	 * 完全自定义学习
	 */
	public static final String Model_Customer="@";
	
	
	/**
	 * WL_23默认设备类型默认图片及文字资源
	 */
	private static final Map<String,WL23_ResourceInfo> DEFAULT_RESOURCE=new HashMap<>();
	/**
	 * WL_23空调个各种模式下的图片资源
	 */
	private static final Map<String,Integer> AirModeImage=new HashMap<>();
	public static class WL23_ResourceInfo{
		public final int smallIcon;
		public final int name;
		public String strName="";
		
		public WL23_ResourceInfo(int smallIcon,int name){
			this.smallIcon=smallIcon;
			this.name=name;
		}
	}
	public static WL23_ResourceInfo getResourceInfo(String type) {
		WL23_ResourceInfo info = DEFAULT_RESOURCE.get(type);
		if (info == null)
			info = UNKNOWN_RESOURCE;
		return info;
	}
	/**
	 * 获取空调个模式下所用到的图片
	 * @param modetype 空调模式标志</br>
	 * mode:模式</br>
	 * wind:风量</br>
	 * windLR:左右扫风</br>
	 * windUP:上下扫风</br>
	 * @param modeValue 所要取的值
	 * @return 图片资源ID
	 */
	public static int getAirModeImage(String modetype,int modeValue){
		Integer imageid=-1;
		String imageKey=modetype+"_"+modeValue;
		if(AirModeImage.containsKey(imageKey)){
			imageid=AirModeImage.get(imageKey);
		}
		return imageid.intValue();
	}
	static{
		init_DEFAULT_RESOURCE();
		init_AirModeImage();
	}
	
	public static WulianFragment getUeiFragment(String model){
		WulianFragment fragment=null;
		switch (model) {
		case Model_A:
		case Model_D:
		case Model_Y:
		case Model_V:
		case Model_M:
			fragment=new PowerAmplifierRemooteControlFragment();
			break;
		case Model_T:
		case Model_N:
			fragment=new TVRemoteControlFragment();
			break;
		case Model_C:
			fragment=new TopBoxControlFragment();
			break;
		case Model_T1:
			fragment=new ProjectorRemoteControlFragment();
			break;
		case Model_Z:
			fragment=new ACRemoteControlFragment();
			break;
		case Model_Customer:
			fragment=new CustomRemoteControlFragment();
			break;
		default:
			break;
		}
		/*现有的Fragment还有：
		 * DVDRemoteControlFragment
		 * */
		return fragment;
	}
	
	private static void init_DEFAULT_RESOURCE(){
		//电视机

		WL23_ResourceInfo Model_T_Res=new WL23_ResourceInfo(R.drawable.device_tv,cc.wulian.app.model.device.R.string.uei_mainlist_TV);
		DEFAULT_RESOURCE.put(Model_T,Model_T_Res);
		//机顶盒
		WL23_ResourceInfo Model_C_Res=new WL23_ResourceInfo(R.drawable.device_set_top_box,R.string.device_ir_type_stb);
		DEFAULT_RESOURCE.put(Model_C,Model_C_Res);
		//互联网盒子
		WL23_ResourceInfo Model_N_Res=new WL23_ResourceInfo(R.drawable.type_select_net_box,cc.wulian.app.model.device.R.string.uei_mainlist_Internet_box);
		DEFAULT_RESOURCE.put(Model_N,Model_N_Res);
		
		//功放
		WL23_ResourceInfo Model_A_Res=new WL23_ResourceInfo(
						R.drawable.type_select_power_amplifier,cc.wulian.app.model.device.R.string.uei_mainlist_Amplifier);
				DEFAULT_RESOURCE.put(Model_A,Model_A_Res);  		
		//CD
		WL23_ResourceInfo Model_D_Res=new WL23_ResourceInfo(
				R.drawable.type_select_cd,0);
		Model_D_Res.strName="CD";
		DEFAULT_RESOURCE.put(Model_D,Model_D_Res);  
		//DVD
		WL23_ResourceInfo Model_Y_Res=new WL23_ResourceInfo(
				R.drawable.type_select_dvd,0);
		Model_Y_Res.strName="DVD";
		DEFAULT_RESOURCE.put(Model_Y,Model_Y_Res);
		//VCR
		WL23_ResourceInfo Model_V_Res=new WL23_ResourceInfo(
				R.drawable.type_select_vcr,0);
		Model_V_Res.strName="VCR";
		DEFAULT_RESOURCE.put(Model_V,Model_V_Res);
		//音频设备
		WL23_ResourceInfo Model_M_Res=new WL23_ResourceInfo(
				R.drawable.type_select_audio_equipment,cc.wulian.app.model.device.R.string.uei_mainlist_Audio_equipment);
		DEFAULT_RESOURCE.put(Model_M,Model_M_Res);
		
		//空调
		WL23_ResourceInfo Model_Z_Res=new WL23_ResourceInfo(
				R.drawable.device_air_conditioner,
				cc.wulian.app.model.device.R.string.uei_mainlist_air_conditioning);
		DEFAULT_RESOURCE.put(Model_Z,Model_Z_Res);
		
		//投影仪
		WL23_ResourceInfo Model_T1_Res=new WL23_ResourceInfo(
				R.drawable.device_projector_open,
				R.string.rc_select_projector);
		DEFAULT_RESOURCE.put(Model_T1,Model_T1_Res);
		
		//自定义
		WL23_ResourceInfo Model_Customer_Res=new WL23_ResourceInfo(
				R.drawable.type_select_custom,
				R.string.scene_icon_new);
		DEFAULT_RESOURCE.put(Model_Customer,Model_Customer_Res);
		 
	}

	private static void init_AirModeImage(){
		AirModeImage.put("mode_0", R.drawable.uei_air_set_model_off);
		AirModeImage.put("mode_1", R.drawable.uei_air_set_auto_2x);
		AirModeImage.put("mode_2", R.drawable.uei_air_set_model_cool_2x);
		AirModeImage.put("mode_3", R.drawable.uei_air_set_model_heat_2x);
		AirModeImage.put("mode_4", R.drawable.uei_air_set_model_dry_2x);
		AirModeImage.put("mode_5", R.drawable.uei_air_set_model_fan_2x);
		AirModeImage.put("mode_6", R.drawable.uei_air_set_model_feel_2x);
		
		AirModeImage.put("wind_0", R.drawable.uei_air_set_auto_2x);
		AirModeImage.put("wind_1", R.drawable.uei_air_set_wind1);
		AirModeImage.put("wind_2", R.drawable.uei_air_set_wind2);
		AirModeImage.put("wind_3", R.drawable.uei_air_set_wind3);
		AirModeImage.put("wind_4", R.drawable.uei_air_set_wind0);
		AirModeImage.put("wind_5", R.drawable.uei_air_set_model_feel_2x);
		
		AirModeImage.put("windLR_0", R.drawable.uei_air_set_leftrightauto);
		AirModeImage.put("windLR_1", R.drawable.uei_air_set_leftright1_2x);
		AirModeImage.put("windLR_2", R.drawable.uei_air_set_leftright2_2x);
		AirModeImage.put("windLR_3", R.drawable.uei_air_set_leftright3_2x);
		AirModeImage.put("windLR_4", R.drawable.uei_air_set_leftright4_2x);
		AirModeImage.put("windLR_5", R.drawable.uei_air_set_leftright5_2x);
		
		AirModeImage.put("windUP_0", R.drawable.uei_air_set_updownauto);
		AirModeImage.put("windUP_1", R.drawable.uei_air_set_updown1_2x);
		AirModeImage.put("windUP_2", R.drawable.uei_air_set_updown2_2x);
		AirModeImage.put("windUP_3", R.drawable.uei_air_set_updown3_2x);
		AirModeImage.put("windUP_4", R.drawable.uei_air_set_updown4_2x);
		AirModeImage.put("windUP_5", R.drawable.uei_air_set_updown5_2x);
	}
}
