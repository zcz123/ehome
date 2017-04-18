package cc.wulian.smarthomev5.tools;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class AnnouncementManager {
	public static final String ACTIVE_TYPE_NORMAL = "0";
	public static final String ACTIVE_TYPE_LOTTERY = "1";
	public static final String ACTIVE_TYPE_WIN_LOTTERY = "2";
	public static final String KEY_LOTTERY_ACTIVE = "KEY_LOTTERY_ACTIVE";
	private static AnnouncementManager instance = new AnnouncementManager();
	private Preference preference = Preference.getPreferences();
	private List<Announcement> entites = new ArrayList<Announcement>();

	public static AnnouncementManager getInstance() {
		return instance;
	}

	public void checkAnnouncements() {
		boolean isReadAnnouncement = preference.getBoolean(
				IPreferenceKey.P_KEY_REDDOT_NAVIGATION_CONTACT_US, false);
		if (!isReadAnnouncement) {
			for (Announcement entity : entites) {
				int oldVersion = preference.getInt(
						IPreferenceKey.p_KEY_ANNOUNCEMENT_VERSTION, 0);
				int currentVersion = StringUtil.toInteger(entity.getVersion());
				if (currentVersion > oldVersion) {
					preference.putBoolean(
							IPreferenceKey.P_KEY_REDDOT_NAVIGATION_CONTACT_US,
							true);
					preference.putInt(
							IPreferenceKey.p_KEY_ANNOUNCEMENT_VERSTION,
							currentVersion);
				}
			}
		}
	}
	public synchronized List<Announcement> loadNoties(String appID,String gwID) {
		try {
			JSONObject object = new JSONObject();
			object.put("appId", appID);
			object.put("gwId", gwID);
			object.put("language", LanguageUtil.getWulianCloudLanguage());
			String json = HttpUtil.postWulianCloud(WulianCloudURLManager.getAnnouncementURL(), object);
			Logger.debug("annoncement:" + json);
			if (!StringUtil.isNullOrEmpty(json)) {
				entites.clear();
				JSONObject obj = JSONObject.parseObject(json);
				JSONArray array = obj.getJSONArray("data");
				if (array != null) {
					for (int i = 0; i < array.size(); i++) {
						JSONObject announcement = array.getJSONObject(i);
						Announcement entity = new Announcement();
						entity.setActiveDeployTime(announcement
								.getString("deployTime"));
						entity.setActiveDetail(announcement
								.getString("detail"));
						entity.setActiveName(announcement
								.getString("name"));
						entity.setActivePictureUrl(announcement
								.getString("pictureUrl"));
						entity.setActiveUrl(announcement.getString("url"));
						entity.setVersion(announcement.getString("id"));
						entity.setType(announcement.getString("type"));
						entites.add(entity);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return entites;
	}
	public Announcement getShowLotteryAnnouncement() {
		Announcement lotteryActiveAnnouncement = null;
		for (Announcement entity : entites) {
			int oldVersion = preference.getInt(
					IPreferenceKey.P_KEY_ANNOUNCEMENT_LOTTERY_VERSTION,
					0);
			int curVersion = StringUtil.toInteger(entity.getVersion());
			if (curVersion > oldVersion && ACTIVE_TYPE_LOTTERY.equals(entity.getType())) {
				preference.putInt(IPreferenceKey.P_KEY_ANNOUNCEMENT_LOTTERY_VERSTION, curVersion);
				lotteryActiveAnnouncement = entity;
			}
		}
		return lotteryActiveAnnouncement;
	}
	public Announcement getShowWinLotteryAnnouncement(){
		Announcement winLotteryActiveAnnouncement = null;
		for (Announcement entity : entites) {
			int oldVersion = preference.getInt(
					IPreferenceKey.P_KEY_ANNOUNCEMENT_WIN_LOTTERY_VERSTION,
					0);
			int curVersion = StringUtil.toInteger(entity.getVersion());
			if (curVersion > oldVersion && ACTIVE_TYPE_WIN_LOTTERY.equals(entity.getType())) {
				preference.putInt(IPreferenceKey.P_KEY_ANNOUNCEMENT_WIN_LOTTERY_VERSTION, curVersion);
				winLotteryActiveAnnouncement = entity;
			}
		}
		return winLotteryActiveAnnouncement;
	}
	public static class Announcement {
		private String version;
		private String activeName;
		private String activeUrl;
		private String activeDetail;
		private String activeDeployTime;
		private String activePictureUrl;
		private String type;

		public String getVersion() {
			return version;
		}

		public void setVersion(String version) {
			this.version = version;
		}

		public String getActiveName() {
			return activeName;
		}

		public void setActiveName(String activeName) {
			this.activeName = activeName;
		}

		public String getActiveUrl() {
			return activeUrl;
		}

		public void setActiveUrl(String activeUrl) {
			this.activeUrl = activeUrl;
		}

		public String getActiveDetail() {
			return activeDetail;
		}

		public void setActiveDetail(String activeDetail) {
			this.activeDetail = activeDetail;
		}

		public String getActiveDeployTime() {
			return activeDeployTime;
		}

		public void setActiveDeployTime(String activeDeployTime) {
			this.activeDeployTime = activeDeployTime;
		}

		public String getActivePictureUrl() {
			return activePictureUrl;
		}

		public void setActivePictureUrl(String activePictureUrl) {
			this.activePictureUrl = activePictureUrl;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

	}

	public static class LotteryActive {
		private String appID;
		private String gwID;
		private String code;

		public String getAppID() {
			return appID;
		}

		public void setAppID(String appID) {
			this.appID = appID;
		}

		public String getGwID() {
			return gwID;
		}

		public void setGwID(String gwID) {
			this.gwID = gwID;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

	}
}
