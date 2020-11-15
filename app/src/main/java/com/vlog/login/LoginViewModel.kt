package com.vlog.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.common.Result
import com.dibus.ViewModelService

class LoginViewModel @ViewModelService(LoginActivity::class)constructor(private val source: LoginSource):ViewModel(){

    fun login(username:String,password:String):LiveData<Result<LoginResponse>>{
        return if(username.isEmpty() || password.isEmpty()){
            val checkLiveData = MutableLiveData<Result<LoginResponse>>()
            checkLiveData.value = Result.Error(Exception("账号密码长度不能为空"))
            checkLiveData
        }else{
            source.login(username,password)
        }
    }
}