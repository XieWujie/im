package com.vlog.conversation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.common.ext.animateEnd
import com.common.pushExecutors
import com.common.util.Util
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.photo.PhotoListActivity
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.databinding.BottomInputLayoutBinding
import dibus.app.InputBottomCreator

class InputBottom : FrameLayout, TextWatcher {

     var softKeyHeight = 0

    private var showListener:((Boolean)->Unit)? = null
    var fromType = Message.FROM_TYPE_FRIEND

    @AutoWire
    lateinit var msgDao: MsgDao


    private var binding: BottomInputLayoutBinding =
        BottomInputLayoutBinding.inflate(LayoutInflater.from(context), this, true)


    private var conversationId = -1


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)


    init {
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

    private fun showMore(view: View):Animator{
        val layoutParams = view.layoutParams
        val height = if(softKeyHeight<100) Util.dp2dx(context,270).toInt() else softKeyHeight
        val animator = ValueAnimator.ofInt(0, height)
        animator.duration = 250
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.height = value
            view.layoutParams= layoutParams
        }
        animator.start()
        return animator
    }

    private fun hideAnim(view: View):Animator{
        val layoutParams = view.layoutParams
        val height = if(softKeyHeight<100) Util.dp2dx(context,270).toInt() else softKeyHeight
        val animator = ValueAnimator.ofInt(height, 0)
        animator.duration = 250
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.height = value
            view.layoutParams= layoutParams
        }
        animator.start()
        return animator
    }



    private fun showWrite(){
        Util.hideSoftInput(context, this)
        showMore(binding.writeView).animateEnd {
            val p = binding.moreActionLayout.layoutParams.also { it.height = 0 }
            binding.moreActionLayout.layoutParams = p
            showListener?.invoke(true)
        }
    }

    private fun showMoreLayout(){
        Util.hideSoftInput(context, this)
        showMore(binding.moreActionLayout).animateEnd {
            val p = binding.writeView.layoutParams.also { it.height = 0 }
            binding.writeView.layoutParams= p
            showListener?.invoke(true)
        }
    }

     fun showSoftKey(){
        val p = binding.moreActionLayout.layoutParams.also { it.height = 0 }
        binding.moreActionLayout.layoutParams = p
        val pu = binding.writeView.layoutParams.also { it.height = 0 }
        binding.writeView.layoutParams= pu
        Util.showSoftInput(binding.inputText)
    }
    fun hide(){
        if(binding.writeView.height>100){
            hideAnim(binding.writeView)
        }
        if(binding.moreActionLayout.height>100){
            hideAnim(binding.moreActionLayout)
        }
        Util.hideSoftInput(context,this)
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
                binding.inputWrite.sendWordCache(fromType)
            }else{
                val content = binding.inputText.text.toString()
                val msg = Message.obtain(conversationId,Message.MESSAGE_TEXT,content,fromType)
                pushExecutors {
                    msgDao.insert(msg)
                }
                binding.inputText.setText("")
            }
        }
        binding.icMore.setOnClickListener {
            showMoreLayout()
        }

        binding.photoLayout.setOnClickListener {
            PhotoListActivity.launch(conversationId,fromType,it.context)
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