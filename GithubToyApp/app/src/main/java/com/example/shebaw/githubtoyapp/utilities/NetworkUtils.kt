package com.example.shebaw.githubtoyapp.utilities

import android.net.Uri
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Scanner

object NetworkUtils {
    private val GITHUB_BASE_URL =
            "https://api.github.com/search/repositories"
    private val PARAM_QUERY = "q"

    // The sort field. One of stars, forks, or updated.
    // Default: results are sorted by best match if no field is specified.
    private val PARAM_SORT = "sort"
    private val sortBy = "stars"

    fun buildUrl(githubSearchQuery: String): URL? {
        val builtUri = Uri.parse(GITHUB_BASE_URL).buildUpon()
        builtUri.appendQueryParameter(PARAM_QUERY, githubSearchQuery)
                .appendQueryParameter(PARAM_SORT, sortBy)
                .build()
        try {
            return URL(builtUri.toString())
        } catch (e: NumberFormatException) {
            e.printStackTrace()
            return null
        }
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
