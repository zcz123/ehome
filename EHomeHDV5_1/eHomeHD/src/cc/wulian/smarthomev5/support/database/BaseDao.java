/**
 * 本地数据Dao层接口
 */
package cc.wulian.smarthomev5.support.database;

import java.util.List;

import android.database.sqlite.SQLiteDatabase;

public interface BaseDao<T> {
	
	public abstract void insert(T obj);
	public abstract void delete(T obj);
	public abstract void update(T obj);
	public abstract T getById(T obj);
	public abstract List<T> findListAll(T obj);
	public abstract List<T> findListPage(T obj,PageParameter parameter);
	public abstract List<T> findListByIds(List<String> idList);
	public SQLiteDatabase getDatabase();
}
