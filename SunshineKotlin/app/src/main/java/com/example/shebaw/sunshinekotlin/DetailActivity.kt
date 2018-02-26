package com.example.shebaw.sunshinekotlin

import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.LoaderManager
import android.support.v4.app.ShareCompat
import android.support.v4.content.CursorLoader
import android.support.v4.content.Loader
import android.view.Menu
import android.view.MenuItem
import com.example.shebaw.sunshinekotlin.data.WeatherContract
import com.example.shebaw.sunshinekotlin.utilities.SunshineDateUtils
import com.example.shebaw.sunshinekotlin.utilities.SunshineWeatherUtils
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity(),
        LoaderManager.LoaderCallbacks<Cursor> {
    var mForecastSummary = ""
    lateinit var mUri: Uri

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mUri = intent.data
        if (mUri == null) throw NullPointerException("URI for DetailActivity cannot be null")

        // Initialize the loader for DetailActivity
        /* This connects our Activity into the loader lifecycle */
        supportLoaderManager.initLoader(ID_DETAIL_LOADER, null, this)
    }

    /**
     * This is where we inflate and set up the menu for this Activity.
     *
     * @param menu The options menu in which you place your items.
     *
     * @return You must return true for the menu to be displayed;
     *         if you return false it will not be shown.
     *
     * @see #onPrepareOptionsMenu
     * @see #onOptionsItemSelected
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        val item = menu?.findItem(R.id.action_share)
        item?.intent = createShareForecastIntent()
        return true
    }

    /**
     * Callback invoked when a menu item was selected from this Activity's menu. Android will
     * automatically handle clicks on the "up" button for us so long as we have specified
     * DetailActivity's parent Activity in the AndroidManifest.
     *
     * @param item The menu item that was selected by the user
     *
     * @return true if you handle the menu click here, false otherwise
     */
    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, SettingsActivity::class.java))
                true
            }
            R.id.action_share -> {
                val shareIntent = createShareForecastIntent()
                startActivity(shareIntent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    /**
     * Uses the ShareCompat Intent builder to create our Forecast intent for sharing.  All we need
     * to do is set the type, text and the NEW_DOCUMENT flag so it treats our share as a new task.
     * See: http://developer.android.com/guide/components/tasks-and-back-stack.html for more info.
     *
     * @return the Intent to use to share our weather forecast
     */
    private fun createShareForecastIntent() =
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                    .intent
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<Cursor> =
        when (id) {
            ID_DETAIL_LOADER -> {
                CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null)
            }
            else -> throw RuntimeException("Loader Not Implemented: $id")
        }

    override fun onLoadFinished(loader: Loader<Cursor>?, data: Cursor?) {
        /* No data to display, simply return and do nothing */
        // data != null && !data.moveToFirst()
        if (data?.moveToFirst() != true) return

        /****************
         * Weather Date *
         ****************/
        /*
         * Read the date from the cursor. It is important to note that the date from the cursor
         * is the same date from the weather SQL table. The date that is stored is a GMT
         * representation at midnight of the date when the weather information was loaded for.
         *
         * When displaying this date, one must add the GMT offset (in milliseconds) to acquire
         * the date representation for the local date in local time.
         * SunshineDateUtils#getFriendlyDateString takes care of this for us.
         */
        val localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE)
        date.text = SunshineDateUtils.getFriendlyDateString(this,
                localDateMidnightGmt, true)

        /***********************
         * Weather Description *
         ***********************/
        /* Read weather condition ID from the cursor (ID provided by Open Weather Map) */
        val weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID)
        /* Use the weatherId to obtain the proper description */
        weather_description.text = SunshineWeatherUtils.getStringForWeatherCondition(this, weatherId)


        /**************************
         * High (max) temperature *
         **************************/
        /* Read high temperature from the cursor (in degrees celsius) */
        val highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        high_temperature.text = SunshineWeatherUtils.formatTemperature(this, highInCelsius)

        /*************************
         * Low (min) temperature *
         *************************/
        /* Read low temperature from the cursor (in degrees celsius) */
        val lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP)
        /*
         * If the user's preference for weather is fahrenheit, formatTemperature will convert
         * the temperature. This method will also append either °C or °F to the temperature
         * String.
         */
        low_temperature.text = SunshineWeatherUtils.formatTemperature(this, lowInCelsius);

        /************
         * Humidity *
         ************/
        /* Read humidity from the cursor */
        val humidityFloat = data.getFloat(INDEX_WEATHER_HUMIDITY)
        humidity.text = getString(R.string.format_humidity, humidityFloat)

        /****************************
         * Wind speed and direction *
         ****************************/
        /* Read wind speed (in MPH) and direction (in compass degrees) from the cursor  */
        val windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED)
        val windDirection = data.getFloat(INDEX_WEATHER_DEGREES)
        wind.text = SunshineWeatherUtils.getFormattedWind(this, windSpeed, windDirection)

        /************
         * Pressure *
         ************/
        /* Read pressure from the cursor */
        val pressureFloat = data.getFloat(INDEX_WEATHER_PRESSURE)

        /*
         * Format the pressure text using string resources. The reason we directly access
         * resources using getString rather than using a method from SunshineWeatherUtils as
         * we have for other data displayed in this Activity is because there is no
         * additional logic that needs to be considered in order to properly display the
         * pressure.
         */
        pressure.text = getString(R.string.format_pressure, pressureFloat);

        /* Store the forecast summary String in our forecast summary field to share later */
        mForecastSummary = "${date.text} - ${weather_description.text}"+
        "- ${high_temperature.text} - ${low_temperature.text}"
    }

    /**
     * Called when a previously created loader is being reset, thus making its data unavailable.
     * The application should at this point remove any references it has to the Loader's data.
     * Since we don't store any of this cursor's data, there are no references we need to remove.
     *
     * @param loader The Loader that is being reset.
     */
    override fun onLoaderReset(loader: Loader<Cursor>?) {
    }

    companion object {
        const val FORECAST_SHARE_HASHTAG = "#SunshineApp"

        /*
         * The columns of data that we are interested in displaying within our DetailActivity's
         * weather display.
         */
        val WEATHER_DETAIL_PROJECTION = arrayOf<String>(
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
        )

        /*
         * We store the indices of the values in the array of Strings above to more quickly be able
         * to access the data from our query. If the order of the Strings above changes, these
         * indices must be adjusted to match the order of the Strings.
         */
        const val INDEX_WEATHER_DATE = 0;
        const val INDEX_WEATHER_MAX_TEMP = 1;
        const val INDEX_WEATHER_MIN_TEMP = 2;
        const val INDEX_WEATHER_HUMIDITY = 3;
        const val INDEX_WEATHER_PRESSURE = 4;
        const val INDEX_WEATHER_WIND_SPEED = 5;
        const val INDEX_WEATHER_DEGREES = 6;
        const val INDEX_WEATHER_CONDITION_ID = 7;

        /*
         * This ID will be used to identify the Loader responsible for loading the weather details
         * for a particular day. In some cases, one Activity can deal with many Loaders. However, in
         * our case, there is only one. We will still use this ID to initialize the loader and create
         * the loader for best practice. Please note that 353 was chosen arbitrarily. You can use
         * whatever number you like, so long as it is unique and consistent.
         */
        const val ID_DETAIL_LOADER = 353
    }
}
