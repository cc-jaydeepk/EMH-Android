package com.everymomentholy.ui.activity.fragments

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.everymomentholy.R
import com.everymomentholy.ui.activity.ContactUsActivty


class FAQFragment : Fragment() {

    private lateinit var btnContactus: Button
    private lateinit var textFaq: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_faq, container, false)
        textFaq = view.findViewById(R.id.textFaq)
        val content = SpannableString("Frequently Asked Questions")
        content.setSpan(UnderlineSpan(), 0, content.length, 0)
        textFaq.setText(content)
        btnContactus = view.findViewById(R.id.btnContactus)
        btnContactus.setOnClickListener {

            val intent = Intent(activity, ContactUsActivty::class.java)
            startActivity(intent)
        }
        return view
    }
}