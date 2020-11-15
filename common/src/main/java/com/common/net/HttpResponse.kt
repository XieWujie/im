package com.common.net

data class HttpResponse<T>(val statusCode:Int,val data:T,val description:String)