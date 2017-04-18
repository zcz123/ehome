package cc.wulian.smarthomev5.databases;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class WLDataProvider extends ContentProvider
{
	private static final String AUTHORITY = "cc.wulian.smarthomev5";
	public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);

	// for SigninRecords
	private static final int GW_RECORDS 														= 0000;
	private static final int GW_RECORDS_ID													= 0001;

	//
	private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

	static{
		mUriMatcher.addURI(AUTHORITY, "records", GW_RECORDS);
		mUriMatcher.addURI(AUTHORITY, "records/*", GW_RECORDS_ID);
	}
	
	private CustomDataBaseHelper mBaseHelper;

	@Override
	public boolean onCreate(){
		mBaseHelper = CustomDataBaseHelper.getInstance(getContext());
		return true;
	}

	@Override
	public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder ){
		return null;
	}

	@Override
	public String getType( Uri uri ){
		return null;
	}

	@Override
	public Uri insert( Uri uri, ContentValues values ){
		return null;
	}

	@Override
	public int delete( Uri uri, String selection, String[] selectionArgs ){
		return 0;
	}

	@Override
	public int update( Uri uri, ContentValues values, String selection, String[] selectionArgs ){
		return 0;
	}
}