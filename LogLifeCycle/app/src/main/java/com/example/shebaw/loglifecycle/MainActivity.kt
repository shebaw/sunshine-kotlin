package com.example.shebaw.loglifecycle

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.View
import android.widget.TextView

class MainActivity : AppCompatActivity() {
    private val TAG = MainActivity::class.java.simpleName

    // constant values for the names of each respective lifecycle callback
    private val ON_CREATE = "onCreate"
    private val ON_START = "onStart"
    private val ON_RESUME = "onResume"
    private val ON_PAUSE = "onPause"
    private val ON_STOP = "onStop"
    private val ON_RESTART = "onRestart"
    private val ON_DESTROY = "onDestroy"
    private val ON_SAVE_INSTANCE_STATE = "onSaveInstanceState"

    private val LIFECYCLE_CALLBACKS_TEXT_KEY = "callback"

    /*
     * This TextView will contain a running log of every lifecycle callback method called from this
     * Activity. This TextView can be reset to its default state by clicking the Button labeled
     * "Reset Log"
     */
    private lateinit var mLifecycleDisplay: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLifecycleDisplay = findViewById<TextView>(R.id.tv_lifecycle_events_display)
        // previous saved instance
        mLifecycleDisplay.text = savedInstanceState?.getString(LIFECYCLE_CALLBACKS_TEXT_KEY) ?: ""
        logAndAppend(ON_CREATE)
    }

    // called when the app returns from the background and is also called
    // immediately after onCreate is called on startup
    override fun onStart() {
        super.onStart()
        logAndAppend(ON_START)
    }

    // called when the app is put in the background
    override fun onStop() {
        super.onStop()
        logAndAppend(ON_STOP)
    }

    // called when the app is partially obscured (by a top level dialog for example, or an implicit
    // intent)
    override fun onPause() {
        super.onPause()
        logAndAppend(ON_PAUSE)
    }

    // called when the obscuring implicit intent is done
    override fun onResume() {
        super.onResume()
        logAndAppend(ON_RESUME)
    }

    override fun onRestart() {
        super.onRestart()
        logAndAppend(ON_RESTART)
    }

    // called when the app is killed and when the acitivity is destroyed
    // can happen when the screen is rotated
    override fun onDestroy() {
        super.onDestroy()
        logAndAppend(ON_DESTROY)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        logAndAppend(ON_SAVE_INSTANCE_STATE)
        // the TextView was being destroyed when the screen was rotated
        outState.putString(LIFECYCLE_CALLBACKS_TEXT_KEY, mLifecycleDisplay.text.toString())
        // always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(outState)
    }

    private fun logAndAppend(lifecycleEvent: String) {
        Log.d(TAG, "Lifecycle Event: $lifecycleEvent")
        mLifecycleDisplay.append("$lifecycleEvent\n")
    }

    private fun resetLifecycleDisplay(v: View) {
        mLifecycleDisplay.text = "Lifecycle callbacks:\n"
    }
}
