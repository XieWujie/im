package com.vlog.ui.relation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlog.conversation.ConversationActivity
import com.vlog.database.Room
import com.vlog.databinding.RelationListItemBinding
import com.vlog.photo.load

class RoomListAdapter :RecyclerView.Adapter<RoomListAdapter.ViewHolder>(){

    private val mList =ArrayList<Room>()

    fun setList(list: List<Room>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding: RelationListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(room:Room){
            binding.nameText.text = if(room.markName.isNullOrBlank()) room.roomName else room.markName
            binding.avatarView.load(room.roomAvatar)
            itemView.setOnClickListener {
                ConversationActivity.launch(it.context,room)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RelationListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}