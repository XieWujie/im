package com.vlog.conversation

import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.common.util.Util
import com.dibus.BusEvent
import com.dibus.DiBus
import com.vlog.photo.PhotoListActivity
import com.vlog.connect.MessageSend
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.database.User
import com.vlog.databinding.BottomInputLayoutBinding
import com.vlog.user.Owner
import dibus.app.InputBottomCreator
import java.util.*

class InputBottom : FrameLayout, TextWatcher {

     var softKeyHeight = 0

    private var showListener:((Boolean)->Unit)? = null


    private var binding: BottomInputLayoutBinding =
        BottomInputLayoutBinding.inflate(LayoutInflater.from(context), this, true)


    private var conversationId = -1

    private var user: User


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)


    init {
        val owner = Owner()
        user = User(owner.userId, owner.username, owner.username, owner.description)
        event()
        InputBottomCreator.inject(this)
    }

    fun setBottomContentShowListener(action:(Boolean)->Unit){
        showListener = action
    }

    fun setConversationId(id: Int){
        this.conversationId = id
        binding.inputWrite.conversationId = id
    }

    private fun showMore(view: View){
        val layoutParams = view.layoutParams
        val height = if(softKeyHeight<100) Util.dp2dx(context,270).toInt() else softKeyHeight
        val animator = ValueAnimator.ofInt(0, height)
        animator.duration = 250
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.height = value
            view.layoutParams= layoutParams
            if(value == height){
                showListener?.invoke(true)
            }
        }
        animator.start()
    }


    private fun showWrite(){
        val p = binding.moreActionLayout.layoutParams.also { it.height = 0 }
        binding.moreActionLayout.layoutParams = p
        Util.hideSoftInput(context, this)
        showMore(binding.writeView)
    }

    private fun showMoreLayout(){
        val p = binding.writeView.layoutParams.also { it.height = 0 }
        binding.writeView.layoutParams= p
        Util.hideSoftInput(context, this)
        showMore(binding.moreActionLayout)
    }

     fun showSoftKey(){
        val p = binding.moreActionLayout.layoutParams.also { it.height = 0 }
        binding.moreActionLayout.layoutParams = p
        val pu = binding.writeView.layoutParams.also { it.height = 0 }
        binding.writeView.layoutParams= pu
        Util.showSoftInput(binding.inputText)
    }

    fun event() {
        binding.icWrite.setOnClickListener {
            it.isSelected = if (it.isSelected) {
                val p = binding.writeView.layoutParams.also { it.height = 0 }
                binding.writeView.layoutParams= p
                binding.inputWrite.visibility = GONE
                binding.inputText.visibility = VISIBLE
                showSoftKey()
                false
            } else {
                showWrite()
                binding.inputWrite.visibility = VISIBLE
                binding.inputText.visibility = GONE
                true
            }
        }


        binding.inputText.addTextChangedListener(this)
        binding.actionSend.setOnClickListener {
            if(binding.icWrite.isSelected){
                binding.inputWrite.sendWordCache(user)
            }else{
                val content = binding.inputText.text.toString()
                val message = Message(
                    0,
                    Owner().userId,
                    conversationId,
                    Message.MESSAGE_TEXT,
                    content,
                    0
                )
                DiBus.postEvent(message, MessageSend {

                })
                DiBus.postEvent(MsgWithUser(message, user))
                binding.inputText.setText("")
            }
        }
        binding.icMore.setOnClickListener {
            showMoreLayout()
        }

        binding.photoLayout.setOnClickListener {
            val intent = Intent(it.context,ConversationActivity::class.java)
            PhotoListActivity.launch(intent,it.context)
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