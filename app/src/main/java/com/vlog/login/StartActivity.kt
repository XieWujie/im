package com.vlog.login

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.ext.launch
import com.vlog.R
import com.vlog.databinding.ActivityStartBinding
import com.vlog.login.register.RegisterActivity

class StartActivity : AppCompatActivity() {

    private lateinit var binding:ActivityStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_start)
        binding.loginBt.setOnClickListener {
            launch<LoginActivity>()
        }
        binding.registerBt.setOnClickListener {
            launch<RegisterActivity>()
        }
    }
}