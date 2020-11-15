package com.vlog.ui.relation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.common.Result
import com.common.ext.toast
import com.dibus.AutoWire
import com.vlog.R
import com.vlog.verify.list.VerifyListActivity
import dibus.app.RelationFragmentCreator

class RelationFragment :Fragment(){

    lateinit var recyclerView: RecyclerView
    private lateinit var newFLayout:RelativeLayout
    private val adapter = RelationListAdapter()

    @AutoWire
    lateinit var viewModel: RelationViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        RelationFragmentCreator.inject(this)
        val view = inflater.inflate(R.layout.fragment_ralation_layout,container,false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        newFLayout = view.findViewById(R.id.new_friend_layout)
        event()
        return view
    }

    fun event(){
        newFLayout.setOnClickListener {
            VerifyListActivity.launch(it.context)
        }
        viewModel.getRelations().observe(viewLifecycleOwner){
            when(it){
                is Result.Error->context?.toast(it.toString())
                is Result.Data->adapter.setList(it.data)
            }
        }
    }

}