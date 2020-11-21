package com.vlog.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.common.pushExecutors
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.database.MsgWithUser
import com.vlog.database.UserDao
import com.vlog.user.UserSource
import okhttp3.Request

@Service
class ConversationSource {

   @AutoWire
   lateinit var dao: MsgDao

   @AutoWire lateinit var userSource:UserSource

   fun getFromConversation(conversationId:Int):LiveData<List<MsgWithUser>>{
      val liveData = MutableLiveData<List<MsgWithUser>>()
      pushExecutors {
        val mList =  dao.getByConversationId(conversationId).map {
            MsgWithUser(it,userSource.findUser(it.sendFrom))
         }
         liveData.postValue(mList)
      }
      return liveData
   }

   fun loadFromNet(before:Long,conversationId: Int):LiveData<Result<List<Message>>>{
      val url = "$HOST/message/get?destination=$conversationId&&before=$before&&messageType=10"
      val request = Request.Builder()
         .url(url)
         .get()
         .build()
      return request.toLiveData(getType(List::class.java,Message::class.java)){
         dao.insert(it)
      }
   }
}