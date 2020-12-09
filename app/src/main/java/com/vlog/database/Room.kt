package com.vlog.database

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(primaryKeys = ["conversationId"])
data class Room(
    val conversationId: Int,
    val roomName: String,
    val roomAvatar: String,
    val roomMasterId: Int,
    var notify:Boolean = true,
    val background:String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()?:"",
        parcel.readString()?:"",
        parcel.readInt(),
        parcel.readByte() != 0.toByte(),
        parcel.readString()?:""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(conversationId)
        parcel.writeString(roomName)
        parcel.writeString(roomAvatar)
        parcel.writeInt(roomMasterId)
        parcel.writeByte(if (notify) 1 else 0)
        parcel.writeString(background)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Room> {
        override fun createFromParcel(parcel: Parcel): Room {
            return Room(parcel)
        }

        override fun newArray(size: Int): Array<Room?> {
            return arrayOfNulls(size)
        }
    }

}

class MemberInfo(val userId: Int, val nickname: String)

@Dao
interface RoomDao {

    @Query("select * from room")
    fun getRooms(): LiveData<List<Room>>


    @Query("select * from room where conversationId=:conversationId")
    fun getRoomById(conversationId: Int): LiveData<Room>

    @Query("select * from room where conversationId=:conversationId")
    fun getRoom(conversationId: Int):Room?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(room:Room)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(rooms:List<Room>)

    @Delete
    fun delete(room:Room)
}