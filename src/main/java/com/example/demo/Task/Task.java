package com.example.demo.Task;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

@Entity
@Table(name = "Task")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "task_id")
    private Integer id;

    @Column(name = "query")
    private String query;

    @JsonFormat(pattern = "EEE MMM dd HH:mm:ss z yyyy")
    @Column(name = "date", nullable = false)
    private String date;

    @Column(name = "task_interval")
    private int interval;  // Changed from 'boolean' to 'Boolean' to allow null values



    public Task(String query, String date, int interval) {
        this.query = query;
        this.date = date;
        this.interval = interval;
    }

    public Task() {}

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getinterval() {
        return interval;
    }

    public void setinterval(int interval) {
        this.interval = interval;
    }
}
