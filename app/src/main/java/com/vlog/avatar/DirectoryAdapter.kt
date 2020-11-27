package com.vlog.avatar

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.databinding.PhotoDirectoryItemBinding

class DirectoryAdapter:RecyclerView.Adapter<DirectoryAdapter.ViewHolder>() {

    val mList = ArrayList<DirectoryBean>()

    class ViewHolder(val binding:PhotoDirectoryItemBinding):RecyclerView.ViewHolder(binding.root){

       fun bind(bean: DirectoryBean){
           val lastIndex = bean.title.indexOfLast { it == '/' }
           val title = if(lastIndex == -1){
               bean.title
           }else{
               bean.title.substring(lastIndex+1)
           }
           Glide.with(itemView).load(bean.avatar).into(binding.directoryAvatar)
           binding.directoryNameText.text = title
           binding.photoCountText.text = "(${bean.count})"
           binding.selectedView.visibility = if(bean.selected) View.VISIBLE else View.INVISIBLE
           binding.root.setOnClickListener {

           }
       }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = PhotoDirectoryItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}

data class DirectoryBean(val avatar:String,val title:String,val count:Int,val selected:Boolean)