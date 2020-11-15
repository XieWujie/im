package com.vlog.conversation.writeMessage.event

import com.vlog.connect.MessageOnTime

data class SingleWriteEvent(val list: List<MessageOnTime.P>)