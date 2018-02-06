package com.example.shebaw.visualizer

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.*

class SettingsFragment : PreferenceFragmentCompat(), SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_visualizer)

        // iterate through all of the preferences, if it isn't a checkbox preference,
        // call the setSummary method passing in a preference and the value of the
        // preference
        for (i in 0 until preferenceScreen.preferenceCount) {
            val p = preferenceScreen.getPreference(i)
            if (p !is CheckBoxPreference) { // checkbox implements the summary on the xml already
                val value = preferenceScreen.sharedPreferences.getString(p.key, "")
                setPreferenceSummary(p, value)
            }
        }
    }

    private fun setPreferenceSummary(preference: Preference, value: String) {
        when (preference) {
            is ListPreference -> {
                val index = preference.findIndexOfValue(value)
                if (index >= 0) { // is valid index
                    // get the labels using getEntries
                    preference.setSummary(preference.entries[index])
                }
            }
            is EditTextPreference -> {
                preference.setSummary(value)
            }
        }
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, key: String) {
        findPreference(key)?.let {
            if (it !is CheckBoxPreference) {
                val value = sharedPreferences.getString(it.key, "")
                setPreferenceSummary(it, value)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // register ourselves as a callback for preference change
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onDestroy() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onDestroy()
    }

}

