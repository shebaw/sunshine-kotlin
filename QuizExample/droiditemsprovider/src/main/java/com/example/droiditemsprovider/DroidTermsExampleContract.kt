package com.shebaw.shebaw.droidtermsprovider

import android.content.ContentResolver
import android.content.ContentUris
import android.net.Uri
import android.provider.BaseColumns

/**
 * The contract between the DroidTermsExample provider and other applications. Contains definitions
 * for the supported URIs and columns.
 */
class DroidTermsExampleContract : BaseColumns {
    companion object {

        /**
         * This it the content authority for DroidTermsExample provider.
         */
        val CONTENT_AUTHORITY = "com.example.udacity.droidtermsexample"

        /**
         * This is the [Uri] on which all other DroidTermsExample Uris are built.
         */
        val BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY)

        /**
         * The path for terms
         */
        val PATH_TERMS = "terms"

        /**
         * This is the [Uri] used to get a full list of terms and definitions.
         */
        val CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TERMS).build()


        /**
         * This is a String type that denotes a Uri references a list or directory.
         */
        val CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS

        /**
         * This is a String type that denotes a Uri references a single item.
         */
        val CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TERMS


        // Declaring all these as constants makes code a lot more readable.
        // It also looks a more like SQL.

        /**
         * This is the version of the database for [android.database.sqlite.SQLiteOpenHelper].
         */
        val DATABASE_VERSION = 1

        /**
         * This is the name of the SQL table for terms.
         */
        val TERMS_TABLE = "term_entries"
        /**
         * This is the name of the SQL database for terms.
         */
        val DATABASE_NAME = "terms"

        /**
         * This is the column name in the SQLiteDatabase for the word.
         */
        val COLUMN_WORD = "word"
        /**
         * This is the column name in the SQLiteDatabase for the definition.
         */
        val COLUMN_DEFINITION = "definition"

        /**
         * This is an array containing all the column headers in the terms table.
         */
        val COLUMNS = arrayOf(BaseColumns._ID, COLUMN_WORD, COLUMN_DEFINITION)

        /**
         * This is the index of the ID in the terms table
         */
        val COLUMN_INDEX_ID = 0
        /**
         * This is the index of the word in the terms table
         */
        val COLUMN_INDEX_WORD = 1
        /**
         * This is the index of the definition in the terms table
         */
        val COLUMN_INDEX_DEFINITION = 2

        /**
         * This method creates a [Uri] for a single term, referenced by id.
         * @param id The id of the term.
         * @return The Uri with the appended id.
         */
        fun buildTermUriWithId(id: Long): Uri {
            return ContentUris.withAppendedId(CONTENT_URI, id)
        }
    }
}