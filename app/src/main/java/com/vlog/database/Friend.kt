package com.vlog.database

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(primaryKeys = ["userId"])
data class Friend(@Embedded val user: User,
             val conversationId:Int,
             var ownerId:Int,
             var background:String? = "",
             var markName:String? = null,
             var notify:Boolean = true):Parcelable{
    constructor(parcel: Parcel) : this(
        parcel.readParcelable(User::class.java.classLoader)!!,
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(user, flags)
        parcel.writeInt(conversationId)
        parcel.writeInt(ownerId)
        parcel.writeString(background)
        parcel.writeString(markName)
        parcel.writeByte(if (notify) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Friend> {
        override fun createFromParcel(parcel: Parcel): Friend {
            return Friend(parcel)
        }

        override fun newArray(size: Int): Array<Friend?> {
            return arrayOfNulls(size)
        }
    }

}

@Dao
interface FriendDao{

    @Query("select * from friend where ownerId=:userId")
    fun getFriend(userId:Int): LiveData<List<Friend>>


    @Query("select * from friend where conversationId=:conversationId")
    fun getFriendSyn(conversationId: Int): Friend?

    @Query("select * from friend where conversationId=:conversationId")
    fun getFriendByCovId(conversationId: Int):LiveData<Friend>

    @Query("select * from friend where userId=:userId")
    fun getFromUerId(userId: Int):LiveData<Friend>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friend: Friend)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(friends :List<Friend>)
}