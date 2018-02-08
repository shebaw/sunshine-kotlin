package com.example.shebaw.sunshinekotlin

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


