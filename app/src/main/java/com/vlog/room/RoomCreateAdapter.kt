package com.vlog.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlog.database.Friend
import com.vlog.databinding.RoomCreateSelectedItemBinding
import com.vlog.photo.load

class RoomCreateAdapter:RecyclerView.Adapter<RoomCreateAdapter.ViewHolder>()  {

    private val mList = ArrayList<Friend>()
    val checkList = HashSet<Int>()

    fun setList(list:List<Friend>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }



    inner class ViewHolder(val binding: RoomCreateSelectedItemBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(friend: Friend){
           binding.avatarView.load(friend.user.avatar)
            binding.usernameText.text = friend.user.username
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    checkList.add(friend.user.userId)
                }else{
                    checkList.remove(friend.user.userId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RoomCreateSelectedItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
       holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}