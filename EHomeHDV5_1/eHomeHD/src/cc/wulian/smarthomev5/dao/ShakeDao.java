package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Shake;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class ShakeDao extends AbstractDao<ShakeEntity>{
	private static ShakeDao instance = new ShakeDao();
	private ShakeDao(){
	}
	public static ShakeDao getInstance(){
		return instance;
	}
	@Override
	public void insert(ShakeEntity obj) {
		try{
		String sql = "insert into "+Shake.TABLE_SHAKE+" values(?,?,?,?,?,?,?)";
		String objs[] = new String[]{StringUtil.getStringEscapeEmpty(obj.getGwID()),StringUtil.getStringEscapeEmpty(obj.getOperateID()),StringUtil.getStringEscapeEmpty(obj.getOperateType()),StringUtil.getStringEscapeEmpty(obj.getEp()),StringUtil.getStringEscapeEmpty(obj.getEpType()),StringUtil.getStringEscapeEmpty(obj.getEpData()),StringUtil.getStringEscapeEmpty(obj.getTime())};
		database.execSQL(sql,objs);
		}catch(Exception e){
			
		}
	}

	@Override
	public void delete(ShakeEntity obj) {
		String sql = "delete from "+Shake.TABLE_SHAKE+" where "+Shake.GW_ID+"=?";
		String objs[] = new String[]{obj.getGwID()};
		database.execSQL(sql,objs);
	}

	@Override
	public void update(ShakeEntity obj) {
		String sql = "update "+Shake.TABLE_SHAKE+" set "+Shake.GW_ID+"=?,"+Shake.OPERATION_ID+"=?,"+Shake.OPERATION_TYPE+"=?,"+Shake.DEVICE_EP+"=?,"+Shake.DEVICE_EP_TYPE+"=?,"+Shake.DEVICE_EP_DATA+"=?,"+Shake.LAST_TIME+"=?";
		String objs[] = new String[]{StringUtil.getStringEscapeEmpty(obj.getGwID()),StringUtil.getStringEscapeEmpty(obj.getOperateID()),StringUtil.getStringEscapeEmpty(obj.getOperateType()),StringUtil.getStringEscapeEmpty(obj.getEp()),StringUtil.getStringEscapeEmpty(obj.getEpType()),StringUtil.getStringEscapeEmpty(obj.getEpData()),StringUtil.getStringEscapeEmpty(obj.getTime())};
		database.execSQL(sql,objs);
	}

	@Override
	public ShakeEntity getById(ShakeEntity obj) {
		String sql = "select * from "+Shake.TABLE_SHAKE+" where "+Shake.GW_ID+"=? and "+Shake.OPERATION_ID+"=? and "+Shake.DEVICE_EP+"=?";
		String objs[] = new String[]{obj.getGwID(),obj.getOperateID(),obj.getEp()};
		Cursor cursor = database.rawQuery(sql,objs);
		if(cursor.moveToNext()){
			ShakeEntity entity = new ShakeEntity();
			entity.setGwID(cursor.getString(Shake.POS_GW_ID));
			entity.setOperateID(cursor.getString(Shake.POS_OPERATION_ID));
			entity.setOperateType(cursor.getString(Shake.POS_OPERATION_TYPE));
			entity.setEp(cursor.getString(Shake.POS_DEVICE_EP));
			entity.setEpType(cursor.getString(Shake.POS_DEVICE_EP_TYPE));
			entity.setEpData(cursor.getString(Shake.POS_DEVICE_EP_DATA));
			entity.setTime(cursor.getString(Shake.POS_LAST_TIME));
			cursor.close();
			return entity;
		}
		return null;
	}
	@Override
	public List<ShakeEntity> findListAll(ShakeEntity obj) {
		ArrayList<ShakeEntity> entities = new ArrayList<ShakeEntity>();
		String sql = "select * from "+Shake.TABLE_SHAKE+" where "+Shake.GW_ID+"=?";
		String objs[] = new String[]{obj.getGwID()};
		Cursor cursor = database.rawQuery(sql,objs);
		while(cursor.moveToNext()){
			ShakeEntity entity = new ShakeEntity();
			entity.setGwID(cursor.getString(Shake.POS_GW_ID));
			entity.setOperateID(cursor.getString(Shake.POS_OPERATION_ID));
			entity.setOperateType(cursor.getString(Shake.POS_OPERATION_TYPE));
			entity.setEp(cursor.getString(Shake.POS_DEVICE_EP));
			entity.setEpType(cursor.getString(Shake.POS_DEVICE_EP_TYPE));
			entity.setEpData(cursor.getString(Shake.POS_DEVICE_EP_DATA));
			entity.setTime(cursor.getString(Shake.POS_LAST_TIME));
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}
	
}
