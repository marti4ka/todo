package de.unimannheim.becker.todo.md.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tododb";

	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String CREATE_ITEMS = "create table ITEMS ( _id integer primary key autoincrement, title text, description text, last_changed DATETIME DEFAULT CURRENT_TIMESTAMP, archived integer default 0);";
	private static final String CREATE_LOC_REMINDERS = "create table REMINDERS (_id integer primary key autoincrement, latitude real, longitude real, item_id integer);";
	private static final String DROP_ITEMS = "DROP TABLE IF EXISTS ITEMS;";
	private static final String DROP_REMINDERS = "DROP TABLE IF EXISTS REMINDERS;";
	
	public MyDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DROP_ITEMS);
		database.execSQL(CREATE_ITEMS);
		database.execSQL(DROP_REMINDERS);
        database.execSQL(CREATE_LOC_REMINDERS);
	}

	// Method is called during an upgrade of the database,
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_ITEMS);
		database.execSQL(DROP_REMINDERS);
		onCreate(database);
	}
}