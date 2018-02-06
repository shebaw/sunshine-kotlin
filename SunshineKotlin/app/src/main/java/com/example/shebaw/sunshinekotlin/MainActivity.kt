package com.example.shebaw.sunshinekotlin

import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ShareCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.ProgressBar
import com.example.shebaw.sunshinekotlin.data.SunshinePreferences
import com.example.shebaw.sunshinekotlin.utilities.NetworkUtils
import com.example.shebaw.sunshinekotlin.utilities.OpenWeatherJsonUtils
import java.io.IOException

class MainActivity : AppCompatActivity(), ForecastAdapter.ForecastAdapterOnClickHandler {
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mForecastAdapter: ForecastAdapter
    private lateinit var mErrorMessageTextView: TextView
    private lateinit var mLoadingProgressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forecast)
        mRecyclerView = findViewById<RecyclerView>(R.id.recyclerview_forecast)
        mErrorMessageTextView = findViewById<TextView>(R.id.tv_error_message_display)
        mLoadingProgressBar = findViewById<ProgressBar>(R.id.pb_loading_indicator)
        val layoutManager = LinearLayoutManager(this,
                LinearLayout.VERTICAL, false)
        mRecyclerView.layoutManager = layoutManager
        mRecyclerView.setHasFixedSize(true)
        mForecastAdapter = ForecastAdapter(this)
        mRecyclerView.adapter = mForecastAdapter

        loadWeatherData()
    }

    private fun loadWeatherData() {
        WeatherQueryTask().execute(SunshinePreferences.getPreferredWeatherLocation(this))
    }

    private fun showWeatherDataView() {
        mRecyclerView.visibility = View.VISIBLE
        mErrorMessageTextView.visibility = View.INVISIBLE
    }

    private fun showErrorMessage() {
        mErrorMessageTextView.visibility = View.VISIBLE
        mRecyclerView.visibility = View.INVISIBLE
    }

    inner class WeatherQueryTask : AsyncTask<String, Void, ArrayList<String>?>() {
        override fun onPreExecute() {
            mLoadingProgressBar.visibility = View.VISIBLE
            showWeatherDataView()
        }

        override fun doInBackground(vararg params: String): ArrayList<String>? {
            val location = if (params.size > 0) params[0] else return null
            var weatherData: ArrayList<String>? = null
            try {
                val url = NetworkUtils.buildUrl(location)
                val response = NetworkUtils.getResponseFromHttpUrl(url) ?: return null
                weatherData = OpenWeatherJsonUtils.
                        getSimpleWeatherStringsFromJson(this@MainActivity, response)
            } catch (e: NumberFormatException) {
                Log.i("WeatherQueryTask", "invalid url field")
                e.printStackTrace()
            } catch (e: IOException) {
                Log.i("WeatherQueryTask", "network IO error")
                e.printStackTrace()
            }
            return weatherData
        }

        override fun onPostExecute(result: ArrayList<String>?) {
            // hide the progress bar
            mLoadingProgressBar.visibility = View.INVISIBLE
            if (result == null || result.size == 0) {
                showErrorMessage()
                return
            }
            showWeatherDataView()
            mForecastAdapter.setWeatherData(result)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.forecast, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_refresh -> {
                mForecastAdapter.setWeatherData(null)
                loadWeatherData()
                true
            }
            /*
            R.id.action_share -> {
                shareText(DetailActivity.mWeatherEntry.text)
                true
            }
            */
            R.id.action_map -> {
                openLocationInMap()
                true
            }
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else ->super.onOptionsItemSelected(item)
        }

    override fun onListItemClick(s: String) {
        with (Intent(this, DetailActivity::class.java)) {
            putExtra(Intent.EXTRA_TEXT, s)
            startActivity(this)
        }
    }

    private fun shareText(textToShare: String) {
        val mimeType = "text/plain"
        val title = "Learning How to Share"
        ShareCompat.IntentBuilder.from(this)
                .setChooserTitle(title)
                .setType(mimeType)
                .setText(textToShare)
                .startChooser()
    }

    private fun openLocationInMap() {
        val address = PreferenceManager.getDefaultSharedPreferences(this)
                ?.getString("location", DEFAULT_ADDRESS) ?: DEFAULT_ADDRESS
        val builder = Uri.Builder()
        builder.scheme("geo")
                .path("0,0")
                .query(address)
        showMap(builder.build())
    }

    private fun showMap(uri: Uri) =
        with(Intent(Intent.ACTION_VIEW)) {
            data = uri
            resolveActivity(packageManager).let {
                startActivity(this)
            }
        }

    companion object {
        const val DEFAULT_ADDRESS = "1600 Amphitheatre Parkway, CA"
    }
}
