package com.example.shebaw.sunshinekotlin

import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.preference.CheckBoxPreference
import android.support.v7.preference.ListPreference
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat(),
        SharedPreferences.OnSharedPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_general)

        // iterate through all of the preferences, call the setSummary method if it
        // isn't a checkbox
        for (i in 0 until preferenceScreen.preferenceCount) {
            with (preferenceScreen.getPreference(i)) {
                if (this !is CheckBoxPreference) {
                    val value = preferenceScreen.sharedPreferences.getString(key, "")
                    setPreferenceSummary(this, value)
                }
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
            // for other preferences, set the summary to the value's simple string
            // representation
            else -> preference.setSummary(value)
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

    override fun onStart() {
        super.onStart()
        // register ourselves as a callback for preference change
        preferenceScreen.sharedPreferences.registerOnSharedPreferenceChangeListener(this)
    }

    override fun onStop() {
        preferenceScreen.sharedPreferences.unregisterOnSharedPreferenceChangeListener(this)
        super.onStop()
    }
}
