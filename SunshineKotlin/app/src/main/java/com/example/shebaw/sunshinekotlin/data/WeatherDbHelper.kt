package com.example.shebaw.sunshinekotlin.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns

class WeatherDbHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        val SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " +
                "${WeatherContract.WeatherEntry.TABLE_NAME} (" +
                "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, "+
                "${WeatherContract.WeatherEntry.COLUMN_DATE} INTEGER NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_WEATHER_ID} INTEGER NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_MIN_TEMP} REAL NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_MAX_TEMP} REAL NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_PRESSURE} REAL NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_WIND_SPEED} REAL NOT NULL, "+
                "${WeatherContract.WeatherEntry.COLUMN_DEGREES} REAL NOT NULL, "+
                "UNIQUE (${WeatherContract.WeatherEntry.COLUMN_DATE}) ON CONFLICT REPLACE "+
                ");"
        db?.execSQL(SQL_CREATE_WEATHER_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // NOTE: If we are upgrading a previous schema, instead of dropping and recreating the
        // database, we can ammend the table and add the new columns
        val SQL_DROP_WEATHER_TABLE = "DROP TABLE IF EXISTS ${WeatherContract.WeatherEntry.TABLE_NAME};"
        db?.execSQL(SQL_DROP_WEATHER_TABLE)
        onCreate(db)
    }

    companion object {
        const val DATABASE_NAME = "weather.db"
        // should be incremented when the database schema changes
        // or the onUpgrade method will not be called.
        const val DATABASE_VERSION = 3
    }
}
