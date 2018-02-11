package com.example.shebaw.todo_list

import android.content.ContentValues
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Toast
import com.example.shebaw.todo_list.data.TaskContract

class AddTaskActivity : AppCompatActivity() {
    // Declare a member variable to keep track of a task's selected priority.
    private var mPriority = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_task)
        findViewById<RadioButton>(R.id.radButton1).isChecked = true
        mPriority = 1
    }

    // onClickAddTask is called when "ADD" button is clicked.
    // It retrieves user input and inserts that new task data into the underlying database.
    fun onClickAddTask(view: View) {
        // Check if EditText is empty, if not retrieve input and store it in a ContentValues object
        // If the EditText input is empty -> don't create an entry
        val input = findViewById<EditText>(R.id.editTextTaskDescription).text.toString()
        if (input.isBlank()) {
            return
        }
        // new content values object
        val contentValues = ContentValues()
        contentValues.put(TaskContract.TaskEntry.COLUMN_DESCRIPTION, input)
        contentValues.put(TaskContract.TaskEntry.COLUMN_PRIORITY, mPriority)
        // Insert new task data via a ContentResolver
        val uri = contentResolver.insert(TaskContract.TaskEntry.CONTENT_URI, contentValues)
        // Display the URI that is inserted with a Toast
        uri?.let {Toast.makeText(baseContext, it.toString(), Toast.LENGTH_LONG).show()}
        // Call finish to return to MainActivity after this insert is complete
        finish()
    }

    // onPrioritySelected is called whenever a priority button is clicked.
    // It changes the value of mPriority based on the selection button.
    fun onPrioritySelected(view: View) {
        val ids = listOf(R.id.radButton1, R.id.radButton2, R.id.radButton3)
        val priorities = listOf(1, 2, 3)
        mPriority = priorities[ids.indexOf(ids.find { findViewById<RadioButton>(it).isChecked })]
    }
}
