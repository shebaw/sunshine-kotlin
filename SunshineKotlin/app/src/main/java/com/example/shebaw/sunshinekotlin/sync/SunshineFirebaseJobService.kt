package com.example.shebaw.sunshinekotlin.sync

import android.os.AsyncTask
import com.firebase.jobdispatcher.JobParameters
import com.firebase.jobdispatcher.JobService

class SunshineFirebaseJobService : JobService() {
    private var mFetchWeatherTask: AsyncTask<Void, Void, Void?>? = null

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
    override fun onStartJob(jobParams: JobParameters?): Boolean {

        mFetchWeatherTask = object : AsyncTask<Void, Void, Void?>() {
            override fun doInBackground(vararg params: Void?): Void? {
                SunshineSyncTask.syncWeather(applicationContext)
                return null
            }

            override fun onPostExecute(result: Void?) {
                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the jobParameters that were passed to your
                 * job and a boolean representing whether the job needs to be reschedled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */
                jobFinished(jobParams!!, false)
            }
        }

        mFetchWeatherTask?.execute()
        /* There is work "remaining" since the work is being done in a background thread. */
        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        /* If there is background task, cancel it */
        mFetchWeatherTask?.cancel(true)
        /* Return true to signify the job should be retried when the conditions are re-met */
        return true
    }

}
