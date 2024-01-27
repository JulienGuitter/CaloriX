package com.jgtech.calories;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.jgtech.calories.databinding.ActivityCalculateCaloriesBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;

public class CalculateCaloriesActivity extends DrawerBaseActivity {

    private final static String PREFS_SHARE = "com.jgtech.calories";
    private final static String PREF_SELECTED = "selected";

    ActivityCalculateCaloriesBinding activityCalculateCaloriesBinding;

    private EditText editBirthDate;
    private EditText editHeight;
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    private Switch switchHeightUnit;
    private EditText editWeight;
    private Spinner spinnerGender;
    private Spinner spinnerActivity;
    private CheckBox showCheckBox;
    private TextView textResult;
    private TextView unitHeight;

    private String resultStr;
    private int resultInt = 0;
    private String selected;

    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCalculateCaloriesBinding = ActivityCalculateCaloriesBinding.inflate(getLayoutInflater());
        setContentView(activityCalculateCaloriesBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.CalculateActivityName));

        // add custom button to toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.addView(getLayoutInflater().inflate(R.layout.menu_button_calculate, null), new Toolbar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.END));

        //get all the views and set the listeners
        Button emailMenuButton = findViewById(R.id.menuEmailButton);
        emailMenuButton.setOnClickListener(v -> sendMail());

        Button calendarMenuButton = findViewById(R.id.menuCalendarButton);
        calendarMenuButton.setOnClickListener(v -> openCalendar());

        Button calculateButton = findViewById(R.id.calculateButton);
        calculateButton.setOnClickListener(v -> calculate());

        Button resetButton = findViewById(R.id.resetButton);
        resetButton.setOnClickListener(v -> reset());

        editBirthDate = findViewById(R.id.editBirthDate);
        editBirthDate.addTextChangedListener(watchDate);

        unitHeight = findViewById(R.id.unitHeight);

        editHeight = findViewById(R.id.editHeight);
        editHeight.addTextChangedListener(watchHeight);

        switchHeightUnit = findViewById(R.id.switchHeightUnit);
        switchHeightUnit.setOnCheckedChangeListener((b, c) -> onChangeHeightUnit());

        editWeight = findViewById(R.id.editWeight);

        spinnerGender = findViewById(R.id.spinnerGender);

        spinnerActivity = findViewById(R.id.spinnerActivity);

        showCheckBox = findViewById(R.id.showCheckBox);
        showCheckBox.setOnCheckedChangeListener((b, c) -> onChangeShowCheckBox());

        textResult = findViewById(R.id.textResult);

        //get the selected user
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        selected = sharedPreferences.getString(PREF_SELECTED, null);

        //test if the user is already saved and if yes, load and show the data
        if(selected != null){
            //show the data
            String user = sharedPreferences.getString(selected, null);
            if(user != null){
                try {
                    //test if the user has already calculated his calories and if yes, set all views with the data
                    String gender = "", activity = "";
                    JSONObject jsonObject = new JSONObject(user);
                    if(jsonObject.has("dateNaissance")){
                        editBirthDate.setText(jsonObject.getString("dateNaissance"));
                    }
                    if(jsonObject.has("poids")){
                        editWeight.setText(jsonObject.getString("poids"));
                    }
                    if(jsonObject.has("taille")){
                        editHeight.setText(jsonObject.getString("taille"));
                    }
                    if(jsonObject.has("genre")){
                        spinnerGender.setSelection(jsonObject.getInt("genre"));
                        gender = spinnerGender.getSelectedItemPosition() == 0 ? getString(R.string.mr) : getString(R.string.mrs);
                    }
                    if(jsonObject.has("activite")){
                        spinnerActivity.setSelection(jsonObject.getInt("activite"));
                        activity = spinnerActivity.getSelectedItemPosition() == 0 ? getString(R.string.sendentary) : spinnerActivity.getSelectedItemPosition() == 1 ? getString(R.string.active) : getString(R.string.sporty);
                    }
                    if(jsonObject.has("resultat")){
                        resultInt = jsonObject.getInt("resultat");
                        textResult.setText(String.valueOf(resultInt));

                        resultStr = getString(R.string.resCalo, gender, activity, resultInt);
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private void calculate(){
        //check if all fields are filled
        if(editBirthDate.getText().toString().isEmpty()){
            editBirthDate.setError(getResources().getString(R.string.birthDateError));
            editBirthDate.requestFocus();
            return;
        }
        if(!isDateValid(editBirthDate.getText().toString())){
            editBirthDate.setError(getResources().getString(R.string.birthDateError));
            editBirthDate.requestFocus();
            return;
        }
        if(editWeight.getText().toString().isEmpty()){
            editWeight.setError(getResources().getString(R.string.weightError));
            editWeight.requestFocus();
            return;
        }
        if(editHeight.getText().toString().isEmpty()){
            editHeight.setError(getResources().getString(R.string.heightError));
            editHeight.requestFocus();
            return;
        }

        int weight = Integer.parseInt(editWeight.getText().toString());
        int height;
        if(switchHeightUnit.isChecked()){
            height = Integer.parseInt(editHeight.getText().toString());
        }else{
            height = Integer.parseInt(editHeight.getText().toString().replace(",", ""));
        }
        int age = calculateAge(editBirthDate.getText().toString());

        //calculate the calories
        double activity = spinnerActivity.getSelectedItemPosition() == 0 ? 1.37 : spinnerActivity.getSelectedItemPosition() == 1 ? 1.55 : 1.72;
        int sexe = spinnerGender.getSelectedItemPosition() == 0 ? 5 : -161;
        String sexeText = spinnerGender.getSelectedItemPosition() == 0 ? getString(R.string.mr) : getString(R.string.mrs);
        String activityText = spinnerActivity.getSelectedItemPosition() == 0 ? getString(R.string.sendentary) : spinnerActivity.getSelectedItemPosition() == 1 ? getString(R.string.active) : getString(R.string.sporty);

        int result = (int) (((10 * weight) + (6.25 * height) - (5 * age) + sexe) * activity);

        resultInt = result;

        resultStr = getString(R.string.resCalo, sexeText, activityText, result);

        if(showCheckBox.isChecked()){
            textResult.setText(resultStr);
        }

        // save the data and the result
        SharedPreferences sharedUser = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        String user = sharedUser.getString(selected, null);


        //convert string to json
        if(user != null){
            try {
                JSONObject jsonObject = new JSONObject(user);
                jsonObject.put("genre", spinnerGender.getSelectedItemPosition());
                jsonObject.put("dateNaissance", editBirthDate.getText().toString());
                jsonObject.put("poids", editWeight.getText().toString());
                jsonObject.put("taille", editHeight.getText().toString());
                jsonObject.put("activite", spinnerActivity.getSelectedItemPosition());
                jsonObject.put("resultat", resultInt);

                SharedPreferences.Editor editor = sharedUser.edit();
                editor.putString(selected, jsonObject.toString());
                editor.apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        // check if the user and the date already exist
        String[] projection = {DatabaseHelper.COLUMN_ID, DatabaseHelper.COLUMN_USER, DatabaseHelper.COLUMN_DATE};
        String selection = DatabaseHelper.COLUMN_USER + " = ? AND " + DatabaseHelper.COLUMN_DATE + " = ?";
        String[] selectionArgs = {selected, new SimpleDateFormat("dd/MM/yyyy").format(new Date())};

        Cursor cursor = db.query(DatabaseHelper.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if(cursor.getCount() > 0){
            // update the row
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_WEIGHT, weight);
            contentValues.put(DatabaseHelper.COLUMN_HEIGHT, height);
            contentValues.put(DatabaseHelper.COLUMN_CAL, result);

            db.update(DatabaseHelper.TABLE_NAME, contentValues, selection, selectionArgs);
        } else {
            // insert a new row
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.COLUMN_USER, selected);
            contentValues.put(DatabaseHelper.COLUMN_WEIGHT, weight);
            contentValues.put(DatabaseHelper.COLUMN_HEIGHT, height);
            contentValues.put(DatabaseHelper.COLUMN_DATE, new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
            contentValues.put(DatabaseHelper.COLUMN_CAL, result);
            db.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
        }

        cursor.close();
        db.close();
    }

    private void reset(){
        RazFragment razFragment = new RazFragment();
        razFragment.show(getSupportFragmentManager(), "raz");
    }

    public void raz(){
        //reset all the views
        editBirthDate.setText("");
        editWeight.setText("");
        editHeight.setText("");
        spinnerGender.setSelection(0);
        spinnerActivity.setSelection(0);
        textResult.setText(R.string.resDefault);
        resultInt = 0;

        // remove the last result from the shared preferences
        SharedPreferences sharedUser = getSharedPreferences(PREFS_SHARE, MODE_PRIVATE);
        String user = sharedUser.getString(selected, null);
        //convert string to json
        if(user != null){
            try {
                JSONObject jsonObject = new JSONObject(user);
                jsonObject.remove("genre");
                jsonObject.remove("dateNaissance");
                jsonObject.remove("poids");
                jsonObject.remove("taille");
                jsonObject.remove("activite");
                jsonObject.remove("resultat");

                SharedPreferences.Editor editor = sharedUser.edit();
                editor.putString(selected, jsonObject.toString());
                editor.apply();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void sendMail(){
        if(resultInt == 0){
            Toast.makeText(this, R.string.errorPartage, Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {""});
        intent.putExtra(Intent.EXTRA_SUBJECT, R.string.calorie);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.messagePartage, resultInt));
        startActivity(Intent.createChooser(intent, getString(R.string.titlePartage)));
    }

    private void openCalendar(){
        Intent intent = new Intent(this, CalendarActivity.class);
        startActivity(intent);
    }

    private void onChangeHeightUnit(){
        //change the unit of the height
        if(switchHeightUnit.isChecked()){
            // centimeters
            unitHeight.setText(R.string.centimeterUnit);
            String s = editHeight.getText().toString();
            if(s.length() > 1 && s.indexOf(',') != -1){
                //remove ,
                String newText = s.subSequence(0, 1) + "" + s.subSequence(2, s.length());
                editHeight.setText(newText);
                editHeight.setSelection(editHeight.getText().length());
            }
            editMaxLength(editHeight, 3);
        } else {
            // meters
            unitHeight.setText(R.string.meterUnit);
            editMaxLength(editHeight, 4);
            String s = editHeight.getText().toString();
            if(s.length() > 1 && s.indexOf(',') == -1){
                //add , to the 2nd position
                String newText = s.subSequence(0, 1) + "," + s.subSequence(1, s.length());
                editHeight.setText(newText);
                editHeight.setSelection(editHeight.getText().length());
            }
        }
    }

    private void onChangeShowCheckBox(){
        //show or hide the result
        if(resultInt <= 0){
            textResult.setText(R.string.resDefault);
            return;
        }
        if(showCheckBox.isChecked()){
            textResult.setText(resultStr);
        } else {
            textResult.setText(String.valueOf(resultInt));
        }
    }

    private TextWatcher watchHeight = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // TODO
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

            if(switchHeightUnit.isChecked()){
                // centimeters
            } else {
                // meters
                //add , after the first number
                if(s.length() >= 0 && before == 0){
                    if(s.length() == 1){
                        editHeight.setText(s + ",");
                        editHeight.setSelection(editHeight.getText().length());
                    } else if(s.length() > 1 && s.toString().indexOf(',') == -1){
                        //add , to the 2nd position
                        String newText = s.subSequence(0, 1) + "," + s.subSequence(1, s.length());
                        editHeight.setText(newText);
                        editHeight.setSelection(editHeight.getText().length());
                    }
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO
        }
    };

    private TextWatcher watchDate = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //add / after the 2nd and 5th number and check if the date is valid
            String date = s.toString();

            if(date.length() == 1 && Integer.parseInt(date) > 3){
                date = "0" + date;
                editBirthDate.setText(date);
                editBirthDate.setSelection(date.length());
            }

            if(date.length() == 4 && Integer.parseInt(date.substring(3)) > 1){
                date = date.substring(0, 3) + "0" + date.substring(3);
                editBirthDate.setText(date);
                editBirthDate.setSelection(date.length());
            }

            if (date.length() == 2 && before == 0) {
                date += "/";
                editBirthDate.setText(date);
                editBirthDate.setSelection(date.length());
            } else if (date.length() == 5 && before == 0) {
                date += "/";
                editBirthDate.setText(date);
                editBirthDate.setSelection(date.length());
            }

            if(date.length() == 10){
                //check if date is valid
                if(!isDateValid(date)){
                    editBirthDate.setError(getResources().getString(R.string.dateError));
                    editBirthDate.requestFocus();
                }
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private boolean isDateValid(String dateStr){
        //check if the date is valid
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateFormat.setLenient(false);

        try{
            Date date = dateFormat.parse(dateStr);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private void editMaxLength(EditText editText, int maxLength){
        //set the max length of the edit text
        editText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    private static int calculateAge(String dateOfBirth){
        //calculate the age
        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date date = dateFormat.parse(dateOfBirth);

            assert date != null;
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            LocalDate currentDate = LocalDate.now();

            Period period = Period.between(localDate, currentDate);
            return period.getYears();
        }catch (ParseException e){
            e.printStackTrace();
            return -1;
        }
    }
}