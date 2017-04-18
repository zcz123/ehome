package cc.wulian.smarthomev5.dao;

import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import cc.wulian.app.model.device.impls.controlable.musicbox.MusicBoxRecordEntity;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.databases.entitys.MusicBoxRecord;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class MusicDao extends AbstractDao<MusicBoxRecordEntity> {
	private static MusicDao instance = new MusicDao();

	private MusicDao() {
	}

	public static MusicDao getInstance() {
		return instance;
	}

	@Override
	public void insert(MusicBoxRecordEntity entity) {
		String tableName = MusicBoxRecord.TABLE_MUSICBOX_RECORDS;

		ContentValues cv = new ContentValues();

		cv.put(MusicBoxRecord.GW_ID, entity.gwID);
		cv.put(MusicBoxRecord.DEV_ID, entity.devID);
		cv.put(MusicBoxRecord.EP, entity.ep);
		cv.put(MusicBoxRecord.SONG_ID, entity.songID);
		cv.put(MusicBoxRecord.SONG_NAME, entity.songName);
		database.insert(tableName, null, cv);

	}

	@Override
	public void delete(MusicBoxRecordEntity obj) {

	}

	@Override
	public void update(MusicBoxRecordEntity entity) {
		String tableName = MusicBoxRecord.TABLE_MUSICBOX_RECORDS;

		ContentValues cv = new ContentValues();

		cv.put(MusicBoxRecord.GW_ID, entity.gwID);
		cv.put(MusicBoxRecord.DEV_ID, entity.devID);
		cv.put(MusicBoxRecord.EP, entity.ep);
		cv.put(MusicBoxRecord.SONG_ID, entity.songID);
		cv.put(MusicBoxRecord.SONG_NAME, entity.songName);

		database.update(tableName, cv, MusicBoxRecord.GW_ID + "=?" + " and "
				+ MusicBoxRecord.DEV_ID + "=?" + " and " + MusicBoxRecord.EP
				+ "=?" + " and " + MusicBoxRecord.SONG_ID + "=?", new String[] {
				entity.gwID, entity.devID, entity.ep, entity.songID });

	}

	@Override
	public MusicBoxRecordEntity getById(MusicBoxRecordEntity obj) {
		return null;
	}

	public List<MusicBoxRecordEntity> findSongName() {
		List<MusicBoxRecordEntity> list = Lists.newArrayList();
		String sql = "select * from " + MusicBoxRecord.TABLE_MUSICBOX_RECORDS;
		Cursor cursor = database.rawQuery(sql, null);
		if (cursor != null) {
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor
					.moveToNext()) {
				MusicBoxRecordEntity entity = new MusicBoxRecordEntity();
				entity.setGwID(cursor.getString(MusicBoxRecord.POS_GW_ID));
				entity.setDevID(cursor.getString(MusicBoxRecord.POS_DEV_ID));
				entity.setEp(cursor.getString(MusicBoxRecord.POS_EP));
				entity.setSongID(cursor.getString(MusicBoxRecord.POS_SONG_ID));
				entity.setSongName(cursor
						.getString(MusicBoxRecord.POS_SONG_NAME));
				list.add(entity);
			}
			return list;
		} else {
			return null;

		}
	}

}
