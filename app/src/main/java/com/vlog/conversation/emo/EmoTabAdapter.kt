package com.vlog.conversation.emo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.common.HOST_PORT
import com.common.ext.enqueue
import com.common.ext.getType
import com.vlog.R
import okhttp3.Request

class EmoTabAdapter(private val itemClickListener:(View, List<String>)->Unit): RecyclerView.Adapter<EmoTabAdapter.ViewHolder>() {


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
                val netEmoUrl = "$HOST_PORT/file/emo?name=ordinary"
                val request = Request.Builder().url(netEmoUrl).get().build()
                request.enqueue<List<Emo>>({
                    itemClickListener.invoke(view, it.map { it.icon})
                },{}, getType(List::class.java,Emo::class.java))
                view.setOnClickListener {
                    request.enqueue<List<Emo>>({
                        itemClickListener.invoke(view, it.map { it.icon})
                    },{}, getType(List::class.java,Emo::class.java))
                }
            }
            FAVORITE->{
                imag.setImageResource(R.drawable.ic_favarite_s)
            }
            GIF->{
                imag.setImageResource(R.drawable.ic_gif)
                val netEmoUrl = "$HOST_PORT/file/emo?name=magic"
                val request = Request.Builder().url(netEmoUrl).get().build()
                view.setOnClickListener {view->
                    request.enqueue<List<Emo>>({
                        itemClickListener.invoke(view, it.map { "${it.icon}"}.filter { it.contains(".gif") })
                    },{}, getType(List::class.java,Emo::class.java))
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