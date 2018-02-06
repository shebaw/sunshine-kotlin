package com.example.shebaw.githubtoyapp

import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.example.shebaw.githubtoyapp.utilities.NetworkUtils
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity(), LoaderManager.LoaderCallbacks<String> {
    private lateinit var mSearchBoxEditText: EditText
    private lateinit var mUrlDisplayTextView: TextView
    private lateinit var mSearchResultsTextView: TextView
    private lateinit var mErrorMessageTextView: TextView
    private lateinit var mLoadingIndicator: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSearchBoxEditText = findViewById<EditText>(R.id.et_search_box)
        mUrlDisplayTextView = findViewById<TextView>(R.id.tv_url_display)
        mSearchResultsTextView = findViewById<TextView>(R.id.tv_github_search_results_json)
        mErrorMessageTextView = findViewById<TextView>(R.id.tv_error_message_display)
        mLoadingIndicator = findViewById<ProgressBar>(R.id.pb_loading_indicator)

        savedInstanceState?.let {
            mUrlDisplayTextView.text = it.getString(SEARCH_QUERY_URL_EXTRA)
            // no need to restore the persisted state since the loader handles that
            // mSearchResultsTextView.text = it.getString(SEARCH_RESULTS_RAW_JSON)
        }
        // initialize the loader
        supportLoaderManager.initLoader(GITHUB_SEARCH_LOADER, null, this)
    }

    private fun makeGithubSearchQuery() {
        val url = NetworkUtils.buildUrl(mSearchBoxEditText.text.toString()) ?: return
        mUrlDisplayTextView.text = url.toString()

        val queryBundle = Bundle().apply {
            putString(SEARCH_QUERY_URL_EXTRA, url.toString())
        }
        // restart loader creates the loader if it doesn't exist
        supportLoaderManager.restartLoader(GITHUB_SEARCH_LOADER, queryBundle, this)
    }

    private fun showJsonDataView() {
        mSearchResultsTextView.visibility = View.VISIBLE
        mErrorMessageTextView.visibility = View.INVISIBLE
    }

    private fun showErrorMessage() {
        mErrorMessageTextView.visibility = View.VISIBLE
        mSearchResultsTextView.visibility = View.INVISIBLE
    }

    override fun onCreateLoader(id: Int, args: Bundle?): Loader<String> {
        return object: AsyncTaskLoader<String>(this) {
            var mGithubJson: String? = null
            override fun onStartLoading() {
                super.onStartLoading()
                // if no arguments are passed, we don't have a query to perform.
                // simply return.
                if (args == null) return
                mLoadingIndicator.visibility = View.VISIBLE
                if (mGithubJson != null) deliverResult(mGithubJson) else forceLoad()
            }

            override fun loadInBackground(): String? {
                // extract the url from the passed bundle
                val searchQueryUrlString = args?.getString(SEARCH_QUERY_URL_EXTRA)
                if (searchQueryUrlString.isNullOrBlank()) return null
                return try {
                    val githubUrl = URL(searchQueryUrlString)
                    Log.i("queryURL", searchQueryUrlString.toString())
                    NetworkUtils.getResponseFromHttpUrl(githubUrl)
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
            }

            override fun deliverResult(data: String?) {
                mGithubJson = data
                super.deliverResult(data)
            }
        }
    }

    override fun onLoadFinished(loader: Loader<String>?, data: String?) {
        // hide the progress bar now that we're done
        mLoadingIndicator.visibility = View.INVISIBLE
        if (data.isNullOrBlank()) {
            showErrorMessage()
            return
        }
        showJsonDataView()
        mSearchResultsTextView.text = data
    }

    override fun onLoaderReset(loader: Loader<String>?) {
        // we aren't using it here, but we must override it since
        // it's declared as abstract
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        //return true
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_search -> {
                makeGithubSearchQuery()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(SEARCH_QUERY_URL_EXTRA, mUrlDisplayTextView.text.toString())
        // the loader handles persisting the json content
        // outState.putString(SEARCH_RESULTS_RAW_JSON, mSearchResultsTextView.text.toString())
        super.onSaveInstanceState(outState)
    }

    companion object {
        private const val SEARCH_QUERY_URL_EXTRA = "query"
        private const val GITHUB_SEARCH_LOADER = 22
    }
}
