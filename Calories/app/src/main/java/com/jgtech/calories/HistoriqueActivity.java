package com.jgtech.calories;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.jgtech.calories.databinding.ActivityHistoriqueBinding;

import java.util.Date;

public class HistoriqueActivity extends DrawerBaseActivity {

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_SELECTED = "selected";

    ActivityHistoriqueBinding activityHistoriqueBinding;
    private String selected;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityHistoriqueBinding = ActivityHistoriqueBinding.inflate(getLayoutInflater());
        setContentView(activityHistoriqueBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.HistoriqueActivityName));

        //get the selected user
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        selected = sharedPreferences.getString(PREF_SELECTED, null);

        //get the data from the database
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String[] projection = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USER, DatabaseHelper.COLUMN_CAL, DatabaseHelper.COLUMN_DATE, DatabaseHelper.COLUMN_WEIGHT, DatabaseHelper.COLUMN_HEIGHT};
        String selection = DatabaseHelper.COLUMN_USER + " = ?";
        String[] selectionArgs = {selected};
        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        String result = "";

        //get the data from the cursor and put it in the result string
        while(cursor.moveToNext()){
            int cal = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CAL));
            String date = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            int weight = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_WEIGHT));
            int height = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_HEIGHT));

            result += getResources().getString(R.string.resultLine, date , cal, weight, height);
        }

        //set the textview with the result string
        textView = findViewById(R.id.textView4);
        textView.setText(result);

        cursor.close();
        db.close();
    }
}