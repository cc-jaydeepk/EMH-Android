package com.everymomentholy.ui.activity.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.ui.activity.adapter.FavoriteAdapter
import com.everymomentholy.ui.activity.adapter.SearchAdapter

class SearchFragment : Fragment() {

    private lateinit var recyclerviewSearch: RecyclerView
    private var adapter: RecyclerView.Adapter<SearchAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, container, false)
        recyclerviewSearch = view.findViewById(R.id.recyclerviewSearch)
        recyclerviewSearch.layoutManager = LinearLayoutManager(activity)
        recyclerviewSearch.adapter = SearchAdapter()
        adapter = SearchAdapter()
        return view
    }
}