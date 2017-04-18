package cc.wulian.app.model.device.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.configureable.Configureable;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.sensorable.Scanable;
import cc.wulian.app.model.device.impls.sensorable.Sensorable;
import cc.wulian.app.model.device.interfaces.IMultiEpDevice;
import cc.wulian.app.model.device.interfaces.IMultiEpSameTypeDevice;

public class DeviceUtil {
	private DeviceUtil() {

	}

	public static boolean isSameAs(CharSequence c1, CharSequence c2) {
		return TextUtils.equals(c1, c2);
	}

	public static boolean isNull(CharSequence c) {
		if (null == c)
			return true;

		if (c.length() == 0 || isSameAs("null", c))
			return true;

		return false;
	}

	/**
	 * 门锁端口名称 ep 14 --> 密码 <br/>
	 * ep 15 --> 纽扣 <br/>
	 * ep 16 --> 指纹 <br/>
	 * ep 17 --> 射频卡 <br/>
	 */
	public static CharSequence epNameString(CharSequence ep, Context context) {
		String str = null;
		if (WulianDevice.EP_0.equals(ep) || WulianDevice.EP_14.equals(ep)) {
			str = context.getResources().getString(R.string.device_bind_code);
		} else if (WulianDevice.EP_15.equals(ep)) {
			str = context.getResources().getString(
					R.string.device_bind_fastener);
		} else if (WulianDevice.EP_16.equals(ep)) {
			str = context.getResources().getString(
					R.string.device_bind_fingerprint);
		} else if (WulianDevice.EP_17.equals(ep)) {
			str = context.getResources()
					.getString(R.string.device_bind_magcard);
		}
		return str;
	}

	/**
	 * ep 14 --> 1 <br/>
	 * ep 15 --> 2 <br/>
	 * ep 16 --> 3 <br/>
	 * ep 17 --> 4 <br/>
	 */
	public static CharSequence ep2IndexString(CharSequence ep) {
		String str = null;
		if (WulianDevice.EP_0.equals(ep) || WulianDevice.EP_14.equals(ep)) {
			str = "1";
		} else if (WulianDevice.EP_15.equals(ep)) {
			str = "2";
		} else if (WulianDevice.EP_16.equals(ep)) {
			str = "3";
		} else if (WulianDevice.EP_17.equals(ep)) {
			str = "4";
		} else if (WulianDevice.EP_18.equals(ep)) {
			str = "5";
		} else if (WulianDevice.EP_19.equals(ep)) {
			str = "6";
		}
		return str;
	}

	/**
	 * 0 --> ep (0)14 <br/>
	 * 1 --> ep 15 <br/>
	 * 2 --> ep 16 <br/>
	 * 3 --> ep 17 <br/>
	 */
	public static CharSequence index2epString(CharSequence index,
											  boolean firstZeroEp) {
		String str = WulianDevice.EP_14;
		if ("0".equals(index)) {
			str = firstZeroEp ? WulianDevice.EP_0 : WulianDevice.EP_14;
		} else if ("1".equals(index)) {
			str = WulianDevice.EP_15;
		} else if ("2".equals(index)) {
			str = WulianDevice.EP_16;
		} else if ("3".equals(index)) {
			str = WulianDevice.EP_17;
		} else if ("4".equals(index)) {
			str = WulianDevice.EP_18;
		} else if ("5".equals(index)) {
			str = WulianDevice.EP_19;
		}
		return str;
	}

	public static boolean isDeviceDefenseable(WulianDevice device) {
		return device instanceof Defenseable;
	}

	public static boolean isDeviceAlarmable(WulianDevice device) {
		return device instanceof Alarmable;
	}

	public static boolean isDeviceConfigable(WulianDevice device) {
		return device instanceof Configureable;
	}

	public static boolean isDeviceControlable(WulianDevice device) {
		return device instanceof Controlable;
	}

	public static boolean isDeviceSensorable(WulianDevice device) {
		return device instanceof Sensorable;
	}

	public static boolean isDeviceScanable(WulianDevice device) {
		return device instanceof Scanable;
	}

	public static boolean isDeviceMultiEpSameType(WulianDevice device) {
		return device instanceof IMultiEpSameTypeDevice;
	}

	public static boolean isDeviceCompound(WulianDevice device) {
		return device instanceof IMultiEpDevice;
	}

	public static Map<String, Map<Integer, Integer>> getSRDockCategoryDrawables() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> defaultCategory = new LinkedHashMap<Integer, Integer>();
		defaultCategory.put(0, R.drawable.device_calc_dock_open);
		defaultCategory.put(1, R.drawable.device_calc_dock_close);
		defaultCategory.put(2, R.drawable.device_calc_dock_open_big);
		defaultCategory.put(3, R.drawable.device_calc_dock_close_big);
		icons.put("0100", defaultCategory);
		getIconCommonMachine(icons);
		return icons;
	}

	public static Map<String, Map<Integer, Integer>> getDockCategoryDrawables() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> defaultCategory = new LinkedHashMap<Integer, Integer>();
		defaultCategory.put(0, R.drawable.device_dock_open);
		defaultCategory.put(1, R.drawable.device_dock_close);
		defaultCategory.put(2, R.drawable.device_dock_open_big);
		defaultCategory.put(3, R.drawable.device_dock_close_big);
		icons.put("0100", defaultCategory); // 默认
		getIconCommonMachine(icons);
		return icons;
	}

	private static Map<String, Map<Integer, Integer>> getIconCommonMachine(
			Map<String, Map<Integer, Integer>> icons) {
		Map<Integer, Integer> fanCategory = new LinkedHashMap<Integer, Integer>();
		fanCategory.put(0, R.drawable.device_ventilating_fan);
		fanCategory.put(1, R.drawable.device_ventilating_fan_off);
		fanCategory.put(2, R.drawable.device_ventilating_fan_open_big);
		fanCategory.put(3, R.drawable.device_ventilating_fan_1);

		Map<Integer, Integer> heatCategory = new LinkedHashMap<Integer, Integer>();
		heatCategory.put(0, R.drawable.device_heater_open);
		heatCategory.put(1, R.drawable.device_heater_close);
		heatCategory.put(2, R.drawable.device_heater_open_big);
		heatCategory.put(3, R.drawable.device_heater_close_big);

		Map<Integer, Integer> radiatorCategory = new LinkedHashMap<Integer, Integer>();
		radiatorCategory.put(0, R.drawable.device_radiator_open);
		radiatorCategory.put(1, R.drawable.device_radiator_close);
		radiatorCategory.put(2, R.drawable.device_radiator_open_big);
		radiatorCategory.put(3, R.drawable.device_radiator_close_big);

		Map<Integer, Integer> solenoidvalveCategory = new LinkedHashMap<Integer, Integer>();
		solenoidvalveCategory.put(0, R.drawable.device_solenoidvavle_open);
		solenoidvalveCategory.put(1, R.drawable.device_solenoidvavle_close);
		solenoidvalveCategory.put(2, R.drawable.device_solenoidvavle_open_big);
		solenoidvalveCategory.put(3, R.drawable.device_solenoidvavle_close_big);

		icons.put("0101", fanCategory); // 排风扇
		icons.put("0102", heatCategory); // 热水器
		icons.put("0103", radiatorCategory); // 暖气片
		icons.put("0104", solenoidvalveCategory); // 电磁阀
		return icons;
	}

	public static Map<String, Map<Integer, Integer>> getIRCategoryDrawable() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> defaultCategory = new LinkedHashMap<Integer, Integer>();
		defaultCategory.put(0, R.drawable.uei_online);
		defaultCategory.put(1, R.drawable.uei_online);
		defaultCategory.put(2, R.drawable.uei_online);
		defaultCategory.put(3, R.drawable.uei_online);

		// 投影仪
		Map<Integer, Integer> projectorCategory = new LinkedHashMap<Integer, Integer>();
		projectorCategory.put(0, R.drawable.device_projector_open);
		projectorCategory.put(1, R.drawable.device_projector_open);
		projectorCategory.put(2, R.drawable.device_projector_open);
		projectorCategory.put(3, R.drawable.device_projector_open);

		// 电视
		Map<Integer, Integer> tvCategory = new LinkedHashMap<Integer, Integer>();
		tvCategory.put(0, R.drawable.device_tv);
		tvCategory.put(1, R.drawable.device_tv);
		tvCategory.put(2, R.drawable.device_tv);
		tvCategory.put(3, R.drawable.device_tv);

		// 数字机顶盒
		Map<Integer, Integer> boxCategory = new LinkedHashMap<Integer, Integer>();
		boxCategory.put(0, R.drawable.device_set_top_box);
		boxCategory.put(1, R.drawable.device_set_top_box);
		boxCategory.put(2, R.drawable.device_set_top_box);
		boxCategory.put(3, R.drawable.device_set_top_box);

		//  遥控器
		Map<Integer, Integer> remoteCategory = new LinkedHashMap<Integer, Integer>();
		remoteCategory.put(0, R.drawable.device_ir_control_normal);
		remoteCategory.put(1, R.drawable.device_ir_control_normal);
		remoteCategory.put(2, R.drawable.device_ir_control_normal);
		remoteCategory.put(3, R.drawable.device_ir_control_normal);

		// 空调
		Map<Integer, Integer> acCategory = new LinkedHashMap<Integer, Integer>();
		acCategory.put(0, R.drawable.device_air_conditioner);
		acCategory.put(1, R.drawable.device_air_conditioner);
		acCategory.put(2, R.drawable.device_air_conditioner);
		acCategory.put(3, R.drawable.device_air_conditioner);

		icons.put("0100", remoteCategory); // 默认
		icons.put("0101", projectorCategory);
		icons.put("0102", tvCategory);
		icons.put("0103", boxCategory);
		icons.put("0104", defaultCategory);
		icons.put("0105", acCategory);

		return icons;
	}

	public static Map<String, Map<Integer, Integer>> getCurtainCategoryDrawable() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> curtainDefault = new LinkedHashMap<Integer, Integer>();
		curtainDefault.put(0, R.drawable.device_shade_open);
		curtainDefault.put(1, R.drawable.device_shade_close);
		curtainDefault.put(2, R.drawable.device_shade_mid);
		curtainDefault.put(3, R.drawable.device_shade_open_big);
		curtainDefault.put(4, R.drawable.device_shade_close_big);
		curtainDefault.put(5, R.drawable.device_shade_mid_big);

		Map<Integer, Integer> curtainProjector = new LinkedHashMap<Integer, Integer>();
		curtainProjector.put(0, R.drawable.device_projector_open);
		curtainProjector.put(1, R.drawable.device_projector_close);
		curtainProjector.put(2, R.drawable.device_projector_mid);
		curtainProjector.put(3, R.drawable.device_projector_open_big);
		curtainProjector.put(4, R.drawable.device_projector_close_big);
		curtainProjector.put(5, R.drawable.device_projector_mid_big);

		Map<Integer, Integer> curtainTVDoor = new LinkedHashMap<Integer, Integer>();
		curtainTVDoor.put(0, R.drawable.device_tv_door_open);
		curtainTVDoor.put(1, R.drawable.device_tv_door_close);
		curtainTVDoor.put(2, R.drawable.device_tv_door_mid);
		curtainTVDoor.put(3, R.drawable.device_tv_door_open_big);
		curtainTVDoor.put(4, R.drawable.device_tv_door_close_big);
		curtainTVDoor.put(5, R.drawable.device_tv_door_mid_big);

		Map<Integer, Integer> curtainRollingDoor = new LinkedHashMap<Integer, Integer>();
		curtainRollingDoor.put(0, R.drawable.device_rolling_door_open);
		curtainRollingDoor.put(1, R.drawable.device_rolling_door_close);
		curtainRollingDoor.put(2, R.drawable.device_rolling_door_mid);
		curtainRollingDoor.put(3, R.drawable.device_rolling_door_open_big);
		curtainRollingDoor.put(4, R.drawable.device_rolling_door_close_big);
		curtainRollingDoor.put(5, R.drawable.device_rolling_door_mid_big);

		Map<Integer, Integer> curtainAutoFloor = new LinkedHashMap<Integer, Integer>();
		curtainAutoFloor.put(0, R.drawable.device_auto_door_open);
		curtainAutoFloor.put(1, R.drawable.device_auto_door_close);
		curtainAutoFloor.put(2, R.drawable.device_auto_door_mid);
		curtainAutoFloor.put(3, R.drawable.device_auto_door_open_big);
		curtainAutoFloor.put(4, R.drawable.device_auto_door_close_big);
		curtainAutoFloor.put(5, R.drawable.device_auto_door_mid_big);

		Map<Integer, Integer> curtainFloorClock = new LinkedHashMap<Integer, Integer>();
		curtainFloorClock.put(0, R.drawable.device_floor_clock_open);
		curtainFloorClock.put(1, R.drawable.device_floor_clock_close);
		curtainFloorClock.put(2, R.drawable.device_floor_clock_close);
		curtainFloorClock.put(3, R.drawable.device_floor_clock_open_big);
		curtainFloorClock.put(4, R.drawable.device_floor_clock_close_big);
		curtainFloorClock.put(5, R.drawable.device_floor_clock_close_big);

		icons.put("0100", curtainDefault);
		icons.put("0101", curtainProjector);
		icons.put("0102", curtainTVDoor);
		icons.put("0103", curtainRollingDoor);
		icons.put("0104", curtainAutoFloor);
		icons.put("0105", curtainFloorClock);
		return icons;
	}

	public static Map<String, Map<Integer, Integer>> getLightCategoryDrawable() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		Map<Integer, Integer> lightDefault = new LinkedHashMap<Integer, Integer>();
		lightDefault.put(0, R.drawable.device_button_1_open);
		lightDefault.put(1, R.drawable.device_button_1_close);
		lightDefault.put(2, R.drawable.device_button_1_open_big);
		lightDefault.put(3, R.drawable.device_button_1_close_big);
		icons.put("0100", lightDefault);
		getIconCommonMachine(icons);
		return icons;
	}

	public static String getFileURLByLocaleAndCountry(Context context,
													  String fileName) {
		String basicPath = "file:///android_asset/";
		Locale locale = Locale.getDefault();
		String country = locale.getCountry().toLowerCase();
		String language = locale.getLanguage().toLowerCase();
		String url = "introduction/" + language + "_"+ country + "/" + fileName;
		AssetManager am = context.getAssets();
		InputStream inputStream;
		try {
			inputStream = am.open(url);
		} catch (IOException e) {
			e.printStackTrace();
			inputStream = null;
		}
		// 判断该文件路径是否存在,不存在则访问英文版
		if (inputStream == null) {
			url = "introduction/en/" + fileName;
		}
		return basicPath+url;
	}

	public static Map<String, Map<Integer, Integer>> getAiCategoryDrawable() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		/*0 关或下线；1 开*/
		Map<Integer, Integer> lightDefault00 = new LinkedHashMap<Integer, Integer>();
		lightDefault00.put(0, R.drawable.device_ai1_02);
		lightDefault00.put(1, R.drawable.device_ai1_01);
		icons.put("0100", lightDefault00);

		Map<Integer, Integer> lightDefault01 = new LinkedHashMap<Integer, Integer>();
		lightDefault01.put(0, R.drawable.device_ai2_02);
		lightDefault01.put(1, R.drawable.device_ai2_01);
		icons.put("0101", lightDefault01);

		Map<Integer, Integer> lightDefault02 = new LinkedHashMap<Integer, Integer>();
		lightDefault02.put(0, R.drawable.device_ai3_02);
		lightDefault02.put(1, R.drawable.device_ai3_01);
		icons.put("0102", lightDefault02);

		Map<Integer, Integer> lightDefault03 = new LinkedHashMap<Integer, Integer>();
		lightDefault03.put(0, R.drawable.device_ai4_02);
		lightDefault03.put(1, R.drawable.device_ai4_01);
		icons.put("0103", lightDefault03);

		Map<Integer, Integer> lightDefault04 = new LinkedHashMap<Integer, Integer>();
		lightDefault04.put(0, R.drawable.device_ai5_02);
		lightDefault04.put(1, R.drawable.device_ai5_01);
		icons.put("0104", lightDefault04);

		Map<Integer, Integer> lightDefault05 = new LinkedHashMap<Integer, Integer>();
		lightDefault05.put(0, R.drawable.device_ai6_02);
		lightDefault05.put(1, R.drawable.device_ai6_01);
		icons.put("0105", lightDefault05);

		Map<Integer, Integer> lightDefault06 = new LinkedHashMap<Integer, Integer>();
		lightDefault06.put(0, R.drawable.device_ai7_02);
		lightDefault06.put(1, R.drawable.device_ai7_01);
		icons.put("0106", lightDefault06);

		return icons;
	}

	public static Map<String, Map<Integer, Integer>> getAkCategoryDrawable() {
		Map<String, Map<Integer, Integer>> icons = new LinkedHashMap<String, Map<Integer, Integer>>();
		/*0 关或下线；1 开*/
		Map<Integer, Integer> lightDefault00 = new LinkedHashMap<Integer, Integer>();
		lightDefault00.put(0, R.drawable.little_ak_0100_on);
		lightDefault00.put(1, R.drawable.little_ak_0100_off);
		icons.put("0100", lightDefault00);

		Map<Integer, Integer> lightDefault01 = new LinkedHashMap<Integer, Integer>();
		lightDefault01.put(0, R.drawable.little_ak_0101_on);
		lightDefault01.put(1, R.drawable.little_ak_0101_off);
		icons.put("0101", lightDefault01);

		Map<Integer, Integer> lightDefault02 = new LinkedHashMap<Integer, Integer>();
		lightDefault02.put(0, R.drawable.little_ak_0102_on);
		lightDefault02.put(1, R.drawable.little_ak_0102_off);
		icons.put("0102", lightDefault02);

		Map<Integer, Integer> lightDefault03 = new LinkedHashMap<Integer, Integer>();
		lightDefault03.put(0, R.drawable.little_ak_0103_on);
		lightDefault03.put(1, R.drawable.little_ak_0103_off);
		icons.put("0103", lightDefault03);

		return icons;
	}
}
