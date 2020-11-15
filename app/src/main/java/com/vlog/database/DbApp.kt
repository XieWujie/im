package com.vlog.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.dibus.Provide
import dibus.app.DbAppCreator

@Database(entities = [MsgWithUser::class],version = 1)
abstract class DbApp :RoomDatabase(){

    abstract fun msgDao():MsgDao


    @Provide
    fun provideMsgDao() = msgDao()

    init {
        DbAppCreator.inject(this)
    }

    companion object{


     @Volatile
     private var dbApp:DbApp? = null

        fun get(context: Context):DbApp{
            return dbApp?: synchronized(this){
                dbApp?: Room.databaseBuilder(context,DbApp::class.java,"vlog").build().also { dbApp = it }
            }
        }
    }
}