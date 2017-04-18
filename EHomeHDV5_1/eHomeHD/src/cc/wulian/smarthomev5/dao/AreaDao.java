package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class AreaDao extends AbstractDao<RoomInfo>{

	private static AreaDao instance = new AreaDao();
	
	private AreaDao (){
		
	}
	public static AreaDao getInstance(){
		return instance;
	}
	@Override
	public void insert(RoomInfo obj) {
		
	}

	@Override
	public void delete(RoomInfo obj) {
		
	}

	@Override
	public void update(RoomInfo obj) {
		
	}

	@Override
	public List<RoomInfo> findListAll(RoomInfo obj) {
		List<RoomInfo> rooms = new ArrayList<RoomInfo>();
		String sql = "select * from "+Area.TABLE_AREA+" where "+Area.GW_ID+"=?";
		String objs[] = new String[]{obj.getGwID()};
		sql+= " order by "+Area.ID+" asc";
		queryAreas(sql, objs, rooms);
		return rooms;
	}
	private List<RoomInfo> queryAreas(String sql, String[] args,List<RoomInfo> rooms) {
		Cursor cursor = database.rawQuery(sql,args);
		while(cursor.moveToNext()){
			RoomInfo info = new RoomInfo();
			info.setGwID(cursor.getString(Area.POS_GW_ID));
			info.setRoomID(cursor.getString(Area.POS_ID));
			info.setName(cursor.getString(Area.POS_NAME));
			info.setIcon(cursor.getString(Area.POS_ICON));
			info.setCount(cursor.getString(Area.POS_COUNT));
			rooms.add(info);
		}
		cursor.close();
		return rooms;
	}

}
