package com.vlog.verify.list

import androidx.lifecycle.ViewModel
import com.dibus.ViewModelService
import com.vlog.database.VerifyWithUser
import com.vlog.user.Owner
import com.vlog.verify.VerifySource

class VerifyListViewModel @ViewModelService(VerifyListActivity::class) internal constructor(
    private val source:VerifySource
):ViewModel(){

    fun getVerifyList()= source.findVerifyMessage(Owner().userId)

    fun sendVerify(verifyWithUser: VerifyWithUser) = source.sendVerify(verifyWithUser)

    fun obsVerifyList() = source.obsVerifyList(Owner().userId)
}