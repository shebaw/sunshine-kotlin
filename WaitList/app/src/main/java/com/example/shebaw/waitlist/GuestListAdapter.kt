package com.example.shebaw.waitlist

import android.database.Cursor
import android.content.Context
import android.provider.BaseColumns
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shebaw.waitlist.data.WaitlistContract

class GuestListAdapter(val mContext: Context, var mCursor: Cursor) :
        RecyclerView.Adapter<GuestListAdapter.GuestViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): GuestViewHolder {
        val inflater = LayoutInflater.from(mContext)
        val view = inflater.inflate(R.layout.guest_list_item, parent, false)
        return GuestViewHolder(view)
    }

    override fun onBindViewHolder(holder: GuestViewHolder?, position: Int) {
        // Move the cursor to the passed in position
        if (!mCursor.moveToPosition(position)) {
            return
        }
        val name = mCursor.getString(mCursor.getColumnIndex(WaitlistContract.Contacts.COLUMN_GUEST_NAME))
        val partySize = mCursor.getInt(mCursor.getColumnIndex(WaitlistContract.Contacts.COLUMN_PARTY_SIZE))
        // Set the holder's nameTextView to the guest's name
        holder?.nameTextView?.text = name
        holder?.partySizeTextView?.text = partySize.toString()
        holder?.itemView?.tag = mCursor.getLong(mCursor.getColumnIndex(BaseColumns._ID))
    }

    override fun getItemCount() = mCursor.count

    fun swapCursor(newCursor: Cursor) {
        // check if teh current cursor is not null, and close it if so
        // Always close the previous mCursor first
        mCursor.let { it.close() }
        mCursor = newCursor
        // Force the recycler view to refresh
        newCursor.let { notifyDataSetChanged() }

    }

    class GuestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // Will display the guest name
        val nameTextView = itemView.findViewById<TextView>(R.id.name_text_view)
        // Will display the party size number
        val partySizeTextView = itemView.findViewById<TextView>(R.id.party_size_text_view)
    }
}
