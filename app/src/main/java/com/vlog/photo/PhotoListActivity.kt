package com.vlog.photo

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.base.BaseActivity
import com.common.pushExecutors
import com.common.util.Util
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.databinding.ActivityPhotoListBinding
import dibus.app.PhotoListActivityCreator
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class PhotoListActivity : BaseActivity() {

    private val source = PhotoListSource()
    private lateinit var binding:ActivityPhotoListBinding
    private val data = HashMap<String,MutableList<MediaBean>>()
    private val allPhotos = ArrayList<MediaBean>()
    override var customerBar = true
    private var conversationId = -1
    private val allPhoto = "所有图片"
    private var fromType = Message.FROM_TYPE_FRIEND

    @AutoWire
    lateinit var msgDao: MsgDao

    private var animatorDuration = 500L

    private val showAni by lazy {
        ObjectAnimator.ofFloat(binding.isShowCard, "rotation", 0f, 180f).apply {
            duration = animatorDuration
        }
    }

   private val hideAni by lazy {
       ObjectAnimator.ofFloat(binding.isShowCard,"rotation",180f,0f).apply {
           duration = animatorDuration
       }
    }


    private val dAdapter = DirectoryAdapter(){
        pAdapter.apply {
            val originSize = mList.size
            mList.clear()
            val l = if(it == allPhoto){
                allPhotos
            }else{
                data[it]!!
            }
            Log.d("selectedChange",l.toString())
            mList.addAll(l)
            if(mList.size>originSize){
                notifyItemRangeChanged(0,originSize)
                notifyItemRangeInserted(originSize,mList.size-originSize)
            }else{
                notifyItemRangeChanged(0,mList.size)
                notifyItemRangeRemoved(mList.size,originSize-mList.size)
            }
            animatorDuration =200L
            hideDirectory()
            animatorDuration = 500L
        }
    }

    private val pAdapter = PhotoListAdapter()
    private val dList = ArrayList<DirectoryBean>()
    private val animator = RecyclerViewAnimator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setDarkBar(this,Color.parseColor("#ff333333"))
        PhotoListActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_list)
        init()
        dispatchEvent()
    }

    private fun init(){
        binding.photoList.layoutManager = GridLayoutManager(this,4)
        binding.photoList.adapter = pAdapter
        binding.directoryList.layoutManager = LinearLayoutManager(this)
        binding.directoryList.adapter = dAdapter
        animator.addDuration = animatorDuration
        animator.removeDuration = animatorDuration
        binding.directoryList.itemAnimator = animator
        conversationId = intent.getIntExtra("conversationId",-1)
        fromType = intent.getIntExtra("fromType",Message.FROM_TYPE_FRIEND)
    }



   private var isShow = false
    private  var directoryHeight = 0

    private fun dispatchEvent(){
        binding.selectCard.setOnClickListener {
            if(isShow){
               hideDirectory()
            }else{
               showDirectory()
            }
        }
        reqPermission {
            getAllPhotoInfo()
        }

        pAdapter.selectedNotEmptyListener = {
            if(it){
                binding.sendBt.apply {
                    setTextColor(Color.WHITE)
                    setBackgroundResource(R.drawable.yellow_rectangle_bg)
                }
            }else{
                binding.sendBt.apply {
                    setTextColor(resources.getColor(R.color.grey_text))
                    setBackgroundResource(R.drawable.grey43_retangle_bg)
                }
            }
        }

        binding.sendBt.setOnClickListener {
            val photos = pAdapter.selectSets
            if(photos.isEmpty() ||conversationId == -1){
                return@setOnClickListener
            }else{
                var time = Date().time
                val messages = ArrayList<Message>()
                for(b in photos){
                    messages.add(Message.obtain(conversationId,Message.MESSAGE_IMAGE,b.path,fromType,++time))
                }
                Log.d("list_",messages.toString())
                pushExecutors {
                    msgDao.insert(messages)
                    runOnUiThread{
                        onBackPressed()
                    }
                }
            }

        }
    }
    private fun hideDirectory(){
        hideAni.start()
        isShow = false
        binding.directoryList.apply {
            val p = layoutParams
            p.height = directoryHeight
            layoutParams = p
        }
        dAdapter.apply {
            val size = mList.size
            mList.clear()
            notifyItemRangeRemoved(0,size)
        }
        binding.directoryList.postDelayed({
            val p = binding.directoryList.layoutParams
            p.height = 0
            binding.directoryList.layoutParams = p
        },600)
    }

    private fun showDirectory(){
        binding.directoryList.visibility = View.VISIBLE
        isShow = true
        showAni.start()
        if(directoryHeight!= 0){
            binding.directoryList.apply {
                val p = layoutParams
                p.height = directoryHeight
                layoutParams = p
            }
        }
        dAdapter.apply {
            val originSize= mList.size
            mList.addAll(dList)
            notifyItemRangeInserted(originSize,mList.size)
        }
        directoryHeight = binding.directoryList.layoutParams.height
    }


    private fun getAllPhotoInfo() {
        source.getAllPhotoInfo(contentResolver,data){
            val it = data
            if(it.isEmpty())return@getAllPhotoInfo
            val directories = ArrayList<DirectoryBean>()
            for((key,value) in it){
                allPhotos.addAll(value)
                val dBean = DirectoryBean(value[0].path,key,value.size,false)
                directories.add(dBean)
            }
            val allBean = DirectoryBean(allPhotos[0].path,allPhoto,allPhotos.size,true)
            dList.add(allBean)
            dList.addAll(directories)
            pAdapter.mList.addAll(allPhotos)
            runOnUiThread{
                pAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object{

        fun launch(conversationId:Int,fromType:Int,context: Context){
            val intent = Intent(context,PhotoListActivity::class.java)
            intent.putExtra("conversationId",conversationId)
            intent.putExtra("fromType",fromType)
            context.startActivity(intent)
        }
    }

}