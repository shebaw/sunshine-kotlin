package com.example.shebaw.sunshinekotlin.utilities

import android.net.Uri
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

object NetworkUtils {
    private val DYNAMIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/weather";

    private val STATIC_WEATHER_URL =
            "https://andfun-weather.udacity.com/staticweather";

    private val FORECAST_BASE_URL = STATIC_WEATHER_URL


    // these values only effect responses from OpenWeatherMap, not the fake
    // static server
    private val format = "json"
    private val units = "metric"
    private val numDays = "14"

    private val QUERY_PARAM = "q"
    private val LAT_PARAM = "lat"
    private val LON_PARAM = "lon"
    private val FORMAT_PARAM = "mode"
    private val UNITS_PARAM = "units"
    private val DAYS_PARAM = "cnt"


    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param locationQuery The location that will be queried for.
     * @return The URL to use to query the weather server.
     */
    fun buildUrl(locationQuery: String): URL {
        val builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
        builtUri.appendQueryParameter(QUERY_PARAM, locationQuery)
                .appendQueryParameter(FORMAT_PARAM, format)
                .appendQueryParameter(UNITS_PARAM, units)
                .appendQueryParameter(DAYS_PARAM, numDays)
                .build()
        return URL(builtUri.toString())
    }

    fun getResponseFromHttpUrl(url: URL): String? {
        val urlConnection: HttpURLConnection = url.openConnection() as HttpURLConnection
        try {
            val ins = urlConnection.inputStream
            val scanner = Scanner(ins)
            scanner.useDelimiter("\\A")
            return if (scanner.hasNext()) scanner.next() else null
        } finally {
            urlConnection.disconnect()
        }
    }
}
