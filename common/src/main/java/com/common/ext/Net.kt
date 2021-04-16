package com.common.ext

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.common.Result
import com.common.net.HttpResponse
import com.dibus.DiBus
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


inline fun<reified T> Request.enqueue(crossinline onResponse:(response: T)->Unit, crossinline onFailure:(e: Exception)->Unit,type:Type = T::class.java){
    val httpClient:OkHttpClient = DiBus.load()
    if(httpClient == null){
        onFailure(Exception("httpClient is null"))
    }else{
        (httpClient as OkHttpClient).newCall(this)
            .enqueue(object :Callback{
                override fun onFailure(call: Call, e: IOException) {
                    e.printStackTrace()
                    onFailure(e)
                }

                override fun onResponse(call: Call, response: Response) {
                    if(!response.isSuccessful){
                        onFailure(Exception(response.message))
                        return
                    }
                    val gs:Gson = DiBus.load()
                    val realType = getType(HttpResponse::class.java,type)
                    val body = response.body!!.string()
                    Log.d("net_body",body)
                    try {
                        val r = gs.fromJson<HttpResponse<T>>(body,realType)
                        if(r.statusCode != 200){
                            onFailure(Exception(r.description))
                        }else if (r.data == null){
                            onFailure(Exception("data == null"))
                        }else{
                            onResponse(r.data)
                        }
                    }catch (jsonE :JsonSyntaxException){
                        val failType = getType(HttpResponse::class.java,String::class.java)
                        val u = gs.fromJson<HttpResponse<String>>(body,failType)
                        onFailure(Exception(u.description))
                    }

                }

            })
    }
}


 fun getType(raw: Class<*>, vararg args: Type) = object : ParameterizedType {
    override fun getRawType(): Type = raw
    override fun getActualTypeArguments(): Array<out Type> = args
    override fun getOwnerType(): Type? = null
}

inline fun <reified T> Request.toLiveData(type:Type = T::class.java,noinline action:((data:T)->Unit)? = null): LiveData<Result<T>> {
    val liveData = MutableLiveData<Result<T>>()
    this.enqueue<T>({
        action?.invoke(it)
        liveData.postValue(Result.Data(it))
    },{
        liveData.postValue(Result.Error(it))
    },type)
    return liveData
}

@Throws(IOException::class)
inline fun <reified T>Request.sync(type: Type = T::class.java):T{
    val httpClient = DiBus().fetch(OkHttpClient::class.java.canonicalName!!) as OkHttpClient
    val response =  httpClient.newCall(this).execute()
    val gs = DiBus.get(Gson::class.java.canonicalName!!) as Gson
    val realType = getType(HttpResponse::class.java,type)
    val res = response.body!!.string()
    Log.d("net", res)
    val r = gs.fromJson<HttpResponse<T>>(res, realType)
    if (r.statusCode == 200) {
        return r.data
    } else {
        throw IOException(r.description)
    }
}