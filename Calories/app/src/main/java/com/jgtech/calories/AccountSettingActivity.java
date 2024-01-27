package com.jgtech.calories;

import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jgtech.calories.databinding.ActivityAccountSettingBinding;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class AccountSettingActivity extends DrawerBaseActivity {

    ActivityAccountSettingBinding activityAccountSettingBinding;

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_SELECTED = "selected";

    private TextView userName;
    private TextView userCreatDate;
    private ImageView userPP;
    private Button deleteAccountBtn;

    private String selected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAccountSettingBinding = activityAccountSettingBinding.inflate(getLayoutInflater());
        setContentView(activityAccountSettingBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.ProfileActivityName));

        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        selected = sharedPreferences.getString(PREF_SELECTED, null);

        userName = findViewById(R.id.userName);
        userCreatDate = findViewById(R.id.userCreatDate);
        userPP = findViewById(R.id.userPP);
        deleteAccountBtn = findViewById(R.id.deleteAccountBtn);

        deleteAccountBtn.setOnClickListener(v -> deleteAccount());

        fillInformation();

    }

    private void deleteAccount() {
        //delete the selected account
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(selected);
        editor.remove(PREF_SELECTED);
        editor.apply();

        // delete all rows of the selected user
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.COLUMN_USER + " = ?", new String[] {selected});
        db.close();

        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void fillInformation(){
        // show the name and url in the edit text
        SharedPreferences sharedUser = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        String user = sharedUser.getString(selected, null);

        String name = "";
        String url = "";
        String date = "";

        //convert string to json
        if(user == null){
        }else {
            try {
                JSONObject jsonObject = new JSONObject(user);
                name = jsonObject.getString("name");
                url = jsonObject.getString("url");
                date = jsonObject.getString("date");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        userName.setText(name);
        userCreatDate.setText(getString(R.string.createdDate, date));

        //load the image
        Picasso.get().load(url)
                .resize(200, 200)
                .centerCrop()
                .into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                drawable.setCornerRadius(100.0f);
                drawable.setAntiAlias(true);
                userPP.setImageDrawable(drawable);
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }
}