package com.vlog.ui

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.common.base.BaseActivity
import com.common.ext.launch
import com.common.util.Util
import com.vlog.R
import com.vlog.connect.WsConnectionService
import com.vlog.databinding.ActivityMainBinding
import com.vlog.login.StartActivity
import com.vlog.room.RoomCreateActivity
import com.vlog.search.FindActivity
import com.vlog.ui.me.MeFragment
import com.vlog.ui.messageList.MessageListFragment
import com.vlog.ui.relation.RelationFragment
import com.vlog.user.Owner


class MainActivity :BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val adapter = PageAdapter(this, listOf(MessageListFragment(),RelationFragment(),MeFragment()))

    override var customerBar = true

    private var mMenu:Menu? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Util.setLightBar(this,Color.WHITE)
        verify()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)
        binding.page2Layout.adapter = adapter
        dispatchEvent()
    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        verify()
    }

    private fun dispatchEvent(){
        binding.bind()
        val ids = arrayListOf(R.id.message_list_layout,R.id.relation_layout,R.id.me_layout)
        val titles = arrayListOf("vlog","通讯录","我")
        binding.page2Layout.registerOnPageChangeCallback(object :ViewPager2.OnPageChangeCallback(){

            override fun onPageSelected(position: Int) {
               binding.bottomActionLayout.selectedItemId = ids[position]
                title = titles[position]
            }
        })
    }

    private fun ActivityMainBinding.bind(){
        val page = binding.page2Layout
        bottomActionLayout.setOnNavigationItemSelectedListener {item->
            when(item.itemId){
                R.id.message_list_layout-> page.currentItem = 0
                R.id.relation_layout->page.currentItem = 1
                R.id.me_layout->page.currentItem = 2
            }
            true
        }
    }

    private fun verify() {
        val owner = Owner()
        if (owner.userId <0) {
               launch<StartActivity>()
            finish()
        }else{
            if(owner.isLogout) {
                WsConnectionService.connect(this, Owner().userId)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.main, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search -> {
                launch<FindActivity>()
                return true
            }
            R.id.room_create->{
                launch<RoomCreateActivity>()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}