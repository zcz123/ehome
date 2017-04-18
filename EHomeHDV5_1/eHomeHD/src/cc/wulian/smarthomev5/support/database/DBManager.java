package cc.wulian.smarthomev5.support.database;

import android.database.sqlite.SQLiteDatabase;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.databases.DataBaseHelper;

public class DBManager
{
	private DataBaseHelper helper;
	private SQLiteDatabase database;
	private DBManager()
	{
		helper = MainApplication.getApplication().mDataBaseHelper;
		database = helper.getWritableDatabase();
	}
	
	private static DBManager dbManager = null;
	
	public static DBManager getInstance()
	{
		if(dbManager == null){
			dbManager = new DBManager();
		}
		return dbManager;
	}
	
	public void closeDB()
	{
		if(database!= null && database.isOpen())
		{
			database.close();
		}
		
	}
	public SQLiteDatabase getDatabase(){
		return database;
	}
	
}
