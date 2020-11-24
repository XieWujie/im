package com.vlog.ui.me

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.common.ext.launch
import com.vlog.avatar.UserAvatarActivity
import com.vlog.avatar.load
import com.vlog.databinding.FragmentMeBinding
import com.vlog.user.Owner
import com.vlog.user.UserItemEditActivity

class MeFragment :Fragment(){

    private lateinit var binding:FragmentMeBinding


    private var user = Owner().getUser()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMeBinding.inflate(inflater,container,false)
        initView()
        dispatchEvent()
        return binding.root
    }

    private fun dispatchEvent(){
        binding.avatarView.setOnClickListener {
            UserAvatarActivity.launch(this.requireContext(),user)
        }
        binding.userInfoLayout.setOnClickListener {
            requireContext().launch<UserItemEditActivity>()
        }
    }

    override fun onStart() {
        super.onStart()
        user = Owner().getUser()
        initView()
    }

    private fun initView(){
        binding.nameText.text = user.username
        binding.descriptionText.text = user.description
        binding.avatarView.load(user.avatar)
    }
}