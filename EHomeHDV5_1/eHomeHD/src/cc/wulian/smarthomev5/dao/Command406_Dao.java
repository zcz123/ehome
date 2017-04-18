package cc.wulian.smarthomev5.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.uei.UEI;
import cc.wulian.smarthomev5.support.database.AbstractDao;

/**
 * 为406命令存储到数据库中提供统一操作
 * @author yuxiaoxuan
 * @date 2016年7月20日13:48:01
 */
public class Command406_Dao extends AbstractDao<Command406Result>{
	private static Command406_Dao instance;
	public static Command406_Dao getInstance(){
		if(instance==null){
			synchronized (Command406_Dao.class) {
				Command406_Dao temp=instance;
				if(temp==null){
					temp=new Command406_Dao();
					instance=temp;
				}
			}
			instance=new Command406_Dao();
		}
		return instance;
	}
	@Override
	public void insert(Command406Result obj) {
		try{
			String sql =MessageFormat.format( "insert into {0}({1},{2},{3},{4},{5},{6}) values(?,?,?,?,?,?)",
					UEI.TABLE_UEI,
					UEI.GW_ID,
					UEI.DEV_ID,
					UEI.APP_ID,
					UEI.KEY,
					UEI.TIME,
					UEI.VALUE
					);
			String objs[] = new String[] {
					StringUtil.getStringEscapeEmpty(obj.getGwID()),
					StringUtil.getStringEscapeEmpty(obj.getDevID()),
					StringUtil.getStringEscapeEmpty(obj.getAppID()),
					StringUtil.getStringEscapeEmpty(obj.getKey()),
					StringUtil.getStringEscapeEmpty(obj.getTime()),
					StringUtil.getStringEscapeEmpty(obj.getData()) };
			database.execSQL(sql, objs);
			}catch(Exception e){
				e.printStackTrace();
			}
	}

	@Override
	public void delete(Command406Result obj) {
		String sql = "delete from " + UEI.TABLE_UEI + " where " + UEI.GW_ID +"= ? and "+UEI.DEV_ID+"= ? and "+ UEI.KEY + "=?";
		String objs[] = new String[] { obj.getGwID(), obj.getDevID(), obj.getKey()};
		database.execSQL(sql, objs);
//		database.close();
	}

	@Override
	public void update(Command406Result obj) {
		String sql=MessageFormat.format("update {0} set {1}=?,{2}=?,{3}=? where {4}=? and {5}=? and {6}=?", 
				UEI.TABLE_UEI,
				UEI.APP_ID,
				UEI.TIME,
				UEI.VALUE,
				UEI.GW_ID,
				UEI.DEV_ID,
				UEI.KEY);
		String objs[] = new String[] {
				StringUtil.getStringEscapeEmpty(obj.getAppID()),
				StringUtil.getStringEscapeEmpty(obj.getTime()),
				StringUtil.getStringEscapeEmpty(obj.getData()),
				StringUtil.getStringEscapeEmpty(obj.getGwID()),
				StringUtil.getStringEscapeEmpty(obj.getDevID()),				
				StringUtil.getStringEscapeEmpty(obj.getKey())};
		database.execSQL(sql, objs);
//		database.close();
	}
	/**
	 * 修改或增加</br>
	 * 根据网关ID、设备ID和Key进行查询。若无记录则添加该设备；若有记录修改
	 * @param obj
	 */
	public void UpdateOrInsert(Command406Result obj){
		String sqlCount=MessageFormat.format("SELECT COUNT(1) FROM {0} where {1}=? and {2}=? and {3}=?", 
				UEI.TABLE_UEI,
				UEI.GW_ID,
				UEI.DEV_ID,
				UEI.KEY);
		String objs[] = new String[] {
				StringUtil.getStringEscapeEmpty(obj.getGwID()),
				StringUtil.getStringEscapeEmpty(obj.getDevID()),				
				StringUtil.getStringEscapeEmpty(obj.getKey())};
		int itemCount=0;
		Cursor cursor =database.rawQuery(sqlCount,objs);
		if (cursor.moveToNext()) {
			itemCount=cursor.getInt(0);
		}	
		cursor.close();
		if(itemCount==0){
			insert(obj);
		}else{
			update(obj);
		}
	}
	/**
	 * 获取最后的时间戳
	 * @return 最后的时间戳
	 */
	public String GetLastTime(){
		String lastTime="";
		String runsql="SELECT MAX(T_UEI_TIME) FROM T_UEI";
		Cursor cursor =database.rawQuery(runsql,null);
		if(cursor.getCount()>0){
			if(cursor.moveToNext()){
				lastTime=cursor.getString(0);
			}
		}
		cursor.close();
		return lastTime;
	}
	
	public void DeleteItems(String gwID,String devID){
		String runsql="delete from T_UEI where T_UEI_GW_ID=? and T_UEI_DEV_ID=?";
		String arg_values[] = new String[] {gwID,devID};
		database.execSQL(runsql, arg_values);
	}
	public void DeleteItems(String gwID,String devID,String key){
		String runsql="delete from T_UEI where T_UEI_GW_ID=? and T_UEI_DEV_ID=? and T_UEI_KEY=?";
		String arg_values[] = new String[] {gwID,devID,key};
		database.execSQL(runsql, arg_values);
	}
	public List<Command406Result> GetItemsByKey(String gwID,String devID){
		List<Command406Result> results=new ArrayList<>();
		String sql = "select * from " + UEI.TABLE_UEI + " where "
				+ UEI.GW_ID + "=? and " + UEI.DEV_ID + "=? ";
		String objs[] = new String[] { gwID, devID };
		Cursor cursor = database.rawQuery(sql, objs);
		while (cursor.moveToNext()) {
			Command406Result entity = new Command406Result();
			
			entity.setGwID(cursor.getString(cursor.getColumnIndex(UEI.GW_ID)));
			entity.setDevID(cursor.getString(cursor.getColumnIndex(UEI.DEV_ID)));
			entity.setAppID(cursor.getString(cursor.getColumnIndex(UEI.APP_ID)));
			entity.setKey(cursor.getString(cursor.getColumnIndex(UEI.KEY)));
			entity.setTime(cursor.getString(cursor.getColumnIndex(UEI.TIME)));
			entity.setData(cursor.getString(cursor.getColumnIndex(UEI.VALUE)));
			entity.setMode("3");
			results.add(entity);
		}
		cursor.close();
		return results;
	}
	
	public List<Command406Result> GetItemsByKey(String gwID,String devID,String ueiKey){
		List<Command406Result> results=new ArrayList<>();
		String sql = "select * from " + UEI.TABLE_UEI + " where "
				+ UEI.GW_ID + "=? and " + UEI.DEV_ID + "=? and "+UEI.KEY+"=?";
		String objs[] = new String[] { gwID, devID,ueiKey};
		Cursor cursor = database.rawQuery(sql, objs);
		while (cursor.moveToNext()) {
			Command406Result entity = new Command406Result();
			
			entity.setGwID(cursor.getString(cursor.getColumnIndex(UEI.GW_ID)));
			entity.setDevID(cursor.getString(cursor.getColumnIndex(UEI.DEV_ID)));
			entity.setAppID(cursor.getString(cursor.getColumnIndex(UEI.APP_ID)));
			entity.setKey(cursor.getString(cursor.getColumnIndex(UEI.KEY)));
			entity.setTime(cursor.getString(cursor.getColumnIndex(UEI.TIME)));
			entity.setData(cursor.getString(cursor.getColumnIndex(UEI.VALUE)));
			entity.setMode("3");
			results.add(entity);
		}
		cursor.close();
		return results;
	}
}
