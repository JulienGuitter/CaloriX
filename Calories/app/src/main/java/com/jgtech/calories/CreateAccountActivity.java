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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CreateAccountActivity extends AppCompatActivity {

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_USER = "user";
    private final static String PREF_LAST_ID = "lastIDUser";
    private final static String PREF_SELECTED = "selected";
    private final static String PREF_URL = "url";

    private ImageView userPP;
    private EditText userName;
    private Button validateButton;
    private TextView userPPContent;

    private String name;
    private String date;
    private Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        userPP = findViewById(R.id.signinPP);
        userName = findViewById(R.id.signinName);
        validateButton = findViewById(R.id.signinSubmit);

        userPP.setOnClickListener(v -> changePP());
        validateButton.setOnClickListener(v -> validate());

        // get the id of the new user
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        if(id == -1){
            Intent intentReturn = new Intent(this, LoginActivity.class);
            intentReturn.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentReturn);
        }

        // get the date
        Date currentDate = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy", Locale.ENGLISH);
        date = sdf.format(currentDate);
        TextView signinDate = findViewById(R.id.signinDate);
        signinDate.setText(date);
    }

    private void validate(){
        // check if the user has selected a PP and a name
        String url = getPreferences(MODE_PRIVATE).getString(PREF_URL, null);
        if(url == null){
            Toast.makeText(this, R.string.selectProfilePicture, Toast.LENGTH_SHORT).show();
            return;
        }
        name = userName.getText().toString();
        if(name.isEmpty()){
            Toast.makeText(this, R.string.enterName, Toast.LENGTH_SHORT).show();
            return;
        }

        // save the account in the shared preferences
        JSONObject jsonObject = new JSONObject();
        try {
            //format the date
            jsonObject.put("name", name);
            jsonObject.put("url", url);
            jsonObject.put("id", id);
            jsonObject.put("date", date);
            String json = jsonObject.toString();

            SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_USER + id, json);
            editor.putString(PREF_SELECTED, PREF_USER + id);
            editor.putInt(PREF_LAST_ID, id);
            editor.apply();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        // go to the main activity
        Intent intent = new Intent(this, AccountSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void changePP(){
        // change the PP
        getImageUrl();

        userPPContent = findViewById(R.id.signinPPText);
        userPPContent.setText(R.string.signInImageLoading);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.loading);
        userPP.startAnimation(animation);
    }

    private void getImageUrl(){
        // get a random cat image
        String url = "https://api.thecatapi.com/v1/images/search";
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    String res = jsonObject.getString("url");
                    Picasso.get().load(res)
                            .resize(200, 200)
                            .centerCrop()
                            .into(new Target() {
                                @Override
                                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                    // make the image round
                                    RoundedBitmapDrawable drawable = RoundedBitmapDrawableFactory.create(getResources(), bitmap);
                                    drawable.setCornerRadius(100.0f);
                                    drawable.setAntiAlias(true);

                                    userPPContent.setText("");
                                    userPP.clearAnimation();
                                    userPP.setImageDrawable(drawable);

                                    SharedPreferences sharedUser = getPreferences(MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sharedUser.edit();
                                    editor.putString(PREF_URL, res);
                                    editor.apply();
                                }
                                @Override
                                public void onBitmapFailed(Exception e, Drawable errorDrawable) {}
                                @Override
                                public void onPrepareLoad(Drawable placeHolderDrawable) {}
                            });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error){}
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(request);

    }



}