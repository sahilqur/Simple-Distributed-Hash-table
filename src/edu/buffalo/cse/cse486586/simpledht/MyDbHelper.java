package edu.buffalo.cse.cse486586.simpledht;

import android.provider.BaseColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class MyDbHelper extends SQLiteOpenHelper implements BaseColumns {
	public static final String DBASE = "simpledht.db";
	public static final String TABLE_NAME = "messages";
	public static final String KEY = "key";
	public static final String VALUE = "value";
	
	public MyDbHelper(Context context){		
		super(context, DBASE, null, 1);
		context.deleteDatabase(DBASE);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		db.execSQL("CREATE TABLE " + TABLE_NAME + " ( " + KEY + " TEXT PRIMARY KEY, " + VALUE + " TEXT);");
	}
	

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldver, int newver) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	} 
		
}
