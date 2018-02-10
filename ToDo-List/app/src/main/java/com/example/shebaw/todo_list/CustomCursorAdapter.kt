package com.example.shebaw.todo_list

import android.content.Context
import android.database.Cursor
import android.graphics.drawable.GradientDrawable
import android.provider.BaseColumns
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shebaw.todo_list.data.TaskContract

class CustomCursorAdapter(val mContext: Context) :
        RecyclerView.Adapter<CustomCursorAdapter.TaskViewHolder>() {
    private var mCursor: Cursor? = null

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(mContext)
                .inflate(R.layout.task_layout, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val idIndex = mCursor?.getColumnIndex(BaseColumns._ID)
        val descriptionIndex = mCursor?.getColumnIndex(TaskContract.TaskEntry.COLUMN_DESCRIPTION)
        val priorityIndex = mCursor?.getColumnIndex(TaskContract.TaskEntry.COLUMN_PRIORITY)

        // get to the right location in the cursor
        mCursor?.moveToPosition(position)

        // Determine the values of the wanted data
        val id = mCursor?.getInt(idIndex!!)
        val description = mCursor?.getString(descriptionIndex!!)
        val priority = mCursor?.getInt(priorityIndex!!)

        // Set values
        holder.itemView.tag = id
        holder.taskDescriptionView.text = description

        // Programatically set the text and color of the priority TextView
        val priorityString = "${priority}"
        holder.priorityView.text = priorityString

        val priorityCircle = holder.priorityView.background as GradientDrawable
        // Get the appropriate background color based on the priority
        val priorityColor = getPriorityColor(priority!!)
        priorityCircle.setColor(priorityColor)
    }

    private fun getPriorityColor(priority: Int): Int =
        when (priority) {
            1 -> ContextCompat.getColor(mContext, R.color.materialRed)
            2 -> ContextCompat.getColor(mContext, R.color.materialOrange)
            3 -> ContextCompat.getColor(mContext, R.color.materialYellow)
            else -> 0
        }

    override fun getItemCount(): Int = if (mCursor == null) 0 else mCursor?.count!!

    /**
     * When data changes and a re-query occurs, this function swaps the old Cursor
     * with a newly updated Cursor (Cursor c) that is passed in.
     */
    fun swapCursor(c: Cursor?): Cursor? {
        // check if this cursor is the same as the previous cursor
        if (mCursor == c) {
            return null // bc nothing has changed
        }
        val temp = mCursor
        mCursor = c // new cursor value assigned
        // check if this is a valid cursor, then update the cursor
        c?.let { notifyDataSetChanged() }
        return temp
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val taskDescriptionView = itemView.findViewById<TextView>(R.id.taskDescription)
        val priorityView = itemView.findViewById<TextView>(R.id.priorityTextView)
    }
}
