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
import com.everymomentholy.ui.activity.adapter.OrderBookAdapter

class FavoritesFragment : Fragment() {

    private lateinit var favRecyclerView: RecyclerView
    private var adapter: RecyclerView.Adapter<FavoriteAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_favorite, container, false)
        favRecyclerView = view.findViewById(R.id.favRecyclerView)
        favRecyclerView.layoutManager = LinearLayoutManager(activity)
        favRecyclerView.adapter = FavoriteAdapter()
        adapter = FavoriteAdapter()
        return view
    }
}