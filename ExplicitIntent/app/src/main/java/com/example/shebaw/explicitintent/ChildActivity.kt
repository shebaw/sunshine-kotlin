package com.example.shebaw.explicitintent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class ChildActivity : AppCompatActivity() {
    private lateinit var mDisplayText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_child)

        mDisplayText = findViewById<TextView>(R.id.tv_display)
        val starterIntent = intent
        // check to see if the intent that created this has extra data
        // before we use it
       intent.getStringExtra(Intent.EXTRA_TEXT).let {
           mDisplayText.text = it
       }
    }
}
