package com.example.shareplan;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.datepicker.MaterialCalendar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflater.inflate(R.layout.ab_schedule, null);
        getSupportActionBar().setCustomView(v);

        CalendarView calendarView = (CalendarView) findViewById(R.id.calendarView);
        Calendar min = Calendar.getInstance();
        min.set(2021, 0, 0);
        calendarView.setMinimumDate(min);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clickedDayCalendar = eventDay.getCalendar();
                Toast.makeText(ScheduleActivity.this, clickedDayCalendar.getTime().getYear()+1900+", "+(clickedDayCalendar.getTime().getMonth()+1)+", "+clickedDayCalendar.getTime().getDate(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
