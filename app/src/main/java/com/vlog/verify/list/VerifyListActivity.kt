package com.vlog.verify.list

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.LinearLayoutManager
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.databinding.ActivityVerifyListBinding
import dibus.app.VerifyListActivityCreator

class VerifyListActivity : BaseActivity() {
    private lateinit var binding: ActivityVerifyListBinding

    private val adapter = VerifyListAdapter()

    @AutoWire
    lateinit var viewModel: VerifyListViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        VerifyListActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_verify_list)
        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        viewModel.getVerifyList().observe(this) {
            when(it){
                is Result.Error->toast(it.toString())
                is Result.Data->adapter.setList(it.data)
            }
        }
    }

    companion object{
        fun launch(context: Context){
            val intent = Intent(context,VerifyListActivity::class.java)
            context.startActivity(intent)
        }
    }
}