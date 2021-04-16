package com.vlog.conversation

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import com.common.ext.afterTextChanged
import com.common.ext.animateEnd
import com.common.ext.setEmotionText
import com.common.pushExecutors
import com.common.util.Util
import com.dibus.AutoWire
import com.dibus.BusEvent
import com.vlog.conversation.record.RecordHelper
import com.vlog.conversation.writeMessage.event.WordCacheState
import com.vlog.database.CiteEvent
import com.vlog.database.Message
import com.vlog.database.MsgDao
import com.vlog.databinding.BottomInputLayoutBinding
import com.vlog.photo.PhotoListActivity
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

    var micPermission:((callback:()->Unit)->Unit)? = null
    private var micCanUser = false

    var phoneCallListener:(()->Unit)? = null
    var videoCallListener:(()->Unit)? = null


    private var binding: BottomInputLayoutBinding =
        BottomInputLayoutBinding.inflate(LayoutInflater.from(context), this, true)


    private var conversationId = -1


    constructor(context: Context) : super(context)

    constructor(context: Context, attr: AttributeSet) : super(context, attr)


    init {
        InputBottomCreator.inject(this)
        binding.inputText.post {
            binding.recordDesText.layoutParams.apply {
                height = binding.inputText.height
                binding.recordDesText.layoutParams = this
            }
            binding.inputWrite.layoutParams.apply {
               height = binding.inputText.height
                binding.inputWrite.layoutParams = this
            }
        }
        event()
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
        binding.inputText.visibility = View.GONE
        binding.inputWrite.visibility = VISIBLE
        binding.icEmo.visibility = GONE
        binding.actionBt.isSelected = !binding.inputWrite.isEmpty()
        binding.recordDesText.visibility = GONE
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
            binding.inputWrite.visibility = GONE
            it.isSelected = if (it.isSelected) {
                binding.inputText.visibility = VISIBLE
                binding.recordDesText.visibility = GONE
                binding.actionBt.isSelected = binding.inputText.text.isNotEmpty()
                binding.icEmo.visibility = VISIBLE
                binding.inputWrite.visibility = GONE
                showSoftKey()
                false
            } else {
                binding.actionBt.isSelected = false
                binding.recordDesText.visibility = VISIBLE
                binding.recordDesText.text = "按住 说话"
                binding.inputText.visibility = GONE
                binding.icEmo.visibility = GONE
                hide()
                true
            }
        }

        binding.icEmo.setOnClickListener {
            if(binding.emoContainer.height<50){
                showEmo()
            }else{
                showSoftKey()
            }
        }



        binding.inputText.afterTextChanged {
            binding.actionBt.isSelected = it.isNotEmpty()
        }
        binding.actionBt.setOnClickListener {
            if (it.isSelected) {
                if (binding.inputWrite.visibility == VISIBLE) {
                    binding.inputWrite.sendWordCache(fromType, citeMessageId)
                } else if(binding.inputText.visibility == VISIBLE){
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

        binding.writeLayout.setOnClickListener {
            showWrite()
        }

        recordEvent()

        binding.voicePhotoLayout.setOnClickListener {
           phoneCallListener?.invoke()
        }
        binding.videoPhotoLayout.setOnClickListener {
            videoCallListener?.invoke()
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private fun recordEvent(){
        val rect = Rect()
        binding.inputEditCard.getHitRect(rect)
        val recorder = RecordHelper(context)
        binding.recordDesText.setOnTouchListener { _, event ->
            val x = event.rawX.toInt()
            val y = event.rawY.toInt()
            binding.inputEditCard.getGlobalVisibleRect(rect)
            when(event.action){
                MotionEvent.ACTION_DOWN->{
                    if(rect.contains(x,y)){
                        if(!micCanUser){
                            micPermission?.invoke {
                                micCanUser = true
                            }
                        }
                        if(!micCanUser){
                            return@setOnTouchListener false
                        }
                        Util.starVibrate(context, longArrayOf(0,60))
                        binding.recordDesText.text = "正在录音..."
                        recorder.createRecord()
                        recorder.start()
                    }else{
                        return@setOnTouchListener false
                    }
                }
                MotionEvent.ACTION_MOVE->{
                    if(rect.contains(x,y)){
                        binding.recordDesText.text = "正在录音..."
                        recorder.start()
                    }else{
                        binding.recordDesText.text = "松开 结束"
                        recorder.pauseRecord()
                    }
                }
                MotionEvent.ACTION_UP,MotionEvent.ACTION_CANCEL->{
                    binding.recordDesText.text = "按住 说话"
                    recorder.stopRecord()
                    Util.starVibrate(context, longArrayOf(0,50))
                    if(rect.contains(x,y)){
                        val content = recorder.getFilePath()
                        val msg =
                            Message.obtain(conversationId, Message.MESSAGE_RECORD, content, fromType)
                        msg.citeMessageId = citeMessageId
                        pushExecutors {
                            msgDao.insert(msg)
                        }
                    }

                }

            }
            true
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
        binding.citeText.setEmotionText(content)
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