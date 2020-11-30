package com.vlog.photo

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import com.common.pushExecutors
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PhotoListSource {



     fun getAllPhotoInfo(contentResolver: ContentResolver, allPhotosTemp: HashMap<String, MutableList<MediaBean>>,callback:()->Unit) {
        pushExecutors{
            val mediaBeen: MutableList<MediaBean> = ArrayList()
            val mImageUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val projImage = arrayOf<String>(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME
            )
            val mCursor: Cursor? = contentResolver.query(
                mImageUri,
                projImage,
                MediaStore.Images.Media.MIME_TYPE.toString() + "=? or " + MediaStore.Images.Media.MIME_TYPE + "=?",
                arrayOf("image/jpeg", "image/png"),
                MediaStore.Images.Media.DATE_MODIFIED.toString() + " desc"
            )
            if (mCursor != null) {
                while (mCursor.moveToNext()) {
                    // 获取图片的路径
                    val path: String =
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA))
                    val size: Int =
                        mCursor.getInt(mCursor.getColumnIndex(MediaStore.Images.Media.SIZE)) / 1024
                    val displayName: String =
                        mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME))
                    //用于展示相册初始化界面
                    mediaBeen.add(MediaBean(path, size))
                    // 获取该图片的父路径名
                    val dirPath: String = File(path).getParentFile().absolutePath
                    //存储对应关系
                    if (allPhotosTemp.containsKey(dirPath)) {
                        val data: MutableList<MediaBean> = allPhotosTemp[dirPath]!!
                        data.add(MediaBean( path, size))
                        continue
                    } else {
                        val data: MutableList<MediaBean> = ArrayList()
                        data.add(MediaBean(path, size))
                        allPhotosTemp[dirPath] = data
                    }
                }
                mCursor.close()
            }
            callback()
        }
    }
}

data class MediaBean(val path: String,val size:Int,var selected:Boolean = false){
    override fun equals(other: Any?): Boolean {
        return if(other is MediaBean){
            Objects.equals(path,other.path)
        }else{
            false
        }
    }

    override fun hashCode(): Int {
        return path.hashCode()
    }
}