package com.example.shebaw.hydration.sync

import android.app.IntentService
import android.content.Intent

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
class WaterReminderIntentService : IntentService("WaterReminderIntentService") {
    override fun onHandleIntent(intent: Intent?) =
        ReminderTasks.executeTask(this, intent?.action!!)
}