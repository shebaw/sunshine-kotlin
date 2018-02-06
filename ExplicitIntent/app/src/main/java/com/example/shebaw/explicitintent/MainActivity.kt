package com.example.shebaw.explicitintent

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private lateinit var mNameEntry: EditText
    private lateinit var mDoSomethingColButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mDoSomethingColButton = findViewById<Button>(R.id.b_do_something_cool)
        mNameEntry = findViewById<EditText>(R.id.et_text_entry)

        mDoSomethingColButton.setOnClickListener {
            val intent = Intent(this@MainActivity, ChildActivity::class.java)
            intent.putExtra(Intent.EXTRA_TEXT, mNameEntry.text)
            startActivity(intent)
        }
    }
}
