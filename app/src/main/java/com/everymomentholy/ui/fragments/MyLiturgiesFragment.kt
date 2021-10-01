package com.everymomentholy.ui.fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.everymomentholy.R
import com.everymomentholy.ui.adapter.ButtomSLiderAdapter
import com.everymomentholy.ui.adapter.MyLiturgyAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog

class MyLiturgiesFragment : Fragment() {

    private lateinit var recycler_liturgy: RecyclerView
    private lateinit var ll_enroute_bottom_sheet: LinearLayout
    private var adapter: RecyclerView.Adapter<MyLiturgyAdapter.MyViewHolder>? = null
    private lateinit var bt: BottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myliturgies, container, false)
        recycler_liturgy = view.findViewById(R.id.recycler_liturgy)
        //supportActionBar?.hide()
        ll_enroute_bottom_sheet = view.findViewById(R.id.ll_enroute_bottom_sheet)
        recycler_liturgy.layoutManager = LinearLayoutManager(activity)
        ll_enroute_bottom_sheet.setOnClickListener {
            showBottomSheetDialog()
        }

        /*val callback = object : OnBackPressedCallback(true){
           override fun handleOnBackPressed() {
               findNavController().navigate(R.id.nav_favoritesFragment)
           }

       }*/



        //requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner,callback)

        requireActivity()
            .onBackPressedDispatcher
            .addCallback(requireActivity(), object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Fragment back pressed invoked")
                    // Do custom work here

                    // if you want onBackPressed() to be called as normal afterwards
                    if (isEnabled) {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
            )

        recycler_liturgy.adapter = MyLiturgyAdapter()
        adapter = MyLiturgyAdapter()
        return view
    }

    private fun showBottomSheetDialog() {
        //val dialog = context?.let { BottomSheetDialog(it) }
        val dialog = context?.let { BottomSheetDialog(it) }
        val view = layoutInflater.inflate(R.layout.activity_buttom_slider, null)

        val buttomRcv = view.findViewById<RecyclerView>(R.id.buttomRcv)
        buttomRcv.layoutManager = LinearLayoutManager(activity)
        buttomRcv.adapter = ButtomSLiderAdapter()
        // adapter = ButtomSLiderAdapter()

        dialog?.setCancelable(true)
        dialog?.setContentView(view)
        dialog?.show()
    }
}