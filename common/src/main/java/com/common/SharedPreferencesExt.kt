package com.common

import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

private inline fun<T> SharedPreferences.delegate(
    key:String? = null,
    defaultValue:T,
    crossinline getter:SharedPreferences.(String,T)->T,
    crossinline setter:SharedPreferences.Editor.(String,T)->SharedPreferences.Editor
):ReadWriteProperty<Any,T> =
        object :ReadWriteProperty<Any,T>{
            override fun setValue(thisRef: Any, property: KProperty<*>, value: T){
                edit().setter(key?:property.name,value).apply()
            }

            override fun getValue(thisRef: Any, property: KProperty<*>): T =
                    getter(key?:property.name,defaultValue)
        }

fun SharedPreferences.int(key: String? = null,defaultValue: Int = 0) =
        delegate(key,defaultValue,SharedPreferences::getInt,SharedPreferences.Editor::putInt)

fun SharedPreferences.long(key: String? = null, defValue: Long = 0): ReadWriteProperty<Any, Long> {
    return delegate(key, defValue, SharedPreferences::getLong, SharedPreferences.Editor::putLong)
}
fun SharedPreferences.string(key: String? = null, defValue: String = "") =
    delegate(key, defValue, SharedPreferences::getStringNotNull, SharedPreferences.Editor::putString)


fun SharedPreferences.boolean(key: String? = null, defValue:Boolean = false): ReadWriteProperty<Any, Boolean> {
    return delegate(key, defValue, SharedPreferences::getBoolean, SharedPreferences.Editor::putBoolean)
}

fun SharedPreferences.getStringNotNull (key:String,defValue:String): String{
    return this.getString(key,defValue)?:""
}