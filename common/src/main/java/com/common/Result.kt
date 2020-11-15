package com.common

sealed class Result<T> {
    data class Error<T>(val error:Exception):Result<T>(){
        override fun toString(): String {
            return error.message?:"未知错误"
        }
    }

    data class Data<T>(val data:T):Result<T>()

}