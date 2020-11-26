package com.vlog.avatar

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.checkbox.MaterialCheckBox
import com.vlog.R

class PhotoListAdapter :RecyclerView.Adapter<PhotoListAdapter.ViewHolder>(){

    val mList = ArrayList<MediaBean>()
    val selectSets = HashSet<MediaBean>()

    inner class ViewHolder(val view:View):RecyclerView.ViewHolder(view){

        private val photoView = view.findViewById<ImageView>(R.id.photo_view)
        private val checkBox = view.findViewById<MaterialCheckBox>(R.id.item_check)

        fun bind(mediaBean: MediaBean){
            Glide.with(photoView).load(mediaBean.path).into(photoView)
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    selectSets.add(mediaBean)
                }else{
                    selectSets.remove(mediaBean)
                }
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.photo_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}