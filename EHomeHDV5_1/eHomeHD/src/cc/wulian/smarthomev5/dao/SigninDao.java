package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.SigninRecords;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class SigninDao extends AbstractDao<GatewayInfo>{

	private static SigninDao instance = new SigninDao();
	public static SigninDao getInstance(){
		return instance;
	}
	@Override
	public void insert(GatewayInfo obj) {
		
	}
	@Override
	public void delete(GatewayInfo obj) {
		String sql = "delete from "+SigninRecords.TABLE_SIGNIN+" where 1=1 ";
		ArrayList<String> args = new ArrayList<String>();
		if(!StringUtil.isNullOrEmpty(obj.getGwID())){
			sql  += " AND "+SigninRecords.GW_ID+"=?";
			args.add(obj.getGwID());
		}
		database.execSQL(sql,args.toArray(new String[]{}));
		
	}
	@Override
	public void update(GatewayInfo obj) {
		
	}
	@Override
	public GatewayInfo getById(GatewayInfo obj) {
		List<GatewayInfo> infos = findListAll(obj);
		if(infos.size() >0)
			return infos.get(0);
		return null;
	}
	@Override
	public List<GatewayInfo> findListAll(GatewayInfo obj) {
		List<GatewayInfo> gatewayInfos = new ArrayList<GatewayInfo>();
		String sql = "select * from "+SigninRecords.TABLE_SIGNIN+" where 1=1 ";
		ArrayList<String> args = new ArrayList<String>();
		if(!StringUtil.isNullOrEmpty(obj.getGwID())){
			sql += " and "+SigninRecords.GW_ID+"=?";
			args.add(obj.getGwID());
		}
		sql += " order by "+SigninRecords.POS_GW_TIME+" desc";
		querySigninRecords(sql, args.toArray(new String[]{}), gatewayInfos);
		return gatewayInfos;
	}
	private void querySigninRecords(String sql, String[] args,List<GatewayInfo> gatewayInfos) {
		Cursor cursor = database.rawQuery(sql,args);
		while(cursor.moveToNext()){
			GatewayInfo info = new GatewayInfo();
			info.setGwID(cursor.getString(SigninRecords.POS_GW_ID));
			info.setGwPwd(cursor.getString(SigninRecords.POS_GW_PWD));
			info.setGwSerIP(cursor.getString(SigninRecords.POS_GW_IP));
			gatewayInfos.add(info);
		}
		cursor.close();
	}
}
