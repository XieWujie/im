package com.vlog.adapter

import android.view.View
import android.widget.TextView
import com.vlog.R

class TimeHolder(private val view:View):MessageHolder(view) {
    private val timeView =view.findViewById<TextView>(R.id.time_text)

    override fun bindTime(time: String) {
        timeView.text = time
    }

}