package com.vlog.database

import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.room.Embedded
import androidx.room.Entity
import com.vlog.user.Owner
import java.util.*

@Entity(primaryKeys = ["sendTime"])
data class Message(
    val messageId: Int,
    var sendFrom: Int,
    var conversationId: Int,
    val messageType: Int,
    var content: String,
    var createAt: Long = 0,
    val sendTime:Long = Date().time,
    var isSend:Boolean = false,
    val isRead:Boolean = false
):Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readLong()
    ) {
    }


    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(messageId)
        parcel.writeInt(sendFrom)
        parcel.writeInt(conversationId)
        parcel.writeInt(messageType)
        parcel.writeString(content)
        parcel.writeLong(createAt)
        parcel.writeLong(sendTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Message> {

        const val Verify = 3
        const val MESSAGE_WRITE = 12
        const val MESSAGE_TEXT = 11
        const val MESSAGE_IMAGE = 13

        override fun createFromParcel(parcel: Parcel): Message {
            return Message(parcel)
        }

        override fun newArray(size: Int): Array<Message?> {
            return arrayOfNulls(size)
        }

        fun obtain(conversationId: Int,messageType: Int,content: String):Message{
            Log.d("obtain",conversationId.toString())
            return Message(0,Owner().userId,conversationId,messageType,content)
        }
    }
}

data class MsgWithUser(
    @Embedded val message: Message,
    @Embedded  val user: User
)




