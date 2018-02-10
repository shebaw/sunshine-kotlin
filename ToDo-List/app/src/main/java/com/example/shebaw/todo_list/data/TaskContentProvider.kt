package com.example.shebaw.todo_list.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

class TaskContentProvider : ContentProvider() {
    private lateinit var mTaskDbHelper: TaskDbHelper

    override fun onCreate(): Boolean {
        val context = getContext()
        mTaskDbHelper = TaskDbHelper(context)
        return true
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
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
            addURI(TaskContract.AUTHORITY, "${TaskContract.PATH_TASKS}#", TASK_WITH_ID)
        }

val sUriMatcher = buildUriMatcher()
