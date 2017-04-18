package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Messages;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.support.database.AbstractDao;
import cc.wulian.smarthomev5.utils.DateUtil;

public class MessageDao extends AbstractDao<MessageEventEntity>{

	private static MessageDao instance = new MessageDao();
	//MessageDao通过将构造方法限定为private避免了类在外部被实例化
	private MessageDao(){
	}
	//MessageDao的唯一实例只能通过getInstance方法访问
	public static MessageDao getInstance(){
		return instance;
	}
	@Override
	public void insert(MessageEventEntity obj) {
		String sql = "insert into " + Messages.TABLE_MSG + " values ((select max("+Messages.ID+") from "+Messages.TABLE_MSG+")+1,?,?,?,?,?,?,?,?,?,?,?)";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		args.add("");
		args.add(obj.getDevID());
		args.add(obj.getEp());
		args.add(StringUtil.getStringEscapeEmpty(obj.getEpName()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getEpType()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getEpData()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getTime()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getPriority()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getType()));
		args.add(StringUtil.getStringEscapeEmpty(obj.getSmile()));
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void delete(MessageEventEntity obj) {
		
		String sql = "delete  from "+Messages.TABLE_MSG+" where "+Messages.GW_ID+"=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getMsgID())){
			String ids = obj.getMsgID();
			if(ids.endsWith(",")){
				ids = ids.substring(0,ids.length()-1);
			}
			sql +=  " AND " + Messages.ID+ " in( "+ids+" ) ";
		}
		if(!StringUtil.isNullOrEmpty(obj.getDevID())){
			sql +=  " AND " + Messages.DEVICE_ID+ "=?";
			args.add(obj.getDevID());
		}
		if(!StringUtil.isNullOrEmpty(obj.getType())){
			sql +=  " AND " + Messages.TYPE+ "=?";
			args.add(obj.getType());
		}
		database.execSQL(sql, args.toArray());
	}

	@Override
	public void update(MessageEventEntity obj) {
		
	}
	public void deleteAndInsert(MessageEventEntity obj) {
		try{
			delete(obj);
			insert(obj);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	@Override
	public MessageEventEntity getById(MessageEventEntity obj) {
		return null;
	}

	public List<MessageEventEntity> findLastDeviceMessage(MessageEventEntity obj) {
		List<MessageEventEntity> entities = new ArrayList<MessageEventEntity>();
		ArrayList<String> args = new ArrayList<String>();
		String sql = "select * from "+Messages.TABLE_MSG+" where "+Messages.GW_ID+"=? ";
		sql += " AND " + Messages.TYPE+ " in( "+obj.getType()+" ) ";
		sql += " AND (" + Messages.TIME +" Between"+ " ?" + " and " +" ? )";
		sql += " order by "+Messages.TIME+" asc ";
		
		sql = "select * from ("+sql+" ) group by  t_msg_dev_id, t_msg_dev_ep,  t_msg_type order by t_msg_time desc";
		args.add(obj.getGwID());
		Date today = new Date();
		long start = DateUtil.getTime0H0M0S(today);
		

		long end =  start + DateUtil.MILLI_SECONDS_OF_DAY;
		args.add(start+"");
		args.add(end+"");
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[] {}));
		queryList(entities, cursor);
		cursor.close();
		return entities;
	}
	@Override
	public List<MessageEventEntity> findListAll(MessageEventEntity obj) {
		List<MessageEventEntity> entities = new ArrayList<MessageEventEntity>();
		String sql = "select * from "+Messages.TABLE_MSG+" where "+Messages.GW_ID+"=? ";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getType())){
			String type = obj.getType();
			if(type.endsWith(",")){
				type = type.substring(0,type.length()-1);
			}
			sql +=  " AND " + Messages.TYPE+ " in( "+type+" ) ";
		}
		if(!StringUtil.isNullOrEmpty(obj.getTime())){
			sql += "AND (" + Messages.TIME +" Between"+ " ?" + " and " +" ? )";
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
		
		sql += "order by " + Messages.TIME + " desc";
		
		Cursor cursor = database.rawQuery(sql, args.toArray(new String[] {}));
		queryList(entities, cursor);
		cursor.close();
		return entities;
	}
	private void queryList(List<MessageEventEntity> entities, Cursor cursor) {
		while(cursor.moveToNext()){
			MessageEventEntity entity = new MessageEventEntity();
			
			String msgID = cursor.getString(Messages.POS_ID);
			String gwID = cursor.getString(Messages.POS_GW_ID);
			String userID = cursor.getString(Messages.POS_USER_ID);
			String devID = cursor.getString(Messages.POS_DEVICE_ID);
			String ep = cursor.getString(Messages.POS_DEVICE_EP);
			String epName = cursor.getString(Messages.POS_DEVICE_EP_NAME);
			String epType = cursor.getString(Messages.POS_DEVICE_EP_TYPE);
			String epData = cursor.getString(Messages.POS_DEVICE_EP_DATA);
			String time = cursor.getString(Messages.POS_TIME);
			String priority = cursor.getString(Messages.POS_PRIORITY);
			String type = cursor.getString(Messages.POS_TYPE);
			String smile = cursor.getString(Messages.POS_SMILE);
			
			entity.setGwID(gwID);
			entity.setMsgID(msgID);
			entity.setUserID(userID);
			entity.setEp(ep);
			entity.setEpType(epType);
			entity.setTime(time);
			entity.setDevID(devID);
			entity.setEpData(epData);
			entity.setEpName(epName);
			entity.setPriority(priority);
			entity.setType(type);
			entity.setSmile(smile);
			entities.add(entity);
		}
	}

}

