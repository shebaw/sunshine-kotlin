package com.example.shebaw.quizexample

import android.database.Cursor
import android.os.AsyncTask
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.shebaw.shebaw.droidtermsprovider.DroidTermsExampleContract


class MainActivity : AppCompatActivity() {
    private lateinit var mButton: Button
    private lateinit var mWordTextView: TextView
    private lateinit var mDefinitionTextView: TextView
    private var mWordCol: Int = 0
    private var mDefCol: Int = 0
    // change this to a sealed class instead
    private var mCurrentState = State.HIDDEN
    private var mData: Cursor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get the views
        mWordTextView = findViewById<TextView>(R.id.text_view_word)
        mDefinitionTextView = findViewById<TextView>(R.id.text_view_definition)
        mButton = findViewById<Button>(R.id.button_next)

        // Run the database operation to get the cursor off of the main thread
        WordFetchTask().execute()
    }

    fun onButtonClick(view: View) {
        when (mCurrentState) {
        State.HIDDEN -> showDefinition()
        State.SHOWN -> nextWord()
        }
    }

    fun nextWord() {
        mData?.let {
            // Move to the next position in the cursor, if
            if (!it.moveToNext()) {
                it.moveToFirst()
            }
            // Hide the definition TextView
            mDefinitionTextView.visibility = View.INVISIBLE

            // Change the button text
            mButton.text = getString(R.string.show_definition)

            // Get the next word
            mWordTextView.text = it.getString(mWordCol)
            mDefinitionTextView.text = it.getString(mDefCol)

            mCurrentState = State.HIDDEN

        }
        mButton.text = getString(R.string.show_definition)
        mCurrentState = State.HIDDEN
    }

    fun showDefinition() {
        mData?.let {
            mDefinitionTextView.visibility = View.VISIBLE

            mButton.text = getString(R.string.next_word)
            mCurrentState = State.SHOWN
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Remember to close your cursor
        mData?.close()
    }

    // Use an async task to do the data fetch off the main thread.
    inner class WordFetchTask : AsyncTask<Void, Void, Cursor>() {

        // Invoked on a background thread
        override fun doInBackground(vararg params: Void?): Cursor {
            // Make the query to get the data
            val resolver = getContentResolver()

            // Call the query method on the resolver with teh correct Uri from the contract class
            val cursor = resolver.query(DroidTermsExampleContract.CONTENT_URI,
                    null, null, null, null)
            return cursor
        }

        override fun onPostExecute(result: Cursor?) {
            super.onPostExecute(result)

            // Set the data for MainActivity
            mData = result
            mWordCol = mData?.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD)!!
            mDefCol = mData?.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION)!!

            // Set the initial state
            nextWord()
        }
    }

    enum class State {
        HIDDEN, SHOWN
    }
}
