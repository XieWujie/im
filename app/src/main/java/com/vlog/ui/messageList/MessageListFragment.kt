package com.vlog.ui.messageList

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dibus.AutoWire
import com.vlog.conversation.ConversationSource
import com.vlog.databinding.FragmentMessageListBinding
import com.vlog.user.Owner
import dibus.app.MessageListFragmentCreator


class MessageListFragment : Fragment() {

    private lateinit var binding:FragmentMessageListBinding

    @AutoWire
    lateinit var source:ConversationSource
    private val mAdapter = MessageListAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMessageListBinding.inflate(inflater,container,false)
        MessageListFragmentCreator.inject(this)
        dispatch()
        return binding.root
    }

    private fun dispatch(){
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = mAdapter
        }

        binding.refreshLayout.setOnRefreshListener {
            refresh()
        }

        source.getRecentMessage().observe(viewLifecycleOwner){
            mAdapter.refreshList(it)
        }
        refresh()
        val itemTouchHelper = ItemTouchHelper(object :ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT or  ItemTouchHelper.RIGHT
        ){
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                mAdapter.removeItem(viewHolder.adapterPosition)

            }

        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun refresh(){
        source.getRecentMessageByNet(Owner().userId).observe(viewLifecycleOwner){
            binding.refreshLayout.isRefreshing = false
        }
    }
    

}