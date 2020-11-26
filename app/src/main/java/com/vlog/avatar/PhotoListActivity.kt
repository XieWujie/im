package com.vlog.avatar

import android.os.Bundle
import com.common.base.BaseActivity
import com.vlog.R

class PhotoListActivity : BaseActivity() {

    private val source = PhotoListSource()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_photo_list)
    }


    private fun getAllPhotoInfo() {
        source.getAllPhotoInfo(contentResolver){

        }
    }

}