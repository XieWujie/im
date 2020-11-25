package com.vlog.login.register

import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.launch
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.databinding.ActivityRegisterBinding
import com.vlog.ui.MainActivity
import dibus.app.RegisterActivityCreator


class RegisterActivity : BaseActivity(){

    private lateinit var binding:ActivityRegisterBinding
    @AutoWire
    lateinit var viewModel: RegisterViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RegisterActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        dispatchEvent()
    }

    private fun dispatchEvent(){
        binding.loginBotton.setOnClickListener {
            viewModel.register(binding.userText.text.toString(),binding.passwordText.text.toString()).observe(this,
                Observer {
                    when(it){
                        is Result.Error->toast(it.toString())
                        is Result.Data->{
                            launch<MainActivity>()
                        }
                    }
                })
        }

        binding.userText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus || binding.userText.text.isNotEmpty()){
                binding.icUser.visibility = View.GONE
            }else{
                binding.icUser.visibility = View.VISIBLE
            }
        }
        binding.passwordText.setOnFocusChangeListener { v, hasFocus ->
            if(hasFocus || binding.passwordText.text.isNotEmpty()){
                binding.icPassword.visibility = View.GONE
            }else{
                binding.icPassword.visibility = View.VISIBLE
            }
        }
    }


}