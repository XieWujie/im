package com.vlog.user

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.common.Result
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.vlog.R
import com.vlog.database.User
import com.vlog.databinding.ItemEditLayoutBinding
import dibus.app.UserSourceCreator

class UserNameEditActivity : AppCompatActivity() {

    private lateinit var binding:ItemEditLayoutBinding
    private var isChange = false

    private val source = UserSourceCreator.get()
    private val  user = Owner().getUser()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.item_edit_layout)
        binding.titleText.text = "更改名字"
        binding.editText.setText(user.username)
    }

    override fun onStart() {
        super.onStart()
        dispatchEvent()
    }

    private fun dispatchEvent(){
        binding.editText.afterTextChanged {
            if(!isChange){
                isChange = true
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.yellow_rectangle_bg)
                    setTextColor(Color.WHITE)
                }
            }
        }

        binding.submitBt.setOnClickListener {
            val text =binding.editText.text.toString()
            if(isChange && text.isNotEmpty()){
                val newUser = user.copy(username = text)
                source.userUpdate(newUser).observe(this){
                    when(it){
                        is Result.Error->{
                            it.error.printStackTrace()
                            toast(it.toString())
                        }
                        is Result.Data->{
                            Owner().username = text
                            Owner().userFetcher.username = text
                            onBackPressed()
                        }
                    }
                }
            }
        }
        binding.leftView.setOnClickListener {
            onBackPressed()
        }
    }
}