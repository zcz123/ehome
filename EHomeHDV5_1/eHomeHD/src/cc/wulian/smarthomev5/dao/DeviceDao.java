package cc.wulian.smarthomev5.dao;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Device;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class DeviceDao extends AbstractDao<DeviceInfo>{

	private static DeviceDao instance = new DeviceDao();
	private DeviceDao(){
	}
	public static DeviceDao getInstance(){
		return instance;
	}
	@Override
	public void insert(DeviceInfo obj) {
		
	}

	@Override
	public void delete(DeviceInfo obj) {
		
	}

	@Override
	public void update(DeviceInfo obj) {
		
	}
	
	@Override
	public DeviceInfo getById(DeviceInfo obj) {
		return null;
	}

	public List<DeviceInfo> findListShakeRemain(DeviceInfo deviceInfo,List<ShakeEntity> objs){
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		String sql = "select * from "+Device.TABLE_DEVICE+" where "+Device.GW_ID+"=?";
		String args[] = new String[]{deviceInfo.getGwID()};
		if(objs != null){
			for(ShakeEntity d : objs){
				sql+=" and ("+Device.ID+" != '"+d.getOperateID()+"'"+" or "+Device.EP+" != '"+d.getEp()+"')";
			}
		}
		sql+= " order by "+Device.NAME+" asc";
		queryDevices(sql, args, devices);
		return devices;
	}

	public List<DeviceInfo> findListWifiRemain(DeviceInfo deviceInfo,
			List<WifiEntity> objs){
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		String sql = "select * from "+Device.TABLE_DEVICE+" where "+Device.GW_ID+"=?";
		String args[] = new String[]{deviceInfo.getGwID()};
		if(objs != null){
			for(WifiEntity d : objs){
				sql+=" and ("+Device.ID+" != '"+d.getOperateID()+"'"+" or "+Device.EP+" != '"+d.getEp()+"')";
			}
		}
		sql+= " order by "+Device.NAME+" asc";
		queryDevices(sql, args, devices);
		return devices;
	}
	public List<DeviceInfo> findListNFCRemain(DeviceInfo deviceInfo,List<NFCEntity> objs){
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		String sql = "select * from "+Device.TABLE_DEVICE+" where "+Device.GW_ID+"=?";
		String args[] = new String[]{deviceInfo.getGwID()};
		if(objs != null){
			for(NFCEntity d : objs){
				sql+=" and ("+Device.ID+" != '"+d.getID()+"'"+" or "+Device.EP+" != '"+d.getEp()+"')";
			}
		}
		sql+= " order by "+Device.NAME+" asc";
		queryDevices(sql, args, devices);
		return devices;
	}
	public List<DeviceInfo> findListTaskRemain(DeviceInfo deviceInfo,List<TaskInfo> objs){
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		String sql = "select * from "+Device.TABLE_DEVICE+" where "+Device.GW_ID+"=?";
		String args[] = new String[]{deviceInfo.getGwID()};
		if(objs != null){
			for(TaskInfo d : objs){
				sql+=" and ("+Device.ID+" != '"+d.getDevID()+"'"+" or "+Device.EP+" != '"+d.getEp()+"')";
			}
		}
		sql+= " order by "+Device.NAME+" asc";
		queryDevices(sql, args, devices);
		return devices;
	}
	private List<DeviceInfo> queryDevices(String sql, String[] args,List<DeviceInfo> devices) {
		Cursor cursor = database.rawQuery(sql,args);
		while(cursor.moveToNext()){
			DeviceInfo info = new DeviceInfo();
			info.setGwID(cursor.getString(Device.POS_GW_ID));
			info.setDevID(cursor.getString(Device.POS_ID));
			info.setType(cursor.getString(Device.POS_TYPE));
			info.setName(cursor.getString(Device.POS_NAME));
			info.setRoomID(cursor.getString(Device.POS_AREA_ID));
			DeviceEPInfo epInfo = new DeviceEPInfo();
			epInfo.setEp(cursor.getString(Device.POS_EP));
			epInfo.setEpType(cursor.getString(Device.POS_EP_TYPE));
			epInfo.setEpName(cursor.getString(Device.POS_EP_NAME));
			epInfo.setEpData(cursor.getString(Device.POS_EP_DATA));
			epInfo.setEpStatus(cursor.getString(Device.POS_EP_STATUS));
			info.setDevEPInfo(epInfo);
			devices.add(info);
		}
		cursor.close();
		return devices;
	}
	@Override
	public List<DeviceInfo> findListAll(DeviceInfo obj) {
		List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
		String sql = "select * from "+Device.TABLE_DEVICE+" where "+Device.GW_ID+"=?";
		String objs[] = new String[]{obj.getGwID()};
		if(!StringUtil.isNullOrEmpty(obj.getType())){
			sql+= " and "+Device.TYPE+" in("+obj.getType()+")";
		}
		sql+= " order by "+Device.NAME+" asc";
		queryDevices(sql, objs, devices);
		return devices;
	}
	public void insertOrUpdate(DeviceInfo devInfo, DeviceEPInfo epInfo) {
		if(isExist(devInfo, epInfo)){
			updateDevice(devInfo,epInfo);
		}else{
			insertDevice(devInfo,epInfo);
		}
	}
	
	private boolean isExist(DeviceInfo devInfo, DeviceEPInfo epInfo){
		boolean isexists=false;
		String runsql=MessageFormat.format("select count(1) from {0} where {1}=? and {2}=? and {3}=?", 
				Device.TABLE_DEVICE,
				Device.GW_ID,
				Device.ID,
				Device.EP);
		String objs[] = new String[]{devInfo.getGwID(),devInfo.getDevID(),epInfo.getEp()};
		int itemCount=0;
		Cursor cursor =database.rawQuery(runsql,objs);
		if (cursor.moveToNext()) {
			itemCount=cursor.getInt(0);
		}	
		cursor.close();
		isexists=itemCount>0;
		return isexists;
	}
	
	
	public void insertDevice(DeviceInfo devInfo, DeviceEPInfo epInfo) {
		String sql = "insert into " + Device.TABLE_DEVICE + " values (?,?,?,?,?,?,?,?,?,?,?,?,?)";
		ArrayList<String> args = new ArrayList<String>();
		args.add(devInfo.getDevID());
		args.add(StringUtil.getStringEscapeEmpty(epInfo.getEp()));
		args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpType()));
		if(!StringUtil.equals(epInfo.getEpName(), "-1")){
			args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpName()));
		}else{
			args.add(StringUtil.getStringEscapeEmpty(""));
		}
		args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpStatus()));
		args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpData()));
		args.add(StringUtil.getStringEscapeEmpty(devInfo.getName()));
		args.add(devInfo.getGwID());
		args.add(StringUtil.getStringEscapeEmpty(devInfo.getRoomID()));
		args.add(StringUtil.getStringEscapeEmpty(devInfo.getType()));
		args.add(StringUtil.getStringEscapeEmpty(devInfo.getCategory()));
		args.add("");
		args.add(StringUtil.getStringEscapeEmpty(devInfo.getIsOnline()));
		database.execSQL(sql,args.toArray(new String[]{}));
	}
	
	public void updateDevice(DeviceInfo devInfo, DeviceEPInfo epInfo) {
		String sql = "update " + Device.TABLE_DEVICE + " set ";
						
		ArrayList<String> args = new ArrayList<String>();
		if(!StringUtil.isNullOrEmpty(devInfo.getName())){
			sql += Device.NAME+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(devInfo.getName()));
		}
		if(!StringUtil.isNullOrEmpty(epInfo.getEpName())){
			sql += Device.EP_NAME+"=?,";
			if(!StringUtil.equals(epInfo.getEpName(), "-1")){
				args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpName()));
			}else{
				args.add(StringUtil.getStringEscapeEmpty(""));
			}
		}
		if(!StringUtil.isNullOrEmpty(epInfo.getEpData())){
			sql += Device.EP_DATA+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpData()));
		}
		if(!StringUtil.isNullOrEmpty(devInfo.getRoomID())){
			sql += Device.AREA_ID+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(devInfo.getRoomID()));
		}
		if(!StringUtil.isNullOrEmpty(devInfo.getType())){
			sql += Device.TYPE+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(devInfo.getType()));
		}
		if(!StringUtil.isNullOrEmpty(devInfo.getCategory())){
			sql += Device.CATEGORY+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(devInfo.getCategory()));
		}
		if(!StringUtil.isNullOrEmpty(devInfo.getIsOnline())){
			sql += Device.ONLINE+"=?,";
			args.add(StringUtil.getStringEscapeEmpty(devInfo.getIsOnline()));
		}
		sql += Device.EP_STATUS+"=?";
		args.add(StringUtil.getStringEscapeEmpty(epInfo.getEpStatus()));
		sql += " where "+Device.ID+"=? and "+Device.EP+"=? and "+Device.EP_TYPE+"=? and "+Device.GW_ID+"=?";
		database.execSQL(sql,args.toArray(new String[]{}));
	}

}
