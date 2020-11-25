package com.vlog.ui.relation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.R
import com.vlog.database.Verify
import com.vlog.verify.list.VerifyListActivity
import dibus.app.RelationFragmentCreator

class RelationFragment :Fragment(){

    lateinit var recyclerView: RecyclerView
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
        event()
        return view
    }

    override fun onStart() {
        super.onStart()
        fleshList()
    }

    @BusEvent
    fun newFriendEvent(verify:Verify){

    }

    private fun fleshList(){
        viewModel.getRelations().observe(viewLifecycleOwner){

        }
    }

    private fun event(){
        viewModel.friendListen().observe(viewLifecycleOwner){
            adapter.setList(it)
        }
    }

}