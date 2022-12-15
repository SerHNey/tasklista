package com.example.todo;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<Task> arrayList;
    static ItemAdapter adapter;
    ListView listView;

    //----------------------------------------------Cancel Alarm

    AlarmManager alarmManager;
    Intent alarm_intent;
    Intent api;

    //----------------------------------------------Cancel Alarm

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Loading the saved data in SharedPreferences
        loadData();

        //Initialising adapter for listView
        adapter = new ItemAdapter(this, arrayList);
        listView = findViewById(R.id.listView);

        listView.setAdapter(adapter);

        //initialising alarmManager and alarm_intent, which we use it later
        alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm_intent = new Intent(getApplicationContext(), MyAlarm.class);

    }
    //method for loading data from sharedPreferences
    private void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        Gson gson = new Gson();
        //json contains the stored data if any, else null
        String json = sharedPreferences.getString("task_list", null);
        Type type = new TypeToken<ArrayList<Task>>(){}.getType();
        arrayList = gson.fromJson(json, type);

        if(arrayList == null){
            arrayList = new ArrayList<>();
        }
    }

    //method to save data in sharedPrefs
    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();

        String json = gson.toJson(MainActivity.arrayList);

        editor.putString("task_list", json);
        editor.apply();
    }


    //ItemAdapter as ArrayAdapter of type item which we have created as a separate class
    public class ItemAdapter extends ArrayAdapter<Task> {
        public ItemAdapter(Context context, ArrayList<Task> users) {
            super(context, 0, users);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            Task user = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_item, parent, false);
            }
            // Lookup view for data population
            //holds the TITLE of the item
            final TextView tvtitle = (TextView) convertView.findViewById(R.id.item_title);

            //Setting up the feature to edit any item by clicking on that item
            tvtitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //onClick Editor opens where the user can edit the item
                    Intent in = new Intent(getApplicationContext(), EditTask.class);

                    //we pass this itemId so the editor knows if it
                    //it should create another item or edit the item's details
                    in.putExtra("itemId", position);
                    startActivity(in);
                }
            });
            //feature to delete the item by click and hold
            tvtitle.setOnLongClickListener(new View.OnLongClickListener() {
                Task i = getItem(position);
                @Override
                public boolean onLongClick(View v) {
                    //alert dialog to confirm with the user before deleting the selected item
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("Вы точно хотите удалить эту задачу?")
                            .setMessage("Эта задачу удалена")
                            .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //check if any dateTime(alarm) is associated with this item
                                    //delete if any
                                    if(i.dateTime != null)
                                    {
                                        //alarm exists, so cancel
                                        cancelAlarm(i.id);
                                    }

                                    //remove the item from the adapter
                                    adapter.remove(i);

                                    saveData();
                                }
                            })
                            .setNegativeButton("Нет", null)
                            .show();

                    return true;
                }
            });
            //holds the details of the item
            final TextView tvdetails = (TextView) convertView.findViewById(R.id.item_details);

            //holds the state of the item, if checked(completed) or not
            CheckBox tvdone = (CheckBox) convertView.findViewById(R.id.item_done);

            //on changing the state of the item
            tvdone.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (buttonView.isChecked()) {
                        Task currentItem = getItem(position);
                        currentItem.is_done = true;

                        //feature to strike through the text if the item is checked(completed)
                        tvtitle.setPaintFlags(tvtitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                        tvdetails.setPaintFlags(tvtitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                        saveData();

                    }
                    else {

                        Task currentItem = getItem(position);
                        currentItem.is_done = false;
                        tvtitle.setPaintFlags(tvtitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        tvdetails.setPaintFlags(tvtitle.getPaintFlags() & (~ Paint.STRIKE_THRU_TEXT_FLAG));
                        saveData();
                    }
                }
            });
            // Populate the data into the template view using the data object
            tvtitle.setText(user.title);
            tvdetails.setText(user.detail);
            //item_details will be formatted as bullet list
            String[] lines = user.detail.split("\n");
            tvdetails.setText("");
            //only if item_details exist
            if(!user.detail.isEmpty()){
                for (int i = 0; i < lines.length; i++) {
                    //"\u2022" -> bullet point
                    tvdetails.append("\u2022  " + lines[i]+"\n");
                }
            }
            tvdone.setChecked(user.is_done);
            // Return the completed view to render on screen
            return convertView;
        }
    }

    //Menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId())
        {
            //menu to add item to the list
            case R.id.add_item:
                //startActivity(new Intent(MainActivity.this, EditTask.class));
                startActivity(new Intent(MainActivity.this, PhotoListActivity.class));
                return true;

            case R.id.clear_all:
                clearall();
                return true;

            //clear  only those items which are being checked
            case R.id.clear_completed:
                clear_completed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void clearall(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Вы уверены что хотите удалить все записи?")
                .setMessage("Записи будут удалены безвозвратно")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //adapter.clear() also could have been used to remove the items
                        //but I also need to cancel the alarms associated with it
                        while(adapter.getCount() > 0)
                        {
                            Task it = adapter.getItem(adapter.getCount() - 1);
                            //clear the alarms associated with this item
                            if(it.dateTime != null)
                            {
                                //alarm must be set
                                cancelAlarm(it.id);
                            }
                            adapter.remove(it);
                        }
                        saveData();
                        Toast.makeText(getApplicationContext(), "Записи удалены", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();
    }
    //similarly for removing checked items
    public void clear_completed(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Вы точно хотите удалить запись?")
                .setMessage("Запись удалена")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int ind = adapter.getCount();
                        while(ind > 0)
                        {
                            Task it = adapter.getItem(ind - 1);
                            if(it.is_done)
                            {
                                //clear the alarms associated with this item
                                if(it.dateTime != null)
                                {
                                    //alarm must be set
                                    cancelAlarm(it.id);
                                }
                                adapter.remove(it);
                            }
                            ind--;

                        }

                        saveData();
                        Toast.makeText(getApplicationContext(), "Запись удалена", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Нет", null)
                .show();

    }
    //method to cancel alarm of particular item_id (reqCode)
    public void cancelAlarm(int req_code){
        PendingIntent pen = PendingIntent.getBroadcast(MainActivity.this,
                req_code, alarm_intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pen);
    }

}
