package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Log;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.databases.entitys.Monitor;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class CameraDao extends AbstractDao<CameraInfo>{

	private static CameraDao instance = new CameraDao();
	public static CameraDao getInstance(){
		return instance;
	}
	@Override
	public void insert(CameraInfo obj) {
		String sql = "insert into " + Monitor.TABLE_MONITOR + " values ((select max("+Monitor.ID+") from "+Monitor.TABLE_MONITOR+")+1,?,?,?,?,?,?,?,?,?,?,?)";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwId());
		args.add(obj.getCamName());
		args.add(obj.getIconId()+"");
		args.add(obj.getCamType()+"");
		args.add(obj.getUid());
		args.add(obj.getHost());
		args.add(obj.getPort()+"");
		args.add(obj.getUsername());
		args.add(obj.getPassword());
		args.add(obj.getBindDev());
		args.add(obj.getAreaID());
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void delete(CameraInfo obj) {
		String sql = "delete from " + Monitor.TABLE_MONITOR + " where "+Monitor.GW_ID +"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwId());
		if(obj.getCamId() > 0){
			sql += " AND "+Monitor.ID +"=?";
			args.add(obj.getCamId()+"");
		}
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void update(CameraInfo obj) {
		String sql = "update " + Monitor.TABLE_MONITOR + " set "
	                  +Monitor.NAME+"=?,"+Monitor.ICON+"=?,"+Monitor.TYPE+"=?,"+Monitor.UID+"=?,"
	                  +Monitor.HOST+"=?,"+Monitor.PORT+"=?,"+Monitor.USER+"=?,"+Monitor.PWD+"=?,"
	                  +Monitor.BIND_DEV_ID+"=?,"+Monitor.AREAID+"=?"+ " where "+Monitor.GW_ID +"=?"+ " and "+Monitor.ID +"=?";
		ArrayList<String> args = new ArrayList<String>();

		args.add(obj.getCamName());
		args.add(obj.getIconId()+"");
		args.add(obj.getCamType()+"");
		args.add(obj.getUid());
		args.add(obj.getHost());
		args.add(obj.getPort()+"");
		args.add(obj.getUsername());
		args.add(obj.getPassword());
		args.add(obj.getBindDev());
		args.add(obj.getAreaID());
		args.add(obj.getGwId());
		args.add(obj.getCamId()+"");
		
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public CameraInfo getById(CameraInfo obj) {
		List<CameraInfo> cameraInfos = findListAll(obj);
		if(cameraInfos.size() >0)
			return cameraInfos.get(0);
		return null;
	}

	/**
	 * 根据UID来判断在数据库中是否存在
	 * add  mabo
	 * @param obj
	 * @return
     */
	public boolean isExistCamera(CameraInfo obj){
		boolean flag=false;
		ArrayList<String> args = new ArrayList<String>();
		String sql = "select * from "+Monitor.TABLE_MONITOR+" where "+Monitor.GW_ID+"=?";
		args.add(obj.getGwId());
		if (obj.getUid()!=null){
			sql+=" AND "+Monitor.UID+" = ?";
			args.add(obj.getUid()+"");
		}
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[]{}));
		if (cursor.getCount()==0){
			flag=true;
		}
		cursor.close();
		return flag;
	}
	@Override
	public List<CameraInfo> findListAll(CameraInfo obj) {
		List<CameraInfo> entities = new ArrayList<CameraInfo>();
		ArrayList<String> args = new ArrayList<String>();
		String sql = "select * from "+Monitor.TABLE_MONITOR+" where "+Monitor.GW_ID+"=?";
		args.add(obj.getGwId());
		if(obj.getCamId() > 0){
			sql += " AND "+Monitor.ID +" = ?";
			args.add(obj.getCamId()+"");
		}
		if(!StringUtil.isNullOrEmpty(obj.getAreaID())){
			if(Area.AREA_DEFAULT.equals(obj.getAreaID())){
				sql += " AND "+Monitor.AREAID+" not in "+" (select "+ Monitor.AREAID +" from "+Area.TABLE_AREA+")";
			}
			else{
			sql += " AND "+Monitor.AREAID +" = ?";
			args.add(obj.getAreaID()+"");
		    }
		}
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[]{}));
		while(cursor.moveToNext()){
			CameraInfo info = new CameraInfo();
			info.setCamId(StringUtil.toInteger(cursor.getString(Monitor.POS_ID)));
			info.setCamName(cursor.getString(Monitor.POS_NAME));
			info.setIconId(StringUtil.toInteger(cursor.getString(Monitor.POS_ICON)));
			info.setCamType(StringUtil.toInteger(cursor.getString(Monitor.POS_TYPE)));
			info.setGwId(cursor.getString(Monitor.POS_GW_ID));
			info.setUid(cursor.getString(Monitor.POS_UID));
			info.setHost(cursor.getString(Monitor.POS_HOST));
			info.setPort(StringUtil.toInteger(cursor.getString(Monitor.POS_PORT)));
			info.setUsername(cursor.getString(Monitor.POS_USER));
			info.setPassword(cursor.getString(Monitor.POS_PWD));
			info.setBindDev(cursor.getString(Monitor.POS_BIND_DEV_ID));
			info.setAreaID(cursor.getString(Monitor.POS_AREAID));
			entities.add(info);
		}
		cursor.close();
		return entities;
	}


	
	@Override
	public List<CameraInfo> findListByIds(List<String> idList) {
		return super.findListByIds(idList);
	}


}
