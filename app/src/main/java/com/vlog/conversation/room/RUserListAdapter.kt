package com.vlog.conversation.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.R
import com.vlog.database.User
import com.vlog.databinding.RUListItemBinding

class RUserListAdapter:RecyclerView.Adapter<RUserListAdapter.ViewHolder>() {


    val mList = ArrayList<User>()


    class ViewHolder(private val binding:RUListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(user: User){
            binding.usernameText.text = user.username
            Glide.with(itemView).load(user.avatar).placeholder(R.drawable.avater_default).into(binding.avatarView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =RUListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}