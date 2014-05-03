package de.unimannheim.becker.todo.md.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ReminderDAO {
    private static final String ID = "_id";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String ITEM_ID = "item_id";
    private static final String REMINDERS_TABLE = "REMINDERS";
    private static final String MY_QUERY = "SELECT r._id, r.latitude, r.longitude, r.item_id FROM " + REMINDERS_TABLE
            + " r INNER JOIN ITEMS i ON r.item_id=i._id WHERE i.archived=?";

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public ReminderDAO(Context context) {
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public int deleteAll() {
        return database.delete(REMINDERS_TABLE, null, null);
    }

    public boolean storeReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(LNG, reminder.getLongtitude());
        values.put(LAT, reminder.getLatitude());
        values.put(ITEM_ID, reminder.getItemId());
        return database.insert(REMINDERS_TABLE, null, values) != -1;
    }

    public Reminder[] getActive() {
        Cursor mCursor = database.rawQuery(MY_QUERY, new String[] { String.valueOf(0) });
        if (mCursor.moveToFirst()) {
            int count = mCursor.getCount();
            Reminder[] reminders = new Reminder[count];

            for (int i = 0; i < count; i++) {
                reminders[i] = parseReminder(mCursor);
                mCursor.moveToNext();
            }
            mCursor.close();
            return reminders;
        } else {
            mCursor.close();
            return new Reminder[0];
        }
    }

    private Reminder parseReminder(Cursor mCursor) {
        Reminder reminder = new Reminder();
        reminder.setLatitude(mCursor.getDouble(mCursor.getColumnIndex(LAT)));
        reminder.setLongtitude(mCursor.getDouble(mCursor.getColumnIndex(LNG)));
        reminder.setId(mCursor.getInt(mCursor.getColumnIndex(ID)));
        reminder.setItemId(mCursor.getInt(mCursor.getColumnIndex(ITEM_ID)));
        return reminder;
    }
}
