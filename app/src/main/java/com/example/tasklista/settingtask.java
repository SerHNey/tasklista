package com.example.tasklista;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

public class settingtask extends AppCompatActivity {
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
}