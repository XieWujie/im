package com.vlog.photo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.databinding.PhotoDirectoryItemBinding

class DirectoryAdapter(val callback: (String) -> Unit) :
    RecyclerView.Adapter<DirectoryAdapter.ViewHolder>() {

    val mList = ArrayList<DirectoryBean>()

    private var lastItemListener: (() -> Unit)? = null

    inner class ViewHolder(val binding: PhotoDirectoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(bean: DirectoryBean, index: Int) {
            val lastIndex = bean.title.indexOfLast { it == '/' }
            val title = if (lastIndex == -1) {
                bean.title
            } else {
                bean.title.substring(lastIndex + 1)
            }
            Glide.with(itemView).load(bean.avatar).into(binding.directoryAvatar)
            binding.directoryNameText.text = title
            binding.photoCountText.text = "(${bean.count})"
            binding.selectedView.visibility = if (bean.selected) {
                lastItemListener = {
                    bean.selected = false
                    binding.selectedView.visibility = View.INVISIBLE
                }
                View.VISIBLE
            } else {
                View.INVISIBLE
            }

            binding.root.setOnClickListener {
                lastItemListener?.invoke()
                binding.selectedView.visibility = View.VISIBLE
                bean.selected = true
                callback(bean.title)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            PhotoDirectoryItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position], position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}

data class DirectoryBean(
    val avatar: String,
    val title: String,
    val count: Int,
    var selected: Boolean
)