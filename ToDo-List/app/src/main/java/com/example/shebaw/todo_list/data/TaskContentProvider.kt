package com.example.shebaw.todo_list.data

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.shebaw.todo_list.data.TaskContract.TaskEntry.Companion.TABLE_NAME

class TaskContentProvider : ContentProvider() {
    private lateinit var mTaskDbHelper: TaskDbHelper

    override fun onCreate(): Boolean {
        mTaskDbHelper = TaskDbHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        // Get access to the task database (to write new data to)
        val db = mTaskDbHelper.writableDatabase
        lateinit var returnUri: Uri
        // Write URI matching code to identify the match for the tasks directory
        when (sUriMatcher.match(uri)) {
            TASKS -> {
                // Insert new values into the database
                // Inserting values into tasks table
                val id = db.insert(TABLE_NAME, null, values)
                if (id > 0) {
                    returnUri = ContentUris.withAppendedId(TaskContract.TaskEntry.CONTENT_URI, id)
                } else {
                    throw android.database.SQLException("Failed to insert row into $uri")
                }
            }
            else -> UnsupportedOperationException("Unknown URI: $uri")
        }
        // Notify the resolver if the uri has changed, and return the newly inserted URI
        context.contentResolver.notifyChange(uri, null)
        return returnUri
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        val db = mTaskDbHelper.writableDatabase
        lateinit var retCursor: Cursor
        when (sUriMatcher.match(uri)) {
            TASKS -> {
                retCursor = db.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder)
            }
            // Add a case to query for a single row of data by ID
            TASK_WITH_ID -> {
                // using selection and selectionArgs
                // URI: content://<authority>/tasks/#
                val id = uri?.pathSegments?.get(1)

                // Selection is the _ID column = ?, and the Selection args = the row ID from the URI
                val mSelection = "_id=?"
                val mSelectionArgs = arrayOf(id)
                retCursor = db.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        sortOrder)
            }
            else -> UnsupportedOperationException("Unknown URI: $uri")
        }
        // Set a notification URI for the cursor
        retCursor.setNotificationUri(context.contentResolver, uri)
        return retCursor
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        val db = mTaskDbHelper.writableDatabase
        var tasksDeleted = 0
        when (sUriMatcher.match(uri)) {
            TASK_WITH_ID -> {
                // Get the task ID from the URI path
                val id = uri?.pathSegments?.get(1)
                // Use slections/selectionArgs to filter for this id
                tasksDeleted = db.delete(TABLE_NAME, "_id=?", arrayOf(id))
            }
            else -> throw UnsupportedOperationException("Unknown URI: $uri")
        }
        // Notify the resolver of a change and return the number of items deleted
        if (tasksDeleted != 0) {
            // A task was deleted, set notification
            context.contentResolver.notifyChange(uri, null)
        }
        // Return the number of tasks deleted
        return tasksDeleted
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

// Define final integer constants for the directory of tasks and a single item.
// It's convention to use 100, 200, 300, etc for directories
const val TASKS = 100
// and related ints (101, 102, 103) for items in the directory.
const val TASK_WITH_ID = 101

fun buildUriMatcher() =
        UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(TaskContract.AUTHORITY, TaskContract.PATH_TASKS, TASKS)
            addURI(TaskContract.AUTHORITY, "${TaskContract.PATH_TASKS}/#", TASK_WITH_ID)
        }

val sUriMatcher = buildUriMatcher()
