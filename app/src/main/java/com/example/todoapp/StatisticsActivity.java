package com.example.todoapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class StatisticsActivity extends AppCompatActivity {

    private static final String TASK_PREF = "TaskPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // Get the username from the intent
        String username = getIntent().getStringExtra("USERNAME");

        // TextView to display statistics
        TextView statsView = findViewById(R.id.stats_view);

        // Load the tasks for the given user
        List<Task> tasks = loadTasks(username);
        int overdueCount = 0;

        // Get the current date in the format yyyy-MM-dd
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDateStr = sdf.format(new Date());

        try {
            Date currentDate = sdf.parse(currentDateStr);
            for (Task task : tasks) {
                if (task.getDeadline() != null) {
                    Date taskDeadline = sdf.parse(task.getDeadline());
                    if (taskDeadline != null && taskDeadline.before(currentDate) && !task.getStatus().equals("Finished")) {
                        overdueCount++;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Display the count of overdue tasks
        statsView.setText("Overdue tasks: " + overdueCount);
    }

    // Load tasks for the specified username
    private List<Task> loadTasks(String username) {
        SharedPreferences prefs = getSharedPreferences(TASK_PREF, MODE_PRIVATE);
        String userKey = username + "_tasks";  // Use username to get the correct task list
        String json = prefs.getString(userKey, null);

        List<Task> tasks = new ArrayList<>();
        if (json != null) {
            tasks = new Gson().fromJson(json, new TypeToken<List<Task>>() {}.getType());
        }

        return tasks;
    }
}
