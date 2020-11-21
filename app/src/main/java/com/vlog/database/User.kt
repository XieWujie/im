package com.vlog.database

import android.os.Parcel
import android.os.Parcelable
import androidx.lifecycle.LiveData
import androidx.room.*

@Entity(primaryKeys = ["userId"])
data class User(val userId:Int, val username:String, val avatar:String, val description:String):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(userId)
        parcel.writeString(username)
        parcel.writeString(avatar)
        parcel.writeString(description)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }

}

@Dao
interface UserDao{

    @Query("select * from user where userId=:userId")
    fun getUser(userId:Int): User

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user:List<User>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(user:User)

}



