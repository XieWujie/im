package com.vlog.user

import androidx.lifecycle.ViewModel
import com.dibus.ViewModelService

class UserHomeViewModel @ViewModelService(UserHomeActivity::class)internal constructor(
    private val source: UserSource
):ViewModel(){

    fun userRelation(userId:Int) = source.checkRelation(userId)

}