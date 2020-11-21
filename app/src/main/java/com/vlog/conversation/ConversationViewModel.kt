package com.vlog.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.common.Result
import com.dibus.ViewModelService
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser

class ConversationViewModel @ViewModelService(ConversationActivity::class)
  constructor (private val source: ConversationSource):ViewModel(){


    fun queryMessage(conversationId:Int) = source.getFromConversation(conversationId)


  fun query(before:Long,conversationId: Int): LiveData<Result<List<Message>>> {
    return source.loadFromNet(before, conversationId)
  }
}