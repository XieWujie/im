package com.common.base

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.common.Util
import com.common.ext.toast


open class BaseActivity :AppCompatActivity(){

    protected open var customerBar = false

    private var callback:(()->Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!customerBar){
            Util.setLightBar(this,Color.parseColor("#ffededed"))
        }
    }


    private var permission = arrayOf<String>(
         Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    // 声明一个集合，在后面的代码中用来存储用户拒绝授权的权
    private var mPermissionList: MutableList<String> = ArrayList()



    //授权服务
   protected fun checkPermission(callback:()->Unit) {
        this.callback = callback
        for (i in permission.indices) {
            if (ContextCompat.checkSelfPermission(this, permission[i]
                ) !== PackageManager.PERMISSION_GRANTED
            ) {
                mPermissionList.add(permission[i])
            }
        }
        if (mPermissionList.isEmpty()) { //未授予的权限为空，表示都授予了
           toast("已经授权")
            callback.invoke()
            agreeRequest()
        } else { //请求权限方法
            val permissions = mPermissionList.toTypedArray() //将List转为数组
            ActivityCompat.requestPermissions(this, permissions, 1)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
         permissions: Array<String>,
         grantResults: IntArray
    ) {
        if (requestCode == 1) {
            for (i in grantResults.indices) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) { //用户拒绝授权的权限
                    //判断是否勾选禁止后不再询问
                    val showRequestPermission: Boolean =
                        ActivityCompat.shouldShowRequestPermissionRationale(
                            this,
                            permissions[i]
                        )
                    if (showRequestPermission) {
                      toast("权限未申请")
                    }
                } else { //用户同意的权限
                   agreeRequest()
                    this.callback?.also {
                        it.invoke()
                        this.callback = null
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    open fun agreeRequest(){

    }
}