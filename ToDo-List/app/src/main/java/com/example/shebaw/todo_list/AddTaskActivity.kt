package com.example.shebaw.todo_list

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton

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
        // not yet implemented
    }

    // onPrioritySelected is called whenever a priority button is clicked.
    // It changes the value of mPriority based on the selection button.
    fun onPrioritySelected(view: View) {
        when {
            findViewById<RadioButton>(R.id.radButton1).isChecked -> mPriority = 1
            findViewById<RadioButton>(R.id.radButton2).isChecked -> mPriority = 2
            findViewById<RadioButton>(R.id.radButton3).isChecked -> mPriority = 3
        }
    }
}
