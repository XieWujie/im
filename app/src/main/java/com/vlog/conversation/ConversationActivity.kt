package com.vlog.conversation

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.base.BaseActivity
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.adapter.MessageListAdapter
import com.vlog.databinding.ActivityConversationBinding
import com.vlog.database.User
import dibus.app.ConversationActivityCreator
import kotlinx.android.synthetic.main.activity_verify_list.*

class ConversationActivity :BaseActivity() {

    private lateinit var binding: ActivityConversationBinding
    private var isLoading = false

    @AutoWire
    lateinit var adapter: MessageListAdapter

    @AutoWire
    lateinit var viewModel: ConversationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ConversationActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversation)
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        adapter.notifyDataSetChanged()
        init()
    }

    private fun init(){
        val user = intent.getParcelableExtra<User>("user")
        val conversationId = intent.getIntExtra("conversationId",-1)
        if(conversationId != -1){
            binding.bottomInputLayout.setConversationId(conversationId)
        }
        dispatchEvent(conversationId)
    }

   private fun dispatchEvent(conversationId: Int){
        binding.recyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if(!isLoading && !recyclerView.canScrollVertically(-1)){
                    isLoading = true
                    viewModel.query(adapter.getFirstItemBefore(),conversationId).observe(this@ConversationActivity){
                        isLoading = false
                    }

                }
            }
        })
        viewModel.queryMessage(conversationId).observe(this){
            adapter.flashList(it)
        }
       adapter.registerAdapterDataObserver(object :RecyclerView.AdapterDataObserver(){
           override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
               if(positionStart>0){
                   binding.recyclerView.scrollToPosition(adapter.itemCount-1)
               }
           }
       })
    }

    companion object{

        fun launch(context: Context, user: User, conversationId:Int){
            val intent = Intent(context,ConversationActivity::class.java)
            intent.putExtra("conversationId",conversationId)
            intent.putExtra("user",user)
            context.startActivity(intent)
        }
    }
}