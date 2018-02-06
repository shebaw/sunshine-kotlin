package com.example.shebaw.waitlist.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class WaitlistDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_WAITLIST_TABLE = "CREATE TABLE " +
                "${WaitlistContract.Contacts.TABLE_NAME} ("+
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "${WaitlistContract.Contacts.COLUMN_GUEST_NAME} TEXT NOT NULL, " +
                "${WaitlistContract.Contacts.COLUMN_PARTY_SIZE} INTEGER NOT NULL, " +
                "${WaitlistContract.Contacts.COLUMN_TIMESTAMP} TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");"
        db?.execSQL(SQL_CREATE_WAITLIST_TABLE)

    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // NOTE: If we are upgrading a previous schema, instead of dropping and recreating the
        // database, we can ammend the table and add the new columns
        val SQL_DROP_WAITLIST_TABLE = "DROP TABLE IF EXISTS ${WaitlistContract.Contacts.TABLE_NAME};"
        db?.execSQL(SQL_DROP_WAITLIST_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "waitlist.db"
        // should be incremented when the database schema changes
        const val DATABASE_VERSION = 1
    }
}
