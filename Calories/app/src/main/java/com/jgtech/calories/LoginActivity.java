package com.jgtech.calories;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_USER = "user";
    private final static String PREF_LAST_ID = "lastIDUser";
    private final static String PREF_SELECTED = "selected";

    private ViewGroup accountButtonContainer;
    private ArrayList<String> namesList = new ArrayList<>();

    private ArrayList<String> urlsList = new ArrayList<>();
    private ArrayList<Integer> idList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        accountButtonContainer = findViewById(R.id.accountButtonContainer);
        accountButtonContainer.removeAllViews();

        getAllAccounts();

        loadAccount();
    }

    private void getAllAccounts() {
        // Get all the accounts from the preferences
        // For each account get the name and the url of the image
        // Add the name and the url to the lists
        SharedPreferences preferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        Map<String, ?> allAccounts = preferences.getAll();

        for(Map.Entry<String, ?> entry : allAccounts.entrySet()){
            String key = entry.getKey();
            if(key.startsWith(PREF_USER)){
                String json = (String) entry.getValue();
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String name = jsonObject.getString("name");
                    String url = jsonObject.getString("url");
                    int id = jsonObject.getInt("id");
                    namesList.add(name);
                    urlsList.add(url);
                    idList.add(id);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private void loadAccount(){
        // Load the account buttons
        for(int i = 0; i < namesList.size() + 1; i++){
            final int id, index = i;

            View accountButton = getLayoutInflater().inflate(R.layout.account_button, accountButtonContainer, false);
            Button button = accountButton.findViewById(R.id.customAccountButton);

            if(i == namesList.size()){
                button.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                button.setText(R.string.btnContentAdd);

                id = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE).getInt(PREF_LAST_ID, 0) + 1;
            }else{
                String name = namesList.get(i);
                String url = urlsList.get(i);
                id = idList.get(i);
                button.setText(name);

                // Load the image from the url and set it as the button icon, when the image is downloaded it put it in the cache so it will be faster to load it the next time
                Picasso.get().load(url)
                             .resize(200, 200)
                             .centerCrop()
                             .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                        drawable.setCornerRadius(100.0f);
                        drawable.setAntiAlias(true);

                        button.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);
                    }
                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {}
                });
            }

            // Set the listener on the button
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(index == namesList.size()){
                        // go to create account activity
                        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
                        intent.putExtra("id", id);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    }else {
                        // save the selected account
                        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(PREF_SELECTED, "user" + id);
                        editor.apply();

                        startActivity(new Intent(LoginActivity.this, AccountSettingActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                    }
                }
            });
            // add the button to the container
            accountButtonContainer.addView(accountButton);


            if(i == 3 || i == namesList.size()){
                break;
            }
        }
    }
}