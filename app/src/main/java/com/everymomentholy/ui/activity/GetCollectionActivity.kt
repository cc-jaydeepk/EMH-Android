package com.everymomentholy.ui.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.ui.activity.adapter.FavoriteAdapter
import com.everymomentholy.ui.activity.adapter.GetCollectionAdapter

class GetCollectionActivity : AppCompatActivity() {

    private lateinit var rcvGetCollection: RecyclerView
    private var adapter: RecyclerView.Adapter<GetCollectionAdapter.MyViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_getcollection)

        rcvGetCollection = findViewById(R.id.rcvGetCollection)
        rcvGetCollection.layoutManager = LinearLayoutManager(this)
        rcvGetCollection.adapter = GetCollectionAdapter()
        adapter = GetCollectionAdapter()
    }
}