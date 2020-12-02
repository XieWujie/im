package com.common.ext

import android.animation.Animator
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.common.pushExecutors

fun Context.toast(content:String){
    Toast.makeText(this,content,Toast.LENGTH_LONG).show()
}

inline fun <reified T>Context.launch(){
    val intent = Intent(this,T::class.java)
    startActivity(intent)
}

inline fun <reified T,reified R> LiveData<T>.map(crossinline transformer:(T)->R):LiveData<R> {
    val liveData = this
    val r = object : MutableLiveData<R>() {
        val observer = Observer<T> {
            setValue(transformer(it))
        }

        override fun onInactive() {
            liveData.removeObserver(observer)
        }
    }
    observeForever(r.observer)
    return r
}

inline fun Animator.animateEnd(crossinline listener:()->Unit){
    this.addListener(object :Animator.AnimatorListener{
        override fun onAnimationStart(animation: Animator?) {

        }

        override fun onAnimationEnd(animation: Animator?) {
           listener.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {

        }

        override fun onAnimationRepeat(animation: Animator?) {

        }

    })
}




