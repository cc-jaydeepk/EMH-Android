package com.everymomentholy.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.everymomentholy.R

class NotificationDetailFragment : Fragment() {

    private lateinit var txtDateandTime: TextView
    private lateinit var txtDescription: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_notificationdetail, container, false)
        txtDateandTime = view.findViewById(R.id.txtDateandTime)
        txtDescription = view.findViewById(R.id.txtDescription)

        val bundle = this.arguments
        if (bundle != null) {
            val notificationDate = bundle["date"].toString()
            val notificationMessage = bundle["message"].toString()

            txtDateandTime.text = notificationDate.toString()
            txtDescription.text = notificationMessage.toString()
        }

        // txtDateandTime.text = notificationDate.toString()


        return view
    }
}