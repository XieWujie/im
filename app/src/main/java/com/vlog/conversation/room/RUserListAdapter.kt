package com.vlog.conversation.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.common.ext.launch
import com.vlog.R
import com.vlog.database.User
import com.vlog.databinding.RUListItemBinding
import com.vlog.photo.load
import com.vlog.room.MemberAddActivity
import com.vlog.user.Owner
import com.vlog.user.UserHomeActivity
import com.vlog.user.UserItemEditActivity

class RUserListAdapter(private val conversationId:Int):RecyclerView.Adapter<RecyclerView.ViewHolder>() {


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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):RecyclerView.ViewHolder{
        if(viewType == 0) {
            val binding =
                RUListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            return ViewHolder(binding)
        }else{
            val view = LayoutInflater.from(parent.context).inflate(R.layout.room_member_ic,parent,false)
            view.setOnClickListener {
                MemberAddActivity.launch(conversationId,mList,it.context)
            }
            return object :RecyclerView.ViewHolder(view){

            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder)
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size+1
    }

    override fun getItemViewType(position: Int): Int {
       return if(position<mList.size){
           0
       }else{
           1
       }
    }

}