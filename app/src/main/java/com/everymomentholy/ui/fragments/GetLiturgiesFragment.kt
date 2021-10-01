package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.ui.activity.GetCollectionActivity

class GetLiturgiesFragment : Fragment() {
    lateinit var btnUnlock: Button
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // return inflater.inflate(R.layout.fragment_getliturgies, container, false)
        val view = inflater.inflate(R.layout.fragment_getcollection, container, false)
        btnUnlock = view.findViewById(R.id.btnUnlock)
        btnUnlock.setOnClickListener {
            val intent = Intent(activity, GetCollectionActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}