package com.vlog.database

import androidx.room.Embedded
import androidx.room.Entity

@Entity
data class Message(val messageId:Int, var sendFrom:Int, var destination:Int, val messageType:Int, var content:String,var createAt:Long){

    companion object{

        const val SendVerifyAdd  = 3
        const val VerifyAgree = 4
        const val MESSAGE_WRITE = 12
        const val MESSAGE_TEXT = 11
    }
}

@Entity(primaryKeys = ["messageId"])
data class MsgWithUser(
    @Embedded
    val message: Message,
    @Embedded
    val user: User)


