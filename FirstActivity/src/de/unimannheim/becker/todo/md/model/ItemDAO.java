package de.unimannheim.becker.todo.md.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemDAO {
	private static final String DESCRIPTION = "description";
	private final static String TITLE = "title";

	private MyDatabaseHelper dbHelper;

	private SQLiteDatabase database;

	public final static String EMP_TABLE = "ITEMS"; // name of table

	public final static String EMP_ID = "_id"; // id value for employee

	public ItemDAO(Context context) {
		dbHelper = new MyDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
	}

	public int deleteAll() {
		return database.delete(EMP_TABLE, null, null);
	}

	public boolean storeItem(Item item) {
		ContentValues values = new ContentValues();
		values.put(TITLE, item.getTitle());
		values.put(DESCRIPTION, item.getDescription());
		return database.insert(EMP_TABLE, null, values) != -1;
	}

	/**
	 * @return the not archived items
	 */
	public Item[] getItems() {
		String[] cols = new String[] { DESCRIPTION, TITLE };
		Cursor mCursor = database.query(true, EMP_TABLE, cols, null, null, null, null, null, null);
		if (mCursor.moveToFirst()) {
			int count = mCursor.getCount();
			Item[] items = new Item[count];

			for (int i = 0; i < count; i++) {
				items[i] = parseItem(mCursor);
				mCursor.moveToNext();
			}
			mCursor.close();
			return items;
		} else {
			mCursor.close();
			return new Item[0];
		}
	}

	private Item parseItem(Cursor mCursor) {
		Item item = new Item();
		item.setDescription(mCursor.getString(mCursor.getColumnIndex(DESCRIPTION)));
		item.setTitle(mCursor.getString(mCursor.getColumnIndex(TITLE)));
		return item;
	}

	// public Item[] getArchived() {
	// return new Item[0];
	// }
	//
	// public boolean archiveItem(Item item) {
	// return false;
	// }
	//
	// public boolean unarchive(Item item) {
	// return false;
	// }
}
