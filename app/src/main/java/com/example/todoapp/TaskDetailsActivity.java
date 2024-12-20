package com.example.todoapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TaskDetailsActivity extends AppCompatActivity {

    private static final String TASK_PREF = "TaskPref";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        String username = getIntent().getStringExtra("USERNAME");

        EditText titleField = findViewById(R.id.task_title);
        EditText descriptionField = findViewById(R.id.task_description);
        EditText deadlineField = findViewById(R.id.task_deadline);
        Spinner statusSpinner = findViewById(R.id.task_status_spinner);
        Button saveButton = findViewById(R.id.save_task);

        boolean isEdit = getIntent().getBooleanExtra("IS_EDIT", false);
        Log.d("TaskDetailsActivity", "isEdit: " + isEdit);
        final Task[] task = new Task[1];  // Use an array to make task reference effectively final

        if (isEdit) {
            String taskJson = getIntent().getStringExtra("TASK");
            if (taskJson != null) {
                Gson gson = new Gson();
                task[0] = gson.fromJson(taskJson, Task.class);

                titleField.setText(task[0].getTitle());
                descriptionField.setText(task[0].getDescription());
                deadlineField.setText(task[0].getDeadline());
            }
        }

        // Inside TaskDetailsActivity.java
        saveButton.setOnClickListener(view -> {
            String title = titleField.getText().toString();
            String description = descriptionField.getText().toString();
            String deadline = deadlineField.getText().toString();
            String status = statusSpinner.getSelectedItem().toString();

            if (!title.isEmpty() && !deadline.isEmpty()) {
                Task taskToSave;

                // If editing an existing task, use the existing task or create a new one if null
                if (task[0] != null && task[0].checkId()) {
                    taskToSave = task[0];  // Update existing task
                    taskToSave.setTitle(title);
                    taskToSave.setDescription(description);
                    taskToSave.setDeadline(deadline);
                    taskToSave.setStatus(status);
                } else {
                    // Create a new task
                    taskToSave = new Task(UUID.randomUUID().toString(), title, description, deadline, status);
                }

                // Save the task
                saveTask(taskToSave, username, isEdit);
                Toast.makeText(TaskDetailsActivity.this, "Task saved", Toast.LENGTH_SHORT).show();

                // Send a result back to the DashboardActivity to refresh data
                Intent resultIntent = new Intent();
                resultIntent.putExtra("USERNAME", username);  // Pass username back
                setResult(RESULT_OK, resultIntent);

                finish(); // Close the current activity and return to DashboardActivity
            } else {
                Toast.makeText(TaskDetailsActivity.this, "Fill all mandatory fields", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTask(Task task, String username, boolean isEdit) {
        SharedPreferences prefs = getSharedPreferences(TASK_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        String userKey = username + "_tasks";
        List<Task> tasks = loadTasks(username);

        // Log the task list before modification
        Log.d("TaskDetailsActivity", "Loaded tasks (before modification): " + tasks.size());

        if (isEdit) {
            boolean taskUpdated = false;
            for (int i = 0; i < tasks.size(); i++) {
                if (tasks.get(i).getId().equals(task.getId())) {
                    tasks.set(i, task);  // Update task
                    taskUpdated = true;
                    Log.d("TaskDetailsActivity", "Task updated at index " + i + ": " + task.getTitle());
                    break;
                }
            }
            if (!taskUpdated) {
                Log.d("TaskDetailsActivity", "No task found with ID: " + task.getId());
            }
        } else {
            tasks.add(task);  // Add new task
            Log.d("TaskDetailsActivity", "New task added: " + task.getTitle());
        }

        // Log tasks after modification
        Log.d("TaskDetailsActivity", "Tasks after modification: " + tasks.size());

        // Save the updated task list back to SharedPreferences
        Gson gson = new Gson();
        String json = gson.toJson(tasks);
        editor.putString(userKey, json);
        editor.apply();  // Apply changes asynchronously

        // Log the final JSON being saved to SharedPreferences
        Log.d("TaskDetailsActivity", "Tasks saved to SharedPreferences: " + json);
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
