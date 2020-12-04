package com.vlog.conversation.writeMessage

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dibus.DiBus
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vlog.conversation.adapter.WordWriteAdapter

class WordListLayout :RecyclerView{


    private val mList = ArrayList<MessageWriteWord>()
    private val mAdapter = WordWriteAdapter(mList)

    constructor(context: Context):super(context)
    constructor(context: Context, att: AttributeSet):super(context,att)

    init {
        this.adapter = mAdapter
    }


    fun receiveWriteEvent(list:List<MessageWriteWord>){
        layoutManager = if(list.size>7){
            GridLayoutManager(context,7)
        }else{
            GridLayoutManager(context,list.size)
        }
        mList.clear()
        mList.addAll(list)
        mAdapter.notifyDataSetChanged()
    }

    fun handleWrite(content:String){
        val gson:Gson = DiBus.load()
        val token = object : TypeToken<List<MessageWriteWord>>(){}.type
        val m = gson.fromJson<List<MessageWriteWord>>(content,token)
        receiveWriteEvent(m)
    }
}