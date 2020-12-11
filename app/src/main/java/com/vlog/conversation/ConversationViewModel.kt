package com.vlog.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.common.Result
import com.dibus.ViewModelService
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner

class ConversationViewModel @ViewModelService(ConversationActivity::class)
constructor(private val source: ConversationSource) : ViewModel() {


    fun queryMessage(conversationId: Int, maxTime: Long) =
        source.getFromConversation(conversationId, maxTime, 250)


    fun queryBeforeMessage(conversationId: Int, maxTime: Long) =
        source.getFromConversation(conversationId, maxTime, 250)

    fun getLatest(conversationId: Int) = source.dao.getLatestMessage(conversationId)

    fun query(before: Long, conversationId: Int): LiveData<Result<List<MsgWithUser>>> {
        return source.loadFromNet(before, conversationId)
    }

    fun roomChangeListen(conversationId: Int) = source.roomDao.getRoomById(conversationId)

    fun friendUpdateListen(conversationId: Int) = source.friendDao.getFriendByCovId(conversationId)
}