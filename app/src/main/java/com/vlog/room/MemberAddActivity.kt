package com.vlog.room

import android.content.Context
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.Result
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.FriendDao
import com.vlog.database.User
import com.vlog.databinding.ActivityMemberAddBinding
import com.vlog.user.Owner
import dibus.app.MemberAddActivityCreator

class MemberAddActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMemberAddBinding

    private val mAdapter = RoomMemberSelectAdapter()

    @AutoWire
    lateinit var friendDao: FriendDao

    @AutoWire
    lateinit var roomSource: RoomSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = DataBindingUtil.setContentView(this,R.layout.activity_member_add)
        MemberAddActivityCreator.inject(this)
        val list = intent.getParcelableArrayListExtra<User>("list")
        val conversationId = intent.getIntExtra("conversationId",-1)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MemberAddActivity)
            adapter = mAdapter
        }
        dispatchEvent(list?.toList()?: emptyList(),conversationId)
    }

    fun dispatchEvent(list: List<User>,conversationId: Int){
        Log.d("memberAdd:","origin:$list")
        friendDao.getFriend(Owner().userId).observe(this){mList->
            Log.d("memberAdd:","friend:$mList")
            val newList =  mList.map { it.user }.toMutableList()
            newList.removeAll(list)
            Log.d("memberAdd:","new:$newList")
            mAdapter.setList(newList.toList())
        }
        mAdapter.checkListStateListener = {
            if(it){
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.yellow_rectangle_bg)
                    setTextColor(Color.WHITE)
                }
            }else{
                binding.submitBt.apply {
                    setBackgroundResource(R.drawable.grey_bt_rectangle)
                    setTextColor(Color.parseColor("#dadada"))
                }
            }
        }

        binding.submitBt.setOnClickListener {
            if(mAdapter.checkList.isEmpty())return@setOnClickListener
            roomSource.addUser(conversationId,mAdapter.checkList.toList()).observe(this){
                when(it){
                    is Result.Error->toast(it.toString())
                    is Result.Data->{
                        onBackPressed()
                    }
                }
            }
        }
        binding.icBackView.setOnClickListener {
            onBackPressed()
        }
    }


    companion object{

        fun launch(conversationId:Int,memberExit:ArrayList<User>,context: Context){
            val intent = Intent(context,MemberAddActivity::class.java)
            intent.putParcelableArrayListExtra("list",memberExit)
            intent.putExtra("conversationId",conversationId)
            context.startActivity(intent)
        }
    }
}