package com.design.locationsaver;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class DataBaseHelper extends SQLiteOpenHelper {


    public static final String LOCATION_TABLE = "LOCATION_TABLE";
    public static final String COLUMN_PLACE_NAME = "PLACE_NAME";
    public static final String COLUMN_X_AXIS = "X_AXIS";
    public static final String COLUMN_Y_AXIS = "Y_AXIS";
    public static final String COLUMN_IS_CURRENT_LOCATION = "IS_CURRENT_LOCATION";
    public static final String COLUMN_ID = "ID";

    public DataBaseHelper(@Nullable Context context) {
        super(context, "location.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTableStatement = "CREATE TABLE " + LOCATION_TABLE + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_PLACE_NAME + " TEXT, " + COLUMN_X_AXIS + " DOUBLE, " + COLUMN_Y_AXIS + " DOUBLE, " + COLUMN_IS_CURRENT_LOCATION + " BOOL)";
        db.execSQL(createTableStatement);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public boolean addOne(LocationModel locationModel) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put(COLUMN_PLACE_NAME, locationModel.getPlaceName());
        cv.put(COLUMN_X_AXIS, locationModel.getX());
        cv.put(COLUMN_Y_AXIS, locationModel.getY());
        cv.put(COLUMN_IS_CURRENT_LOCATION, locationModel.isCurrentLocation());

        long insert = db.insert(LOCATION_TABLE,null, cv);
        if (insert == -1) {
            return false;
        }
        else {
            return true;
        }
    }

    public boolean deleteOne(LocationModel locationModel) {

        SQLiteDatabase db = this.getWritableDatabase();
        String queryString = "DELETE FROM " + LOCATION_TABLE + " WHERE " + COLUMN_ID + " = " + locationModel.getId();

        Cursor cursor = db.rawQuery(queryString, null);
        if(cursor.moveToFirst()) {
            return true;
        }
        else {
            return false;
        }
    }

    public List<LocationModel> getAllLocations() {
        List<LocationModel> returnList = new ArrayList<>();
        String queryString = "SELECT * FROM " + LOCATION_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(queryString, null);
        // if there are results
        if(cursor.moveToFirst()) {
            do {
                int locationID = cursor.getInt(0);
                String placeName = cursor.getString(1);
                double xAxis = cursor.getDouble(2);
                double yAxis = cursor.getDouble(3);
                boolean isCurrentLocation = cursor.getInt(4) == 1 ? true: false;

                LocationModel newLocation = new LocationModel(locationID, placeName, xAxis, yAxis, isCurrentLocation);
                returnList.add(newLocation);

            } while (cursor.moveToNext());

        }
        else {
            // do not add anything to the list
        }
        cursor.close();
        db.close();
        return returnList;
    }
}
