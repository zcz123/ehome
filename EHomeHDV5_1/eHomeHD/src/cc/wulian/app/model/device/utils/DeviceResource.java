package cc.wulian.app.model.device.utils;

import java.util.HashMap;
import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.WL_02_IR_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_03_Door_Window_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_04_Emergency_Button;
import cc.wulian.app.model.device.impls.alarmable.WL_05_Electronic_Fence;
import cc.wulian.app.model.device.impls.alarmable.WL_06_Water_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_07_Smoke_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_09_Combustible_Gas_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_10_GasValve;
import cc.wulian.app.model.device.impls.alarmable.WL_40_Motion_Light_S;
import cc.wulian.app.model.device.impls.alarmable.WL_43_Fire_2;
import cc.wulian.app.model.device.impls.alarmable.WL_A4_Glass_Sensors;
import cc.wulian.app.model.device.impls.alarmable.WL_A5_Doorbell_Button;
import cc.wulian.app.model.device.impls.alarmable.WL_B0_Ipad_Alarm;
import cc.wulian.app.model.device.impls.alarmable.WL_C0_Human_Inductor;
import cc.wulian.app.model.device.impls.alarmable.converters4.WL_A1_Converters_Input_4;
import cc.wulian.app.model.device.impls.alarmable.onetranslator.WL_B9_One_Wried_Wireless_Translator;
import cc.wulian.app.model.device.impls.common.EmptyDevice;
import cc.wulian.app.model.device.impls.configureable.compound.WL_38_Pocket_Keys;
import cc.wulian.app.model.device.impls.configureable.ir.WL_22_IR_Control;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control;
import cc.wulian.app.model.device.impls.configureable.ir.WL_24_AR_IR_Control;
import cc.wulian.app.model.device.impls.configureable.touch.WL_32_Touch_2;
import cc.wulian.app.model.device.impls.configureable.touch.WL_33_Touch_3;
import cc.wulian.app.model.device.impls.configureable.touch.WL_34_Touch_4;
import cc.wulian.app.model.device.impls.configureable.touch.WL_36_Touch_6;
import cc.wulian.app.model.device.impls.controlable.WL_01_Warning;
import cc.wulian.app.model.device.impls.controlable.WL_25_MechanicalArm;
import cc.wulian.app.model.device.impls.controlable.WL_26_DoorControl;
import cc.wulian.app.model.device.impls.controlable.WL_27_Barrier;
import cc.wulian.app.model.device.impls.controlable.WL_28_WaterValve;
import cc.wulian.app.model.device.impls.controlable.WL_65_Shade;
import cc.wulian.app.model.device.impls.controlable.WL_66_Blind;
import cc.wulian.app.model.device.impls.controlable.WL_91_D_Temp_Light_Led;
import cc.wulian.app.model.device.impls.controlable.WL_92_Light_Adjust;
import cc.wulian.app.model.device.impls.controlable.WL_A6_Light_Doorbell_S;
import cc.wulian.app.model.device.impls.controlable.WL_Aj_Switch;
import cc.wulian.app.model.device.impls.controlable.WL_Ak_10Switch;
import cc.wulian.app.model.device.impls.controlable.WL_Aq_Switch;
import cc.wulian.app.model.device.impls.controlable.WL_Ar_Shade;
import cc.wulian.app.model.device.impls.controlable.WL_At_Switch;
import cc.wulian.app.model.device.impls.controlable.WL_Au_BuiltinCurtain;
import cc.wulian.app.model.device.impls.controlable.WL_DE_BackgroundMusic;
import cc.wulian.app.model.device.impls.controlable.WL_OF_Clotheshorse;
import cc.wulian.app.model.device.impls.controlable.WL_OK_ZHIHUANG_Motor;
import cc.wulian.app.model.device.impls.controlable.WL_OZ_CentralAir;
import cc.wulian.app.model.device.impls.controlable.WL_Oa_Rangehood;
import cc.wulian.app.model.device.impls.controlable.WL_Ob_HouseholdAir;
import cc.wulian.app.model.device.impls.controlable.WL_Oc_WashingMachine;
import cc.wulian.app.model.device.impls.controlable.WL_Od_GasStoves;
import cc.wulian.app.model.device.impls.controlable.WL_Oe_Fridge;
import cc.wulian.app.model.device.impls.controlable.aircondtion.WL_a0_DaiKin_Air_Conditioner;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_14_dimming_light_1;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_Aw_Switch_Scene_2;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_Ay_Scene_2;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_Be_change_Scene;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_f0_Aust_Switch_1;
import cc.wulian.app.model.device.impls.controlable.austkey.WL_f1_Aust_Switch_2;
import cc.wulian.app.model.device.impls.controlable.bgmusic.WL_DD_BackgroundMusic;
import cc.wulian.app.model.device.impls.controlable.cooker.WL_E2_Electric_cooker;
import cc.wulian.app.model.device.impls.controlable.curtain.WL_80_Curtain_1;
import cc.wulian.app.model.device.impls.controlable.curtain.WL_81_Curtain_2;
import cc.wulian.app.model.device.impls.controlable.dimmerlight.WL_12_D_Light;
import cc.wulian.app.model.device.impls.controlable.dimmerlight.WL_13_Dual_D_Light;
import cc.wulian.app.model.device.impls.controlable.dock.WL_16_Dock;
import cc.wulian.app.model.device.impls.controlable.dock.WL_50_Dock_1;
import cc.wulian.app.model.device.impls.controlable.dock.WL_51_Dock_2;
import cc.wulian.app.model.device.impls.controlable.dock.WL_53_Dock_3;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_67_DoorLock_1;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_68_DoorLock_2;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_69_DoorLock_3;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_70_DoorLock_4;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_89_DoorLock_6;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_Bd_DoorLock_6;
import cc.wulian.app.model.device.impls.controlable.doorlock.WL_OW_DoorLock_5;
import cc.wulian.app.model.device.impls.controlable.ems.WL_15_Ems;
import cc.wulian.app.model.device.impls.controlable.ems.WL_72_EML;
import cc.wulian.app.model.device.impls.controlable.ems.WL_77_SR_EMS;
import cc.wulian.app.model.device.impls.controlable.fancoil.WL_Af_FanCoil;
import cc.wulian.app.model.device.impls.controlable.floorwarm.WL_Ap_FloorWarm;
import cc.wulian.app.model.device.impls.controlable.flowerfm.WL_D9_FolwerFM;
import cc.wulian.app.model.device.impls.controlable.led.WL_90_Light_Led;
import cc.wulian.app.model.device.impls.controlable.light.WL_11_Light;
import cc.wulian.app.model.device.impls.controlable.light.WL_61_Light_1;
import cc.wulian.app.model.device.impls.controlable.light.WL_62_Light_2;
import cc.wulian.app.model.device.impls.controlable.light.WL_63_Light_3;
import cc.wulian.app.model.device.impls.controlable.light.WL_64_Light_4;
import cc.wulian.app.model.device.impls.controlable.light.WL_D7_Light;
import cc.wulian.app.model.device.impls.controlable.light.WL_D8_Light_Voice_Led;
import cc.wulian.app.model.device.impls.controlable.light.WL_OB_Light_4;
import cc.wulian.app.model.device.impls.controlable.metalswitch.WL_Am_switch_1;
import cc.wulian.app.model.device.impls.controlable.metalswitch.WL_An_switch_2;
import cc.wulian.app.model.device.impls.controlable.metalswitch.WL_Ao_switch_3;
import cc.wulian.app.model.device.impls.controlable.module.WL_93_Module_Color_Light;
import cc.wulian.app.model.device.impls.controlable.musicbox.WL_E4_MusicBox;
import cc.wulian.app.model.device.impls.controlable.newthermostat.WL_82_Thermostat;
import cc.wulian.app.model.device.impls.controlable.newthermostat.WL_DB_NewThermostat;
import cc.wulian.app.model.device.impls.controlable.thermostat.WL_78_Thermostat;
import cc.wulian.app.model.device.impls.controlable.thermostat_cooperation.WL_O6_thermostat_custom;
import cc.wulian.app.model.device.impls.controlable.toc.WL_A2_Two_Output_Converter;
import cc.wulian.app.model.device.impls.nouseable.WL_31_Extender;
import cc.wulian.app.model.device.impls.nouseable.WL_52_Button_1;
import cc.wulian.app.model.device.impls.nouseable.WL_54_Button_2;
import cc.wulian.app.model.device.impls.nouseable.WL_55_Button_3;
import cc.wulian.app.model.device.impls.nouseable.WL_56_Button_4;
import cc.wulian.app.model.device.impls.sensorable.WL_17_Temhum;
import cc.wulian.app.model.device.impls.sensorable.WL_18_Co2;
import cc.wulian.app.model.device.impls.sensorable.WL_19_Light_S;
import cc.wulian.app.model.device.impls.sensorable.WL_20_VOC;
import cc.wulian.app.model.device.impls.sensorable.WL_41_Flow;
import cc.wulian.app.model.device.impls.sensorable.WL_42_CTHV;
import cc.wulian.app.model.device.impls.sensorable.WL_44_PM2P5;
import cc.wulian.app.model.device.impls.sensorable.WL_45_Scale;
import cc.wulian.app.model.device.impls.sensorable.WL_46_Carpark;
import cc.wulian.app.model.device.impls.sensorable.WL_47_Human_Traffic;
import cc.wulian.app.model.device.impls.sensorable.WL_48_Sphy;
import cc.wulian.app.model.device.impls.sensorable.WL_A0_Air_Quality;
import cc.wulian.app.model.device.impls.sensorable.WL_D1_Temperature;
import cc.wulian.app.model.device.impls.sensorable.WL_D2_Humidity;
import cc.wulian.app.model.device.impls.sensorable.WL_D3_LightIntensity;
import cc.wulian.app.model.device.impls.sensorable.WL_D4_Noise;
import cc.wulian.app.model.device.impls.sensorable.WL_D5_PM2P5;
import cc.wulian.app.model.device.impls.sensorable.WL_D6_VOC;
import cc.wulian.app.model.device.impls.sensorable.WL_a1_Curtain_Detector;
import cc.wulian.app.model.device.impls.controlable.WL_Ai_30ASwitch;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * device's default resource info, contains default small icon, default name
 */
public class DeviceResource {
	public static ResourceInfo UNKNOWN_RESOURCE = new ResourceInfo(
			R.drawable.device_unknow, R.string.device_unknow, EmptyDevice.class);

	private DeviceResource() {
	}

	public static class ResourceInfo {
		public final int smallIcon;
		public final int name;
		public Class<? extends WulianDevice> clazz;

		public ResourceInfo(int smallIcon, int name, Class<? extends WulianDevice> clazz) {
			this.smallIcon = smallIcon;
			this.name = name;
			this.clazz = clazz;
		}
	}

	public static ResourceInfo getResourceInfo(String type) {
		ResourceInfo info = DEFAULT_RESOURCE.get(type);
		if (info == null)
			info = UNKNOWN_RESOURCE;
		return info;
	}

	private static final Map<String, ResourceInfo> DEFAULT_RESOURCE = new HashMap<String, ResourceInfo>();

	public static final String DEVICETYPE_AD = "Ad";

	static {
		/**
		 * 01
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_WARNING,
				new ResourceInfo(R.drawable.device_warning_normal,
						R.string.device_type_01, WL_01_Warning.class));

		/**
		 * 02
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MOTION,
				new ResourceInfo(R.drawable.device_pir_sensor_normal,
						R.string.device_type_02, WL_02_IR_Sensors.class));

		/**
		 * 03
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONTACT,
				new ResourceInfo(R.drawable.device_doorwin_normal,
						R.string.device_type_03, WL_03_Door_Window_Sensors.class));

		/**
		 * 04
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_EMERGENCY,
				new ResourceInfo(R.drawable.device_dangerbutton_normal,
						R.string.device_type_04, WL_04_Emergency_Button.class));

		/**
		 * 05
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MOTION_F,
				new ResourceInfo(R.drawable.device_motion_fence_normal,
						R.string.device_type_05, WL_05_Electronic_Fence.class));

		/**
		 * 06
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_WATER,
				new ResourceInfo(R.drawable.device_water_normal,
						R.string.device_type_06, WL_06_Water_Sensors.class));

		/**
		 * 07
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_FIRE, new ResourceInfo(
				R.drawable.device_smoke_normal, R.string.device_type_07, WL_07_Smoke_Sensors.class));

		/**
		 * 08
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_NH3, new ResourceInfo(
				R.drawable.device_nh3_normal, R.string.device_type_08, EmptyDevice.class));

		/**
		 * 09
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_GAS, new ResourceInfo(
				R.drawable.device_gas_normal, R.string.device_type_09, WL_09_Combustible_Gas_Sensors.class));

		/**
		 * 10
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_GAS_VALVE,
				new ResourceInfo(R.drawable.device_gas_valve_close,
						R.string.device_type_10, WL_10_GasValve.class));

		/**
		 * 11
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT,
				new ResourceInfo(R.drawable.device_light_close,
						R.string.device_type_11, WL_11_Light.class));

		/**
		 * 12
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT,
				new ResourceInfo(R.drawable.device_d_light_close,
						R.string.device_type_12, WL_12_D_Light.class));

		/**
		 * 13
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DUAL_D_LIGHT,
				new ResourceInfo(R.drawable.device_dual_d_light_close,
						R.string.device_type_13, WL_13_Dual_D_Light.class));
		/**
		 * 14
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_AUS_DIMMING_LIGHT,
				new ResourceInfo(R.drawable.device_d_light_close,
						R.string.device_type_14, WL_14_dimming_light_1.class));
		/**
		 * 15
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_EMS, new ResourceInfo(
				R.drawable.device_calc_dock_close, R.string.device_type_15, WL_15_Ems.class));

		/**
		 * 16
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOCK, new ResourceInfo(
				R.drawable.device_dock_close, R.string.device_type_16, WL_16_Dock.class));

		/**
		 * 17
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TEMHUM,
				new ResourceInfo(R.drawable.device_temp_normal,
						R.string.device_type_17, WL_17_Temhum.class));

		/**
		 * 18
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CO2, new ResourceInfo(
				R.drawable.device_co2_normal, R.string.device_type_18, WL_18_Co2.class));

		/**
		 * 19
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_S,
				new ResourceInfo(R.drawable.device_light_sensor_normal,
						R.string.device_type_19, WL_19_Light_S.class));

		/**
		 * 20
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_VOC, new ResourceInfo(
				R.drawable.device_voc_normal, R.string.device_type_20, WL_20_VOC.class));

		/**
		 * 22
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL,
				new ResourceInfo(R.drawable.device_ir_control_normal,
						R.string.device_type_22, WL_22_IR_Control.class));

		/**
		 * 23
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_23, new ResourceInfo(
				R.drawable.uei_online, R.string.device_type_23, WL_23_IR_Control.class));

		/**
		 * 24
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_AR_IR_CONTROL,
				new ResourceInfo(R.drawable.device_ir_control_normal,
						R.string.device_type_24, WL_24_AR_IR_Control.class));
		/**
		 * 25
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MECHANICAL_ARM,
				new ResourceInfo(R.drawable.device_mechanicalarm_small_isf,
						R.string.device_type_25, WL_25_MechanicalArm.class));
		/**
		 * 26
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL,
				new ResourceInfo(R.drawable.device_door_ctrl_close,
						R.string.device_type_26, WL_26_DoorControl.class));

		/**
		 * 27
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BARRIER,
				new ResourceInfo(R.drawable.device_barrier_close,
						R.string.device_type_27, WL_27_Barrier.class));

		/**
		 * 28
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE,
				new ResourceInfo(R.drawable.device_water_valve_close,
						R.string.device_type_28, WL_28_WaterValve.class));

		/**
		 * 29
		 */
		/*
		 * DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_PANEL_SPEAKER, new
		 * ResourceInfo(R.drawable.device_panel_speaker_normal,
		 * R.string.device_type_29));
		 */

		/**
		 * 31
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_EXTENDER,
				new ResourceInfo(R.drawable.device_extender_normal,
						R.string.device_type_31, WL_31_Extender.class));

		/**
		 * 32
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TOUCH_2,
				new ResourceInfo(R.drawable.device_bind_scene_normal_2,
						R.string.device_type_32, WL_32_Touch_2.class));

		/**
		 * 33
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TOUCH_3,
				new ResourceInfo(R.drawable.device_bind_scene_normal_3,
						R.string.device_type_33, WL_33_Touch_3.class));

		/**
		 * 34
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TOUCH_4,
				new ResourceInfo(R.drawable.device_bind_scene_normal_4,
						R.string.device_type_34, WL_34_Touch_4.class));

		/**
		 * 35
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_KEYBOARD,
				new ResourceInfo(R.drawable.device_bind_scene_normal_4,
						R.string.device_type_35, EmptyDevice.class));

		/**
		 * 36
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TOUCH_6,
				new ResourceInfo(R.drawable.device_bind_scene_normal_6,
						R.string.device_type_36, WL_36_Touch_6.class));

		/**
		 * 37
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TOUCH_6_2,
				new ResourceInfo(R.drawable.device_bind_scene_normal_6,
						R.string.device_type_36, WL_36_Touch_6.class));

		/**
		 * 38
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_POCKET_KEYS,
				new ResourceInfo(R.drawable.device_pocket_keys_normal,
						R.string.device_type_38, WL_38_Pocket_Keys.class));

		/**
		 * 40
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MOTION_LIGHT_S,
				new ResourceInfo(R.drawable.device_motion_light_sensor_normal,
						R.string.device_type_40, WL_40_Motion_Light_S.class));

		/**
		 * 41
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_FLOW, new ResourceInfo(
				R.drawable.device_water_normal, R.string.device_type_41, WL_41_Flow.class));

		/**
		 * 42
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CTHV, new ResourceInfo(
				R.drawable.device_cthv_normal, R.string.device_type_42, WL_42_CTHV.class));

		/**
		 * 43
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_FIRE_SR,
				new ResourceInfo(R.drawable.device_smoke_normal,
						R.string.device_type_07, WL_43_Fire_2.class));

		/**
		 * 44
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_PM2P5,
				new ResourceInfo(R.drawable.device_pm2p5_normal,
						R.string.device_type_44, WL_44_PM2P5.class));

		/**
		 * 45
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_SCALE,
				new ResourceInfo(R.drawable.device_banlance_normal,
						R.string.device_type_45, WL_45_Scale.class));

		/**
		 * 46
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CARPARK,
				new ResourceInfo(R.drawable.device_carpark_unobstructed,
						R.string.device_type_46, WL_46_Carpark.class));

		/**
		 * 47
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_HUMAN_TRAFFIC,
				new ResourceInfo(R.drawable.device_human_traffic_n_a,
						R.string.device_type_47, WL_47_Human_Traffic.class));

		/**
		 * 48
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_SPHYGMOMETER,
				new ResourceInfo(
						R.drawable.device_blood_pressure_monitor_normal,
						R.string.device_type_48, WL_48_Sphy.class));

		/**
		 * 50
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOCK_1,
				new ResourceInfo(R.drawable.device_dock_close,
						R.string.device_type_50, WL_50_Dock_1.class));

		/**
		 * 51
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOCK_2,
				new ResourceInfo(R.drawable.device_dock_close,
						R.string.device_type_51, WL_51_Dock_2.class));

		/**
		 * 52
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BUTTON_1,
				new ResourceInfo(R.drawable.device_button_1_close,
						R.string.device_type_52, WL_52_Button_1.class));

		/**
		 * 53
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOCK_3,
				new ResourceInfo(R.drawable.device_button_3_default,
						R.string.device_type_53, WL_53_Dock_3.class));

		/**
		 * 54
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BUTTON_2,
				new ResourceInfo(R.drawable.device_button_2_default,
						R.string.device_type_54, WL_54_Button_2.class));

		/**
		 * 55
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BUTTON_3,
				new ResourceInfo(R.drawable.device_button_3_default,
						R.string.device_type_55, WL_55_Button_3.class));

		/**
		 * 56
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BUTTON_4,
				new ResourceInfo(R.drawable.device_button_4_default,
						R.string.device_type_56, WL_56_Button_4.class));

		/**
		 * 57
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONTROL_1,
				new ResourceInfo(R.drawable.device_button_1_close,
						R.string.device_type_57, EmptyDevice.class));

		/**
		 * 58
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONTROL_2,
				new ResourceInfo(R.drawable.device_button_2_default,
						R.string.device_type_58,  EmptyDevice.class));

		/**
		 * 59
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONTROL_3,
				new ResourceInfo(R.drawable.device_button_3_default,
						R.string.device_type_59, EmptyDevice.class));

		/**
		 * 61
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1,
				new ResourceInfo(R.drawable.device_button_1_close,
						R.string.device_type_61, WL_61_Light_1.class));

		/**
		 * 62
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_2,
				new ResourceInfo(R.drawable.device_button_2_default,
						R.string.device_type_62, WL_62_Light_2.class));

		/**
		 * 63
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_3,
				new ResourceInfo(R.drawable.device_button_3_default,
						R.string.device_type_63, WL_63_Light_3.class));

		/**
		 * 64
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_4,
				new ResourceInfo(R.drawable.device_button_4_default,
						R.string.device_type_64, WL_64_Light_4.class));

		/**
		 * 65
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_SHADE,
				new ResourceInfo(R.drawable.device_shade_close,
						R.string.device_type_65, WL_65_Shade.class));

		/**
		 * 66
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_BLIND,
				new ResourceInfo(R.drawable.device_blind_close,
						R.string.device_type_66, WL_66_Blind.class));

		/**
		 * 67
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK,
				new ResourceInfo(R.drawable.device_door_lock_close,
						R.string.device_type_67, WL_67_DoorLock_1.class));

		/**
		 * 68
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2,
				new ResourceInfo(R.drawable.device_door_lock_close,
						R.string.device_type_68, WL_68_DoorLock_2.class));

		/**
		 * 69
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3,
				new ResourceInfo(R.drawable.device_door_lock_close,
						R.string.device_type_69, WL_69_DoorLock_3.class));

		/**
		 * 70
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_4,
				new ResourceInfo(R.drawable.device_door_lock_close,
						R.string.device_type_70, WL_70_DoorLock_4.class));

		/**
		 * 72
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_EML_1,
				new ResourceInfo(R.drawable.device_button_1_close,
						R.string.device_type_72, WL_72_EML.class));

		/**
		 * 77
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_EMS_SR,
				new ResourceInfo(R.drawable.device_calc_dock_close,
						R.string.device_type_15, WL_77_SR_EMS.class));

		/**
		 * 78
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT,
				new ResourceInfo(R.drawable.device_thermost_close,
						R.string.device_type_78, WL_78_Thermostat.class));

		/**
		 * 80
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_1,
				new ResourceInfo(R.drawable.device_shade_close,
						R.string.device_type_80, WL_80_Curtain_1.class));

		/**
		 * 81
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_2,
				new ResourceInfo(R.drawable.device_shade_close,
						R.string.device_type_81, WL_81_Curtain_2.class));
		
		/**
		 * 82
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_82, new ResourceInfo(R.drawable.device_thermost82_icon,
						R.string.device_type_82, WL_82_Thermostat.class));

		/**
		 * 89
		 */
		DEFAULT_RESOURCE.put("89", new ResourceInfo(
				R.drawable.device_door_lock_close, R.string.device_type_89, WL_89_DoorLock_6.class));
		/**
		 * 90
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_LED,
				new ResourceInfo(R.drawable.device_light_led_auto,
						R.string.device_type_90, WL_90_Light_Led.class));

		/**
		 * 91
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_91_Temp_led,
				new ResourceInfo(R.drawable.device_d_light_close,
						R.string.device_type_91, WL_91_D_Temp_Light_Led.class));

		/**
		 * 92
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_92, new ResourceInfo(
				R.drawable.device_d_light_close, R.string.device_type_92, WL_92_Light_Adjust.class));

		/**
		 * 93
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_93_Module,
				new ResourceInfo(R.drawable.device_module_light,
						R.string.device_type_93, WL_93_Module_Color_Light.class));
		/**
		 * A0
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_A0, new ResourceInfo(
				R.drawable.device_cthv_normal, R.string.device_type_42, WL_A0_Air_Quality.class));

		/**
		 * A1
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONVERTERS_INPUT_4,
				new ResourceInfo(R.drawable.device_four_convert,
						R.string.device_type_A1, WL_A1_Converters_Input_4.class));

		/**
		 * A2
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CONVERTERS_OUTPUT_2,
				new ResourceInfo(R.drawable.device_convert,
						R.string.device_type_A2, WL_A2_Two_Output_Converter.class));

		/**
		 * A4
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_GLASS_BROKEN,
				new ResourceInfo(R.drawable.device_glass_broken,
						R.string.device_type_A4, WL_A4_Glass_Sensors.class));

		/**
		 * A5
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORBELL_C,
				new ResourceInfo(R.drawable.device_doorbell_c_normal,
						R.string.device_type_A5, WL_A5_Doorbell_Button.class));

		/**
		 * A6
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DOORBELL_S,
				new ResourceInfo(R.drawable.device_doorbell_s_allow,
						R.string.device_type_A6, WL_A6_Light_Doorbell_S.class));
		/**
		 * E2
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_PRESSURE_COOKER,
				new ResourceInfo(R.drawable.device_pressure_cooker,
						R.string.device_type_E2, WL_E2_Electric_cooker.class));

		/**
		 * B0
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_IPADWARNING,
				new ResourceInfo(R.drawable.device_warning_ipad,
						R.string.device_type_B0, WL_B0_Ipad_Alarm.class));
		/**
		 * B9
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_ONETRANSLATOR,
				new ResourceInfo(R.drawable.device_one_translator,
						R.string.device_type_B9, WL_B9_One_Wried_Wireless_Translator.class));
		/**
		 * C0
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_HUMANINDUCTOR,
				new ResourceInfo(R.drawable.device_human_inductor_defence,
						R.string.device_type_C0, WL_C0_Human_Inductor.class));
		/**
		 * E4
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MUSIC_BOX,
				new ResourceInfo(R.drawable.device_music_box,
						R.string.device_type_E4, WL_E4_MusicBox.class));

		/**
		 * D8
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_MINI_LIGHT,
				new ResourceInfo(R.drawable.mini_gate_icon_d,
						R.string.device_type_D8, WL_D8_Light_Voice_Led.class));
		/**
		 * D9
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_FLOWER_FM,
				new ResourceInfo(R.drawable.device_open_radio_icon,
						R.string.device_type_D9, WL_D9_FolwerFM.class));
		/**
		 * a1
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_DETECTOR,
				new ResourceInfo(R.drawable.device_curtain_detector,
						R.string.device_type_a1, WL_a1_Curtain_Detector.class));

		/**
		 * f0
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_1,
				new ResourceInfo(R.drawable.device_aust_key_1,
						R.string.device_type_f0, WL_f0_Aust_Switch_1.class));
		/**
		 * f1
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_SWITCH_KEY_2,
				new ResourceInfo(R.drawable.device_aust_key_2,
						R.string.device_type_f1, WL_f1_Aust_Switch_2.class));
		/**
		 * O6
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT_O6,
				new ResourceInfo(R.drawable.device_thermost_close,
						R.string.device_type_78, WL_O6_thermostat_custom.class));
		/**
		 * OK
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_ZHIHUANG_MOTOR,
				new ResourceInfo(R.drawable.device_shade_close,
						R.string.device_type_OK, WL_OK_ZHIHUANG_Motor.class));
		/**
		 * OW
		 */

		DEFAULT_RESOURCE.put("OW", new ResourceInfo(
				R.drawable.device_door_lock_close, R.string.device_type_69, WL_OW_DoorLock_5.class));
		/**
		 * D1
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_TEMPERATURE,
				new ResourceInfo(R.drawable.device_temp_normal,
						R.string.device_type_D1, WL_D1_Temperature.class));

		/**
		 * D2
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_HUMIDITY,
				new ResourceInfo(R.drawable.device_temp_normal,
						R.string.device_type_D2, WL_D2_Humidity.class));
		/**
		 * D3
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_INTENSITY,
				new ResourceInfo(R.drawable.device_light_sensor_normal,
						R.string.device_type_D3, WL_D3_LightIntensity.class));
		/**
		 * D4
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_NOISE,
				new ResourceInfo(R.drawable.device_noise_normal,
						R.string.device_type_d4, WL_D4_Noise.class));

		/**
		 * D5
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_PM2P5,
				new ResourceInfo(R.drawable.device_pm2p5_normal,
						R.string.device_type_44, WL_D5_PM2P5.class));
		/**
		 * D6
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC,
				new ResourceInfo(R.drawable.device_voc_normal,
						R.string.device_type_20, WL_D6_VOC.class));

		/**
		 * D7
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_LIGHT,
				new ResourceInfo(R.drawable.device_light_led_auto,
						R.string.device_type_90, WL_D7_Light.class));

		/**
		 * DB
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_THERMOSTAT_DB,
				new ResourceInfo(R.drawable.device_button_4_default,
						R.string.device_type_78, WL_DB_NewThermostat.class));

		/**
		 * OB
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_LIGHT_OB_4,
				new ResourceInfo(R.drawable.device_button_4_default,
						R.string.device_type_OB, WL_OB_Light_4.class));

		/**
		 * Ad
		 */
		DEFAULT_RESOURCE.put(DEVICETYPE_AD, new ResourceInfo(
				R.drawable.device_human_inductor_defence,
				R.string.device_type_C0, WL_C0_Human_Inductor.class));
		
		/**
		 * DD
		 */
		DEFAULT_RESOURCE.put("DD", new ResourceInfo(
				R.drawable.device_background_music,
				R.string.device_type_DD, WL_DD_BackgroundMusic.class));
                /**
		 * a0
		 */
		DEFAULT_RESOURCE.put(ConstUtil.DEV_TYPE_FROM_GW_DAIKIN_AIR_CONDITIONING, new ResourceInfo(
				R.drawable.device_a0_little_1, R.string.device_dajin, WL_a0_DaiKin_Air_Conditioner.class));

		/**
		 * Ai
		 */
		DEFAULT_RESOURCE.put("Ai", new ResourceInfo(
				R.drawable.device_ai_online,
				R.string.device_type_Ai,
				WL_Ai_30ASwitch.class));
		/**
		 * Ap
		 */
		DEFAULT_RESOURCE.put("Ap", new ResourceInfo(R.drawable.device_thermost82_icon, R.string.Ap,
				WL_Ap_FloorWarm.class));
		/**
		 * Af
		 */
		DEFAULT_RESOURCE.put("Af", new ResourceInfo(R.drawable.device_thermost82_icon, R.string.device_add_thermostat_fans_name,
				WL_Af_FanCoil.class));

		/**
		 * Bd
		 */
		DEFAULT_RESOURCE.put("Bd",  new ResourceInfo(R.drawable.device_door_lock_close, R.string.device_name_add_smart_touch_doorlock,WL_Bd_DoorLock_6.class));

		/**
		 * Au 窗帘控制器
		 */
		DEFAULT_RESOURCE.put("Au",  new ResourceInfo(R.drawable.curtain_au01, R.string.Au,
				WL_Au_BuiltinCurtain.class));

		/**
		 * Aj
		 */
		DEFAULT_RESOURCE.put("Aj", new ResourceInfo(R.drawable.device_button_1_close, R.string.Aj,
				WL_Aj_Switch.class));

		/**
		 * At
		 */
		DEFAULT_RESOURCE.put("At", new ResourceInfo(R.drawable.device_button_2_default, R.string.At,
				WL_At_Switch.class));

		/**
		 * Ak
		 */
		DEFAULT_RESOURCE.put("Ak", new ResourceInfo(R.drawable.little_ak_0100_off, R.string.Ak,
				WL_Ak_10Switch.class));

		/**
		 * Aq
		 */
		DEFAULT_RESOURCE.put("Aq", new ResourceInfo(R.drawable.device_button_1_close, R.string.device_type_11,
				WL_Aq_Switch.class));
		/**
		 * OZ--美的中央空调
		 */
		DEFAULT_RESOURCE.put("OZ", new ResourceInfo(R.drawable.device_thermost_open, R.string.device_name_central_air_conditioning,WL_OZ_CentralAir.class));

		/**
		 * Am
		 */
		DEFAULT_RESOURCE.put("Am",new ResourceInfo(R.drawable.device_button_1_close,
				R.string.add_device_name_switch_1_key, WL_Am_switch_1.class));

		/**
		 * An
		 */
		DEFAULT_RESOURCE.put("An",new ResourceInfo(R.drawable.device_button_1_close,
				R.string.add_device_name_switch_2_key, WL_An_switch_2.class));

		/**
		 * Ao
		 */
		DEFAULT_RESOURCE.put("Ao",new ResourceInfo(R.drawable.device_button_1_close,
				R.string.add_device_name_switch_3_key, WL_Ao_switch_3.class));

		/**
		 * Ay
		 */
		DEFAULT_RESOURCE.put("Ay",new ResourceInfo(R.drawable.device_bind_scene_normal_2,
				R.string.device_type_32, WL_Ay_Scene_2.class));
		/**
		 * Aw
		 */
		DEFAULT_RESOURCE.put("Aw",new ResourceInfo(R.drawable.device_aust_key_1,
				R.string.device_type_f0, WL_Aw_Switch_Scene_2.class));
		/**
		 * Be
		 */
		DEFAULT_RESOURCE.put("Be",new ResourceInfo(R.drawable.device_d_light_close,
				R.string.device_type_14, WL_Be_change_Scene.class));
		/**
		 * OZ--中央空调
		 */
		DEFAULT_RESOURCE.put("OZ", new ResourceInfo(R.drawable.device_oz_centralair, R.string.device_name_central_air_conditioning,WL_OZ_CentralAir.class));
		/**
		 * Oa--抽油烟机
		 */
		DEFAULT_RESOURCE.put("Oa", new ResourceInfo(R.drawable.device_oa_rangehood, R.string.device_Oa_Rangehood,WL_Oa_Rangehood.class));
		/**
		 * Ob--家用空调
		 */
		DEFAULT_RESOURCE.put("Ob", new ResourceInfo(R.drawable.device_ob_householdair, R.string.device_Ob_HouseholdAir,WL_Ob_HouseholdAir.class));
		/**
		 * Oc--洗衣机
		 */
		DEFAULT_RESOURCE.put("Oc", new ResourceInfo(R.drawable.device_oc_washingmachine, R.string.device_Oc_WashingMachine,WL_Oc_WashingMachine.class));
		/**
		 * Od--燃气灶
		 */
		DEFAULT_RESOURCE.put("Od", new ResourceInfo(R.drawable.device_od_gasstoves, R.string.device_Od_GasStoves,WL_Od_GasStoves.class));
		/**
		 * Oe--冰箱
		 */
		DEFAULT_RESOURCE.put("Oe", new ResourceInfo(R.drawable.device_oe_fridge, R.string.device_Oe_Fridge,WL_Oe_Fridge.class));
		/**
		 * DE--海尔思背景音乐
		 */
		DEFAULT_RESOURCE.put("DE", new ResourceInfo(R.drawable.device_thermost_open, R.string.device_type_DE,WL_DE_BackgroundMusic.class));
		/**
		 * OF--晾霸 - 晾衣架
		 */
		DEFAULT_RESOURCE.put("OF",new ResourceInfo(R.drawable.device_of, R.string.device_OF_Clotheshorse,WL_OF_Clotheshorse.class));
		/**
		 * Ar--金属窗帘控制器
		 */
		DEFAULT_RESOURCE.put("Ar",
				new ResourceInfo(R.drawable.device_shade_close,
						R.string.add_device_name_curtain_control, WL_Ar_Shade.class));
	}
}
