package com.everymomentholy.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.ui.activity.ChangePasswordActivity

class MyProfileFragment : Fragment() {

    lateinit var imgChangePsw: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_myprofile, container, false)
        imgChangePsw = view.findViewById(R.id.imgChangePsw)
        imgChangePsw.setOnClickListener {
            val intent = Intent(activity, ChangePasswordActivity::class.java)
            startActivity(intent)
        }
        return view
    }
}