package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Looper;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.utils.DeviceResource;
import cc.wulian.app.model.device.utils.DeviceResource.ResourceInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;

public class DeviceTool {
	public static String createDeviceTypeCompat(String oldType) {
		if (StringUtil.isNullOrEmpty(oldType))
			return oldType;

		String newType;
		int length = oldType.length();
		if (length >= 3) {
			newType = oldType.substring(length - 2);
		} else {
			newType = oldType;
		}
		return newType;
	}

	@SuppressWarnings("deprecation")
	public static void showDeviceAlarmOrSensorInfo(Context context,
			CharSequence info) {
		if (Looper.myLooper() == null)
			Looper.prepare();
		Toast toast = new Toast(context);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 0);

		LinearLayout linearLayout = new LinearLayout(context);
		linearLayout.setBackgroundDrawable(context.getResources().getDrawable(
				R.drawable.device_alarm_toast));

		TextView textView = new TextView(context);
		textView.setText(info);
		textView.setTextSize(18);
		linearLayout.addView(textView);

		toast.setView(linearLayout);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.show();
	}

	public static boolean isSameDeviceInstance(WulianDevice targetDevice,
			String gwID, String devID) {
		return StringUtil.equals(gwID, targetDevice.getDeviceGwID())
				&& StringUtil.equals(devID, targetDevice.getDeviceID());
	}

	public static String getDefaultAreaTextByIconIndex(Context context,
			int iconIndex) {

		int strRes = R.string.area_icon_other_room;
		if (iconIndex == 1) {
			strRes = R.string.area_icon_living_room;
		} else if (iconIndex == 2) {
			strRes = R.string.area_icon_main_room;
		} else if (iconIndex == 3) {
			strRes = R.string.area_icon_subaltern_room;
		} else if (iconIndex == 4) {
			strRes = R.string.area_icon_child_room;
		} else if (iconIndex == 5) {
			strRes = R.string.area_icon_study_room;
		} else if (iconIndex == 6) {
			strRes = R.string.area_icon_toilet_room;
		} else if (iconIndex == 7) {
			strRes = R.string.area_icon_kitchen_room;
		} else if (iconIndex == 8) {
			strRes = R.string.area_icon_restaurant_room;
		} else if (iconIndex == 9) {
			strRes = R.string.area_icon_balcony_room;
		} else if (iconIndex == 10) {
			strRes = R.string.device_config_edit_dev_area_type_multifunction;
		} else if (iconIndex == 11) {

			strRes = R.string.area_icon_corridor_room;
		}
		else if (iconIndex == 12) {
			strRes = R.string.device_room_bathroom;
		} else if (iconIndex == 13) {
			strRes = R.string.device_room_entrance;
		} else if (iconIndex == 14) {
			strRes = R.string.device_room_pet_room;
		} else if (iconIndex == 15) {
			strRes = R.string.device_room_storage_room;
		} else if (iconIndex == 16) {
			strRes = R.string.device_room_courtyard;
		} else if (iconIndex == 17) {
			strRes = R.string.device_room_garden;
		} else if (iconIndex == 18) {
			strRes = R.string.area_icon_other_room;
		}
		return context.getResources().getString(strRes);
	}

	// 区域选中状态下的背景
	public static int PressgetAreaIconResourceByIconIndex(String icon) {

		int iconIndex = StringUtil.toInteger(icon);
		int drawableRes = R.drawable.area_icon_other_room;
		if (iconIndex == 1) {
			drawableRes = R.drawable.area_icon_living_room;
		} else if (iconIndex == 2) {
			drawableRes = R.drawable.area_icon_main_room;
		} else if (iconIndex == 3) {
			drawableRes = R.drawable.area_icon_subaltern_room;
		} else if (iconIndex == 4) {
			drawableRes = R.drawable.area_icon_child_room;
		} else if (iconIndex == 5) {
			drawableRes = R.drawable.area_icon_study_room;
		} else if (iconIndex == 6) {
			drawableRes = R.drawable.area_icon_toilet_room;
		} else if (iconIndex == 7) {
			drawableRes = R.drawable.area_icon_kitchen_room;
		} else if (iconIndex == 8) {
			drawableRes = R.drawable.area_icon_restaurant_room;
		} else if (iconIndex == 9) {
			drawableRes = R.drawable.area_icon_balcony_room;
		} else if (iconIndex == 10) {
			drawableRes = R.drawable.area_icon_multifunction_room;
		} else if (iconIndex == 11) {
			drawableRes = R.drawable.area_icon_corridor_room;
		} else if (iconIndex == 12) {
			drawableRes = R.drawable.area_icon_bath_room;
		} else if (iconIndex == 13) {
			drawableRes = R.drawable.area_icon_hallway_room;
		} else if (iconIndex == 14) {
			drawableRes = R.drawable.area_icon_pethouse_room;
		} else if (iconIndex == 15) {
			drawableRes = R.drawable.area_icon_store_room;
		} else if (iconIndex == 16) {
			drawableRes = R.drawable.area_icon_yard_room;
		} else if (iconIndex == 17) {
			drawableRes = R.drawable.area_icon_garden_room;
		} else if (iconIndex == 18) {
			drawableRes = R.drawable.area_icon_other_room;
		}
		return drawableRes;
	}

	/**
	 * 区域默认状态下的背景图片为灰色//add by hxc
	 * 
	 * @param icon
	 * @return
	 */
	public static int DefaultgetAreaIconResourceByIconIndex(String icon) {

		int iconIndex = StringUtil.toInteger(icon);
		int drawableRes = R.drawable.area_icon_other_room_click;
		if (iconIndex == 1) {
			drawableRes = R.drawable.area_icon_living_room_click;
		} else if (iconIndex == 2) {
			drawableRes = R.drawable.area_icon_main_room_click;
		} else if (iconIndex == 3) {
			drawableRes = R.drawable.area_icon_subaltern_room_click;
		} else if (iconIndex == 4) {
			drawableRes = R.drawable.area_icon_child_room_click;
		} else if (iconIndex == 5) {
			drawableRes = R.drawable.area_icon_study_room_click;
		} else if (iconIndex == 6) {
			drawableRes = R.drawable.area_icon_toilet_room_click;
		} else if (iconIndex == 7) {
			drawableRes = R.drawable.area_icon_kitchen_room_click;
		} else if (iconIndex == 8) {
			drawableRes = R.drawable.area_icon_restaurant_room_click;
		} else if (iconIndex == 9) {
			drawableRes = R.drawable.area_icon_balcony_room_click;
		} else if (iconIndex == 10) {
			drawableRes = R.drawable.area_icon_multifunction_room_click;
		} else if (iconIndex == 11) {
			drawableRes = R.drawable.area_icon_corridor_room_click;
		} else if (iconIndex == 12) {
			drawableRes = R.drawable.area_icon_bath_room_click;
		} else if (iconIndex == 13) {
			drawableRes = R.drawable.area_icon_hallway_room_click;
		} else if (iconIndex == 14) {
			drawableRes = R.drawable.area_icon_pethouse_room_click;
		} else if (iconIndex == 15) {
			drawableRes = R.drawable.area_icon_store_room_click;
		} else if (iconIndex == 16) {
			drawableRes = R.drawable.area_icon_yard_room_click;
		} else if (iconIndex == 17) {
			drawableRes = R.drawable.area_icon_garden_room_click;
		} else if (iconIndex == 18) {
			drawableRes = R.drawable.area_icon_other_room_click;
		}
		return drawableRes;
	}

	// 获取场景图片
	public static String getSceneTextByIcon(Context context, int iconIndex) {
		int strRes = 11;
		if (iconIndex == 0) {
			strRes = R.string.scene_default_back_home;
		} else if (iconIndex == 1) {
			strRes = R.string.scene_icon_leave_hom;
		} else if (iconIndex == 2) {
			strRes = R.string.scene_icon_sleep;
		} else if (iconIndex == 3) {
			strRes = R.string.scene_icon_night;
		} else if (iconIndex == 4) {
			strRes = R.string.scene_icon_get_up;
		} else if (iconIndex == 5) {
			strRes = R.string.scene_icon_movice;
		} else if (iconIndex == 6) {
			strRes = R.string.scene_icon_customer;
		} else if (iconIndex == 7) {
			strRes = R.string.scene_icon_sport;
		} else if (iconIndex == 8) {
			strRes = R.string.scene_icon_dinner;
		} else if (iconIndex == 9) {
			strRes = R.string.scene_icon_all_on;
		} else if (iconIndex == 10) {
			strRes = R.string.scene_icon_all_off;
		} else if (iconIndex == 11) {
			strRes = R.string.scene_icon_new;
		}
		if (strRes != 0) {
			return context.getResources().getString(strRes);
		}
		return "";
	}

	
	/**
	 * 获取信号强度图片
	 * 
	 * @param context
	 * @param rss
	 * @return
	 */
	public static Drawable getSignalDrawer(Context context, Integer rss) {
		if (rss == null)
			rss = 0;
		if (rss > 0 && rss <= 30) {
			return context.getResources().getDrawable(R.drawable.device_rssi_2);
		} else if (rss > 30 && rss <= 60) {
			return context.getResources().getDrawable(R.drawable.device_rssi_4);
		} else if (rss > 60 && rss <= 100) {
			return context.getResources().getDrawable(R.drawable.device_rssi_6);
		} else {
			return context.getResources().getDrawable(R.drawable.device_rssi_0);
		}
	}

	public static Drawable getSmileDrawable(Context context, String smile) {
		Resources resources = context.getResources();
		if (Messages.SMILE_A.equals(smile)) {
			return resources.getDrawable(R.drawable.home_a_imageview);
		} else if (Messages.SMILE_B.equals(smile)) {
			return resources.getDrawable(R.drawable.home_b_imageview);
		} else if (Messages.SMILE_C.equals(smile)) {
			return resources.getDrawable(R.drawable.home_c_imageview);
		} else if (Messages.SMILE_D.equals(smile)) {
			return resources.getDrawable(R.drawable.home_d_imageview);
		}
		return null;
	}

	public static String getDeviceShowName(WulianDevice device) {
		String name = "";
		if (device == null)
			return name;
		name = device.getDeviceName();
		if (StringUtil.isNullOrEmpty(name)) {
			name = device.getDefaultDeviceName();
		}
		return name;
	}

	public static String getDeviceAlarmAreaName(WulianDevice device) {
		String Areaname = "";
		if (device != null) {
			DeviceAreaEntity entity = AreaGroupManager.getInstance()
					.getDeviceAreaEntity(device.getDeviceGwID(),
							device.getDeviceRoomID());
			if (entity == null || "-1".equals(entity.getRoomID()))
				return Areaname;
			return entity.getName();
		} else {
			return Areaname;
		}
	}

	public static String getDeviceNameByIdAndType(Context context,
			String devID, String type) {
		String result = devID;
		ResourceInfo resource = DeviceResource.getResourceInfo(type);
		if (resource != null) {
			result = context.getString(resource.name);
		}
		return result;
	}

	public static List<RoomInfo> getDefaultRoomInfos(Context context) {
		List<RoomInfo> infos = new ArrayList<RoomInfo>();
		RoomInfo benroomInfo = new RoomInfo();
		benroomInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_benroom));
		RoomInfo livingRoomInfo = new RoomInfo();
		livingRoomInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_type_living_room));
		RoomInfo bathroomInfo = new RoomInfo();
		bathroomInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_type_bathroom));
		RoomInfo kitchenInfo = new RoomInfo();
		kitchenInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_type_kitchen));
		RoomInfo balconyInfo = new RoomInfo();
		balconyInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_type_balcony));
		RoomInfo corridorInfo = new RoomInfo();
		corridorInfo.setName(context.getResources().getString(
				R.string.device_config_edit_dev_area_type_corrido));
		infos.add(benroomInfo);
		infos.add(livingRoomInfo);
		infos.add(bathroomInfo);
		infos.add(kitchenInfo);
		infos.add(balconyInfo);
		infos.add(corridorInfo);
		return infos;
	}

	public static String getDefaultRoomIconID(Context context, String areaName) {
		Resources resources = context.getResources();
		if (StringUtil.isNullOrEmpty(areaName)) {
			return "00";
		}
		String roomKeys = resources.getString(R.string.area_icon_other_room);
		String[] keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "00";
			}
		}

		roomKeys = resources.getString(R.string.area_icon_living_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "01";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_main_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "02";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_subaltern_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "03";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_child_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "04";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_study_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "05";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_toilet_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "06";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_kitchen_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "07";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_restaurant_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "08";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_balcony_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "09";
			}
		}

		roomKeys = resources.getString(R.string.area_icon_multifunction_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "10";
			}
		}
		roomKeys = resources.getString(R.string.area_icon_corridor_room);
		keys = roomKeys.split(",");
		for (String key : keys) {
			if (areaName.contains(key)) {
				return "11";
			}
		}
		return "00";
	}

	public static String getCategoryName(Context context, Category category) {
		if (Category.C_CONTROL == category) {
			return context.getResources().getString(
					R.string.device_electrical_control);
		} else if (Category.C_ENVIRONMENT == category) {
			return context.getResources().getString(
					R.string.device_environment_adjust);
		} else if (Category.C_HEALTH == category) {
			return context.getResources().getString(
					R.string.device_health_report);
		} else if (Category.C_LIGHT == category) {
			return context.getResources().getString(
					R.string.device_light_management);
		} else if (Category.C_SECURITY == category) {
			return context.getResources().getString(
					R.string.device_security_protection);
		} else {
			return context.getResources().getString(
					R.string.device_integrated_service);
		}
	}
}
