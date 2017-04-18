package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.NFC;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class NFCDao extends AbstractDao<NFCEntity>{
	private static NFCDao instance = new NFCDao();
	private NFCDao(){
	}
	public static NFCDao getInstance(){
		return instance;
	}
	@Override
	public void insert(NFCEntity obj) {
		try{
		String sql = "insert into "+NFC.TABLE_NFC+" values(?,?,?,?,?,?,?)";
		String objs[] = new String[]{StringUtil.getStringEscapeEmpty(obj.getGwID()),StringUtil.getStringEscapeEmpty(obj.getNfcUID()),StringUtil.getStringEscapeEmpty(obj.getID()),StringUtil.getStringEscapeEmpty(obj.getType()),StringUtil.getStringEscapeEmpty(obj.getEp()),StringUtil.getStringEscapeEmpty(obj.getEpType()),StringUtil.getStringEscapeEmpty(obj.getEpData())};
		database.execSQL(sql,objs);
		}catch(Exception e){
			
		}
	}

	@Override
	public void delete(NFCEntity obj) {
		String sql = "delete from "+NFC.TABLE_NFC+" where "+NFC.GW_ID+"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getNfcUID())){
			sql += " AND "+NFC.NFC_UID+"=?";
			args.add(obj.getNfcUID());
		}
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void update(NFCEntity obj) {
	}
	@Override
	public List<NFCEntity> findListAll(NFCEntity obj) {
		ArrayList<NFCEntity> entities = new ArrayList<NFCEntity>();
		String sql = "select * from "+NFC.TABLE_NFC+" where "+NFC.GW_ID+"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getNfcUID())){
			sql += " AND "+NFC.NFC_UID+"=?";
			args.add(obj.getNfcUID());
		}
		Cursor cursor = database.rawQuery(sql,args.toArray(new String[]{}));
		while(cursor.moveToNext()){
			NFCEntity entity = new NFCEntity();
			entity.setGwID(cursor.getString(NFC.POS_GW_ID));
			entity.setNfcUID(cursor.getString(NFC.POS_NFC_UID));
			entity.setID(cursor.getString(NFC.POS_OPERATION_ID));
			entity.setType(cursor.getString(NFC.POS_OPERATION_TYPE));
			entity.setEp(cursor.getString(NFC.POS_DEVICE_EP));
			entity.setEpType(cursor.getString(NFC.POS_DEVICE_EP_TYPE));
			entity.setEpData(cursor.getString(NFC.POS_DEVICE_EP_DATA));
			entities.add(entity);
		}
		cursor.close();
		return entities;
	}
	
}
