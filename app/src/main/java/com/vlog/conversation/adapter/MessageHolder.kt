package com.vlog.conversation.adapter

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.common.ext.toast
import com.common.pushExecutors
import com.common.pushMainThread
import com.common.util.ScreenUtils
import com.common.util.Util
import com.dibus.DiBus
import com.vlog.R
import com.vlog.connect.MessageSend
import com.vlog.conversation.record.CirclePlayBar
import com.vlog.conversation.writeMessage.WordListLayout
import com.vlog.database.CiteEvent
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.database.User
import com.vlog.databinding.CiteMessageLayoutBinding
import com.vlog.databinding.MsgLongClickLayoutBinding
import com.vlog.photo.loadWithMaxSize
import com.vlog.user.Owner
import dibus.app.MessageSourceCreator
import java.util.*

open class MessageHolder(view: View) : RecyclerView.ViewHolder(view) {

    private val msgSource = MessageSourceCreator.get()

    open fun bind(message: MsgWithUser) {}

    open fun bindTime(time: String) {}

    protected val context: Context = view.context


    private val dp100 = Util.dp2dx(view.context, 100).toInt()

    protected fun loadCite(citeMessageId: Int, viewGroup: ViewGroup) {
        viewGroup.removeAllViews()
        viewGroup.visibility = View.GONE
        if (citeMessageId == -1) return
        val context = itemView.context
        pushExecutors {
            val msgWrap = try {
                msgSource.getMsgWithUser(citeMessageId)
            } catch (e: Exception) {
                e.printStackTrace()
                return@pushExecutors
            }
            val msg = msgWrap?.message ?: return@pushExecutors
            pushMainThread {
                val binding =
                    CiteMessageLayoutBinding.inflate(LayoutInflater.from(context), null, false)
                binding.apply {
                    usernameText.text = msgWrap.user.username
                    timeText.text = Util.getTime(msg.createAt * 1000)
                }
                binding.citeContentLayout.removeAllViews()
                viewGroup.visibility = View.VISIBLE
                viewGroup.addView(binding.root)
                when (msg.messageType) {
                    Message.MESSAGE_TEXT -> TextView(context).apply {
                        text = msg.content
                        textSize = 10f
                        setTextColor(Color.BLACK)
                        binding.citeContentLayout.addView(this)
                    }
                    Message.MESSAGE_IMAGE -> ImageView(context).apply {
                        scaleType = ImageView.ScaleType.FIT_XY
                        binding.citeContentLayout.addView(
                            this,
                            dp100.toInt(),
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        loadWithMaxSize(msg.content, dp100)
                    }
                    Message.MESSAGE_WRITE -> WordListLayout(context).apply {
                        this.handleWrite(msg.content)
                        binding.citeContentLayout.addView(
                            this,
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                    }
                    Message.MESSAGE_RECORD -> {
                        val layout = LayoutInflater.from(context)
                            .inflate(R.layout.cite_record_layout, binding.citeContentLayout, false)
                        val timeText = layout.findViewById<TextView>(R.id.time_text)
                        val circleView = layout.findViewById<CirclePlayBar>(R.id.record_play_view)
                        RecordHolder.b(msg.content, timeText, circleView)
                    }
                    else -> return@pushMainThread
                }
            }
        }
    }
}

internal fun View.setLongClick(
    message: Message,
    fromUser: User,
    action: (Dialog, GridLayout) -> Unit = { _, _ -> }
) {
    setOnLongClickListener {
        longClick(this, message, fromUser, action)
        true
    }
}

internal fun addActionView(view: View, gridLayout: GridLayout) {
    val p = GridLayout.LayoutParams()
    val colCount = gridLayout.columnCount
    val childCount = gridLayout.childCount
    p.rowSpec = GridLayout.spec(childCount / colCount)
    p.columnSpec = GridLayout.spec(childCount % colCount)
    p.width = Util.dp2dx(view.context,70).toInt()
    p.height = Util.dp2dx(view.context,30).toInt()
    if (view is TextView) {
        view.setTextColor(Color.WHITE)
        view.gravity = Gravity.CENTER
    }
    gridLayout.addView(view, p)
}


internal fun longClick(
    view: View,
    message: Message,
    fromUser: User,
    action: (Dialog, GridLayout) -> Unit = { _, _ -> }
) {

    val context = view.context
    val dialog = Dialog(context, R.style.long_click_dialog)
    val binding = MsgLongClickLayoutBinding.inflate(LayoutInflater.from(context), null, false)
    val root = binding.root
    val grid = root.findViewById<GridLayout>(R.id.grid)
    action.invoke(dialog, grid)

    val threeMinutes = Date().time - 3 * 60 * 1000
    if (message.sendFrom == Owner().userId && message.sendTime > threeMinutes && message.isSend) {

        val withDrawText = TextView(context).apply {
            text = "撤回"
        }
        withDrawText.setOnClickListener {
            val message = Message.obtain(
                message.conversationId,
                Message.MESSAGE_WITHDRAW,
                message.messageId.toString(),
                message.fromType
            )
            dialog.dismiss()
            DiBus.postEvent(message, MessageSend {
                if (it != null) {
                    context.toast(it.toString())
                }
            })
        }
        addActionView(withDrawText, grid)
    }

    if(message.isSend && message.createAt>1000L &&message.messageType != Message.MESSAGE_RECORD) {
        val citeText = TextView(context).apply {
            text = "引用"
            addActionView(this, grid)
        }
        citeText.setOnClickListener {
            DiBus.postEvent(CiteEvent(fromUser.username, message))
            dialog.dismiss()
        }
    }

    val screenHeight = ScreenUtils.getScreenHeight(context)
    val screeWidth = ScreenUtils.getScreenWidth(context)
    val location = IntArray(2)
    view.getLocationInWindow(location)
    dialog.apply {
        setContentView(root)
        setCancelable(true)
    }
    val dp70 = Util.dp2dx(context,70).toInt()
    val dp30 = Util.dp2dx(context,30).toInt()
    dialog.window?.apply {
        val p = attributes
        if (location[0] > screeWidth / 2) {
            p.x = location[0] - dp70
        } else {
            p.x = location[0]
        }
        if (location[1] > screenHeight / 2) {
            p.y = location[1] - dp70
        } else {
            p.y = location[1] + view.height - dp30
        }

        attributes = p
    }

    dialog.show()
    dialog.window?.setGravity(Gravity.START or Gravity.TOP)
}