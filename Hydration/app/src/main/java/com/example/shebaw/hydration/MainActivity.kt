package com.example.shebaw.hydration

import android.content.*
import android.os.BatteryManager
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Toast
import com.example.shebaw.hydration.sync.ReminderTasks
import com.example.shebaw.hydration.sync.ReminderUtilities
import com.example.shebaw.hydration.sync.WaterReminderIntentService
import com.example.shebaw.hydration.utilities.NotificationUtils
import com.example.shebaw.hydration.utilities.PreferenceUtilities
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SharedPreferences.OnSharedPreferenceChangeListener {
    private var mToast: Toast? = null
    private lateinit var mChargingIntentFilter: IntentFilter
    private lateinit var mChargingReceiver: ChargingBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /** Set the original values in the UI **/
        updateWaterCount()
        updateChargingReminderCount()

        ReminderUtilities.scheduleChargingReminder(this)

        /** Setup the shared preference listener **/
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.registerOnSharedPreferenceChangeListener(this)

        mChargingIntentFilter = IntentFilter()
        with (mChargingIntentFilter) {
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
        }

        mChargingReceiver = ChargingBroadcastReceiver()
    }

    override fun onResume() {
        super.onResume()

        /* Determine the current charging state */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Get a BatteryManager instance
            val batteryManager = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
            showCharging(batteryManager.isCharging)
        } else {
            /* Create a new intent filter with the action ACTION_BATTERY_CHANGED. This is a sticky
             * broadcast that contains a lot of information abou the battery state.
             */
            val ifilter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            // Set a new Intent object equal to what is returned by registerReceiver, passing in null
            // for the receiver. Pass in your intent filter as well. Passing in null means that you're
            // getting the current state of a sticky broadcast - the intent returned will contain the
            // battery information you need.
            val currentBatteryStatusIntent = registerReceiver(null, ifilter)
            val batteryStatus = currentBatteryStatusIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            val isCharging = batteryStatus in setOf(BatteryManager.BATTERY_STATUS_CHARGING, BatteryManager.BATTERY_STATUS_FULL)
            showCharging(isCharging)
        }
        /* Register the receiver with the intent filter */
        registerReceiver(mChargingReceiver, mChargingIntentFilter)
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(mChargingReceiver)
    }

    /**
     * Updates the TextView to display the new water count from SharedPreferences
//     */
    private fun updateWaterCount() {
        val waterCount = PreferenceUtilities.getWaterCount(this)
        tv_water_count.text = waterCount.toString()
    }

    /*
     * Updates the TextView to display the new charging reminder count from SharedPreferences
     */
    private fun updateChargingReminderCount() {
        val chargingReminders = PreferenceUtilities.getChargingReminderCount(this)
        /* use Plurals to change it to a plural form */
        val formattedChargingReminders = resources.getQuantityString(
                R.plurals.charge_notification_count, chargingReminders, chargingReminders)
        tv_charging_reminder_count.text = formattedChargingReminders
    }

    private fun showCharging(isCharging: Boolean) {
        iv_power_increment.setImageResource(if (isCharging) R.drawable.ic_power_pink_80px
            else R.drawable.ic_power_grey_80px)
    }

    fun incrementWater(view: View) {
        mToast?.let { it.cancel() }
        mToast = Toast.makeText(this, R.string.water_chug_toast, Toast.LENGTH_SHORT)
        mToast?.show()
        /* Create an explicit intent for WaterReminderIntentService */
        with (Intent(this, WaterReminderIntentService::class.java)) {
            /* Set the action of the intent to ACTION_INCREMENT_WATER_COUNT */
            setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT)
            /* Call startService and pass the explicit intent you just created */
            startService(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        /* Cleanup the shared preference listener */
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        prefs.unregisterOnSharedPreferenceChangeListener(this)
    }

    /**
     * This is a listener that will update the UI when the water count or charging reminder counts
     * change
     */
    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences?, key: String?) {
        when {
            PreferenceUtilities.KEY_WATER_COUNT.equals(key) -> updateWaterCount()
            PreferenceUtilities.KEY_CHARGING_REMINDER_COUNT.equals(key) -> updateChargingReminderCount()
        }
    }

    /*
    fun testNotification(view: View) =
            NotificationUtils.remindUserBecauseCharging(this)
    */

    private inner class ChargingBroadcastReceiver : BroadcastReceiver() {
        /* Since the broadcast receiver is only registered with two intents,
         * the charging and discharging intents, we can compare to that to see if it's being plugged
         * in or not without worrying about other intents.
         */
        override fun onReceive(context: Context?, intent: Intent?) =
            showCharging(intent?.action == Intent.ACTION_POWER_CONNECTED)
    }

}
