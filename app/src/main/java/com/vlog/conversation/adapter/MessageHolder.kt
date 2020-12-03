package com.vlog.conversation.adapter

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.vlog.database.Message
import com.vlog.database.MsgWithUser

open class MessageHolder(view:View) :RecyclerView.ViewHolder(view){

    open fun bind(message: MsgWithUser){}

    open fun bindTime(time:String){}
}

internal fun View.registerDragBack(message: Message){
   
}