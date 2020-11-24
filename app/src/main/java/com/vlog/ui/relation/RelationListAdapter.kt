package com.vlog.ui.relation

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.ext.launch
import com.vlog.R
import com.vlog.avatar.load
import com.vlog.conversation.ConversationActivity
import com.vlog.database.Friend
import com.vlog.databinding.RelationListItemBinding
import com.vlog.verify.list.VerifyListActivity

class RelationListAdapter :RecyclerView.Adapter<RecyclerView.ViewHolder>(){


    private val mList = ArrayList<Friend>()



    fun setList(list:List<Friend>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
    class ViewHolder(private val binding:RelationListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(friend: Friend){
            binding.avatarView.load(friend.user.avatar)
            binding.nameText.text = friend.user.username
            binding.root.setOnClickListener {
                ConversationActivity.launch(it.context,friend)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = RelationListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return when(viewType){
            NEW_RELATION->{
                NewRelationViewHolder(binding)
            }
            ROOM->RoomViewHolder(binding)
            else->{
                ViewHolder(binding)
            }
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if(holder is ViewHolder)
       holder.bind(mList[position-2])
    }

    override fun getItemCount(): Int {
        return mList.size+2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> NEW_RELATION
            1-> ROOM
            else-> RELATION_LIST
        }
    }

    class NewRelationViewHolder (private val binding:RelationListItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.nameText.text = "新朋友"
            binding.avatarView.scaleType = ImageView.ScaleType.CENTER
            binding.avatarView.setImageResource(R.drawable.ic_new_friend)
            binding.avatarCard.setCardBackgroundColor(Color.parseColor("#fa9e3b"))
            itemView.setOnClickListener {
                VerifyListActivity.launch(it.context)
            }
        }
    }

    class RoomViewHolder(private val binding:RelationListItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.avatarView.scaleType = ImageView.ScaleType.CENTER
            binding.nameText.text = "群组"
            binding.avatarView.setImageResource(R.drawable.ic_room)
            binding.avatarCard.setCardBackgroundColor(Color.parseColor("#07c160"))
            itemView.setOnClickListener {
                it.context.launch<RoomListActivity>()
            }
        }
    }

    private companion object{
        const val NEW_RELATION = 0
        const val ROOM = 1
        const val RELATION_LIST = 2
    }
}