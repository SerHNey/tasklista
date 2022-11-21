package com.example.tasklista;

public class Task {
    int id;
    String nametask;
    String detail;
    Boolean is_ready;
    String datetime;
    public Task(int id,String nametask, String detail, boolean is_ready, String datetime){
        this.id = id;
        this.nametask = nametask;
        this.detail = detail;
        this.is_ready = is_ready;
        this.datetime = datetime;
    }
}
