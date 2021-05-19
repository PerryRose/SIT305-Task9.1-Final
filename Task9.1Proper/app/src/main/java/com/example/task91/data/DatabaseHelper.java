package com.example.task91.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.task91.model.Location;
import com.example.task91.util.Util;

import java.util.LinkedList;

public class DatabaseHelper extends SQLiteOpenHelper {
    public DatabaseHelper(@Nullable Context context) {
        super(context, Util.DATABASE_NAME, null, Util.DATABASE_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String CREATE_LOCATIONS_TABLE = "CREATE TABLE " + Util.TABLE_NAME
                + "("
                + Util.LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Util.LOCATION_NAME + " TEXT, "
                + Util.LATITUDE + " TEXT, "
                + Util.LONGITUDE + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(CREATE_LOCATIONS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        String DROP_LOCATION_TABLE = "DROP TABLE IF EXISTS " + Util.TABLE_NAME;
        sqLiteDatabase.execSQL(DROP_LOCATION_TABLE);

        onCreate(sqLiteDatabase);
    }

    public long insertLocation(Location location)
    {
        try
        {
            // Get Writable Database
            SQLiteDatabase db = this.getWritableDatabase();

            // Set Values
            ContentValues contentValues = new ContentValues();
            contentValues.put(Util.LOCATION_NAME, location.getName());
            contentValues.put(Util.LATITUDE, location.getLatitude());
            contentValues.put(Util.LONGITUDE, location.getLongitude());

            // Insert note
            long newRowId = db.insert(Util.TABLE_NAME, null, contentValues);

            // Close database
            db.close();

            // Return Row id
            return newRowId;
        }
        catch (Exception e)
        {
            Log.e("Error", "Exception Caught: " + e.getMessage());
            return 0;
        }
    }

    public LinkedList<Location> getLocations()
    {
        try
        {
            // Get Readable Database
            SQLiteDatabase db = this.getReadableDatabase();

            String query = "SELECT * FROM " + Util.TABLE_NAME;

            // Query Database
            Cursor cursor = db.rawQuery(query, new String[]{});

            LinkedList<Location> locations = new LinkedList<>();

            if (cursor.moveToFirst()) {
                do {
                    int locationId = cursor.getInt(cursor.getColumnIndex(Util.LOCATION_ID));
                    String locationName = cursor.getString(cursor.getColumnIndex(Util.LOCATION_NAME));
                    String locationLat = cursor.getString(cursor.getColumnIndex(Util.LATITUDE));
                    String locationLon = cursor.getString(cursor.getColumnIndex(Util.LONGITUDE));

                    Location location = new Location(locationId, locationName, locationLat, locationLon);
                    locations.add(location);

                } while (cursor.moveToNext());
            }

            // Close cursor
            cursor.close();

            // Close Database
            db.close();

            // Return locations
            return locations;
        }
        catch (Exception e)
        {
            Log.e("Error", "Exception Caught: " + e.getMessage());
            return new LinkedList<>();
        }
    }
}
