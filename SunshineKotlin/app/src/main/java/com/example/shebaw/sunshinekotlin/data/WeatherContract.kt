package com.example.shebaw.sunshinekotlin.data

import android.net.Uri
import android.provider.BaseColumns
import com.example.shebaw.sunshinekotlin.utilities.SunshineDateUtils

class WeatherContract {
    companion object {
        /*
         * The "Content authority" is a name for the entire content provider, similar to the
         * relationship between a domain name and its website. A convenient string to use for the
         * content authority is the package name for the app, which is guaranteed to be unique on the
         * Play Store.
         */
        val CONTENT_AUTHORITY = "com.example.shebaw.sunshinekotlin"

        /*
         * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
         * the content provider for Sunshine.
         */
        val BASE_CONTENT_URI = Uri.parse("content://$CONTENT_AUTHORITY")

        /*
         * Possible paths that can be appended to CONTENT_URI to form valid URI's that Sunshine
         * can handle. For instance,
         *
         *     content://com.example.android.sunshine/weather/
         *     [           CONTENT_URI         ][ PATH_WEATHER ]
         *
         * is a valid path for looking at weather data.
         *
         *      content://com.example.android.sunshine/givemeroot/
         *
         * will fail, as the ContentProvider hasn't been given any information on what to do with
         * "givemeroot". At least, let's hope not. Don't be that dev, reader. Don't be that dev.
         */
        val PATH_WEATHER = "weather"

    }
    class  WeatherEntry : BaseColumns {
        companion object {
            val CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                    .appendPath(PATH_WEATHER)
                    .build()
            const val TABLE_NAME = "weather"
            const val COLUMN_DATE = "date"
            const val COLUMN_WEATHER_ID = "weather_id"
            const val COLUMN_MIN_TEMP = "min"
            const val COLUMN_MAX_TEMP = "max"
            const val COLUMN_HUMIDITY = "humidity"
            const val COLUMN_PRESSURE = "pressure"
            const val COLUMN_WIND_SPEED = "wind"
            const val COLUMN_DEGREES = "degrees"

            /**
             * Builds a URI that adds the weather date to the end of the forecast content URI path.
             * This is used to query details about a single weather entry by date. This is what we
             * use for the detail view query. We assume a normalized date is passed to this method.
             *
             * @param date Normalized date in milliseconds
             * @return Uri to query details about a single weather entry
             */
            fun buildWeatherUriWithDate(date: Long): Uri =
                    BASE_CONTENT_URI.buildUpon()
                            .appendPath(date.toString())
                            .build()

            /**
             * Returns just the selection part of the weather query from a normalized today value.
             * This is used to get a weather forecast from today's date. To make this easy to use
             * in compound selection, we embed today's date as an argument in the query.
             *
             * @return The selection part of the weather query for today onwards
             */
            fun getSqlSelectForTodayOnwards(): String {
                val normalizedUtcNow = SunshineDateUtils.normalizeDate(System.currentTimeMillis())
                return "${WeatherContract.WeatherEntry.COLUMN_DATE} >= $normalizedUtcNow"
            }
        }
    }
}
