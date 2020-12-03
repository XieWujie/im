package com.vlog.conversation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.InvalidationTracker
import com.common.HOST_PORT
import com.common.Result
import com.common.ext.getType
import com.common.ext.toLiveData
import com.dibus.AutoWire
import com.dibus.Service
import com.vlog.App
import com.vlog.database.*
import com.vlog.user.UserSource
import okhttp3.Request

@Service
class ConversationSource {

   @AutoWire
   lateinit var dao: MsgDao

   @AutoWire
   lateinit var userDao: UserDao

   @AutoWire
   lateinit var friendDao: FriendDao

   @AutoWire
   lateinit var roomDao: RoomDao

   @AutoWire lateinit var userSource:UserSource

   fun getFromConversation(conversationId:Int,maxTime:Long,count:Int = 20):LiveData<List<MsgWithUser>>{
      return dao.getLiveById(conversationId,maxTime,count)
   }

   fun loadFromNet(before:Long,conversationId: Int):LiveData<Result<List<MsgWithUser>>>{
      val url = "$HOST_PORT/message/get?conversationId=$conversationId&&before=$before&&messageType=10"
      val request = Request.Builder()
         .url(url)
         .get()
         .build()
      return request.toLiveData(getType(List::class.java,MsgWithUser::class.java)){
         val users = it.map { it.user }
         userDao.insert(users)
         val messages = it.map { it.message.apply { isSend = true } }
         dao.insert(messages)
      }
   }

   fun getRecentMessageByNet(userId:Int):LiveData<Result<List<MsgWithUser>>>{
      val url = "$HOST_PORT/message/recent?userId=$userId"
      val request = Request.Builder()
         .url(url)
         .get()
         .build()
      return request.toLiveData(getType(List::class.java,MsgWithUser::class.java)){
         dao.insert(it.map { it.message.apply { isSend = true } })
      }
   }


   fun getRecentMessage():LiveData<List<MsgConv>>{
      val roomDatabase = DbApp.get(App.get())
      val liveData = object :MutableLiveData<List<MsgConv>>(){
         val observer = object :InvalidationTracker.Observer(arrayOf("message")){
            override fun onInvalidated(tables: MutableSet<String>) {
               val message = dao.getRecentMessage().map {
                  if(it.fromType == Message.FROM_TYPE_ROOM){
                     val room = roomDao.getRoom(it.conversationId)
                     MsgConv(it,null ,room)
                  }else{
                     val friend = friendDao.getFriendSyn(it.conversationId)
                     MsgConv(it,friend,null)
                  }
               }
               postValue(message)
            }
         }
         override fun onInactive() {
            if(!hasObservers()) {
               roomDatabase.invalidationTracker.removeObserver(observer)
            }
         }
      }

      roomDatabase.invalidationTracker.addObserver(liveData.observer)
      return liveData
   }
}

data class MsgConv(val message: Message, val friend:Friend?,val room: Room?)