package com.vlog.ui.relation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.R
import com.vlog.conversation.ConversationActivity
import com.vlog.databinding.FriendListItemBinding

class RelationListAdapter :RecyclerView.Adapter<RelationListAdapter.ViewHolder>(){


    private val mList = ArrayList<UserWithCov>()



    fun setList(list:List<UserWithCov>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
    class ViewHolder(private val binding:FriendListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(userWithCov: UserWithCov){
            Glide.with(itemView).load(userWithCov.user.avatar).placeholder(R.drawable.avater_default)
                .into(binding.avatarView)
            binding.usernameText.text = userWithCov.user.username
            binding.root.setOnClickListener {
                ConversationActivity.launch(it.context,userWithCov.user,userWithCov.conversationId)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       val binding = FriendListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}