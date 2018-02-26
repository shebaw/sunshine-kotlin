package com.example.shebaw.hydration.utilities

import android.content.Context
import android.preference.Preference
import android.preference.PreferenceManager

/**
 * This class contains utility methods which update water and charging counts in SharedPreferences
 */
object PreferenceUtilities {
    @Synchronized private fun setWaterCount(context: Context, glassesOfWater: Int) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        with (prefs.edit()) {
            putInt(KEY_WATER_COUNT, glassesOfWater)
            apply()
        }
    }

    fun getWaterCount(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(KEY_WATER_COUNT, DEFAULT_COUNT)
    }

    @Synchronized fun incrementWaterCount(context: Context) {
        val waterCount = getWaterCount(context)
        setWaterCount(context, waterCount + 1)
    }

    @Synchronized fun incrementChargingReminderCount(context: Context) {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        val chargingReminders = prefs.getInt(KEY_CHARGING_REMINDER_COUNT, DEFAULT_COUNT)

        with (prefs.edit()) {
            putInt(KEY_CHARGING_REMINDER_COUNT, chargingReminders + 1)
            apply()
        }
    }

    fun getChargingReminderCount(context: Context): Int {
        val prefs = PreferenceManager.getDefaultSharedPreferences(context)
        return prefs.getInt(KEY_CHARGING_REMINDER_COUNT, DEFAULT_COUNT)
    }

    const val KEY_WATER_COUNT = "water-count"
    const val KEY_CHARGING_REMINDER_COUNT = "charging-reminder-count"
    const val DEFAULT_COUNT = 0
}
