package cc.wulian.smarthomev5.dao;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.databases.entitys.Scene;
import cc.wulian.smarthomev5.support.database.AbstractDao;

public class SceneDao extends AbstractDao<SceneInfo>{

private static SceneDao instance = new SceneDao();
	
	private SceneDao(){
		
	}
	public static SceneDao getInstance(){
		return instance;
	}
	@Override
	public void insert(SceneInfo obj) {
		
	}

	@Override
	public void delete(SceneInfo obj) {
		String sql = "delete from " + Scene.TABLE_SCENE + " where "+Scene.GW_ID +"=?";
		ArrayList<String> args = new ArrayList<String>();
		args.add(obj.getGwID());
		if(!StringUtil.isNullOrEmpty(obj.getSceneID())){
			sql += " AND "+ Scene.ID+"=?";
			args.add(obj.getSceneID());
		}
		database.execSQL(sql,args.toArray(new String[]{}));
	}

	@Override
	public void update(SceneInfo obj) {
		
	}

	@Override
	public List<SceneInfo> findListAll(SceneInfo obj) {
		List<SceneInfo> scenes = new ArrayList<SceneInfo>();
		String sql = "select * from "+Scene.TABLE_SCENE+" where "+Scene.GW_ID+"=?";
		String objs[] = new String[]{obj.getGwID()};
		sql+= " order by "+Scene.ID+" asc";
		queryScenes(sql, objs, scenes);
		return scenes;
	}
	private List<SceneInfo> queryScenes(String sql, String[] args,List<SceneInfo> scenes) {
		Cursor cursor = database.rawQuery(sql,args);
		while(cursor.moveToNext()){
			SceneInfo sceneInfo = new SceneInfo();
			sceneInfo.setGwID(cursor.getString(Scene.POS_GW_ID));
			sceneInfo.setSceneID(cursor.getString(Scene.POS_ID));
			sceneInfo.setName(cursor.getString(Scene.POS_NAME));
			sceneInfo.setIcon(cursor.getString(Scene.POS_ICON));
			sceneInfo.setStatus(cursor.getString(Scene.POS_STATUS));
			scenes.add(sceneInfo);
		}
		cursor.close();
		return scenes;
	}


}
