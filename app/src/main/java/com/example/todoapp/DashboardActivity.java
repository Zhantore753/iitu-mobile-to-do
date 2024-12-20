package com.example.todoapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity {

    private static final String TASK_PREF = "TaskPref";
// Inside DashboardActivity.java

    private static final int TASK_DETAIL_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        String username = getIntent().getStringExtra("USERNAME");

        Button btnCreateTask = findViewById(R.id.btn_create_task);
        Button btnViewStats = findViewById(R.id.btn_view_stats);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initially load tasks
        List<Task> taskItems = loadTasks(username);
        TaskAdapter adapter = new TaskAdapter(this, taskItems, username);
        recyclerView.setAdapter(adapter);

        btnCreateTask.setOnClickListener(v -> {
            Intent createTaskIntent = new Intent(DashboardActivity.this, TaskDetailsActivity.class);
            createTaskIntent.putExtra("USERNAME", username);
            startActivityForResult(createTaskIntent, TASK_DETAIL_REQUEST_CODE);
        });

        btnViewStats.setOnClickListener(v -> {
            Intent statsIntent = new Intent(DashboardActivity.this, StatisticsActivity.class);
            statsIntent.putExtra("USERNAME", username);
            startActivity(statsIntent);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == TASK_DETAIL_REQUEST_CODE && resultCode == RESULT_OK) {
            // Ensure that the username is correctly passed back
            String username = data.getStringExtra("USERNAME");

            // Load the updated task list from SharedPreferences
            List<Task> updatedTaskList = loadTasks(username);

            // Set the updated task list to the adapter
            TaskAdapter adapter = new TaskAdapter(this, updatedTaskList, username);
            RecyclerView recyclerView = findViewById(R.id.recycler_view);
            recyclerView.setAdapter(adapter);
        }
    }


    private List<Task> loadTasks(String username) {
        SharedPreferences prefs = getSharedPreferences(TASK_PREF, MODE_PRIVATE);
        String userKey = username + "_tasks";
        String json = prefs.getString(userKey, null);

        List<Task> tasks = new ArrayList<>();
        if (json != null) {
            tasks = new Gson().fromJson(json, new TypeToken<List<Task>>() {}.getType());
        }

        return tasks;
    }

}
