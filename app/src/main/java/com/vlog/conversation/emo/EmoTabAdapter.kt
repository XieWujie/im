package com.vlog.conversation.emo

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.vlog.R

class EmoTabAdapter(private val itemClickListener:(View, List<String>)->Unit) : RecyclerView.Adapter<EmoTabAdapter.ViewHolder>() {


    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private val srcView = view.findViewById<ImageView>(R.id.tab_src_view)


    }



    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> SMALL_EMO
            1-> GIF
            2-> FAVORITE
            else->0
        }
    }

    companion object{
        private const val SMALL_EMO = 1
        private const val FAVORITE = 3
        private const val GIF = 2
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.emo_tab_item,parent,false)
        val imag = view.findViewById<ImageView>(R.id.tab_src_view)
        val holder = ViewHolder(view)
        when(viewType){
            SMALL_EMO->{
                imag.setImageResource(R.drawable.ic_emoji)
                val asset = imag.context.assets
                val list = asset.list("emotions")?:return holder
                val sources = list.map { "file:///android_asset/$it" }
                itemClickListener.invoke(view,sources)
                view.setOnClickListener {
                    itemClickListener.invoke(it, sources)
                }
            }
            FAVORITE->{
                imag.setImageResource(R.drawable.ic_favarite_s)
            }
            GIF->{
                imag.setImageResource(R.drawable.ic_gif)
                val asset = imag.context.assets
                val list = asset.list("gif_emoji")?:return holder
                val sources = list.map { "file:///android_asset/$it" }
                view.setOnClickListener {
                    itemClickListener.invoke(it,sources)
                }
            }

        }
        return holder
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int {
       return 3
    }

}