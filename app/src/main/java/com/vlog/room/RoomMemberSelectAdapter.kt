package com.vlog.room

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vlog.database.User
import com.vlog.databinding.RoomCreateSelectedItemBinding
import com.vlog.photo.load

class RoomMemberSelectAdapter:RecyclerView.Adapter<RoomMemberSelectAdapter.ViewHolder>()  {

    private val mList = ArrayList<User>()
    val checkList = HashSet<Int>()

    var checkListStateListener:((Boolean)->Unit)? = null

    fun setList(list:List<User>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }


    inner class ViewHolder(val binding: RoomCreateSelectedItemBinding) :RecyclerView.ViewHolder(binding.root){

        fun bind(user: User){
           binding.avatarView.load(user.avatar)
            binding.usernameText.text = user.username
            binding.checkbox.setOnCheckedChangeListener { _, isChecked ->
                if(isChecked){
                    checkList.add(user.userId)
                    if(checkList.size == 1){
                        checkListStateListener?.invoke(true)
                    }
                }else{
                    checkList.remove(user.userId)
                    if(checkList.isEmpty()){
                        checkListStateListener?.invoke(false)
                    }
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