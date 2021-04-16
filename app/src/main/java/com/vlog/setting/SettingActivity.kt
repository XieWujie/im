package com.vlog.setting

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.launch
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.databinding.ActivitySettingBinding
import com.vlog.login.StartActivity
import com.vlog.user.Owner
import com.vlog.user.UserSource
import dibus.app.SettingActivityCreator

class SettingActivity :BaseActivity() {

    private lateinit var binding:ActivitySettingBinding

    @AutoWire
    lateinit var userSource: UserSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_setting)
        SettingActivityCreator.inject(this)
        dispatchEvent()
    }

    private fun dispatchEvent(){
        binding.loginOutLayout.setOnClickListener {
            userSource.logout(Owner().userId).observe(this){
                when(it){
                    is Result.Error->toast(it.toString())
                    is Result.Data->{
                        Owner().isLogout = true
                        toast("已退出登录")
                        launch<StartActivity>()
                    }
                }
            }
        }
        binding.modifyPassword.setOnClickListener {
            launch<ModifyPasswordActivity>()
        }
    }
}