package com.example.shebaw.todo_list

import android.content.Intent
import android.database.Cursor
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.LoaderManager
import android.support.v4.content.AsyncTaskLoader
import android.support.v4.content.Loader
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import com.example.shebaw.todo_list.data.TaskContract

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<Cursor> {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: CustomCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the RecyclerView to it's corresponding view
        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerViewTasks)

        // Set the layout for the RecyclerView to be a linear layout, which measures and
        // positions items within a RecyclerView into a linear list
        mRecyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the adapter and attach it to the RecyclerView
        mAdapter = CustomCursorAdapter(this)
        mRecyclerView.adapter = mAdapter

        /*
         Add a touch helper to the RecyclerView to recognize when a user swipes to delete an item.
         An ItemTouchHelper enables touch behavior (like swipe and move) on each ViewHolder,
         and uses callbacks to signal when a user is performing these actions.
         */
        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?,
                                viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                // Works because the id was set as a tag in the RecyclerView code
                val id = viewHolder.itemView.tag
                var uri = TaskContract.TaskEntry.CONTENT_URI
                uri = uri.buildUpon().appendPath(id.toString()).build()

                contentResolver.delete(uri, null, null)

                // Restart the loader since we have deleted an item and the recycler view needs to
                // be repopulated
                supportLoaderManager.restartLoader(TASK_LOADER_ID, null, this@MainActivity)
            }
        }).attachToRecyclerView(mRecyclerView)

        /*
         Set the Floating Action Button (FAB) to its corresponding View.
         Attach an OnClickListener to it, so that when it's clicked, a new intent will be created
         to launch the AddTaskActivity.
         */
        with (findViewById<FloatingActionButton>(R.id.fab)) {
            setOnClickListener(object : View.OnClickListener {
                override fun onClick(v: View?) {
                    // Create a new intent to start an AddTaskActivity
                    val addTaskIntent = Intent(this@MainActivity,
                            AddTaskActivity::class.java)
                    startActivity(addTaskIntent)
                }
            })
        }

        /*
         Ensure a loader is initialized and active. If the loader doesn't already exist, one is
         created, otherwise the last created loader is re-used.
         */
        supportLoaderManager.initLoader(TASK_LOADER_ID, null, this);
    }

    /**
     * This method is called after this activity has been paused or restarted.
     * Often, this is after new data has been inserted through an AddTaskActivity,
     * so this restarts the loader to re-query the underlying data for any changes.
     */
    override fun onResume() {
        super.onResume()

        // re-queries for all tasks
        supportLoaderManager.restartLoader(TASK_LOADER_ID, null, this)
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> {
        return object : AsyncTaskLoader<Cursor>(this) {
            // Initialize a Cursor, that will hold all the task data
            var mTaskData: Cursor? = null

            override fun onStartLoading() {
                if (mTaskData != null) deliverResult(mTaskData) else forceLoad()
            }

            override fun loadInBackground(): Cursor? {
                // Query and load all task data in the background; sort by priority
                // Use a try/catch block to catch any errors in loading data
                try {
                    return contentResolver.query(TaskContract.TaskEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            TaskContract.TaskEntry.COLUMN_PRIORITY)
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to asynchronously load data.")
                    e.printStackTrace()
                    return null
                }
            }

            // deliverResult sends the result of the load, a Cursor, to the registered listener
            override fun deliverResult(data: Cursor?) {
                mTaskData = data
                super.deliverResult(data)
            }
        }
    }

    // Called when a previously created loader has finished its load
    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        // Update the data that the adapter uses to create ViewHolders
        mAdapter.swapCursor(data)
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.
     * onLoaderReset removes any references this activity had to the loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>?) {
        mAdapter.swapCursor(null)
    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        val TASK_LOADER_ID = 0
    }

}
