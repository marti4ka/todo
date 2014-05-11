package de.unimannheim.becker.todo.md.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class LocationDAO {
    private static final String LOCATIONS_TABLE = "LOCATIONS";
    private static final String MAPPING_TABLE = "MAPPING";
    private static final String ITEM_ID = "item_id";
    private static final String LOCATION_ID = "location_id";
    private static final String LAT = "latitude";
    private static final String LNG = "longitude";
    private static final String ID = "_id";
    private static final String SELECT_ALL_ACTIVE_LOCATIONS = "SELECT l._id, l.latitude, l.longitude FROM "
            + LOCATIONS_TABLE + " l INNER JOIN " + MAPPING_TABLE
            + " m ON l._id=m.location_id INNER JOIN ITEMS i ON m.item_id=i._id WHERE i.archived=?";
    private static final String SELECT_LOCATIONS_FOR_ITEM = "SELECT l._id, l.latitude, l.longitude FROM "
            + LOCATIONS_TABLE + " l INNER JOIN " + MAPPING_TABLE + " m ON l._id=m.location_id WHERE m.item_id=?";
    private static final String SELECT_LOCATION_TITLE_FROM_ACTIVE_ITEM = "SELECT i.title FROM ITEMS i LEFT JOIN "
            + MAPPING_TABLE + " m ON i._id=m.item_id WHERE m.location_id=? AND i.archived=0";

    private MyDatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public LocationDAO(Context context) {
        dbHelper = new MyDatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * for test only
     * 
     * @return
     */
    public void deleteAll() {
        database.delete(MAPPING_TABLE, null, null);
        database.delete(LOCATIONS_TABLE, null, null);
    }

    public boolean storeLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(LNG, location.getLongtitude());
        values.put(LAT, location.getLatitude());
        long locationId = database.insert(LOCATIONS_TABLE, null, values);
        location.setId(locationId);
        return locationId != -1;
    }

    public Location[] getAll() {
        String[] cols = { ID, LAT, LNG };
        Cursor mCursor = database.query(true, LOCATIONS_TABLE, cols, null, null, null, null, null, null);
        return parseLocationCursor(mCursor, false);
    }

    public Location[] getLocationsForItem(long itemId) {
        Cursor mCursor = database.rawQuery(SELECT_LOCATIONS_FOR_ITEM, new String[] { String.valueOf(itemId) });
        return parseLocationCursor(mCursor, true);
    }

    private Location[] parseLocationCursor(Cursor mCursor, boolean mapped) {
        if (mCursor.moveToFirst()) {
            int count = mCursor.getCount();
            Location[] reminders = new Location[count];

            for (int i = 0; i < count; i++) {
                reminders[i] = parseLocation(mCursor);
                reminders[i].setMapped(mapped);
                if (mapped) {
                    Cursor titlesCursor = database.rawQuery(SELECT_LOCATION_TITLE_FROM_ACTIVE_ITEM, new String[] { String.valueOf(reminders[i].getId()) });
                    if(titlesCursor.moveToFirst()) {
                        reminders[i].setTitle(titlesCursor.getString(0));
                    }
                }
                mCursor.moveToNext();
            }
            mCursor.close();
            return reminders;
        } else {
            mCursor.close();
            return new Location[0];
        }
    }

    public boolean mapLocationToItem(double locationId, double itemId) {
        ContentValues values = new ContentValues();
        values.put(LOCATION_ID, locationId);
        values.put(ITEM_ID, itemId);
        database.insert(MAPPING_TABLE, null, values);
        return true;
    }

    public boolean unMapLocationToItem(double locationId, double itemId) {
        String where = LOCATION_ID + " =? AND " + ITEM_ID + " =?";
        String[] args = { String.valueOf(locationId), String.valueOf(itemId) };
        database.delete(MAPPING_TABLE, where, args);
        return true;
    }

    private Location parseLocation(Cursor mCursor) {
        Location location = new Location();
        location.setLatitude(mCursor.getDouble(mCursor.getColumnIndex(LAT)));
        location.setLongtitude(mCursor.getDouble(mCursor.getColumnIndex(LNG)));
        location.setId(mCursor.getInt(mCursor.getColumnIndex(ID)));
        return location;
    }

    public Location[] getAllLocationsForActiveItems() {
        Cursor mCursor = database.rawQuery(SELECT_ALL_ACTIVE_LOCATIONS, new String[] { String.valueOf(0) });
        return parseLocationCursor(mCursor, true);
    }
}
