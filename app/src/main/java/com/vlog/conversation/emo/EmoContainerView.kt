package com.vlog.conversation.emo

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vlog.R

class EmoContainerView :FrameLayout{

    constructor(context: Context):super(context)

    constructor(context: Context,attributeSet: AttributeSet):super(context,attributeSet)

    private val  tabList:RecyclerView
    private val  emoList:RecyclerView
    private val listAdapter = EmoListAdapter()

    init {
        LayoutInflater.from(context).inflate(R.layout.emoji_list_layout,this,true)
        tabList = findViewById(R.id.tab_list)
        emoList = findViewById(R.id.emo_list)
        emoList.apply {
            adapter = listAdapter
            layoutManager = GridLayoutManager(context,10)
        }
        tabList.apply {
            layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)
            adapter = EmoTabAdapter{_,list->
               listAdapter.setList(list)
            }
        }
    }


}