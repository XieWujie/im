package com.vlog.adapter

import android.util.Log
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlog.R
import com.vlog.conversation.writeMessage.FinalReadView
import com.vlog.conversation.writeMessage.MessageWriteWord

class WordWriteAdapter(private val mList:ArrayList<MessageWriteWord>) :RecyclerView.Adapter<WordWriteAdapter.ViewHolder>(){



    class ViewHolder(val view:FinalReadView):RecyclerView.ViewHolder(view){

        fun load(messageWriteWord: MessageWriteWord){
            view.writeWord(messageWriteWord)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.word_view,parent,false)
        view.setOnDragListener { v, event ->
            when(event.action){
                DragEvent.ACTION_DRAG_EXITED->Log.d("WordWriteAdapter","drag_exit")
            }
            true
        }
        return ViewHolder(view.findViewById(R.id.word_view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.load(mList[position])
    }

    override fun getItemCount(): Int {
       return mList.size
    }
}