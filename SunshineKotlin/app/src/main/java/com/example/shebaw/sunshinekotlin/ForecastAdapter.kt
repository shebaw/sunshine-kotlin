package com.example.shebaw.sunshinekotlin

import android.content.Context
import android.database.Cursor
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.shebaw.sunshinekotlin.utilities.SunshineDateUtils
import com.example.shebaw.sunshinekotlin.utilities.SunshineWeatherUtils

class ForecastAdapter(val clickHandler: ForecastAdapterOnClickHandler, val mContext: Context) :
        RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>() {
    private var mCursor: Cursor? = null

    interface ForecastAdapterOnClickHandler {
        fun onListItemClick(s: String)
    }

    private var mWeatherData: ArrayList<String>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastAdapterViewHolder {
        val context = parent.context
        val layoutIdForListItem = R.layout.forecast_list_item
        val inflater = LayoutInflater.from(context)
        val shouldAttachToParentImmediately = false

        val view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately)
        return ForecastAdapterViewHolder(view)
    }

    override fun onBindViewHolder(holder: ForecastAdapterViewHolder, position: Int) {
        // Move the cursor to the appropriate position
        mCursor?.moveToPosition(position)

        // Weather Summary
        // Generate a weather summary with the date, description, high and low

        // Read date from the cursor
        val dateInMillis = mCursor?.getLong(MainActivity.INDEX_WEATHER_DATE)!!
        // Get human readable string using our utility method
        val dateString = SunshineDateUtils.getFriendlyDateString(mContext, dateInMillis, false)
        // Use the weatherId to obtain the proper description
        val weatherId = mCursor?.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID)!!
        val description = SunshineWeatherUtils.getStringForWeatherCondition(mContext, weatherId)
        // Read high temperature from the cursor (in degrees celsius)
        val highInCelsius = mCursor?.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP)!!
        // Read low temperature from the cursor (in degrees celsius)
        val lowInCelsius = mCursor?.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP)!!

        val highAndLowTemperature =
                SunshineWeatherUtils.formatHighLows(mContext, highInCelsius, lowInCelsius)
        val weatherSummary = "$dateString - $description - $highAndLowTemperature"

        // Display the summary that you created above
        holder.weatherSummary.text = weatherSummary
    }

    /**
     * This method simply returns the number of items to display. It is used behind the scenes
     * to help layout our Views and for animations.
     *
     * @return The number of items available in our forecast
     */
    override fun getItemCount() = mCursor?.count ?: 0

    /**
     * Swaps the cursor used by the ForecastAdapter for its weather data. This method is called by
     * MainActivity after a load has finished, as well as when the Loader responsible for loading
     * the weather data is reset. When this method is called, we assume we have a completely new
     * set of data, so we call notifyDataSetChanged to tell the RecyclerView to update.
     *
     * @param newCursor the new cursor to use as ForecastAdapter's data source
     */
    fun swapCursor(newCursor: Cursor?) {
        // close the prevoius cursor
        mCursor?.let { it.close() }
        mCursor = newCursor
        notifyDataSetChanged()
    }

    inner class ForecastAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val weatherSummary = view.findViewById<TextView>(R.id.tv_weather_data)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("onClick", "called for text ${weatherSummary.text.toString()}")
            clickHandler.onListItemClick(weatherSummary.text.toString())
        }
    }

    fun setWeatherData(weatherData: ArrayList<String>?) {
        mWeatherData = weatherData
        notifyDataSetChanged()
    }
}
