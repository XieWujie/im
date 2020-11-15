package com.vlog.conversation.writeMessage

import com.vlog.connect.MessageOnTime


data class MessageWriteWord(val minH:Float,val minV:Float,val maxH:Float,val maxV:Float,val list:List<MessageOnTime.P>)