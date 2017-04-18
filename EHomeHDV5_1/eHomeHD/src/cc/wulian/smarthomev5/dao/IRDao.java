package cc.wulian.smarthomev5.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Device;
import cc.wulian.smarthomev5.databases.entitys.DeviceIR;
import cc.wulian.smarthomev5.support.database.AbstractDao;

/**
 * 红外转发配置表
 * @author Administrator
 *
 */
public class IRDao extends AbstractDao<DeviceIRInfo>{

	private static IRDao instance = new IRDao();
	private IRDao(){
	}
	public static IRDao getInstance(){
		return instance;
	}
	@Override
	public void insert(DeviceIRInfo obj) {
		String sql = "insert into " + DeviceIR.TABLE_DEVICE_IR + " values (?,?,?,?,?,?,?,?)";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getDeviceID());
		args.add(obj.getEp());
		args.add(obj.getGwID());
		args.add(StringUtil.getStringEscapeEmpty(obj.getKeyset()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getIRType()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getCode()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getName()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getStatus()));
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	public void insertOrUpdate(DeviceIRInfo obj){
		if(isExists(obj)){
			delete(obj);
			insert(obj);
		}else{
			insert(obj);
		}
	}
	
	private boolean isExists(DeviceIRInfo obj){
		boolean isexists=false;
		String runsql=MessageFormat.format("select count(1) from {0} where {1}=? and {2}=? and {3}=? and {4}=?", 
				DeviceIR.TABLE_DEVICE_IR,
				DeviceIR.GW_ID,
				DeviceIR.ID,
				DeviceIR.EP,
				DeviceIR.KEYSET);
		String objs[] = new String[]{obj.getGwID(),obj.getDeviceID(),obj.getEp(),obj.getKeyset()};
		int itemCount=0;
		Cursor cursor =database.rawQuery(runsql,objs);
		if (cursor.moveToNext()) {
			itemCount=cursor.getInt(0);
		}	
		cursor.close();
		isexists=itemCount>0;
		return isexists;
	}
	@Override
	public void delete(DeviceIRInfo obj) {
		String sql = "delete from " + DeviceIR.TABLE_DEVICE_IR + " where "+DeviceIR.GW_ID +"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getDeviceID())){
			sql += " AND "+ DeviceIR.ID+"=?";
			args.add(obj.getDeviceID());
		}
		if(!StringUtil.isNullOrEmpty(obj.getEp())){
			sql += " AND "+ DeviceIR.EP+"=?";
			args.add(obj.getEp());
		}
		if(!StringUtil.isNullOrEmpty(obj.getIRType()) ){
			sql += " AND "+DeviceIR.TYPE+" = ?";
			args.add(obj.getIRType());
		}
		if(!StringUtil.isNullOrEmpty(obj.getKeyset()) ){
			sql += " AND "+DeviceIR.KEYSET+" = ?";
			args.add(obj.getKeyset());
		}
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void update(DeviceIRInfo obj) {
		String sql = "update " + DeviceIR.TABLE_DEVICE_IR + " set "+DeviceIR.TYPE+"=?,"+DeviceIR.CODE+"=?,"+DeviceIR.NAME+"=?,"+DeviceIR.STATUS+"=?"+" where "+DeviceIR.ID+"=?,"+DeviceIR.EP+"=?,"+DeviceIR.GW_ID+"=?,"+DeviceIR.KEYSET+"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(StringUtil.getStringEscapeEmpty(obj.getIRType()));
		args.add(obj.getCode());
		args.add(StringUtil.getStringEscapeEmpty(obj.getName()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getStatus()));
		args.add(obj.getDeviceID());
		args.add(obj.getEp());
		args.add(obj.getGwID());
		args.add(obj.getKeyset());
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public DeviceIRInfo getById(DeviceIRInfo obj) {
		List<DeviceIRInfo> infos = findListAll(obj);
		if(infos.size() >0)
			return infos.get(0);
		return null;
	}
	@Override
	public List<DeviceIRInfo> findListAll(DeviceIRInfo obj) {
		List<DeviceIRInfo> irEntites = new ArrayList<DeviceIRInfo>();
		String sql = "select * from "+DeviceIR.TABLE_DEVICE_IR+" where "+DeviceIR.GW_ID+"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getDeviceID())){
			sql+= " and "+DeviceIR.ID+" =? ";
			args.add(obj.getDeviceID());
		}
		if(!StringUtil.isNullOrEmpty(obj.getEp())){
			sql+= " and "+DeviceIR.EP+" = ?";
			args.add(obj.getEp());
		}
		if(!StringUtil.isNullOrEmpty(obj.getIRType()) ){
			sql += " and "+DeviceIR.TYPE+" = ?";
			args.add(obj.getIRType());
		}
		if(!StringUtil.isNullOrEmpty(obj.getStatus())){
			sql += " and "+DeviceIR.STATUS+" = ?";
			args.add(obj.getStatus());
		}
		sql+= " order by "+DeviceIR.CODE+" asc";
		queryIRs(sql, args.toArray(new String[]{}), irEntites);
		return irEntites;
	}
	private void queryIRs(String sql, String[] args,List<DeviceIRInfo> irEntites) {
		Cursor cursor = database.rawQuery(sql,args);
		while(cursor.moveToNext()){
			DeviceIRInfo dirInfo = new DeviceIRInfo();
			dirInfo.setDeviceID(cursor.getString(DeviceIR.POS_ID));
			dirInfo.setEp(cursor.getString(DeviceIR.POS_EP));
			dirInfo.setGwID(cursor.getString(DeviceIR.POS_GW_ID));
			dirInfo.setIRType(cursor.getString(DeviceIR.POS_TYPE));
			dirInfo.setCode(cursor.getString(DeviceIR.POS_CODE));
			dirInfo.setKeyset(cursor.getString(DeviceIR.POS_KEYSET));
			dirInfo.setName(cursor.getString(DeviceIR.POS_NAME));
			dirInfo.setStatus(cursor.getString(DeviceIR.POS_STATUS));
			irEntites.add(dirInfo);
		}
		cursor.close();
	}


}
