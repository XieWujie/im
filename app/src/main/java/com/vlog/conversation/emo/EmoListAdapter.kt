package com.vlog.conversation.emo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.pushMainThread
import com.dibus.DiBus
import com.vlog.R
import com.vlog.conversation.EmoAddEvent

class EmoListAdapter :RecyclerView.Adapter<EmoListAdapter.ViewHolder>(){

    private val mList = ArrayList<String>()

    fun setList(list:List<String>){
        mList.clear()
        mList.addAll(list)
        pushMainThread {
            notifyDataSetChanged()
        }
    }


    class ViewHolder( private val view:View):RecyclerView.ViewHolder(view){
        private val img = view.findViewById<ImageView>(R.id.emo_src_view)

        fun bind(source:String){
            Glide.with(img).load(source).dontAnimate().into(img)
            img.setOnClickListener {
                DiBus.postEvent(EmoAddEvent(source))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.emo_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}