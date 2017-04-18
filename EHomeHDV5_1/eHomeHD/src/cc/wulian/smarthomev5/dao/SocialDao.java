package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Social;
import cc.wulian.smarthomev5.entity.SocialEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;
import cc.wulian.smarthomev5.support.database.PageParameter;


public class SocialDao extends AbstractDao<SocialEntity>{

	private static SocialDao instance = new SocialDao();
	private SocialDao(){
	}
	public static SocialDao getInstance(){
		return instance;
	}
	
	@Override
	public void insert(SocialEntity obj) {
		String sql = "insert into "+Social.TABLE_SOCIAL+" values(?,(select max(" + Social.SOCIAL_ID + ") from " + Social.TABLE_SOCIAL + ")+1,?,?,?,?,?,?)";
		String objs[] = new String[]{StringUtil.getStringEscapeEmpty(obj.getGwID()),StringUtil.getStringEscapeEmpty(obj.getUserType()),StringUtil.getStringEscapeEmpty(obj.getUserID()),StringUtil.getStringEscapeEmpty(obj.getAppID()),StringUtil.getStringEscapeEmpty(obj.getUserName()),StringUtil.getStringEscapeEmpty(obj.getData()),StringUtil.getStringEscapeEmpty(obj.getTime())};
		database.execSQL(sql,objs);
		sql = "select * from "+Social.TABLE_SOCIAL+" where rowid=(select LAST_INSERT_ROWID())";
		Cursor cursor = database.rawQuery(sql, null);
		if(cursor.moveToNext()){
			obj.setSocialID(cursor.getInt(Social.POS_SOCIAL_ID)+"");
		}
		cursor.close();
	}

	@Override
	public void delete(SocialEntity obj) {
		String sql = "delete  from "+Social.TABLE_SOCIAL+" where "+Social.GW_ID+"=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getSocialID())){
			String ids = obj.getSocialID();
			if(ids.endsWith(",")){
				ids = ids.substring(0,ids.length()-1);
			}
			sql +=  " AND " + Social.SOCIAL_ID+ " in( "+ids+" ) ";
		}
		database.execSQL(sql, args.toArray());
	}

	@Override
	public void update(SocialEntity obj) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public List<SocialEntity> findListPage(SocialEntity obj,
			PageParameter parameter) {
		List<SocialEntity> entities = new ArrayList<SocialEntity>();
		String sql = "select * from (select * from "+Social.TABLE_SOCIAL+" where "+Social.GW_ID+"=? order by "+Social.TIME+" desc) limit ?,?";
		sql = "select * from (" + sql + ") " + "order by " + Social.TIME + " asc";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		args.add(parameter.getTotalCount()+"");
		args.add(parameter.getPageSize()+"");
		Cursor cursor = database.rawQuery(sql,args.toArray(new String[]{}));
		while(cursor.moveToNext()){
			SocialEntity entity = new SocialEntity();
			
			String socialID = cursor.getString(Social.POS_SOCIAL_ID);
			String gwID = cursor.getString(Social.POS_GW_ID);
			String userID = cursor.getString(Social.POS_USER_ID);
			String userName = cursor.getString(Social.POS_USER_NAME);
			String userType = cursor.getString(Social.POS_USER_TYPE);
			String time = cursor.getString(Social.POS_TIME);
			String appID = cursor.getString(Social.POS_APP_ID);
			String data = cursor.getString(Social.POS_DATA);
			
			entity.setGwID(gwID);
			entity.setSocialID(socialID);
			entity.setData(data);
			entity.setTime(time);
			entity.setAppID(appID);
			entity.setUserID(userID);
			entity.setUserName(userName);
			entity.setUserType(userType);
			
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}
	
	@Override
	public SocialEntity getById(SocialEntity obj) {
		return null;
	}
	@Override
	public List<SocialEntity> findListAll(SocialEntity obj) {
		List<SocialEntity> entities = new ArrayList<SocialEntity>();
		String sql = "select * from "+Social.TABLE_SOCIAL+" where "+Social.GW_ID+"=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getTime())){
			sql += "AND (" + Social.TIME +" Between"+ " ?" + " and " +" ? )";
			long time = Long.parseLong(obj.getTime());
			Date today = new Date(time); 
			today.setHours(0);
			today.setMinutes(0);
			today.setSeconds(0);
			long begingTime = today.getTime();
			today.setHours(23);
			today.setMinutes(59);
			today.setSeconds(59);
			long endTime = today.getTime();
			
			args.add(begingTime+"");
			args.add(endTime+"");
		}
		
		sql += "order by " + Social.TIME + " desc";
		sql = "select * from (" + sql + ") " + "order by " + Social.TIME + " asc";
		
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[] {}));
		while(cursor.moveToNext()){
			SocialEntity entity = new SocialEntity();
			
			String socialID = cursor.getString(Social.POS_SOCIAL_ID);
			String gwID = cursor.getString(Social.POS_GW_ID);
			String userID = cursor.getString(Social.POS_USER_ID);
			String userName = cursor.getString(Social.POS_USER_NAME);
			String userType = cursor.getString(Social.POS_USER_TYPE);
			String time = cursor.getString(Social.POS_TIME);
			String appID = cursor.getString(Social.POS_APP_ID);
			String data = cursor.getString(Social.POS_DATA);
			
			entity.setGwID(gwID);
			entity.setSocialID(socialID);
			entity.setData(data);
			entity.setTime(time);
			entity.setAppID(appID);
			entity.setUserID(userID);
			entity.setUserName(userName);
			entity.setUserType(userType);
			
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}
	
}
