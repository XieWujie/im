package com.vlog.conversation.adapter

import android.app.Dialog
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.common.pushExecutors
import com.common.pushMainThread
import com.common.util.ScreenUtils
import com.common.util.Util
import com.dibus.DiBus
import com.vlog.R
import com.vlog.conversation.writeMessage.FinalReadViewList
import com.vlog.conversation.writeMessage.WordListLayout
import com.vlog.database.CiteEvent
import com.vlog.database.Message
import com.vlog.database.MsgWithUser
import com.vlog.database.User
import com.vlog.databinding.CiteMessageLayoutBinding
import com.vlog.databinding.MsgLongClickLayoutBinding
import com.vlog.photo.load
import dibus.app.MessageSourceCreator
import java.lang.IllegalArgumentException

open class MessageHolder(view:View) :RecyclerView.ViewHolder(view){

    private val msgSource = MessageSourceCreator.get()

    open fun bind(message: MsgWithUser){}

    open fun bindTime(time:String){}

    protected fun loadCite(citeMessageId:Int,viewGroup: ViewGroup){
        if(citeMessageId == -1)return
        val context = itemView.context
        pushExecutors {
            val msgWrap =  try {
                 msgSource.getMsgWithUser(citeMessageId)
            }catch (e :Exception){
                e.printStackTrace()
                return@pushExecutors
            }
            val msg = msgWrap.message
            pushMainThread {
                val binding = CiteMessageLayoutBinding.inflate(LayoutInflater.from(context),null,false)
                binding.apply {
                    usernameText.text = msgWrap.user.username
                    timeText.text = Util.getTime(msg.createAt*1000)
                }
                val view = when(msg.messageType){
                    Message.MESSAGE_TEXT->TextView(context).apply {
                        text = msg.content
                        textSize = 10f
                        setTextColor(Color.BLACK)
                    }
                    Message.MESSAGE_IMAGE->ImageView(context).apply {
                        scaleType = ImageView.ScaleType.CENTER_CROP
                          load(msg.content)
                    }
                    Message.MESSAGE_WRITE->WordListLayout(context).apply {
                        this.handleWrite(msg.content)
                    }
                    else->return@pushMainThread
                }
                binding.citeContentText.removeAllViews()
                binding.citeContentText.addView(view)
                viewGroup.visibility = View.VISIBLE
                viewGroup.removeAllViews()
                viewGroup.addView(binding.root)
            }
        }
    }
}

internal fun View.setLongClick(message: Message,fromUser: User){
    val dp30 = Util.dp2dx(context,30).toInt()
    val dp70 = Util.dp2dx(context,70).toInt()
    val screenHeight = ScreenUtils.getScreenHeight(context)
    val screeWidth = ScreenUtils.getScreenWidth(context)
    setOnLongClickListener { view->
        val location = IntArray(2)
        getLocationInWindow(location)
        val dialog = Dialog(context,R.style.long_click_dialog)
        val binding = MsgLongClickLayoutBinding.inflate(LayoutInflater.from(context),null,false)
        val root = binding.root
        dialog.apply {
            setContentView(root)
            setCancelable(true)
        }
        dialog.window?.apply {
            val p = attributes
            if(location[0]>screeWidth/2){
                p.x = location[0]-dp70
            }else{
                p.x = location[0]
            }
            if(location[1]>screenHeight/2){
                p.y = location[1]-dp70
            }else{
                p.y = location[1]+view.height-dp30
            }

            Log.d("Dialog","x:${location[0]},y:${location[1]}")
            attributes = p
        }
        binding.citeText.setOnClickListener {
            DiBus.postEvent(CiteEvent(fromUser.username,message))
            dialog.dismiss()
        }
        dialog.show()
        dialog.window?.setGravity(Gravity.START or Gravity.TOP)
        true
    }
}