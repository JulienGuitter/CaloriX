package com.jgtech.calories;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "CaloriX.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "historique";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USER = "user";
    public static final String COLUMN_WEIGHT = "weight";
    public static final String COLUMN_HEIGHT = "height";
    public static final String COLUMN_DATE = "date";
    public static final String COLUMN_CAL = "cal";

    private static final String TABLE_CREATE =
            "CREATE TABLE "+ TABLE_NAME +" ( " +
                    COLUMN_ID +" INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    COLUMN_USER +" VARCHAR(10), " +
                    COLUMN_WEIGHT +" INT, " +
                    COLUMN_HEIGHT +" INT, " +
                    COLUMN_DATE +" DATE, " +
                    COLUMN_CAL +" INT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }
}
