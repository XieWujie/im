package com.vlog.connect.notify

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager.EXTRA_REASON
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.RemoteInput
import com.bumptech.glide.Glide
import com.common.HOST_PORT
import com.common.pushExecutors
import com.vlog.R
import com.vlog.conversation.ConversationActivity
import com.vlog.database.Friend
import com.vlog.database.Message
import com.vlog.database.Room
import com.vlog.database.User
import com.vlog.user.Owner
import com.vlog.verify.list.VerifyListActivity


class Notify(private val context: Context) {

    private val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val channelId = "chat"


    fun sendNotification(room: Room, msg: Message) {
        checkChanel()
        pushExecutors {
            val intent = Intent(context, ConversationActivity::class.java)
            intent.putExtra("room", room)
            sendNotification(room.roomName, msg, room.roomAvatar, intent)
        }

    }

    private fun checkChanel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "聊天信息",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            manager.createNotificationChannel(channel)
        }
    }

    fun sendNotification(user: User, msg: Message) {
        checkChanel()
        pushExecutors {
            val intent = when (msg.messageType) {
                Message.Verify -> Intent(context, VerifyListActivity::class.java)
                else -> Intent(context, ConversationActivity::class.java).apply {
                    putExtra("friend", Friend(user, msg.conversationId, Owner().userId))
                }
            }
            sendNotification(user.username, msg, user.avatar, intent)
        }
    }

    private fun sendNotification(title: String, message: Message, avatar: String, intent: Intent) {
        val myBitmap = Glide.with(context)
            .asBitmap()
            .load("$HOST_PORT$avatar")
            .submit()
            .get()
        val content = when (message.messageType) {
            Message.MESSAGE_IMAGE -> "[图片]"
            Message.Agree -> "${title}已同意添加你为好友"
            Message.Verify -> "${title}请求添加你为好友"
            Message.MESSAGE_WRITE -> "[手写]"
            else -> message.content
        }
        val notify = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(content)
            .setDefaults(NotificationCompat.DEFAULT_LIGHTS)
            .setAutoCancel(true)
            .setDefaults(NotificationCompat.DEFAULT_SOUND)
            .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
            .setWhen(System.currentTimeMillis())
            .setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_CANCEL_CURRENT
                )
            )
            .setSmallIcon(R.drawable.ic_message)
            .setLargeIcon(myBitmap)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH && message.messageType != Message.Verify) {
            val action = getRemoveAction(message)
            notify.addAction(action)
        }
        manager.notify(1, notify.build())
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT_WATCH)
    private fun getRemoveAction(message: Message): NotificationCompat.Action? {
        //添加一个回复组件，
        val remoteInput = RemoteInput.Builder("reply")
            .build()

        //添加一个 pendingIntent 的广播
        val intent = Intent("com.vlog.message.reply")
        intent.putExtra("message", message)
        //  intent.setPackage("android");

        val replyPi = PendingIntent.getBroadcast(
            context, 2,
            intent, PendingIntent.FLAG_UPDATE_CURRENT
        )

        //构建 action
        return NotificationCompat.Action.Builder(
            R.mipmap.ic_launcher,
            "回复", replyPi
        ).addRemoteInput(remoteInput).build()
    }
}