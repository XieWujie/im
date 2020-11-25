package com.vlog.login.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.Result
import com.dibus.ViewModelService
import com.vlog.login.LoginResponse

class RegisterViewModel @ViewModelService(RegisterActivity::class)constructor(private val source: RegisterSource):ViewModel(){

    fun register(username:String, password:String):LiveData<Result<LoginResponse>>{
        val checkLiveData = MutableLiveData<Result<LoginResponse>>()
        if(username.isEmpty() || password.isEmpty()){
            checkLiveData.value = Result.Error(Exception("账号密码长度不能为空"))
            return checkLiveData
        }else{
            return source.register(username,password)
        }
    }
}