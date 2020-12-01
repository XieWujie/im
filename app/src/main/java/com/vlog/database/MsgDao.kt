package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface MsgDao {

    @Query("select * from message where conversationId=:destination order by sendTime")
    fun getByConversationId(destination:Int):List<Message>

    @Query("select message.*,user.* from message,user where conversationId=:conversationId and message.sendFrom==user.userId order by createAt")
    fun getLiveById(conversationId:Int):LiveData<List<MsgWithUser>>


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(message: Message)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(msgs: List<Message>)

    @Query("select * from message where messageId in (select messageId from message where createAt in (select MAX(createAt) from message group by conversationId)) order  by createAt desc")
    fun getRecentMessage():List<Message>
}