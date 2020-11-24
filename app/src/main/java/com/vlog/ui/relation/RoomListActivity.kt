package com.vlog.ui.relation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.toast
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.R
import com.vlog.database.Room
import com.vlog.databinding.ActivityRoomListBinding
import com.vlog.user.Owner
import dibus.app.RoomListActivityCreator

class RoomListActivity :BaseActivity() {

    private lateinit var binding:ActivityRoomListBinding

    private var listInValidate = false

    @AutoWire
    lateinit var source: RoomListSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RoomListActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_room_list)
        binding.backLayout.setOnClickListener {
            onBackPressed()
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val adapter = RoomListAdapter()
        binding.recyclerView.adapter = adapter
        source.obsRooms(Owner().userId).observe(this){
            adapter.setList(it)
        }
        flashList()
    }

    private fun flashList(){
        source.requestFromNet(Owner().userId).observe(this){
            when(it){
                is Result.Error->toast(it.toString())
            }
        }
    }

    override fun onStart() {
        super.onStart()
        if(listInValidate){
            flashList()
            listInValidate = false
        }
    }

    @BusEvent
    fun roomUpdateEvent(room:Room){
        listInValidate = true
    }
}