package com.vlog.database

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dibus.Provide
import dibus.app.DbAppCreator

@Database(entities = [Message::class,Verify::class,Friend::class,User::class,Room::class],version = 1)
abstract class DbApp :RoomDatabase(){

    abstract fun msgDao():MsgDao

    abstract fun verifyDao():VerifyDao

    abstract fun friendDao():FriendDao

    abstract fun userDao():UserDao

    abstract fun roomDao():RoomDao

    @Provide
    fun provideMsgDao() = msgDao()

    @Provide
    fun provideVerifyDao() = verifyDao()

    @Provide
    fun provideFriendDao() = friendDao()

    @Provide
    fun provideUserDao() = userDao()

    @Provide
    fun provideRoomDao() = roomDao()

    init {
        DbAppCreator.inject(this)
    }

    companion object{


     @Volatile
     private var dbApp:DbApp? = null

        fun get(context: Context):DbApp{
            return dbApp?: synchronized(this){
                dbApp?: androidx.room.Room.databaseBuilder(context,DbApp::class.java,"vlog").build().also { dbApp = it }
            }
        }
    }
}