package com.vlog.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.HOST
import com.common.Result
import com.common.ext.getType
import com.common.ext.map
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

   @AutoWire
   lateinit var userDao: UserDao

   @AutoWire lateinit var userSource:UserSource

   fun getFromConversation(conversationId:Int):LiveData<List<MsgWithUser>>{
      return dao.getLiveById(conversationId)
   }

   fun loadFromNet(before:Long,conversationId: Int):LiveData<Result<List<MsgWithUser>>>{
      val url = "$HOST/message/get?destination=$conversationId&&before=$before&&messageType=10"
      val request = Request.Builder()
         .url(url)
         .get()
         .build()
      return request.toLiveData(getType(List::class.java,MsgWithUser::class.java)){
         val users = it.map { it.user }
         userDao.insert(users)
         val messages = it.map { it.message }
         dao.insert(messages)
      }
   }
}