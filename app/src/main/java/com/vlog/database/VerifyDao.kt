package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VerifyDao {

    @Query("select * from verify where userTo=:userTo")
    fun getFromUserTo(userTo:Int): List<Verify>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verify: Verify)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verifies: List<Verify>)

}