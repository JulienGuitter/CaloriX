package com.jgtech.calories;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.CalendarView;

import com.jgtech.calories.databinding.ActivityCalculateCaloriesBinding;
import com.jgtech.calories.databinding.ActivityCalendarBinding;

import java.util.Date;

public class CalendarActivity extends DrawerBaseActivity {

    ActivityCalendarBinding activityCalendarBinding;

    CalendarView calendarView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityCalendarBinding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(activityCalendarBinding.getRoot());
        allocateActivityTitle(getResources().getString(R.string.CalendarActivityName));

        // Set calendar to current date
        calendarView = findViewById(R.id.calendarView);
        calendarView.setDate(System.currentTimeMillis(), false, true);
    }
}