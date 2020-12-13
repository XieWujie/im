package com.vlog.conversation

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Editable
import android.text.Html
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.common.ext.afterTextChanged
import com.common.ext.animateEnd
import com.common.ext.setEmotionText
import com.common.pushExecutors
import com.common.util.Util
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.photo.PhotoListActivity
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.CiteEvent
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.databinding.BottomInputLayoutBinding
import com.vlog.photo.load
import dibus.app.InputBottomCreator

class InputBottom : FrameLayout {

    var softKeyHeight = 0

    @Volatile
    private var citeMessageId = -1

    private var showListener: ((Boolean) -> Unit)? = null
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

    fun setBottomContentShowListener(action: (Boolean) -> Unit) {
        showListener = action
    }

    fun setConversationId(id: Int) {
        this.conversationId = id
        binding.inputWrite.conversationId = id
    }

    private fun showMore(view: View): Animator {
        val layoutParams = view.layoutParams
        val height = if (softKeyHeight < 100) Util.dp2dx(context, 270).toInt() else softKeyHeight
        val animator = ValueAnimator.ofInt(0, height)
        animator.duration = 250
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.start()
        return animator
    }

    private fun hideAnim(view: View): Animator {
        val layoutParams = view.layoutParams
        val height = if (softKeyHeight < 100) Util.dp2dx(context, 270).toInt() else softKeyHeight
        val animator = ValueAnimator.ofInt(height, 0)
        animator.duration = 250
        animator.addUpdateListener {
            val value = it.animatedValue as Int
            layoutParams.height = value
            view.layoutParams = layoutParams
        }
        animator.start()
        return animator
    }


    private fun showWrite() {
        Util.hideSoftInput(context, this)
        showMore(binding.writeView).animateEnd {
            hide0(binding.emoContainer)
            hide0(binding.moreActionLayout)
            showListener?.invoke(true)
        }
    }

    private fun showMoreLayout() {
        Util.hideSoftInput(context, this)
        showMore(binding.moreActionLayout).animateEnd {
            hide0(binding.emoContainer)
            hide0(binding.writeView)
            showListener?.invoke(true)
        }
    }

    fun showSoftKey() {
        hide0(binding.emoContainer)
        hide0(binding.writeView)
        hide0(binding.moreActionLayout)
        Util.showSoftInput(binding.inputText)
    }

    fun hide() {
        if (binding.writeView.height > 100) {
            hideAnim(binding.writeView)
        }
        if (binding.moreActionLayout.height > 100) {
            hideAnim(binding.moreActionLayout)
        }
        Util.hideSoftInput(context, this)
    }

    private fun showEmo() {
        Util.hideSoftInput(context, this)
        showMore(binding.emoContainer).animateEnd {
            hide0(binding.moreActionLayout)
            hide0(binding.writeView)
            showListener?.invoke(true)
        }
    }

    @BusEvent
    fun emoAddEvent(event: EmoAddEvent){
        val newText = "${binding.inputText.text.toString()}[${event.source}]"
        binding.inputText.setEmotionText(newText)
    }

    private fun hide0(view: View) {
        val p = view.layoutParams.also { it.height = 0 }
        view.layoutParams = p
    }

    fun event() {
        binding.icWrite.setOnClickListener { it ->

            it.isSelected = if (it.isSelected) {
                val p = binding.writeView.layoutParams.also { it.height = 0 }
                binding.writeView.layoutParams = p
                binding.inputWrite.visibility = GONE
                binding.inputText.visibility = VISIBLE
                binding.actionBt.isSelected = binding.inputText.text.isNotEmpty()
                showSoftKey()
                false
            } else {
                binding.actionBt.isSelected = !binding.inputWrite.isEmpty()
                showWrite()
                binding.inputWrite.visibility = VISIBLE
                binding.inputText.visibility = GONE
                true
            }
        }

        binding.icEmo.setOnClickListener {
            showEmo()
        }



        binding.inputText.afterTextChanged {
            binding.actionBt.isSelected = it.isNotEmpty()
        }
        binding.actionBt.setOnClickListener {
            if (it.isSelected) {
                if (binding.icWrite.isSelected) {
                    binding.inputWrite.sendWordCache(fromType, citeMessageId)
                } else {
                    val content = binding.inputText.text.toString()
                    val msg =
                        Message.obtain(conversationId, Message.MESSAGE_TEXT, content, fromType)
                    msg.citeMessageId = citeMessageId
                    pushExecutors {
                        msgDao.insert(msg)
                    }
                    binding.inputText.setText("")
                }
                citeMessageId = -1
                binding.citeCard.visibility = GONE
            } else {
                if (binding.moreActionLayout.height > 100) {
                    showSoftKey()
                } else {
                    showMoreLayout()
                }
            }
        }

        binding.photoLayout.setOnClickListener {
            PhotoListActivity.launch(conversationId, fromType, it.context)
        }
    }

    @BusEvent
    fun citeEvent(citeEvent: CiteEvent) {
        citeMessageId = citeEvent.message.messageId
        binding.citeCard.visibility = View.VISIBLE
        val r = when (citeEvent.message.messageType) {
            Message.MESSAGE_IMAGE -> {
                binding.citePhoto.visibility = View.VISIBLE
                binding.wordListLayout.visibility = View.GONE
                binding.citePhoto.load(citeEvent.message.content)
                "[图片]"
            }
            Message.MESSAGE_TEXT -> {
                binding.citePhoto.visibility = View.GONE
                binding.wordListLayout.visibility = View.GONE
                citeEvent.message.content
            }
            Message.MESSAGE_WRITE -> {
                binding.citePhoto.visibility = View.GONE
                binding.wordListLayout.visibility = View.VISIBLE
                binding.wordListLayout.handleWrite(citeEvent.message.content)
                "[手写]"
            }
            else -> return
        }
        val content = "${citeEvent.from}:$r"
        binding.citeText.text = content
        binding.citeDismissCard.setOnClickListener {
            binding.citeText.clearComposingText()
            binding.citeCard.visibility = View.GONE
            citeMessageId = -1
        }
    }

    @BusEvent
    fun actionEvent(wordCacheState: WordCacheState) {
        binding.actionBt.isSelected = !wordCacheState.isEmpty
    }


}