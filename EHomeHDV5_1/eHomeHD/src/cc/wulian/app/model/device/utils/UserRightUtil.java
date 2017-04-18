package cc.wulian.app.model.device.utils;

import android.content.Context;
import android.widget.Toast;

import com.yuantuo.customview.ui.WLToast;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.ihome.wan.sdk.user.entity.UserRights;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.Preference;

public class UserRightUtil {
	public class EntryPoint {
		public static final int DEFAULT = 0;
		public static final int HOUSEKEEPER = 1;
		public static final int CAMERA = 2;
		public static final int FEEDBACK = 3;
		public static final int DEVICE_ADD = 4;
		public static final int DEVICE_SET_ROOM = 5;
		public static final int DEVICE_RENAME = 6;
		public static final int DEVICE_DELETE = 7;
		public static final int DEVICE_SET = 8;
		public static final int SCENE_EDIT = 9;
		public static final int SCENE_TIMING = 10;
		public static final int SCENE_RENAME = 11;
		public static final int SCENE_DELETE = 12;
		public static final int GATEWAY_RENAME = 13;
		public static final int GATEWAY_SET_ROOM = 14;
		public static final int GATEWAY_MOD_PASSWORD = 15;
		public static final int GATEWAY_AUTHORIZE = 16;
		public static final int GATEWAY_TIMEZONE = 17;
		public static final int GATEWAY_TIMING = 18;
		public static final int GATEWAY_DREAMFLOWER = 19;
		public static final int GATEWAY_FILECLOUD = 20;
		public static final int ROOM_ADD = 21;
		public static final int ROOM_DELETE = 22;
		public static final int ROOM_MODIFY = 23;
		public static final int SCENE_ADD = 24;
		public static final int GATEWAY_ROUTING = 25;
	}

	private UserRights ur = null;

	private static int[] ep_rights = new int[100];
	private static Context mContext = null;
	static {
		Class<EntryPoint> ep = EntryPoint.class;
		Field[] fields = ep.getFields();
		for (Field f : fields) {
			int m = f.getModifiers();
			if (Modifier.isStatic(m)) {
				try {
					int v = f.getInt(ep);
					ep_rights[v] = UserRights.SEE_ONLY;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private boolean isAdmin = true;

	public boolean isAdmin() {
		return isAdmin;
	}

	public void setAdmin(boolean isAdmin) {
		this.isAdmin = isAdmin;
	}

	private static Map<String, Integer> pagesNotAllowForGuest = new HashMap<String, Integer>();
	static {
		pagesNotAllowForGuest.put("cc.wulian.smarthomev5.fragment.house.HouseKeeperManagerFragment",
				EntryPoint.HOUSEKEEPER);
	}

	public boolean canDo(int operation) {
		if (isAdmin) {
			return true;
		}
		if (ep_rights[operation] == UserRights.ALL) {
			return true;
		} else {
			WLToast.showToast(mContext,
					mContext.getResources().getString(cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);
			return false;
		}
	}

	public boolean canEnter(int operation) {
		if (isAdmin) {
			return true;
		}
		if (ep_rights[operation] == UserRights.ALL || ep_rights[operation] == UserRights.SEE_ONLY) {
			return true;
		} else {
			WLToast.showToast(mContext,
					mContext.getResources().getString(cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);
			return false;
		}
	}

	public boolean canSeeDevice(String devID) {
		if (isAdmin) {
			return true;
		}
		Integer r = ur.getSubDeviceRight(devID);
		return (r == UserRights.ALL || r == UserRights.SEE_ONLY);
	}

	public boolean canControlDevice(String devID) {
		if (isAdmin) {
			return true;
		}
		Integer r = ur.getSubDeviceRight(devID);
		return r == UserRights.ALL;
	}

	public boolean canSeeScene(String sceneID) {
		if (isAdmin) {
			return true;
		}
		int r = ur.getSceneRight(sceneID);
		return (r == UserRights.ALL || r == UserRights.SEE_ONLY);
	}

	public boolean canControlScene(String sceneID) {
		if (isAdmin) {
			return true;
		}
		int r = ur.getSceneRight(sceneID);
		return r == UserRights.ALL;
	}

	public boolean canOpenFragment(String fragmentName) {
		if (isAdmin) {
			return true;
		}
		Integer r = pagesNotAllowForGuest.get(fragmentName);
		if (r == null) {
			return true;
		}
		if (r == UserRights.ALL) {
			return true;
		} else {
			WLToast.showToast(mContext,
					mContext.getResources().getString(cc.wulian.smarthomev5.R.string.common_no_right),
					Toast.LENGTH_SHORT);
			return false;
		}
	}

	private UserRightUtil() {
		mContext = MainApplication.getApplication().getApplicationContext();
	}

	private static UserRightUtil instance = null;

	public static UserRightUtil getInstance() {
		if (instance == null) {
			instance = new UserRightUtil();
		}
		return instance;
	}

	public int loadUserRight(String gwID) {
		// 需要加载权限，说明用户是授权的，不应该保存密码。
		Preference.getPreferences().saveRememberChecked(false, gwID);

		ur = WLUserManager.getInstance().getStub().getAllRights(gwID);
		ep_rights[EntryPoint.CAMERA] = ur.getCameraRight();
		return ur.status;
	}

}
