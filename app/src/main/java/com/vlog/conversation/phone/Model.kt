package com.vlog.conversation.phone

import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

/**
 * websocket 信令交互
 * Created by taxiao on 2019/8/14
 * CSDN:http://blog.csdn.net/yin13753884368/article
 * Github:https://github.com/taxiao213
 * 微信公众号:他晓
 */
data class Model(val sessionDescription: SessionDescription,val iceCandidate: IceCandidate)