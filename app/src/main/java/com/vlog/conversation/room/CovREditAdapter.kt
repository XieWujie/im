package com.vlog.conversation.room

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.Result
import com.common.ext.launch
import com.common.ext.toast
import com.dibus.BusEvent
import com.vlog.database.Room
import com.vlog.databinding.CovRoomItemBinding
import com.vlog.photo.load
import com.vlog.room.RoomAvatarEditActivity
import com.vlog.room.RoomNameEditActivity
import com.vlog.room.RoomSource
import com.vlog.ui.MainActivity
import com.vlog.user.Owner
import dibus.app.ItemViewHolderCreator
import dibus.app.RoomSourceCreator

class CovREditAdapter(private val lifecycleOwner: LifecycleOwner,private var room:Room):RecyclerView.Adapter<CovREditAdapter.ViewHolder>() {

    private val source: RoomSource = RoomSourceCreator.get()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
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





    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind()
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

   inner class RUViewHolder(view:RecyclerView):ViewHolder(view){
        private val adapter = RUserListAdapter(room.conversationId)
        init {
            view.setBackgroundColor(Color.WHITE)
            view.layoutManager = GridLayoutManager(itemView.context,6)
            view.adapter = adapter
        }

        override fun bind(){
            source.getMembers(room.conversationId).observe(lifecycleOwner){
                when(it){
                    is Result.Error->itemView.context.toast(it.toString())
                    is Result.Data->{
                        adapter.mList.clear()
                        adapter.mList.addAll(it.data)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
        }
    }

    inner class ItemViewHolder(private val binding:CovRoomItemBinding):ViewHolder(binding.root){
        init {
            ItemViewHolderCreator.inject(this)
            bind()
            binding.avatarLayout.setOnClickListener {
                RoomAvatarEditActivity.launch(it.context,room)
            }
            binding.roomNameLayout.setOnClickListener {
                RoomNameEditActivity.launch(it.context,room)
            }
            binding.quitRoomLayout.setOnClickListener {view->
                source.quitRoom(room,Owner().userId).observe(lifecycleOwner){
                    when(it){
                        is Result.Error->view.context.toast(it.toString())
                        is Result.Data->{
                            view.context.launch<MainActivity>()
                        }
                    }
                }
            }
        }

        @BusEvent
        fun roomUpdateEvent(r: Room){
            room = r
            bind()
        }

        override fun bind(){
            binding.roomNameText.text = room.roomName
            binding.avatarView.load(room.roomAvatar)
        }
    }

   abstract class ViewHolder(view:View):RecyclerView.ViewHolder(view){
       abstract fun bind()
    }

    companion object{
        private const val ROOM_USER_LIST = 0

        private const val COV_ROOM_ITEM = 1
    }
}