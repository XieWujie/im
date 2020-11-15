package com.vlog.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vlog.database.MsgWithUser

abstract class MessageHolder(view:View) :RecyclerView.ViewHolder(view){

    abstract fun bind(message: MsgWithUser)
}