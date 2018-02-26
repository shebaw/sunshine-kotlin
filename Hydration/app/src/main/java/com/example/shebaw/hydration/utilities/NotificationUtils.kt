package com.example.shebaw.hydration.utilities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationCompat.Action
import android.support.v4.content.ContextCompat
import com.example.shebaw.hydration.MainActivity
import com.example.shebaw.hydration.R
import com.example.shebaw.hydration.sync.ReminderTasks
import com.example.shebaw.hydration.sync.WaterReminderIntentService

object NotificationUtils {
    /* Clears all notifications */
    fun clearAllNotifications(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        notificationManager.cancelAll()
    }

    fun remindUserBecauseCharging(context: Context) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            /* A channel must exist for notifications above Oreo */
            val mChannel = NotificationChannel(
                    WATER_REMINDER_NOTIFICATION_CHANNEL_ID,
                    context.getString(R.string.main_notification_channel_name),
                    /* Force the notification to pop-up on the device using the heads up display */
                    NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(mChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(context, WATER_REMINDER_NOTIFICATION_CHANNEL_ID)
                .setColor(ContextCompat.getColor(context, R.color.colorPrimary))
                .setSmallIcon(R.drawable.ic_drink_notification)
                .setLargeIcon(largeIcon(context))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_title))
                .setContentTitle(context.getString(R.string.charging_reminder_notification_body))
                .setStyle(NotificationCompat.BigTextStyle().bigText(
                        context.getString(R.string.charging_reminder_notification_body)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                /* Set the intent to launch our MainActivity on click */
                .setContentIntent(contentIntent(context))
                /* Add the two new actions using the addAction method and your helper methods */
                .addAction(drinkWaterAction(context))
                .addAction(ignoreReminderAction(context))
                /* Gurantees that the notification goes away when clicked on */
                .setAutoCancel(true)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            /* Set the priority here if the version is below Oreo */
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH)
        }
        notificationManager.notify(WATER_REMINDER_NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun ignoreReminderAction(context: Context): Action {
        /* Create an Intent to launch WaterReminderIntentService */
        val ignoreReminderIntent = Intent(context, WaterReminderIntentService::class.java)
        /* Set the action of the intent to designate you want to dismiss the notification */
        ignoreReminderIntent.setAction(ReminderTasks.ACTION_DISMISS_NOTIFICATION)
        /* Create a PendingIntent from the intent to launch WaterReminderIntentService */
        val ignoreReminderPendingIntent = PendingIntent.getService(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
        /* Create an Action for the user to ignore the notification (and dismiss it) */
        return Action(R.drawable.ic_cancel_black_24px,
                "No, thanks.",
                ignoreReminderPendingIntent)
    }

    private fun drinkWaterAction(context: Context): Action {
        /* Create an Intent to launch WaterReminderIntentService */
        val incrementWaterCountIntent = Intent(context, WaterReminderIntentService::class.java)
        /* Set the action of the intent to designate you want to increment the water count */
        incrementWaterCountIntent.setAction(ReminderTasks.ACTION_INCREMENT_WATER_COUNT)
        /* Create a PendingIntent from the intent to launch WaterReminderIntentService */
        val incrementWaterPendingIntent = PendingIntent.getService(
                context,
                ACTION_DRINK_PENDING_INTENT_ID,
                incrementWaterCountIntent,
                PendingIntent.FLAG_CANCEL_CURRENT)
        /* Create an Action for the user to tell us they've had a glass of water */
        return Action(R.drawable.ic_local_drink_black_24px,
                "I did it!",
                incrementWaterPendingIntent)
    }

    private fun contentIntent(context: Context): PendingIntent {
        val startAcitivityIntent = Intent(context, MainActivity::class.java)
        return PendingIntent.getActivity(
                context,
                WATER_REMINDER_PENDING_INTENT_ID,
                startAcitivityIntent,
                PendingIntent.FLAG_UPDATE_CURRENT)
    }

    /* This method is necessary to decode a bitmap needed for the notification */
    private fun largeIcon(context: Context) =
            BitmapFactory.decodeResource(context.resources,
                R.drawable.ic_local_drink_black_24px)
    /*
     * This notification ID can be used to access our notification after we've displayed it. This
     * can be handy when we need to cancel the notification, or perhaps update it. This number is
     * arbitrary and can be set to whatever you like. 1138 is in no way significant.
     */
    const val WATER_REMINDER_NOTIFICATION_ID = 1138
    /**
     * This pending intent id is used to uniquely reference the pending intent
     */
    const val WATER_REMINDER_PENDING_INTENT_ID = 3417
    /**
     * This notification channel id is used to link notifications to this channel
     */
    const val WATER_REMINDER_NOTIFICATION_CHANNEL_ID = "reminder_notification_channel"
    const val ACTION_DRINK_PENDING_INTENT_ID = 1
    const val ACTION_IGNORE_PENDING_INTENT_ID = 14
}
