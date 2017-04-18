package cc.wulian.smarthomev5.dao;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.v4.database.DatabaseUtilsCompat;
import cc.wulian.app.model.device.impls.controlable.toc.TwoOutputEntity;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.databases.entitys.TwoOutputConverterRecord;
import cc.wulian.smarthomev5.support.database.AbstractDao;

/**
 * 二路输入表
 * 
 * @author Administrator
 * 
 */
public class TOCDao extends AbstractDao<TwoOutputEntity> {
	private static TOCDao instance = new TOCDao();

	private TOCDao() {

	}

	public static TOCDao getInstance() {
		return instance;
	}

	@Override
	public void insert(TwoOutputEntity entity) {
		String tableName = TwoOutputConverterRecord.TABLE_TWO_OUTPUT;

		ContentValues cv = new ContentValues();
		cv.put(TwoOutputConverterRecord.GW_ID, entity.gwID);
		cv.put(TwoOutputConverterRecord.DEV_ID, entity.devID);
		cv.put(TwoOutputConverterRecord.KEY_ID, entity.keyID);
		cv.put(TwoOutputConverterRecord.KEY_NAME, entity.keyName);
		cv.put(TwoOutputConverterRecord.ONE_TYPE, entity.oneType);
		cv.put(TwoOutputConverterRecord.ONE_VALUE, entity.oneValue);
		cv.put(TwoOutputConverterRecord.TWO_TYPE, entity.twoType);
		cv.put(TwoOutputConverterRecord.TWO_VALUE, entity.twoValue);

		database.insert(tableName, null, cv);
	}

	@Override
	public void delete(TwoOutputEntity obj) {
		String tableName = TwoOutputConverterRecord.TABLE_TWO_OUTPUT;

		List<String> args = Lists.newArrayList();

		String[] clauseArgs = null;
		String whereClause = TwoOutputConverterRecord.GW_ID + "=?";
		args.add(obj.gwID);

		whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
				TwoOutputConverterRecord.DEV_ID + "=?");
		args.add(obj.devID);

		boolean isNullkeyID = StringUtil.isNullOrEmpty(obj.keyID);
		if (!isNullkeyID) {
			whereClause = DatabaseUtilsCompat.concatenateWhere(whereClause,
					TwoOutputConverterRecord.KEY_ID + "=?");
			args.add(obj.keyID);

		}

		clauseArgs = new String[args.size()];
		args.toArray(clauseArgs);

		database.delete(tableName, whereClause, clauseArgs);
	}

	@Override
	public void update(TwoOutputEntity entity) {
		String tableName = TwoOutputConverterRecord.TABLE_TWO_OUTPUT;

		ContentValues cv = new ContentValues();
		cv.put(TwoOutputConverterRecord.GW_ID, entity.gwID);
		cv.put(TwoOutputConverterRecord.DEV_ID, entity.devID);
		cv.put(TwoOutputConverterRecord.KEY_ID, entity.keyID);
		cv.put(TwoOutputConverterRecord.KEY_NAME, entity.keyName);
		cv.put(TwoOutputConverterRecord.ONE_TYPE, entity.oneType);
		cv.put(TwoOutputConverterRecord.ONE_VALUE, entity.oneValue);
		cv.put(TwoOutputConverterRecord.TWO_TYPE, entity.twoType);
		cv.put(TwoOutputConverterRecord.TWO_VALUE, entity.twoValue);

		database.update(tableName, cv, TwoOutputConverterRecord.GW_ID
				+ "=? and " + TwoOutputConverterRecord.DEV_ID + " =? and "
				+ TwoOutputConverterRecord.KEY_ID + " =? ", new String[] {
				entity.gwID, entity.devID, entity.keyID });
	}

	public List<TwoOutputEntity> findTwoOutputGridInfo() {
		List<TwoOutputEntity> list = Lists.newArrayList();
		String sql = "select * from "
				+ TwoOutputConverterRecord.TABLE_TWO_OUTPUT;
		Cursor cursor = database.rawQuery(sql, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				TwoOutputEntity entity = new TwoOutputEntity();
				entity.setGwID(cursor
						.getString(TwoOutputConverterRecord.POS_GW_ID));
				entity.setDevID(cursor
						.getString(TwoOutputConverterRecord.POS_DEV_ID));
				entity.setKeyID(cursor
						.getString(TwoOutputConverterRecord.POS_KEY_ID));
				entity.setKeyName(cursor
						.getString(TwoOutputConverterRecord.POS_KEY_NAME));
				entity.setOneType(cursor
						.getString(TwoOutputConverterRecord.POS_ONE_TYPE));
				entity.setOneValue(cursor
						.getString(TwoOutputConverterRecord.POS_ONE_VALUE));
				entity.setTwoType(cursor
						.getString(TwoOutputConverterRecord.POS_TWO_TYPE));
				entity.setTwoValue(cursor
						.getString(TwoOutputConverterRecord.POS_TWO_VALUE));

				list.add(entity);
			}
			return list;
		} else {
			return null;
		}

	}

}
