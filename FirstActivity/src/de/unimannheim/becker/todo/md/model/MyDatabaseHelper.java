package de.unimannheim.becker.todo.md.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {

	private static final String DATABASE_NAME = "tododb";

	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String CREATE_ITEMS = "create table ITEMS ( _id integer primary key autoincrement, title text, description text, last_changed DATETIME DEFAULT CURRENT_TIMESTAMP, archived integer default 0);";
	private static final String CREATE_LOCATIONS = "create table LOCATIONS (_id integer primary key autoincrement, latitude real, longitude real);";
	private static final String CREATE_MAPPING = "create table MAPPING (item_id integer, location_id integer);";
    private static final String DROP_MAPPING = "DROP TABLE IF EXISTS MAPPING;";
	private static final String DROP_ITEMS = "DROP TABLE IF EXISTS ITEMS;";
	private static final String DROP_LOCATIONS = "DROP TABLE IF EXISTS LOCATIONS;";
	
	public MyDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	// Method is called during creation of the database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DROP_ITEMS);
		database.execSQL(CREATE_ITEMS);
		database.execSQL(DROP_LOCATIONS);
        database.execSQL(CREATE_LOCATIONS);
        database.execSQL(DROP_MAPPING);
        database.execSQL(CREATE_MAPPING);
	}

	// Method is called during an upgrade of the database,
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		database.execSQL(DROP_ITEMS);
		database.execSQL(DROP_LOCATIONS);
		onCreate(database);
	}
}