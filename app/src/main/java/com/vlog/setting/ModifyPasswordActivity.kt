package com.vlog.setting

import android.graphics.Color
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.vlog.R
import com.vlog.databinding.ActivityModifyPasswordBinding
import com.vlog.user.Owner
import dibus.app.UserSourceCreator

class ModifyPasswordActivity : BaseActivity() {

    private lateinit var binding: ActivityModifyPasswordBinding
    private var isPermit = false

    private val source = UserSourceCreator.get()
    private val user = Owner().getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_modify_password)
    }

    override fun onStart() {
        super.onStart()
        dispatchEvent()
    }

    private fun dispatchEvent() {
        binding.lastText.afterTextChanged {
            modifyEvent()
        }
        binding.newText.afterTextChanged {
            modifyEvent()
        }

        binding.submitBt.setOnClickListener {
            if(!isPermit)return@setOnClickListener
            val old = binding.lastText.text.toString()
            val newP = binding.newText.text.toString()
            source.modifyPassword(user.userId,old,newP).observe(this) {
                when (it) {
                    is Result.Error -> {
                        it.error.printStackTrace()
                        toast(it.toString())
                    }
                    is Result.Data -> {
                        onBackPressed()
                    }
                }
            }
        }
        binding.leftView.setOnClickListener {
            onBackPressed()
        }
    }

    private fun modifyEvent(){
        if(binding.lastText.text.length>=6 && binding.newText.text.length>=6){
            binding.submitBt.apply {
                setBackgroundResource(R.drawable.yellow_rectangle_bg)
                setTextColor(Color.WHITE)
                isPermit = true
            }
        }else{
            binding.submitBt.apply {
                setBackgroundResource(R.drawable.grey_bt_rectangle)
                setTextColor(Color.parseColor("#dadada"))
                isPermit = false
            }
        }
    }


}