package com.vlog.ui

import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.databinding.DataBindingUtil
import com.common.util.Util
import com.common.base.BaseActivity
import com.common.ext.launch
import com.vlog.R
import com.vlog.databinding.ActivityMainBinding
import com.vlog.login.StartActivity
import com.vlog.room.RoomCreateActivity
import com.vlog.search.FindActivity
import com.vlog.ui.me.MeFragment
import com.vlog.ui.messageList.MessageListFragment
import com.vlog.ui.relation.RelationFragment
import com.vlog.user.Owner
import dibus.app.WsListenerCreator

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

    private fun dispatchEvent(){
        binding.bind()
    }

    private fun ActivityMainBinding.bind(){
        messageListLayout.setOnClickListener {
            page2Layout.currentItem = 0
            title = "vlog"
            mMenu?.findItem(R.id.action_add)?.isVisible = true
        }
        relationLayout.setOnClickListener {
            page2Layout.currentItem = 1
            title = "通讯录"
            mMenu?.findItem(R.id.action_add)?.isVisible = true
        }
        meLayout.setOnClickListener {
            page2Layout.currentItem = 2
            title = "我"
            mMenu?.findItem(R.id.action_add)?.isVisible = false
        }
    }

    private fun verify() {
        if (Owner().userId == 0) {
               launch<StartActivity>()
        }else{
            WsListenerCreator.get().connect()
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