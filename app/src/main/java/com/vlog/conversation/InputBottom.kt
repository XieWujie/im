package com.vlog.conversation

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.common.Util
import com.dibus.BusEvent
import com.dibus.DiBus
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.user.Owner
import com.vlog.connect.MessageSend
import com.vlog.databinding.BottomInputLayoutBinding
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.User
import dibus.app.InputBottomCreator

class InputBottom : FrameLayout, TextWatcher {

    private var binding: BottomInputLayoutBinding =
        BottomInputLayoutBinding.inflate(LayoutInflater.from(context), this, true)

    private var conversationId = -1

    private lateinit var user: User


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)


    init {
        val owner = Owner()
        user = User(owner.userId,owner.username,owner.username,owner.description)
        event()
        InputBottomCreator.inject(this)
    }

    fun setConversationId(id:Int){
        this.conversationId = id
        binding.inputWrite.conversationId = id
    }

    fun event() {
        binding.icWrite.setOnClickListener {
            it.isSelected = if (it.isSelected) {
                binding.writeView.visibility = GONE
                binding.inputWrite.visibility = GONE
                binding.inputText.visibility = VISIBLE
                Util.showSoftInput(binding.inputText)
                false
            } else {
                binding.writeView.visibility = VISIBLE
                binding.inputWrite.visibility = VISIBLE
                binding.inputText.visibility = GONE
                Util.hideSoftInput(context,this)
                true
            }
        }

        binding.inputText.addTextChangedListener(this)
        binding.actionSend.setOnClickListener {
            if(binding.icWrite.isSelected){
                binding.inputWrite.sendWordCache(user)
            }else{
                val content = binding.inputText.text.toString()
                val message = Message(0, Owner().userId,conversationId, Message.MESSAGE_TEXT,content,0)
                DiBus.postEvent(message,MessageSend{

                })
                binding.inputText.setText("")
            }
        }

    }

    @BusEvent
    fun actionEvent(wordCacheState: WordCacheState){
        if(wordCacheState.isEmpty){
            binding.icMore.visibility = VISIBLE
            binding.actionSend.visibility = GONE
        }else{
            binding.icMore.visibility = GONE
            binding.actionSend.visibility = VISIBLE
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }

    override fun afterTextChanged(s: Editable) {
        if (s.isEmpty()) {
            binding.icMore.visibility = VISIBLE
            binding.actionSend.visibility = GONE
        } else {
            binding.icMore.visibility = GONE
            binding.actionSend.visibility = VISIBLE
        }
    }


}