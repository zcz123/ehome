package cc.wulian.smarthomev5.support.database;

import java.util.ArrayList;
import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public abstract class AbstractDao<T> implements BaseDao<T> {

	protected SQLiteDatabase database ;
	public AbstractDao(){
		database = DBManager.getInstance().getDatabase();
	}
	@Override
	public List<T> findListAll(T obj) {
		return new ArrayList<T>();
	}
	@Override
	public List<T> findListPage(T obj, PageParameter parameter) {
		return new ArrayList<T>() ;
	}
	@Override
	public List<T> findListByIds(List<String> idList) {
		return new ArrayList<T>();
	}
	
	@Override
	public T getById(T obj) {
		return null;
	}
	@Override
	public SQLiteDatabase getDatabase() {
		return database;
	}
}
