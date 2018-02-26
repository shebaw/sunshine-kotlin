package com.example.shebaw.sunshinekotlin.sync

import android.content.Context
import android.text.format.DateUtils
import com.example.android.sunshine.utilities.OpenWeatherJsonUtils
import com.example.shebaw.sunshinekotlin.data.SunshinePreferences
import com.example.shebaw.sunshinekotlin.data.WeatherContract
import com.example.shebaw.sunshinekotlin.utilities.NetworkUtils

object SunshineSyncTask {
    @Synchronized fun syncWeather(context: Context) {
        try {
            /*
             * The getUrl method will return the URL that we need to get the forecast JSON for the
             * weather. It will decide whether to create a URL based off of the latitude and
             * longitude or off of a simple location as a String.
             */
            val weatherRequestUrl = NetworkUtils.getUrl(context)

            /* Use the URL to retrieve the JSON */
            val jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestUrl!!)

            /* Parse the JSON into a list of weather values */
            val weatherValues = OpenWeatherJsonUtils
                    .getWeatherContentValuesFromJson(context, jsonWeatherResponse!!)

            /*
             * In cases where our JSON contained an error code, getWeatherContentValuesFromJson
             * would have returned null. We need to check for those cases here to prevent any
             * NullPointerExceptions being thrown. We also have no reason to insert fresh data if
             * there isn't any to insert.
             */
            if (weatherValues != null && !weatherValues.isEmpty()) {
                /* Get a handle on the ContentResolver to delete and insert data */
                val sunshineContentResolver = context.contentResolver

                /* Delete old weather data because we don't need to keep multiple days' data */
                sunshineContentResolver.delete(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        null,
                        null)

                /* Insert our new weather data into Sunshine's ContentProvider */
                sunshineContentResolver.bulkInsert(
                        WeatherContract.WeatherEntry.CONTENT_URI,
                        weatherValues.toTypedArray())

                /*
                 * Finally, after we insert data into the ContentProvider, determine whether or not
                 * we should notify the user that the weather has been refreshed.
                 */
                val notificationsEnabled = SunshinePreferences.areNotificationsEnabled(context)

                /*
                 * If the last notification was shown was more than 1 day ago, we want to send
                 * another notification to the user that the weather has been updated. Remember,
                 * it's important that you shouldn't spam your users with notifications.
                 */
                val timeSinceLastNotification = SunshinePreferences
                        .getEllapsedTimeSinceLastNotification(context)

                var oneDayPassedSinceNotification = timeSinceLastNotification >= DateUtils.DAY_IN_MILLIS

                /*
                 * We only want to show the notification if the user wants them shown and we
                 * haven't shown a notification in the past day.
                 */
                if (notificationsEnabled && oneDayPassedSinceNotification) {
                    NotificationUtils.notifyUserOfNewWeather(context)
                }


            }
            /* If the code reaches this point, we have successfully performed our sync */
        } catch (e: Exception) {
            /* Server probably invalid */
            e.printStackTrace()
        }
    }
}