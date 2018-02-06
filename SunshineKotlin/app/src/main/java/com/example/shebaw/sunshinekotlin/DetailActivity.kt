package com.example.shebaw.sunshinekotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ShareCompat
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView

class DetailActivity : AppCompatActivity() {
    lateinit var mWeatherEntry: TextView
    private var mForecast = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        mWeatherEntry = findViewById<TextView>(R.id.tv_weather_entry)
        intent?.getStringExtra(Intent.EXTRA_TEXT)?.let {
            mForecast = it
            mWeatherEntry.text = mForecast
        }
    }

    private fun createShareForecastIntent() =
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setText(mForecast + FORECAST_SHARE_HASHTAG)
                    .intent

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail, menu)
        val item = menu?.findItem(R.id.action_share)
        item?.intent = createShareForecastIntent()
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?) =
        when (item?.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        const val FORECAST_SHARE_HASHTAG = "#SunshineApp"
    }
}
