package com.example.tasklista;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.text.format.Time;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;

public class settingtask extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    EditText nametask_text, detail_text;
    TextView dateTime_text;
    Button addButton;

    //----------------------------------------------------------date Time
    public static String alarm_title;

    AlarmManager alarmManager;
    Intent alarm_intent;
    PendingIntent pendingIntent;

    //set time to this Calender
    Calendar c;

    //get current time from this Calender
    Calendar calendar;

    int day, month, year, hour, minute;
    long set_Time;

    //----------------------------------------------------------date Time

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingtask);

        dateTime_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_DateTime();
            }
        });

    }

    public void set_DateTime(){
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(settingtask.this,settingtask.this,year, month, day);
        datePickerDialog.show();

    }
    public void clear(View v)
    {
        nametask_text.setText("");
        detail_text.setText("");
        dateTime_text.setText("");
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH,month);
        c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

        hour = calendar.get(Calendar.HOUR);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(settingtask.this, settingtask.this, hour, minute, DateFormat.is24HourFormat(this));
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND,0);

        set_Time = c.getTimeInMillis();

        Date date =c.getTime();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE, d MMM yyyy HH:MM");
        dateTime_text.setText(simpleDateFormat.format(date).toString());
    }
}