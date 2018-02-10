package com.example.shebaw.todo_list.data

import android.net.Uri
import android.provider.BaseColumns

class TaskContract {
    companion object {
        // The Authority, which is how your code knows which Content provider to access
        val AUTHORITY = "com.example.shebaw.todo_list"
        // The base content URI
        val BASE_CONTENT_URI = Uri.parse("content://${AUTHORITY}")
        // Define the possible paths for accessing data in this contract
        // This is the path for the "tasks" directory
        val PATH_TASKS = "tasks"
    }
    class TaskEntry : BaseColumns {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build()
            val TABLE_NAME = "tasks"
            val COLUMN_DESCRIPTION = "description"
            val COLUMN_PRIORITY = "priority"
        }
    }
}