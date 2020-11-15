package com.vlog.verify.list

import androidx.lifecycle.ViewModel
import com.dibus.ViewModelService
import com.vlog.user.Owner

class VerifyListViewModel @ViewModelService(VerifyListActivity::class) internal constructor(
    private val source:VerifyListSource
):ViewModel(){

    fun getVerifyList()= source.findVerifyMessage(Owner().userId)
}