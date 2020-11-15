package com.vlog.conversation

import androidx.lifecycle.LiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
import okhttp3.Request

@Service
class ConversationSource {

   @AutoWire
   lateinit var dao: MsgDao

   fun getFromConversation(conversationId:Int):LiveData<List<MsgWithUser>>{
      return dao.getFromDestination(conversationId)
   }

   fun loadFromNet(before:Long,conversationId: Int):LiveData<Result<List<MsgWithUser>>>{
      val url = "$HOST/message/get?destination=$conversationId&&before=$before&&messageType=10"
      val request = Request.Builder()
         .url(url)
         .get()
         .build()
      return request.toLiveData(getType(List::class.java,MsgWithUser::class.java)){
         dao.insert(it)
      }
   }
}