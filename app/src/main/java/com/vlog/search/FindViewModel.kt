package com.vlog.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.common.Result
import com.dibus.ViewModelService
import com.vlog.database.User

class FindViewModel @ViewModelService(FindActivity::class)
internal constructor(private val source: FindSource) :ViewModel(){


    fun findUser(key:String):LiveData<Result<List<User>>>{
        return source.searchUser(key)
    }
}