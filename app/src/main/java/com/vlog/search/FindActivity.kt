package com.vlog.search

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.Result
import com.common.base.BaseActivity
import com.common.ext.afterTextChanged
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.databinding.ActivityFindBinding
import dibus.app.FindActivityCreator

class FindActivity : BaseActivity() {

    private lateinit var binding:ActivityFindBinding
    
    @AutoWire
    lateinit var viewModel: FindViewModel
    private val adapter = FindAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FindActivityCreator.inject(this)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_find)
        recyclerViewInit(binding.resultList)
        eventDispatch()
    }

    private fun recyclerViewInit(recyclerView: RecyclerView){
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun eventDispatch(){
        binding.inputEdit.setOnFocusChangeListener { _, hasFocus ->
            if(hasFocus){
                binding.outLayout.transitionToEnd()
            }else{
                binding.outLayout.transitionToStart()
            }
        }
        binding.inputEdit.afterTextChanged { key ->
            if(key.isNotEmpty())
            viewModel.findUser(key).observe(this, Observer {
                when(it){
                    is Result.Error->toast(it.toString())
                    is Result.Data->{
                        adapter.setList(it.data)
                    }
                }
            })
        }
    }
}