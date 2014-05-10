package de.unimannheim.becker.todo.md.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ItemDAO {
    private static final String DESCRIPTION = "description";
    private static final String TITLE = "title";
    private static final String ARCHIVED = "archived";
    private static final String TIMESTAMP = "last_changed";
    private static final String ID = "_id"; 
    private static final String EMP_TABLE = "ITEMS"; 

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase database;

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
        long itemId = database.insert(EMP_TABLE, null, values);
        item.setId(itemId);
        return itemId != -1;
    }

    /**
     * @return the not archived items
     */
    public Item[] getItems() {
        return getAll(0);
    }

    public Item[] getArchived() {
        return getAll(1);
    }

    public boolean archiveItem(long itemId) {
        ContentValues values = new ContentValues();
        values.put(ARCHIVED, 1);
        int affectedRows = database.update(EMP_TABLE, values, "_id = " + itemId, null);
        return affectedRows == 1;
    }
    
    private Item[] getAll(int archived) {
        String[] cols = new String[] { ID, DESCRIPTION, TITLE, TIMESTAMP };
        String selection = "archived = " + archived;
        Cursor mCursor = database.query(true, EMP_TABLE, cols, selection, null, null, null, TIMESTAMP, null);
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
        item.setTimestamp(mCursor.getLong(mCursor.getColumnIndex(TIMESTAMP)));
        item.setId(mCursor.getInt(mCursor.getColumnIndex(ID)));
        return item;
    }
}
