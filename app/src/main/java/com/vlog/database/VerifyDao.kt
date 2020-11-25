package com.vlog.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface VerifyDao {

    @Query("select verify.*,user.* from verify,user where userTo=:userTo and userFrom==userId")
    fun getFromUserTo(userTo:Int): LiveData<List<VerifyWithUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verify: Verify)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(verifies: List<Verify>)

}