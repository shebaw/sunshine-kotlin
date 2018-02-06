package com.example.shebaw.waitlist

import android.content.ContentValues
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.provider.BaseColumns
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.View
import android.widget.EditText
import com.example.shebaw.waitlist.data.WaitlistContract
import com.example.shebaw.waitlist.data.WaitlistDbHelper

class MainActivity : AppCompatActivity() {
    private lateinit var mAdapter: GuestListAdapter
    private lateinit var mDb: SQLiteDatabase
    private lateinit var mNewGuestNameEditText: EditText
    private lateinit var mNewPartySizeEditExt: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set local attributes to coressponding views
        val waitlistRecyclerView = findViewById<RecyclerView>(R.id.all_guests_list_view)
        mNewGuestNameEditText = findViewById<EditText>(R.id.person_name_edit_text)
        mNewPartySizeEditExt = findViewById<EditText>(R.id.party_count_edit_text)

        // Set layout for the RecyclerView, because it's a list we are using the linear layout
        waitlistRecyclerView.layoutManager = LinearLayoutManager(this)


        // Get a writable database reference
        mDb = WaitlistDbHelper(this).writableDatabase
        val cursor = getAllGuests()

        // Create an adapter for that cursor to display the data
        mAdapter = GuestListAdapter(this, cursor)
        // Link the adapter to the RecyclerView
        waitlistRecyclerView.adapter = mAdapter

        // We handle deleting by swiping here
        // Create a new ItemTouchHelper with a SimpleCallback that handles both LEFT and RIGHT swipes

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?,
                                target: RecyclerView.ViewHolder?): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                val id: Long = viewHolder?.itemView?.tag as Long
                removeGuest(id)
                mAdapter.swapCursor(getAllGuests())
            }
        }).attachToRecyclerView(waitlistRecyclerView)
    }

    // This method is called when a user clicks on Add to waitlist button
    fun addToWaitlist(view: View) {
        if (mNewGuestNameEditText.text.isNullOrBlank() || mNewPartySizeEditExt.text.isNullOrBlank()) {
            return
        }
        var partySize = 1
        try {
            mNewPartySizeEditExt.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Log.i("addToWaitList", e.message)
        }
        addGuest(mNewGuestNameEditText.text.toString(), partySize)

        // call swapCursor since our adapter is using the old cursor with the old query result
        // that doesn't include the new guest
        mAdapter.swapCursor(getAllGuests())

        // to make the UI look nice, call getText().clear() on both EditTexts
        // alsocall clearFocus() on mNewPartySizeEditText
        mNewGuestNameEditText.text.clear()
        mNewPartySizeEditExt.text.clear()
        mNewPartySizeEditExt.clearFocus()
    }

    private fun getAllGuests(): Cursor =
        mDb.query(
                WaitlistContract.Contacts.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                WaitlistContract.Contacts.COLUMN_TIMESTAMP) // order by

    private fun addGuest(name: String, partySize: Int): Long =
        with (ContentValues()) {
            put(WaitlistContract.Contacts.COLUMN_GUEST_NAME, name)
            put(WaitlistContract.Contacts.COLUMN_PARTY_SIZE, partySize)
            // return the ID
            mDb.insert(WaitlistContract.Contacts.TABLE_NAME, null, this)
        }

    private fun removeGuest(id: Long): Boolean =
        mDb.delete(WaitlistContract.Contacts.TABLE_NAME,
                "${BaseColumns._ID} = $id;", null) > 0
}
