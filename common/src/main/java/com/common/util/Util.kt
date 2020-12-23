package com.common.util

import android.app.Activity
import android.app.Service
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import java.text.SimpleDateFormat
import java.util.*

object Util {

    fun dp2dx(context: Context, dp: Int):Double{
       val density =  context.resources.displayMetrics.density
        return (dp+0.5)*density
    }

    fun getTime(timeMill: Long):String{
        val oneDate = 24*60*60*1000
        val lastDate = Date().time-oneDate
        val p = if(timeMill>lastDate){
            "HH:mm"
        }else{
            "MM-dd HH:mm"
        }
        val format = SimpleDateFormat(p, Locale.CHINA).format(timeMill)
        return format.toString()
    }

    /**
     * 隐藏键盘
     *
     * @param context
     * @param view
     */
    fun hideSoftInput(context: Context, view: View?) {
        if (view != null) {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun clipText(context: Context, text: String){
        val m = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        m.setPrimaryClip(ClipData.newPlainText("lineMe", text))
    }

    fun showSoftInput(editText: EditText){
        editText.requestFocus()
        val imm = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(editText, 0)
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        if (contentUri.scheme == "file") {
            return contentUri.encodedPath
        } else {
            var cursor: Cursor? = null
            return try {
                val pro = arrayOf(MediaStore.Images.Media.DATA)
                cursor = context.contentResolver.query(contentUri, pro, null, null, null)
                if (null != cursor) {
                    val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    cursor.moveToFirst()
                    cursor.getString(column_index)
                } else {
                    ""
                }
            } finally {
                cursor?.close()
            }
        }
    }

    fun starVibrate(context: Context, pattern: LongArray?, isRepeat: Boolean = false) {
        val vib: Vibrator = context.getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        vib.vibrate(pattern, if (isRepeat) 1 else -1)
    }

    fun setLightBar(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =  window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = color
        }
    }

    fun setDarkBar(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= 21) {
            val window = activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =  window.decorView.systemUiVisibility and  View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inc()
            window.statusBarColor = color
        }
    }

    fun setFullScreen(activity: Activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window= activity.window
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.decorView
                .setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility =  window.decorView.systemUiVisibility or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
        }
    }
}