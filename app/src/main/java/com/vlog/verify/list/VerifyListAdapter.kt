package com.vlog.verify.list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vlog.R
import com.vlog.avatar.load
import com.vlog.database.User
import com.vlog.database.Verify
import com.vlog.database.VerifyWithUser
import com.vlog.databinding.VerifyListItemBinding


class VerifyListAdapter(private val mList:ArrayList<VerifyWithUser>,):RecyclerView.Adapter<VerifyListAdapter.ViewHolder>() {


    private var agreeAction:((VerifyWithUser,Int)->Unit)? = null

    fun registerAgreeAction(agreeAction:(VerifyWithUser,Int)->Unit){
        this.agreeAction = agreeAction
    }


   inner class ViewHolder(private val binding:VerifyListItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(verify: Verify, user: User,position: Int){
            binding.avatarView.load(user.avatar)
            binding.usernameText.text = user.username
            binding.verifyMessageText.text = verify.verifyInfo
            when(verify.state){
                Verify.noAction->{
                    binding.agreeBt.setOnClickListener {_->
                        val newV = verify.copy(state = Verify.agree)
                        agreeAction?.invoke(VerifyWithUser(newV,user),position)
                    }
                }
                Verify.agree->{
                    binding.agreeBt.apply {
                        text = "已同意"
                        background = null
                    }
                }
                Verify.defy->{
                    binding.agreeBt.apply {
                        text = "已拒绝"
                        background = null
                    }
                }
            }

        }
    }



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      val binding = VerifyListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val en = mList[position]
        holder.bind(en.verify,en.user,position)
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}