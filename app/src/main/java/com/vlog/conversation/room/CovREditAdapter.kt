package com.vlog.conversation.room

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.common.Result
import com.common.ext.toast
import com.vlog.R
import com.vlog.database.Room
import com.vlog.databinding.CovRoomItemBinding

class CovREditAdapter(private val lifecycleOwner: LifecycleOwner,private val room:Room):RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ROOM_USER_LIST->{
                RUViewHolder(RecyclerView(parent.context))
            }
            COV_ROOM_ITEM->{
                val binding = CovRoomItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
                ItemViewHolder(binding)
            }
            else->throw RuntimeException("no such viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
       if(holder is RUViewHolder){
           holder.bind()
       }
    }

    override fun getItemCount(): Int {
        return 2
    }

    override fun getItemViewType(position: Int): Int {
        return when(position){
            0-> ROOM_USER_LIST
            else-> COV_ROOM_ITEM
        }
    }

   inner class RUViewHolder(view:RecyclerView):RecyclerView.ViewHolder(view){
        val source = RoomMembersSource()
        private val adapter = RUserListAdapter()
        init {
            view.setBackgroundColor(Color.WHITE)
            view.layoutManager = GridLayoutManager(itemView.context,6)
            view.adapter = adapter
        }

        fun bind(){
            source.getMembers(room.conversationId).observe(lifecycleOwner){
                when(it){
                    is Result.Error->itemView.context.toast(it.toString())
                    is Result.Data->{
                        adapter.mList.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class ItemViewHolder(private val binding:CovRoomItemBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.nameText.text = room.roomName
            Glide.with(itemView).load(room.roomAvatar).placeholder(R.drawable.avater_default).into(binding.avatarView)
        }
    }

    companion object{
        private const val ROOM_USER_LIST = 0

        private const val COV_ROOM_ITEM = 1
    }
}