package com.example.todo;

public class Task {
    String title;
    String detail;
    Boolean is_done;//check box
    String dateTime;
    int id;//unique id which we use as requestCode for alarm

    public Task(String title, String detail, Boolean is_done, String dateTime, int id){
        this.title = title;
        this.detail = detail;
        this.is_done = is_done;
        this.dateTime = dateTime;
        this.id = id;
    }
}
