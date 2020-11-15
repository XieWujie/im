package com.vlog.login

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.launch
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.databinding.ActivityLoginBinding
import com.vlog.ui.MainActivity
import dibus.app.LoginActivityCreator


class LoginActivity : BaseActivity() {

    private lateinit var binding:ActivityLoginBinding

    @AutoWire
     lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        LoginActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        dispatchEvent()
        binding.passwordText.setText("123456")
        binding.userText.setText("xiee")
    }

    private fun dispatchEvent(){
        val observerOfLogin = Observer<Result<LoginResponse>> {
            when(it){
               is Result.Error->toast(it.error.message!!)
                is Result.Data->{
                    launch<MainActivity>()
                }
            }
        }
        binding.loginBotton.setOnClickListener {
            viewModel.login(
                binding.userText.text.toString(),
                binding.passwordText.text.toString()
            ).observe(this, observerOfLogin)
        }

        binding.userText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus || binding.userText.text.isNotEmpty()){
                binding.icUser.visibility = View.GONE
            }else{
                binding.icUser.visibility = View.VISIBLE
            }
        }
        binding.passwordText.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus || binding.passwordText.text.isNotEmpty()){
                binding.icPassword.visibility = View.GONE
            }else{
                binding.icPassword.visibility = View.VISIBLE
            }
        }
    }


}