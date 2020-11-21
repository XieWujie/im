package com.vlog.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import com.common.base.BaseActivity
import com.common.ext.launch
import com.vlog.user.Owner
import com.vlog.R
import com.vlog.databinding.ActivityMainBinding
import com.vlog.login.LoginActivity
import com.vlog.room.RoomCreateActivity
import com.vlog.search.FindActivity
import com.vlog.ui.relation.RelationFragment
import dibus.app.WsListenerCreator

class MainActivity :BaseActivity() {
    private lateinit var binding: ActivityMainBinding

    private val adapter = PageAdapter(this, listOf(RelationFragment()))


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verify()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setSupportActionBar(binding.mainToolbar)
        binding.page2Layout.adapter = adapter
    }

    private fun verify() {
        if (Owner().userId == -1) {
               launch<LoginActivity>()
        }else{
            WsListenerCreator.get().connect()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
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