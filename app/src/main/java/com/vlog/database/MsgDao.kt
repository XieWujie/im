package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface MsgDao {

    @Query("select * from message where conversationId=:destination and messageType>10 and messageType<100 order by sendTime")
    fun getByConversationId(destination:Int):List<Message>

    @Query("select message.*,user.* from message,user where conversationId=:conversationId and message.sendFrom==user.userId and sendTime<:maxTime and messageType>10 and messageType<100 order by sendTime desc limit 0,:count")
    fun getLiveById(conversationId:Int,maxTime:Long,count:Int = 20):LiveData<List<MsgWithUser>>

    @Query("select message.*,user.* from message,user where messageId=:messageId and userId=message.sendFrom")
    fun getByMessageId(messageId:Int):MsgWithUser


    @Query("select message.*,user.* from message,user where conversationId=:conversationId and message.sendFrom==user.userId and messageType>10 and messageType<100 order by sendTime desc limit 5")
    fun getLatestMessage(conversationId: Int):LiveData<List<MsgWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msgs: List<Message>)

    @Query("select * from message where messageId in (select messageId from message where createAt in (select MAX(createAt) from message group by conversationId))  and messageType>10 and messageType<100 order  by createAt desc")
    fun getRecentMessage():List<Message>

    @Delete
    fun delete(message: Message)
}