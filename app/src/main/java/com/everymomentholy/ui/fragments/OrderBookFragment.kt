package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.ui.adapter.OrderBookAdapter

class OrderBookFragment : Fragment() {

    private lateinit var rcv_order_book: RecyclerView
    private var adapter: RecyclerView.Adapter<OrderBookAdapter.MyViewHolder>? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_ordeerbook, container, false)
        rcv_order_book = view.findViewById(R.id.rcv_order_book)
        rcv_order_book.layoutManager = LinearLayoutManager(activity)
        rcv_order_book.adapter = OrderBookAdapter()
        adapter = OrderBookAdapter()
        return view
    }
}