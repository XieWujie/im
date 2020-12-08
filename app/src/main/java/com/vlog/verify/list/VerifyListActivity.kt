package com.vlog.verify.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.launch
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.database.VerifyWithUser
import com.vlog.databinding.ActivityVerifyListBinding
import com.vlog.search.FindActivity
import dibus.app.VerifyListActivityCreator

class VerifyListActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyListBinding



    @AutoWire
    lateinit var viewModel: VerifyListViewModel

    private val mList = ArrayList<VerifyWithUser>()

    val adapter = VerifyListAdapter(mList)



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VerifyListActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify_list)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        binding.loadBar.visibility = View.VISIBLE
        viewModel.getVerifyList().observe(this) {
            binding.loadBar.visibility = View.GONE
        }
        viewModel.obsVerifyList().observe(this){
            mList.clear()
            mList.addAll(it)
            adapter.notifyDataSetChanged()
        }
        binding.comeBackView.setOnClickListener {
            onBackPressed()
        }

        adapter.registerAgreeAction { verify, position ->
            binding.loadBar.visibility = View.VISIBLE
            viewModel.sendVerify(verify).observe(this) {
                binding.loadBar.visibility = View.GONE
                when (it) {
                    is Result.Error -> toast(it.toString())
                    is Result.Data -> {
                        mList[position] = verify
                        adapter.notifyItemChanged(position)
                    }
                }
            }
        }

        binding.newFriendText.setOnClickListener {
            launch<FindActivity>()
        }
    }


    companion object{
        fun launch(context: Context){
            val intent = Intent(context,VerifyListActivity::class.java)
            context.startActivity(intent)
        }
    }
}