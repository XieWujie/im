package com.vlog.conversation.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.ext.launch
import com.vlog.database.User
import com.vlog.databinding.RUListItemBinding
import com.vlog.photo.load
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

class RUserListAdapter:RecyclerView.Adapter<RUserListAdapter.ViewHolder>() {


    val mList = ArrayList<User>()


    class ViewHolder(private val binding:RUListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(user: User){
            binding.usernameText.text = user.username
            binding.avatarView.load(user.avatar)
            binding.avatarView.setOnClickListener {
                if (user.userId != Owner().userId) {
                    UserHomeActivity.launch(it.context, user)
                } else {
                    it.context.launch<UserItemEditActivity>()
                }
            }
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