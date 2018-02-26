package com.example.shebaw.hydration.sync

import android.content.Context
import com.example.shebaw.hydration.utilities.NotificationUtils
import com.example.shebaw.hydration.utilities.PreferenceUtilities

object ReminderTasks {
    fun executeTask(context: Context, action: String) {
        when (action) {
            ACTION_INCREMENT_WATER_COUNT -> incrementWaterCount(context)
            /* If the user ignored the reminder, clear the notification */
            ACTION_DISMISS_NOTIFICATION -> NotificationUtils.clearAllNotifications(context)
            ACTION_CHARGING_REMINDER -> issueChargingReminder(context)
        }
    }

    private fun issueChargingReminder(context: Context) {
        PreferenceUtilities.incrementChargingReminderCount(context)
        NotificationUtils.remindUserBecauseCharging(context)
    }

    fun incrementWaterCount(context: Context) {
        PreferenceUtilities.incrementWaterCount(context)
        /* If the water count was incremented, clear any notifications */
        NotificationUtils.clearAllNotifications(context)
    }

    val ACTION_INCREMENT_WATER_COUNT = "increment-water-count"
    val ACTION_DISMISS_NOTIFICATION = "dismiss-notification"
    val ACTION_CHARGING_REMINDER = "charging-reminder"
}
