package com.vlog.ui.relation

import androidx.lifecycle.ViewModel
import com.dibus.ViewModelService
import com.vlog.user.Owner

class RelationViewModel @ViewModelService(RelationFragment::class) internal constructor(
    private val source: RelationSource
):ViewModel(){


    fun getRelations() = source.getRelations(Owner().userId)

    fun friendListen() = source.friendListen(Owner().userId)
}