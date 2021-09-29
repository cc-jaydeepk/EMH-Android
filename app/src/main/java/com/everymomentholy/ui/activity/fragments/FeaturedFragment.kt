package com.everymomentholy.ui.activity.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.ui.activity.adapter.FavoriteAdapter
import com.everymomentholy.ui.activity.adapter.FeaturedAdapter

class FeaturedFragment : Fragment() {

    private lateinit var rcvFeatured: RecyclerView
    private var adapter: RecyclerView.Adapter<FeaturedAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_featured, container, false)
        rcvFeatured = view.findViewById(R.id.rcvFeatured)
        rcvFeatured.layoutManager = LinearLayoutManager(activity)
        rcvFeatured.adapter = FeaturedAdapter()
        adapter = FeaturedAdapter()
        return view
    }

}