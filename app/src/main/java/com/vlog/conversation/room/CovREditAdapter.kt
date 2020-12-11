package com.vlog.conversation.room

import android.content.Intent
import android.graphics.Color
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.Result
import com.common.ext.launch
import com.common.ext.toast
import com.vlog.database.Room
import com.vlog.databinding.CovRoomItemBinding
import com.vlog.photo.load
import com.vlog.room.RoomAvatarEditActivity
import com.vlog.room.RoomMarkNameActivity
import com.vlog.room.RoomNameEditActivity
import com.vlog.ui.MainActivity
import com.vlog.user.Owner
import dibus.app.RoomSourceCreator

class CovREditAdapter(private val lifecycleOwner: LifecycleOwner,private val room: Room):RecyclerView.Adapter<CovREditAdapter.ViewHolder>() {



    private val source = RoomSourceCreator.get()

    var changeRoomBackgroundListener:(()->Unit)? = null



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
            bind()
            binding.avatarLayout.setOnClickListener {
                RoomAvatarEditActivity.launch(it.context,room)
            }
            binding.roomNameLayout.setOnClickListener {
                RoomNameEditActivity.launch(it.context,room)
            }

            binding.notifySwitch.setOnCheckedChangeListener { view, isChecked ->
                source.roomNotify(room,!isChecked,Owner().userId).observe(lifecycleOwner){
                    when(it){
                        is Result.Error->{
                            view.context.toast(it.toString())
                            binding.notifySwitch.isChecked = isChecked
                        }
                        is Result.Data->{
                            room.notify = !isChecked
                            view.context.toast("更新成功")
                        }
                    }
                }
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
            binding.roomBackgroundLayout.setOnClickListener {
               changeRoomBackgroundListener?.invoke()
            }

            binding.roomMarkNameLayout.setOnClickListener {
                RoomMarkNameActivity.launch(room,it.context)
            }
        }

        override fun bind(){
            binding.notifySwitch.isChecked = !room.notify
            binding.roomNameText.text = room.roomName
            binding.avatarView.load(room.roomAvatar)
            binding.roomMarkNameText.text = room.markName?:""
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