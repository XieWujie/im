package com.vlog.verify.list

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.dibus.DiBus
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import com.vlog.R
import com.vlog.connect.MessageSend
import com.vlog.databinding.VerifyListItemBinding
import com.vlog.database.User

class VerifyListAdapter:RecyclerView.Adapter<VerifyListAdapter.ViewHolder>() {


    private val mList = ArrayList<MsgWithUser>()

    fun setList(list:List<MsgWithUser>){
        Log.d("VerifyListAdapter","updateList:${list.toString()}")
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }

    class ViewHolder(private val binding:VerifyListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(message: Message, user: User){
            Glide.with(itemView).load(user.avatar).placeholder(R.drawable.avater_default)
                .into(binding.avatarView)
            binding.usernameText.text = user.username
            binding.verifyMessageText.text = message.content
            binding.agreeBt.setOnClickListener {_->
                val message = Message(0, Owner().userId,user.userId, Message.VerifyAgree,"ok",0)
                DiBus().sendEvent(message,MessageSend{
                    if(it){
                        binding.agreeBt.apply {
                            text = "已同意"
                            background = null
                        }
                    }
                })
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = VerifyListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val en = mList[position]
        holder.bind(en.message,en.user)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}