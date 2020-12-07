package com.vlog.conversation

import com.vlog.database.Message
import com.vlog.database.MsgWithUser

data class MessageRemoveEvent(val msg:Message)

data class MessageChangeEvent(val msg: MsgWithUser)

data class MessageInsertEvent(val msg: MsgWithUser)

 class ImageLoadFinish()