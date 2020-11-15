package com.vlog.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.R
import com.vlog.databinding.FindItemBinding
import com.vlog.database.User
import com.vlog.user.UserHomeActivity

class FindAdapter :RecyclerView.Adapter<FindAdapter.ViewHolder>(){


    private val mList = ArrayList<User>()

    fun setList(list:List<User>){
        mList.clear()
        mList.addAll(list)
        notifyDataSetChanged()
    }
   class ViewHolder(val binding:FindItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(user: User){
            Glide.with(binding.avatarView).load(user.avatar).placeholder(R.drawable.avater_default).into(binding.avatarView)
            binding.usernameText.text = user.username
            binding.root.setOnClickListener {
                UserHomeActivity.launch(it.context,user)
            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = FindItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mList[position])
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}