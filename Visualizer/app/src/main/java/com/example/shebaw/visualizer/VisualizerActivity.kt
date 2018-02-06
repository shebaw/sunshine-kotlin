package com.example.shebaw.visualizer

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import com.example.shebaw.visualizer.AudioVisuals.AudioInputReader
import com.example.shebaw.visualizer.AudioVisuals.VisualizerView
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast

class VisualizerActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {

    private lateinit var mVisualizerView: VisualizerView
    private var mAudioInputReader: AudioInputReader? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_visualizer)
        mVisualizerView = findViewById<VisualizerView>(R.id.activity_visualizer)
        setupSharedPreferences()
        setupPermissions()
    }

    private fun setupSharedPreferences() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        with (mVisualizerView) {
            setShowBass(sharedPreferences.getBoolean(getString(R.string.pref_show_bass_key),
                    resources.getBoolean(R.bool.pref_show_bass_default)))
            setShowMid(sharedPreferences.getBoolean("show_mid", true))
            setShowTreble(sharedPreferences.getBoolean("show_treble", true))
            setMinSizeScale(sharedPreferences.getFloat("min_size_scale", 1.0f))
            setColor(sharedPreferences
                    .getString("color", "red"))
        }
        loadColorFromPreferences(sharedPreferences)
        loadSizeFromSharedPreferences(sharedPreferences)
        // our callback will be called when the preference gets changed so that we can apply
        // the changes immediately
        sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    private fun loadColorFromPreferences(sharedPreferences: SharedPreferences) {
        mVisualizerView.setColor(sharedPreferences.getString("color", "red"))
    }

    private fun loadSizeFromSharedPreferences(sharedPreferences: SharedPreferences) {
        val minSize = sharedPreferences.getString(getString(R.string.pref_size_key),
                getString(R.string.pref_size_default)).toFloat()
        mVisualizerView.setMinSizeScale(minSize)
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) =
            when (key) {
                getString(R.string.pref_show_bass_key) ->
                    mVisualizerView.setShowBass(sharedPreferences.getBoolean(key,
                            resources.getBoolean(R.bool.pref_show_bass_default)))
                getString(R.string.pref_show_mid_key) ->
                    mVisualizerView.setShowMid(sharedPreferences.getBoolean(key,
                            resources.getBoolean(R.bool.pref_show_mid_default)))
                getString(R.string.pref_show_triangle_key) ->
                    mVisualizerView.setShowMid(sharedPreferences.getBoolean(key,
                            resources.getBoolean(R.bool.pref_show_triangle_default)))
                getString(R.string.pref_color_key) ->
                    mVisualizerView.setColor(sharedPreferences.getString(key,
                            getString(R.string.pref_color_red_value)))
                getString(R.string.pref_size_key) ->
                        loadSizeFromSharedPreferences(sharedPreferences)
                else -> Unit
            }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     */

    /**
     * onPause Cleanup audio stream
     */
    override fun onPause() {
        super.onPause()
        mAudioInputReader?.let {
            it.shutdown(isFinishing)
        }
    }

    override fun onResume() {
        super.onResume()
        mAudioInputReader?.let {
            it.restart()
        }
    }

    override fun onDestroy() {
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.visualizer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean =
        when (item?.itemId) {
            R.id.action_settings -> {
                val startSettingsActivity = Intent(this, SettingsActivity::class.java)
                startActivity(startSettingsActivity)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    /**
     * App Permissions for Audio
     */
    private fun setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                val permissionsWeNeed = arrayOf(Manifest.permission.RECORD_AUDIO)
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE)
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = AudioInputReader(mVisualizerView, this)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = AudioInputReader(mVisualizerView, this)

                } else {
                    Toast.makeText(this,
                            "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show()
                    finish()
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
        }// Other permissions could go down here
    }

    companion object {
        private val MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88
    }
}