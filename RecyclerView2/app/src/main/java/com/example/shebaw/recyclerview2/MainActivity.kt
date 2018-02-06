package com.example.shebaw.recyclerview2

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Toast

class MainActivity : AppCompatActivity(), GreenAdapter.ListItemClickListener {
    val NUM_LIST_ITEMS = 100

    lateinit var mAdapter: GreenAdapter
    lateinit var mNumbersList: RecyclerView
    var mToast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mNumbersList = findViewById(R.id.rv_numbers) as RecyclerView
        val layoutManager = LinearLayoutManager(this)
        mNumbersList.layoutManager = layoutManager
        mNumbersList.setHasFixedSize(true)
        mAdapter = GreenAdapter(NUM_LIST_ITEMS, this)
        mNumbersList.adapter = mAdapter
    }

    override fun onListItemClick(index: Int) {
        // queued toast that isn't displayed should be cancelled instead
        // so that the next message gets shown immediately
        mToast?.let { it.cancel() }
        mToast = Toast.makeText(this, "$index", Toast.LENGTH_LONG)
        mToast?.show()
    }
}
