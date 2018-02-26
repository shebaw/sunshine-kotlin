package com.example.shebaw.sunshinekotlin.data

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import com.example.shebaw.sunshinekotlin.utilities.SunshineDateUtils

/*
 * These constant will be used to match URIs with the data they are looking for. We will take
 * advantage of the UriMatcher class to make that matching MUCH easier than doing something
 * ourselves, such as using regular expressions.
 */
const val CODE_WEATHER = 100
const val CODE_WEATHER_WITH_DATE = 101

private fun buildUriMatcher() =
        UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(WeatherContract.CONTENT_AUTHORITY, WeatherContract.PATH_WEATHER, CODE_WEATHER)
            addURI(WeatherContract.CONTENT_AUTHORITY, "${WeatherContract.PATH_WEATHER}/#",
                    CODE_WEATHER_WITH_DATE)
        }

val sUriMatcher = buildUriMatcher()

class WeatherProvider : ContentProvider() {
    private lateinit var mOpenHelper: WeatherDbHelper

    /**
     * In onCreate, we initialize our content provider on startup. This method is called for all
     * registered content providers on the application main thread at application launch time.
     * It must not perform lengthy operations, or application startup will be delayed.
     *
     * Nontrivial initialization (such as opening, upgrading, and scanning
     * databases) should be deferred until the content provider is used (via {@link #query},
     * {@link #bulkInsert(Uri, ContentValues[])}, etc).
     *
     * Deferred initialization keeps application startup fast, avoids unnecessary work if the
     * provider turns out not to be needed, and stops database errors (such as a full disk) from
     * halting application launch.
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    override fun onCreate(): Boolean {
        /*
         * As noted in the comment above, onCreate is run on the main thread, so performing any
         * lengthy operations will cause lag in your app. Since WeatherDbHelper's constructor is
         * very lightweight, we are safe to perform that initialization here.
         */
        mOpenHelper = WeatherDbHelper(context)
        return true
    }

    /**
     * Handles requests to insert a set of new rows. In Sunshine, we are only going to be
     * inserting multiple rows of data at a time from a weather forecast. There is no use case
     * for inserting a single row of data into our ContentProvider, and so we are only going to
     * implement bulkInsert. In a normal ContentProvider's implementation, you will probably want
     * to provide proper functionality for the insert method as well.
     *
     * @param uri    The content:// URI of the insertion request.
     * @param values An array of sets of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     *
     * @return The number of values that were inserted.
     */
    override fun bulkInsert(uri: Uri?, values: Array<out ContentValues>?): Int =
        with (mOpenHelper.writableDatabase) {
            when (sUriMatcher.match(uri)) {
                CODE_WEATHER -> {
                    this.beginTransaction()
                    var rowsInserted = 0
                    try {
                        values?.map {
                            val weatherDate =
                                    it.getAsLong(WeatherContract.WeatherEntry.COLUMN_DATE)
                            if (!SunshineDateUtils.isDateNormalized(weatherDate)) {
                                throw IllegalArgumentException("Date must be normalized to insert")
                            }

                            val _id = this.insert(WeatherContract.WeatherEntry.TABLE_NAME,
                                    null, it)
                            if (_id != -1L) {
                                rowsInserted++
                            }
                        }
                        this.setTransactionSuccessful()
                    } finally {
                        this.endTransaction()
                    }
                    if (rowsInserted > 0) {
                        context.contentResolver.notifyChange(uri, null)
                    }
                    rowsInserted
                }

            // If the URI doesn't match CODE_WEATHER, return the super implementation of bulkInsert
                else -> super.bulkInsert(uri, values)
            }
        }

    /**
     * Handles query requests from clients. We will use this method in Sunshine to query for all
     * of our weather data as well as to query for the weather on a particular day.
     *
     * @param uri           The URI to query
     * @param projection    The list of columns to put into the cursor. If null, all columns are
     *                      included.
     * @param selection     A selection criteria to apply when filtering rows. If null, then all
     *                      rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the
     *                      selection.
     * @param sortOrder     How the rows in the cursor should be sorted.
     * @return A Cursor containing the results of the query. In our implementation,
     */
    override fun query(uri: Uri?, projection: Array<out String>?, selection: String?,
                       selectionArgs: Array<out String>?, sortOrder: String?): Cursor {
        var cursor: Cursor? = null
        when (sUriMatcher.match(uri)) {
            /*
             * When sUriMatcher's match method is called with a URI that looks something like this
             *
             *      content://com.example.android.sunshine/weather/1472214172
             *
             * sUriMatcher's match method will return the code that indicates to us that we need
             * to return the weather for a particular date. The date in this code is encoded in
             * milliseconds and is at the very end of the URI (1472214172) and can be accessed
             * programmatically using Uri's getLastPathSegment method.
             *
             * In this case, we want to return a cursor that contains one row of weather data for
             * a particular date.
             */
            CODE_WEATHER_WITH_DATE -> {
                /*
                 * In order to determine the date associated with this URI, we look at the last
                 * path segment. In the comment above, the last path segment is 1472214172 and
                 * represents the number of seconds since the epoch, or UTC time.
                 */
                val normalizedUtcDateString = uri?.lastPathSegment

                /*
                 * The query method accepts a string array of arguments, as there may be more
                 * than one "?" in the selection statement. Even though in our case, we only have
                 * one "?", we have to create a string array that only contains one element
                 * because this method signature accepts a string array.
                 */
                val selectionArguments = arrayOf(normalizedUtcDateString)

                cursor = mOpenHelper.readableDatabase.query(
                        // Table we are going to query
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        /*
                         * A projection designates the columns we want returned in our Cursor.
                         * Passing null will return all columns of data within the Cursor.
                         * However, if you don't need all the data from the table, it's best
                         * practice to limit the columns returned in the Cursor with a projection.
                         */
                        projection,
                        /*
                         * The URI that matches CODE_WEATHER_WITH_DATE contains a date at the end
                         * of it. We extract that date and use it with these next two lines to
                         * specify the row of weather we want returned in the cursor. We use a
                         * question mark here and then designate selectionArguments as the next
                         * argument for performance reasons. Whatever Strings are contained
                         * within the selectionArguments array will be inserted into the
                         * selection statement by SQLite under the hood.
                         */
                        "${WeatherContract.WeatherEntry.COLUMN_DATE} = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder)
            }

            CODE_WEATHER -> {
                cursor = mOpenHelper.readableDatabase.query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder)

            }

            else -> throw UnsupportedOperationException("Unknown URI: ${uri.toString()}")
        }
        cursor.setNotificationUri(context.contentResolver, uri)
        return cursor
    }

    override fun update(uri: Uri?, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?): Int {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * Deletes data at a given URI with optional arguments for more fine tuned deletions.
     *
     * @param uri           The full URI to query
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs Used in conjunction with the selection statement
     * @return The number of rows deleted
     */
    override fun delete(uri: Uri?, selection: String?, selectionArgs: Array<out String>?): Int {
        var numRowsDeleted = 0
        /*
         * If we pass null as the selection to SQLiteDatabase#delete, our entire table will be
         * deleted. However, if we do pass null and delete all of the rows in the table, we won't
         * know how many rows were deleted. According to the documentation for SQLiteDatabase,
         * passing "1" for the selection will delete all rows and return the number of rows
         * deleted, which is what the caller of this method expects.
         */
        var selection = selection
        if (selection == null) selection = "1"
        when (sUriMatcher.match(uri)) {
            CODE_WEATHER -> {
                numRowsDeleted = mOpenHelper.writableDatabase.delete(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        selection,
                        selectionArgs)
            }
            else -> throw UnsupportedOperationException("Unknown uir: $uri")
        }
        /* If we actually deleted any rows, notify that a change has occurred to this URI */
        if (numRowsDeleted != 0) {
            context.contentResolver.notifyChange(uri, null)
        }
        return numRowsDeleted
    }

    override fun getType(uri: Uri?): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun insert(uri: Uri?, values: ContentValues?): Uri {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    /**
     * You do not need to call this method. This is a method specifically to assist the testing
     * framework in running smoothly. You can read more at:
     * http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
     */
    override fun shutdown() {
        mOpenHelper.close()
        super.shutdown()
    }
}
