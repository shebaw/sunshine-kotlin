package com.example.shebaw.hydration.sync

import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class WaterReminderFirebaseJobService : JobService() {
    /* JobService runs in the main thread like services.
     * Even though showing a notification is a quick operation, we will
     * run it a new thread to show you how it's done.
     */
    private var mBackgroundTask: AsyncTask<Any, Void, Any?>? = null

    /**
     * The entry point to your Job. Implementations should offload work to another thread of
     * execution as soon as possible.
     *
     * This is called by the Job Dispatcher to tell us we should start our job. Keep in mind this
     * method is run on the application's main thread, so we need to offload work to a background
     * thread.
     *
     * @return whether there is more work remaining.
     */
    override fun onStartJob(params: JobParameters): Boolean {
        /* By default, jobs are executed on the main thread, so make an anonymous class extending
         * AsyncTask called mBackgroundTask.
         * Here's where we man an AyncTask so that this is no longer on the main thread.
         */
        mBackgroundTask = object : AsyncTask<Any, Void, Any?>() {
            override fun doInBackground(vararg params: Any?): Any? {
                /* Use ReminderTasks to execute the new charging reminder task you made, use
                 * this service as the context (WaterReminderFirebaseJobService.this) and return null
                 * when finished
                 */
                val context = this@WaterReminderFirebaseJobService
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_CHARGING_REMINDER)
                return null
            }

            override fun onPostExecute(result: Any?) {
                /*
                 * Once the AsyncTasks is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParameters that were passed to your
                 * job and a boolean representing whether the job needs to be reschedled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */
                jobFinished(params, false)
            }
        }
        // execute the async task
        mBackgroundTask?.execute()
        // We return true since our work is still running in a background thread
        return true
    }

    /**
     * Called when the scheduling engine has decided to interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     *
     * @return whether the job should be retried
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     */
    override fun onStopJob(params: JobParameters?): Boolean {
        /* If there is background task, cancel it */
        mBackgroundTask?.cancel(true)
        /* Return true to signify the job should be retried when the conditions are re-met */
        return true
    }
}
