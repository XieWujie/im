package com.vlog.database

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["messageId"])
data class Message(
    val messageId: Int,
    var sendFrom: Int,
    var conversationId: Int,
    val messageType: Int,
    var content: String,
    var createAt: Long
) {

    companion object {

        const val Verify = 3
        const val MESSAGE_WRITE = 12
        const val MESSAGE_TEXT = 11
    }
}

data class MsgWithUser(
    @Embedded val message: Message,
    @Embedded  val user: User
)




