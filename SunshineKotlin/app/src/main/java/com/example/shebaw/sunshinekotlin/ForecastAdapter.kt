package com.example.shebaw.sunshinekotlin

import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

class ForecastAdapter(val clickHandler: ForecastAdapterOnClickHandler) : RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder>() {
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
        holder.mWeatherTextView.text = mWeatherData?.get(position) ?: ""
    }

    override fun getItemCount() = mWeatherData?.size ?: 0

    inner class ForecastAdapterViewHolder(view: View) : RecyclerView.ViewHolder(view),
            View.OnClickListener {
        val mWeatherTextView = view.findViewById<TextView>(R.id.tv_weather_data)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.d("onClick", "called for text ${mWeatherTextView.text.toString()}")
            clickHandler.onListItemClick(mWeatherTextView.text.toString())
        }
    }

    fun setWeatherData(weatherData: ArrayList<String>?) {
        mWeatherData = weatherData
        notifyDataSetChanged()
    }
}
