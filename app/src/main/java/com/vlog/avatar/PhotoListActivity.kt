package com.vlog.avatar

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.base.BaseActivity
import com.common.util.Util
import com.vlog.R
import com.vlog.databinding.ActivityPhotoListBinding

class PhotoListActivity : BaseActivity() {

    private val source = PhotoListSource()
    private lateinit var binding:ActivityPhotoListBinding
    override var customerBar = true


    private val dAdapter = DirectoryAdapter()
    private val pAdapter = PhotoListAdapter()
    private val dList = ArrayList<DirectoryBean>()
    private val animator = RecyclerViewAnimator()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setDarkBar(this,Color.parseColor("#ff333333"))
        binding = DataBindingUtil.setContentView(this,R.layout.activity_photo_list)
        init()
        dispatchEvent()
    }

    private fun init(){
        binding.photoList.layoutManager = GridLayoutManager(this,4)
        binding.photoList.adapter = pAdapter
        binding.directoryList.layoutManager = LinearLayoutManager(this)
        binding.directoryList.adapter = dAdapter
        animator.addDuration = 600
        animator.removeDuration = 600
        binding.directoryList.itemAnimator = animator
    }



    private fun dispatchEvent(){
        var isShow = false
        val showAni = ObjectAnimator.ofFloat(binding.isShowCard,"rotation",180f,0f)
        showAni.duration = 600
        val hideAni = ObjectAnimator.ofFloat(binding.isShowCard,"rotation",0f,180f)
        hideAni.duration = 600
        var directoryHeight = 0
        binding.selectCard.setOnClickListener {
            if(isShow){
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
            }else{
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
        }
        checkPermission {
            getAllPhotoInfo()
        }
    }

    private fun getAllPhotoInfo() {
        source.getAllPhotoInfo(contentResolver){
            if(it.isEmpty())return@getAllPhotoInfo
            val allPhotos = ArrayList<MediaBean>()
            val directories = ArrayList<DirectoryBean>()
            for((key,value) in it){
                allPhotos.addAll(value)
                val dBean = DirectoryBean(value[0].path,key,value.size,false)
                directories.add(dBean)
            }
            val allBean = DirectoryBean(allPhotos[0].path,"所有图片",allPhotos.size,true)
            dList.add(allBean)
            dList.addAll(directories)
            pAdapter.mList.addAll(allPhotos)
            runOnUiThread{
                pAdapter.notifyDataSetChanged()
            }
        }
    }

    companion object{

        fun launch(int: Intent,context: Context){
            val intent = Intent(context,PhotoListActivity::class.java)
            intent.putExtra("intent",int)
            context.startActivity(intent)
        }
    }

}