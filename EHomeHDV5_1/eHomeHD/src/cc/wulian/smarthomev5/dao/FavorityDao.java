package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Favority;
import cc.wulian.smarthomev5.entity.FavorityEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class FavorityDao extends AbstractDao<FavorityEntity> {

	private static FavorityDao instance = new FavorityDao();

	private FavorityDao() {
	}

	public static FavorityDao getInstance() {
		return instance;
	}

	@Override
	public void insert(FavorityEntity obj) {
		String sql = "insert into " + Favority.TABLE_FAVORITY
				+ " values(?,?,?,?,?,?,?,?)";
		String objs[] = new String[] {
				StringUtil.getStringEscapeEmpty(obj.getGwID()),
				StringUtil.getStringEscapeEmpty(obj.getOperationID()),
				StringUtil.getStringEscapeEmpty(obj.getEpData()),
				StringUtil.getStringEscapeEmpty(obj.getEp()),
				StringUtil.getStringEscapeEmpty(obj.getEpType()),
				StringUtil.getStringEscapeEmpty(obj.getType()),
				StringUtil.getStringEscapeEmpty(obj.getOrder()),
				StringUtil.getStringEscapeEmpty(obj.getTime()) };
		database.execSQL(sql, objs);
	}

	@Override
	public void delete(FavorityEntity obj) {
		String sql = "delete  from " + Favority.TABLE_FAVORITY + " where "
				+ Favority.GW_ID + "=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if (!StringUtil.isNullOrEmpty(obj.getOperationID())) {
			sql += " AND " + Favority.OPERATION_ID + " =? ";
			args.add(obj.getOperationID());
		}
		database.execSQL(sql, args.toArray());
	}

	@Override
	public void update(FavorityEntity obj) {
		ContentValues cv = new ContentValues();
		cv.put(Favority.GW_ID, obj.getGwID());
		cv.put(Favority.OPERATION_ID, obj.getOperationID());
		cv.put(Favority.OPERATION_DATA, obj.getEpData());
		cv.put(Favority.DEVICE_EP, obj.getEp());
		cv.put(Favority.DEVICE_EP_TYPE, obj.getEpType());
		cv.put(Favority.OPERATION_TYPE, obj.getType());
		cv.put(Favority.COUNT, obj.getOrder());
		cv.put(Favority.LAST_TIME, obj.getTime());
		database.update(Favority.TABLE_FAVORITY, cv, Favority.GW_ID + "=? and "
				+ Favority.OPERATION_ID + "=?", new String[] { obj.getGwID(),
				obj.getOperationID() });
	}

	@Override
	public FavorityEntity getById(FavorityEntity obj) {
		FavorityEntity entity = new FavorityEntity();
		String sql = "select * from " + Favority.TABLE_FAVORITY + " where "
				+ Favority.GW_ID + "=? " + " and " + Favority.OPERATION_ID
				+ "=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		args.add(obj.getOperationID());
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[] {}));
		while (cursor.moveToNext()) {

			String gwID = cursor.getString(Favority.POS_GW_ID);
			String type = cursor.getString(Favority.POS_OPERATION_TYPE);
			String operationID = cursor.getString(Favority.POS_OPERATION_ID);
			String epData = cursor.getString(Favority.POS_OPERATION_DATA);
			String time = cursor.getString(Favority.POS_LAST_TIME);
			String ep = cursor.getString(Favority.POS_DEVICE_EP);
			String epType = cursor.getString(Favority.POS_DEVICE_EP_TYPE);
			String order = cursor.getString(Favority.POS_COUNT);
			entity.setGwID(gwID);
			entity.setType(type);
			entity.setOperationID(operationID);
			entity.setEpType(epType);
			entity.setOrder(order);
			entity.setEpData(epData);
			entity.setEp(ep);
			entity.setTime(time);
		}
		cursor.close();
		return entity;
	}

	@Override
	public List<FavorityEntity> findListAll(FavorityEntity obj) {
		List<FavorityEntity> entities = new ArrayList<FavorityEntity>();
		String sql = "select * from " + Favority.TABLE_FAVORITY + " where "
				+ Favority.GW_ID + "=? "+ " and " + Favority.OPERATION_TYPE + "=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		args.add(obj.getType());
		sql += "order by " + Favority.COUNT + " desc," + Favority.LAST_TIME
				+ " desc";
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[] {}));
		while (cursor.moveToNext()) {
			FavorityEntity entity = new FavorityEntity();

			String gwID = cursor.getString(Favority.POS_GW_ID);
			String type = cursor.getString(Favority.POS_OPERATION_TYPE);
			String operationID = cursor.getString(Favority.POS_OPERATION_ID);
			String epData = cursor.getString(Favority.POS_OPERATION_DATA);
			String time = cursor.getString(Favority.POS_LAST_TIME);
			String ep = cursor.getString(Favority.POS_DEVICE_EP);
			String epType = cursor.getString(Favority.POS_DEVICE_EP_TYPE);
			String order = cursor.getString(Favority.POS_COUNT);
			entity.setGwID(gwID);
			entity.setType(type);
			entity.setOperationID(operationID);
			entity.setEpType(epType);
			entity.setOrder(order);
			entity.setEpData(epData);
			entity.setEp(ep);
			entity.setTime(time);
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}

	public void operateFavorityDao(FavorityEntity obj) {
		String sql = "select * from " + Favority.TABLE_FAVORITY + " where "
				+ Favority.GW_ID + "=? and " + Favority.OPERATION_ID + "=?";
		String[] agrs = new String[] { obj.getGwID(), obj.getOperationID() };
		Cursor cursor = database.rawQuery(sql, agrs);
		FavorityEntity entity = null;
		while (cursor.moveToNext()) {
			entity = new FavorityEntity();
			String gwID = cursor.getString(Favority.POS_GW_ID);
			String operationID = cursor.getString(Favority.POS_OPERATION_ID);
			String order = cursor.getString(Favority.POS_COUNT);
			entity.setGwID(gwID);
			entity.setOperationID(operationID);
			entity.setOrder(order);
		}
		cursor.close();
		if (entity != null) {
			// 如果是自动排序的
			if (obj.getOrder().equals(Favority.OPERATION_AUTO)) {
				if (entity.getOrder().equals(Favority.OPERATION_USER)) {
					obj.setOrder(Favority.OPERATION_USER);
				}
				update(obj);
				// 如果是用户排序,数据库已存在即删除
			} else if (obj.getOrder().equals(Favority.OPERATION_USER)) {
				if (entity.getOrder().equals(Favority.OPERATION_USER)) {
					delete(obj);
				} else {
					update(obj);
				}
			}
		} else {
			insert(obj);
		}
	}

}
