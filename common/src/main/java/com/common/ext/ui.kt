package com.common.ext

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

fun Context.toast(content:String){
    Toast.makeText(this,content,Toast.LENGTH_LONG).show()
}

inline fun <reified T>Context.launch(){
    val intent = Intent(this,T::class.java)
    startActivity(intent)
}