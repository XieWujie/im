package com.vlog.database

import androidx.room.Embedded
import androidx.room.Entity

@Entity(primaryKeys = ["verifyId"])
data class Verify(val verifyId:Int, val state:Int, val verifyInfo:String, val userFrom:Int, val userTo:Int, val createAt:Int){
        companion object{
            const val agree = 0
            const val defy = 1
            const val noAction = 2
        }
    }



class VerifyWithUser(
    @Embedded
    val verify:Verify,
    @Embedded
    val user:User)