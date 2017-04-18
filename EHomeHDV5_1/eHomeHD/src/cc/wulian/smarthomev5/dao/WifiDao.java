package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Wifi;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class WifiDao extends AbstractDao<WifiEntity> {
	private static WifiDao instance = null;

	private WifiDao() {

	}

	public static WifiDao getInstance() {
		if (instance == null) {
			instance = new WifiDao();
		}
		return instance;
	}

	@Override
	public void insert(WifiEntity obj) {
		try {
			String sql = "insert into " + Wifi.TABLE_WIFI
					+ " values(?,?,?,?,?,?,?,?,?)";
			String objs[] = new String[] {
					StringUtil.getStringEscapeEmpty(obj.getGwID()),
					StringUtil.getStringEscapeEmpty(obj.getOperateID()),
					StringUtil.getStringEscapeEmpty(obj.getOperateType()),
					StringUtil.getStringEscapeEmpty(obj.getEp()),
					StringUtil.getStringEscapeEmpty(obj.getEpType()),
					StringUtil.getStringEscapeEmpty(obj.getEpData()),
					StringUtil.getStringEscapeEmpty(obj.getTime()),
					StringUtil.getStringEscapeEmpty(obj.getSSID()),
					StringUtil.getStringEscapeEmpty(obj.getConditionContent()) };
			database.execSQL(sql, objs);
		} catch (Exception e) {

		}
	}

	@Override
	public void delete(WifiEntity obj) {
		String sql = "delete from " + Wifi.TABLE_WIFI + " where " + Wifi.GW_ID
				+ "=?";
		String objs[] = new String[] { obj.getGwID() };
		database.execSQL(sql, objs);
	}

	@Override
	public void update(WifiEntity obj) {
		String sql = "update " + Wifi.TABLE_WIFI + " set " + Wifi.GW_ID + "=?,"
				+ Wifi.OPERATION_ID + "=?," + Wifi.OPERATION_TYPE + "=?,"
				+ Wifi.DEVICE_EP + "=?," + Wifi.DEVICE_EP_TYPE + "=?,"
				+ Wifi.DEVICE_EP_DATA + "=?," + Wifi.LAST_TIME + "=?"
				+ Wifi.WIFI_SSID + "=?" + Wifi.CONDITION_CONTENT + "=?";
		String objs[] = new String[] {
				StringUtil.getStringEscapeEmpty(obj.getGwID()),
				StringUtil.getStringEscapeEmpty(obj.getOperateID()),
				StringUtil.getStringEscapeEmpty(obj.getOperateType()),
				StringUtil.getStringEscapeEmpty(obj.getEp()),
				StringUtil.getStringEscapeEmpty(obj.getEpType()),
				StringUtil.getStringEscapeEmpty(obj.getEpData()),
				StringUtil.getStringEscapeEmpty(obj.getTime()),
				StringUtil.getStringEscapeEmpty(obj.getSSID()),
				StringUtil.getStringEscapeEmpty(obj.getConditionContent()) };
		database.execSQL(sql, objs);
	}

	@Override
	public WifiEntity getById(WifiEntity obj) {
		String sql = "select * from " + Wifi.TABLE_WIFI + " where "
				+ Wifi.GW_ID + "=? and " + Wifi.OPERATION_TYPE + "=?";
		String objs[] = new String[] { obj.getGwID(), obj.getOperateType() };
		Cursor cursor = database.rawQuery(sql, objs);
		if (cursor.moveToNext()) {
			WifiEntity entity = new WifiEntity();
			entity.setGwID(cursor.getString(Wifi.POS_GW_ID));
			entity.setOperateID(cursor.getString(Wifi.POS_OPERATION_ID));
			entity.setOperateType(cursor.getString(Wifi.POS_OPERATION_TYPE));
			entity.setEp(cursor.getString(Wifi.POS_DEVICE_EP));
			entity.setEpType(cursor.getString(Wifi.POS_DEVICE_EP_TYPE));
			entity.setEpData(cursor.getString(Wifi.POS_DEVICE_EP_DATA));
			entity.setTime(cursor.getString(Wifi.POS_LAST_TIME));
			entity.setSSID(cursor.getString(Wifi.POS_WIFI_SSID));
			entity.setConditionContent(cursor
					.getString(Wifi.POS_CONDITION_CONTENT));
			cursor.close();
			return entity;
		}
		return null;
	}

	@Override
	public List<WifiEntity> findListAll(WifiEntity obj) {
		ArrayList<WifiEntity> entities = new ArrayList<WifiEntity>();
		String sql = "select * from " + Wifi.TABLE_WIFI + " where "
				+ Wifi.GW_ID + "=?";
		String objs[] = new String[] { obj.getGwID() };
		Cursor cursor = database.rawQuery(sql, objs);
		while (cursor.moveToNext()) {
			WifiEntity entity = new WifiEntity();
			entity.setGwID(cursor.getString(Wifi.POS_GW_ID));
			entity.setOperateID(cursor.getString(Wifi.POS_OPERATION_ID));
			entity.setOperateType(cursor.getString(Wifi.POS_OPERATION_TYPE));
			entity.setEp(cursor.getString(Wifi.POS_DEVICE_EP));
			entity.setEpType(cursor.getString(Wifi.POS_DEVICE_EP_TYPE));
			entity.setEpData(cursor.getString(Wifi.POS_DEVICE_EP_DATA));
			entity.setTime(cursor.getString(Wifi.POS_LAST_TIME));
			entity.setSSID(cursor.getString(Wifi.POS_WIFI_SSID));
			entity.setConditionContent(cursor
					.getString(Wifi.POS_CONDITION_CONTENT));
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}

}
